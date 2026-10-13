# Avaliação v2 do Rafael — situação conferida em 13/10/2026

Mensagem recebida pelo usuário; não representa integração ou aceite em aparelho. Nesta conferência, `git fetch`, lista de branches remotas e PRs abertos não revelaram o código da v2 no repositório configurado `gabzieg/atmosfera`. Nenhum patch correspondente foi encontrado nesta máquina.

## Evidência acessível

Estado examinado antes desta atualizacao documental; ela nao incorpora codigo da v2.

| Item | Resultado conferido |
|---|---|
| PR #50 | Aberta, em rascunho, cabeça `1998ae6`; sem revisão registrada |
| Checks do GitHub dessa revisão | Build e secret-scan aprovados |
| Catálogo nos assets versionados | 71 cenas / 366 artes |
| Branch `motor/cenas-efeitos-marcacao` | Continua em `6cf8023`, entrega sanitizada de R8 |
| `main` | Continua em `e7129f6`; PR #50 não mesclada |
| Novidades encontradas no fetch | Branches Dependabot; não foram integradas |

Um commit local ou publicado em outro remoto pode existir. Para localizá-lo, falta hash e branch/URL, ou publicação dos patches no repositório compartilhado. Não foi aplicado código inferido a partir da mensagem.

## Trabalho relatado por Rafael, ainda não conferido aqui

- Prévias intermediárias e cache temporário.
- Preservação da busca/rolagem e atualização dos cadeados.
- Controles independentes para testar Premium e artes.
- Nomes/agrupamentos ajustados; retirada temporária de Estacionamento e Feira de Natal.
- Resultado local: **69 cenas / 358 artes / 67 cards**, 98 testes unitários e lint sem erros.
- 34 cenas disponíveis com mapeamento automático dos efeitos, ainda sem conferência visual. Esse número não demonstra aprovação das demais cenas ou correto funcionamento do mapeamento.
- Brilho, vapor e sprites registrados para trabalho posterior.

No catálogo atual, `estacionamento` tem quatro artes. Há IDs distintos de Natal (`natal`, `natalchale`, `natalsala`); o patch deve identificar qual corresponde à retirada da Feira de Natal. Cards agrupados são uma organização visual, não necessariamente cenários ou direitos de compra. Manter SKU e posse por variante, respeitando a oferta de cada arte.

## Ordem da integração

1. Rafael publicar uma branch de revisão baseada na cabeça da PR #50, ou disponibilizar os patches e indicar o commit-base. Não fazer force-push para substituir a revisão compartilhada.
2. Conferir diff e origem de alterações: prévias/cache, navegação, cadeados, controles debug, nomes/agrupamentos, exclusões e configurações do motor. As prévias intermediárias implicam nova requisição de rede antes do HD; avaliar dados enviados, hash, tamanho, retenção/limpeza e textos legais. Conferir que os controles de teste não concedam direitos em release.
3. Reconciliar os IDs retirados e as 358 artes com manifesto, miniaturas, propostas de produtos e agrupamento de cards. Retirada da vitrine não deve revogar automaticamente direitos de uma compra válida; definir continuidade conforme a oferta.
4. Executar novamente os checks na revisão integrada. Os 98 testes relatados são do ambiente do Rafael, não resultado da PR atual.
5. Atualizar checklist, SPEC e documentos com o comportamento final. A privacidade atual descreve miniaturas locais e HD solicitado; a descrição da prévia/cache precisa acompanhar a implementação real, sem adivinhar o que foi enviado/guardado.
6. Rafael avaliar no aparelho e pela Play: visual, efeitos, compra/download/restauração, offline, cancelamento/falha e atualização. As 34 cenas com mapeamento automático precisam de evidências específicas.
7. Depois de revisão/aceite, código de versão e configuração definidos, gerar um único novo candidato AAB da revisão integrada. Nenhum novo AAB ou publicação foi realizado nesta conferência.

## Texto para Gabriel encaminhar ao Rafael

> Rafael, a PR #50 ainda está no commit 1998ae6 e a branch do motor em 6cf8023. Não encontrei sua v2 nas branches do repositório compartilhado. Publique os patches/ajustes em uma branch baseada na PR #50 e informe hash e branch/URL, ou indique onde já estão publicados e o commit-base. Precisamos conferir as prévias/cache, os controles debug, o agrupamento dos 67 cards e os IDs exatos de Estacionamento/Feira de Natal antes de atualizar o catálogo para 69 cenas/358 artes. Depois executaremos os checks da revisão integrada; sua avaliação no aparelho e o ciclo de compras pela Play continuam pendentes. Não inclua assinatura, senhas, comprovantes ou dados de testadores.

Este texto não foi enviado a terceiros por ferramentas. As etapas 1–5 permanecem com os aceites externos já registrados; a etapa 6 ainda não tem candidato validado.
