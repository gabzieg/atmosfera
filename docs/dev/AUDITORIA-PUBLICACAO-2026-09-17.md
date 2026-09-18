# Auditoria do Terra / Atmosfera — 17 de setembro de 2026

**Conclusão: o produto está implementado em boa parte, mas ainda não está pronto para publicação comercial.** A lacuna principal é fechar a entrega completa: descobrir uma arte → comprar → baixar → aplicar → restaurar após reinstalação. Clima comercial, consistência legal e validação em aparelhos também são bloqueios.

Esta análise considera o estado local, inclusive alterações ainda não commitadas. O nome visível é **Terra**; o pacote continua `com.atmosfera.wallpaper`. Não é necessário renomear o pacote para mudar a marca. Não houve acesso ao Play Console, à conta de hospedagem ou a compras reais. Pendências externas abaixo significam **não comprovadas nesta auditoria**, não necessariamente inexistentes.

Plano executável: [PLANO-LANCAMENTO.md](PLANO-LANCAMENTO.md). Operação depois da publicação: [OPERACAO-POS-LANCAMENTO.md](OPERACAO-POS-LANCAMENTO.md).

---

## Atualização — 18 de setembro de 2026

Um dia de trabalho sobre esta auditoria. **Nenhum bloqueio foi integralmente
fechado**; três avançaram de forma relevante e um teve achado novo que muda o
critério de aceite do projeto inteiro. O quadro abaixo distingue o que mudou no
código do que apenas foi decidido.

| Bloqueio | Situação | O que de fato mudou |
|---|---|---|
| B01 — entrega sob demanda | **Adiado por decisão**, não resolvido | Decidido lançar local-only, sem servidor. O AAB de 405,6 MB cabe no teto de 500 MB do módulo base, então é viável — mas o critério de saída original (catálogo remoto, comprar→baixar→aplicar) continua não atendido. `Acervo.BASE_PADRAO` segue vazio. |
| B02 — Premium × implementação | **Parcial** | `MainViewModel.setEffectStyle()` passou a exigir Premium (só `pixel` é livre) e a UI ganhou cadeado. **Continua aberto:** `AtmosferaWallpaperService.kt:107` lê `EstiloEfeito.atual()` **sem checar posse** — quem perder o Premium segue com o estilo pago renderizando. A auditoria pedia verificação "ao aplicar **e ao carregar no serviço**". Prévia sem uso permanente e tabela única de direitos também seguem abertas. |
| B03 — compras | **Parcial** | `acknowledgePurchase()` passou a logar falha; cancelamento, item já possuído e erro passaram a ser tratados; `MainActivity.onResume()` reconsulta compras. **Continua aberto:** `LICENSE_PUBLIC_KEY_BASE64` vazia (depende do Play Console), estado de posse ainda é booleano em preferências sem observação reativa, sem superfície de erro na UI, e os 75 SKUs não foram cadastrados. |
| B04 — clima comercial | **Sem mudança** | Não endereçado. |
| B05 — páginas legais | **Parcial, mas com o item mais crítico fechado** | `PaginasLegaisSincronizadasTest` **passa** — era o teste que falhava nesta auditoria (item da seção 5). As três cópias voltaram a ser byte a byte idênticas, `public_html/` foi criado e sincronizado, e os placeholders de controlador, contato e URL foram preenchidos. Encarregado (DPO) resolvido pela isenção da **Resolução CD/ANPD nº 2/2022, art. 11**, citando o artigo — não por nome suposto, como a auditoria exigia. **Continua aberto:** `ReportarProblemaDialog.kt:63` ainda aponta para `rafael.huppes@gmail.com` em vez do suporte Terra; "Vigente desde" e data de publicação seguem `[PREENCHER]` (só existem no dia real); URL pública não validada; o texto que nega reembolso pelo desenvolvedor não foi corrigido. |
| B06 — Data Safety | **Sem mudança** | Depende de B01 e B04. |
| B07 — release e qualidade | **Avançado, com achado novo grave** | Ver abaixo. |

### B07 — o que foi apurado

A auditoria pedia "conferir a matriz oficial, alinhar versões e repetir o
release". Feito, com resultado conclusivo:

