# Ajuste de R8 para revisão do build Terra

Atualização da PR #50 em 11/10: o override 9.1.29 foi integrado a `android-app/build.gradle`, a partir do script sanitizado do commit `6cf8023`. A CI passa a compilar também release minificada. Não é necessário `-I` no build normal; o init script abaixo permanece como referência da entrega original. Aprovação em aparelho e geração de novo AAB continuam posteriores à revisão da integração.

`terra-aab-build.init.gradle` contém somente o override de R8 9.1.29 usado
na geração anterior. O original no workspace também configurava a chave
**pública** de compras e um diretório temporário de build. Essas duas partes
foram omitidas: não são necessárias para revisar a compatibilidade do R8.
Nenhum arquivo de assinatura acompanha esta entrega.

A [matriz oficial do Android](https://developer.android.com/build/kotlin-support),
conferida em 11/10/2026, exige R8 9.1.29 para Kotlin 2.4. O Terra anterior
usava Kotlin 2.4.10 e AGP 8.13.2; o R8 8.13.19 embutido emitia avisos de
metadados Kotlin. O override 9.1.29 foi usado no AAB local anterior e
conferido no cabeçalho do `mapping.txt`. Ele ainda não foi integrado à PR #50.

## Revisão na branch atualizada

A partir de `android-app`, com o JDK/SDK configurados:

```powershell
.\gradlew.bat -I ../tools/gradle/terra-aab-build.init.gradle buildEnvironment
.\gradlew.bat -I ../tools/gradle/terra-aab-build.init.gradle :app:assembleRelease
```

Conferir R8 9.1.29 na resolução de dependências e no cabeçalho do mapping
da versão minificada. Comparar os avisos de metadados com a execução anterior.
Não usar `bundleRelease` como prova de assinatura: ela depende da configuração
local existente, que não é fornecida aqui.

O script não altera fontes, pacote, versionCode, dependências de runtime,
ofertas ou catálogo. Não baixa nem publica conteúdo do R2. Depois da revisão,
integrar a configuração aprovada ao build e à CI da PR #50 para que o candidato
seja reproduzível. Testar esse candidato minificado em aparelho e pela Play;
o AAB antigo não comprova as correções posteriores.

## Verificação desta entrega — 11/10/2026

Executado `buildEnvironment --offline --no-daemon` com este init script em
checkout separado da fonte do AAB anterior, com JDK 17. Resultado:
`BUILD SUCCESSFUL`; R8 9.1.29 aparece resolvido no classpath. Essa verificação
confirma o carregamento do script e a resolução da dependência no ambiente
local. Não foi recompilada nem testada em aparelho a revisão atual da PR #50.
