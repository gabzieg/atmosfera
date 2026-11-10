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
| **Clima** | MET Norway (sem chave; User-Agent + cache HTTP exigidos pelos termos), localização via `FusedLocationProviderClient`, cache local do último clima, consulta só com o wallpaper visível na home e o aparelho desbloqueado (`ClimaNaHome`); sem atualização periódica em segundo plano. | `weather/` |
| **Compras** | Google Play Billing — Premium (compra única global) + cenários avulsos. | `billing/` |
| **Painel de debug** | Força clima/hora/vento pra calibrar efeitos sem depender do clima real. Só em builds debug. | `debug/` |

## Tecnologias

| | |
|---|---|
| **Renderização** | Kotlin + Android `Canvas` (sprites, sem OpenGL) |
| **UI** | Jetpack Compose (Material 3) |
| **Clima** | MET Norway Locationforecast 2.0 (gratuita para uso comercial, sem chave, CC BY 4.0) |
| **Localização** | Google Play Services FusedLocationProvider |
| **Compras** | Google Play Billing 9.1.0 |
| **Build** | Kotlin 2.4.21 · AGP 8.13.2 · Gradle 8.14.6 · JDK 17 · compileSdk/targetSdk 36 · minSdk 26 |

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
  [docs/legal/PRIVACIDADE.md](docs/legal/PRIVACIDADE.md) corresponde ao comportamento. A revisão atual ainda é rascunho; use
  `python scripts/sincronizar_documentos_legais.py` para gerar as três exportações. Ver [processo editorial](docs/legal/README.md).
- **Usando Claude Code neste repo?** → [CLAUDE.md](CLAUDE.md) tem os comandos de build/verificação
  e as convenções do projeto pra não precisar reler tudo a cada sessão.

## Primeiro clone: ligue os hooks

```bash
./scripts/setup-hooks.sh
```

Aponta o `core.hooksPath` pra [.githooks/](.githooks/). O `pre-push` avisa,
ainda na sua máquina, quando você tenta empurrar direto pra `main`.

