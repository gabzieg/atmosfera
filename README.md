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
troca de cenário do motor — ver `HANDOFF-FRONTEND.md`).

## Estrutura do repositório

```text
atmosfera/
├── README.md                 ← este arquivo
├── CLAUDE.md                 ← contexto do projeto p/ Claude Code (comandos, convenções)
├── HANDOFF-FRONTEND.md       ← divisão motor/front + interface estável entre eles
├── GUIA_COMPLETO.md          ← como rodar o projeto (Android Studio e terminal)
├── CHECKLIST_PUBLICACAO.md   ← itens a revisar antes de publicar na Play Store
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

- **Começando agora?** → [GUIA_COMPLETO.md](GUIA_COMPLETO.md)
- **Vai mexer no companion app (UI/loja/billing)?** → [HANDOFF-FRONTEND.md](HANDOFF-FRONTEND.md)
  descreve a fronteira entre o motor (congelado) e o front, e a interface estável entre os dois.
- **Vai publicar uma versão?** → [CHECKLIST_PUBLICACAO.md](CHECKLIST_PUBLICACAO.md)
- **Usando Claude Code neste repo?** → [CLAUDE.md](CLAUDE.md) tem os comandos de build/verificação
  e as convenções do projeto pra não precisar reler tudo a cada sessão.
