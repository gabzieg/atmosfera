# Atualização do acervo no R2

Python 3.9+; para upload, instalar boto3 no ambiente Python escolhido. A validação offline usa apenas a biblioteca padrão.

```powershell
python -m pip install boto3
./tools/r2/publicar_terra_r2.ps1 -Dist 'CAMINHO_DO_ACERVO/dist' -Output 'CAMINHO_DE_SAIDA' -DryRun
./tools/r2/publicar_terra_r2.ps1 -Dist 'CAMINHO_DO_ACERVO/dist' -Output 'CAMINHO_DE_SAIDA'
```

`-Python` aceita o caminho de outro interpretador. O acervo original está fora deste repositório; a saída também deve ficar fora dele. O manifesto de entrada precisa apontar para os arquivos originais existentes, antes da versão no nome.

O wrapper lê `%LOCALAPPDATA%/Terra/r2-upload.dpapi`, criado previamente no computador do titular. É um JSON protegido por DPAPI do Windows, com R2_ACCOUNT_ID, R2_BUCKET, R2_ACCESS_KEY_ID e R2_SECRET_ACCESS_KEY. Só o usuário/computador que o protegeu consegue abrir essa cópia; não enviar credenciais por Git. Em outro ambiente, configurar acesso restrito equivalente. O script Python também pode ser executado diretamente com essas variáveis de ambiente. Não são credenciais para incluir no Android.

Publicação: valida tamanho/SHA-256 de todos os arquivos, exclui simpsons/budokai/konoha/burj, cria nomes com o hash completo, compara os objetos remotos e envia somente diferenças. Confere tamanho/ETag/cache antes de atualizar manifest.json por último. Nunca exclui objetos remotos. A correspondência MD5/ETag pressupõe uploads simples pelo script, sem multipart.

Arte alterada gera outra URL; adicionar arte gera novos arquivos. Uma variante que reutilize exatamente os mesmos arquivos pode reutilizar suas referências. Isso não configura a loja, os direitos de compra nem o mapeamento do motor no app. Objetos antigos permanecem no bucket e continuam ocupando armazenamento, inclusive após atualização.

`--dry-run` não lê credenciais, não acessa o R2 e não envia nada. Gera o manifesto e imprime contagens. Não valida a compatibilidade do app Android. O relatório de upload também não comprova acesso público: esse acesso exige verificação separada. Os scripts não criam alertas de consumo.
