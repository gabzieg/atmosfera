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
- [docs/dev/SPEC.md](docs/dev/SPEC.md) — o que é "pronto" (MVP publicável),
  não-objetivos, versões travadas e requisitos de segurança. Responde "o que
  estamos construindo", não "como".
- [docs/dev/ROADMAP.md](docs/dev/ROADMAP.md) — fases até publicar, cada uma
  com critério de saída explícito. Leia pra saber em que fase o projeto está.
- [docs/dev/TASKS.md](docs/dev/TASKS.md) — o que está em andamento hoje. Mais
  volátil que o ROADMAP; se contradizer `git log`/`git status`, confie no repo.
- [docs/dev/HANDOFF-FRONTEND.md](docs/dev/HANDOFF-FRONTEND.md) — **fronteira entre motor
  (`engine/`) e front** (`ui/`, `billing/`, `service/`, `weather/`) e a
  interface estável entre os dois. Leia antes de mexer em `engine/**` ou
  `assets/atmosfera/**` — é tratado como "congelado" por convenção do time,
  mas pode ser editado se o pedido for explícito (já aconteceu — ver seção 7
  do próprio HANDOFF).
- [docs/dev/CHECKLIST_PUBLICACAO.md](docs/dev/CHECKLIST_PUBLICACAO.md) — pendências de Play Store.
- [docs/dev/GUIA_PLAY_CONSOLE.md](docs/dev/GUIA_PLAY_CONSOLE.md) — respostas
  prontas (com a linha de código que sustenta cada uma) pro Data Safety Form
  e Content Rating Questionnaire.

## Arquitetura em uma tabela

| Pacote | Responsabilidade | Fronteira |
|---|---|---|
| `engine/` | `EffectEngine`, `SceneState`, `Atlas`, `Catalogo`, `Cena`, mais o suporte multi-cenário/multi-estilo (`Cenario.kt`/`Cenas`/`CenaCfg`, `Estilo.kt`/`Estilos`/`EstiloCfg`, prefs `ArteFundo` e `EstiloEfeito`) — desenha o wallpaper. `carregar(assets, cenaId, arte, estilo)` recarrega os assets do cenário/estilo escolhido | Motor (ver docs/dev/HANDOFF-FRONTEND.md) |
| `service/` | `AtmosferaWallpaperService` — hospeda o motor, busca clima, repassa pro motor | Front |
| `ui/` | Compose: `MainScreen` (Scaffold/NavHost/BottomNav), `HomeTab`, `StoreTab`, `SettingsTab`, `theme/`, `components/` | Front |
| `weather/` | `WeatherRepository` (Open-Meteo/Retrofit), `WeatherCache`, `LocationHelper`, `WeatherWorker` | Front |
| `billing/` | `BillingManager`, `Plano` (flag Premium local) | Front (Willian) |
| `debug/` | `DebugActivity`/`DebugOverride` — painel de teste, só builds debug, sem entrada na navegação normal | Front (ferramenta interna) |

## Comandos

Rode sempre a partir de `android-app/`.

```bash
./gradlew assembleDebug            # build — gate da CI (.github/workflows/build.yml)
./gradlew testDebugUnitTest        # testes unit (JVM puro) — também roda na CI, antes do APK
./gradlew lintDebug                # lint — roda na CI com baseline (app/lint-baseline.xml).
                                    # O baseline congela os avisos/erros pré-existentes (incl.
                                    # RemoveWorkManagerInitializer no manifesto e o falso-positivo
                                    # de permissão em LocationHelper.kt): a CI só quebra em erro
                                    # NOVO. Pra corrigir um: apague o baseline e regenere com lintDebug.
./gradlew installDebug             # build + instala no device/emulador conectado
./gradlew bundleRelease            # gera app-release.aab ASSINADO — exige keystore.properties
                                    # preenchido (ver "Build de release" abaixo); sem isso builda
                                    # sem assinar.
adb devices                         # confirma emulador/device antes de instalar
adb shell am start -n com.atmosfera.wallpaper/.ui.MainActivity
adb shell am start -n com.atmosfera.wallpaper/.debug.DebugActivity   # painel de debug
```

