# Atmosfera — Guia Completo de Configuração e Execução

## Visão Geral do Projeto

```
atmosfera/
├── image-generator/          ← Script Python (roda 1x no seu PC)
│   └── generate_wallpapers.py
└── android-app/              ← Projeto Android (Android Studio)
    ├── build.gradle
    ├── settings.gradle
    ├── gradle.properties
    └── app/
        ├── build.gradle
        ├── proguard-rules.pro
        └── src/main/
            ├── AndroidManifest.xml
            ├── assets/wallpapers/     ← Imagens geradas pelo script
            ├── java/com/atmosfera/wallpaper/
            │   ├── AtmosferaApp.kt
            │   ├── service/
            │   │   └── AtmosferaWallpaperService.kt
            │   ├── weather/
            │   │   ├── WeatherCondition.kt
            │   │   ├── WeatherRepository.kt
            │   │   ├── WeatherCache.kt
            │   │   ├── LocationHelper.kt
            │   │   └── BootReceiver.kt
            │   └── ui/
            │       └── MainActivity.kt
            └── res/
                ├── layout/activity_main.xml
                ├── values/{strings, colors, themes}.xml
                ├── xml/wallpaper_info.xml
                └── drawable/...
```

---

## PARTE 1 — Gerar as Imagens com Gemini Imagen

### 1.1 Pré-requisitos

