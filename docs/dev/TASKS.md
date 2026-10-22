# Terra — Tasks (fase atual)

**Prioridade em 22/10:** candidato **7 / 1.0.5** gerado em 20/10 (`c1848a9`) e mesclado à `main` pelo PR #53; identidade, hashes e pendências no [diagnóstico](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#atualização-em-22102026--candidato-7-identificado). Teste fechado ainda não iniciado. Conferido no Console em 22/10: o rascunho do fechado **não tem pacote** (causa dos dois erros), países já só Brasil, licenças salvas (7 contas), certificado de upload igual ao do AAB 7, declaração de **ID de publicidade pendente** (bloqueia envio), política com `?authuser=1` e 0 participantes. Sequência em [A1–A8 do checklist](CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md#a--antes-de-iniciar-o-teste-fechado). Os registros de 19/10 abaixo sobre "aguardar versionCode/candidato" estão superados.

**Prioridade em 19/10:** PR #52 incorporada e registros operacionais revisados. [Diagnóstico do teste fechado](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md) e auditor local de AAB preparados; pacote local de 06/10 reprovado por documentos desatualizados. Aguardar maior versionCode, composição do rascunho/certificado e configuração do Console para fechar candidato. Segurança dos dados e análise de Age Signals preparadas, sem SDK novo ou declaração final. Sete contas de licença informadas; checkout/adesões/contagem pendentes.

**Conciliação atual em 15/10:** `0d36b4a` integrado; documentos 1.12/1.8/1.5 preparados. Adultos/Brasil no teste e futuros efeitos/estilos incluídos no Premium confirmados; artes separadas. Registros anteriores de regra de efeitos futuros pendente foram superados. Fornecedores, bases, governança, continuidade, candidato e aprovação continuam pendentes, conforme [D01–D10](../../.lgpd/gaps.md).

## Respostas de Rafael em 15/10 — atualização do escopo

Teste fechado exclusivamente no Brasil, somente com adultos e contas de licença. Gabriel fará a revisão/aprovação final dos textos, ainda pendente. O candidato é a próxima versão após os ajustes; número/versionCode e data inicial dependem de conferência no Console. Quantidade de contas e configuração efetiva de licença não confirmadas. Premium inclui futuros efeitos e estilos, preservadas artes/variantes separadas. Intenção de não retirar conteúdo, com plano de exceção ainda em estudo. Logs, retenção e acesso dos fornecedores seguem sem definição/verificação.

Este registro supera as pendências de decisão sobre país, participantes menores, responsável pela revisão e inclusão de efeitos/estilos futuros nos registros anteriores. Não confirma configuração do Console, aprovação jurídica ou início da contagem. Fontes locais 1.11/1.7/1.3 precisam de revisão e republicação no Sites; geração de HTML não altera páginas públicas.

Detalhe: [respostas e roteiro para iniciar o teste](RESPOSTAS-RAFAEL-TESTE-FECHADO-2026-10-15.md).


**Refinamento editorial em 15/10:** etapa 5 com Privacidade 1.10, Termos 1.6 e Contato 1.3; instruções internas e decisões no [registro de refinamento](REFINAMENTO-EDITORIAL-LEGAL-2026-10-15.md). Textos continuam rascunhos, sem aprovação/vigência ou republicação no Google Sites. Referências anteriores a 1.9/1.5/1.2 são históricas.

## Site publicado em 15/10/2026

Rafael informou a publicação do Google Sites. Links conferidos na navegação, documentados sem seleção de conta:

| Destino | URL |
|---|---|
| Site | https://sites.google.com/view/terra-live-wallpaper |
| Política de Privacidade — campo do Console | https://sites.google.com/view/terra-live-wallpaper/privacidade |
| Termos | https://sites.google.com/view/terra-live-wallpaper/termos |
| Contato | https://sites.google.com/view/terra-live-wallpaper/contato |