- **Causa raiz dos avisos do R8 identificada**: a matriz oficial
  (developer.android.com/build/kotlin-support) exige **R8 9.1.29+ para Kotlin
  2.4.x**; o AGP 8.13.2 empacota **R8 8.13.19**, que casa com Kotlin 2.3.x. O
  descompasso é real, não ruído.
- **Duas correções testadas**: baixar para Kotlin 2.3.20 (gate + `bundleRelease`
  verdes, aviso zerado) e subir para AGP 9.4.0 (idem). Optou-se pela segunda.
- **A migração para AGP 9.4 foi REVERTIDA**: passou em **100% do gate** —
  incluindo `bundleRelease` assinado, tamanho idêntico e `zipalign -P 16` — e
  **o app não abria**. Detalhes em [DECISAO-AGP-9-MIGRACAO.md](DECISAO-AGP-9-MIGRACAO.md).
- **O aviso de metadata do R8 continua existindo** e foi aceito como cosmético:
  o build atual funciona em aparelho, e a tentativa de eliminá-lo quebrou o app.
- **16 KB confirmado**: `zipalign -P 16 -c -v 4` → `Verification successful`.

### O achado que muda o critério de aceite do projeto

A auditoria já observava que "a CI atual valida debug, não o comportamento do
release minificado" (B07) e que os modelos do `Acervo` precisam de "validação com
R8 ativo" (seção 3). Hoje isso deixou de ser hipótese:

> O build de release com AGP 9.4 passou em todo o gate e **crashava na abertura**:
> `RuntimeException: Failed to create an instance of class androidx.work.impl.WorkDatabase`
> (`MainActivity.onCreate` → `WeatherWorker.schedule` → WorkManager → Room por reflexão).

Causa: a regra que o `room-runtime` 2.5.0 traz sozinho é
`-keep class * extends androidx.room.RoomDatabase` — **sem `{ <init>(); }`**.
Preserva a classe, não o construtor que o Room chama por reflexão. O R8 8.13.x
preservava por conta própria; o R8 9.x não. Corrigido explicitamente em
`proguard-rules.pro` — hoje é no-op (APK byte a byte do mesmo tamanho), amanhã
é obrigatório.

**Consequência para o processo:** existe uma faixa inteira de defeitos — tudo
que depende de reflexão (Room, WorkManager, Retrofit/Gson, Billing) — que
**nenhuma verificação automática deste projeto alcança**, porque os testes são
JVM puro, não há `androidTest/` e o R8 só roda no release. Gate verde valida a
compilação, não o produto.

**Novo critério de aceite, adotado hoje:** instalar o **build de release** em
aparelho e exercitar os caminhos de reflexão antes de considerar qualquer
mudança de build concluída.

### Primeira validação em aparelho do build de release

A auditoria registrava "sem validação em aparelho físico" (seção 5). O estado
atual foi verificado com o release instalado em emulador — não apenas compilado:

| Caminho | Resultado |
|---|---|
| App abre | ✅ processo vivo |
| Sobrevive à navegação até a Loja | ✅ |
| WorkManager + Room + Retrofit/Gson | ✅ `Worker result SUCCESS` |
| `BillingManager` inicializa | ✅ falha apenas ambiental (sem conta Google no AVD) |
| `FATAL EXCEPTION` | **0** |

Continua valendo: sem aparelho físico, sem transação de compra real, sem
publicação externa.

### Outras correções do dia

- **Rebrand Atmosfera → Terra** concluído em strings, páginas legais e ficha.
- **Localização em segundo plano** passou a reusar a coordenada em cache em vez
  de buscar posição nova — alinhando o código ao que a política já prometia
  (relacionado ao risco de `WeatherWorker` na seção 3, que **não** foi fechado:
  o tratamento de `Result` de erro continua como a auditoria descreve).
- **Cidade padrão corrigida** de "Novo Hamburgo - PR" para "RS" (2 ocorrências).
  O risco de fundo apontado na seção 3 — cair para cidade fixa quando não há
  localização — **permanece**.
- **Curadoria de estilos**: ~30 → 16, e os 4 cenários cortados por IP saíram do
  `Catalogo` (ainda eram consultados no Play apesar de não aparecerem na Loja).
