# CLAUDE.md

Contexto do projeto Atmosfera para o Claude Code — leia isto antes de explorar
a árvore de arquivos. Objetivo: não precisar reler o app inteiro a cada sessão.

## O que é o projeto

Live wallpaper Android (`android-app/`) que desenha uma cena animada reagindo
ao clima real (MET Norway) — chuva, neve, nuvens, sol/lua, vento, névoa, fumaça
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
- [docs/dev/HANDOFF-FRONTEND.md](docs/dev/HANDOFF-FRONTEND.md) — a **interface
  estável entre motor (`engine/`) e front** (`ui/`, `billing/`, `service/`,
  `weather/`). Continua útil como referência do contrato (`EffectEngine`,
  `Catalogo`, `Cena`, `carregar(Context)`). **Nota 2026-09-11:** a fronteira "motor
  do Rafael / front do Gabriel" acabou — `engine/` e `assets/` viraram do
  Gabriel e não são mais "congelados"; edite direto quando precisar.
- [docs/dev/CHECKLIST_PUBLICACAO.md](docs/dev/CHECKLIST_PUBLICACAO.md) — pendências de Play Store.
- [docs/dev/GUIA_PLAY_CONSOLE.md](docs/dev/GUIA_PLAY_CONSOLE.md) — respostas
  prontas (com a linha de código que sustenta cada uma) pro Data Safety Form
  e Content Rating Questionnaire.
- [docs/README.md](docs/README.md) — **índice** de toda a documentação, com o
  estado de cada arquivo (vigente / parcial / superado). Comece por ele quando
  não souber qual documento vale.
- [.lgpd/README.md](.lgpd/README.md) — registros internos de LGPD (ROPA, mapa de
  dados, fornecedores, retenção, incidentes, lacunas). Mudou coleta, SDK,
  fornecedor, permissão ou finalidade? Atualize **aqui e em `docs/legal/`** no
  mesmo PR.

## Arquitetura em uma tabela

| Pacote | Responsabilidade | Fronteira |
|---|---|---|
| `engine/` | `EffectEngine`, `SceneState`, `Atlas`, `Catalogo`, `Cena`, mais o suporte multi-cenário/multi-estilo (`Cenario.kt`/`Cenas`/`CenaCfg`, `Estilo.kt`/`Estilos`/`EstiloCfg`, prefs `ArteFundo` e `EstiloEfeito`) — desenha o wallpaper. `carregar(assets, cenaId, arte, estilo)` recarrega os assets do cenário/estilo escolhido | Motor (ver docs/dev/HANDOFF-FRONTEND.md) |
| `service/` | `AtmosferaWallpaperService` — hospeda o motor, busca clima, repassa pro motor | Front |
| `ui/` | Compose: `MainScreen` (Scaffold/NavHost/BottomNav), `HomeTab`, `StoreTab`, `SettingsTab`, `theme/`, `components/` | Front |
| `weather/` | `WeatherRepository` (MET Norway/Retrofit), `WeatherCache`, `LocationHelper`, `ClimaNaHome`; `WeatherWorker` legado sem consultas | Front |
| `billing/` | `BillingManager`, `Plano` (flag Premium local) | Front (Gabriel) |
| `debug/` | `DebugActivity`/`DebugOverride` — painel de teste, só builds debug, sem entrada na navegação normal | Front (ferramenta interna) |

## Comandos

Rode sempre a partir de `android-app/`.

```bash
./gradlew assembleDebug            # build — gate da CI (.github/workflows/build.yml)
./gradlew testDebugUnitTest        # testes unit (JVM puro) — também roda na CI, antes do APK
./gradlew lintDebug                # lint — roda na CI com baseline (app/lint-baseline.xml),
                                    # regenerado em 2026-08-08 pelo lint 8.13.2. O baseline hoje
                                    # tem ZERO erros: só avisos (estilo UseKtx, versões de lib,
                                    # orientação fixa). A CI quebra em qualquer erro novo.
                                    # Pra regenerar: apague o arquivo e rode lintDebug DUAS vezes —
                                    # a primeira recria o baseline e falha de propósito.
./gradlew installDebug             # build + instala no device/emulador conectado
./gradlew bundleRelease            # gera app-release.aab ASSINADO — exige keystore.properties
                                    # preenchido (ver "Build de release" abaixo); sem isso builda
                                    # sem assinar.
adb devices                         # confirma emulador/device antes de instalar
adb shell am start -n com.terra.wallpaper/.ui.MainActivity
adb shell am start -n com.terra.wallpaper/.debug.DebugLauncher   # painel de debug
                                    # É o ALIAS (exported=true) que abre a DebugActivity. Mirar
                                    # direto em .debug.DebugActivity falha com SecurityException:
                                    # ela é exported=false. Só existe em build debug — na gaveta
                                    # de apps aparece como "Atmosfera Teste".
```

SDK Android local: `android-app/local.properties` (`sdk.dir`) — já configurado
nesta máquina, não precisa de `ANDROID_HOME`.

