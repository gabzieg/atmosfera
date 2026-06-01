# 🌤 Atmosfera

**Live Wallpaper Android com clima real e imagens geradas por IA**

O wallpaper muda automaticamente com base no clima atual da sua localização —
sol, nuvens, chuva, neve, tempestade — com imagens fotorrealistas geradas pelo Gemini Imagen.

## Tecnologias

| Camada | Tecnologia |
|---|---|
| App Android | Kotlin + WallpaperService |
| Clima | Open-Meteo API (gratuita, sem chave) |
| Localização | Google Play Services FusedLocationProvider |
| Imagens | Gemini Imagen 3 (pré-geradas, embutidas no app) |
| Atualização | WorkManager (a cada 30 min) |
| Cache | SharedPreferences (TTL 30 min) |

## Condições climáticas suportadas

| Código WMO | Condição | Períodos |
|---|---|---|
| 0–1 | ☀️ Ensolarado | Manhã, Tarde |
| 2 | ⛅ Parcialmente nublado | Manhã, Tarde, Noite |
| 3 | ☁️ Nublado | Manhã, Tarde, Noite |
| 45–48 | 🌫 Neblina | Manhã, Tarde |
| 51–63 | 🌦 Chuva fraca | Manhã, Tarde, Noite |
| 65–82 | 🌧 Chuva forte | Manhã, Tarde, Noite |
| 71–86 | ❄️ Neve | Manhã, Tarde, Noite |
| 95–99 | ⛈ Tempestade | Manhã, Tarde, Noite |
| 0 (noite) | 🌙 Noite estrelada | Noite |

## Como começar

Leia o **[GUIA_COMPLETO.md](./GUIA_COMPLETO.md)** — ele tem tudo, passo a passo.

## Estrutura

```
atmosfera/
├── GUIA_COMPLETO.md          ← Leia primeiro!
├── image-generator/          ← Gerador de imagens (Python)
└── android-app/              ← App Android (Kotlin)
```