- **Entry point da `PremiumScreen` restaurado** — a tela estava órfã desde um
  merge anterior.

**A decisão de lançamento da auditoria continua válida:** não submeter à
produção com B01–B07 em aberto. Nada do que foi feito hoje altera esse veredito;
o que mudou é que três bloqueios encolheram e o critério de verificação ficou
mais rigoroso.

## 1. O que já atende à proposta

| Área | Evidência no projeto | Avaliação |
|---|---|---|
| Wallpaper animado nativo | `engine/EffectEngine.kt`, `service/AtmosferaWallpaperService.kt` | Motor Canvas, variação por horário/clima e parada dos callbacks quando invisível implementados; desempenho ainda exige medição em aparelho. |
| Aplicativo de configuração | `ui/`: onboarding, início, loja, detalhe, Premium, ajustes, tutoriais | Jornadas principais existem; não é necessário recomeçar a interface. |
| Meteorologia | `weather/`: Open-Meteo, cache, localização aproximada, WorkManager | Integração real e fallback implementados; licença comercial e comportamento em segundo plano precisam ser resolvidos. |
| Monetização | `billing/BillingManager.kt` | Compra única, consulta de preços, restauração e reconciliação de reembolso existem; há lacunas de confirmação e de atualização da UI. |
| Conteúdo | `engine/Catalogo.kt` | **74 cenários / 74 SKUs de cenário**, além de `atmosfera_premium`; 5 cenários têm uma arte gratuita. A documentação que fala em 76 está desatualizada. |
| Download | `engine/Acervo.kt` | Manifesto, cache, progresso, extração e verificação de hash implementados; uso de download encontrado apenas na tela de debug. |
| Qualidade | `app/src/test`, `.github/workflows/build.yml` | Testes JVM, lint, build debug e varredura de segredos configurados. Não há suíte instrumentada versionada. |
| Loja e textos | `docs/loja`, `docs/legal`, HTML web e assets | Há seis screenshots, ícone, feature graphic e textos; a existência dos arquivos não comprova que representem a versão atual. |

Não faltam login, rede social, anúncios, assinatura ou aplicativo iOS para cumprir a proposta atual. Acrescentá-los agora ampliaria o escopo sem resolver o lançamento.

## 2. Bloqueios confirmados no código e nos arquivos

### B01 — Entrega sob demanda ainda não integrada

- `Acervo.BASE_PADRAO` está vazio. Uma instalação nova de produção não tem servidor configurado.
- `SceneThumbnail` lê `context.assets`; `cenarioTemAsset` procura `fundo.png` embarcado. Remover as artes pagas hoje faria a loja ocultar cenários e perder suas miniaturas.
- `MainViewModel.aplicar()` grava a seleção diretamente; não aguarda `Acervo.baixarArte()`.
- `BillingManager.processar()` chama `Cena.definir()` ao comprar cenário; isso precisa passar pela disponibilidade da arte antes de aplicá-la.
- O diretório `assets/atmosfera` contém **523 arquivos e 403,71 MiB** nesta árvore. É tamanho em disco, não download final da Play. Inclui artes pagas, contrariando o critério de entrega da SPEC.
- A ferramenta `tools/pacote_cenas.py` mencionada na documentação não foi encontrada nesta árvore. É preciso trazê-la para um fluxo reproduzível ou registrar claramente o repositório/artefato externo responsável.

**Saída:** instalação limpa mostra catálogo remoto; comprar baixa e valida apenas o necessário; só então aplica; conteúdo baixado funciona offline; artes pagas completas não ficam no módulo base. Manter as cinco amostras gratuitas coerentes com a oferta atual. R2 é o caminho indicado pelo código recente; PAD aparece em documentação histórica e não está integrado.

### B02 — Oferta Premium não corresponde integralmente à implementação

`SPEC.md` promete efeitos vivos e todos os estilos de efeito no Premium. `MainViewModel.setEffectStyle()` grava qualquer estilo sem verificar Premium. A curadoria de estilos livres e o papel dos packs continuam indefinidos.

**Saída:** tabela única de direitos para cenário, arte e estilo; verificação ao aplicar e ao carregar no serviço; prévia permitida sem conceder uso permanente; restauração e revogação testadas. Packs podem ficar para uma versão posterior: não são necessários ao MVP de cenas avulsas + Premium. Preços comerciais de referência constam na SPEC, mas a UI deve continuar mostrando os valores recebidos da Play.

