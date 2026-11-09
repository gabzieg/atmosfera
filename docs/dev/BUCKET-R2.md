# Cloudflare R2 — acervo Terra

## Estado e limites

Conferência de fonte em09/11/2026, base `eb245b4`; nenhuma leitura remota ou upload nesta entrega. O índice **embarcado** em `android-app/app/src/main/assets/atmosfera/catalogo/manifest.json` contém71cenas/366artes (contagem por Python dos mapas `cenas`/`artes`); isso não mede o conteúdo atual do bucket. O PR43 registrou em04/10/2026 um envio de74cenas/389artes,1.242arquivos/440.656.483bytes: registro histórico, não inventário atual verificado. Cortes históricos: simpsons/budokai/konoha/burj; não substituem licenças.

Bucket de testes registrado: `terra-acervo`, classe Standard/região Eastern North America no registro de outubro; estado da conta não reconferido. URL pública de testes registrada: `https://pub-722473b758b144279fc34ecb931a6045.r2.dev/`, índice `manifest.json`. Não é endpoint de candidato nem produção validada. Não enviar identificação pessoal em headers para contornar bloqueios: o registro histórico de403/1010 por cliente não prova disponibilidade Android atual.

## Preparar e publicar (operação separada)

Entrada canônica/validação: [README da ferramenta](../../tools/r2/README.md). Gerar dist com o empacotador na origem do acervo, com manifesto e arquivos físicos; ferramentas externas históricas `tools/pacote_cenas.py`/`tools/subir_acervo.py` não são scripts deste checkout. A ferramenta versionada aqui é `tools/r2/publicar_terra_r2.ps1` + Python; não misturar procedimentos nem presumir aprovação para executá-la.

Layout de origem: `manifest.json`, `pack/<cena>/_cena.zip`, `pack/<cena>/<arte>.zip`, `thumb/<cena>__<arte>.webp`, `preview/<cena>__<arte>.webp`; nomes já versionados são aceitos se coerentes. Saída tem hash completo no nome e no campo. Objetos primeiro/manifesto por último evitam índice apontando para objeto ainda não enviado; sem limpeza remota. Comparação tamanho/MD5/ETag poupa reenvio, mas não mede disponibilidade pública nem consumo real de operações.

## Configuração de conta e acesso

Criação/alteração de conta, cartão, bucket, token, domínio e acesso são ações do titular com checkpoint próprio. O roteiro histórico era criar R2/bucket, habilitar acesso público de testes e criar token Object Read & Write limitado ao bucket, guardado fora do repositório. Não criar outra conta/bucket por seguir um texto antigo. Nome antigo `atmosfera-acervo` e arquivo `.atmosfera-r2.env` pertencem ao roteiro anterior; a ferramenta atual documenta DPAPI/variáveis no README. Não confirma validade, prazo ou existência de token atual.

Arquivos públicos podem ser copiados por quem tem a URL. Compra é validada no app/Play e não pelo sigilo do objeto. `r2.dev` é para desenvolvimento; entrega de produção tem configuração/validação próprias. Preços, franquias e possíveis cobranças devem ser conferidos nas [fontes oficiais de preços](https://developers.cloudflare.com/r2/pricing/) e [acesso público](https://developers.cloudflare.com/r2/buckets/public-buckets/) antes de operar; volume histórico não garante custo zero.

## Ligar ao app e conferir

`Acervo.BASE_PADRAO` deriva de `BuildConfig.ACERVO_URL`, configurada no Gradle do app. O índice embarcado válido tem precedência em debug/release; release sem ele retorna vazio, não busca outro índice remoto. Debug permite URL alternativa e fallback de índice, sob as condições do consumidor. Este PR não muda URL, índice ou Android. O importador existente `scripts/sincronizar_acervo_r2.py` gera índice/miniaturas com sete cortes e seleção nativa/grátis; não é uploader, e executá-lo altera assets/pode baixar dados. `scripts/verificar_pacotes_r2.py` é um ensaio opt-in de downloads reais. Nenhum deles foi importado/executado aqui; veja [adaptação R2/R8](ADAPTACAO-R2-R8-2026-10-11.md) como registro histórico.

Caches: arte versionada usa `public, max-age=31536000, immutable`; índice `public, max-age=300`; Content-Type ZIP/WebP/JSON correto. Em operação autorizada futura: conferir objeto e metadados no destino, GET público sem credenciais/dados pessoais, depois fluxo real de compra/download/cancelamento/integridade/aplicação no Android. Resultado de upload ou dry-run não substitui esses controles. Instalação/build final/Console seguem checkpoints; não executados aqui.

## Consumo e mudanças de terceiros

Não há alerta automático demonstrado por este PR. O antigo `atmosfera 2.0/tools/quota_r2.py`, externo a esta árvore, descrevia armazenamento por inventário e operações por analytics (token específico Account Analytics: Read); sem essa permissão, operações ficavam sem medição. Não afirmar que roda ou que protege a conta hoje. Não atribuir tarefa ao Rafael.

Preserva-se como referência histórica a régua de03/09: marcos25/50/75%;80–95% a cada5%;97–100% a cada0,5%; anunciar salto entre marcos e rearmar se consumo cair/voltar. Comparar chave/tamanho/ETag/data entre inventários distinguia objeto criado/removido/sobrescrito de upload local pendente. Nada disso foi implementado ou ativado pela ferramenta deste PR; política/monitoramento vigente requerem conferência própria. Uso não se confunde com RAM do telefone e franquia não é bloqueio de cobrança.

Fila operacional única: [TASKS](TASKS.md). [Entrega de arte](ENTREGA-DE-ARTE.md) preserva desenho histórico, não inventário/estado de lançamento atual.
