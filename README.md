# 🌤 Atmosfera

> Live Wallpaper Android que reage ao clima real, desenhado em tempo real.

O papel de parede muda ao vivo (~30 fps) com base no clima atual da localização do
usuário: sol, nuvens, chuva, neve, névoa, tempestade, vento, fumaça de chaminé,
estrelas, lua e mais — tudo desenhado por um motor de partículas em `Canvas`
nativo sobre uma arte de fundo fixa (sprites, não vídeo/imagens pré-renderizadas).

## Visão geral

| Camada | O que faz | Pasta |
|---|---|---|
| **Motor de efeitos** | Recebe um `SceneState` (clima + hora + plano) e desenha o cenário a cada frame combinando a arte de fundo com sprites animados. | `engine/` + `assets/atmosfera/` |
| **Serviço de wallpaper** | Hospeda o motor como `WallpaperService`, busca o clima e repassa pro motor. | `service/` |
| **App companion** | Tela Início (clima + preview + "Definir papel de parede"), Loja (cenários, Premium, compras avulsas) e Ajustes. Jetpack Compose, Material 3. | `ui/` |
| **Clima** | Open-Meteo (sem chave), localização via `FusedLocationProviderClient`, cache 30 min, atualização via `WorkManager`. | `weather/` |
| **Compras** | Google Play Billing — Premium (compra única global) + cenários avulsos. | `billing/` |
| **Painel de debug** | Força clima/hora/vento pra calibrar efeitos sem depender do clima real. Só em builds debug. | `debug/` |

## Tecnologias

| | |
|---|---|
| **Renderização** | Kotlin + Android `Canvas` (sprites, sem OpenGL) |
| **UI** | Jetpack Compose (Material 3) |
| **Clima** | Open-Meteo API (gratuita, sem chave) |
| **Localização** | Google Play Services FusedLocationProvider |
| **Compras** | Google Play Billing 6.2.1 |
| **Build** | Kotlin 1.9.23 · AGP 8.3.0 · JDK 17 · compileSdk 34 · minSdk 26 |

## Cenários

O catálogo (`engine/Catalogo.kt`) é a fonte da verdade dos wallpapers disponíveis.
Hoje: **Cabana na floresta** (grátis, disponível) e **Tanque — campo de batalha**
(avulso; assets prontos em `assets/atmosfera/cenas/tanque/`, ainda não plugado na
troca de cenário do motor — ver `docs/dev/HANDOFF-FRONTEND.md`).

## Estrutura do repositório

```text
atmosfera/
├── README.md                 ← este arquivo
├── CLAUDE.md                 ← contexto do projeto p/ Claude Code (comandos, convenções)
├── .githooks/                 ← hooks versionados (pre-push); ligar com scripts/setup-hooks.sh
├── docs/                      ← documentação + páginas públicas
│   ├── dev/                  ← doc interna: HANDOFF, SPEC, ROADMAP, TASKS, CHECKLIST, GUIA_PLAY_CONSOLE
│   ├── legal/                ← privacidade, termos, contato (texto canônico em .md)
│   ├── privacidade/          ← espelho HTML — é o que vai pro ar e o que o app embute
│   ├── termos/                  (idem)
│   ├── contato/                 (idem)
│   └── _config.yml           ← mantém dev/ e legal/ fora do GitHub Pages
├── android-app/               ← app Android (Kotlin)
│   └── app/src/main/
│       ├── assets/atmosfera/     ← sprites e fundos do motor
│       └── java/com/atmosfera/wallpaper/
│           ├── engine/            ← motor de efeitos
│           ├── service/           ← WallpaperService
│           ├── ui/                ← app companion (Compose)
│           ├── weather/           ← clima, localização, cache
│           ├── billing/           ← Google Play Billing
│           └── debug/             ← painel de teste (só debug)
├── image-generator/          ← ferramenta de uma fase anterior do projeto (ver nota no arquivo)
└── sprite-tester/            ← ferramenta HTML/JS de apoio pra calibrar sprites
```

## Documentação

- **Vai mexer no companion app (UI/loja/billing)?** → [docs/dev/HANDOFF-FRONTEND.md](docs/dev/HANDOFF-FRONTEND.md)
  descreve a fronteira entre o motor (congelado) e o front, e a interface estável entre os dois.
