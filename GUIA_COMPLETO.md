# Atmosfera — Guia de Setup e Execução

> Ver também: [README.md](README.md) (visão geral) · [HANDOFF-FRONTEND.md](HANDOFF-FRONTEND.md)
> (arquitetura motor/front) · [CLAUDE.md](CLAUDE.md) (contexto para o Claude Code).

Este guia cobre como abrir, rodar e testar o app — tanto pelo Android Studio
quanto pelo terminal. Substitui os antigos `GUIA_COMPLETO.md` +
`GUIA_PASSO_A_PASSO.md` (que descreviam uma arquitetura anterior baseada em
imagens pré-renderizadas; o motor hoje é um renderizador de sprites em `Canvas` —
ver [README.md](README.md)).

## Pré-requisitos

- Android Studio (Hedgehog ou mais recente) **ou** apenas JDK 17 + Android SDK
  se for usar só o terminal.
- Android SDK com **API 34** instalada.
- Um emulador (AVD) ou aparelho físico com **Android 8.0 (API 26)** ou superior.

## Opção A — Pelo Android Studio

1. **Abrir**: Android Studio → **Open** → selecione a pasta `android-app/`
   (não a raiz do repositório) → aguarde o Gradle sincronizar.
2. **Dispositivo**: escolha um AVD no dropdown de dispositivos, ou conecte um
   aparelho físico com Depuração USB ativada.
3. **Rodar**: run configuration `app` → ▶ Run (`Shift+F10`).
4. No app: conceda a permissão de localização (ou deixe negada — cai no
   fallback de Guarapuava/PR) → toque em **"Definir papel de parede"** → o
   Android abre o seletor → escolha "Atmosfera – Clima ao Vivo" → confirme →
   Home para ver o wallpaper ativo.

## Opção B — Pelo terminal

Com um emulador já rodando ou aparelho conectado (`adb devices` deve listar
pelo menos um):

```bash
cd android-app
./gradlew installDebug            # compila e instala o debug APK
adb shell am start -n com.atmosfera.wallpaper/.ui.MainActivity
```

Outros comandos úteis:

```bash
./gradlew assembleDebug           # só compila, não instala
./gradlew lintDebug                # lint (não roda na CI, ver observação abaixo)
adb devices                        # lista dispositivos/emuladores conectados
adb shell screencap -p /sdcard/x.png && adb pull /sdcard/x.png .   # screenshot
```

> No Git Bash / MSYS no Windows, caminhos começando com `/` (como
> `/sdcard/...`) são reescritos para caminho de disco Windows por padrão.
> Prefixe o comando com `MSYS_NO_PATHCONV=1` quando isso quebrar (`adb pull`,
> por exemplo).

## Testar climas diferentes sem esperar o clima real

O painel de debug (`DebugActivity`, só existe em builds debug) força
condição/hora/vento/névoa e mostra uma prévia ao vivo do motor. Ele não tem
nenhum botão que leve até ele na navegação normal do app (é intencional — é
uma ferramenta interna). Pra abrir:

```bash
adb shell am start -n com.atmosfera.wallpaper/.debug.DebugActivity
```

Ou pelo Android Studio: **Run → Edit Configurations** → na run config `app`,
em "Launch Options" troque `Default Activity` por `Specified Activity` e
escolha `com.atmosfera.wallpaper.debug.DebugActivity`.

Ligue o switch **"Forçar este clima no wallpaper"** no painel pra fazer o
wallpaper de verdade (não só a prévia) usar a condição escolhida.

## Simular localização no emulador

Sem GPS real, o emulador permite definir uma localização manual:
**Extended Controls (⋮) → Location** → insira lat/long → **Send**.

| Cidade | Lat | Long |
|---|---|---|
| Guarapuava, PR (padrão do app sem permissão) | -25.3947 | -51.4528 |
| São Paulo, SP | -23.5505 | -46.6333 |
| Recife, PE | -8.0476 | -34.8770 |

## Build de release

```bash
keytool -genkey -v -keystore atmosfera-release.jks \
  -alias atmosfera -keyalg RSA -keysize 2048 -validity 10000
```

Configure a assinatura em `android-app/app/build.gradle` (`signingConfigs`) e
gere o bundle: **Build → Generate Signed Bundle/APK → Android App Bundle**.
Nunca versione a keystore nem senhas — o `.gitignore` já cobre `*.jks` e
`*.keystore`. Antes de publicar, veja
[CHECKLIST_PUBLICACAO.md](CHECKLIST_PUBLICACAO.md).

## Solução de problemas comuns

| Problema | Causa provável |
|---|---|
| "Gradle sync failed" | JDK errado — confirme JDK 17 em File → Project Structure → SDK Location |
| App abre mas cai pro launcher sem erro aparente | Emulador não estava totalmente pronto no primeiro `am start` — tente de novo depois de alguns segundos |
| Live Wallpaper não aparece na lista | Confirme instalação sem erros no Logcat; ou vá em Settings → Display → Wallpaper → Live Wallpapers |
| "PERMISSION_DENIED" de localização | Normal sem conceder a permissão — o app usa Guarapuava/PR como padrão |
| Emulador muito lento | Confirme virtualização por hardware ativa (HAXM/KVM) |

## Logcat — filtros úteis

```
tag:WeatherRepository   → chamadas à API Open-Meteo
tag:LocationHelper      → localização
tag:WeatherCache        → cache de clima
```