SDK Android local: `android-app/local.properties` (`sdk.dir`) — já configurado
nesta máquina, não precisa de `ANDROID_HOME`.

**JDK 17 obrigatório** (Gradle 8.6 — mesma versão travada na CI,
`.github/workflows/build.yml`). JDK 25+ (ex.: o JBR embutido no Android
Studio) quebra o build com `Unsupported class file major version`.
`JAVA_HOME` deve apontar pro Temurin 17 instalado nesta máquina
(`C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot`). **Gotcha
Windows:** a variável só é lida por processos novos — se `gradlew` reclamar de
versão de JDK numa sessão de terminal já aberta, abra um terminal novo (ou
re-exporte `JAVA_HOME` manualmente) em vez de assumir que o ambiente está
quebrado.

**Windows/Git Bash:** caminhos `/sdcard/...` em `adb shell`/`adb pull` são
reescritos para path do Windows pelo MSYS. Prefixe com `MSYS_NO_PATHCONV=1`
quando isso quebrar um comando `adb`.

**Build de release:** keystore de produção fica **fora do repo**, em
`C:\Users\gbrus\Chaves\atmosfera-release.jks`, referenciada por
`android-app/keystore.properties` (gitignored — nunca commitar). Sem esse
arquivo local, `bundleRelease`/`assembleRelease` builda sem assinar. A CI
roda `gitleaks` (job `secret-scan`) em todo push como segunda camada contra
segredo vazado no diff — não é substituto pra checar `git status` antes de
commitar em área que toca build/keystore.

## Gotcha grande: versão do Billing Library

O projeto está travado em `com.android.billingclient:billing-ktx:6.2.1`
(`android-app/app/build.gradle`). **Não suba essa versão sem também subir o
plugin Kotlin.** Confirmado rodando o build de verdade: 7.0.0+ é compilado com
metadata do Kotlin 2.x, que o compilador Kotlin 1.9.23 deste projeto não
consegue ler (`Class was compiled with an incompatible version of Kotlin`).
6.2.1 é o teto compatível com a API atual (`enablePendingPurchases()` sem
parâmetros — a versão com `PendingPurchasesParams` só existe a partir da 7.x).

