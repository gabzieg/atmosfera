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
Atmosfera Clima Ao Vivo
```

23 caracteres. Praticamente o mesmo nome que o Android mostra no seletor de
papel de parede (`wallpaper_label` em `strings.xml`), que hoje traz um travessão
— **divergem só na pontuação**.

**Decidido em 2026-08-28, com o custo conhecido.** Dez alternativas foram
comparadas e a recomendação técnica era `Atmosfera: Papel de Parede`, por dois
motivos que continuam valendo:

- O título é o campo de maior peso no ranking da Play, e este não contém
  nenhuma palavra da categoria. Ninguém busca "atmosfera"; busca-se "papel de
  parede animado".
- "Clima ao Vivo" pode ser lido como app de previsão do tempo, o que atrai
  usuário errado — e avaliação ruim é o eixo sensível deste mercado.

A escolha foi manter o nome mesmo assim. **A consequência prática é que a
descoberta passa a depender inteiramente das descrições**, já que o título não
carrega palavra-chave nenhuma. Ver "Em aberto" → item 5.

Verificado: existem 8+ apps chamados "Atmosfera" na Play (clube de tênis,
estúdio de yoga, gestão de condomínio), **nenhum em clima ou papel de parede** —
não há colisão na nossa categoria. Isso é checagem de nome na loja, **não de
marca registrada**: consulta ao INPI é assunto à parte.

## Descrição curta (máx. 80 caracteres)

```
Papel de parede animado: quando chove lá fora, chove na sua tela.
```

65 caracteres. **Decidida em 2026-08-28**, depois de comparar sete versões.

**Por que esta.** Ela é a única que faz as duas coisas ao mesmo tempo. As palavras
que as pessoas digitam — "papel de parede animado" — ficam **no começo**, que é
onde pesam mais; e a segunda metade entrega a imagem concreta que gruda na
cabeça. As candidatas anteriores escolhiam entre exemplificar *ou* ser
encontrada:

- *"O papel de parede que chove quando está chovendo lá fora."* (57) — memorável,
  mas sem "animado", que é modificador buscado junto.
- *"Papel de parede animado que segue o clima real da sua região."* (61) — as
  palavras certas, mas chapada, sem imagem.
- *"Uma cena viva na sua tela, que muda com o tempo lá fora."* (56) — **descartada
  de saída**: zero palavra-chave. Com o título também sem nenhuma, o app ficaria
  invisível na busca.

**Ela conserta o buraco que o título deixou.** Na listagem, as duas aparecem
coladas:

> **Atmosfera Clima Ao Vivo**
> Papel de parede animado: quando chove lá fora, chove na sua tela.

O título diz "clima", a curta diz "papel de parede animado". Uma cobre a lacuna
da outra.

Uma versão mais longa (*"…que segue a chuva, o sol e a lua da sua região."*, 71)
cobria mais termos, mas lê como lista. A descrição longa já cobre chuva, neve,
sol, lua e vento com folga, e é ela que a Play indexa por inteiro — a curta rende
mais sendo boa de ler.

## Descrição longa (máx. 4.000 caracteres)

```
Não é uma imagem. É uma cena viva.

O Atmosfera é um papel de parede animado que desenha um cenário quadro a quadro no seu celular e faz ele seguir o tempo de verdade da sua região. Se está chovendo lá fora, chove na sua tela. Anoiteceu? As janelas acendem e as estrelas saem.

O QUE A CENA ACOMPANHA
• Chuva, neve, névoa e céu nublado, conforme a previsão real
• A hora do dia — amanhecer, tarde, noite — com a luz mudando junto
• O nascer e o pôr do sol da sua região
• A temperatura e o vento

COMO FUNCIONA
Escolha um cenário, escolha a arte dele e escolha o estilo dos efeitos. Depois é só aplicar como papel de parede: a cena passa a rodar na sua tela inicial e na tela de bloqueio. Dá para trocar quando quiser, quantas vezes quiser, sem perder nada do que já comprou.

