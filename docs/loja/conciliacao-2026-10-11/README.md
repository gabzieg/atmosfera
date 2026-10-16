# Conciliação do acervo R2 — 11/10/2026

Rafael informou que `terra-acervo` já existe e recebeu o acervo. Consulta pública de `manifest.json` respondeu HTTP 200. Apenas o JSON foi baixado; não foram baixados ZIPs ou imagens HD e nenhum arquivo foi alterado no bucket.

HEAD dos objetos comuns, pacote pixel e miniatura da Cabana respondeu HTTP 200, com 138.078, 486.680 e 24.448 bytes, respectivamente, iguais ao manifesto. Isso confirma três objetos de exemplo, não a disponibilidade ou integridade de todos os objetos.

URL de testes: https://pub-722473b758b144279fc34ecb931a6045.r2.dev/

| Conferência | Resultado |
|---|---:|
| Manifesto público bruto | 74 cenas / 389 artes |
| Após as sete famílias excluídas | **71 cenas / 366 artes** |
| Artes que também têm HD nesta máquina | 75 |
| Artes adicionais encontradas no remoto | 291 |
| Artes remotas ausentes das declarações locais do motor | 54 |
| Amostras gratuitas preservadas | 5 |
| Produtos pagos propostos para o conjunto completo | 361 |

As exclusões presentes no manifesto bruto são `eiffel` (15), `ogro` (4) e `timessquare` (4). As outras quatro famílias excluídas já estão ausentes. Todas as 75 artes do inventário local estão no remoto; todas as 312 variantes aprováveis declaradas localmente também estão no remoto. Portanto, 75 era a disponibilidade local de arquivos, e não a extensão do acervo existente.

## Incompatibilidades com a PR #50

- Bucket: manifesto v1, imagens WebP, nomes como `pack/cabana/pixel.<sha256-completo>.zip`. Preparação local: v2, PNGs originais e `pack/cabana/pixel.zip`.
- O campo `sha256` remoto tem 16 caracteres. Os 1.169 descritores aprováveis (71 comuns, 366 pacotes, 366 miniaturas, 366 prévias) têm hash de 64 caracteres no nome, consistente com o prefixo do manifesto. Essa consistência **não verifica o conteúdo do arquivo**; o hash completo ainda precisa ser validado durante o download.
- A validação de caminhos da PR #50 rejeita os nomes de ZIP com hash. O manifesto embarcado fixa os hashes dos PNGs locais, que não correspondem automaticamente aos WebPs publicados. Apenas preencher a URL não resolve a entrega.
- Cinco cenas nativas só declaram as nove artes atuais. Para as variantes remotas dessas mesmas cenas, é necessário mesclar o catálogo e marcar apenas as variantes adicionais como remotas, sem duplicar a cena nem remover as amostras.
- Miniaturas de 291 artes ainda precisam ser incorporadas ao catálogo do app. Os 54 IDs de variantes adicionais precisam ser reconciliados com rótulos/configuração do motor; não assumir que estejam todos renderizando corretamente.

## Próxima implementação

Reaproveitar o acervo já hospedado: normalizar seu manifesto para hashes completos e caminhos existentes, embarcar a revisão conferida, mesclar as 366 variantes com as cinco cenas nativas e preparar as miniaturas leves. Adaptar a validação de caminhos preservando proteção contra extração indevida. Manter o conteúdo remoto existente; os ZIPs PNG locais continuam como alternativa de preparação, não como arquivos a sobrescrever no bucket.

Depois, testar um lote representativo pela Play antes de anunciar todo o catálogo. A aprovação Android e o cadastro dos 361 produtos pagos continuam pendentes. `r2.dev` é endpoint de testes; o endpoint de produção continua pendente.

O arquivo `output/terra-r2-publicacao-2026-10-03/manifest.json` mencionado pelo Rafael não existe nesta máquina. A cópia pública permite esta conciliação, mas falta comparar sua revisão/hash com a fonte dele. `output/terra-aab-build.init.gradle` também não está disponível aqui: Rafael precisa disponibilizar o ajuste sem senhas, chaves ou referências privadas desnecessárias para revisão. Nenhum ajuste de R8 foi aplicado por esta conferência.

O AAB anterior não inclui as correções recentes. Novo candidato somente após revisar a integração, versionar a configuração aprovada e conferir o `versionCode` no Console. Não compartilhar os arquivos de assinatura no Git ou no chat.

## Evidências

- `MANIFESTO-R2.json`: cópia da resposta pública em 11/10.
- `RESUMO.json`: contagens, tamanho informado pelo índice e SHA-256 da cópia.
- `ARTES-CONCILIADAS.csv`: correspondência por cena/arte e propostas de IDs. Não comprova cadastro na Play nem aceite visual.
