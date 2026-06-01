# 🌄 Atmosfera — Guia Completo de Desenvolvimento

## Visão Geral do Projeto

```
atmosfera/
├── image-generator/          ← Script Python (roda uma vez no PC)
│   ├── generate_wallpapers.py
│   └── output/               ← Imagens geradas ficam aqui
│
└── android-app/              ← Projeto Android (abre no Android Studio)
    ├── app/
    │   └── src/main/
    │       ├── assets/wallpapers/   ← Imagens copiadas pelo script
    │       ├── java/com/atmosfera/wallpaper/
    │       │   ├── service/         ← Live Wallpaper Service
    │       │   ├── weather/         ← API + Cache + Location
    │       │   └── ui/              ← Activity principal
    │       └── res/
    ├── build.gradle
    └── settings.gradle
```

---

## PARTE 1 — Gerar as Imagens com Gemini Imagen

### Pré-requisitos

- Python 3.9 ou superior instalado
- Chave de API do Google AI Studio: https://aistudio.google.com/app/apikey

### Passo 1 — Instalar dependências Python

Abra o terminal e execute:

```bash
pip install google-generativeai pillow
```

### Passo 2 — Executar o gerador

```bash
cd atmosfera/image-generator

# Com a chave diretamente:
python generate_wallpapers.py --api_key SUA_CHAVE_AQUI

# Ou definindo como variável de ambiente (recomendado):
export GEMINI_API_KEY=SUA_CHAVE_AQUI
python generate_wallpapers.py
```

O script vai:
1. Gerar **24 imagens** (1080×2340 px, formato WebP)
2. Salvar em `image-generator/output/`
3. Copiar automaticamente para `android-app/app/src/main/assets/wallpapers/`

### Gerar apenas uma cena (para testes)

```bash
python generate_wallpapers.py --api_key SUA_CHAVE --only sunny_morning
```

### Lista completa de cenas geradas

| Arquivo                    | Descrição              |
|----------------------------|------------------------|
| sunny_morning.webp         | Ensolarado – manhã     |
| sunny_afternoon.webp       | Ensolarado – tarde     |
| partly_cloudy_morning.webp | Parcial – manhã        |
| partly_cloudy_afternoon.webp | Parcial – tarde      |
| partly_cloudy_night.webp   | Parcial – noite        |
| cloudy_morning.webp        | Nublado – manhã        |
| cloudy_afternoon.webp      | Nublado – tarde        |
| cloudy_night.webp          | Nublado – noite        |
| light_rain_morning.webp    | Chuva leve – manhã     |
| light_rain_afternoon.webp  | Chuva leve – tarde     |
| light_rain_night.webp      | Chuva leve – noite     |
| heavy_rain_morning.webp    | Chuva forte – manhã    |
| heavy_rain_afternoon.webp  | Chuva forte – tarde    |
| heavy_rain_night.webp      | Chuva forte – noite    |
| storm_morning.webp         | Tempestade – manhã     |
| storm_afternoon.webp       | Tempestade – tarde     |
| storm_night.webp           | Tempestade – noite     |
| foggy_morning.webp         | Neblina – manhã        |
| foggy_afternoon.webp       | Neblina – tarde        |
| snow_morning.webp          | Neve – manhã           |
| snow_afternoon.webp        | Neve – tarde           |
| snow_night.webp            | Neve – noite           |
| clear_night.webp           | Noite clara / estrelas |

---

## PARTE 2 — Configurar o Android Studio

### Passo 3 — Instalar o Android Studio

1. Acesse: https://developer.android.com/studio
2. Baixe e instale o Android Studio (versão **Hedgehog 2023.1.1** ou superior)
3. Durante a instalação, aceite a instalação do **Android SDK**

### Passo 4 — Abrir o projeto

1. Abra o Android Studio
2. Clique em **"Open"** (não "New Project")
3. Navegue até a pasta `atmosfera/android-app/`
4. Clique em **OK**
5. Aguarde o Gradle sincronizar (pode demorar 2–5 minutos na primeira vez)

> ⚠️ Se aparecer erro "Gradle JDK not found": vá em
> `File → Project Structure → SDK Location` e configure o JDK 17
> (o Android Studio já vem com o JDK embutido, selecione "Embedded JDK")

### Passo 5 — Adicionar dependência do Google Play Services (Localização)

No arquivo `app/build.gradle`, dentro de `dependencies { }`, adicione:

```groovy
implementation 'com.google.android.gms:play-services-location:21.2.0'
implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
```

Depois clique em **"Sync Now"** na barra amarela que aparecer.

---

## PARTE 3 — Criar e Configurar o Emulador

### Passo 6 — Criar um AVD (Android Virtual Device)

