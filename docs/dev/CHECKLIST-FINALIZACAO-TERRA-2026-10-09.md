# Checklist de finalização do Terra — 09/10/2026

**Refinamento editorial da etapa 5 em 15/10:** Privacidade 1.10, Termos 1.6 e Contato 1.3 preparados. Instruções operacionais separadas das páginas públicas; efeitos e estilos atuais identificados conforme o código. Estado de rascunho, aprovação, vigência e aceites preservados. Ver [alterações e pendências para fechamento](REFINAMENTO-EDITORIAL-LEGAL-2026-10-15.md). Gerar as cópias locais não republica o Google Sites; as referências datadas abaixo às versões 1.9/1.5/1.2 registram a entrega anterior.

## Reconferência do commit de Rafael — 15/10/2026

Commit `84f2662` incorporado à cópia local: documentação, páginas legais e preparação do teste fechado; sem mudanças no código funcional. Privacidade 1.9, termos 1.5 e contato 1.2. A PR #50 continua em rascunho, sem revisão registrada. Secret-scan aprovado; o build dessa revisão falhou na conferência do manifesto legal, antes dos checks Android. Manifesto regenerado nesta reconferência: somente três hashes das fontes corrigidos, sem alterar textos ou HTMLs. Aprovação do CI desta correção ainda precisa ser conferida após o envio.

### Próximas pendências, na ordem do teste fechado

- [ ] Conferir Privacidade, Termos e Contato sem login e no celular, comparando o conteúdo público com as fontes 1.9/1.5/1.2. A ferramenta de navegação não abriu os URLs nesta reconferência; isso não prova indisponibilidade do site.
- [ ] Identificar versão/código disponíveis no Console, registrar vigência e aprovação dos textos aplicáveis ao teste; republicar no Google Sites após mudanças. Os documentos continuam rascunhos.
- [ ] Conferir contas de licença e instrumentos de pagamento de teste; inscrição na faixa não comprova compra sem cobrança.
- [ ] Concluir declarações do Console com evidências do candidato: dados, público, classificação, países e contato. A preparação dos documentos não comprova preenchimento.
- [ ] Conferir produtos ativos e entrega correspondente; catálogo atual tem 69 cenas/358 artes/67 cards, cinco amostras e 353 produtos pagos propostos. Configuração comercial e endpoint adequado à distribuição continuam pendentes.
- [ ] Resolver falhas impeditivas conhecidas e identificar o candidato; conferir CI, gerar AAB único e distribuir pela faixa fechada. Nenhum novo AAB foi gerado nesta reconferência.
- [ ] Registrar adesão e início efetivo no Console; realizar avaliação de Premium/artes, wallpapers/efeitos, clima, compras/download/restauração e estabilidade durante o teste, conforme prioridade do Rafael. Não marcar os aceites das etapas 1–4 e 6 apenas pelo início do teste.
- [ ] Antes da produção: consolidar resultados, catálogo/continuidade, oferta, fornecedores/licenças e revisão jurídica pendentes. Melhorias futuras e regra de efeitos futuros do Premium continuam separadas do aceite atual.

## Site publicado em 15/10/2026

Rafael informou a publicação do Google Sites. Links conferidos na navegação, documentados sem seleção de conta:

| Destino | URL |
|---|---|
| Site | https://sites.google.com/view/terra-live-wallpaper |
| Política de Privacidade — campo do Console | https://sites.google.com/view/terra-live-wallpaper/privacidade |
| Termos | https://sites.google.com/view/terra-live-wallpaper/termos |
| Contato | https://sites.google.com/view/terra-live-wallpaper/contato |

Compras do teste: somente contas testadoras de licença, conforme decisão do titular em 15/10. Falta conferir a configuração efetiva das contas e dos instrumentos de pagamento. O site está publicado com metadados de rascunho; publicação não significa aprovação jurídica, vigência do candidato, cadastro no Console ou começo dos 14 dias. Ainda é necessário conferir o acesso sem login em sessão anônima e conciliar versão/vigência/textos do artefato. Alterações nas fontes locais exigem republicação no Google Sites; gerar os HTMLs não modifica o site.


