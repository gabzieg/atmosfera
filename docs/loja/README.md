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

## Ícone da ficha — `icone-512.png`

512×512, PNG 32-bit, 9 KB, **zero pixel transparente** (a Play rejeita fundo
transparente na ficha: ela renderiza por cima um preto ou branco que você não
escolheu). Conferido pixel a pixel, não assumido.

O ícone do app é 100% vetorial (`res/mipmap-*/ic_launcher.xml` → cor sólida +
`drawable/ic_launcher_foreground.xml`), sem nenhum PNG no repo. Como o vetor são
seis formas geométricas simples e o primeiro plano já preenche os 108×108 com
azul opaco (a cor de fundo `colorPrimaryDark` fica coberta), ele foi
rasterizado diretamente das coordenadas do `pathData` — sem precisar mexer no
app. Conferido contra o ícone real na gaveta de apps do emulador: mesmas cores,
mesma montanha, mesma posição do sol.

O arquivo é a versão **sem máscara** (quadrado cheio), que é o correto: a Play
aplica o próprio arredondamento de 30%.

### ⚠️ Dois problemas com o ícone, que este arquivo só reproduz fielmente

1. **O sol é cortado pela máscara do launcher.** No aparelho ele aparece como um
   pedaço de amarelo grudado na borda. O `pathData` põe o sol em x 70–94 de 108,
   e a máscara adaptativa corta tudo fora da zona central (~x 18–90). É defeito
   do vetor, não da renderização — conteúdo de adaptive icon precisa caber na
   zona segura.
2. **É um ícone genérico de template.** Montanha e sol chapados não dizem nada
   sobre um app cuja proposta é cena pintada à mão com motor de partículas — e
   vai competir na Play ao lado de apps que usam a própria arte. Um recorte da
   cabana comunicaria o produto em vez de escondê-lo.

Nenhum dos dois bloqueia o upload; os dois custam conversão.

## Feature graphic — `feature-graphic-1024x500.png`

1024×500, PNG **24-bit sem canal alfa** (a Play não aceita alfa aqui), 371 KB.

Composição: fundo no mesmo `#0D1117` do app, três ladrilhos arredondados com
cabana, fiordes e tanque, e o bloco de texto no padrão que o próprio app usa
(kicker miúdo em maiúsculas → título → tagline). A ideia é que a peça pareça o
Atmosfera, não um banner genérico: quem vê a ficha e depois abre o app reconhece
o mesmo desenho.

Os três ladrilhos existem para comunicar **variedade** — o argumento de venda é
que há cenários diferentes, e uma imagem só não diz isso. A tagline "Chove lá
fora, chove na sua tela." ecoa de propósito a descrição curta da `FICHA.md`;
repetir a mesma promessa nos dois lugares é o que faz ela grudar.

Texto e ladrilhos ficam dentro de margem de 64 px porque **a Play corta as
bordas** em algumas telas.

## Ícone — três opções para decidir

| Arquivo | O que é |
|---|---|
| `icone-512.png` | O atual: montanha e sol vetoriais. Genérico, e com o sol fora da zona segura |
| `icone-512-cabana.png` | Recorte 512×512 da cabana em escala **1:1** — zero reamostragem, mas a cabana fica pequena no tamanho real de ícone |
| `icone-512-cabana-medio.png` | Recorte 384×384 ampliado 4/3. **Recomendado**: a cabana preenche o quadro e mantém silhueta legível a 48 px |

Uma quarta variante foi gerada e descartada: recorte de 256×256 em 2×. Ficou tão
fechado que virou textura de madeira — sem silhueta, irreconhecível em tamanho
de ícone.

> **Se trocar o ícone da ficha, troque também o do app.** Hoje o ícone instalado
> é o vetor da montanha (`res/mipmap-*/ic_launcher.xml`). Ficha e aparelho
> mostrando ícones diferentes confunde quem instala — e é a Play que fica
> estranha, não o app. Isso é mudança em `res/`, área nossa, sem PR.

## O que ainda falta pra fechar a Fase 5

- [ ] Escolher entre as três opções de ícone e, se mudar, atualizar `res/`.
