# Hyperframes Composition Brief — Terra

## Objetivo

Produzir um filme de apresentação de 20 segundos, horizontal, 1920 × 1080, 30 fps, polished, moderno e dinâmico. Português brasileiro, música e efeitos discretos de licença adequada, sem narração. Seguir o fluxo completo Brag.

## Estado da entrega

Planejamento e materiais preparados. `composition/index.html` é uma **prévia de storyboard independente**, não uma composição Hyperframes validada. Não renderizar como entrega final por um fluxo alternativo. Adaptá-la somente depois de carregar as cinco skills Hyperframes exigidas pela Brag. `brag.jpg` é capa conceitual provisória, não quadro extraído de um MP4.

## Fontes e limites

Usar exclusivamente as evidências listadas em `brag-plan.md` e as três artes locais de `composition/assets/art/`. A marca atual é Terra; o nome Atmosfera no README é legado. Não exibir elementos antigos como tanque nem divulgar compras ou preços. Não chamar localização, clima, billing ou servidores; dados fictícios declarados.

## Direção visual

Paleta original: fundo `#0D0E10`, superfície `#17181B`, borda `#32353A`, texto `#F4F5F7`, secundário `#9A9EA5`. Seleção por borda clara e botão invertido. Preservar artes e recortes verticais; nada de cores novas para os controles. Tipografia Material 3 padrão; usar uma alternativa local documentada se a fonte Android não estiver disponível. Títulos a partir de 96 px, corpo de 32–36 px; margem 96 px.

## Roteiro vinculante

1. 0–4 s: “O clima mudou. Sua tela também.” Cabana, sol → chuva; condição simulada explícita.
2. 4–10 s: “Do dia à noite.” Mesma cabana, transformação noturna; passagem acelerada.
3. 10–16 s: “Escolha seu cenário.” Meus cenários, selecionar Jardim japonês, prévia estática muda, destacar Definir papel de parede. Cortar antes de afirmar conclusão do seletor Android.
4. 16–20 s: Terra, “Cenário animado. Clima real.” Jardim como hero; composição da coleção.

Implementar movimento determinístico e seek-safe conforme o Hyperframes atual. Entradas rápidas de 0,3–0,5 s, depois segurar. Manter rótulo de dados fictícios. A tela Início não deve mostrar a animação do motor sobre seu card de clima: o código atual usa miniatura estática.

## Áudio

Ativos originais preparados localmente: `assets/audio/terra-original.wav`, `soft-tap.wav` e `soft-signature.wav`. Síntese e mix intermediários reproduzíveis por `generate-audio.py`, sem pacotes ou rede. Licença CC0-1.0 para esses sons originais; não utilizar músicas e SFX da skill sem termos verificáveis.

Cue source: `assets/audio/cues/terra-original.music-cues.json`. Grade original de 120 BPM, grandes transições em 4, 10 e 16 s; seleção em 11,5 s; botão em 14,5 s; assinatura em 18,5 s. Music bed discreta, fade-in 0–0,8 s e fade-out 18,7–20 s. A implementação Hyperframes decide ganhos e tracks finais, sem sobrepor mix intermediário e stems. `work/audio-reference.wav` é referência de mix, não faixa adicional.

A extração audio-reactive do Hyperframes não pôde ser executada porque faltam as domain skills. Se houver suporte após instalação autorizada, permitir apenas respiração sutil de uma borda/luz de apresentação, sem equalizadores ou flashes. Pacing e leitura prevalecem sobre batidas.

## Checklist de retomada

- Disponibilizar localmente as cinco domain skills; ler as instruções atuais antes da implementação.
- Disponibilizar o pacote Hyperframes em versão exata, escolhida e autorizada antes de qualquer download; usar executável instalado diretamente, sem npx que possa baixar dependências.
- Confirmar a disponibilidade dos componentes de render local, incluindo encoder MP4/H.264 e áudio AAC. O FFmpeg existente do Playwright só expõe PNG e VP8 como encoders, sem H.264 ou AAC.
- Adaptar os materiais ao contrato atual Hyperframes e executar `hyperframes check`, sem ignorar contraste ou overflow.
- Fazer preview e inspeção visual local; usuário já autorizou renderização autônoma, não é necessária nova aprovação estética.
- Renderizar `brag.mp4`, duração 20,000 s, 1920 × 1080, 600 frames a 30 fps, áudio original, sem voz.
- Escolher melhor quadro assentado, começando pela cena 3 em 13 s; comparar com abertura e encerramento. Extrair `brag.jpg` e incorporar seus pixels somente no frame 0, preservando duração e áudio.
- Verificar corte, leitura, contagem de quadros, duração, áudio, licença, claims e ausência de informações sensíveis no MP4 final.

## Autorizações necessárias

Esta execução não autoriza downloads, instalação, navegação externa ou upload. Para concluir, o usuário pode fornecer uma instalação local de Hyperframes com as cinco skills e os componentes de render. Alternativamente, deverá autorizar primeiro a consulta à documentação oficial para identificar pacote e versão, e depois autorizar o pacote/versão específicos e eventuais componentes de render ausentes. Nenhuma dependência foi instalada.
