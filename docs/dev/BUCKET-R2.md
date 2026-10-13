# Cloudflare R2 — Terra

Estado confirmado em 04/10/2026.

- Bucket: terra-acervo, classe Standard, região Eastern North America.
- Catálogo enviado: 74 cenas, 389 artes, 1242 arquivos referenciados, 440.656.483 bytes (~440,7 MB decimais).
- Cortes aplicados: simpsons, budokai, konoha e burj. Isso não substitui a validação das licenças das artes restantes.
- Acesso público ativado pelo titular para testes: https://pub-722473b758b144279fc34ecb931a6045.r2.dev/
- Manifesto: https://pub-722473b758b144279fc34ecb931a6045.r2.dev/manifest.json
- Upload reutilizável autorizado: token terra-acervo-upload, leitura/escrita apenas neste bucket, sem expiração, protegido pelo Windows fora do repositório. Nunca incluir o token no app.
- O titular acompanhará o consumo. Alertas automáticos ainda não ativados; destino de e-mail pendente.

## Atualizações

A ferramenta versionada está em tools/r2/. Envia apenas arquivos diferentes, valida integridade e publica o manifesto por último. Não apaga objetos antigos. A saída local e o acervo original não fazem parte desta atualização do GitHub. Consulte o README da ferramenta antes de executar.

Alterar uma imagem exige enviar o arquivo alterado; um arquivo novo recebe URL nova com hash. Reaproveitar exatamente o mesmo arquivo não exige nova cópia. Mapeamento, produtos e catálogo do aplicativo precisam de integração própria. Uma alteração no bucket não cria um produto no Google Play.

## Limites e verificação

r2.dev é destinado a desenvolvimento, sujeito a limites de requisições próprios, independentemente do tamanho pequeno do acervo. Não é uma URL de produção validada. A franquia de armazenamento não equivale à memória RAM do celular; também existem métricas de operações de leitura e escrita. Conferir os preços/franquias atuais para a classe usada antes de assumir ausência de cobrança.

A verificação pública baixou o manifesto e amostras com tamanho/hash conferidos usando identificação Terra-Acervo-Verification/1.0 (suporteterrabr@gmail.com). O cliente Python padrão recebeu 403 (Cloudflare 1010). Esse resultado depende da requisição; ainda falta testar o cliente real do Android. Não prometer que o acesso público está protegido contra cópia: qualquer pessoa com a URL de um objeto pode baixar o arquivo.

O relatório de upload não comprova disponibilidade pública nem liberação de compras. As pendências de integração estão em CAMINHO_GITHUB_PLAY_TERRA.md e no acompanhamento local.

Documentação oficial: https://developers.cloudflare.com/r2/buckets/public-buckets/ e https://developers.cloudflare.com/r2/pricing/.