CENÁRIOS E ESTILOS
Uma cabana na floresta, uma vila norueguesa nos fiordes, um jardim japonês, um farol, um pântano, uma praia tropical. Cada cenário vem em mais de uma arte — pixel art, argila, aquarela, ukiyo-e e outras — e você escolhe separadamente o estilo dos efeitos. A combinação é sua.

O QUE É GRÁTIS
A Cabana na floresta é gratuita, com o clima real, o ciclo de dia e noite, as estrelas, as janelas acesas e a neve caindo.

O QUE É PAGO
• Premium: uma compra única que liga os efeitos vivos em todos os cenários — raios, rajadas de vento, vagalumes, fumaça de chaminé, acúmulo de neve, estrela cadente, lampiões acendendo e fases da lua.
• Cenários avulsos: compre só o que você quiser, um a um.

SEM ASSINATURA. SEM ANÚNCIO.
Compra única de verdade: você paga uma vez e acabou. Não há mensalidade, não há renovação e não existe anúncio em lugar nenhum do app — nem na versão gratuita.

PARA QUEM É
Para quem gosta de tela calma e quer um papel de parede que muda sozinho, sem precisar mexer. Se você já achou bonito ver a chuva pela janela, a ideia é essa.

Uma coisa que é justo dizer antes de você instalar: o Atmosfera NÃO é um app de previsão do tempo. Ele usa o clima para desenhar a cena, mas não mostra previsão para os próximos dias, alertas, radar nem mapas. Se é previsão que você procura, este não é o app.

SOBRE A SUA LOCALIZAÇÃO
O Atmosfera usa a localização aproximada apenas para consultar a previsão do tempo. Não há cadastro, não há conta e não há rastreamento. E se você preferir não dar a permissão, o app funciona igual, com o clima de uma cidade padrão.

REQUISITOS
Android 8.0 ou mais recente. A internet é usada só para atualizar o clima de tempos em tempos — e, se você ficar sem conexão, a cena continua rodando normalmente com a última informação recebida.
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
| "Android 8.0 ou mais recente" | `minSdk 26` em `app/build.gradle` |
| "sem conexão, a cena continua rodando" | `weather/WeatherCache` guarda a última leitura; o motor desenha a partir do `SceneState`, sem depender de rede |
| "sem perder nada do que já comprou" | Compras são não-consumíveis (`INAPP`) e `Cena`/`ArteFundo`/`EstiloEfeito` são prefs locais — trocar de cenário não revoga posse |

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

### ~~1. Qual descrição curta~~ — resolvido em 2026-08-28

O impasse era entre memorabilidade e descoberta. Resolvido escrevendo uma versão
nova que faz as duas: palavras-chave na frente, imagem concreta atrás. Ver a
seção "Descrição curta" acima.

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

### ~~5. A descrição longa não contém "papel de parede"~~ — resolvido em 2026-08-28

O texto longo não usava a expressão uma única vez: falava em "cenário", "cena" e
"celular". Com o título sem palavra da categoria, isso deixava o app quase
invisível para quem busca o termo que as pessoas de fato digitam.

Corrigido na expansão do texto: a expressão agora aparece no primeiro parágrafo
(antes da dobra, que é o trecho que mais pesa), em "COMO FUNCIONA" e em "PARA
QUEM É".

### 4. Confirmar "fases da lua" como Premium — **com o Rafael** (2026-08-28)

É a única afirmação da descrição que **não foi verificada direto no motor** — foi
deduzida da lista `EFEITOS_PREMIUM` em `ui/PremiumScreen.kt`. Se o tier grátis já
mostrar as fases, a descrição está errada nos dois lugares onde toca no assunto.

Deixou de ser uma checagem de código e virou decisão de produto: o Rafael vai
definir o que é grátis e o que é pago entre as fases da lua e os demais efeitos.
Só ajustar o texto depois que a linha estiver traçada — mudar agora é retrabalho.
