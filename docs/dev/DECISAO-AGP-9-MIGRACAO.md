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

---

# RESULTADO — 2026-09-18: TENTADA, REVERTIDA

**Status: a migração foi executada, passou em TODO o gate, e foi revertida
porque quebra o app em runtime.** Não refaça sem ler o que está abaixo.

O trabalho está preservado na branch `chore/agp-9-migracao` (commit `4b7456a`),
não apagada de propósito: o AGP 10.0 vai tornar isto obrigatório um dia, e essa
branch é o ponto de partida — com as armadilhas já mapeadas.

## O que quebrou

O build de release do AGP 9.4 **crasha na abertura**:

```
RuntimeException: Failed to create an instance of class androidx.work.impl.WorkDatabase
  MainActivity.onCreate → WeatherWorker.schedule → WorkManager → WorkDatabase
```

O Room instancia `WorkDatabase_Impl` por reflexão (nome da classe + `_Impl`). O
R8 9.x é mais agressivo que o 8.13.x e removeu a classe. O `proguard-rules.pro`
não tem regra de keep pra Room/WorkManager — só Retrofit, Gson e OkHttp.

## A comparação que atribui a culpa (mesmo device, mesma hora, 1 variável)

| | AGP 8.13.2 | AGP 9.4.0 |
|---|---|---|
| Abre | ✅ processo vivo | ❌ FATAL EXCEPTION |
| WorkManager + Retrofit/Gson | ✅ `Worker result SUCCESS` | ❌ não alcançado |
| Billing inicializa | ✅ (falha só ambiental: sem conta Google) | ❌ não alcançado |

Regressão da migração, não defeito pré-existente. Confirmado empiricamente
recompilando o release em `a5f37aa` e instalando no mesmo emulador.

## Por que revertemos em vez de consertar

1. **O problema original nunca causou defeito real.** O build com os avisos de
   metadata funciona. O aviso é cosmético (qualidade de otimização, não
   correção). Trocaríamos um aviso inofensivo por um app que não abre.
2. **A extensão do estrago é desconhecida.** O crash ocorre no `onCreate` e
   bloqueia tudo atrás dele — Billing, Loja, troca de cenário e aplicar
   wallpaper nunca foram exercitados sob R8 9.x. Se ele é mais agressivo com
   reflexão, cada um é candidato. Consertar não é "adicionar uma regra": é uma
   caça a bugs de duração imprevisível, às vésperas de publicar.
3. **Custo já cobrado** além disto: a versão do Kotlin passa a viver em dois
   lugares (catálogo + `buildscript{}` raiz), porque o Kotlin embutido do AGP 9
   rebaixa pra KGP 2.2.10 em silêncio se não for contrariado.

## ⚠️ A lição de processo (vale pra QUALQUER migração futura)

**Os critérios de rollback escritos acima eram todos de build-time** — gate,
zipalign, tamanho do AAB. **Todos passaram. O app crasha mesmo assim.**

O gate deste projeto não alcança defeito de runtime:
- os testes são JVM puro (`src/test/`), sem `androidTest/`;
- `minifyEnabled` só vale no release, então o R8 nem roda no que é testado.

Existe uma faixa inteira de defeitos — tudo que depende de reflexão: Room,
WorkManager, Retrofit/Gson, Billing — que **nenhuma verificação automática atual
detecta**. Esse buraco continua aberto independentemente desta decisão.

**Critério de aceite pra migração daqui em diante: instalar o build de RELEASE
num device e exercitar os caminhos de reflexão.** Gate verde valida a
compilação, não o produto.

## Pendências que este episódio revelou (nenhuma feita)

- **Regra de keep pra Room/WorkManager no `proguard-rules.pro`.** Hoje só não
  morde porque o R8 8.13 é tolerante — é fragilidade latente, não código correto.
  Vale corrigir mesmo ficando no AGP 8.
- **O aviso de metadata do R8 continua existindo** (a opção A, baixar pro Kotlin
  2.3.20, resolve com uma linha — mas o release dela TAMBÉM nunca rodou em
  device; testar antes de confiar).
- **`compileSdk 37` / `lifecycle 2.11.0`** seguem travados pelo teto da AGP 8.13.