Compras do teste: somente contas testadoras de licença, conforme decisão do titular em 15/10. Falta conferir a configuração efetiva das contas e dos instrumentos de pagamento. O site está publicado com metadados de rascunho; publicação não significa aprovação jurídica, vigência do candidato, cadastro no Console ou começo dos 14 dias. Ainda é necessário conferir o acesso sem login em sessão anônima e conciliar versão/vigência/textos do artefato. Alterações nas fontes locais exigem republicação no Google Sites; gerar os HTMLs não modifica o site.


> Estado do trabalho em andamento, literalmente agora. Diferente do
> `ROADMAP.md` (fases do produto até publicar), isto é o dia a dia — o que
> está pela metade, o que decidir, o que testar. Atualizar sempre que algo
> muda de status, não deixar ficar mentiroso.
>
> **Histórico de trabalho concluído não mora aqui** — mora no `git log`, que
> não desatualiza. Este arquivo ficou dois meses parado (2026-08-10 → 2026-10-09)
> justamente por acumular item feito e referenciar uma branch que já tinha sido
> mesclada.

**Última atualização:** 2026-10-15. V2 publicada na PR #50 @ `e9a221f` e incorporada à cópia local. Build e secret-scan do GitHub aprovados; reconferência local de 17 testes direcionados sem falhas/erros e sincronização legal conferida. PR permanece em rascunho; avaliação em aparelho/Play, produtos, produção, documentos aprovados e versionCode continuam pendentes. Nenhum novo AAB.

**Prioridade definida em 14/10 por Rafael:** preparar documentação e declarações para iniciar o teste fechado. Compras, wallpapers, efeitos e nomes serão avaliados durante os 14 dias. Domínio próprio não é necessário; Google Sites/Blogger públicos são opções. Ver [preparação legal do teste](PREPARACAO-LEGAL-TESTE-FECHADO-2026-10-14.md) e guia do Console atualizado. Google Sites e URLs registrados em 15/10; compras somente com contas de licença confirmadas pelo titular. Configuração efetiva das contas, versão/vigência e aprovação dos textos permanecem pendentes.

**Integração da v2 em 13/10:** ajustes funcionais aplicados sobre `8ec0993` na branch `fix/premium-privacidade-etapas-1-2`, para revisão na PR #50. Prévias/cache, navegação, cadeados, controles debug independentes, nomes e agrupamentos integrados. Vitrine: 69 cenas/358 artes/67 cards; manifesto e miniaturas preservam 71 cenas/366 artes. Estacionamento (`estacionamento`) e Feira de Natal (`natal`) ficam temporariamente fora da vitrine; Chalé/Sala de Natal permanecem. Validação desta integração: 98 testes sem falhas, lint sem erros, builds debug/release e conferência dos documentos aprovados; aparelho/Play, produtos, produção, documentos legais e candidato AAB continuam pendentes. Ver [entrega da v2](AVALIACAO-V2-RAFAEL-2026-10-13.md).

Os registros datados abaixo sao historicos; a prioridade acima e o checklist representam o acompanhamento atual.

**Execução local em 2026-10-10:** etapa 1 do [checklist de finalização](CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md) implementada: Premium não libera artes; UI e serviço compartilham a regra de acesso; oferta e termos corrigidos. **75 testes unitários, lint e build debug aprovados.** Falta aceite em aparelho/Play; mudanças locais ainda não incorporadas à `main`. Usuário confirmou que houve somente compras de teste, sem cobrança real. O estado da `main` registrado abaixo não representa automaticamente essa árvore modificada.

**Etapa 2 local em 2026-10-10:** G05/G08/G09/G16 implementados: canal oficial no relato, cache de localização/freio da MET excluídos de backup/transferência, logs minimizados em release e função de Geocoder sem uso removida. **78 testes, lint e build debug aprovados.** Seção de backup da política e três HTMLs atualizados. Por orientação do usuário, avaliações no app ficam com **Rafael**, com roteiro no checklist; seguem pendentes e não impedem avançar nas próximas implementações.

**Etapa 3 em 2026-10-10:** [inventário por arte](../loja/catalogo-2026-10-10/README.md) preparado, sem mudar o catálogo do aplicativo ou o bucket. Após cortes: 71 cenas/75 artes com fundo; 237 variantes declaradas sem fundo local; 70 propostas de produtos pagos. Usuário reconfirmou objetivo de todo o acervo aprovado, nova arte como compra separada e ausência de packs no lançamento. Falta conciliar o inventário de 366 artes do Rafael, aprovar renderização, definir futuros efeitos/estilos no Premium e conferir ofertas/países/Console.