### B03 — Compras ainda não têm evidência suficiente para produção

- `LICENSE_PUBLIC_KEY_BASE64` está vazia; `assinaturaValida()` devolve `true` nesse caso. É ausência explícita de verificação, não apenas configuração estética.
- O callback de `acknowledgePurchase()` ignora o resultado. Não há tratamento persistente de falha; uma nova abertura pode tentar outra vez, mas isso não garante confirmação no prazo.
- Falha ao abrir pagamento e estados diferentes de sucesso são apenas logados/ignorados; a UI não recebe um estado completo de compra pendente, cancelada ou com erro.
- A posse de cenário é um booleano em preferências. O ViewModel observa a mudança de cena, não a mudança de posse. Comprar o cenário já ativo pode deixar o botão/estado da tela desatualizado até recompor/reabrir.
- Não foi encontrada consulta de compras no retorno da Activity ao primeiro plano; revisar compras pendentes concluídas fora do app.
- Cadastrar só Premium e Tanque não basta para expor os **74** cenários atuais. Publicar apenas um subconjunto exige limitar explicitamente a oferta.

**Saída:** confirmação com tratamento de erro/repetição, estado observável de direitos, compra/restauração/reembolso testados pela Play e lista de produtos coerente. Verificação no servidor é uma evolução recomendada de segurança; a chave pública local não torna o cliente inviolável. A Google documenta confirmação em até três dias após a compra passar a `PURCHASED`. [Integração oficial](https://developer.android.com/google/play/billing/integrate).

### B04 — Serviço de clima precisa de autorização comercial

`WeatherRepository` usa `https://api.open-meteo.com/`, sem chave. Os termos da API gratuita restringem o serviço a uso não comercial; um aplicativo com conteúdo vendido precisa regularizar essa utilização antes do lançamento monetizado. Escolher plano comercial, outro provedor compatível ou obter autorização documentada; conferir atribuição da fonte. Se houver chave privada, não tratá-la como segredo seguro dentro do APK. Um proxy altera custos e o fluxo de dados e precisa aparecer na política. [Termos da Open-Meteo](https://open-meteo.com/en/terms).

### B05 — Páginas legais divergentes e publicação não comprovada

- O teste `PaginasLegaisSincronizadasTest` falha: `docs/` e `assets/legal/` divergem. As diferenças incluem marca, controlador, datas e cidade padrão; não são apenas quebras de linha.
- `public_html/privacidade` e `public_html/termos` coincidem com `docs/`, mas `public_html/contato` diverge. `public_html` não participa do teste atual nem das entradas de teste declaradas no Gradle.
- Há placeholders visíveis de controlador, contato, URL e vigência na política web. A identidade legal deve corresponder ao titular responsável e à ficha; não inferir isso do nome do autor de um arquivo.
- `ReportarProblemaDialog.Suporte.EMAIL` aponta para um endereço diferente do suporte Terra informado em `public_html/contato`. Unificar o destino do atendimento e testar o fluxo sem cliente de e-mail.
- A URL citada, `https://terra-livewallpaper.pages.dev/privacidade/`, não pôde ser validada: falha de resolução DNS neste ambiente. Isso não é prova de indisponibilidade global, mas impede considerá-la publicada.
- Os textos dizem que o desenvolvedor não consegue processar reembolsos. O Play Console oferece essa operação; corrigir o fluxo de suporte. [Gerenciamento oficial de pedidos](https://support.google.com/googleplay/android-developer/answer/2741495?hl=en).

**Saída:** mesma versão aprovada em Markdown, web e app; nenhum placeholder visível; URL HTTPS pública acessível sem login; titular, contato, vigência e processo de atendimento consistentes. Confirmar a aplicabilidade das obrigações legais ao titular, sem preencher identidade ou nomear encarregado por suposição.

### B06 — Data Safety precisa refletir a arquitetura final

O guia atual não pode ser copiado sem revisão. Retenção em cache local não é, isoladamente, o motivo para declarar processamento remoto não efêmero; a Open-Meteo informa logs que podem conter coordenadas por até 90 dias. Também não basta desinstalar para apagar logs externos. [Política do provedor](https://open-meteo.com/en/terms).

Revisar localização aproximada, Geocoder do sistema, compras/SDKs, backup Android, suporte por e-mail e o CDN que entregar artes. O Geocoder pode depender de serviço remoto: não prometer que só existe processamento local por ser uma API Android. Classificar compartilhamento conforme o papel real de cada fornecedor, incluindo exceções para prestadores de serviço. Permissões, sozinhas, não descrevem todo o tratamento. [Orientação oficial de Data Safety](https://support.google.com/googleplay/android-developer/answer/10787469).

### B07 — Release e qualidade real ainda precisam de aprovação técnica

O build usa `targetSdk 36`, `minSdk 26`, Billing `9.1.0` e configuração de assinatura. O target atende ao requisito de novos apps de agosto de 2026; Billing 9 consta na tabela de suporte até agosto de 2028. Isso não substitui testes de compatibilidade. [API alvo](https://support.google.com/googleplay/android-developer/answer/11926878?hl=en), [Billing](https://developer.android.com/google/play/billing/deprecation-faq).

O empacotamento inclui `libandroidx.graphics.path.so`: mesmo com código próprio Kotlin, testar as bibliotecas nativas para páginas de **16 KB**, tanto alinhamento quanto execução. [Guia Android](https://developer.android.com/guide/practices/page-sizes).

A CI atual valida debug, não o comportamento do release minificado. A execução desta auditoria também emitiu avisos do R8 ao interpretar metadados Kotlin, pedindo revisão da combinação Kotlin/AGP/R8. Conferir a [matriz oficial de compatibilidade](https://developer.android.com/studio/build/kotlin-d8-r8-versions), alinhar versões suportadas e repetir o release. Medir download por dispositivo com bundletool/Console, conferir assinatura e testar o AAB distribuído pela Play. Não reutilizar números históricos de 16 MB, 140 MB ou 1,58 GB como se fossem a medição atual.

A página específica de limites consultada informa **500 MB para o módulo base**, calculados sobre download comprimido, e aviso ao usuário em rede móvel acima de 200 MB. Portanto, 403,71 MiB de assets em disco não prova, por si só, rejeição por tamanho. A entrega remota continua necessária pelo requisito do produto e pelo peso da instalação. [Limites de tamanho](https://support.google.com/googleplay/android-developer/answer/9859372?hl=pt-BR).

## 3. Riscos funcionais que precisam de reprodução/teste antes de liberar

| Prioridade | Evidência | Risco / ação |
|---|---|---|
| Alta | `AtmosferaWallpaperService`: `onCreate` e `onVisibilityChanged` podem postar o mesmo `frame`; não há deduplicação ao iniciar | Possíveis loops simultâneos e gasto de bateria. Instrumentar contagem, garantir um único loop e testar alternância rápida entre home, prévia e tela apagada. |
| Alta | O serviço carrega assets em IO e desenha na main; mutex protege cargas entre si | Verificar corrida de recarga/liberação com desenho e destruição. Testar trocas rápidas, rotação/superfície e baixa memória. |
| Alta | `LocationHelper` retorna cidade fixa quando não obtém localização | O wallpaper pode sair do local válido em cache para o padrão quando a localização não estiver disponível. Definir preservação da última posição e estado claro de dado antigo, sem pedir permissão de fundo apenas para contornar o problema. |
| Alta | `WeatherWorker`: `fetchWeather` devolve `Result`; somente `onSuccess` é tratado | Erro HTTP pode terminar em `Result.success()` do Worker e não fazer retry. O Worker usa coordenadas em cache sem consultar permissão; revisar a política de continuar consultas após revogação. |
| Média | `WeatherRepository.blocoAtual` compara horário da API com relógio local do aparelho | Fuso manual diferente da localização pode selecionar bloco incorreto. Testar e usar referência temporal coerente. |
| Alta para CDN | `Acervo`: verifica existência para decidir que pack já está pronto | Não há atualização por hash/versão do pack instalado; cache inválido pode substituir manifesto válido. Validar antes de persistir e definir atualização/rollback de conteúdo. |
| Alta para CDN | `Acervo`: ZIP inteiro em memória, hash opcional e temporário por destino | Limitar tamanho de download/extração, exigir integridade, validar caminhos/estrutura, serializar downloads iguais e propagar cancelamento para HTTP. O código já barra formas comuns de Zip Slip; isso não cobre todos os limites de robustez. |
| Alta para release | Modelos de manifesto em `Acervo` usam Gson sem `SerializedName`; regras próprias do ProGuard preservam modelos de clima, não explicitamente os do acervo | Validar desserialização com R8 ativo e proteger o contrato JSON; testes JVM debug não cobrem ofuscação. |
| Média | `allowBackup=true`, compras em preferências e artes em `filesDir` | Definir regras de backup: evitar cópia de acervo descartável e confiar na reconciliação com Play ao restaurar direitos. |
| Média | `LocationHelper` loga latitude/longitude sem guarda `BuildConfig.DEBUG` | Remover/condicionar logs sensíveis em release e verificar regras finais do R8; o interceptor HTTP já é condicionado. |
| Média | Conteúdo e UI cresceram; faltam testes instrumentados | TalkBack, fonte ampliada, contraste, navegação, telas grandes e aparelhos de pouca memória precisam de revisão. |

Estes riscos resultam de leitura de código; não foram todos reproduzidos em aparelho. Não devem ser apresentados como crashes já observados.

## 4. Documentação que pode induzir decisões erradas

`README.md` ainda descreve catálogo de duas cenas. `ROADMAP.md` conserva instruções de PAD e status antigos. `SPEC.md` mistura cinco amostras com o critério antigo de embarcar somente cabana. `ENTREGA-DE-ARTE.md` cita números e ferramenta de outra árvore. Os responsáveis e a marca também variam entre arquivos.

A referência de execução desta auditoria é o novo plano. Ao implementar cada fase, atualizar SPEC, checklist e guia correspondentes; não marcar etapas antigas como concluídas apenas porque existe classe com aquele nome.

## 5. Verificação executada

- Leitura de arquitetura, implementação principal, documentação, manifesto, dependências, CI e testes; inventário de assets e SKUs.
- Comparação dos três conjuntos de HTML e busca de placeholders.
- Geração de [inventário de 75 produtos para revisão](../loja/PRODUTOS-REVISAO.csv), extraído do catálogo: 74 cenários + Premium. Não é arquivo de importação oficial e não cadastra produtos no Console.
- Consulta a fontes oficiais de publicação e fornecedores em 17/09/2026.
- Execução de `testDebugUnitTest lintDebug assembleDebug bundleRelease` com JDK 17. **34 testes executados; 33 passaram e 1 falhou**, por divergência das páginas legais; o comando encerrou com falha.
- Execução separada de `lintDebug assembleDebug bundleRelease --continue` para avaliar empacotamento sem confundir a falha dos testes com erro de compilação. **APK debug gerado; lint debug sem erros, com 20 avisos**, além dos suprimidos pelo baseline. `lintVitalRelease` não apontou erros/avisos novos. Os avisos de metadados emitidos pelo R8 estão registrados em B07.
- O segundo comando terminou com **BUILD SUCCESSFUL**. AAB release gerado: **425.304.354 bytes (405,60 MiB)**; APK debug: **446.026.092 bytes (425,36 MiB)**. São tamanhos dos arquivos locais, não estimativa de download por dispositivo.
- `jarsigner -verify` retornou `jar verified` e código 0, com avisos de certificado autoassinado/cadeia não confiável, ausência de timestamp e diferenças de leitura JarFile/JarInputStream. Isso comprova verificação pelo utilitário, não aceitação pelo Console; validar o bundle na ferramenta Android e no canal interno antes de distribuir. O AAB está em `android-app/app/build/outputs/bundle/release/app-release.aab` e o mapping em `android-app/app/build/outputs/mapping/release/`.
- Sem validação em aparelho físico, sem transação de compra, sem publicação externa. Acessos e dados pessoais não foram presumidos; as alterações locais anteriores foram preservadas.

**Decisão de lançamento:** não submeter para produção até resolver B01–B07 e registrar evidências da matriz do plano. É possível preparar conta, produtos e testers durante as correções, evitando deixar o prazo de teste para o fim.