Quem manda de verdade é o **servidor**: a `main` tem um ruleset no GitHub que
exige **PR + CI `build` verde para qualquer arquivo** (0 aprovações — o autor
mescla o próprio PR) e bloqueia force-push e deleção. `git push origin main` é
recusado com ou sem `--no-verify`. Fluxo completo em
[CLAUDE.md](CLAUDE.md) → "Regras de PR".

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
adb shell am start -n com.terra.wallpaper/.ui.MainActivity
```

No Git Bash/MSYS no Windows, prefixe `MSYS_NO_PATHCONV=1` quando um caminho
`/sdcard/...` for reescrito pra path do Windows (`adb pull`, por exemplo).

**Painel de debug** (só builds debug — força clima/hora/vento pra calibrar
efeitos sem esperar o clima real):

```bash
adb shell am start -n com.terra.wallpaper/.debug.DebugLauncher
```

O alvo é o **alias** `.debug.DebugLauncher` — apontar direto para
`.debug.DebugActivity` falha com `SecurityException`, porque a Activity é
`exported=false`. Em build debug o painel também aparece na gaveta de apps
como "Atmosfera Teste".

**Simular localização** no emulador sem GPS real: Extended Controls (⋮) →
Location → lat/long → Send. Padrão do app sem permissão: São Paulo, SP
(-25.3947, -51.4528).

**Regressões dos produtos Play (sem rede):** na raiz do repositório, execute
`python3 -B scripts/run_play_regressions.py` com Python 3.9 ou superior
(a CI usa Python 3.12). O mesmo comando roda as suítes completas de cadastro e
preparação no check obrigatório `build`, em PRs para `main` e pushes na `main`,
com limite de dois minutos na CI. Usa apenas a biblioteca padrão, console
simulado e arquivos versionados: não requer credenciais nem instalação de
`requirements-play.txt`. Falha de teste ou ausência de testes em qualquer uma
das suítes reprova o passo.

**Composição do candidato (#86, decisão2.7):** estacionamento/natal/eiffel/ogro/timessquare
são não finalizadas, preservadas no histórico Git e no grupo do TASKS, fora dos índices
embarcados e dos assets do candidato. As sete exclusões anteriores e sprites
pixel_retro/pixel_retro_2 continuam fora. Configurações históricas de Cenas e objetos
remotos não são apagados por esse recorte. Antes de congelar a composição:

```bash
python3 -B scripts/candidate_catalog.py
python3 -B -m unittest discover -s scripts -p 'test_candidate_catalog.py' -v
```

O primeiro comando apenas confere pares, referências com hash completo, miniaturas,
produtos e camadas nativas. `--apply` recorta explicitamente os dois índices locais
e remove assets vedados; não importa manifesto remoto, não baixa artes nem toca no
bucket. O importador reutiliza a mesma política e não reintroduz as famílias não
finalizadas. No Windows, usar `py` no lugar de `python3`. Controle no check `build`.

**Build de release — candidato para teste:** use a chave de upload existente
do aplicativo. Para atualizar o Terra, não gere nem substitua a chave. Na máquina
segura de geração, confira JDK 17, SDK Android 36 e o wrapper do repositório.
Antes de alterar `versionCode`, reconfira o maior código enviado e envios em
andamento no Console; use um código maior e ainda não utilizado, preservando
`com.terra.wallpaper`. O nome atual é `1.0.5`; mudá-lo exige decisão explícita.

Identifique a revisão final em um checkout limpo separado, sem sobrescrever
alterações de outro workspace. Na raiz, registre `git rev-parse HEAD`,
`git branch --show-current` e `git status --short` (deve estar vazio), rode
`git diff --check` e `python3 scripts/sincronizar_documentos_legais.py --check`.
Uma árvore preparada sem commit ainda depende da definição/autorização dessa
revisão; CI de outro commit não valida o candidato.

Configure localmente `android-app/keystore.properties` conforme o exemplo,
usando a assinatura existente e preenchendo também `playLicensePublicKey`
com a chave pública de licenciamento do aplicativo no Play Console. Confira
presença e correspondência dessa chave na máquina segura; o Gradle valida o
formato Base64/RSA quando preenchida, mas aceita vazio. Sem esse arquivo o
release pode compilar sem assinatura; com Billing vazio, compras são recusadas.
Nunca envie senhas, keystore ou o conteúdo desse arquivo para Git/chat.

Endpoint aprovado pelo dono para o candidato: `https://acervo.terrawallpaper.com.br/`
(#86, decisão de09/11/2026, recibo6748608436; conferência pública10/11). Release aceita URL vazia e não herda o bucket padrão do debug;
passe explicitamente o endpoint aprovado. Geração/assinatura abaixo dependem do
checkpoint5.0 no SHA/código/ambiente identificados, não da existência deste roteiro. No PowerShell,
em `android-app/`, com os arquivos locais configurados, execute cada etapa e
pare se houver erro:

```powershell
$ErrorActionPreference = 'Stop'
java -version
if ($LASTEXITCODE -ne 0) { throw 'Java indisponivel' }
.\gradlew.bat --version
if ($LASTEXITCODE -ne 0) { throw 'Gradle indisponivel' }
$acervoTeste = 'https://acervo.terrawallpaper.com.br/'
if ([string]::IsNullOrWhiteSpace($acervoTeste)) { throw 'Endpoint obrigatorio' }
.\gradlew.bat clean testDebugUnitTest lintDebug assembleDebug assembleRelease bundleRelease "-PterraAcervoUrl=$acervoTeste"
if ($LASTEXITCODE -ne 0) { throw 'Gate/geracao falhou' }
```

Em Bash, o mesmo gate usa `./gradlew` e o argumento
`-PterraAcervoUrl` com o endpoint confirmado. Confira localmente o BuildConfig
gerado para release: `DEBUG=false`, `ACERVO_URL` igual ao endpoint confirmado e
chave Billing presente e correspondente à configuração local. Registre apenas
o resultado dessas conferências, sem copiar o conteúdo do arquivo.

Verifique **o AAB final**, em `android-app/`, usando o JAR oficial do
[bundletool](https://developer.android.com/tools/bundletool) já disponível no
ambiente. Se a ferramenta faltar, registre a pendência antes de concluir o
candidato. No PowerShell:

```powershell
$ErrorActionPreference = 'Stop'
$bundletoolJar = Read-Host 'Caminho do JAR bundletool existente'
if (!(Test-Path -LiteralPath $bundletoolJar -PathType Leaf)) { throw 'JAR ausente' }
$aab = 'app/build/outputs/bundle/release/app-release.aab'
java -jar "$bundletoolJar" validate "--bundle=$aab"
if ($LASTEXITCODE -ne 0) { throw 'Bundle invalido' }
java -jar "$bundletoolJar" dump manifest "--bundle=$aab" --module=base
if ($LASTEXITCODE -ne 0) { throw 'Manifesto indisponivel' }
jarsigner -verify -verbose -certs -strict "$aab"
if ($LASTEXITCODE -ne 0) { throw 'Verificacao de assinatura exige classificacao antes de seguir' }
keytool -printcert -jarfile "$aab"
if ($LASTEXITCODE -ne 0) { throw 'Certificado indisponivel' }
Get-FileHash -Algorithm SHA256 "$aab"
```

No manifesto extraído, confira pacote, versão/código acordados, minSdk 26 e
targetSdk 36; `debuggable` e `usesCleartextTraffic` não podem estar `true`, nem
o alias `.debug.DebugLauncher` estar presente. A `DebugActivity` interna existe
no manifesto principal e deve continuar sem exportação. Registre a verificação
da assinatura e compare a impressão SHA-256 do certificado com o certificado
de **upload** esperado, não com a chave pública Billing ou, se diferente, o
certificado de assinatura dos APKs pela Play. Avisos de certificado autoassinado
ou cadeia não confiável podem causar saída não zero no
[`jarsigner -strict`](https://docs.oracle.com/en/java/javase/17/docs/specs/man/jarsigner.html):
registre e avalie os motivos com o certificado esperado; falha de integridade,
entrada sem assinatura ou identidade divergente impede concluir.

Guarde o AAB e `app/build/outputs/mapping/release/mapping.txt` juntos em
armazenamento privado do responsável. Registre revisão/branch, estado Git,
versão, endpoint público, ferramentas usadas, resultados dos gates, SHA-256
do AAB e impressão do certificado de upload no item de candidato do
[checklist canônico](docs/dev/CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md).
Essas verificações não comprovam compras/restauração em aparelho, correção de
erros do Console ou aprovação pela Play. Gerar o candidato não autoriza upload
ou distribuição.

**Problemas comuns:**

| Problema | Causa provável |
|---|---|
| "Gradle sync failed" | JDK errado — confirme JDK 17 em File → Project Structure → SDK Location |
| Live Wallpaper não aparece na lista | Confira instalação sem erro no Logcat; ou Settings → Display → Wallpaper → Live Wallpapers |
| "PERMISSION_DENIED" de localização | Normal sem conceder a permissão — cai no fallback de São Paulo/SP |
| Emulador muito lento | Confirme virtualização por hardware ativa (HAXM/KVM) |

**Logcat — filtros úteis:** `tag:WeatherRepository` (chamadas à MET Norway),
`tag:LocationHelper`, `tag:WeatherCache`.
