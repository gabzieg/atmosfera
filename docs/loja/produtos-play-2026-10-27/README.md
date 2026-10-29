# Cadastro das artes na Play — compra piloto validada

Registro da sessão de 27/10/2026. Fontes: prints do Console e checkout, relato direto de Rafael e catálogo versionado. Não contém pedidos, contas pessoais, tokens ou recibos.

## Resultado e causa

Antes do cadastro, o Console exibia somente `terra_premium`; o app mostrava "Compra indisponível" nas artes. O botão depende do preço/oferta retornados pela Play, independentemente do estado Premium. O piloto foi cadastrado com:

| Campo | Valor confirmado |
|---|---|
| Produto | `terra_arte_biblioteca_vangogh` |
| Nome | Biblioteca — Van Gogh |
| Opção de compra | `terra-arte-biblioteca-vangogh` |
| Tipo | Compra única, opção Comprar |
| Status | Ativo, compatível com versões anteriores |
| Preço Brasil | R$ 1,99, informado por Rafael e exibido no checkout |
| Países da opção | 174 países/regiões no print; Rafael ativou preços em massa. Não corresponde ao alvo desejado de somente Brasil. |

Depois do cadastro, o app exibiu **Comprar R$ 1,99** e abriu o checkout correto. O print mostra **Cartão de teste (aprovado sempre)** e **"Este é um pedido de teste, você não será cobrado"**, ainda antes do gesto final de compra. Rafael informou **compra concluída** e ajustes visuais no wallpaper, que serão tratados separadamente. Não atribuir ao print pré-compra uma confirmação final que ele não mostra.

**Conclusão delimitada:** a ausência do produto/oferta era bloqueio do piloto; cadastrar a arte fez o fluxo avançar sem alteração no código ou novo APK. Não comprova compra de todo catálogo, restauração/reinstalação, recusa, pendência ou revogação. Não comprova início do teste fechado nem aprovação dos documentos. Premium continua independente das artes.

## Inventário e padrão de IDs

`PRODUTOS.csv` é inventário técnico para conferência, **não arquivo de importação do Console nem confirmação de cadastro em massa**. Gerado de `ARTES-CONCILIADAS.csv`, mantém 361 produtos pagos; as cinco amostras não geram produtos. Premium é existente e não deve ser recriado.

- ID de produto: manter exatamente `terra_arte_<cenario>_<arte>`, pois o app consulta esse valor. Não renomear produtos existentes.
- ID de opção proposto: trocar `_` por `-` no ID do produto, preservando o padrão já usado no piloto. Não substitui o ID consultado pelo aplicativo. Identificadores existentes diferentes devem ser conciliados, não recriados automaticamente.
- Preço regular aprovado: R$ 1,99 por arte. Sem ofertas de desconto, aluguel, pré-venda ou compra em quantidade nesta preparação.
- Alvo de disponibilidade: BR. O piloto ficou ativo em 174 países; revisar sua disponibilidade e a das futuras opções. Não presumir que a faixa fechada restrita a Brasil corrige o alcance de todas as ofertas futuras.
- Oito artes de `estacionamento`/`natal` permanecem no inventário, identificadas como ocultas na vitrine do candidato 7. Preservar os IDs; ativação/visibilidade depende de decisão de catálogo, não de descarte silencioso.
- Títulos e descrição são gerados conforme o app e devem ser revisados antes da aplicação; fallbacks de slugs ficam marcados para revisão humana.

Comandos na raiz:

```text
python scripts/preparar_produtos_play.py
python scripts/preparar_produtos_play.py --check
```

## Cadastro restante e limite desta entrega

Rafael autorizou popular o Console com as artes e organizar os IDs. A tentativa de acesso ao Brave por Computer Use foi interrompida pela ferramenta, que não conseguiu determinar a URL atual com confiança suficiente. Nenhum produto adicional foi criado por essa tentativa. O cadastro em massa continua pendente; os 360 produtos restantes estão como não reconferidos no inventário, não como ativos ou definitivamente ausentes em uma leitura atual.

A documentação Google informa que importação/exportação de produtos por CSV no Console deixou de estar disponível; gestão em massa deve usar a API Publishing, ou gestão individual pelo Console. Não prometer upload direto deste CSV. API exige acesso autorizado já configurado; não criar credenciais ou expor chaves em Git/chat como atalho.

## Procedimento para continuar