**Etapa 4 local em 2026-10-10:** compra por arte, reconciliacao de direitos, catalogo com miniaturas e download solicitado integrados. 71 cenas/75 artes com arquivos, cinco amostras e 70 SKUs propostos. ZIPs locais em `dist/acervo-preparado/`, sem upload; miniaturas JPEG somam cerca de 2 MB. Falta URL publica do bucket, produtos ativos e aceite do Rafael; nao e versao pronta para publicar. Roteiro em [INTEGRACAO-ARTES-ACERVO-2026-10-10.md](INTEGRACAO-ARTES-ACERVO-2026-10-10.md). 84 testes aprovados, lint sem erros e builds debug/release aprovados; R8 emite avisos de metadados Kotlin, a avaliar no candidato. Politica: delta de backup em revisao 1.6, demais refinamentos legais na etapa 5.

**Adaptacao R2/R8 em 11/10:** catalogo/miniaturas de 366 artes integrados, variantes remotas mescladas as cenas nativas e validacao de caminhos com hash adaptada. R8 9.1.29 integrado ao build e release minificada incluida na CI. Sem novo AAB, upload ou cadastro de produtos. Conferencia HTTP de seis ZIPs representativos aprovada; aceite de renderizacao, compras e producao com Rafael. Ver [adaptacao e roteiro](ADAPTACAO-R2-R8-2026-10-11.md).

**Etapa 5 em 11/10:** textos canonicos refinados (privacidade 1.7, termos 1.4, contato 1.1), identificacao/canal unificados e atendimento de 60 dias incorporado. Geracao explicita sincroniza nove HTMLs e manifesto; checks locais e importador de Willian aprovados sem alterar seu front-end. Mantidos como rascunho: faltam aprovacao juridica, dominios/hospedagem, vigencia/versao aplicavel e processos de continuidade/transferencias/publico. Historico preservado; rascunhos pessoais nao publicados. Ver [entrega da etapa 5](ETAPA-5-DOCUMENTOS-E-LANDING-2026-10-11.md).

**Encaminhamento em 11/10:** usuario solicitou commit/push de todo o material pendente para Huppes. Registros locais antigos agora marcados como historicos; canal antigo e caminhos pessoais desnecessarios omitidos. Checklist e roteiro de passagem atualizados. Etapa 6 ainda depende do maior versionCode no Console (usuario nao sabe) e da avaliacao do Rafael (resultado ainda nao recebido). Nenhum novo AAB gerado. Ver [encaminhamento ao Huppes](ENCAMINHAMENTO-HUPPES-2026-10-11.md).

## Estado (confirmado em 2026-10-09)

**Atualização do acervo em 11/10:** Rafael confirmou bucket existente, acessível na URL de testes. Conciliação do manifesto público confirma 71 cenas/366 artes após cortes: todas as 75 locais, mais 291; 54 variantes ausentes das declarações locais do motor. A PR #50 precisa adaptar caminhos com hash, manifesto WebP, catálogo e miniaturas antes de usar a URL. Não houve upload, novo build nem ajuste de R8 nesta conferência. O init script mencionado está apenas na máquina do Rafael. Evidências em [conciliação R2](../loja/conciliacao-2026-10-11/README.md). Esta atualização substitui a indicação anterior de bucket inexistente; endpoint de produção segue pendente.