**Isso deixou de ser só trava técnica — é bloqueio de publicação.** O Google
exige Billing Library **v8+ pra qualquer app novo ou update**; o prazo pra
v6.x já venceu em 31/ago/2025 (extensão até 01/nov/2025), ambos no passado
([developer.android.com/google/play/billing/deprecation-faq](https://developer.android.com/google/play/billing/deprecation-faq)).
Como o Atmosfera ainda não foi publicado, ele nasce sob a regra de "app novo":
não dá pra criar a ficha na Play Store com Billing 6.2.1. Migrar o plugin
Kotlin (1.9.23 → 2.x) deixa de ser "quando sobrar tempo" e vira pré-requisito
antes da Fase 2 do `ROADMAP.md` (criação de produtos/teste de compra). Ainda
não escalado pro ROADMAP/SPEC — decisão pendente do usuário sobre prioridade.

## Design system (Compose)

Tema em `ui/theme/` (`Color.kt`, `Theme.kt`, `Type.kt`) — **monocromático**
(preto/cinzas/branco, sem matiz): hierarquia vem de contraste, peso e
opacidade; estado "ativo" é resolvido por inversão (preenchimento claro sobre
fundo escuro), não por cor de acento. Paleta isolada em `Color.kt` — pra trocar
a identidade depois, edite só ali + o mapeamento em `Theme.kt`. Todo composable
deve usar `MaterialTheme.colorScheme.*`, nunca `Color(0xFF...)` solto.

Componentes reutilizáveis em `ui/components/`: `SectionCard`, `StatChip`,
`StatusPill`, `MosaicCard` (`Cards.kt`), `SceneThumbnail.kt` (carrega
`fundo.png` de um cenário/arte dos assets, placeholder honesto se faltar),
`StackedThumbnail.kt` (pilha de variantes de arte), `MasonryGrid.kt`,
`PillSearchBar.kt`, `SectionCarousel.kt` (grid/busca/carrossel da Loja em
mosaico). Reaproveite em vez de duplicar `Card { Column(padding...) }` em
telas novas.

## Convenções

- Identificadores em português no domínio do app (`estado`, `motor`,
  `desenhar`, `Cenario`), em inglês nas coisas genéricas de framework/Compose —
  siga o padrão já estabelecido no arquivo que estiver editando.
- Testes unit existem em `android-app/app/src/test/` (JVM puro, sem device) —
  `WeatherMappingTest`, `IntervaloClimaTest`, `PaginasLegaisSincronizadasTest`.
  Sem `androidTest/` (instrumentado) ainda.
- Sem sistema de dependency injection — objetos são construídos direto
  (`BillingManager(context) { ... }`) ou são `object` singletons sobre
  `SharedPreferences` (`Plano`, `Cena`, `DebugOverride`).

## Skills

Skills de projeto em `.claude/skills/`:

- **`run`** — build/instalar/abrir/tirar screenshot no emulador. Carregada
  quando o pedido for rodar ou testar visualmente o app.
- **`abrir-pr`** — fluxo de pull request: decide se a mudança exige PR ou pode
  ir direto na `main`, nomeia a branch, roda o gate, revisa o diff (`/code-review`)
  e abre o PR. **Leia antes
  de commitar/pushar** qualquer coisa que toque `engine/`, `assets/atmosfera/`,
  `billing/`, `AndroidManifest.xml`, `build.gradle` ou `.github/`.

Consistência de código (simplificação, revisão) pode usar as skills genéricas
do Claude Code (`simplify`, `/code-review`) normalmente.

## Regras de PR (resumo)

PR obrigatório só nas **áreas de risco** acima; doc e ajuste de UI podem ir
direto na `main`. Aprovação por área via `.github/CODEOWNERS`: motor (`engine/`,
`assets/`) → Rafael; billing (`billing/`) e documentos legais → Willian; resto
do front (`ui/`, `weather/`, `service/`) → Gabriel.
Detalhes e escape hatches em `.claude/skills/abrir-pr/SKILL.md`.

**Nada disso é aplicado pelo servidor.** O repo é privado no plano free:
branch protection e CODEOWNERS respondem `403 Upgrade to GitHub Pro`, então o
CODEOWNERS não pede revisor sozinho. O que existe de verdade hoje são duas
redes, ambas contornáveis:

- `.githooks/pre-push` (ligar com `./scripts/setup-hooks.sh`) — barra push
  direto na `main` em área de risco. Escapa com `git push --no-verify`.
- `.github/workflows/aviso-push-direto.yml` — abre issue quando um commit que
  não veio de PR toca área de risco. Avisa depois, não bloqueia.

A lista de caminhos de risco está duplicada nos dois + no CODEOWNERS. Mudou
uma, mude as três.

**Terceiro colaborador (Willian, `@uWillianG`)**: dono de `billing/`
(`BillingManager`, `Plano`, integração Google Play Billing) e dos **documentos
legais** (`docs/legal/*.md` + espelhos em `docs/<pagina>/index.html` e
`assets/legal/`). Também cuida do site de apresentação/marketing do Atmosfera —
fora deste repo, em repositório próprio (nome a definir, ex. `atmosfera-site`)
por causa da stack diferente (web, não Android/Gradle). Site ainda não criado.

Compliance de publicação **não** é todo dele: o Data Safety Form declara o que
o código coleta (área do Gabriel) e a conta do Play Console é do titular legal.
Divisão completa em [docs/dev/SPEC.md](docs/dev/SPEC.md) → "Publicação na Play Store".
