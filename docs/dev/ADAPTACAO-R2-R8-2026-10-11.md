# Adaptação ao acervo hospedado e R8 — 11/10/2026

Base: PR #50. Rafael confirmou equivalência JSON entre o manifesto público e sua cópia de publicação. O ajuste sanitizado de R8 foi revisado no commit `6cf8023`; somente os dois arquivos de `tools/gradle/` foram trazidos, sem mesclar a branch antiga ou o questionário com dados pessoais.

## Implementação

- Build versionado fixa `com.android.tools:r8:9.1.29` no classpath. A [matriz oficial](https://developer.android.com/build/kotlin-support) indica essa versão mínima para Kotlin 2.4. O build normal não depende do init script local; a CI passa a conferir `assembleRelease` além de testes, lint e debug.
- Manifesto embarcado normalizado mantém os caminhos WebP já publicados, com hash no nome. Os hashes completos são extraídos dos nomes e conferidos contra os prefixos do manifesto original; cada download exige o hash completo do conteúdo. Nenhum arquivo foi substituído no R2.
- Catálogo: 71 cenas/366 artes, cinco amostras e 361 SKUs pagos propostos. Cenas nativas são mescladas às variantes remotas; somente nove artes continuam embutidas. As 357 demais precisam de download HD, após concessão do direito e solicitação da pessoa.
- 366 miniaturas WebP existentes no bucket foram incorporadas, com tamanho e SHA-256 conferidos; somam 7.951.528 bytes. Elas não requerem compra nem download HD para visualizar o catálogo. JPEGs intermediários anteriores foram removidos.
- O destrave de testes continua exclusivo de debug, sem gravar compras; ele também exige instalação antes de aplicar uma arte remota.
- `debug` usa o endpoint público de testes por padrão. `release` exige `-PterraAcervoUrl=...` explícito; não presume que `r2.dev` seja servidor de produção. O build não cadastra produtos, concede compras ou publica arquivos.

## Reproduzir o catálogo

Na raiz, executar `python scripts/sincronizar_acervo_r2.py`. O script usa a cópia conferida em `docs/loja/conciliacao-2026-10-11/MANIFESTO-R2.json`, filtra as sete famílias excluídas e baixa apenas miniaturas, verificando tamanho e hash. Não faz upload.

`scripts/preparar_acervo.py` permanece como alternativa PNG local. Por padrão, agora escreve os metadados em `dist/acervo-preparado/assets-catalogo/`, preservando o catálogo R2 do app. `--usar-no-app` substitui explicitamente essa fonte e não deve ser usado no candidato que reutiliza o bucket atual. O antigo gerador JPEG não é necessário para a integração R2.

## Conferência remota realizada

Foi executado `python scripts/verificar_pacotes_r2.py`: seis ZIPs (comuns e artes Cabana/clay, Aldeia japonesa/aqua e Porto/pixel) passaram em tamanho, SHA-256 completo, integridade ZIP e presença de arquivos obrigatórios. Total: 2.421.888 bytes. Os arquivos ficam em `android-app/app/build/acervo-smoke/`, fora do Git e do aplicativo. Esse lote não valida os demais ZIPs, o motor no aparelho nem checkout pela Play.

O catálogo ainda contém 54 variantes sem declaração específica no motor local. A leitura remota permite selecionar seus arquivos, mas posição dos astros, proporções, máscaras e efeitos precisam do aceite do Rafael. Metadados disponíveis não são aprovação visual.

## Antes do AAB

1. Rafael revisar a adaptação e conferir no aparelho as amostras, as variantes adicionais das cinco cenas e um lote de cenas remotas.
2. Usar `docs/loja/conciliacao-2026-10-11/ARTES-CONCILIADAS.csv` como lista proposta dos 361 produtos pagos (nenhum cadastrado por esta execução). Conferir preços/produtos e compra individual, pagamento pendente, restauração, reembolso, download, cancelamento, falha e uso offline. Direitos e arquivos permanecem independentes do Premium.
3. Aprovar catálogo/miniaturas e os parâmetros das 54 variantes adicionais; ampliar o aceite para todas as artes anunciadas.
4. Definir endpoint de produção/versionamento, dados do provedor nos textos legais e continuidade das compras. Não substituir objetos com nome de hash.
5. Conferir o próximo `versionCode` disponível no Console. Não reutilizar automaticamente o código 6 dos artefatos anteriores.
6. Só então gerar um novo AAB da revisão aprovada, com a configuração de endpoint conferida e assinatura local autorizada. Preservar commit, hash e mapping. Não publicar arquivos de assinatura.

Nenhum novo AAB foi gerado por esta adaptação. A avaliação no aplicativo continua com Rafael, conforme orientação do usuário.

## Resultado dos checks locais

89 testes aprovados, sem falhas ou testes ignorados. Lint debug sem erros, com 31 avisos existentes; lint vital de release sem erros. `assembleDebug` e `assembleRelease` concluidos com sucesso. O cabecalho do mapping confirma `compiler_version: 9.1.29`; nao apareceram os avisos anteriores de metadados Kotlin. R8 registrou dois avisos sobre o provedor de classes do AGP nao suportar parsing assincrono; compilacao concluida usando o caminho sincrono.

Inspecao dos dois APKs confirma 366 miniaturas e nove fundos HD nativos; as outras artes nao foram embutidas. Os dois testes novos de HTTP usam servidor local e exercitam hash/recibo e preservacao da arte anterior em falha; nao substituem testes de cancelamento, rede/espaco no Android ou faturamento pela Play.
