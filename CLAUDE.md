# CLAUDE.md

Contexto do projeto Atmosfera para o Claude Code — leia isto antes de explorar
a árvore de arquivos. Objetivo: não precisar reler o app inteiro a cada sessão.

## O que é o projeto

Live wallpaper Android (`android-app/`) que desenha uma cena animada reagindo
ao clima real (Open-Meteo) — chuva, neve, nuvens, sol/lua, vento, névoa, fumaça
de chaminé — via um motor de partículas em `Canvas`, não vídeo/imagem. Tem um
app companion em Compose (Início/Loja/Ajustes) pra ativar o wallpaper e vender
Premium + cenários extras via Google Play Billing.

Documentação de apoio (leia sob demanda, não de cara):
- [README.md](README.md) — visão geral e stack.
- [HANDOFF-FRONTEND.md](HANDOFF-FRONTEND.md) — **fronteira entre motor
  (`engine/`) e front** (`ui/`, `billing/`, `service/`, `weather/`) e a
  interface estável entre os dois. Leia antes de mexer em `engine/**` ou
  `assets/atmosfera/**` — é tratado como "congelado" por convenção do time,
  mas pode ser editado se o pedido for explícito (já aconteceu — ver seção 7
  do próprio HANDOFF).
- [GUIA_COMPLETO.md](GUIA_COMPLETO.md) — como rodar (Android Studio e terminal).
- [CHECKLIST_PUBLICACAO.md](CHECKLIST_PUBLICACAO.md) — pendências de Play Store.

## Arquitetura em uma tabela

| Pacote | Responsabilidade | Fronteira |
|---|---|---|
| `engine/` | `EffectEngine`, `SceneState`, `Atlas`, `Catalogo`, `Cena`, mais o suporte multi-cenário/multi-estilo (`Cenario.kt`/`Cenas`/`CenaCfg`, `Estilo.kt`/`Estilos`/`EstiloCfg`, prefs `ArteFundo` e `EstiloEfeito`) — desenha o wallpaper. `carregar(assets, cenaId, arte, estilo)` recarrega os assets do cenário/estilo escolhido | Motor (ver HANDOFF-FRONTEND.md) |
| `service/` | `AtmosferaWallpaperService` — hospeda o motor, busca clima, repassa pro motor | Front |
| `ui/` | Compose: `MainScreen` (Scaffold/NavHost/BottomNav), `HomeTab`, `StoreTab`, `SettingsTab`, `theme/`, `components/` | Front |
| `weather/` | `WeatherRepository` (Open-Meteo/Retrofit), `WeatherCache`, `LocationHelper`, `WeatherWorker` | Front |
| `billing/` | `BillingManager`, `Plano` (flag Premium local) | Front |
| `debug/` | `DebugActivity`/`DebugOverride` — painel de teste, só builds debug, sem entrada na navegação normal | Front (ferramenta interna) |

## Comandos

Rode sempre a partir de `android-app/`.

```bash
./gradlew assembleDebug            # build — é o único gate real da CI (.github/workflows/build.yml)
./gradlew installDebug             # build + instala no device/emulador conectado
./gradlew lintDebug                 # NÃO roda na CI; tem 2 erros pré-existentes conhecidos
                                     # (falso-positivo em LocationHelper.kt, aviso do WorkManager
                                     # init no manifesto) — não são deste projeto, não tente "corrigir"
                                     # sem que seja pedido explicitamente.
adb devices                         # confirma emulador/device antes de instalar
adb shell am start -n com.atmosfera.wallpaper/.ui.MainActivity
adb shell am start -n com.atmosfera.wallpaper/.debug.DebugActivity   # painel de debug
```

SDK Android local: `android-app/local.properties` (`sdk.dir`) — já configurado
nesta máquina, não precisa de `ANDROID_HOME`.

**Windows/Git Bash:** caminhos `/sdcard/...` em `adb shell`/`adb pull` são
reescritos para path do Windows pelo MSYS. Prefixe com `MSYS_NO_PATHCONV=1`
quando isso quebrar um comando `adb`.

## Gotcha grande: versão do Billing Library

O projeto está travado em `com.android.billingclient:billing-ktx:6.2.1`
(`android-app/app/build.gradle`). **Não suba essa versão sem também subir o
plugin Kotlin.** Confirmado rodando o build de verdade: 7.0.0+ é compilado com
metadata do Kotlin 2.x, que o compilador Kotlin 1.9.23 deste projeto não
consegue ler (`Class was compiled with an incompatible version of Kotlin`).
6.2.1 é o teto compatível com a API atual (`enablePendingPurchases()` sem
parâmetros — a versão com `PendingPurchasesParams` só existe a partir da 7.x).

## Design system (Compose)

Tema em `ui/theme/` (`Color.kt`, `Theme.kt`, `Type.kt`) — dark + azul,
reaproveita as cores do XML legado (`colors.xml`/`themes.xml`):
`AtmBackground` #0D1117, `AtmSurface` #1C2333, `AtmAccent` #378ADD. Todo
composable de tela deve usar `MaterialTheme.colorScheme.*`, nunca `Color(0xFF...)`
solto — isso já causou uma inconsistência visual (banner Premium com cores de
tema claro num app dark) corrigida numa rodada anterior.

Componentes reutilizáveis em `ui/components/`: `SectionCard`, `StatChip`,
`StatusPill` (`Cards.kt`), `SceneThumbnail.kt` (carrega `fundo.png` de um
cenário dos assets, com fallback pra cabana), `WeatherIcon.kt` (emoji por
`WeatherCondition`). Reaproveite em vez de duplicar `Card { Column(padding...) }`
em telas novas.

## Convenções

- Identificadores em português no domínio do app (`estado`, `motor`,
  `desenhar`, `Cenario`), em inglês nas coisas genéricas de framework/Compose —
  siga o padrão já estabelecido no arquivo que estiver editando.
- Sem testes automatizados no projeto ainda (não existe `test/`/`androidTest/`)
  — não assuma que existem antes de tentar rodá-los.
- Sem sistema de dependency injection — objetos são construídos direto
  (`BillingManager(context) { ... }`) ou são `object` singletons sobre
  `SharedPreferences` (`Plano`, `Cena`, `DebugOverride`).

## Skills

Este repo tem uma skill de projeto em `.claude/skills/run/SKILL.md` que
encapsula o fluxo de build/instalar/abrir/tirar screenshot descrito acima —
carregada automaticamente quando o pedido for rodar ou testar visualmente o
app. Consistência de código (simplificação, revisão) pode usar as skills
genéricas do Claude Code (`simplify`, `/code-review`) normalmente.