- Python 3.9 ou superior instalado
- Chave de API do Google AI Studio (https://aistudio.google.com/app/apikey)

### 1.2 Instalar dependências Python

Abra o terminal na pasta `image-generator/` e execute:

```bash
pip install google-generativeai pillow
```

### 1.3 Executar o gerador

```bash
# Opção A: passando a chave direto
python generate_wallpapers.py --api_key SUA_CHAVE_AQUI

# Opção B: via variável de ambiente (recomendado)
export GEMINI_API_KEY=SUA_CHAVE_AQUI
python generate_wallpapers.py

# Gerar apenas algumas cenas específicas (para testar)
python generate_wallpapers.py --api_key SUA_CHAVE --only sunny_morning cloudy_afternoon rainy_night
```

O script vai:
1. Criar a pasta `image-generator/output/` com as imagens `.webp`
2. Copiar automaticamente para `android-app/app/src/main/assets/wallpapers/`

> ⏱ **Tempo estimado:** ~2 min para as 24 imagens (depende da API).
> 💡 **Dica:** Se uma imagem falhar, execute novamente — o script pula as que já existem.

### 1.4 Verificar as imagens geradas

Após rodar, confirme que existem arquivos `.webp` em:
```
android-app/app/src/main/assets/wallpapers/
```

Arquivos esperados:
```
sunny_morning.webp          partly_cloudy_morning.webp
sunny_afternoon.webp        partly_cloudy_afternoon.webp
cloudy_morning.webp         partly_cloudy_night.webp
cloudy_afternoon.webp       light_rain_morning.webp
cloudy_night.webp           light_rain_afternoon.webp
heavy_rain_morning.webp     light_rain_night.webp
heavy_rain_afternoon.webp   storm_morning.webp
heavy_rain_night.webp       storm_afternoon.webp
storm_night.webp            foggy_morning.webp
foggy_afternoon.webp        snow_morning.webp
snow_afternoon.webp         snow_night.webp
clear_night.webp
```

---

## PARTE 2 — Configurar o Android Studio

### 2.1 Instalar o Android Studio

1. Baixe em: https://developer.android.com/studio
2. Instale com as opções padrão (marque "Android Virtual Device" durante a instalação)
3. Na primeira abertura, o Android Studio vai baixar o Android SDK automaticamente

### 2.2 Abrir o projeto

1. Abra o Android Studio
2. Clique em **"Open"** (não "New Project")
3. Navegue até a pasta `atmosfera/android-app/`
4. Clique em **OK**
5. Aguarde o Gradle sincronizar (barra de progresso no rodapé — pode levar 3-5 min na primeira vez)

> ⚠️ Se aparecer a mensagem **"Gradle sync failed"**, vá para a seção "Solução de Problemas" no final deste guia.

### 2.3 Verificar o SDK instalado

1. No menu: **File → Project Structure → SDK Location**
2. Confirme que o Android SDK está instalado (geralmente em `~/Android/Sdk` no Linux/Mac ou `C:\Users\SEU_USUARIO\AppData\Local\Android\Sdk` no Windows)
3. Certifique-se de que o **JDK** é versão 17 ou superior

---

## PARTE 3 — Criar o Emulador Android

### 3.1 Abrir o AVD Manager

- Menu: **Tools → Device Manager**
- Ou clique no ícone de celular na barra lateral direita

### 3.2 Criar um novo dispositivo virtual

1. Clique em **"+" → Create Virtual Device**
2. Em **"Category"**, selecione **Phone**
3. Escolha o dispositivo: **Pixel 7** (recomendado — boa resolução para testar wallpaper)
4. Clique em **Next**

### 3.3 Selecionar a imagem do sistema

1. Selecione a aba **"Recommended"**
2. Baixe e selecione: **API 34 (Android 14) — x86_64**
   - Clique no ícone de download ⬇ ao lado da versão se ainda não estiver instalada
   - Aguarde o download concluir
3. Clique em **Next**

### 3.4 Configurar o AVD

Na tela de configuração:
- **AVD Name:** `Atmosfera_Pixel7`
- **Startup orientation:** Portrait
- Em **"Show Advanced Settings"**:
  - **RAM:** 2048 MB
  - **VM Heap:** 512 MB
  - **Internal Storage:** 4096 MB
- Clique em **Finish**

---

## PARTE 4 — Executar o App no Emulador

### 4.1 Iniciar o emulador

1. No **Device Manager**, clique no botão ▶ (play) ao lado de `Atmosfera_Pixel7`
2. Aguarde o emulador iniciar completamente (tela de desbloqueio aparecer)

### 4.2 Rodar o app (companion activity)

1. Na barra de ferramentas superior do Android Studio:
   - **Run configuration:** selecione `app`
   - **Device:** selecione `Atmosfera_Pixel7`
2. Clique no botão **▶ Run** (ou `Shift+F10`)
3. O app vai compilar e instalar no emulador automaticamente

### 4.3 O que você verá

O app companion vai abrir mostrando:
- **Preview** do wallpaper atual baseado no clima
- **Temperatura** e condição climática
- Botão **"Definir como Wallpaper"**

### 4.4 Ativar o Live Wallpaper no emulador

1. Toque em **"Definir como Wallpaper"** no app
2. O Android vai abrir o seletor de wallpaper
3. Selecione **"Atmosfera – Clima ao Vivo"**
4. Toque em **"Definir wallpaper"**
5. Pressione o botão Home no emulador → o wallpaper estará ativo!

> 💡 **Dica:** No emulador, o Live Wallpaper funciona normalmente. Você pode ver o relógio, temperatura e as partículas de chuva/neve na tela inicial.

---

## PARTE 5 — Simular Condições Climáticas (para testar)

Como o emulador não tem GPS real, você pode simular a localização:

### 5.1 Simular localização no emulador

1. No emulador, clique nos **"..."** (três pontinhos) na barra lateral
2. Vá em **Location**
3. Digite as coordenadas desejadas:
   - Guarapuava, PR: Lat `-25.3947`, Long `-51.4528`
   - São Paulo, SP: Lat `-23.5505`, Long `-46.6333`
   - Recife, PE: Lat `-8.0476`, Long `-34.8770`
4. Clique em **"Send"**

### 5.2 Forçar um clima específico (modo debug)

Para testar um clima específico sem depender da API, você pode editar temporariamente o `WeatherRepository.kt` e adicionar um retorno fixo:

```kotlin
// APENAS PARA TESTE — remova antes de publicar
suspend fun fetchWeather(latitude: Double, longitude: Double): Result<WeatherState> {
    return Result.success(WeatherState(
        condition = WeatherCondition.STORM,   // ← troque aqui
        period = DayPeriod.NIGHT,
        temperatureCelsius = 14.0,
        feelsLikeCelsius = 11.0,
        description = "Tempestade"
    ))
}
```

Condições disponíveis para testar:
- `WeatherCondition.SUNNY` + `DayPeriod.MORNING`
- `WeatherCondition.STORM` + `DayPeriod.NIGHT`
- `WeatherCondition.SNOW` + `DayPeriod.AFTERNOON`
- `WeatherCondition.FOGGY` + `DayPeriod.MORNING`
- *(todas as combinações da tabela no README)*

---

## PARTE 6 — Build de Release (para publicar)

Quando o app estiver pronto para publicação:

### 6.1 Gerar a keystore (assinar o app)

No terminal:
```bash
keytool -genkey -v -keystore atmosfera-release.jks \
  -alias atmosfera -keyalg RSA -keysize 2048 -validity 10000
```

Guarde o arquivo `.jks` e as senhas em local seguro!

### 6.2 Configurar assinatura no build.gradle

```gradle
android {
    signingConfigs {
        release {
            storeFile file('../atmosfera-release.jks')
            storePassword 'SUA_SENHA'
            keyAlias 'atmosfera'
            keyPassword 'SUA_SENHA'
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            // ...
        }
    }
}
```

### 6.3 Gerar o AAB (formato da Play Store)

- Menu: **Build → Generate Signed Bundle/APK**
- Selecione **Android App Bundle (.aab)**
- Siga os passos e selecione a keystore criada

O arquivo `.aab` gerado em `app/release/` é o que você vai fazer upload na Play Store.

---

## Solução de Problemas Comuns

### ❌ "Gradle sync failed"
- Verifique sua conexão com a internet
- Menu: **File → Invalidate Caches → Invalidate and Restart**
- Verifique se o JDK 17 está instalado: **File → Project Structure → SDK Location → JDK**

### ❌ "Asset não encontrado" (wallpaper preto)
- Confirme que as imagens `.webp` estão em `app/src/main/assets/wallpapers/`
- Execute o script Python novamente
- Verifique os nomes dos arquivos (devem ser exatamente como listados na seção 1.4)

### ❌ "PERMISSION_DENIED" para localização
- No emulador: **Settings → Apps → Atmosfera → Permissions → Location → Allow**
- O app usa Guarapuava, PR como fallback quando sem permissão

### ❌ Live Wallpaper não aparece na lista
- Certifique-se de que o app foi instalado com sucesso (sem erros no Logcat)
- Tente via Settings → Display → Wallpaper → Live Wallpapers

### ❌ Emulador muito lento
- Verifique se a virtualização por hardware (HAXM/KVM) está habilitada:
  - Windows: BIOS → Enable Virtualization Technology
  - Linux: `sudo apt install qemu-kvm`

---

## Logcat — Filtros úteis para debug

No Android Studio, na aba **Logcat**, use estes filtros:

```
tag:AtmosferaEngine    → logs do wallpaper (desenho, bitmap)
tag:WeatherRepository  → logs da API Open-Meteo
tag:LocationHelper     → logs de localização
tag:WeatherCache       → logs de cache
```

---

## Próximos Passos (Roadmap)

- [ ] Nome da cidade dinâmico (Geocoder reverso)
- [ ] Tela de configurações (cidade manual, unidade °C/°F)
- [ ] Transição suave entre wallpapers (fade in/out)
- [ ] Widget para tela inicial (AppWidgetProvider)
- [ ] Publicação na Play Store (conta de dev: U$ 25 taxa única)
- [ ] Testes em dispositivo físico