- **Vai publicar uma versão?** → [docs/dev/CHECKLIST_PUBLICACAO.md](docs/dev/CHECKLIST_PUBLICACAO.md)
- **Mexeu em `weather/`, `billing/` ou no manifesto?** → confira se
  [docs/legal/PRIVACIDADE.md](docs/legal/PRIVACIDADE.md) continua verdadeira (é o texto publicado, não
  um rascunho) e replique a mudança em [`docs/privacidade/index.html`](docs/privacidade/index.html).
- **Usando Claude Code neste repo?** → [CLAUDE.md](CLAUDE.md) tem os comandos de build/verificação
  e as convenções do projeto pra não precisar reler tudo a cada sessão.

## Primeiro clone: ligue os hooks

```bash
./scripts/setup-hooks.sh
```

Aponta o `core.hooksPath` pra [.githooks/](.githooks/). O `pre-push` barra push
direto na `main` quando o diff toca **área de risco** (`engine/`,
`assets/atmosfera/`, `billing/`, `AndroidManifest.xml`, `build.gradle`,
`.github/`) — essas exigem branch + PR. Doc e ajuste de UI continuam podendo
ir direto na `main`, de propósito.

É uma rede local, não uma trava: `git push --no-verify` passa por cima. Como o
repositório é privado no plano free do GitHub, **branch protection e CODEOWNERS
não funcionam** (a API responde `403 Upgrade to GitHub Pro`), então o hook mais
o workflow [aviso-push-direto](.github/workflows/aviso-push-direto.yml) — que
abre uma issue quando algo escapa — são o que existe hoje no lugar de um gate
de servidor.

## Rodando o projeto

**Pré-requisitos:** Android Studio (Hedgehog+) **ou** só JDK 17 + Android SDK
(API 34) pra terminal. Emulador (AVD) ou aparelho físico Android 8.0+ (API 26+).

**Pelo Android Studio:** abrir a pasta `android-app/` (não a raiz do repo) →
aguardar o Gradle sincronizar → escolher device → run configuration `app` →
▶ Run.

**Pelo terminal**, com emulador/aparelho já conectado (`adb devices` deve listar):

```bash
cd android-app
./gradlew installDebug            # compila e instala o debug APK
adb shell am start -n com.atmosfera.wallpaper/.ui.MainActivity
```

No Git Bash/MSYS no Windows, prefixe `MSYS_NO_PATHCONV=1` quando um caminho
`/sdcard/...` for reescrito pra path do Windows (`adb pull`, por exemplo).

**Painel de debug** (só builds debug — força clima/hora/vento pra calibrar
efeitos sem esperar o clima real):

```bash
adb shell am start -n com.atmosfera.wallpaper/.debug.DebugActivity
```

**Simular localização** no emulador sem GPS real: Extended Controls (⋮) →
Location → lat/long → Send. Padrão do app sem permissão: Guarapuava, PR
(-25.3947, -51.4528).

**Build de release:** gerar a keystore de produção uma vez só (guarde em local
seguro — perdê-la impede publicar updates do mesmo app):

```bash
keytool -genkey -v -keystore atmosfera-release.jks \
  -alias atmosfera -keyalg RSA -keysize 2048 -validity 10000
```

Copiar `android-app/keystore.properties.example` para
`android-app/keystore.properties` e preencher `storeFile`/senhas — o
`build.gradle` lê esse arquivo sozinho e assina o release automaticamente
quando ele existir (sem ele, `assembleRelease` builda sem assinar; não afeta
`assembleDebug` nem a CI). Gerar pelo Android Studio: **Build → Generate
Signed Bundle/APK**, ou `./gradlew bundleRelease` no terminal. Nunca versione
a keystore, as senhas nem `keystore.properties` — o `.gitignore` já cobre
publicar, ver [docs/dev/CHECKLIST_PUBLICACAO.md](docs/dev/CHECKLIST_PUBLICACAO.md).

**Problemas comuns:**

| Problema | Causa provável |
|---|---|
| "Gradle sync failed" | JDK errado — confirme JDK 17 em File → Project Structure → SDK Location |
| Live Wallpaper não aparece na lista | Confira instalação sem erro no Logcat; ou Settings → Display → Wallpaper → Live Wallpapers |
| "PERMISSION_DENIED" de localização | Normal sem conceder a permissão — cai no fallback de Guarapuava/PR |
| Emulador muito lento | Confirme virtualização por hardware ativa (HAXM/KVM) |

**Logcat — filtros úteis:** `tag:WeatherRepository` (chamadas à Open-Meteo),
`tag:LocationHelper`, `tag:WeatherCache`.
