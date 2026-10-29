# ECA Digital e sinais de idade — avaliação contextual do Terra

Avaliação documental de **29/10/2026**, para A4. Recomendação técnica sujeita à
**decisão de Gabriel e validação jurídica necessária**; nenhuma delas foi obtida
nesta entrega. Público pretendido **13+**, teste fechado **somente adultos no
Brasil**, conforme decisão de 28/10 em [TASKS](../docs/dev/TASKS.md).
**A4 permanece aberto e bloqueia o envio do teste.**

## 1. Versão, método e limites

Base inspecionada: `820e09893b70b86ab2b6a1d83ee9b77709bbaedc`,
`com.terra.wallpaper`, fonte **1.0.5 / código 7**, minSdk 26 / targetSdk 36.
Código Kotlin, catálogo, manifesto principal e dependências declaradas foram
conferidos. `git diff c1848a9 820e098 --` nos caminhos de código, catálogo,
manifesto e configuração Android examinados não apresentou diferenças.

O [diagnóstico do candidato](../docs/dev/DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md)
vincula documentalmente o AAB 7 a `c1848a9`, com textos 1.14/1.8/1.6.
As fontes atuais têm textos 1.19/1.10/1.9, ainda rascunhos, destinados ao pacote
8 planejado. **Não foram abertos AAB, Console, contas, logs ou caixa de suporte
nesta sessão**, nem gerado pacote 8. A equivalência estática do código não prova
configuração, comportamento em aparelho ou conteúdo de um futuro binário.

A conferência de 22/10 registrou público-alvo 13–15, 16–17 e 18+ no Console;
é evidência anterior, sem reconferência agora. **IARC 14 no Brasil é relato**:
faltam resultado efetivo, descritores, respostas e fundamento da classificação.
Classificação indicativa, público declarado, idade aferida e recrutamento adulto
são informações diferentes. A faixa 13–15 não demonstra adequação aos 13 anos.

Esta avaliação aprofunda a preparação de 19/10 (`c746d19`, PR #52, incorporada
em `1f21454`); preserva seus limites e substitui sua pendência de enquadramento
por análise e recomendação condicionada. Não constitui aprovação jurídica,
RIPD concluído, comprovação de controles da Play ou autorização de distribuição.

## 2. Fontes oficiais consultadas em 29/10/2026

