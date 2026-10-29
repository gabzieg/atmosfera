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
- Nomes/descrições comerciais devem ser conferidos com o catálogo antes da publicação; o CSV não inventa títulos a partir de nomes técnicos.

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
3. Cadastrar/ativar produtos e opções pelo acesso autorizado disponível; registrar contagem efetiva após leitura de confirmação. A permissão de preencher o catálogo não autoriza publicar a versão do app, cobrar compras reais ou contratar serviço.
4. Reabrir o app e conferir preços em amostra de artes; testar compra/cancelamento/restauração com conta licenciada e instrumento de teste.
5. Corrigir diagnósticos de Billing se persistirem falhas em produtos ativos: código de resposta, mensagem e motivos dos produtos não retornados, sem dados pessoais/tokens.

Referências consultadas na sessão: [cadastro de produtos e gestão em massa](https://support.google.com/googleplay/android-developer/answer/1153481?hl=pt-BR), [consulta de produtos e ofertas](https://developer.android.com/google/play/billing/integrate), [testes de faturamento](https://developer.android.com/google/play/billing/test).

## Feedback para conversa oficial / Central de Execução

Compra piloto Biblioteca — Van Gogh destravada após cadastro do produto `terra_arte_biblioteca_vangogh` e opção ativa por R$ 1,99. Checkout com instrumento de teste e sem cobrança comprovado no print; conclusão da compra relatada por Rafael. Sem mudança de APK/código de Billing. Próxima pendência: conciliar e cadastrar as demais artes usando o inventário de 361 IDs, revisar os países do piloto e testar restauração. Ajustes de wallpaper ficam em tarefa separada. Cadastro em massa não executado nesta entrega por bloqueio técnico da navegação.
