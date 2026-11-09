# Preparação e publicação do acervo R2

Python 3.9+. A entrada canônica é uma pasta `dist/` produzida pelo empacotador, contendo `manifest.json` e **todos os arquivos físicos referenciados**. O JSON sozinho (inclusive o índice embarcado no app) não substitui esse conjunto. Campos: `cenas[id].cena` e `cenas[id].artes[id].{pack,thumb,preview}`, cada referência com `arquivo`, `bytes` inteiro positivo e `sha256`. Não usar esta ferramenta para gerar o acervo a partir do APK.

`catalog.py:prepare_catalog(manifest, read_bytes)` separa a preparação sem efeitos externos: recebe JSON e leitor injetado, devolve cópia normalizada e mapa de chaves para origens. Os testes usam somente bytes sintéticos; não importam o CLI, boto3, credenciais ou rede:

```sh
python3 -B -m unittest discover -s tools/r2 -p 'test_*.py' -v
```

## Contrato com Android

Referência: `android-app/app/src/main/java/com/terra/wallpaper/engine/Acervo.kt`, verificações de pacotes/prévias. Packs: `pack/<cena>/<arte>[.<sha256>].zip` (inclui `_cena`), até128MiB; imagens: `preview/<cena>__<arte>[.<sha256>].webp` e `thumb/...`, até4MiB nesta ferramenta. Tamanho deve coincidir com os bytes; hash não pode ser vazio e deve coincidir com o conteúdo. O input legado pode abreviar um hash hexadecimal não vazio: a saída sempre contém o SHA-256 **completo**, em minúsculas, no campo e no nome. Arquivo já versionado com o mesmo hash mantém o nome; hash divergente no nome é recusado. Caminhos fora desse layout são recusados, e o leitor do CLI também impede escapar da pasta de origem por symlink.

A preparação preserva metadados e os quatro cortes históricos simpsons/budokai/konoha/burj; não é filtro atual de catálogo ou aprovação de licenças. O importador `scripts/sincronizar_acervo_r2.py` tem sete exclusões (inclui timessquare/ogro/eiffel), além da seleção nativa/grátis: preservar isso é tarefa de integração própria, não republicar cenas por inferência. Não inventa assinatura digital, não atualiza o índice embarcado e não libera compra. Release usa o manifesto embarcado quando válido; na ausência dele, retorna catálogo vazio. O fallback remoto do índice só existe em debug. Portanto publicar `manifest.json` no bucket não muda o catálogo de um release existente.

## Operação futura — exige escopo e autorização próprios

Os comandos abaixo são referência, **não foram executados na #103**, nem mesmo dry-run. Para uso real, conferir dist/destino, autorização, integridade, preços/cotas e acesso com o titular. boto3 é necessário apenas para upload.

```powershell
./tools/r2/publicar_terra_r2.ps1 -Dist 'CAMINHO_DO_ACERVO/dist' -Output 'CAMINHO_DE_SAIDA' -DryRun
./tools/r2/publicar_terra_r2.ps1 -Dist 'CAMINHO_DO_ACERVO/dist' -Output 'CAMINHO_DE_SAIDA'
```

`-Python` escolhe outro interpretador. Original e saída ficam fora da árvore versionada (ou em `local/` ignorado). `--dry-run` gera manifesto/contagens localmente, sem credenciais/rede; isso não prova o cliente Android nem disponibilidade pública. Não reapontar o app por causa de uma preparação.

O wrapper Windows lê `%LOCALAPPDATA%/Terra/r2-upload.dpapi`, JSON protegido por DPAPI do usuário/computador original. Campos: R2_ACCOUNT_ID, R2_BUCKET, R2_ACCESS_KEY_ID, R2_SECRET_ACCESS_KEY. Não ler/criar/transportar esse arquivo nesta entrega; jamais incluir credenciais no app, Git ou chat. Python recebe esses campos por ambiente, sem passá-los na linha de comando. O wrapper restaura o ambiente ao terminar. Não foi ensaiado em Windows nesta tarefa.

Upload preservado: objetos versionados primeiro, `manifest.json` por último; só diferenças por tamanho/MD5/ETag, conferência de tamanho/ETag/cache; sem excluir objetos. Arte usa `public, max-age=31536000, immutable`; índice usa `public, max-age=300`. Content-Type é ZIP/WebP/JSON. MD5/ETag pressupõe upload simples, não multipart. Relatório de upload não prova acesso público. `public_access` permanece não conferido nesta execução; qualquer status/URL/verificação anterior fica apenas em `previous_public_evidence`, junto do bucket e dos campos de contexto existentes no registro anterior. Contexto ausente não é preenchido com o destino atual; nem mesmo bucket igual torna a prova histórica atual. Os objetos antigos continuam ocupando armazenamento; a ferramenta não cria alertas de consumo nem apaga dados.
