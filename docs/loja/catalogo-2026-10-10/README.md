# Inventário e oferta por arte — etapa 3, 10/10/2026

**Estado:** inventário local preparado e decisões comerciais parcialmente consolidadas. Nenhuma arte foi aprovada visualmente por esta execução; nenhum produto foi cadastrado, preço alterado ou arquivo publicado no bucket.

## Oferta confirmada nesta conversa

- Objetivo de lançamento: **todo o acervo aprovado**, conforme reconfirmado por Gabriel em 10/10. Um lote de teste serve para validar o mecanismo e não muda automaticamente esse objetivo.
- Premium: compra única de R$ 49,90, pelos oito efeitos vivos e todos os estilos de efeito da oferta aplicável. Não compra arte.
- Arte individual: preço regular registrado de R$ 1,99; libera uma combinação cenário/variante específica.
- **Cada arte/variante nova será uma compra separada**, mesmo que pertença a um cenário já adquirido. Confirmado por Gabriel em 10/10.
- **Sem packs comerciais no lançamento.** Confirmado por Gabriel em 10/10.
- Futuros efeitos/estilos no Premium: **a definir com Rafael** antes de prometer inclusão. A indefinição não autoriza retirar direitos da oferta já vendida.
- Promoções de artes por R$ 0,99: intenção registrada pelo titular, mas seleção inicial e operação ainda pendentes. O CSV não atribui promoção a um item sem decisão.
- Cinco amostras gratuitas: Cabana/pixel, Bruxa/clay, Lavanda/aqua, Esfinge/vangogh e Jardim/ukiyoe.

## Resultado da conferência local

| Evidência | Contagem |
|---|---:|
| Cenas com fundo nos assets Android | 5 |
| Artes com fundo nos assets Android | 9 |
| Cenas/artes com fundo em `content-packs/cenarios-futuros` | 69 / 69 |
| Cenas/artes físicas antes dos cortes desta conferência | 74 / 78 |
| Cenas/artes físicas após os sete temas excluídos | **71 / 75** |
| Amostras gratuitas com fundo | 5 |
| Propostas de produtos pagos com fundo local | **70** |
| Variantes únicas declaradas no motor após as exclusões | **312** |
| Variantes declaradas que não têm fundo nesta cópia | **237** |

O questionário do Rafael menciona **71 cenas/366 artes** em outro inventário. A coincidência de 71 cenas não comprova a composição equivalente. A diferença de 54 entre as 366 artes relatadas e as 312 variantes declaradas aqui precisa ser conciliada por identificador, não preenchida por suposição.

Há uma declaração duplicada de `vale/cartoon` no motor. O inventário registra esse aviso e conta a variante uma vez; não foi alterada a configuração do motor nesta etapa.

## Arquivos

- [ARTES.csv](ARTES.csv): uma linha por variante declarada ou com fundo local, incluindo itens excluídos e arquivos ausentes para rastreabilidade.
- [ARTES.json](ARTES.json): os mesmos itens, com caminhos, bytes e SHA-256 dos arquivos locais de arte e dos arquivos comuns de cada cena.
- [RESUMO.json](RESUMO.json): contagens, exclusões e avisos para reconferência.

Os campos `product_id_proposto` são sugestões, não IDs verificados/cadastrados na Play. Só recebem proposta as artes pagas com fundo local e não excluídas. Os itens sem fundo continuam registrados, sem autorização para venda. Nenhum item tem `teste_android=aprovado`; arquivo existente não comprova renderização.

Tamanhos são **bytes dos arquivos de origem**, sem compressão de pacote, e não o tamanho de download da Play/R2. Miniaturas dedicadas estão marcadas como pendentes quando não existe `thumb.png`/`thumb.webp`; a prévia atual que reduz o fundo não equivale a uma miniatura embutida própria.

`arquivos_presentes_nao_testada` indica que foram encontrados fundo, frente, zonas e declaração da variante. Não verifica recorte, correspondência de máscaras, comportamento de luzes, originalidade, compra ou funcionamento no motor. Profundidade, marcações JSON e camadas opcionais precisam de curadoria conforme a cena. Arquivos comuns são repetidos na lista de cada arte para mostrar dependências; não somar essas repetições como tamanho único do acervo.

## Exclusões

Não ofertar `simpsons`, `budokai`, `konoha`, `timessquare`, `burj`, `ogro` e `eiffel`. Nesta cópia, os quatro primeiros cortes históricos (`simpsons`, `budokai`, `konoha`, `burj`) não têm pastas no acervo local. `timessquare`, `ogro` e `eiffel` têm uma arte pixel cada em `content-packs`; estão marcadas como **excluídas**, sem produto proposto. Os arquivos foram preservados para rastreabilidade, não removidos nem publicados.

Os sprites `pixel_retro` e `pixel_retro_2` não foram encontrados na raiz dos assets compartilhados. Esse exame da fonte não substitui conferência do conteúdo de um AAB final.

O inventário contém linhas excluídas intencionalmente. Ele **não é um manifesto de produção nem um CSV oficial de importação da Play**. A integração futura deverá usar uma lista explícita de itens autorizados; não importar todas as linhas indiscriminadamente.

## Como regenerar

Na raiz do projeto:

```powershell
python scripts/inventariar_artes.py --data 2026-10-10 --saida docs/loja/catalogo-2026-10-10
```

O script usa apenas bibliotecas padrão do Python e examina dois diretórios: assets de cenas do Android e `content-packs/cenarios-futuros`, além das declarações em `engine/Cenario.kt`. Não lê credenciais, não consulta bucket/Console, não altera arquivos de arte e não modifica o aplicativo. Não inclui imagens de arquivos de backup ou outputs de outros workspaces.

O parser reconhece o formato atual das declarações Kotlin; se esse formato mudar, deve ser revisado. Arte declarada e arte presente são estados diferentes. O resumo inclui o hash do arquivo do motor para identificar a origem da declaração.

## O que falta para fechar a etapa 3

- [ ] Recuperar o manifesto/inventário que sustenta as 366 artes e conciliar seus identificadores com este relatório. Recuperar as variantes ausentes a partir de fonte autorizada, sem publicar material ainda não conferido.
- [ ] Confirmar quais itens realmente existem no bucket e o lote aprovado no Android; Rafael fará a avaliação.
- [ ] Resolver a declaração duplicada de `vale/cartoon` com conferência da configuração desejada.
- [ ] Gerar/conferir miniaturas pequenas dedicadas, camadas e marcações por arte.
- [ ] Confirmar a inclusão ou não de futuros efeitos/estilos no Premium.
- [ ] Fixar seleção inicial e operação das ofertas promocionais, além de países/preços realmente configurados no Console.
- [ ] Conferir os IDs propostos antes do cadastro e implementar sua associação a direitos individuais na etapa 4.
- [ ] Conferir origem/licenças das artes e componentes distribuídos; preservar os registros disponíveis. Declaração de geração com GPT não equivale a auditoria independente.

O [checklist de finalização](../../dev/CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md) continua sendo a referência da sequência. A avaliação das etapas 1/2 permanece com Rafael e não foi marcada como concluída.