1. Ler os produtos/opções atuais e comparar por ID com o inventário; preservar Premium e piloto e reportar divergências, sem apagar extras.
2. Conferir títulos, tipo de compra, preço BR e países. Preparar somente os faltantes; não sobrescrever preços/estados extras sem conciliação.
3. Após merge e autorização sobre o plano simulado, cadastrar pela API Publishing conforme o procedimento abaixo; registrar contagem efetiva após leitura de confirmação. A permissão de preencher o catálogo não autoriza publicar a versão do app, cobrar compras reais ou contratar serviço.
4. Reabrir o app e conferir preços em amostra de artes; testar compra/cancelamento/restauração com conta licenciada e instrumento de teste.
5. Corrigir diagnósticos de Billing se persistirem falhas em produtos ativos: código de resposta, mensagem e motivos dos produtos não retornados, sem dados pessoais/tokens.

Referências consultadas na sessão: [cadastro de produtos e gestão em massa](https://support.google.com/googleplay/android-developer/answer/1153481?hl=pt-BR), [consulta de produtos e ofertas](https://developer.android.com/google/play/billing/integrate), [testes de faturamento](https://developer.android.com/google/play/billing/test).

## Feedback para conversa oficial / Central de Execução

Compra piloto Biblioteca — Van Gogh destravada após cadastro do produto `terra_arte_biblioteca_vangogh` e opção ativa por R$ 1,99. Checkout com instrumento de teste e sem cobrança comprovado no print; conclusão da compra relatada por Rafael. Sem mudança de APK/código de Billing. Próxima pendência: conciliar e cadastrar as demais artes usando o inventário de 361 IDs, revisar os países do piloto e testar restauração. Ajustes de wallpaper ficam em tarefa separada. Cadastro em massa não executado nesta entrega por bloqueio técnico da navegação.

## Preparação pela API Publishing — decisão de 28/10

O cadastro restante cabe ao niemeyer pela API Publishing, após merge e autorização explícita sobre o plano simulado. Esta preparação não executou cadastro, alteração de países nem nova compra. Alvo: **somente Brasil**, R$ 1,99, inclusive no piloto; Premium e produtos extras ficam intocados. As oito artes ocultas de `estacionamento`/`natal` mantêm IDs e ficam indisponíveis. Não criar desconto, aluguel, pré-venda ou compra em quantidade.

O gerador acrescenta `titulo`, `descricao`, `estado_opcao`, `revisar_nomes` e `origem_textos` ao inventário. Nos produtos novos, usa as tabelas de `NomesCatalogo.nome` e `StoreTab.estiloNome`, inclusive fallback literal e sufixos; não inventa aliases nem converte números em romanos. Há **247 títulos marcados para revisão** (30 slugs de cenário, 50 de arte); lista completa no gerador. Título máximo observado: 37/55 caracteres. Descrição padrão dos novos: “Arte para o wallpaper Terra. Compra única. Não inclui o Premium.” (64/200); maior descrição preservada: 115/200. Limites oficiais em [OneTimeProductListing](https://developers.google.com/android-publisher/api-ref/rest/v3/monetization.onetimeproducts#OneTimeProductListing).

**Decisão do usuário em 29/10:** preservar exatamente os títulos e descrições das dez artes já cadastradas. O baseline aprovado está em `TEXTOS-EXISTENTES.json`, obtido por leitura da API; o CSV identifica esses registros por `origem_textos=console_preservado_2026-10-29`. A descrição padrão aplica-se às outras 351 linhas. Uma alteração posterior em qualquer texto/idioma, preço, estado, opção ou tipo continua sendo divergência e bloqueia toda aplicação; não há aceitação automática de texto novo. O título histórico do piloto continua **Biblioteca — Van Gogh**, preservado apesar do fallback **Biblioteca — Vangogh** do app. O app não foi alterado.

### Acesso e execução posterior

O usuário prepara uma conta de serviço no Google Cloud, ativa a Google Play Android Developer API e convida essa conta no Play Console **somente para `com.terra.wallpaper`**, com **Gerenciar presença na loja** (Manage store presence). Essa é a permissão que inclui preços e produtos no [quadro oficial de permissões](https://support.google.com/googleplay/android-developer/answer/9844686?hl=en). Não conceder administrador, publicação de versões ou pedidos por rotina. Ver [configuração oficial de acesso à API](https://developers.google.com/android-publisher/getting_started). Leitura autenticada realizada em 29/10: 11 produtos, dez artes ACTIVE + Premium. As dez têm preço BR de R$ 1,99; nove estão somente no Brasil. O piloto permanece em 174 países com novas regiões habilitadas. Restam 351 artes; nenhum produto foi criado ou alterado por esta preparação.

Guarde a chave e o ambiente virtual fora de qualquer checkout. O script recusa chave dentro desta árvore, inclusive por symlink; recebe o caminho por `--credentials` ou `TERRA_PLAY_SERVICE_ACCOUNT_KEY`. Não imprime conteúdo da chave, token, e-mail da conta ou resposta bruta de erro. Não enviar chave ou logs de autenticação pelo chat. Dependências são exclusivas do operador, não da CI Android:

```sh
python3 -m venv "$HOME/.venvs/terra-play"
"$HOME/.venvs/terra-play/bin/pip" install -r scripts/requirements-play.txt
python3 scripts/preparar_produtos_play.py --check
python3 -m unittest discover -s scripts -p 'test_cadastrar_produtos_play.py' -v
"$HOME/.venvs/terra-play/bin/python" scripts/cadastrar_produtos_play.py
```

1. Sem `--apply`, o comando lista produtos/opções atuais e plano por ID: criar, ajustar, sem mudança, divergências e extras intocados. Nenhuma escrita; nem conversão de preços é chamada neste modo. O plano mostra nomes pendentes de revisão.
2. Niemeyer revisa nomes, descrição, IDs, R$ 1,99, somente BR, opções indisponíveis das oito ocultas e o ajuste do piloto. Divergências de título, preço, estado, opção ou tipo bloqueiam toda aplicação; não são corrigidas automaticamente. O acesso real de leitura foi confirmado; conciliar com `TEXTOS-EXISTENTES.json` e reler o catálogo antes da operação, pois ele pode mudar.
3. Após merge e autorização explícita do usuário sobre esse plano, o operador repete o comando com `--apply`. O script relê antes de agir. Se o estado mudou, interrompe. Não existe autorização de aplicação nesta fatia.
4. Após aplicação, o comando relê o Console e relata ativos, DRAFT, INACTIVE, total indisponível e divergências separadamente. Não declara conclusão se restarem criações, ajustes ou divergências. Repetir a simulação deve deixar criar/ajustar/divergências vazios; os IDs sem mudança e extras continuam listados.
5. Registrar contagem real sanitizada e conferir preços no app em uma amostra. A7, A8, B2, restauração e checkout adicional seguem pendentes; simulações não comprovam cadastro ou entrega de conteúdo real.

### Contrato da API e limites

Fontes oficiais conferidas em 29/10/2026: [list](https://developers.google.com/android-publisher/api-ref/rest/v3/monetization.onetimeproducts/list) com paginação e opções embutidas, [get](https://developers.google.com/android-publisher/api-ref/rest/v3/monetization.onetimeproducts/get) para reconferência, [patch](https://developers.google.com/android-publisher/api-ref/rest/v3/monetization.onetimeproducts/patch) para criação/ajuste, [batchUpdateStates](https://developers.google.com/android-publisher/api-ref/rest/v3/monetization.onetimeproducts.purchaseOptions/batchUpdateStates) para ativar somente novas opções visíveis e [convertRegionPrices](https://developers.google.com/android-publisher/api-ref/rest/v3/monetization/convertRegionPrices) para obter a versão atual das regiões. Da conversão só se usa `regionVersion.version`; nenhum preço estrangeiro convertido é aplicado.

A criação envia uma única configuração regional, BR/BRL 1,99, `buyOption`, opção compatível e sem `newRegionsConfig`. O estado é output-only: não se envia `ACTIVE` no upsert. A API cria a opção em `DRAFT`; as visíveis são ativadas em chamada separada. As oito ocultas permanecem DRAFT, que não é retornado pela Billing Library, conforme [estados das opções](https://support.google.com/googleplay/android-developer/answer/16430488?hl=en); não se faz ativação intermediária. DRAFT atende ao objetivo de indisponibilidade aprovado e é contado separadamente de INACTIVE literal. No cenário simulado completo: 353 ACTIVE, 8 DRAFT e 0 INACTIVE; não são contagens reais do Console. Não se ativa uma opção preexistente divergente. Se houver falha entre criação e ativação, o DRAFT visível é divergência para conciliação humana; o script não o ativa silenciosamente na próxima execução.

Para impedir sobrescrita numa criação concorrente, `allowMissing=true` usa máscara apenas do `productId` imutável (a máscara é ignorada quando o produto é criado). Em existente, preço/texto/opções ficam fora da máscara. O ajuste do piloto relê e preserva a opção e todos os preços, altera apenas `availability` fora do Brasil e nas regiões novas para `NO_LONGER_AVAILABLE` quando antes estavam `AVAILABLE`. A máscara é `purchaseOptions`, pois [FieldMask](https://protobuf.dev/reference/protobuf/google.protobuf/#field-mask) não atravessa campos repetidos. O estado é omitido e o título fica fora da máscara. A API não documenta precondição por ETag nesse método: o operador deve impedir edições concorrentes no Console durante a aplicação. Não há repetição automática de escrita após erro ou timeout; simule de novo e concilie.
