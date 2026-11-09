# Como as mudanças chegam ao aplicativo

Conferência de fonte em09/11/2026, base `eb245b4`. O roteiro original de04/10 fica histórico nos commits do PR43; este texto separa procedimentos válidos do estado atual. A fila é [TASKS](TASKS.md).

## Três entregas distintas

1. **R2:** arquivos do acervo; o envio histórico de04/10 não comprova o bucket atual. Ver [BUCKET-R2](BUCKET-R2.md) e [entrada da ferramenta](../../tools/r2/README.md). Upload não atualiza sozinho índice embarcado, mapeamento, compra ou produtos Play.
2. **GitHub:** código, fontes e ferramentas entram por PR/revisão/checks. Nem merge nem publicação de HTML demonstram site público, instalação ou aprovação comercial. Esta entrega não executa uploader/CLI.
3. **Play Console:** recebe AAB assinado em tarefa própria. App instalado não muda com um PR ou imagem enviada ao R2. Destino/faixa/convite/envio dependem de escopo vigente, sem assumir teste interno/produção pelo roteiro antigo.

## Antes do build/envio autorizado

- `com.terra.wallpaper`; fonte atual1.0.5/código7 no `android-app/app/build.gradle`. Código8 é planejado, não confirmado: conferir usados/disponíveis e certificados no outro computador antes de gerar candidato. A versão1.0.4/código5 era a conferência de outubro.
- Premium continua `terra_premium`; catálogo/produtos/mapeamento têm validação própria. Não empacotar automaticamente cópia antiga com `com.atmosfera.wallpaper`.
- Motor já tem download; conferir no alvo compra/download/cancelamento/integridade/aplicação. `Acervo.BASE_PADRAO` vem do BuildConfig, e release usa índice embarcado; não é mais uma constante vazia a preencher. Fonte embarcada71cenas/366artes não mede bucket ou disponibilidade.
- Política1.22/Termos1.12/Contato1.10 têm redação aceita, metadados ainda de rascunho. Fontes canônicas em `docs/legal/`, publicação/vigência dependem de decisão. Não voltar à política1.3 ou ao público13+ pelo guia antigo. R02/A4/R03 e teste adulto permanecem conforme decisões vigentes; plano futuro13+ não está liberado.
- Assinatura: usar chave/configuração privada existente na máquina autorizada; não criar identidade nova por ausência local. Localizar certificados/códigos é etapa própria, não convite para buscar credenciais nesta árvore ou pedir segredo no chat.
- Gate do projeto no commit final e ensaio real pertinente precedem candidato; `bundleRelease`/instalação/envio não foram executados nesta tarefa. APKs diagnósticos não são AAB final nem prova de instalação.

## Publicação e testes

No Console autorizado, conferir faixa e rótulos reais antes de enviar AAB/notas/revisar avisos/publicar. Teste interno, fechado e produção são marcos distintos; requisitos/exigências vigentes ficam no plano/Console, não se deduzem dos números de outubro. Nenhuma autorização de produção é criada por este documento.

Endereço público de testes e limitações em BUCKET-R2. Para entrega de produção, definir domínio/forma de entrega e validar acesso sob escopo próprio. Arquivos públicos podem ser copiados fora do app, mesmo com compra controlada pela Play.
