# Reconferência do Terra / Atmosfera — 18 de setembro de 2026

**Não voltamos à estaca zero. Há correções preservadas, os 34 testes passam e o release abre. O aplicativo ainda não está pronto para publicação: a busca de clima falhou no release e o catálogo oferece variantes sem arquivos locais.**

Esta versão substitui o diagnóstico contraditório anterior. O nome do arquivo foi mantido para preservar links; o histórico de 17/09 e a atualização anterior estão no Git. Base reconferida: **`a61c387`**, com árvore de arquivos rastreados inicialmente limpa. As alterações desta revisão se limitam aos quatro documentos solicitados. Evidências de execução ficam em `android-app/build/` e não são parte do produto.

Referências: [plano](PLANO-LANCAMENTO.md), [operação pós-lançamento](OPERACAO-POS-LANCAMENTO.md), [inventário atualizado](../loja/PRODUTOS-REVISAO.csv).

## 1. O que efetivamente foi concluído ou preservado

| Item | Estado reconferido | Evidência e limite |
|---|---|---|
| Sincronização dos HTML legais | **Concluído** | Privacidade, termos e contato são idênticos por SHA-256 entre `docs/`, `public_html/` e assets. Não significa que o conteúdo jurídico inteiro esteja pronto. |
| Testes JVM | **Concluído nesta execução** | 34 testes, zero falhas/erros. Em 17/09 havia uma falha na sincronização legal. |
| Build debug e release | **Concluído nesta execução** | `testDebugUnitTest lintDebug assembleDebug assembleRelease bundleRelease --continue` terminou com sucesso. |
| Proteção do construtor Room | **Implementada e preservada** | `b409c20`: regra `-keep class * extends androidx.room.RoomDatabase { <init>(); }`. Abertura do release atual não reproduziu o crash de WorkDatabase. |
| Entrada da tela Premium | **Confirmada no emulador** | Loja → “Ver o que muda” abriu o comparador Premium. |
| Bloqueio de estilos na interface | **Implementado, parcial no produto** | `MainViewModel.setEffectStyle()` bloqueia estilos diferentes de pixel sem Premium; chips mostram cadeado. Falta validar/revogar no serviço. |
| Consulta de compras no retorno | **Implementada** | `MainActivity.onResume()` chama `restaurar()`. Não houve compra válida neste teste. |
| Tratamento inicial de callbacks de compra | **Implementado, parcial** | Cancelamento/erro são logados; item já possuído dispara restauração; falha de acknowledge é logada. Ainda não há tratamento completo visível na UI nem repetição persistente. |
| Localização no Worker | **Preservada** | Worker usa coordenadas em cache. O WallpaperService ainda chama `getLocalizacao()`: não afirmar que todo segundo plano já foi corrigido. |
| Catálogo e estilos curados | **Preservados** | 74 cenas, 16 estilos de efeito; os 16 sprites referenciados existem. Estes números já apareciam no estado local de 17/09: o commit preservou trabalho, não criou 74 cenas novas hoje. |
| Marca Terra | **Parcial** | Nome do app/HTML atualizados; a Home ainda mostra “Atmosfera”, confirmado visualmente e em `HomeTab.kt:111`. |

A reversão foi da migração de build, não das correções de produto. A configuração atual continua **AGP 8.13.2, Kotlin 2.4.10, target/compileSdk 36, minSdk 26 e Billing 9.1.0**. Não reabrir a migração como primeiro passo sem uma reprodução e um teste de release.

## 2. Verificação executada agora

### Build e arquivos