**JDK 17 obrigatório.** A versão do Gradle (8.14.5) vem do **wrapper**
(`android-app/gradle/wrapper/gradle-wrapper.properties`) — a CI usa `./gradlew`
e não pina versão própria, justamente pra não divergir de novo. JDK 25+ (ex.: o
JBR embutido no Android Studio) quebra o build com
`Unsupported class file major version`.
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

## Prazos do Google que amarram a stack (não são preferência nossa)

A trava histórica "Kotlin 1.9.23 / Billing 6.2.1" **acabou em 2026-08-08**: foi
migrada de uma vez para Kotlin 2.4.10 + Billing 9.1.0 + AGP 8.13.2 + Gradle
8.14.5 + `compileSdk`/`targetSdk` 36. O motor (`engine/`) compilou **sem uma
linha alterada** — ele só usa `android.graphics` e stdlib.

Duas exigências do Google, ambas com o **mesmo prazo: 31/ago/2026** (extensão
mediante pedido até 01/nov/2026). Não são opcionais para publicar:

| Exigência | Onde vive | Validade da versão atual |
|---|---|---|
| Billing Library **v8+** ([FAQ](https://developer.android.com/google/play/billing/deprecation-faq)) | `libs.versions.toml` → `billing = "9.1.0"` | v9 vale até 31/ago/2028 |
| `targetSdk` **36+** ([política](https://support.google.com/googleplay/android-developer/answer/11926878)) | `app/build.gradle` → `targetSdk 36` | — |

Tetos que ainda existem (confirmados quebrando o build, não suposição):
- **`lifecycle` 2.11.0 exige `compileSdk` 37**, acima do máximo da AGP 8.13.x —
  por isso está em 2.10.0. Subir exige ir para AGP 9.x (que pede Gradle 9.5).
- **Compras exigem Play Store E conta Google logada.** O AVD `Pixel_8` responde
  `In-app billing API version 3 is not supported on this device` — mas **não** é
  por falta de Play Store: a imagem é `android-34/google_apis_playstore` e o
  `com.android.vending` está instalado (confirmado com `pm list packages`). O
  que falta é **conta Google logada** (`dumpsys account` volta vazio). Logar uma
  conta no emulador (Configurações → Contas) é o que destrava o serviço de
  billing; produto criado no Play Console continua sendo requisito à parte pra
  compra de verdade.

  Consequência prática: sem isso o `tanque` fica **intestável**, apesar de os
  assets já existirem em `assets/atmosfera/cenas/tanque/`. Para conseguir ver
  e testar conteúdo pago, use o painel de debug (`adb shell am start -n
  com.terra.wallpaper/.debug.DebugLauncher`) → **"Destravar cenários pagos
  (teste)"**. A flag é lida num único ponto (`DebugOverride.destravarPagos`),
  blindado por `BuildConfig.DEBUG` — em release o método devolve `false` sempre,
  então não existe caminho para um APK publicado liberar conteúdo pago por aí.
  O switch "Premium (efeitos vivos)" ao lado é outra coisa: liga os efeitos
  vivos, **não** dá posse dos cenários pagos (são compras separadas).

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
  e abre o PR. **Toda mudança na `main` exige PR** (ruleset do servidor, ver
  "Regras de PR") — a skill descreve o fluxo.

Consistência de código (simplificação, revisão) pode usar as skills genéricas
do Claude Code (`simplify`, `/code-review`) normalmente.

## Regras de PR (resumo)

**Toda mudança na `main` entra por PR — sem exceção.** Não é convenção: o
servidor aplica (ruleset abaixo). A antiga lista de "áreas de risco" (que
chegou a ser só `.github/`) continua no hook local e no CODEOWNERS, mas perdeu o
efeito prático — o ruleset já exige PR para qualquer arquivo.

Fluxo: trabalhar em `integracao/lancamento-teste` (ou branch própria) → push →
PR para `main` → CI `build` verde → mesclar com **merge commit** (preserva o
histórico; os docs citam hashes de commit). O Gabriel mescla os próprios PRs.

**Ruleset `main`** (id `20260889`, criado 2026-08-02, ativo; verificado via
`gh api` em 2026-09-29/30 — o `gh` está instalado e autenticado como
`gabzieg`):

| Regra | Efeito |
|---|---|
| `required_status_checks: build` | A CI tem que passar no commit do PR |
| `pull_request`, **0 aprovações** | PR obrigatório, mas o autor mescla sozinho (era 1 até 2026-09-30 — ver abaixo) |
| `non_fast_forward` | Force-push bloqueado — reescrever/"rebootar" o histórico da `main` não é possível |
| `deletion` | A `main` não pode ser apagada |

`bypass_actors: []` — ninguém contorna, nem o dono. A opção "Require an
additional approval for unattributed Copilot pull requests" está marcada, mas
só vale para PR aberto pelo Copilot **e** com contagem de aprovações maior que
zero — hoje não afeta nada.

**Configuração geral do repositório** (Settings → General → Pull Requests) é
separada do ruleset e também precisa permitir o método de merge: merge commit,
squash e rebase habilitados (merge commit ligado em 2026-09-30 — estava
desligado e barrou o primeiro merge do #31). **"Automatically delete head
branches" está ligado**: ao mesclar, o GitHub apaga a branch de origem — se for
a branch de trabalho, recriar com `git push origin <branch>`.

**Por que 0 aprovações (2026-09-30, decisão do Gabriel):** a exigência de 1
aprovação vinha de quando o time tinha três pessoas. Num time efetivamente solo,
todo PR do Gabriel dependia de o Rafael clicar em "aprovar", sem revisão real;
o guarda que protege de fato é a CI obrigatória, que continua. Mesmo raciocínio
que já tinha encolhido a lista de áreas de risco: revisão solo não melhora com
PR pra si mesmo — melhora com guarda automático. A mudança foi feita pelo
Gabriel na interface do GitHub: o classificador do modo automático do Claude
Code bloqueia, com razão, que o agente enfraqueça proteção de branch sozinho.

Aprovação: **tudo → Gabriel**. O Rafael não revisa código de terceiros — mas
desde 2026-09-23 volta a escrever o próprio (ver "O time" abaixo). Detalhes do
fluxo em `.claude/skills/abrir-pr/SKILL.md`.

(Histórico: até 2026-09-19 este arquivo afirmava que nada era aplicado pelo
servidor e que só `.github/` exigia PR — a lista de áreas de risco tinha
encolhido em 2026-08-09 e 2026-09-11. Está tudo no `git log` deste arquivo.)

Abaixo, as duas redes locais que continuam existindo — agora como primeira
barreira, não como única:

- `.githooks/pre-push` (ligar com `./scripts/setup-hooks.sh`) — barra push
  direto na `main` em área de risco. Escapa com `git push --no-verify`, mas
  isso só adianta o erro: o servidor recusa depois.
- ~~`.github/workflows/aviso-push-direto.yml`~~ — **removido em 2026-09-30.**
  Abria issue quando um commit "não vindo de PR" tocava área de risco; com o
  ruleset exigindo PR pra tudo, isso ficou impossível e ele só gerava alarme
  falso (issue #32: sinalizou um commit que entrou via PR #31).

A lista de caminhos de risco está duplicada no hook e no CODEOWNERS. Mudou
uma, mude a outra.

**O time é Gabriel e Rafael, com Claude dos dois lados.** Entre 2026-09-11 e
2026-09-23 o Rafael ficou só com a publicação de packs de wallpaper (conteúdo)
— mas **voltou a tocar código em 2026-09-23**: fixes reais em `engine/`
(oclusão de lua/estrela cadente por silhueta, `EffectEngine.kt`) e uma feature
em `ui/` (brilho + rolagem lateral em Ajustes), os dois com
`Co-Authored-By: Claude Sonnet 5` nos commits dele — mesmo rigor de gate que o
resto do projeto usa. **Não está confirmado se isso foi combinado entre os dois
ou é retomada por conta própria** — perguntar ao Gabriel antes de assumir
qualquer coisa sobre escopo dele daqui pra frente.

Consequência prática enquanto isso não for esclarecido: `engine/` voltou a ter
duas pessoas editando, o que é exatamente o cenário que gerou o conflito de
merge do `EffectEngine` no passado (motivo original do PR obrigatório nessas
áreas). O PR já é obrigatório pra tudo (ruleset acima), mas com 0 aprovações
ninguém olha o diff do outro — se o trabalho paralelo continuar, combinar quem
mexe em qual arquivo vale mais que a regra.

(Até 2026-09-11 o Rafael tocava `engine/` e `assets/atmosfera/` e entregava por
snapshot; era isso que justificava o PR obrigatório nessas áreas, regra que caiu
quando ele passou a só publicar packs — e que agora pode voltar a fazer sentido,
dependendo da resposta acima.)

Houve um terceiro (Willian, `@uWillianG`), que saiu. Ele escreveu a política de
privacidade (`2bb49ff`, 29/jul) e nada mais — `TERMOS.md` e `CONTATO.md` foram
redigidos depois, sem ele. O site de apresentação, que era responsabilidade dele,
**nunca foi começado e hoje está sem dono**.

Vale a lição, porque ela vai se repetir: a divisão de trabalho ficou apontando
para ele por um mês depois de ele parar de contribuir. Isso é pior do que não ter
dono nenhum — com um nome errado no papel, todo mundo assume que alguém está
olhando aquele arquivo. Quando alguém sair, corrija os documentos no mesmo dia.

Compliance de publicação **não** é todo dele: o Data Safety Form declara o que
o código coleta (área do Gabriel) e a conta do Play Console é do titular legal.
Divisão completa em [docs/dev/SPEC.md](docs/dev/SPEC.md) → "Publicação na Play Store".
