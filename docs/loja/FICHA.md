# Ficha da Play Store — textos

Rascunho para colar no Play Console. Escrito a partir do comportamento real do
app, não de promessa: cada afirmação abaixo tem código por trás (ver
"Verificação" no fim).

> **Revisar antes de publicar.** Descrição de loja é peça de marketing e de
> compliance ao mesmo tempo: afirmação que o app não cumpre é motivo de
> rejeição e de avaliação ruim.
>
> **Há quatro decisões em aberto** — ver "Em aberto" no fim do arquivo. Uma
> delas é bloqueio, não preferência.

---

## Título (máx. 30 caracteres)

```
Atmosfera – Clima ao Vivo
```

É o mesmo nome que o Android mostra no seletor de papel de parede
(`wallpaper_label` em `strings.xml`) — vale manter idêntico para o usuário
reconhecer o app depois de instalar.

## Descrição curta (máx. 80 caracteres)

```
O papel de parede que chove quando está chovendo lá fora.
```

Alternativas, se preferir algo mais explicativo e menos direto:

```
Papel de parede animado que segue o clima real da sua região.
```

```
Uma cena viva na sua tela, que muda com o tempo lá fora.
```

## Descrição longa (máx. 4.000 caracteres)

```
Não é uma imagem. É uma cena viva.

O Atmosfera desenha um cenário quadro a quadro no seu celular e faz ele seguir o tempo de verdade da sua região. Se está chovendo lá fora, chove na sua tela. Anoiteceu? As janelas acendem e as estrelas saem.

O QUE A CENA ACOMPANHA
• Chuva, neve, névoa e céu nublado, conforme a previsão real
• A hora do dia — amanhecer, tarde, noite — com a luz mudando junto
• O nascer e o pôr do sol da sua região
• A temperatura e o vento

CENÁRIOS E ESTILOS
Uma cabana na floresta, uma vila norueguesa nos fiordes, um jardim japonês, um farol, um pântano, uma praia tropical. Cada cenário vem em mais de uma arte — pixel art, argila, aquarela, ukiyo-e e outras — e você escolhe separadamente o estilo dos efeitos. A combinação é sua.

O QUE É GRÁTIS
A Cabana na floresta é gratuita, com o clima real, o ciclo de dia e noite, as estrelas, as janelas acesas e a neve caindo.

O QUE É PAGO
• Premium: uma compra única que liga os efeitos vivos em todos os cenários — raios, rajadas de vento, vagalumes, fumaça de chaminé, acúmulo de neve, estrela cadente, lampiões acendendo e fases da lua.
• Cenários avulsos: compre só o que você quiser, um a um.

SEM ASSINATURA. SEM ANÚNCIO.
Compra única de verdade: você paga uma vez e acabou. Não há mensalidade, não há renovação e não existe anúncio em lugar nenhum do app — nem na versão gratuita.

SOBRE A SUA LOCALIZAÇÃO
O Atmosfera usa a localização aproximada apenas para consultar a previsão do tempo. Não há cadastro, não há conta e não há rastreamento. E se você preferir não dar a permissão, o app funciona igual, com o clima de uma cidade padrão.
```

---

## Verificação — o que sustenta cada afirmação

| Afirmação | Onde está no código |
|---|---|
| "desenha quadro a quadro" | `engine/EffectEngine.draw()`, `Canvas` a ~30 fps no `AtmosferaWallpaperService` |
| "segue o tempo de verdade" | `weather/WeatherRepository` (Open-Meteo), `SceneState.aplicarClima` |
| "nascer e pôr do sol" | `WeatherState.sunriseHour` / `sunsetHour` |
| "Cabana é gratuita" | `engine/Catalogo.kt` — único com `gratis = true` |
| Os 8 efeitos do Premium | `ui/PremiumScreen.kt` → `EFEITOS_PREMIUM`, lidos de `estado.premium` no motor |
| "compra única, sem assinatura" | `billing/BillingManager` usa só `ProductType.INAPP`, nenhum `SUBS` |
| "não existe anúncio" | Nenhum SDK de ads no projeto — ver `SPEC.md` → Não-objetivos |
| "localização aproximada" | Só `ACCESS_COARSE_LOCATION` no manifesto; `ACCESS_FINE` removida em 2026-08-08 |
| "funciona sem a permissão" | `weather/LocationHelper` cai para cidade padrão (Guarapuava, PR) |

## Decisões de redação

**Sem números de cenários e estilos.** Seria mais vendedor dizer "9 cenários e
24 estilos", mas esses números mudam a cada snapshot do motor e a descrição
envelheceria em silêncio. Se quiser incluir, confira antes: hoje são **9
cenários com arte publicada** e **27 pacotes de sprites** no `assets/`.

**Sem preço no texto.** Preço vive no Play Console e muda; repetir na descrição
cria duas fontes de verdade.

**A cidade padrão não é nomeada.** No app ela aparece como Guarapuava, PR — na
ficha isso só geraria dúvida em quem não é da região.

**"fases da lua" aparece só no Premium.** O tier grátis mostra a lua, mas as
fases são efeito pago (`estado.premium` no motor) — dizer o contrário nos dois
lugares seria contradição dentro da própria descrição.

---

## Em aberto — decisões do Gabriel

Quatro pontos que o rascunho não resolve sozinho. O terceiro é bloqueio; os
outros três são preferência.

### 1. Qual descrição curta

A escolhida é a mais memorável, mas é a única que **não usa "animado" nem
"wallpaper"** — pode custar em busca. As alternativas são mais explicativas e
menos marcantes. Troca direta entre memorabilidade e descoberta.

### 2. Incluir números ou não

"9 cenários e 27 estilos" vende mais do que "uma cabana, uma vila norueguesa…".
Ficou de fora porque esses números mudam a cada snapshot do motor e a descrição
envelheceria em silêncio. **Só incluir se alguém assumir mantê-los a cada
release.**

### 3. ⚠️ A descrição promete cenários que talvez não estejam à venda

O texto cita **jardim japonês, farol, pântano e praia**. A estratégia definida em
2026-08-25 é lançar com **poucos cenários** (ver `ROADMAP.md` → Fase 4). Se esses
não entrarem no lançamento, a ficha vira **promessa não cumprida** — motivo de
avaliação ruim e potencialmente de rejeição.

**Não publicar antes de definir quais cenários entram** e ajustar a lista do
parágrafo "CENÁRIOS E ESTILOS" para conter só eles.

### 4. Confirmar "fases da lua" como Premium

É a única afirmação da descrição que **não foi verificada direto no motor** —
foi deduzida da lista `EFEITOS_PREMIUM` em `ui/PremiumScreen.kt`. Se o tier
grátis já mostrar as fases, a descrição está errada nos dois lugares onde toca
no assunto.