**Prioridade definida em 14/10 por Rafael:** preparar documentação e declarações para iniciar o teste fechado. Compras, wallpapers, efeitos e nomes serão avaliados durante os 14 dias. Domínio próprio não é necessário; Google Sites/Blogger públicos são opções. Ver [preparação legal do teste](PREPARACAO-LEGAL-TESTE-FECHADO-2026-10-14.md) e guia do Console atualizado. URL/plataforma, regime de compras, versão/vigência e aprovação permanecem a confirmar.

**Reconferência em 13/10:** commit `e9a221f` publicado e incorporado à cópia local. Build e secret-scan do GitHub aprovados na [execução da PR #50](https://github.com/gabzieg/atmosfera/actions/runs/38403864423). Reexecução local de 17 testes direcionados (prévias, cache, catálogo e documentos): zero falhas/erros; gerador legal conferido. PR continua em rascunho, sem merge ou novo AAB. Avaliação no aparelho permanece com Rafael.

**Integração da v2 em 13/10:** ajustes funcionais aplicados sobre `8ec0993` na branch `fix/premium-privacidade-etapas-1-2`, para revisão na PR #50. Prévias/cache, navegação, cadeados, controles debug independentes, nomes e agrupamentos integrados. Vitrine: 69 cenas/358 artes/67 cards; manifesto e miniaturas preservam 71 cenas/366 artes. Estacionamento (`estacionamento`) e Feira de Natal (`natal`) ficam temporariamente fora da vitrine; Chalé/Sala de Natal permanecem. Validação desta integração: 98 testes sem falhas, lint sem erros, builds debug/release e conferência dos documentos aprovados; aparelho/Play, produtos, produção, documentos legais e candidato AAB continuam pendentes. Ver [entrega da v2](AVALIACAO-V2-RAFAEL-2026-10-13.md).


**Regra de trabalho:** executar uma etapa por vez, registrar a evidência e só marcar conclusão quando seu critério de aceite estiver atendido. Código preparado, teste local, teste pela Play e publicação são estados distintos.

**Orientação do usuário em 10/10:** avaliações no aplicativo serão feitas pelo Rafael. Podemos avançar na implementação das próximas etapas, mantendo o aceite em aparelho das anteriores explicitamente pendente. Não houve envio de mensagem ao Rafael por esta execução.

**Base da conferência:** `main` em `e7129f6`; questionário do Rafael no commit `c1b95ec`, fora da `main`; decisões confirmadas nesta conversa. Este checklist não comprova o estado atual do Play Console nem substitui testes em aparelho.

**Atualização em 10/10/2026:** etapa 1 implementada na árvore local e verificada com 75 testes unitários, lint e build de debug aprovados. Aceite em aparelho/Play pendente; mudanças ainda não incorporadas à `main` nem publicadas.

**Etapa 2 em 10/10:** correções locais de suporte, backup, logs e remoção da geocodificação sem uso implementadas; **78 testes unitários, lint e build de debug aprovados**. Política técnica de backup atualizada para revisão 1.5, com três HTMLs sincronizados. Conferência em aparelho atribuída ao Rafael; não é validação jurídica integral da política.

**Verificação adicional da etapa 2:** `compileReleaseKotlin` aprovado. Isso confere a compilação do código de release; não representa geração de um novo AAB nem teste de execução em aparelho.

**Roteiro para encaminhamento:** [prompt para Rafael](PROMPT-RAFAEL-AVALIACAO-ETAPAS-1-2-E-CATALOGO-2026-10-10.md), com avaliação das duas etapas e inventário/decisões para a etapa 3. A entrega de código e checklist será compartilhada na branch `fix/premium-privacidade-etapas-1-2`; a avaliação em aparelho e a publicação seguem pendentes. Os rascunhos jurídicos de 05/10 e a consolidação de 04/10 continuam locais nesta entrega; não presumir que estejam disponíveis no GitHub.

## Decisões registradas — concluídas

- [x] Premium `terra_premium`: compra única de R$ 49,90, apenas efeitos e estilos. Não libera artes pagas.
- [x] Compra avulsa registrada pelo titular: R$ 1,99 por arte específica, não por todas as variantes do cenário.
- [x] Canal oficial de suporte e privacidade: `suporteterrabr@gmail.com`.
- [x] Responsável indicado: Rafael Huppes, pessoa física. Validar os papéis jurídicos na etapa 5.
- [x] Público registrado: 13+. Avaliar obrigações e conferir declaração na Play na etapa 5.
- [x] Retenção do atendimento registrada: até 60 dias após resolução, ressalvadas obrigação legal ou defesa em processo. Aplicar aos registros controlados pela equipe.
- [x] **Confirmado pelo usuário em 09/10: ocorreram somente compras de teste, sem cobrança real.** Não foi identificado comprador real a migrar nesta informação; o relato não é uma exportação auditada do Console.
- [x] Provedor de clima: MET Norway. Código passou a consultar somente com wallpaper na home, tela acordada e desbloqueada; worker legado não consulta. Falta aceite em aparelho.

## 1. Corrigir a separação entre Premium e artes — implementação local validada

**Objetivo:** comprar Premium nunca concede o direito a uma arte paga. As cinco amostras continuam utilizáveis.

- [x] Conferir os caminhos de seleção, aplicação e leitura pelo serviço, incluindo `isSceneUnlocked()`, `isArtUnlocked()` e `ArteFundo.atual()`.
- [x] Separar autorização de arte da autorização de efeito. UI e serviço usam `AcessoArte`; Premium não participa da autorização de arte. O antigo indicador de compra por cenário não libera variantes.
- [x] Impedir que uma arte paga previamente selecionada por compra de teste continue aplicada sem direito válido após a atualização: leitura retorna a amostra do mesmo cenário.
- [x] Manter seleção/uso das cinco amostras gratuitas independente do Premium. A regra de autorização é local e não exige rede; aceite offline em aparelho ainda faz parte da conferência abaixo.
- [x] Corrigir descrições em Premium, Loja e tutoriais e a cláusula Premium dos termos (revisão 1.3, três cópias HTML sincronizadas).
- [x] Corrigir o caminho de arte bloqueada: ações ficam indisponíveis, com explicação de que Premium não inclui a arte e a compra individual ainda não existe nesta versão. O acesso ao Premium pelos estilos continua disponível.
- [x] Especificar regressões: seis testes em `AcessoArteTest` cobrem amostras, variantes pagas, preferências antigas, troca entre cenários, arte inexistente e destrave de debug. Seleção/aplicação valida o direito no código; fluxo de compra/restauração e interface serão conferidos em aparelho.
- [x] Executar testes unitários, lint e build de debug apropriados à mudança: **75 testes, zero falhas/erros; lint sem erros (com warnings existentes); assembleDebug aprovado**. Testes/build repetidos depois da atualização dos termos.
- [ ] Registrar resultado em aparelho/versão Play antes do aceite final de publicação.

**Aceite:** UI e wallpaper aplicam o mesmo direito; Premium afeta somente efeitos/estilos; o usuário não é induzido a comprar Premium para receber arte.

**Limite:** esta etapa não conclui a compra individual nem a loja remota. Esses direitos e fluxos serão implementados nas etapas 3–4.

### Conferência manual do Rafael que falta para encerrar o aceite

1. Instalar o APK de debug da árvore atual em um aparelho/emulador. Deixar o destrave de conteúdo do painel de debug **desligado** para testar a autorização normal.
2. Sem Premium, conferir as cinco amostras e alternar entre elas; repetir sem rede.
3. Com Premium de teste restaurado pela Play em uma futura versão interna, conferir efeitos/estilos e verificar que as variantes pagas continuam bloqueadas. Repetir após revogar/restaurar o Premium.
4. Em Bruxa, Lavanda, Esfinge e Jardim, selecionar a variante pixel paga: conferir a prévia, o aviso e as ações indisponíveis; não deve abrir compra de Premium por essa arte.
5. Atualizar uma instalação de teste anterior que tinha uma dessas artes pagas selecionadas: conferir a volta à amostra correta, tanto no app quanto no wallpaper.
6. Conferir que uma arte gratuita selecionada em outro cenário pode ser aplicada corretamente e que a preferência pixel gratuita da Cabana não autoriza pixel pago nos demais.

Não havia aparelho/emulador conectado na execução de 10/10. Artefato para a conferência visual: `android-app/app/build/outputs/apk/debug/app-debug.apk`. Este APK não é o AAB destinado ao Play Console e seu destrave de debug não comprova comportamento de release.

## 2. Corrigir os quatro pontos técnicos de privacidade — implementação local validada

- [x] G05: “Reportar problema” passa a usar `suporteterrabr@gmail.com`, igual ao canal oficial. Conferência no cliente de e-mail fica no roteiro do Rafael.
- [x] G08: excluir `atmosfera_weather_cache.xml` e `atmosfera_met_freio.xml` do backup legado, cloud-backup e device-transfer. Manter exclusão de `atmosfera_plano.xml` e preferências comuns elegíveis. Três testes verificam as regras e sua ligação ao manifesto; não apaga automaticamente backups antigos.
- [x] G09: remover o log com latitude/longitude; falhas de localização usam mensagem genérica. Detalhes do estado meteorológico e das exceções ficam restritos a debug; release registra apenas diagnóstico genérico/tipo da falha, além dos códigos de bloqueio do provedor.
- [x] G16: remover `nomeDoLugar()`, imports e tabela de estados sem uso. Não foi introduzido outro serviço de localização/geocodificação. São Paulo/SP permanece o local padrão; a cidade real não passa a ser resolvida por este ajuste. Nomes legados no cache podem aparecer enquanto ainda aplicáveis.
- [x] Conferir estaticamente o fluxo atual: sem permissão, `LocationHelper` retorna São Paulo/SP; `ClimaNaHome` limita consultas; o worker legado não consulta. O teste dinâmico de revogação e transições de tela segue pendente.
- [x] Executar os checks afetados: **78 testes unitários, zero falhas/erros; lint sem erros e build de debug aprovados**. Alterações nas regras de backup agora invalidam o resultado dos testes no Gradle.
- [x] Atualizar a seção de backup da política e manter os HTMLs de `docs/`, `public_html/` e assets idênticos. Demais refinamentos jurídicos/editoriais continuam na etapa 5.
- [ ] Rafael: validar destinatário, restauração/backup, diagnóstico de release, indicação do local e localização negada/revogada em aparelho, conforme roteiro abaixo.

**Aceite:** canal, backup, diagnósticos e localização correspondem ao comportamento descrito nos textos refinados.

### Roteiro de avaliação do Rafael — etapas 1 e 2

Registrar versão/commit e resultado por item. O APK de debug em `android-app/app/build/outputs/apk/debug/app-debug.apk` foi atualizado com as duas etapas; a avaliação de release/Play exige artefato posterior correspondente.

1. Executar a conferência da etapa 1 acima, com o destrave de debug desligado.
2. Abrir “Algo errado neste cenário?” e preparar um relato: o cliente de e-mail deve mostrar o canal oficial; preparar o relato não deve enviá-lo automaticamente.
3. Testar instalação sem permissão de localização e voltar à home com o wallpaper ativo: o local padrão deve ser São Paulo/SP. Conceder e depois revogar a permissão; conferir consulta e indicação de local sem atribuir São Paulo à localização real.
4. Conferir retomada da home, tela apagada, bloqueio e app por cima: chamadas de clima devem ocorrer somente na condição elegível. Usar a versão de release correspondente para conferir ausência de latitude/longitude e detalhes de requisição nos logs de produção.
5. Testar backup/restauração ou transferência em versões representativas do Android: preferências comuns podem voltar; coordenadas/clima, freio da MET e flag Premium não devem voltar pelas novas regras. Restaurar a compra pela Play e deixar a nova consulta acontecer na home; testar também restauração sem rede.

Conferir separadamente backups produzidos por versões antigas: a nova configuração não comprova que essas cópias tenham sido apagadas. Referência técnica das regras: [documentação de backup do Android](https://developer.android.com/identity/data/autobackup).

## 3. Congelar o catálogo e os produtos do lançamento

**Avanço em 10/10:** [inventário por arte](../loja/catalogo-2026-10-10/README.md) gerado a partir da fonte local, com CSV, JSON, hashes e propostas de produto. Nenhuma consulta/cadastro no Console, publicação no bucket ou avaliação visual por esta execução.

- [x] Escopo reconfirmado pelo usuário em 10/10: **todo o acervo aprovado** como objetivo do lançamento. Não substituir por um lote menor sem nova decisão; testar por lotes não altera a oferta pretendida.
- [x] Conferir a fonte local após os cortes: **71 cenas/75 artes com fundo**, cinco gratuitas e 70 pagas candidatas. O motor declara 312 variantes após exclusões, **237 sem fundo local**; há declaração duplicada de `vale/cartoon`. CSV/JSON preparados, sem aprovação de renderização.
- [ ] Conciliar a fonte local com o inventário remoto de **71 cenas/366 artes** mencionado pelo Rafael. Caminho/manifesto e composição ainda não comprovados nesta execução.
- [x] Separar amostras, artes pagas e exclusões na tabela. Os sete temas excluídos não recebem proposta de produto; `timessquare`, `ogro` e `eiffel` ainda existem no acervo local e permanecem identificados como excluídos, sem apagar os arquivos.
- [ ] Conferir cortes no manifesto de produção e artefato efetivamente distribuído. Inventário local não é manifesto de publicação.
- [x] Preparar tabela por arte: identificação, origem, arquivos, hashes, dimensões, bytes, miniaturas, preço regular e ID de produto **proposto**. Console e teste Android marcados como não verificados; miniaturas dedicadas ausentes identificadas.
- [x] Novas artes/variantes: **cada arte nova será uma compra separada**, confirmado pelo usuário em 10/10.
- [ ] Definir com Rafael se **futuros efeitos/estilos** ficam incluídos no Premium. Não prometer inclusão automática enquanto essa regra estiver aberta.
- [x] **Sem packs comerciais no lançamento**, confirmado pelo usuário em 10/10. Pacotes de arquivo para download são outro conceito.
- [ ] Configurar preço regular e ofertas reais de R$ 0,99, sem confundir preço promocional com uma nova posse.
- [ ] Conferir países de oferta/distribuição e preços correspondentes.
- [ ] Preservar registros de origem e licenças das artes, fontes, sprites e dados meteorológicos, com revisão dos itens efetivamente distribuídos.

**Aceite:** existe uma lista única do que o usuário pode experimentar, comprar e receber. O inventário não contém produto sem entrega correspondente.

## 4. Implementar compra por arte e entrega remota

**Adaptacao em 11/10:** 366 variantes mescladas ao catalogo, 366 miniaturas WebP verificadas, caminhos remotos e hashes completos integrados. R8 9.1.29 versionado; CI passa a compilar release minificada. Debug aponta ao bucket de testes; release exige URL explicita. Seis ZIPs representativos conferidos. Falta revisao/aceite do Rafael, produtos na Play e configuracao de producao. Ver [roteiro atualizado](ADAPTACAO-R2-R8-2026-10-11.md). Nenhum AAB novo.


**Dependência para aceite:** catálogo/produtos da etapa 3 aprovados. Por solicitação do usuário, implementação adiantada em 10/10 com arquivos disponíveis; isso não conclui a etapa 3. Ver [integração e roteiro](INTEGRACAO-ARTES-ACERVO-2026-10-10.md).

- [x] Modelar posse por cenário + arte e associar os produtos pagos correspondentes; não usar um direito por cenário para liberar todas as variantes.
- [ ] Integrar carregamento de produtos/preços, compra, estados pendentes, confirmação, restauração e revogação.
- [x] Implementar catálogo e miniaturas sem embutir todo o acervo HD (75 artes locais; aprovação/completude pendentes).
- [ ] Configurar endpoint de produção do bucket e conferir controles de upload/acesso. Não incluir credenciais no aplicativo.
- [ ] Verificar origem/integridade de manifesto e pacotes, versionamento e compatibilidade com o motor.
- [ ] Implementar download solicitado pelo usuário: tamanho, progresso, cancelamento, repetição, instalação segura e falhas compreensíveis.
- [ ] Preservar o wallpaper ativo quando houver falta de rede/espaço, interrupção ou pacote inválido.
- [ ] Implementar limpeza de arquivos e comprovar uso offline do conteúdo instalado.
- [ ] Testar compra → download → aplicação → reinício → reinstalação/restauração → novo download.
- [ ] Validar visualmente cada arte anunciada no Android; ampliar catálogo por lotes conferidos.

**Aceite:** cada arte paga é entregue e utilizável; Premium e posse da arte permanecem independentes em todos os estados.

## 5. Refinar, revisar e sincronizar documentos

**Execucao editorial de 11/10:** fontes canonicas atualizadas: privacidade 1.7, termos 1.4, contato 1.1. Nove HTMLs gerados, hashes/estado registrados, checks de fidelidade e importador de Willian conferidos. Revisao continua rascunho; nao houve publicacao nem aprovacao juridica. Ver [entrega e pendencias](ETAPA-5-DOCUMENTOS-E-LANDING-2026-10-11.md).

- [x] Reconciliar SPEC, TASKS, plano de lancamento e pos-lancamento com a oferta atual; preservar planos anteriores como historico.
- [x] Refinar fontes canonicas para clima na home, compras por arte, R2, canal oficial e atendimento de 60 dias; preservar rascunhos de 05/10 como registros historicos.
- [x] Consolidar fontes, versoes editoriais, modelo e manifesto de integridade; sincronizar docs, public_html e assets.
- [ ] Definir vigencia real e versao de distribuicao aplicavel, inclusive teste/compras anteriores; registrar aprovacao efetiva.
- [ ] Validar papeis, bases legais, transferencias, publico 13+/ECA Digital, licencas e continuidade do conteudo adquirido. Resposta do titular nao equivale a parecer juridico.
- [ ] Definir dominio e hospedagem com Willian; conferir cookies, registros, formularios e tratamento real da landing.
- [x] Preparar geracao/exportacao explicita e testar compatibilidade com o importador, sem alterar o front-end de Willian.
- [x] Conferir texto, metadados, links relativos e historico; ampliar o check para as tres copias e a fonte canonica. Atualizar site nao altera apps instalados.
- [ ] Importar a revisao aprovada, publicar e verificar as tres paginas HTTPS sem login e com metadados finais.
- [ ] Decidir o tratamento de documentos pessoais na branch do Rafael e no PR #48, sem presumir apagamento do historico. Os registros locais foram versionados como historicos por solicitacao do usuario, com dados internos desnecessarios omitidos.

**Aceite:** comportamento, oferta, documentos e paginas publicas coerentes para a mesma revisao aprovada, sem confundir preparacao editorial com validacao juridica ou publicacao.

## 6. Preparar e validar uma única versão candidata

**Inicio em 11/10:** CI da revisao `6dffa5a` aprovada (build e varredura de segredos). Usuario ainda nao sabe o maior versionCode enviado e nao recebeu o resultado da avaliacao de Rafael. A versao do projeto segue codigo 6/1.0.5; nenhum novo AAB foi gerado para envio. Validacao em aparelho permanece com Rafael. Ver [encaminhamento](ENCAMINHAMENTO-HUPPES-2026-10-11.md).

- [ ] Resolver defeitos conhecidos das jornadas ofertadas; não confundir CI aprovada com aceite do produto.
- [ ] Validar instalação limpa online/offline, onboarding e atualização de uma versão de teste anterior.
- [ ] Testar clima na home, tela bloqueada/apagada, app por cima, retomada e instalação sem cache.
- [ ] Testar efeitos, brilho, rolagem, trocas de arte e recriação do wallpaper.
- [ ] Medir memória, bateria, estabilidade e compatibilidade em aparelhos representativos; verificar requisitos de páginas de memória de 16 KB aplicáveis.
- [ ] Conferir certificado de upload, Play App Signing, acesso autorizado às chaves e armazenamento seguro, sem ler/publicar senhas no relatório.
- [ ] Conferir `versionCode` disponível no Console antes do build destinado ao envio.
- [ ] Gerar AAB da revisão aprovada com configuração versionada; arquivar commit, hash, certificado, mapping e notas de versão.
- [ ] Testar o artefato distribuído pela Play, incluindo compra/restauração e relatórios de pré-lançamento.

**Aceite:** há um único candidato identificado e testado, sem defeito conhecido que impeça usar o gratuito ou receber o conteúdo comprado.

## 7. Concluir configuração e teste fechado no Play Console

- [ ] Conferir titularidade, verificações e perfil de pagamentos efetivamente exigidos pela conta.
- [ ] Configurar produtos e preços da oferta final; conferir chave pública de compras no build sem expor configuração privada.
- [ ] Concluir Segurança dos dados, classificação IARC, público-alvo, anúncios, acesso e demais declarações pertinentes.
- [ ] Atualizar ficha, contato, ícone, gráfico e screenshots do app real; não anunciar artes ou recursos indisponíveis.
- [ ] Definir roteiro e registrar coordenação do teste fechado pelo responsável já indicado.
- [ ] Cumprir o requisito aplicável de pelo menos 12 participantes inscritos continuamente por 14 dias; teste interno não substitui essa faixa.
- [ ] Registrar feedback, correções e uso real; solicitar acesso à produção quando elegível.

**Aceite:** Console sem pendências impeditivas, teste exigido comprovado e acesso à produção aprovado.

Referência: [requisitos oficiais de teste](https://support.google.com/googleplay/android-developer/answer/14151465?hl=en). O prazo não garante aprovação automática.

## 8. Publicar e verificar a primeira distribuição

- [ ] Enviar a versão aprovada e acompanhar revisão/respostas do Google.
- [ ] Conferir países, oferta e primeira disponibilidade pública.
- [ ] Instalar pela listagem pública e verificar versão, links legais, preço, amostras, compra, entrega e aplicação.
- [ ] Registrar a data real de lançamento e os canais de atendimento/operação.

**Aceite:** aplicativo aprovado e distribuído com oferta e conteúdo conferidos; envio para revisão não será marcado como publicação concluída.

## 9. Operar depois da publicação

- [ ] D0–D2: conferir instalação pública, falhas, compras, suporte, clima e downloads.
- [ ] D3–D7: priorizar falhas de pagamento/entrega, crashes, ANRs e consumo excessivo; validar correções antes de distribuir updates.
- [ ] Semanalmente: revisar custos do bucket, disponibilidade, pedidos/reembolsos e feedback; ampliar artes apenas com teste e oferta configurada.
- [ ] Aplicar retenção de 60 dias ao atendimento encerrado, com preservação justificada das exceções e controle de acesso.
- [ ] Manter histórico do acervo e arquivos necessários à restauração de compras; definir recuperação de falha e continuidade.
- [ ] Revisar documentos e declarações quando houver nova finalidade, fornecedor ou funcionalidade.

**Aceite:** responsáveis executam a rotina e mantêm evidências. A frequência de novas artes não será tratada como promessa automática ao consumidor.

## Acompanhamento

| Etapa | Estado atualizado em 15/10 | Evidência |
|---|---|---|
| Decisões registradas | Concluído o registro acima | Conversa e questionário do titular; não valida configuração do Console. |
| 1 — Premium × artes | Implementação local e checks concluídos; aceite em aparelho pendente | `AcessoArte` compartilhado, seleção/aplicação protegidas, oferta corrigida, 75 testes aprovados, lint e build debug aprovados em 10/10. Não publicado. |
| 2 — Privacidade técnica | Implementação local e checks concluídos; avaliação do Rafael pendente | Canal oficial, exclusões de backup/transferência, logs minimizados, Geocoder sem uso removido, política sincronizada e 78 testes aprovados em 10/10. |
| 3 — Catálogo/produtos | V2 integrada; catálogo final e aceite pendentes | Vitrine com 69 cenas/358 artes/67 cards, cinco amostras e 353 SKUs pagos propostos. Manifesto preserva 71 cenas/366 artes; `estacionamento` e `natal` temporariamente indisponíveis. Faltam aceite Android, configuração comercial, continuidade do conteúdo e regra de futuros efeitos. |
| 4 — Compra/entrega remota | R2 e ajustes v2 integrados; aceite pendente | Prévias intermediárias, cache temporário, navegação e cadeados integrados em `e9a221f`; controles debug de Premium e artes independentes. Nove artes nativas preservadas; R8 9.1.29 versionado. Faltam avaliação do Rafael, produtos reais, teste pela Play e produção. |
| 5 — Documentos/site | Refinamento editorial preparado; aceite pendente | Privacidade 1.10, termos 1.6 e contato 1.3; instruções internas separadas. Google Sites registrado com revisão anterior; falta republicação/conferência, decisões materiais, aprovação e vigência/versão. |
| 6 — Candidato validado | V2 verificada; CI da revisão legal e aceite pendentes | `e9a221f` teve CI aprovada; 98 testes registrados por Rafael e 17 direcionados reconferidos. `84f2662` falhou no manifesto legal; correção local aprovada em --check, aguardando CI. Aparelho/Play e versionCode ainda não conferidos; nenhum novo AAB. |
| 7 — Console/teste fechado | Pendente de evidência atual | Sem acesso ao painel nesta rodada. |
| 8 — Publicação | Pendente | Nenhuma publicação realizada nesta rodada. |
| 9 — Operação | Planejada | Começa após a publicação real. |