| Fonte e natureza | Teor utilizado e limite |
|---|---|
| **Lei vigente:** [Lei 15.211/2025, texto atualizado na Câmara](https://www2.camara.leg.br/legin/fed/lei/2025/lei-15211-17-setembro-2025-797997-normaatualizada-pl.html) | Arts. 1º–18, 19–26 e 39: escopo, prevenção, adequação, idade e supervisão; distinguir capítulos específicos. [Planalto](https://www.planalto.gov.br/ccivil_03/_ato2023-2026/2025/lei/l15211.htm) bloqueou a consulta; usado o texto oficial alternativo. |
| **Decreto vigente:** [12.880/2026, Câmara](https://www2.camara.leg.br/legin/fed/decret/2026/decreto-12880-18-marco-2026-798813-publicacaooriginal-178481-pe.html) | Arts. 14–18, 22 e 24–27, 47 e 49: conteúdo, exceção editorial condicionada, proporcionalidade, sinais, contestação e avaliação de impacto. [Planalto](https://www.planalto.gov.br/ccivil_03/_ato2023-2026/2026/decreto/d12880.htm) indisponível na consulta. |
| **Lei vigente:** [LGPD, texto atualizado na Câmara](https://www2.camara.leg.br/legin/fed/lei/2018/lei-13709-14-agosto-2018-787077-normaatualizada-pl.html) e [Enunciado ANPD 1/2023](https://www.gov.br/anpd/pt-br/assuntos/noticias/anpd-divulga-enunciado-sobre-o-tratamento-de-dados-pessoais-de-criancas-e-adolescentes) | Arts. 6º, 7º, 11, 14, 18 e 38; melhor interesse, informação compreensível, bases por operação e impacto. Enunciado admite hipóteses dos arts. 7º/11, sempre com melhor interesse. |
| **Orientação publicada, não nova lei:** [Guia ANPD de fornecedores, v1.0, outubro/2026](https://www.gov.br/anpd/pt-br/centrais-de-conteudo/materiais-educativos-e-publicacoes/guia-fornecedores-de-produtos-ou-servicos-de-tecnologia-da-informacao.pdf/@@display-file/file), [publicação de 28/10](https://www.gov.br/anpd/pt-br/assuntos/noticias/anpd-publica-guia-que-esclarece-quem-deve-cumprir-e-quais-sao-as-responsabilidades-das-plataformas-na-protecao-de-criancas-e-adolescentes) | Pp. 24–32, 35–41: controle editorial, três requisitos cumulativos de acesso provável, presunções e análise contextual. Supera tratar todo o guia de escopo como mera proposta em consulta. Não é o guia definitivo de aferição de idade. |
| **Orientação preliminar:** [ANPD, mecanismos confiáveis, março/2026, v1.0](https://www.gov.br/anpd/pt-br/assuntos/eca-digital/mecanismos-confiaveis-de-afericao-de-idade-orientacoes-preliminares.pdf/@@download/file) | Pp. 4–9, 16–19: proporcionalidade, riscos do serviço e da própria aferição, minimização e inclusão; referência institucional preliminar de monitoramento, sujeita a alteração. |
| **Proposta em atualização:** [tomada de subsídios sobre guia de aferição](https://brasilparticipativo.presidencia.gov.br/processes/Guia-orientativo-mecanismos-de-afericao-idade/f/4604/) e [painel ECA da ANPD](https://www.gov.br/anpd/pt-br/assuntos/eca-digital) | Painel ainda apresenta a proposta após tomada de subsídios. Não foi localizada versão definitiva de aferição nas páginas consultadas; anexo da proposta não abriu. Não atribuir força normativa a essa proposta nem presumir sua aprovação pela data prevista. |
| **Documentação técnica/termos, não norma brasileira:** [visão geral Play Age Signals](https://developer.android.com/google/play/age-signals/overview?hl=pt-br), [solicitação](https://developer.android.com/google/play/age-signals/request-age-signals?hl=pt-br), [respostas](https://developer.android.com/google/play/age-signals/understand-age-signals-responses?hl=pt-br) | Beta; guia de solicitação descreve SDK 0.0.4, acesso e consulta separados. Respostas atualizadas em 24/10 distinguem origem e limites da faixa. Documentação não comprova habilitação na conta ou sinal efetivo no Brasil. |

O painel ANPD descreve adaptação/monitoramento em agosto–novembro/2026 e
fiscalização prevista a partir de janeiro/2027, preservando ações a qualquer
tempo. **Esse cronograma não suspende genericamente os deveres vigentes.**

## 3. Produto efetivo e acesso adolescente

Caminhos Kotlin abaixo relativos a
`android-app/app/src/main/java/com/terra/wallpaper/`; assets a
`android-app/app/src/main/assets/`. Funções indicam os pontos verificáveis.

| Operação conferida | Evidência e implicação |
|---|---|
| Acesso e atratividade | `engine/Catalogo.kt:carregar`, `ui/StoreTab.kt`, `ui/SceneDetailScreen.kt`: wallpaper animado, fantasia/pixel/artes, navegação sem cadastro próprio; público 13+ pretendido. Atratividade e facilidade são plausíveis. Restrição do teste depende da operação de recrutamento, sem barreira etária própria identificada. |
| Conteúdo ofertado | `atmosfera/catalogo/artes.json`: **366 artes / 71 cenas**; `Catalogo.carregar` e `NomesCatalogo.temporariamenteFora`: **358 artes / 69 cenas** na vitrine normal, oito de `estacionamento`/`natal` ocultas, preservadas no acervo. Falha de leitura retorna cinco cenas embarcadas; esse fallback não é filtro de idade. |
| Compras | `billing/BillingManager.kt:comprar/processar/reconciliar`, `Catalogo.produtoArte`: Premium e artes são compras únicas INAPP, itens determinados, com validação/restauração; nenhum bloqueio etário/parental próprio identificado. Ter Billing não comprova aprovação parental. A oferta efetiva depende de produtos/países ativos; só instrumentos de teste foram decididos para o fechado. |
| Localização/clima | Manifesto principal pede COARSE; `weather/LocationHelper.kt:getLocalizacao`: só lê posição com permissão, senão usa São Paulo. `WeatherRepository.kt:coordenada/fetchWeather/criarApi`: envia duas casas decimais à MET por HTTPS, com IP e identificação técnica. `WeatherCache.save` e serviço conservam coordenadas recebidas localmente, antes do arredondamento de rede. |
| Condições e retenção do clima | `weather/ClimaNaHome.kt:podeAtualizar`, `service/AtmosferaWallpaperService.kt:carregarClima`: home visível, tela interativa/desbloqueada, sem prévia/app aberto; revogar permissão impede nova leitura, **não apaga cache anterior**. `WeatherRepository` usa cache HTTP de 512 KiB; XMLs `backup_rules`/`data_extraction_rules` excluem caches pertinentes. Não equivale a prazo de exclusão ou supervisão parental. |
| IP e prévias | `engine/Acervo.kt:preview/baixarArte` e `ui/components/ScenePreview.kt`: prévias remotas e HD por HTTPS; R2 recebe dados de conexão/caminho. Prévia pode carregar antes de compra; não depende de permitir localização. Endpoint release vem de `BuildConfig.ACERVO_URL`; candidato 7 usa bucket de teste segundo diagnóstico, sem conferência HTTP nesta avaliação. |
| Suporte e compartilhamento | `ui/components/ReportarProblemaDialog.kt:enviarRelato`: cena/arte/efeito/sintomas, versão, modelo e Android no texto; usuário revisa/completa/anexa no cliente externo. `SceneDetailScreen` compartilha texto por intent externo. Não há publicação entre usuários dentro do Terra; e-mail/anexos podem identificar menores e conter dados sensíveis incidentais. |
| Controles atuais | `ui/SettingsTab.kt`, onboarding, manifesto e dependências `app/build.gradle`/`gradle/libs.versions.toml`: textos/ajustes comuns e permissão opcional; não identificados SDK de idade, recepção de sinais, perfis supervisionados, painel parental ou limitação/medição de tempo própria. Não identificados SDK dedicado de ads/analytics, conta própria ou chat no código lido. Isso não é auditoria de dependências transitivas/manifesto mesclado do AAB. |

**Inspeção visual limitada:** examinadas 12 miniaturas locais, escolhendo também
motivos de possível atenção: `bruxa/16bits`, `halloween/aqua`,
`tanque/needlefelting`, `trincheira/16bits`, `postapoc/cozy`, `heroi/pixel`,
`velhooeste/aqua`, `cyberpunk/cozy`, `cabana/aqua`, `jardim/clay`, `lavanda/aqua`
e `esfinge/gizcera`. Há motivos militares, ruínas e fantasia; na amostra não
observei nudez sexual ou violência gráfica. **Não é classificação indicativa.**
Não examinei as 358 artes integrais, animações/efeitos em execução, ZIPs HD,
prévias remotas nem todas as variantes; nomes/hash/miniatura não certificam HD.
Rafael deve conferir o acervo efetivamente distribuído e seus descritores;
Gabriel deve confrontá-los com IARC e registrar a cobertura por versão.

**Conclusão de enquadramento (inferência técnica):** para a distribuição futura,
o direcionamento declarado 13+ já inclui adolescentes; não depende de provar
os três requisitos de acesso provável. Para o teste adulto, o produto continua
atrativo e fácil de usar; são possíveis danos financeiros e exposição de
localização/IP e anexos. A probabilidade/gravidade agregadas ainda não foram
medidas nem permitem afastar risco significativo. Não concluo dispensa por
recrutamento adulto. A seleção editorial das artes também exige avaliar a
categoria do art. 2º, IX; o Terra vende conteúdo próprio selecionado, não é uma
loja que distribui aplicativos nem sistema operacional.

Aplicando o guia publicado de escopo: os requisitos de acesso provável são
cumulativos e há categorias presumidas, inclusive serviço com controle editorial.
Não há evidência suficiente para afastar eventual presunção editorial neste
caso. Mitigação não elimina, por si, o enquadramento. A falta de chat reduz
certos riscos de interação, sem dispensar deveres gerais. A conclusão sobre o
escopo do teste e eventual exceção editorial cabe à validação jurídica; até lá,
a recomendação considera aplicáveis as salvaguardas pertinentes ao produto.

## 4. Matriz de obrigações e lacunas

“Aplicável” indica a avaliação técnica para o produto 13+, sem certificar
cumprimento; “condicional” indica o fato que ainda precisa ser demonstrado.
Gabriel conduz A4; Rafael é o titular/controlador e executa as conferências
sob seu acesso. Validação jurídica não foi atribuída como aceite já dado.

| Obrigação | Aplicabilidade | Evidência no produto | Lacuna/medida | Responsável |
|---|---|---|---|---|
| Lei arts. 1º–5º; Decreto art. 24 — escopo, melhor interesse e proporcionalidade | Aplicável ao direcionamento 13+; alcance do fechado precisa de decisão fundamentada | Atratividade, uso sem cadastro, catálogo e fluxos acima | Adotar avaliação contextual; documentar risco inerente e residual, inclusive da aferição, sem dispensa por porte/recrutamento | Gabriel + Rafael; validação jurídica |
| Lei arts. 6º–8º — prevenção, conteúdo por faixa e informação no acesso | Aplicável | Curadoria e catálogo fixo, amostra visual parcial; IARC não comprovado | Conferir conteúdo integral/efeitos e classificação, divulgar faixa efetiva no acesso; não ofertar a 13 anos item incompatível. Nenhuma mudança de público autorizada | Rafael confere acervo; Gabriel concilia; jurídico valida |
| Lei art. 7º — privacidade mais protetiva por padrão | Aplicável | Permissão opcional, COARSE/HTTPS e limites do clima; prévias geram conexão sem localização | Avaliar configuração mais protetiva para menores, bloqueio de compartilhamento de geolocalização e prévias; cache/revogação/informação precisam de aceite. Permissão Android não é decisão parental nem base LGPD | Gabriel; Rafael valida jornada/configurações |
| Lei art. 9º; Decreto arts. 14–18 — conteúdo inadequado/proibido e verificação | Condicional ao conteúdo real; ausência de evidência não fecha inaplicabilidade | Nenhuma oferta de produto legalmente proibido identificada nos metadados/amostra; tanque/trincheira são imagens, não comércio de armas | Confirmar acervo e fundamento IARC. Se conteúdo sujeito ao art. 9º, impedir acesso indevido e atender verificação confiável a cada acesso; autodeclaração não basta. Não inferir obrigação universal de documento/biometria | Rafael + Gabriel; jurídico resolve enquadramento |
| Lei arts. 10–11 — experiência por idade, soluções reguladas | Aplicável | Mesma experiência para todos, sem sinal identificado | Definir diferenciação/adequação e contestação; não esperar solução pública para justificar omissão nem coletar dado excessivo | Gabriel + jurídico |
| Lei art. 12; Decreto art. 25 — aferição pela loja/SO, autorização de download, API | Dever direto de Google/loja/SO; não é execução comprovada do Terra | Distribuição/Billing pela Play; configuração familiar não conferida | Rafael/Gabriel verificam disponibilidade, autorização e controles em conta de teste supervisionada. Não atribuir ao app o que cabe à plataforma nem presumir autorização sem manifestação | Google na plataforma; Rafael/Gabriel verificam evidência |
| Lei art. 13; Decreto arts. 24–25 — finalidade exclusiva/minimização | Aplicável a futura aferição | Sem dado etário próprio identificado | Receber só faixa/origem necessária; evitar nascimento/documento/biometria, histórico, logs e correlação com compras/suporte. Definir descarte e base antes de implementar | Gabriel; controlador adota e jurídico valida |
| Lei art. 14 e art. 15; Decreto art. 26 — receber idade e proteger no próprio serviço | Aplicável ao produto 13+; Decreto explicita recepção para oferta inadequada/proibida | Nenhum receptor/filtro etário identificado | Art. 14 da lei tem redação geral; ausência de conteúdo 18+ não autoriza ignorá-lo. Preparar recepção de sinais e controles próprios; resolver alcance/solução na decisão final, sem transferir responsabilidade integral à Play | Gabriel propõe; Rafael/controlador + jurídico validam |
| Lei art. 39 — modulação/dispensa editorial de deveres listados | Condicional; requisitos não comprovados | Curadoria própria e canais comuns; sem mediação parental demonstrada | Avaliar separadamente a dispensa dos artigos enumerados: classificação/adequação e sinalização, transparência, mediação de conteúdo/dados/interação/transações e denúncias acessíveis. Não demonstrados esses requisitos, não adotar a dispensa. Ela não abrange automaticamente o art. 14 | Gabriel + jurídico; Rafael comprova execução |
| Decreto art. 22 — exceção para conteúdo editorial/autoral | Condicional; **não demonstrada** | Acervo selecionado; sem perfil infantil/supervisão comprovados | Mesmo se enquadrado como editorial, a dispensa de aferição exige condições, incluindo perfis/contas adequados e supervisão/bloqueio. Não basta chamar as imagens de arte | Gabriel + jurídico; Rafael comprova acervo/licenças |
| Lei art. 16 e parágrafo único; Decreto art. 47 — informação e impacto | Aplicável; requisitos do relatório precisam de enquadramento | Documentos acessíveis sem compra; mapa A01–A08 e risco ainda preliminares | Informação clara sobre riscos e medidas para menores/responsáveis; concluir avaliação de impacto/relatórios pertinentes e versão resumida quando exigida. Este registro não substitui esses relatórios | Controlador/Rafael + Gabriel; jurídico |
| Lei arts. 17–18 — supervisão, tempo, compras e geolocalização | Aplicável, proporcional à natureza do wallpaper | Ajustes comuns, checkout Play; sem ferramentas parentais próprias identificadas | Definir controles de tempo/uso, compras, privacidade e geolocalização, aviso de supervisão e informação em português; demonstrar integração com SO/Play e limites, inclusive wallpaper ativo fora do companion. Sem necessidade demonstrada de criar chat/conta | Gabriel especifica; Rafael testa; jurídico valida suficiência |
| Lei arts. 19–21 — monitoramento infantil/jogos/loot boxes | Sem gatilho identificado | Wallpaper/clima, sem monitorar criança para responsáveis, sem jogo ou recompensa aleatória paga | Reavaliar se funcionalidades mudarem; venda de arte determinada não é loot box | Gabriel |
| Lei arts. 22–26 — publicidade e redes sociais | Vedações de perfilamento pertinentes; regras específicas sociais sem gatilho identificado | Sem feed/contas conectadas/chat; compartilhar por intent externo não cria rede social própria | Não criar perfil publicitário com idade. Art. 24 está no capítulo social, mas seu caput usa formulação ampla: jurídico deve resolver alcance/vinculação até 16 anos, sem isenção automática por falta de conta; não presumir que 16–17 dispensa proteção | Gabriel + jurídico |
| LGPD arts. 6º, 7º/11, 14, 18 e 38 — dados de menores | Aplicável aos tratamentos efetivos | Localização/cache/IP, compras, suporte; [mapa](data-map.md), [bases](legal-basis.md) e [risco](avaliacao-risco.md) preliminares | Base por finalidade, melhor interesse, informação compreensível, direitos/retenção/fornecedores e impacto. Consentimento parental específico do art. 14, §1º, refere-se a crianças; não generalizar a adolescentes nem confundir com autorização de download/compras | Controlador/Rafael + Gabriel; jurídico valida |

## 5. Recomendação sobre aferição e sinais

**Condicionar a decisão final às evidências abaixo; não recomendar dispensa
geral nem introduzir coleta direta de documentos/biometria.** Para distribuição
13+, recomendo como caminho técnico preferencial receber faixa por API da
loja/SO, com controles próprios proporcionais, dada a redação do art. 14 e as
lacunas de adequação/supervisão identificadas. Aprovar só o SDK não resolveria A4.

Antes de escolher integração ou alternativa juridicamente fundamentada:

1. **Gabriel/Rafael:** obter resultado IARC e fundamento, conferir o conteúdo
   efetivamente ofertado por faixa e conciliar especificamente os 13 anos; se
   houver conteúdo restrito, delimitar itens/controles necessários. Não alterar
   silenciosamente o público pretendido nem retirar artes nesta fatia.
2. **Rafael/Gabriel:** demonstrar acesso à API, faixas/origens e estados reais
   para conta/distribuição brasileira, sem dados pessoais no registro; testar
   compartilhamento, recusa e supervisão. A API beta existe, mas sua cobertura
   no contexto concreto permanece desconhecida.
3. **Gabriel + validação jurídica, com adoção pelo controlador:** decidir alcance
   do art. 14, exceção editorial se cogitada, supervisão arts. 16–18 e caput do
   art. 24; registrar solução e medidas para o fechado e para distribuição 13+.
   Indisponibilidade da API exige alternativa/medidas fundamentadas, não dispensa.

### Desenho proposto se a integração for adotada em outra fatia

As regras abaixo **não estão implementadas**. O modo restrito proposto permite
somente informação/contestação e conteúdo local previamente aprovado para todas
as idades possíveis do sinal; até existir esse subconjunto validado, apenas
informação/ajuda. Sem novas compras, leituras de localização, prévias/downloads
ou consultas de clima nesse modo; preservar registros de direitos adquiridos,
sem permitir que restauração contorne restrição de conteúdo.

| Sinal/estado | Comportamento proposto |
|---|---|
| 18+ explícito, origem suficiente para o risco | Experiência adulta no catálogo aprovado, mantendo permissão facultativa e controles comuns. `SHARED` sozinho não prova idade; dado autodeclarado não satisfaz art. 9º. |
| 13–15 | Somente conteúdo aprovado para toda a faixa, proteções de compras/localização e supervisão validadas. Se IARC 14 implicar restrição de item, a faixa que inclui 13 não o desbloqueia; exigir sinal compatível ou manter modo restrito. |
| 16–17 | Experiência adolescente aprovada, mantendo supervisão e proteção financeira/geográfica. Se for necessária distinção até 16 pelo art. 24, aplicar proteção mais forte a toda a faixa ou sinal adequado, nunca presumir 17. |
| Abaixo de 13 | Impedir ingresso no fluxo 13+; oferecer informação e ajuda ao responsável, sem coletar documento no Terra. Isso não muda o público pretendido. |
| Faixa cruza limite, limites incoerentes ou origem insuficiente | Modo restrito; explicar insuficiência e caminho de correção/contestação. Intervalo aberto só vale com limite inferior explícito; `null` isolado não é adulto. |
| Sinal ausente, campos ausentes, `NOT_SHARED` ou recusa | Modo restrito com motivo claro e opção não coercitiva de resolver na plataforma; nenhuma presunção de adulto/autorização familiar. Não forçar coleta direta como fallback. |
| `VERIFICATION_REQUIRED` | Modo restrito; orientar resolução na Play e permitir nova tentativa depois, sem liberação antecipada. |
| Erro, indisponibilidade, offline ou instalação fora da cobertura | Modo restrito; aviso e nova tentativa por ação do usuário. Não reutilizar indefinidamente um sinal anterior como liberação, nem reconsultar continuamente. |
| Revogação de compartilhamento/aprovação ou mudança de sinal | Invalidar autorização derivada, reavaliar e voltar ao modo correspondente/restrito; interromper operações que dependam dela. Revogação etária não é reembolso: preservar direitos e conteúdo local permitido. |

A API consultada distingue faixas e origem (autodeclarada, gerida por responsável,
estimada ou verificada). A escolha da confiabilidade necessária depende do risco;
não basta comparar um número com 18. Os termos limitam uso à adequação etária e
ao próprio app, vedando publicidade/perfilamento/analytics. A biblioteca declara
não coletar dados de uso, o que **não dispensa** avaliar o tratamento pelo Terra.

Minimização proposta: decisão apenas em memória durante a sessão; sem faixa,
origem ou identificador em logs, e-mail, R2 ou analytics. Se a revogação efetiva
exigir identificador/notificação/persistência, definir arquitetura, necessidade,
retenção e exclusão em nova fatia antes de adotar a API; não prometer revogação
só com consulta inicial. Fornecer contestação acessível conforme Decreto art. 27,
sem pedir prova documental pelo suporte comum. Casos válidos, ausentes, limites,
recusa, erro, revogação e atualização deverão ter testes e aceite pela Play.

Qualquer adoção altera o inventário de tratamento e pode afetar bases, risco,
fornecedores, Privacidade/Termos/Contato, Segurança dos dados, declaração de
público/classificação, dependências e candidato. Revisar cada impacto antes da
distribuição; não marcar coleta/compartilhamento automaticamente apenas pela
inclusão da biblioteca. **Nenhum desses textos, formulários ou código foi
alterado nesta avaliação.**

## 6. Decisão por fase e aceites necessários

| Fase | Conclusão recomendada e bloqueio | Quem resolve / evidência de saída |
|---|---|---|
| Teste fechado adulto/Brasil | **A4 continua bloqueante.** O recrutamento reduz exposição operacional, mas não elimina o enquadramento do produto 13+. Não há base nesta análise para exigir universalmente documento/biometria, nem para liberar o teste sem decisão sobre recepção de sinais e demais medidas aplicáveis | Gabriel decide com validação jurídica e adoção pelo controlador; Rafael comprova recrutamento/configuração sem expor listas. Registrar se medidas prévias são necessárias ou qual solução fundamentada permite essa fase. Demais gates A2/A3/A5–A8 continuam independentes. |
| Futura distribuição 13+ | **Bloqueada para ampliação** até adequação demonstrada. IARC/acervo, recepção de sinais ou alternativa fundamentada, proteções por padrão, supervisão e dados ainda têm lacunas | Gabriel/Rafael fecham evidências e eventual nova fatia de implementação; jurídico valida enquadramento/suficiência e controlador adota decisão. Conferir tudo no candidato efetivo antes de distribuir. |

- [x] Avaliação contextual/documental produzida, com fontes, matriz e limites.
- [ ] Resultado IARC, descritores e conciliação da oferta aos 13 anos comprovados.
- [ ] Acervo integral/efeitos e candidato efetivo conferidos por Rafael/Gabriel.
- [ ] Disponibilidade e adequação dos sinais/alternativa demonstradas.
- [ ] Gabriel registra decisão por fase; controlador adota as medidas e há
  validação jurídica necessária, incluindo supervisão e eventuais relatórios.
- [ ] Medidas exigidas pela decisão implementadas e validadas em fatia própria,
  se necessárias; dados/documentos/declarações conciliados antes da distribuição.

**Fechamento:** esta entrega conclui a preparação documental solicitada, não A4.
A decisão seguinte é de Gabriel, com as evidências de Rafael e validação jurídica;
nenhum envio, publicação, convite ou ampliação de público fica autorizado.