1. No Android Studio, vá em **Tools → Device Manager**
2. Clique em **"Create Virtual Device"**
3. Selecione: **Pixel 7** (recomendado) → Next
4. Selecione a imagem do sistema: **API 34 (Android 14) — x86_64**
   - Se não aparecer, clique em "Download" ao lado dela
5. Clique em **Next → Finish**

### Passo 7 — Iniciar o emulador

1. No **Device Manager**, clique no botão ▶ (play) ao lado do Pixel 7
2. Aguarde o emulador iniciar completamente (primeira vez pode demorar 2–3 min)

---

## PARTE 4 — Rodar o App no Emulador

### Passo 8 — Verificar as imagens nos assets

Antes de compilar, confirme que as imagens foram copiadas:

- No Android Studio, expanda: `app → src → main → assets → wallpapers`
- Você deve ver os arquivos `.webp` listados

> ⚠️ Se a pasta `wallpapers` estiver vazia, volte à Parte 1 e rode o script Python.
> Sem as imagens o app abre normalmente mas o wallpaper mostrará o placeholder.

### Passo 9 — Executar o app

1. No topo do Android Studio, selecione o **Pixel 7** no dropdown de dispositivos
2. Clique no botão **▶ Run** (ou pressione `Shift+F10`)
3. O app vai compilar e instalar no emulador automaticamente

### Passo 10 — Ativar o Live Wallpaper no emulador

O Live Wallpaper **não aparece sozinho** — precisa ser ativado manualmente:

**Opção A (pelo app):**
1. No app Atmosfera que abriu, toque em **"Definir como Wallpaper"**
2. O Android vai abrir o seletor de wallpapers
3. Role até encontrar **"Live Wallpapers"**
4. Toque em **"Atmosfera – Clima ao Vivo"**
5. Toque em **"Definir papel de parede"**

**Opção B (pelo emulador diretamente):**
1. Pressione Home no emulador para ir à tela inicial
2. Segure um espaço vazio da tela
3. Toque em **"Wallpapers"**
4. Toque em **"Live Wallpapers"**
5. Selecione **"Atmosfera"**

### Passo 11 — Conceder permissão de localização

Na primeira execução o app pedirá permissão de localização:
- Toque em **"Permitir apenas durante o uso"**

> No emulador, a localização padrão é Mountain View, CA (EUA).
> Para testar com coordenadas brasileiras, vá em:
> `Extended Controls (⋮) → Location → Set Location`
> e insira: Latitude **-25.3947**, Longitude **-51.4528** (Guarapuava, PR)

---

## PARTE 5 — Verificar o Funcionamento

### Como testar diferentes climas

Como o Open-Meteo retorna o clima real, para simular outras condições:

1. Abra o arquivo `WeatherRepository.kt`
2. Localize a função `fetchWeather`
3. Antes do `return Result.success(state)`, force uma condição:

```kotlin
// Descomentar para testar:
// val state = WeatherState(
//     condition = WeatherCondition.STORM,
//     period = DayPeriod.NIGHT,
//     temperatureCelsius = 14.0,
//     feelsLikeCelsius = 11.0,
//     description = "Tempestade"
// )
```

### Logs do Live Wallpaper

Para ver os logs do serviço em tempo real:
1. Vá em **View → Tool Windows → Logcat**
2. Filtre por: `AtmosferaEngine` ou `WeatherRepository`

---

## PARTE 6 — Gerar APK para Testes

### Passo 12 — Build do APK de debug

1. Vá em **Build → Build Bundle(s) / APK(s) → Build APK(s)**
2. Aguarde a compilação
3. Clique em **"locate"** no toast que aparecer
4. O APK estará em: `app/build/outputs/apk/debug/app-debug.apk`

### Instalar em um celular físico

```bash
# Via ADB (com USB debugging ativo no celular):
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Problemas Comuns

| Problema | Solução |
|----------|---------|
| "Gradle sync failed" | File → Invalidate Caches → Restart |
| "AAPT: error: resource not found" | Verifique se todos os drawables foram criados |
| Wallpaper aparece preto | As imagens WebP não foram copiadas para assets/ |
| "Cannot find symbol: SwipeRefreshLayout" | Adicione a dependência swiperefreshlayout no build.gradle |
| Emulador muito lento | Enable Hardware Acceleration no BIOS (Intel HAXM ou AMD-V) |
| Localização não funciona | No emulador, defina localização manual em Extended Controls |

---

## Próximos Passos (roadmap)

- [ ] Tela de configurações (escolher cidade manual)
- [ ] Animação de transição suave ao trocar de wallpaper
- [ ] Pack de imagens premium (florestas, praias, desertos)
- [ ] Widget para tela inicial (além do Live Wallpaper)
- [ ] Publicação na Google Play Store
- [ ] Monetização: app pago R$ 2,99 ou freemium com pack extra

---

*Desenvolvido com Kotlin + Open-Meteo + Gemini Imagen*
