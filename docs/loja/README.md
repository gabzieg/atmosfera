# Ficha da Play Store — material gráfico

Capturas de tela para a listagem do Atmosfera na Google Play (Fase 5 do
[ROADMAP.md](../dev/ROADMAP.md)). Feitas em 2026-08-25 no AVD `Pixel_8`
(API 36), com o app da branch `front/kotlin-2-billing-9`.

## O que está aqui

| Arquivo | Tela | Por que está na ficha |
|---|---|---|
| `01-onboarding.png` | Onboarding, passo 1 | Cena em tela cheia com a frase que explica o produto — é o gancho |
| `02-wallpaper-aplicado.png` | Launcher do Android | O produto rodando de verdade, atrás dos ícones |
| `03-inicio.png` | Início | Clima real ligado à cena, com "Meus cenários" |
| `04-loja.png` | Loja | Variedade: quatro cenários, artes diferentes, estados "Atual" e "Bloqueado" |
| `05-detalhe-cenario.png` | Detalhe (Fiordes) | Prévia ao vivo — prova que é motor, não imagem parada |
| `06-premium.png` | Venda do Premium | Comparador grátis × vivo, a proposta da compra |

Ajustes ficou de fora de propósito: mostra cuidado, mas não vende wallpaper.

## Especificação que elas cumprem

Verificado arquivo por arquivo, não assumido:

- **1080×1920**, razão 1,78 — o teto da Play é **2:1**. Captura crua do
  `Pixel_8` sai 1080×2400 (2,22:1) e **seria rejeitada**; por isso o emulador
  é forçado pra 1080×1920 antes de capturar.
- **PNG 24-bit, sem canal alfa** — `screencap` grava com alfa, que a Play não
  aceita; cada arquivo é redesenhado em `Format24bppRgb`.
- Lado mínimo ≥ 320 px e máximo ≤ 3840 px.

## Como refazer

```bash
adb shell wm size 1080x1920      # obrigatório: 1080x2400 estoura a razão 2:1
# … capturar com screencap -p …
adb shell wm size reset          # devolver o emulador ao estado original
```

Depois converter pra 24-bit sem alfa (o `screencap` sozinho não serve).

Duas capturas exigem preparo de estado:

- **02** — o wallpaper precisa estar aplicado, e o `screencap` **não** pega a
  superfície do live wallpaper nos primeiros segundos: a primeira tentativa
  sai preta. Espere o launcher desenhar e capture de novo.
- **05** — sem conta Google no emulador o Billing não responde e o botão fica
  "Indisponível", o que faz o app parecer quebrado numa ficha de loja. É
  artefato do ambiente de teste, não do produto: ligue **Destravar cenários
  pagos (teste)** no painel de debug antes de capturar, e desligue depois.

## O que ainda falta pra fechar a Fase 5

- [ ] **Feature graphic 1024×500** — obrigatório em toda ficha. É peça de arte;
  não dá pra derivar de screenshot.
- [ ] **Ícone 512×512** — o ícone do app é 100% vetorial (`mipmap-*/*.xml`),
  sem nenhum PNG; precisa ser renderizado.
- [ ] **Descrição curta (80 caracteres) e longa (4.000)**.
