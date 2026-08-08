---
name: run
description: Build, install, launch and screenshot the Atmosfera Android app on a connected emulator/device. Use whenever asked to run, test, or visually verify the Atmosfera app (companion app or the live wallpaper itself) instead of the generic run patterns.
---

# Rodar o Atmosfera

Este projeto é um app Android (`android-app/`), não um servidor web — "rodar o
app" significa compilar, instalar num emulador/device e abrir a Activity certa
via `adb`, não `npm run dev`.

## 1. Confirmar que há um device/emulador conectado

```bash
SDK="$HOME/AppData/Local/Android/Sdk"   # ou leia sdk.dir de android-app/local.properties
"$SDK/platform-tools/adb.exe" devices
```

Se a lista vier vazia, o usuário precisa abrir o Android Studio e ligar um AVD
(hoje existe um chamado `Pixel_8`) antes de continuar — não tente subir o
emulador via `emulator -avd` a não ser que peçam explicitamente (é lento e
pode já haver um rodando fora do seu controle).

## 2. Build + instalar

Sempre a partir de `android-app/`:

```bash
cd android-app
./gradlew installDebug --console=plain --no-daemon
```

Isso compila e instala em um único comando. Se só quiser validar que compila
sem instalar, use `assembleDebug` (é o gate real da CI, ver `.github/workflows/build.yml`).

## 3. Abrir

```bash
adb shell am start -n com.atmosfera.wallpaper/.ui.MainActivity
```

Painel de debug (força clima/hora/vento, destrava cenários pagos; só builds
debug):

```bash
adb shell am start -n com.atmosfera.wallpaper/.debug.DebugLauncher
```

Use o **alias** `.debug.DebugLauncher`, não `.debug.DebugActivity`: a Activity
é `exported=false` e mirar nela direto devolve
`SecurityException: Permission Denial: ... not exported`. Na gaveta de apps o
mesmo painel aparece como "Atmosfera Teste".

Depois de abrir, confirme que a activity certa está em foco antes de seguir
em frente (evita reportar sucesso quando o app na verdade caiu de volta pro
launcher):

```bash
adb shell "dumpsys window | grep -i mCurrentFocus"
```

## 4. Screenshot pra verificação visual

```bash
adb shell "screencap -p /sdcard/shot.png"
MSYS_NO_PATHCONV=1 adb pull /sdcard/shot.png /caminho/local/shot.png
```

O prefixo `MSYS_NO_PATHCONV=1` é obrigatório no Git Bash/MSYS (Windows) — sem
ele, `/sdcard/shot.png` é reescrito para um caminho de disco Windows e o `adb
pull` falha com "failed to stat remote object". Depois de puxar a imagem, leia
o PNG com a ferramenta de leitura de arquivos pra realmente ver o resultado —
não assuma que renderizou certo só porque os comandos não deram erro.

## 5. Navegar entre telas sem tocar na tela

```bash
adb shell input tap <x> <y>
```

As coordenadas do `adb shell input tap` são em pixels reais do device (ex.:
1080×2400 no Pixel 8), não nos pixels "exibidos" que aparecem em anotações de
screenshot — multiplique pelo fator de escala se estiver lendo coordenadas de
uma imagem redimensionada.

## Erros conhecidos que não são bug do app

- **App volta pro launcher sem erro no logcat logo após `am start`**: geralmente
  o emulador ainda não tinha terminado de inicializar quando o comando rodou.
  Espere alguns segundos e tente de novo antes de investigar como se fosse
  crash — confirme primeiro com `dumpsys window | grep mCurrentFocus`.