- **App**: versão **1.0.5, versionCode 6** na `main`. AAB assinado gerado em 06/10 (SHA-256 `19b88b3d…e353e6bb`). **Não está confirmado** que foi enviado ao Console nem que o código 6 está livre — só o Gabriel vê o Console.
- **Gate**: `testDebugUnitTest lintDebug assembleDebug` verde, **69 testes** unitários (14 classes), CI verde na `main`.
- **Teste em aparelho do clima só na home (PR #46)**: **ainda não feito.** O emulador não sobe a partir do agente (virtualização negada no Windows); o Gabriel precisa ligá-lo, ou testar pelo teste interno em um celular. Roteiro em [CLIMA-SOMENTE-HOME.md](CLIMA-SOMENTE-HOME.md).
- **Rafael**: sem commits novos desde o #46 (05/10 22:25).
- **PRs abertos**: #43 (Rafael, **rascunho**: documentação do R2), #48 (**rascunho**: estrutura `.lgpd/` e índice `docs/README.md`), #45 e #47 (Dependabot — **#47 espera teste em celular**; mexe em bibliotecas do app).

## Decisões pendentes do Gabriel

Detalhe e opções em `.lgpd/gaps.md` e `.lgpd/STATUS.md` — **ainda no PR #48 (rascunho), não na `main`**.

1. **Escopo fechado em 10/10:** objetivo de todo o acervo aprovado, reconfirmado pelo usuário. Sem packs comerciais no lançamento; cada arte nova é compra separada. Falta conciliar/validar arquivos e implementar a Loja remota; o catálogo de produção não foi aprovado pelo inventário local.
2. **Premium nas 5 cenas — decisão fechada e correção local implementada em 10/10.** Apenas efeitos/estilos. Termos §5.1 e interface atualizados; aceite em aparelho/Play ainda pendente. Usuário informou somente compras de teste.
3. **Público 13+ ou 18+** (ECA Digital).
4. **Encarregado** — designar o Rafael ou manter a dispensa de ATPP.
5. **Retenção do atendimento** — 60 dias após resolver?
6. **Qual texto legal vale** — o `.md` canônico (com `[PREENCHER]`) e os HTML divergem; e o que fazer com `docs/legal/revisao-2026-10-05/` (fora do Git).
7. **Correções G05/G08/G09/G16 — implementadas localmente em 10/10.** Ver evidência acima; avaliação em aparelho pelo Rafael ainda pendente.
8. **Advogado** — quem revisa (G03, G11, G12, G20) e quando.

## Em andamento / a fazer

- [ ] **Teste em aparelho do #46**: clima atualizando ao voltar à home; instalação nova sem cache (a tela Início mostra "Carregando clima…" até o wallpaper rodar?); nome da cidade (a função que o resolvia não é mais chamada); nenhuma consulta com app aberto, tela apagada ou bloqueada.
- [ ] **Subir o AAB 1.0.5 no teste interno** e conferir o código 6.
- [ ] **Play Console**: Data Safety, IARC, público-alvo, ficha da loja, contato do app (o perfil público ainda usa e-mail pessoal), chave de licença `playLicensePublicKey`, produtos (hoje só `terra_premium`).
- [ ] **Teste fechado**: 12 testadores por 14 dias seguidos; **o coordenador ainda não foi definido** (pergunta 22 do Rafael).
- [ ] **Medir o motor em aparelho antigo** — decide o `minSdk 26`.
- [ ] **Compra de verdade no teste**: precisa de conta Google logada; o AVD não tem.
- [ ] **Ficha da loja** ([loja/FICHA.md](../loja/FICHA.md)): ainda descreve cenários que não estão no app; revisar quando o escopo (decisão 1) fechar.
- [ ] **Registro de autoria/licença das artes e sprites** (pergunta 19 do Rafael). Os estilos `pixel_mario` e `pixel_zelda` já saíram do código; `Estilo.kt` tem 16 estilos.
- [ ] **Conformidade**: lacunas G01–G21 em `.lgpd/gaps.md`.

## Esperando outras pessoas

- **Willian** — domínio, hospedagem e publicação das 3 páginas legais (a política precisa de URL pública para o Console).
- **Rafael** — marcar o #43 como pronto; registro de licenças; revisão jurídica; operação do R2 (só se o catálogo remoto entrar).
- **Segurança da chave de upload** (ainda aberto; o agente não lê o arquivo): a senha continua em texto em `C:\Users\gbrus\Chaves\SENHA-LEIA-E-APAGUE.txt` — mover para um gerenciador de senhas e apagar o `.txt`; e **backup do `.jks` em dois lugares**. Não enviar a chave ao Rafael: o AAB pode ser gerado por aqui ou por um fluxo na CI.

## Decisões tomadas (não reabrir sem motivo novo)

- **Clima**: MET Norway (2026-10-03, a API gratuita da Open-Meteo é só não comercial); consulta **só com o wallpaper visível na home, tela acordada e desbloqueada** (2026-10-05, PR #46); freio persistente em 429/403.
- **Monetização**: Premium **R$ 49,90**, compra única, só efeitos e estilos; compra avulsa de **uma arte específica por R$ 1,99**, segundo o questionário do titular de 08/10. Sem packs comerciais no lançamento, confirmado em 10/10. Separação de Premium implementada localmente em 10/10; compra por arte integrada localmente; aceite pela Play pendente. Conferir o estado efetivo da versão distribuída.
- **Acervo remoto (R2)**: objetivo de lançamento desde 29/09, a pedido do Rafael — **sujeito à decisão pendente 1**.
- **Público 13+** (2026-10-04) — sujeito à decisão pendente 3.
- **Prévia ao vivo fora da Home** (2026-08-10): fica no onboarding, no detalhe da Loja e no comparador do Premium.
- **Descartado: desenhar a cena em bitmap reduzido e ampliar** (`Canvas(Bitmap)` é software; piorou de 25 ms para 38 ms).
- **Cenário sem asset some da Loja por verificação real** (`cenarioTemAsset` em `ui/components/SceneThumbnail.kt`), não por lista fixa.
- **Toda mudança na `main` entra por PR**, 0 aprovações, CI `build` obrigatória, merge commit (ver `CLAUDE.md`).
- **Sem anúncios** no lançamento; **CodeQL descartado** (repositório privado em plano free).
- **AGP 9 não** (testado e revertido em 09/2026: passou na CI e o app não abria) — ver [DECISAO-AGP-9-MIGRACAO.md](DECISAO-AGP-9-MIGRACAO.md). O Dependabot está configurado para não propor os majors que quebram.

## Quem toca o quê

Fonte completa em [SPEC.md](SPEC.md) e `.github/CODEOWNERS`.

| Setor | Dono | Estado |
|---|---|---|
| `ui/`, `weather/`, `service/`, `billing/`, `debug/` | Gabriel | Ativo |
| `engine/` + `assets/atmosfera/` | Gabriel e Rafael | O Rafael voltou a corrigir o motor em 23/09; combinar quem mexe em qual arquivo |
| Clima só na home (`ClimaNaHome`) | Rafael (autor do #46) | Mesclado sem revisão de outra pessoa; falta o teste em aparelho |
| Documentos legais e conteúdo do Data Safety | Gabriel | Rascunhos Privacidade 1.14 / Termos 1.8 / Contato 1.6; faltam aprovação/vigência, republicação e validação jurídica |
| Páginas legais (Google Sites) | Rafael publicou em 15/10; Gabriel publica as revisões aprovadas (22/10) | Público mostra 1.9 / 1.5 / 1.2 em 22/10; Gabriel precisa de acesso de edição ao site |
| Site de apresentação (`terra.app`) | Willian | Retomou em outubro; sem domínio definido. As páginas legais não dependem dele |
| Conta Play Console | Rafael (titular e controlador; conta pessoal) | A conta Google do Gabriel também acessa (conferido em 22/10). Só `terra_premium` cadastrado (conferido em 22/10) |
| Cloudflare R2 | Rafael (titular) | Bucket de teste; sem URL de produção |

## Backlog (não puxar sem avisar)

- Loja remota no app: listar o manifesto, comprar, baixar com progresso e aplicar; preencher `BASE_PADRAO`; manifesto assinado. **Só depois da decisão pendente 1.**
- Cobertura de teste de comportamento na UI (hoje só testes JVM; zero instrumentado).
- Revisão cruzada entre `TERMOS.md` e `PRIVACIDADE.md`: foram escritos por autores diferentes, em momentos diferentes (o `.lgpd/` já cobre a Política; falta cruzar com os Termos §5).
- Módulos Gradle `:engine`/`:app`.
