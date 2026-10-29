# Checklist de finalização do Terra — acompanhamento em 19/10/2026

Este é o checklist canônico de finalização, criado em 09/10. O [retorno de 19/10](RETORNO-GABRIEL-PLAY-CONSOLE-2026-10-19.md) preserva o relato do Console; [TASKS](TASKS.md) resume a próxima ação. Não há candidato, início de teste ou aprovação final comprovados. Código preparado, CI, aceite em aparelho e publicação são estados distintos.

**Preparação de 22/10/2026:** há candidato 7 / 1.0.5 identificado no parecer estático Windows ([identidade e hashes](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#atualização-em-22102026--candidato-7-identificado)), ligado documentalmente ao commit `c1848a9`; sua disponibilidade interna foi relatada. Isso supera o estado de ausência de candidato do quadro de 19/10 e os registros de preparação em A2 (“sem AAB gerado”, “ainda sem novo AAB” e “AAB final continuam pendentes”), preservados abaixo como histórico: o AAB 7 já foi gerado no Windows e identificado no parecer de 20/10. Não gerar nem reassinar outro AAB para cumprir esses registros superados. Faixa fechada, conciliação oficial, acesso pago do revisor e aprovações continuam pendentes. O detalhamento da seção A abaixo prepara operações futuras; não autoriza executá-las nem declara gates concluídos.

**Atualização de 28/10/2026:** [composição e validação do fechado](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#composição-do-teste-fechado--28102026): A1 concluído; A2 avançou, ainda aberto pelas evidências abaixo. Código 7 salvo com 0 erros e 1 aviso, sem envio, aprovação ou início do fechado. A associação antecipada foi autorizada por Gabriel antes de A3–A6, superando só essa ordem de 22/10; enviar continua condicionado a A3–A7 e nova autorização. A faixa fechada já contém a lista de 7 contas, mas o painel mostra 0 participantes; faltam no mínimo 5 contas além das 7 para B1 (12 adesões contínuas por 14 dias), todas com licença conforme A6. A3–A8, B e C permanecem abertos. O quadro de 19/10 abaixo é histórico.

## Estado confirmado e relatos pendentes

| Área | Estado em 19/10 | Fonte/limite da evidência |
|---|---|---|
| Integração | PR #50 em rascunho, branch `fix/premium-privacidade-etapas-1-2`, commit `fbc365f0fc8604d2d39175432cfcb6bd60108222`; `origin/main` em `e7129f6` | [PR #50](https://github.com/gabzieg/atmosfera/pull/50); integração não mesclada à main na conferência. |
| CI | Jobs build e secret-scan aprovados no commit acima | [Execução 39356755311](https://github.com/gabzieg/atmosfera/actions/runs/39356755311): testes JVM, sincronização legal, lint, builds debug/release e APK debug; não comprova AAB assinado ou aceite pela Play. |
| Fonte Android | `com.terra.wallpaper`, `versionName 1.0.5`, `versionCode 7` preparado localmente em 20/10, minSdk 26, targetSdk 36 | [build.gradle](../../android-app/app/build.gradle); valores do fonte, sem confirmar pacote enviado, maior código usado ou código disponível no Console. |
| Código informado na sessão | Gabriel informou em 19/10 que “6 (1.0.5)”, maior código já enviado e pacote incluído no rascunho fechado, foi enviado em 06/10/2026 | Data relatada anterior às integrações documentadas de 10–13/10; não comprova conteúdo do AAB nem candidato atual. Processamento, composição, caminho de atualização e origem/assinatura continuam pendentes; a data não diagnostica os dois erros. Novo AAB atualizado exige código maior que 6; a conciliação documental de 19/10 não escolheu nem alterou a versão. Preparação posterior registrada em A2. |
| Versão fechada | Dois erros de atualização/composição relatados; causa não identificada | [Bloqueio e roteiro](RETORNO-GABRIEL-PLAY-CONSOLE-2026-10-19.md#bloqueio-da-versão-de-teste-fechado--prioridade-técnica). O código 7 prepara o candidato atualizado autorizado em 20/10, após Gabriel reconfirmar 6 como último código; essa preparação é independente do diagnóstico A1 e não comprova correção dos dois erros. Gerar ou enviar um AAB como correção desses erros depende da causa demonstrada em A1. |
| Console | Rafael relatou 9/11 tarefas, todos os países selecionados e sete contas em lista de licença | Relato não comprova conclusão das duas tarefas restantes, correção para Brasil, salvamento das licenças ou checkout de teste. |
| Participação | Nenhum início confirmado dos 14 dias; último painel relatado tinha zero participantes | Sete contas de licença não equivalem a participantes inscritos na faixa fechada. |
| Privacidade e documentos | Privacidade 1.14, Termos 1.8 e Contato 1.6 preparados; fontes continuam rascunhos | [Evidências E01–E10](../../.lgpd/evidencias-privacidade-etapa2.md) e [lacunas](../../.lgpd/gaps.md); bases/governança, configurações, aprovação/vigência e correspondência app/site/Console pendentes. |
| Site público | GET sem cookies/credenciais em 19/10 retornou HTTP 200 para as três páginas: Privacidade 1.9, Termos 1.5 e Contato 1.2, em revisão | [Privacidade](https://sites.google.com/view/terra-live-wallpaper/privacidade), [Termos](https://sites.google.com/view/terra-live-wallpaper/termos), [Contato](https://sites.google.com/view/terra-live-wallpaper/contato). HTML defasado das fontes locais; Termos públicos ainda tratam efeitos futuros como indefinidos. Não foi teste visual/mobile ou comparação integral; republicar após aprovação. |
| Teste e oferta | Brasil, adultos e instrumentos de teste, sem cobrança real; Premium inclui futuros efeitos/estilos, artes separadas | [Decisões de 15/10](RESPOSTAS-RAFAEL-TESTE-FECHADO-2026-10-15.md). IARC 14 relatado não resolve público-alvo ou ECA Digital. |

## A — Antes de iniciar o teste fechado

A identificação do candidato destrava a conferência final das declarações e textos. Levantamento de fornecedores e avaliação de público podem avançar em paralelo. Responsáveis abaixo seguem a [divisão registrada](RETORNO-GABRIEL-PLAY-CONSOLE-2026-10-19.md#divisão-dos-próximos-passos); não atribuem nova aprovação a terceiros.

| Feito | Ação/dependência | Responsável | Evidência para encerrar |
|---|---|---|---|
| [x] | A1. Diagnosticar os dois erros ([roteiro do Console](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#conferência-necessária-no-console--rafael); **22/10: causa identificada, rascunho do fechado sem pacote** — [conferência](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#conferência-direta-no-console--22102026)): AAB incluído e processado, composição da versão, maior `versionCode`, identidade/assinatura e compatibilidade/segmentação | Gabriel, com dados do Console fornecidos por Rafael | Metadados sanitizados do pacote e versão, comparação dos códigos/segmentação e causa demonstrada; sem credenciais ou listas de contas. **Concluído em 28/10:** causa de 22/10 confirmada antes da inclusão exclusiva do 7; salvamento e validação com 0 erros — [evidência](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#composição-do-teste-fechado--28102026). |
| [ ] | A2. Corrigir a causa comprovada de A1, identificar um candidato e reconferir a validação da versão | Gabriel | Branch/commit, versionName/versionCode, SHA-256 do AAB, certificados públicos pertinentes, configuração e mapping R8 arquivados; pacote correto processado e erros resolvidos no Console. Gabriel reconfirmou em 20/10 que 6 (1.0.5) é a última versão; código 7 preparado no fonte, sem AAB gerado. Se houver novo envio antes da geração, reconferir o código. Preparação independente: [roteiro de release](../../README.md#rodando-o-projeto) atualizado localmente para configuração e verificação do candidato; ainda sem novo AAB. Em 20/10, Gabriel indicou o endpoint R2 de testes e confirmou ambiente Windows/Billing utilizáveis; 12 arquivos de amostra (ZIPs, prévias e miniaturas de três artes) acessíveis por HTTPS, com tamanho/hash conferidos contra o catálogo embarcado. Revisão de geração, execução dos gates no Windows e AAB final continuam pendentes; gate Android neste Mac não iniciou por falta de Java. Essa preparação não conclui A1/A2. |
| [ ] | A3. Validar Segurança dos dados contra A2 e fornecedores reais ([matriz de 19/10](../../.lgpd/seguranca-dados-play-2026-10-19.md); **26/10: respostas conservadoras propostas para o candidato 7** em [respostas propostas](../../.lgpd/seguranca-dados-respostas-candidato-7-2026-10-26.md), nada salvo/enviado/aprovado no Console; **27/10:** [reinspeção do AAB 7 no Windows](../../.lgpd/seguranca-dados-respostas-candidato-7-2026-10-26.md#10-atualização-em-27102026--reinspeção-do-aab-7-no-windows) sem `AD_ID` no manifesto final e sem alívio de resposta; **27/10, posterior:** [conferência e salvamento no Console](../../.lgpd/seguranca-dados-respostas-candidato-7-2026-10-26.md#11-atualização-em-27102026--conferência-e-salvamento-no-console): só o 7 ativo no Teste interno, demais versões arquivadas, maior código 7, AD_ID salvo como Não em 24/10, política sem `?authuser=1` desde 22/10; Segurança dos dados salva com autorização às 19:59, com finalidades e URL alinhadas; nada enviado para revisão ou aprovado, A3 continua aberto) | Gabriel; Rafael confirma configurações das contas | Revisão sanitizada do formulário: MET/Cloudflare/Billing/suporte, IPs/identificadores, exceções de compartilhamento, prévias automáticas e coleta obrigatória/opcional; manifesto/SDKs/endpoints e informação/permissão de localização conferidos no candidato. Aprovação registrada, sem presumir ausência de compartilhamento. |
| [ ] | A4. Registrar análise ECA Digital, possível acesso por adolescentes e necessidade/desenho de sinais de idade ([registro](../../.lgpd/adolescentes-eca-digital.md)) | Gabriel, com validação jurídica necessária | **29/10: avaliação contextual preparada**, com matriz, evidências/limites do produto e recomendação condicionada por fase. **A4 segue aberto e bloqueia envio:** faltam evidência IARC/acervo/sinais, decisão de Gabriel e validação jurídica necessária; medidas exigidas devem ser comprovadas. Público 13+ e teste adulto preservados; nenhum SDK adotado. |
| [ ] | A5. Aprovar revisão/vigência dos textos aplicáveis e conferir app/site/Console após A2–A4 | Gabriel aprova; publicação pelo responsável pelo site | Revisão local de 28/10: Privacidade 1.19 / Termos 1.10 / Contato 1.9 para o pacote 8, ainda sem aprovação/vigência ou republicação. [D1–D7 da Segurança dos dados](../../.lgpd/seguranca-dados-respostas-candidato-7-2026-10-26.md#8-divergências-identificadas-entre-esta-proposta-e-as-fontes-locais-privacidademd-114--termosmd-18) conciliados no texto na parte 1b ([desfecho por item e evidências](../../.lgpd/seguranca-dados-respostas-candidato-7-2026-10-26.md#12-conciliação-editorial-d1d7--28102026-a5-parte-1b)): D1–D4/D6 esclarecidos, D5/D7 superados como divergências de redação. Respostas conservadoras preservadas, sem alterar Console. Aprovação/vigência e republicação ficam para a fatia 2; A4 e pendências de fornecedores/SDKs permanecem abertos. Exige revisão aprovada identificada, páginas HTTPS sem login e no celular, links sem seleção de conta, conteúdo sincronizado e vigência ligada ao candidato. Gerar HTML não republica Sites. |
| [ ] | A6. Conferir Brasil na faixa e demais telas pertinentes, licenças e configuração dos instrumentos de teste | Rafael | Configuração salva e divergência de todos os países resolvida. Cadastrar e confirmar licença de todos os participantes que terão acesso à interface de compras, inclusive quem pretende usar apenas o gratuito; ampliar a cobertura das sete contas antes de convite/distribuição. Sem licença confirmada, não liberar o candidato ao participante no fluxo atual. Evidência sanitizada, sem dados pessoais no Git; instrumentos no checkout em A8. |
| [ ] | A7. Fechar configuração impeditiva e disponibilizar candidato na faixa para conferência, após A2–A6 | Gabriel e Rafael conforme acesso | Itens restantes identificados e resolvidos, contatos/ficha/recursos e produtos necessários ao roteiro conferidos; mínimos seguros de instalação, informação, uso gratuito e entrega definidos e comprovados. Publicação e convites exigem autorização correspondente. |
| [ ] | A8. Com o candidato acessível pela Play, conferir conta e instrumento de teste antes de concluir cada compra | Rafael | Conferir no respectivo checkout a conta já licenciada em A6 e o instrumento de teste, sem cobrança real; sem essa confirmação, não concluir a compra. Não é necessário que todos comprem; as 12 adesões de B1 não substituem a licença de todos que terão acesso à compra. |

Não marcar A7 apenas por CI verde. Aceites completos visuais, de catálogo, compras e desempenho permanecem nos roteiros detalhados e serão executados durante o teste; falhas impeditivas conhecidas precisam ser resolvidas antes da distribuição.

**A2 em 28/10 — permanece `[ ]`:** branch/commit, versão, hash do AAB, certificado de upload e mapping R8 identificados no [parecer do candidato](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#atualização-em-22102026--candidato-7-identificado); inclusão do pacote processado e erros resolvidos no [registro de 28/10](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#composição-do-teste-fechado--28102026). Falta comprovar arquivamento da configuração R8 do candidato e registro do certificado público de distribuição pertinente (a [conferência no Console de 22/10](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#conferência-direta-no-console--22102026) distingue o de distribuição do de upload, sem registrar seu valor). O hash do AAB enviado continua vinculado documentalmente, sem leitura no Console. As pendências de geração/gates/AAB de 20/10 na célula A2 são históricas, superadas pela preparação de 22/10 no topo; não exigem gerar outro pacote.

### Detalhamento preparatório de A — regularização do candidato 7 (22/10)

A ordem abaixo detalha os itens A1–A8 existentes. Todas as escritas no Console, cadastro/ativação, publicação de páginas, associação/envio/distribuição e convites dependem de autorização posterior sobre o resultado preparado. Nenhuma foi executada nesta preparação de 22/10; a associação/salvamento/validação ocorreu depois, com autorização em 28/10, antes de A3–A6, sem envio. Segurança dos dados, ECA e aprovação legal mantêm evidências próprias; a conclusão de um formulário não encerra os demais gates.

| Item / ordem e dependências | Procedimento preparado e responsável | Evidência e condição de avanço |
|---|---|---|
| A1 → A2 — conciliar antes de mudar a faixa | Histórico preparatório (superado quanto ao pedido de leitura ao Rafael pela [conferência direta de 22/10](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#conferência-direta-no-console--22102026)): Gabriel, com leitura sanitizada fornecida por Rafael: pacote `com.terra.wallpaper`, código 7 / 1.0.5, estado do processamento, composição atual/retida, segmentação e textos integrais dos dois erros; comparar SHA-256 do AAB `0ed276df2a2d0d6f3610849a685b34d071a7cd5b4b4ec6f79520a40e50345280` e certificado público de upload do parecer de 20/10 com a origem oficial, distinguindo certificado de distribuição Play. **Estado após a conferência de 22/10:** código 7 (1.0.5) já ativo no interno e na biblioteca (maior código: 7), certificado de upload no Console igual ao do AAB 7 (`E0:18:F8:…:4D:E3:16`) e rascunho do fechado vazio identificado como causa dos dois erros; falta incluir o 7 da biblioteca no rascunho fechado, concluir a declaração de ID de publicidade e remover `?authuser=1` da URL da política (após autorização). **Pendências históricas superadas:** AD_ID e URL conferidos na [seção 11 de 27/10](../../.lgpd/seguranca-dados-respostas-candidato-7-2026-10-26.md#11-atualização-em-27102026--conferência-e-salvamento-no-console); inclusão exclusiva do 7, salvamento e validação em [28/10](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#composição-do-teste-fechado--28102026), autorizados antes de A3–A6 | Parecer estático externo identificou AAB de 94.604.586 bytes; este Mac não o reextraiu. Registro anterior de "textos dos erros e certificado oficial ainda pendentes" superado em 22/10 (causa dos erros e certificado de upload conferidos no Console; o hash SHA-256 `0ed276df…` do AAB enviado não aparece no Console e continua vínculo só documental; reconferência da validação do rascunho preenchido segue pendente). Disponibilidade interna e envio do 7 não demonstram resolução. Não gerar/reassinar pacote sem defeito ou requisito demonstrado e nova proposta **Nota de 28/10:** a reconferência do rascunho preenchido foi concluída com 0 erros e 1 aviso de símbolos nativos, conforme [registro de 28/10](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#composição-do-teste-fechado--28102026), superando essa pendência histórica. Essa validação comprova a correção de A1; A2 segue aberto pelas evidências restantes acima. |
| A3 — AD_ID e Segurança dos dados, em paralelo à conciliação | Gabriel prepara AD_ID = **Não**, com manifesto/169 bibliotecas/DEX sem publicidade segundo inspeção externa do código 7. Rafael confirma práticas/configurações MET, R2, Billing, localização e suporte; Gabriel confronta o formulário com candidato e versões ainda distribuídas | Não deduzir ausência de IDs/coleta/compartilhamento de AD_ID. Incluir prévias automáticas, coordenadas/IP, caches, histórico/tokens, suporte e SDKs reais. Logs, retenção e exceções de prestador/efemeridade ainda precisam de evidência; A3 continua aberto, sem salvar respostas por esta preparação. **26/10:** [respostas conservadoras por pergunta](../../.lgpd/seguranca-dados-respostas-candidato-7-2026-10-26.md) propostas para `AD_ID` e Segurança dos dados do candidato 7 (`c1848a9`), sem nada salvo, enviado ou aprovado no Console. **27/10:** [reinspeção do AAB 7 no Windows](../../.lgpd/seguranca-dados-respostas-candidato-7-2026-10-26.md#10-atualização-em-27102026--reinspeção-do-aab-7-no-windows): 8 permissões sem `AD_ID`, 169 bibliotecas, `datatransport` via Billing e nenhuma divulgação oficial de `play-services-location`/`billing-ktx`; respostas mantidas nessa reinspeção. **27/10, posterior:** [conferência e salvamento no Console](../../.lgpd/seguranca-dados-respostas-candidato-7-2026-10-26.md#11-atualização-em-27102026--conferência-e-salvamento-no-console): faixas antigas conferidas e arquivadas, nove permissões por versão sem `AD_ID`, maior código 7 e rótulos conferidos; Segurança dos dados salva às 19:59 com 9 subtipos em 8 categorias, coleta e compartilhamento conservadores, finalidades ajustadas e exclusão em `/privacidade`. Envio para revisão bloqueado pelas etapas do painel; nenhuma aprovação do Google, A3 permanece aberto |
| A7 — preparar produtos, após catálogo e preços aprovados, antes de checkout | Gabriel confere relação integral por arte; Rafael/titular executará somente após autorização: 361 IDs pagos de artes + `terra_premium`, compras únicas, R$ 1,99 por arte / R$ 49,90 Premium, BR e opções de compra ativas. R$ 0,99 apenas como oferta de desconto para a seleção rotativa aprovada/configurada e elegível; seleção inicial pendente, sem atribuição de desconto por arte nesta matriz | Matriz local: 366 artes, cinco amostras sem produto, 361 IDs únicos; segundo relato de 21/10 só Premium existe no Console. Preços/país/estado reais e faltantes exigem exportação atual e comparação nos dois sentidos, sem remover extras automaticamente. Nomes preparados são propostas, não títulos já cadastrados |
| A7 — tratar diferença acervo/vitrine sem excluir artes | Gabriel registra que `Catalogo`/`NomesCatalogo` no commit candidato filtram `estacionamento` e `natal`: oito artes preservadas no acervo ficam ocultas; no caminho normal são 358 artes visíveis / 353 IDs de arte consultados, além de Premium | Manter os 361 IDs na conciliação. Cadastro não torna as oito visíveis nem comprova entrega integral no candidato; aceite/decisão deve ser explícito. Sem alterar código ou reduzir escopo nesta preparação |
| A7 — acesso pago do revisor, antes de salvar instrução final/enviar revisão | Gabriel prepara consulta oficial sobre compras únicas sem login, Premium e artes independentes, reutilização e localização; titular envia somente com autorização. Após resposta aplicável, comprovar o procedimento no candidato e preparar instrução completa em inglês | Fonte pública consultada não determinou o método para este fluxo. Não presumir licença do revisor, associação por e-mail, cartão de teste, promoção adequada ou acesso total. Tradução/desmarcar acesso total não resolve acesso. Se exigir outro binário, voltar à proposta antes de implementar |
| A4 → A5 — aprovar textos e metadados antes de publicar | Gabriel consolida avaliação técnica/escopo e aprovação do responsável legal com validação jurídica necessária; Rafael confirma fornecedores e operação. Proposta integral para análise: Privacidade 1.19 / Termos 1.10 / Contato 1.9 locais (revisão de 28/10, pacote 8); Gabriel confirmou em 22/10 que dará o aceite dos textos e publicará as três páginas; revisão/vigência e validação jurídica necessária ainda pendentes | GET público de 22/10: 200, versões 1.9 / 1.5 / 1.2 ainda em revisão. Diferenças: futuros efeitos/estilos no Premium, campos de suporte/direitos, caches e teste adulto/BR. Definir aprovação e vigência da revisão destinada ao pacote 8; o app embute metadados de rascunho e atualizar site não o modifica. ECA, Segurança dos dados e validação jurídica continuam pendentes |
| A5 — verificar publicação autorizada | Gabriel, responsável pela publicação confirmado em 22/10: gerar/exportar pelo processo existente, publicar as três páginas aprovadas, conferir texto, metadados e links HTTPS sem login/seleção de conta em navegador e celular | Preservar versões/condições anteriores. Comparar páginas com fontes aprovadas e cópia embarcada, registrando qualquer diferença. HTTP 200 isolado não conclui A5; geração HTML não publica Sites |
| A2–A7 — associação/validação antecipadas em 28/10; envio após os bloqueios resolvidos | Histórico de 22/10: Gabriel/Rafael conforme acesso e autorização: selecionar o pacote já processado e conciliado no rascunho fechado, conferir composição/atualização e executar a validação da versão. **Nota de 28/10:** a ordem anterior (“associar candidato e revalidar somente depois dos bloqueios resolvidos”) foi superada pela decisão de Gabriel: o niemeyer incluiu somente o 7 da biblioteca, salvou e validou antes de A3–A6, sem enviar — [registro](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#composição-do-teste-fechado--28102026). Gabriel/Rafael seguem responsáveis conforme acesso e autorização | Antes/depois sanitizados com código/hash/procedência, mensagens e estado; resolver a causa de cada erro. Não reenviar o mesmo código nem excluir pacote como tentativa. Pendência impeditiva de acesso, produtos, legal ou declaração mantém envio/distribuição bloqueados |
| A6 → A8 — checkout autorizado com zero cobrança real | Rafael: confirmar BR na faixa e produtos, licenças salvas para todos que terão acesso à interface de compra; instalar candidato pela Play, conferir conta do checkout e indicação/instrumento de teste antes de cada confirmação. Premium e arte são jornadas distintas | Sem indicação inequívoca de teste, cancelar e registrar falha. Concluir compra apenas com autorização de operação e instrumento de teste; verificar direito, assinatura, download/aplicação e restauração, mantendo B2 para a cobertura completa. Isto não é o procedimento do revisor |
| A7 → B1/B4 — adesão e feedback | Rafael confirma canal de feedback e requisito exibido no painel da conta; após autorização de distribuição/convites, orientar saída do interno e adesão ao fechado conforme regra da faixa. Gabriel/Rafael acompanham participantes, período contínuo e resultados por candidato | Sete licenças não são adesões; teste interno não inicia o prazo do fechado. Se aplicável à conta, confirmar 12 adesões contínuas por 14 dias no painel, sem expor e-mails. Evidência de adesão, execução funcional e feedback separadas; prazo não concede produção automaticamente |

**Nota de 28/10 sobre A3:** a afirmação de 27/10 acima, “Envio para revisão bloqueado pelas etapas do painel”, foi superada: após compor e validar, o botão de enviar 13 mudanças ficou habilitado, sem clique, e as verificações rápidas terminaram sem problemas listados — [registro](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#composição-do-teste-fechado--28102026). Isso não é aprovação de A3 nem autorização de envio; com publicação gerenciada desativada, aprovação publicaria logo em seguida para os testadores.

Fontes oficiais consultadas em 22/10: [acesso do revisor](https://support.google.com/googleplay/android-developer/answer/15748846?hl=en), [preparar revisão](https://support.google.com/googleplay/android-developer/answer/9859455?hl=en), [teste de Billing](https://developer.android.com/google/play/billing/test), [promoções](https://support.google.com/googleplay/android-developer/answer/6321495?hl=en), [Segurança dos dados](https://support.google.com/googleplay/android-developer/answer/10787469?hl=en) e [requisitos de teste](https://support.google.com/googleplay/android-developer/answer/14151465?hl=en). O procedimento específico de acesso pago ainda depende de confirmação oficial aplicável; nenhuma fonte aqui equivale a aprovação do candidato ou jurídica.

## B — Durante o teste e o período exigido

| Feito | Ação/dependência | Responsável | Evidência para encerrar |
|---|---|---|---|
| [ ] | B1. Completar adesões elegíveis e acompanhar o requisito conforme as adesões mostradas no Console | Rafael | Contagem e início efetivo registrados sem e-mails; para o requisito relatado, pelo menos 12 inscritos continuamente por 14 dias. Lista de licença, convite e instalação não substituem adesão; checkout não inicia nem reinicia a contagem. |
| [ ] | B2. Executar compras Premium/artes com instrumentos de teste, direitos e entrega | Rafael; Gabriel corrige defeitos | Resultado por versão/commit: compra, pendência, cancelamento/erro, download, aplicação, reinício, reinstalação/restauração, revogação/reembolso e offline. Premium não libera artes. |
| [ ] | B3. Executar aceites das etapas 1–4 e 6 abaixo em aparelhos representativos | Rafael; Gabriel corrige defeitos | Clima só na home, permissão negada/revogada, bloqueio, backup, suporte, logs release, isolamento debug, catálogo, efeitos, memória/bateria/estabilidade e compatibilidade registrados por aparelho e candidato. |
| [ ] | B4. Consolidar feedback e validar correções no artefato correspondente | Gabriel e Rafael | Defeitos e retestes identificados, requisito contínuo acompanhado; não conceder aceite só por passar 14 dias. |

Referência: [requisitos oficiais de teste](https://support.google.com/googleplay/android-developer/answer/14151465?hl=pt-BR). Confirmar o requisito efetivo da conta; cumprir o prazo não concede acesso automático à produção.

## C — Antes de produção ou cobrança real

| Feito | Ação/dependência | Responsável | Evidência para encerrar |
|---|---|---|---|
| [ ] | C1. Consolidar aceites e correções de B, catálogo anunciado, produtos/preços, licenças e continuidade/retirada | Gabriel e Rafael | Etapas 1–6 aprovadas para a revisão final; cada arte anunciada tem entrega e origem/licença verificadas, oferta corresponde ao Console e plano de exceção/continuidade está definido. |
| [ ] | C2. Fechar operação e requisitos legais/comerciais pertinentes | Gabriel valida; Rafael comprova contas/operação | Endpoint adequado, fornecedores/bases/governança, suporte/direitos/retenção em operação, documentos aprovados e verificações de perfil/pagamentos exigidas ao titular comprovados; avaliação ECA/público registrada. |
| [ ] | C3. Obter acesso à produção e autorização de distribuição | Gabriel e titular da conta | Requisito do teste atendido, acesso aprovado, ficha/declarações e candidato conferidos; só então executar etapas 8–9. Teste fechado não autoriza cobrança real. |

Fontes para as revisões pendentes: [Segurança dos dados](https://support.google.com/googleplay/android-developer/answer/10787469?hl=pt-BR) e [ANPD/ECA Digital](https://www.gov.br/anpd/pt-br/assuntos/eca-digital). Estas referências não constituem parecer ou aprovação.

## Como obter evidência sem SDK local

Na conferência feita no Mac em 22/10 não havia JDK/SDK Android utilizável nem AAB local comprovado; no Windows há JDK 17/SDK, e o AAB 7 e seus registros ficam em `output/terra-release-c1848a9-v7/`, fora do Git. A CI fornece testes/lint/compilação e APK debug da revisão identificada; compras/atualizações precisam da Play e wallpaper/desempenho precisam de aparelhos. Instalar JDK 17 + SDK e conectar aparelho permite diagnóstico local posterior. Nenhum teste Android local ou execução do candidato foi realizado nesta atualização documental.

## Critérios detalhados e fontes históricas

As etapas 1–9 abaixo preservam os critérios de aceite. Itens de implementação marcados e números de testes são registros datados, sem novo aceite nesta conciliação; verbos futuros em relatos datados descrevem aquele momento. A ordem operacional atual é A → B → C acima, mantendo os gates de cada etapa.

Histórico: [entrega das etapas 1/2](PROMPT-RAFAEL-AVALIACAO-ETAPAS-1-2-E-CATALOGO-2026-10-10.md), [adaptação R2/R8 de 11/10](ADAPTACAO-R2-R8-2026-10-11.md), [v2 de 13/10](AVALIACAO-V2-RAFAEL-2026-10-13.md), [documentos de 11/10](ETAPA-5-DOCUMENTOS-E-LANDING-2026-10-11.md), [refinamento de 15/10](REFINAMENTO-EDITORIAL-LEGAL-2026-10-15.md) e [respostas de 15/10](RESPOSTAS-RAFAEL-TESTE-FECHADO-2026-10-15.md). O retorno de 19/10 e o quadro atual superam os estados antigos de CI, países, licenças e candidato.

## Decisões registradas — alcance e limites

- [x] Premium `terra_premium`: compra única de R$ 49,90, apenas efeitos e estilos. Não libera artes pagas.
- [x] Compra avulsa registrada pelo titular: R$ 1,99 por arte específica, não por todas as variantes do cenário.
- [x] Canal oficial de suporte e privacidade: `suporteterrabr@gmail.com`.
- [x] Responsável indicado: Rafael Huppes, pessoa física. Validar os papéis jurídicos na etapa 5.
- Registro histórico de público 13+ em 09/10; teste restrito a adultos conforme decisão de 15/10. IARC 14 no Brasil foi relatado em 19/10. Público-alvo do produto e avaliação ECA continuam pendentes, sem conclusão por esses registros.
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
- [x] Correção inicial em 10/10: arte bloqueada não encaminha à compra de Premium. A compra individual foi integrada depois, na etapa 4; conferir a jornada no candidato pela Play.
- [x] Especificar regressões: seis testes em `AcessoArteTest` cobrem amostras, variantes pagas, preferências antigas, troca entre cenários, arte inexistente e destrave de debug. Seleção/aplicação valida o direito no código; fluxo de compra/restauração e interface serão conferidos em aparelho.
- [x] Executar testes unitários, lint e build de debug apropriados à mudança: **75 testes, zero falhas/erros; lint sem erros (com warnings existentes); assembleDebug aprovado**. Testes/build repetidos depois da atualização dos termos.
- [ ] Registrar resultado em aparelho/versão Play antes do aceite final de publicação.

**Aceite:** UI e wallpaper aplicam o mesmo direito; Premium afeta somente efeitos/estilos; o usuário não é induzido a comprar Premium para receber arte.

**Limite:** a verificação desta etapa não conclui o aceite de compra individual e loja remota, implementadas nas etapas 3–4. Os roteiros abaixo preservam a conferência de direitos; usar a revisão identificada no registro atual, sem presumir disponibilidade de APK histórico.

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
- [ ] Concluir aceite do catálogo conciliado na adaptação de 11/10 e v2 de 13/10: manifesto de 71 cenas/366 artes, vitrine de 69 cenas/358 artes/67 cards; conferir composição e renderização no candidato. A conciliação técnica não aprova a oferta final.
- [x] Separar amostras, artes pagas e exclusões na tabela. Os sete temas excluídos não recebem proposta de produto; `timessquare`, `ogro` e `eiffel` ainda existem no acervo local e permanecem identificados como excluídos, sem apagar os arquivos.
- [ ] Conferir cortes no manifesto de produção e artefato efetivamente distribuído. Inventário local não é manifesto de publicação.
- [x] Preparar tabela por arte: identificação, origem, arquivos, hashes, dimensões, bytes, miniaturas, preço regular e ID de produto **proposto**. Console e teste Android marcados como não verificados; miniaturas dedicadas ausentes identificadas.
- [x] Novas artes/variantes: **cada arte nova será uma compra separada**, confirmado pelo usuário em 10/10.
- [x] Regra confirmada em 15/10: futuros efeitos/estilos incluídos no Premium, artes separadas, sem promessa de frequência de lançamentos. Conferir correspondência da oferta no candidato.
- [x] **Sem packs comerciais no lançamento**, confirmado pelo usuário em 10/10. Pacotes de arquivo para download são outro conceito.
- [ ] Configurar preço regular e ofertas reais de R$ 0,99, sem confundir preço promocional com uma nova posse.
- [ ] Conferir países de oferta/distribuição e preços correspondentes.
- [ ] Preservar registros de origem e licenças das artes, fontes, sprites e dados meteorológicos, com revisão dos itens efetivamente distribuídos.

**Aceite:** existe uma lista única do que o usuário pode experimentar, comprar e receber. O inventário não contém produto sem entrega correspondente.

## 4. Implementar compra por arte e entrega remota

**Adaptacao em 11/10:** 366 variantes mescladas ao catalogo, 366 miniaturas WebP verificadas, caminhos remotos e hashes completos integrados. R8 9.1.29 versionado; CI passa a compilar release minificada. Debug aponta ao bucket de testes; release exige URL explicita. Seis ZIPs representativos conferidos. Falta revisao/aceite do Rafael, produtos na Play e configuracao de producao. Ver [roteiro atualizado](ADAPTACAO-R2-R8-2026-10-11.md). Nenhum AAB novo.


**Dependência para aceite:** catálogo/produtos da etapa 3 aprovados. Por solicitação do usuário, implementação adiantada em 10/10 com arquivos disponíveis; isso não conclui a etapa 3. Ver [integração e roteiro](INTEGRACAO-ARTES-ACERVO-2026-10-10.md).

- [x] Modelar posse por cenário + arte e associar os produtos pagos correspondentes; não usar um direito por cenário para liberar todas as variantes.
- [ ] Validar pela Play a integração de produtos/preços, compra, estados pendentes, confirmação, restauração e revogação; implementação não encerra o aceite.
- [x] Catálogo/miniaturas integrados, ampliados para 366 artes na adaptação R2 e refinados na v2; aprovação visual/completude da oferta pendentes.
- [ ] Configurar endpoint de produção do bucket e conferir controles de upload/acesso. Não incluir credenciais no aplicativo.
- [ ] Verificar origem/integridade de manifesto e pacotes, versionamento e compatibilidade com o motor.
- [ ] Validar o download integrado: tamanho, progresso, cancelamento, repetição, instalação segura e falhas compreensíveis.
- [ ] Preservar o wallpaper ativo quando houver falta de rede/espaço, interrupção ou pacote inválido.
- [ ] Validar limpeza de arquivos e uso offline do conteúdo instalado.
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
- [ ] Conferir cookies, registros, formulários e tratamento real do site publicado no Google Sites; domínio próprio não é condição para o teste. Conciliar a revisão pública com as fontes aprovadas.
- [x] Preparar geracao/exportacao explicita e testar compatibilidade com o importador, sem alterar o front-end de Willian.
- [x] Conferir texto, metadados, links relativos e historico; ampliar o check para as tres copias e a fonte canonica. Atualizar site nao altera apps instalados.
- [ ] Importar a revisao aprovada, publicar e verificar as tres paginas HTTPS sem login e com metadados finais.
- [ ] Decidir o tratamento de documentos pessoais na branch do Rafael e no PR #48, sem presumir apagamento do historico. Os registros locais foram versionados como historicos por solicitacao do usuario, com dados internos desnecessarios omitidos.

**Aceite:** comportamento, oferta, documentos e paginas publicas coerentes para a mesma revisao aprovada, sem confundir preparacao editorial com validacao juridica ou publicacao.

## 6. Preparar e validar uma única versão candidata

**Registro histórico de 11/10, superado pelo quadro de 19/10:** CI da revisao `6dffa5a` aprovada (build e varredura de segredos). Usuario ainda nao sabe o maior versionCode enviado e nao recebeu o resultado da avaliacao de Rafael. A versao do projeto segue codigo 6/1.0.5; nenhum novo AAB foi gerado para envio. Validacao em aparelho permanece com Rafael. Ver [encaminhamento](ENCAMINHAMENTO-HUPPES-2026-10-11.md).

- [ ] Resolver defeitos conhecidos das jornadas ofertadas; não confundir CI aprovada com aceite do produto.
- [ ] Validar instalação limpa online/offline, onboarding e atualização de uma versão de teste anterior.
- [ ] Testar clima na home, tela bloqueada/apagada, app por cima, retomada e instalação sem cache.
- [ ] Testar efeitos, brilho, rolagem, trocas de arte e recriação do wallpaper.
- [ ] Medir memória, bateria, estabilidade e compatibilidade em aparelhos representativos; verificar requisitos de páginas de memória de 16 KB aplicáveis.
- [ ] Conferir certificado de upload, Play App Signing, acesso autorizado às chaves e armazenamento seguro, sem ler/publicar senhas no relatório.
- [ ] Pendência de segurança ainda sem evidência de fechamento: responsável confirmar senha da chave de upload em gerenciador seguro, remoção da cópia em texto desprotegido e backup protegido da chave em dois locais separados. Registrar somente confirmação, sem caminhos pessoais, senhas ou cópia da chave; esta conferência não requer ler credenciais nem gerar nova chave.
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