- Comando: `gradlew.bat testDebugUnitTest lintDebug assembleDebug assembleRelease bundleRelease --continue --console=plain`, JDK 17.
- Resultado: **BUILD SUCCESSFUL, 42 s**. Houve tarefas reaproveitadas; não foi build limpo e isso não prova eliminação dos avisos históricos do R8.
- Testes: **34 / 34 aprovados**. Lint debug: **20 avisos novos em relação ao baseline, nenhum erro**; lint vital release sem novos erros/avisos.
- AAB: **425.299.168 bytes (405,60 MiB)**. APK release: **424.360.791 bytes (404,70 MiB)**. Tamanho de arquivo local não equivale a download por aparelho no Console.
- SHA-256 do AAB: `C2C86EE2DA96FD479D0A25DB0A1DE22D2EE44C45054BC90723A1A0E51E12A165`.
- `zipalign -P 16 -c 4 app-release.apk`: código **0**. Isso verifica alinhamento ZIP; não comprova sozinho alinhamento ELF nem execução em ambiente de páginas de 16 KB. [Guia Android](https://developer.android.com/guide/practices/page-sizes).
- Assets: **523 arquivos, 423.317.829 bytes (403,71 MiB)**. Sem redução relevante em relação à medição anterior.

### Execução do release

Instalado com `adb install -r`, preservando dados do emulador. Ambiente: `emulator-5554`, **API 34 / páginas de 4096 bytes**. APK identificado como release no metadata, versão **1.0.0 / versionCode 1**, sem flag DEBUGGABLE na instalação.

- Duas aberturas a frio completaram e mantiveram a Activity em foco; não apareceu FATAL EXCEPTION no filtro do processo observado.
- Home → Loja → comparador Premium navegáveis. O resultado não cobre aplicação de todos os wallpapers nem compras.
- **Nas duas aberturas, WeatherRepository registrou:** `java.lang.Class cannot be cast to java.lang.reflect.ParameterizedType`.
- Home permaneceu com **“Carregando clima…”** nas observações, sem mostrar falha nem clima válido.
- Billing retornou `Service connection is disconnected`. Não foi validada transação, e esse erro não prova que os SKUs estejam ausentes no Console.
- Miniaturas “Em breve” foram observadas na Loja; a inspeção de arquivos confirmou variantes ausentes.

Evidências locais: `android-app/build/reconferencia-build.log`, `reconferencia-runtime.log`, `reconferencia-inicio.png`, `reconferencia-loja.xml`, `reconferencia-premium.xml`, `reconferencia-clima.xml`. Os relatórios padrão de testes/lint estão em `android-app/app/build/`.

**Limites:** instalação sobre estado existente, não primeiro uso limpo; sem aparelho físico, compra real, Play Console, publicação externa, bateria medida, teste de todos os cenários ou ambiente de 16 KB. Não apagar dados do emulador para ampliar teste sem necessidade.

## 3. Bloqueios atuais, por prioridade

### B07 / P0 — Clima falha no release apesar do build verde

Este é o achado mais urgente. A mensagem foi reproduzida em dois processos após aberturas a frio. É compatível com problema de tipos genéricos/reflexão no caminho Retrofit/R8, mas **a causa exata não foi isolada**. Não atribuir automaticamente a uma dependência nem concluir que a migração resolve.

A matriz oficial informa R8 **9.1.29** para Kotlin **2.4**; o projeto permanece na combinação anterior e não há evidência suficiente para chamar os avisos de metadata de “cosméticos”. [Compatibilidade Kotlin/R8](https://developer.android.com/build/kotlin-support).

**Próxima entrega:** reproduzir com stack trace útil sem dados pessoais; validar as regras de preservação dos tipos genéricos e o conjunto de versões em alteração isolada; instalar o release resultante e exigir resposta meteorológica válida, cache atualizado, erro visível e funcionamento offline. Um `Worker result SUCCESS` sozinho não prova consulta bem-sucedida: o Worker pode devolver sucesso mesmo quando o repositório retorna falha.

### B01 / P0 — Catálogo local precisa corresponder às artes entregues

A atualização anterior registra decisão de lançar com **conteúdo embarcado, sem servidor de artes**. Esta revisão usa essa decisão documentada como escopo de trabalho. Não confundir “download adiado” com “download concluído”. O aplicativo continua usando internet para clima e compras.

- Há **74 SKUs de cenário + Premium = 75 produtos**; nenhum cadastro no Console foi confirmado.
- `Cenario.kt` declara **335 pares únicos cenário/arte** nos blocos correspondentes aos 74 cenários.
- **78** desses pares têm `fundo.png` no prefixo declarado; **257 não têm**. Existência do fundo não certifica todos os outros arquivos necessários à renderização.
- As **cinco artes gratuitas** têm fundo local. As 74 cenas têm ao menos uma arte com fundo.
- `artesDoCenario()` oferece as variantes da configuração, sem filtrar arquivo local. Também acrescenta pixel a uma lista que já pode conter pixel, criando repetição.
- `Acervo.BASE_PADRAO` continua vazio; download só aparece na ferramenta de debug. Não existe entrega alternativa de produção para as artes ausentes.

**Saída para o MVP embarcado:** definir exatamente quais variantes cada cena vende; incluir todos os arquivos dessa oferta ou retirar da vitrine as variantes ausentes; validar renderização/aplicação/posse e medir o pacote final no Console. O [CSV](../loja/PRODUTOS-REVISAO.csv) lista variantes presentes/ausentes por SKU.

O requisito antigo da SPEC de não embarcar arte paga contradiz o escopo registrado em 18/09. Harmonizar SPEC/checklist antes de fechar a fase. Não obrigar R2/PAD no MVP por inércia do plano antigo, nem afirmar aprovação da Play apenas pelo AAB estar abaixo de 500 MB. [Limites de download](https://support.google.com/googleplay/android-developer/answer/9859372?hl=pt-BR).

### B02 / P1 — Premium implementado apenas em parte

Já há bloqueio no ViewModel e cadeado na UI; **pixel é o único estilo livre no código**. O serviço continua lendo `EstiloEfeito.atual()` sem verificar direito. Perda/restauração de Premium precisa reconciliar também estilo persistido. Tocar no estilo bloqueado apenas retorna sem mudança; falta a prévia/caminho de compra prometidos na SPEC.

**Saída:** regra única ao selecionar e renderizar; downgrade seguro para estilo livre, UI reativa e testes de revogação/restauração. Os 16 estilos possuem suas folhas de sprite, portanto essa parte do conteúdo não foi perdida.

### B03 / P1 — Compras avançaram, mas não estão aprovadas

Fechado no código: consulta em onResume, diagnóstico de falha de confirmação, cancelamento/erro registrados e restauração de item já possuído.

Ainda aberto: chave de licença vazia faz `assinaturaValida()` aceitar sem verificar; posse de cenas permanece em preferências sem fluxo reativo; não há UX completa para erro/pendência nem confirmação com repetição persistente; produtos e preços reais não foram verificados. Não registrar os 75 produtos como “não cadastrados”: **status desconhecido sem acesso ao Console**.

**Saída:** compra → confirmação → liberação da arte local, cancelamento, pagamento pendente, reinstalação, restauração e reembolso validados pela Play. [Integração Billing](https://developer.android.com/google/play/billing/integrate).

### B04 / P1 — Uso comercial do clima não regularizado no repositório

`WeatherRepository` continua no endpoint gratuito `api.open-meteo.com`, sem evidência de contrato/autorização comercial. A API gratuita restringe uso comercial; decidir provedor/plano e preservar a atribuição exigida. Isso é independente do erro técnico de B07. [Termos Open-Meteo](https://open-meteo.com/en/terms).

### B05 / P1 — Sincronização HTML concluída; documentação pública ainda pendente

- Os três conjuntos HTML estão idênticos. Controlador, contato e URL foram preenchidos nos HTML.
- `docs/legal/*.md` ainda contém placeholders de identidade/contato/URL. A fonte dita canônica não acompanha os HTML.
- `public_html/` continua fora do teste de sincronização e das entradas declaradas no Gradle. A conferência manual de hoje não impede regressão futura.
- Datas de vigência/publicação continuam como placeholders visíveis. Não inventar a data futura: definir uma vigência verdadeira para a versão da política disponibilizada aos testers e atualizar o histórico quando necessário.
- A declaração de isenção de encarregado está no texto, mas o enquadramento do titular não foi comprovado nesta auditoria; presença de citação não é validação jurídica.
- Canal do diálogo de suporte difere do suporte Terra da página de contato.
- Persistem afirmações de que o desenvolvedor não consegue processar reembolso; a Play permite essa operação. [Pedidos e reembolsos](https://support.google.com/googleplay/android-developer/answer/2741495?hl=en).
- A tentativa de acessar a URL da política novamente falhou por resolução DNS neste ambiente. Publicação permanece **não comprovada**, não declarada globalmente indisponível.
- Rebrand incompleto: título “Atmosfera” na Home. Revisar capturas e demais textos públicos.

### B06 / P1 — Data Safety e Console sem comprovação nova

Não houve acesso ao Console. Revalidar declarações de localização, Geocoder, logs do provedor, compras, suporte e backup. No MVP embarcado, não declarar um CDN de artes como se estivesse integrado. Se o clima passar por backend próprio, atualizar o fluxo de dados antes de publicar. [Data Safety](https://support.google.com/googleplay/android-developer/answer/10787469).

## 4. Riscos mantidos, sem afirmar reprodução

| Área | Pendência |
|---|---|
| WallpaperService | Deduplicar/instrumentar possíveis posts concorrentes do frame; validar carga/liberação de bitmaps durante transições. |
| Localização | Serviço ainda pode cair na cidade padrão quando posição falha; preservar estado válido e esclarecer fallback. |
| WeatherWorker | Tratar `Result.failure` como falha adequada; revisar uso de coordenadas em cache após revogação da permissão. |
| Hora | API em fuso da localização e relógio do aparelho podem divergir. |
| Backup e logs | Conferir restauração de direitos e logs de coordenadas em release. |
| Qualidade | Medir bateria/memória, acessibilidade, API mínima/atual, múltiplas fabricantes e aplicação real de wallpapers. |
| Acervo futuro | Versão/hash de conteúdo, limites de ZIP, cancelamento, concorrência, JSON/R8 e rollback ainda não prontos; não bloqueiam entrega embarcada se esse código não fizer parte da jornada pública. |

## 5. Ordem prática a partir daqui

1. Corrigir o erro meteorológico **no release** e o estado de erro da Home.
2. Corrigir oferta de variantes locais; usar as listas de ausências no CSV.
3. Completar autorização Premium e compras, sem refazer a navegação que já funciona.
4. Regularizar clima, concluir documentos públicos e alinhar SPEC ao MVP embarcado.
5. Validar instalação limpa, aparelho físico, Play interno/fechado e compatibilidade de 16 KB completa.
6. Só então submeter produção e executar o plano de 90 dias.

**Veredito:** houve progresso verificável, inclusive fechamento da falha de testes. Nenhuma fase ampla de publicação está integralmente aprovada; isso não apaga as subtarefas concluídas. O release abrir é avanço real, mas o erro de clima e a oferta de variantes ausentes impedem chamá-lo de pronto.
