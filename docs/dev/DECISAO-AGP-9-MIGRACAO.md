# Decisão: migrar para AGP 9.x (não baixar o Kotlin) — 2026-09-18

## O achado que motivou isto

`bundleRelease` com Kotlin 2.4.10 emite avisos repetidos do R8 ("An error
occurred when parsing kotlin metadata") — confirmado, não é falso-positivo.
Causa raiz: **Kotlin 2.4 exige R8 9.1.29; o AGP 8.13.2 empacota R8 8.13.19**,
que só suporta oficialmente até Kotlin 2.3. Build passa (é warning, não erro),
mas é descompasso real, não suposição.

## As duas opções investigadas

**Opção A — baixar Kotlin 2.4.10 → 2.3.20.** Testada empiricamente: gate
completo + `bundleRelease` verdes, 0 erro, **warning do R8 zerado**, AAB mesmo
tamanho (405,6 MB), assinatura válida. Custo: uma linha de versão. Descartada
mesmo funcionando — decisão do Gabriel foi pela opção B.

**Opção B — subir pra AGP 9.x (escolhida).** Mantém Kotlin 2.4.10 (nunca foi
exigência do Google — foi bumpado incidentalmente na migração `f179f48` junto
com Billing v8+/targetSdk 36, que são as exigências reais). Resolve o mesmo
achado pela raiz, e já destrava o teto documentado no CLAUDE.md (`lifecycle`
2.11+ exige `compileSdk` 37, acima do máximo do AGP 8.13.x).

## Por que B é risco elevado — e o que isso realmente custa

Confirmado na documentação oficial (developer.android.com/build/releases/agp-9-0-0-release-notes,
.../gradle-plugin-roadmap):

- Exige **Gradle 9.1.0** (hoje 8.14.5) e revalida JDK mínimo.
- **Duas rotas**: (1) migrar pra DSL nova de vez (`variantFilter` →
  `androidComponents.beforeVariants`, `applicationVariants` → `onVariants`,
  `targetSdk` explícito em vez de herdar de `minSdk`, R class deixa de ser
  constante) — ou (2) opt-out temporário (`android.builtInKotlin=false`,
  `android.newDsl=false`) pra adiar a migração de DSL.
- **A rota (2) vence pouco tempo**: o próprio Google descreve o AGP 9.x como
  "a culminação de um esforço de vários anos" rumo ao AGP 10.0, que **remove os
  flags de opt-out por completo**. Estimativa oficial pra isso: **meados de
  2026** — e já está perto (o AGP já está em 9.4.0, lançado em setembro/2026).
  Fazer o opt-out agora significa refazer a mesma migração de DSL de novo, em
  breve, sob prazo mais curto.
- **Recomendação dentro da opção B: migrar pra DSL nova agora (rota 1), não
  usar o opt-out.** Evita fazer o trabalho duas vezes.

## Impacto esperado (a confirmar empiricamente na execução)

- **Usabilidade do app: nenhum impacto esperado.** É mudança de como o Gradle
  monta o projeto, não de comportamento do app — não toca Compose, lógica de
  negócio nem UI.
- **Tamanho do AAB: esperado neutro a levemente menor** (R8 mais novo tende a
  melhorar shrinking). Não medido ainda — só confirma depois de migrar e rodar
  `bundleRelease`, do mesmo jeito que a opção A foi confirmada antes de decidir.

## Linha de backtrack — pré-requisito antes de tocar em qualquer arquivo de build

**Hoje não existe uma linha de backtrack de verdade**, porque o estado bom
atual (Terra + RS + sincronia legal/DPO + B02 + B03, tudo com gate verde) está
inteiro sem commit. "Reverter" não tem pra onde voltar sem isso.

Antes de iniciar a migração:
1. **Commitar o estado atual** (o bloco de correções desta sessão) — é o ponto
   de restauração real.
2. **Fazer a migração numa branch própria** (`chore/agp-9-migracao` ou similar),
   nunca direto em cima do trabalho já commitado.
3. **Critério de rollback**: se o gate completo (`testDebugUnitTest lintDebug
   assembleDebug bundleRelease`) não fechar limpo, ou se a checagem de 16 KB
   (`zipalign -P 16 -c`) falhar, ou se o tamanho do AAB crescer de forma
   inesperada — descarta a branch, volta pro commit do passo 1, sem meio-termo.
4. Só depois de gate verde na branch nova: decidir se ela vira a linha
   principal (merge) ou se fica em espera.

## EXECUÇÃO — 2026-09-18, branch `chore/agp-9-migracao`

**Status: migração feita e verificada.** O ponto de restauração é o commit
`a5f37aa` na `integracao/lancamento-teste` (gate verde, AAB 405,6 MB assinado).

### O que mudou

| Item | De | Para |
|---|---|---|
| AGP | 8.13.2 | 9.4.0 |
| Gradle | 8.14.5 | 9.6.0 |
| Kotlin | 2.4.10 | 2.4.10 (preservado — ver armadilha abaixo) |
| Plugin Kotlin | `kotlin-android` separado | embutido no `com.android.application` |
| `kotlinOptions{}` | dentro de `android{}` | `kotlin{compilerOptions{}}` de nível de arquivo |
| `android.enableJetifier` | `true` | removido |

A parte cara da migração **não se aplicou**: o projeto não usava NENHUMA API
legada de variant (`applicationVariants`, `variantFilter`, `registerJavaGeneratingTask`
etc.) — confirmado por grep antes de começar. Sobrou só a troca de plugin.

### A armadilha: o Kotlin embutido REBAIXA a versão em silêncio

O Kotlin embutido do AGP 9.x usa **KGP 2.2.10** por padrão. Sem intervenção, o
`releaseCompileClasspath` inteiro passou a resolver `kotlin-stdlib:2.2.10` —
ou seja, a migração feita "no caminho feliz" teria **rebaixado o Kotlin de
2.4.10 pra 2.2.10**, versão mais antiga até que os 2.3.20 da opção A, que foi
descartada justamente por ser downgrade.

**O build de debug NÃO revela isso** — compila, passa nos testes, fecha verde.
Só apareceu porque o `bundleRelease` falhou ao tentar resolver
`org.jetbrains.kotlin:compose-group-mapping:2.2.10`, artefato que não existe
nessa coordenada. Sem rodar release, teríamos commitado um downgrade invisível.

Correção (notas do AGP 9.0): declarar a KGP no classpath do buildscript raiz.
Confirmado depois: classpath inteiro de volta em 2.4.10.

**Dívida criada:** a versão do Kotlin agora vive em DOIS lugares —
`gradle/libs.versions.toml` e o `buildscript{}` do `build.gradle` raiz — porque
o catálogo não é acessível dentro de `buildscript{}`. Avisado por comentário nos
dois arquivos. É duplicação frágil do mesmo tipo que o CLAUDE.md já trata como
fonte de erro ("mudou uma, mude as três"); a opção A não criaria isso.

### Por que 2.4.10 importa (a raiz do problema)

Tabela oficial (developer.android.com/build/kotlin-support):

| Kotlin | R8 exigido |
|---|---|
| 2.4.x | **9.1.29+** |
| 2.3.x | 8.13.19 |

O AGP 8.13.2 embute R8 8.13.19 — casa exatamente com Kotlin 2.3.x (por isso a
opção A zerou o aviso), e não com o 2.4.10 que o projeto usa. O AGP 9.4 traz R8
9.4.x, acima do exigido. Era ESTE o descompasso.

### Verificação (rodada final, tudo aplicado)

```
BUILD SUCCESSFUL · EXIT_GRADLE=0
erros de compilação ............ 0
testes falhando ................ 0
aviso kotlin metadata do R8 .... 0   ← o objetivo da migração
deprecations do Gradle 10 ...... 0   (4 corrigidas: sintaxe `prop valor` → `prop = valor`)
menções a jetifier ............. 0
lint ........................... "no errors or warnings"
AAB ............................ 405,6 MB, `jar verified`
alinhamento 16 KB .............. Verification successful
```

Tasks críticas confirmadas executadas (`minifyReleaseWithR8`,
`produceReleaseComposeMapping`, `signReleaseBundle`) — "0 avisos" só tem valor
se o R8 rodou de fato; num build anterior o mesmo zero era falso, porque a
build morria em 37s antes de chegar lá.

**Tamanho: impacto zero.** 405,6 MB antes e depois. **Usabilidade: nenhum
impacto** — nada aqui toca comportamento de runtime, só como o projeto é montado.

### Destravado, mas deliberadamente NÃO feito aqui

O AGP 9.4 suporta até API 37, então o teto que prendia `lifecycle` em 2.10.0
(`2.11.0 exige compileSdk 37`) caiu. Não alterado de propósito: é mudança
independente, e misturar duas coisas numa migração impede saber qual quebrou o
quê. Fica como oportunidade separada, junto com `NotShrinkingResources` e as
sugestões de versão de lib que o lint novo passou a apontar.
