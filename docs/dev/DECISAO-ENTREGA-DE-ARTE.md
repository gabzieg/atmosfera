# Decisão: entrega de arte em camadas (empacotar → PAD → R2)

Status: **decidido 2026-09-11 (Gabriel); pendente ratificação do Rafael**, dono
do subsistema de entrega (motor).

Complementa, **não substitui**, [ENTREGA-DE-ARTE.md](ENTREGA-DE-ARTE.md) e
[BUCKET-R2.md](BUCKET-R2.md): aqueles descrevem o *como* do download por R2;
este define *quando* cada mecanismo entra. Em resumo, o download por R2 fica
adiado — não descartado.

## O número que decide

Medido pelo `tools/pacote_cenas.py`, remedido em 2026-09-03 (reconferir antes de
tratar como atual — o catálogo cresce):

| | valor |
|---|---|
| Arte crua (PNG) em `assets/` hoje | **1,58 GB** — é isto que impede publicar |
| Mesma biblioteca empacotada em **WebP q92** | **266 MB** (55 cenas / 255 artes) |
| Teto do módulo base da Play | **500 MB** |
| Baixar 1 arte comprada | 0,27–2,21 MB (mediana 0,93) |

**O gargalo é o formato, não o volume.** A biblioteca inteira, empacotada, cabe
abaixo do teto. O download só se torna necessário quando o catálogo
*empacotado* — e não o PNG cru — passar de 500 MB.

## Decisão: três camadas, nesta ordem

### Camada 1 — empacotar e embutir (lançamento, e enquanto couber)
Converter PNG→WebP (`pacote_cenas.py`) e **embutir no APK**, em vez de baixar.
Traz o app de 1,58 GB para ~266 MB — publicável, **sem servidor**. A política de
privacidade ("não existe servidor do Atmosfera") continua verdadeira e o Data
Safety fica inalterado.

Fôlego: a ~5 MB empacotados por cena, os ~234 MB de folga cobrem ~45 cenas além
das 55 atuais (~100 no total) antes de encostar no teto.

**Dependência técnica (motor, Rafael):** hoje o `pacote_cenas.py` joga o WebP no
bucket e o que fica no APK é PNG cru + a cabana grátis. Embutir a biblioteca
empacotada é apontar essa saída para `assets/` e confirmar que o carregador
local lê WebP. É a única obra que a Camada 1 exige.

### Camada 2 — Play Asset Delivery (quando o empacotado passar de 500 MB)
Packs on-demand entregues pela **própria Play**. Mantém "não existe servidor do
Atmosfera" literalmente verdadeiro (quem entrega é a loja, já declarada no Data
Safety), custo/ops/cartão zero, e a posse continua sendo do billing
(`Catalogo`/`Plano`) — exatamente como o `Acervo` já assume ("compra é a fonte
da verdade, não o arquivo"). Por-arte a 0,27–2,21 MB, muito abaixo do limite de
512 MB por pack do PAD.

### Camada 3 — R2 (só se a cadência de conteúdo exigir)
O sistema que o Rafael já construiu (`Acervo` + [BUCKET-R2.md](BUCKET-R2.md)). A
vantagem única dele sobre o PAD é **desacoplar lançamento de conteúdo de
lançamento de app**: subir cenário no bucket sem update nem review da Play. O
preço: é endpoint próprio do Atmosfera, o que **quebra** o "não existe servidor"
e obriga reescrever política + Data Safety (o IP do aparelho passa a ser visível
ao CDN). Só vale se o plano for soltar conteúdo com frequência, independente das
versões do app — o que, pré-lançamento e com duas pessoas, não é o caso ainda.

## Gatilhos de transição
- **1 → 2:** biblioteca *empacotada* projetada para passar de ~450 MB (margem
  antes do teto de 500).
- **2 → 3:** necessidade real de publicar conteúdo novo sem update do app,
  aceitando conscientemente o disclosure legal.

## O que NÃO fazer agora
- Não criar bucket, conta Cloudflare nem token de API.
- Não reescrever a política de privacidade para o cenário R2 — seria descrever
  um servidor que não existe. Enquanto o lançamento for local, o texto atual
  está correto.
- O `Acervo` fica no código, **inerte** (`BASE_PADRAO = ""`), sem custo de
  manutenção até a Camada 3.
