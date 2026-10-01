# Brag Plan — Terra

Status: planejamento concluído; fluxo completo Brag bloqueado na composição Hyperframes. Este material não é um vídeo renderizado.

## Produto e evidência

Terra é um papel de parede animado para Android que reage ao clima real. O workspace conserva o nome Atmosfera, mas a marca pública atual é **Terra**, confirmada em `android-app/app/src/main/res/values/strings.xml` e `HomeTab.kt`. O código atual prevalece sobre descrições antigas do README.

Funcionalidades escolhidas e fontes:

- Clima altera o cenário: `engine/SceneConfig.kt`, `SceneState.aplicarClima`, condições de sol, chuva e neve. Mostrar somente sol e chuva, sem prometer desempenho.
- Dia/noite: `SceneConfig.kt`, `horaAtual`, e `EffectEngine.kt`, desenho de lua, estrelas e iluminação. Comprimir a passagem do tempo como demonstração; não sugerir que o clima real muda em segundos.
- Escolha de cenários e definição do wallpaper: `ui/HomeTab.kt`, `MeusCenarios`, `SetWallpaperCta`, `ConfirmarWallpaperDialog` e abertura do seletor Android. `Catalogo.kt` confirma Cabana na floresta, Jardim japonês e Campo de lavanda com suas respectivas artes gratuitas.

## Rubrica de nove perguntas

1. **O que é?** Papel de parede Android com cenário animado que reage ao clima real.
2. **Afirmação mais relevante?** “Cenário animado que reage ao clima real”, texto do próprio aplicativo.
3. **Gancho visual?** A mesma cabana em pixel art passa do céu limpo à chuva, dentro de um celular sobre o fundo monocromático do app.
4. **Qual UI mostrar?** Wallpaper/preview animado e recriação do seletor “Meus cenários” da tela Início. A miniatura de clima da Início deve permanecer estática, conforme o código atual.
5. **Duração mínima satisfatória?** 20 segundos, quatro cenas; 600 quadros a 30 fps.
6. **Tom?** Preset polished, movimento moderno e dinâmico, cortes limpos e tempo para ler.
7. **Áudio?** Trilha eletrônica ambiente original, discreta, mais dois toques e um encerramento suave. Sem voz. Não utilizar os MP3 da skill: o README dos ativos não comprova a licença.
8. **Legenda de compartilhamento?** “Terra: um papel de parede animado que reage ao clima real. Dia e noite, chuva e diferentes cenários na sua tela. Demonstração com dados fictícios.”
9. **Fluxo de uso?** Ver a cena → escolher Jardim japonês em Meus cenários → tocar Definir papel de parede. Não simular uma aplicação concluída sem mostrar o fluxo de confirmação e o seletor Android.

## Conceito

**O clima mudou. Sua tela também.** Uma única cena reconhecível transforma-se, o tempo avança e a pessoa escolhe outra paisagem. O movimento vem do produto: precipitação, iluminação e seleção. A moldura monocromática mantém a identidade; a cor fica nas artes reais.

## Formato e identidade

- Horizontal, 1920 × 1080; duração planejada 20,000 s; 30 fps.
- Fundo `#0D0E10`; superfície `#17181B`; superfície secundária `#202226`; borda `#32353A`.
- Texto `#F4F5F7`; texto secundário `#9A9EA5`; destaque por inversão clara, sem nova cor de marca.
- Tipografia original: Material 3 padrão. A prévia local usa Arial como alternativa instalada; a fonte Android exata ainda precisa de verificação no fluxo Hyperframes.
- Margem de segurança: 96 px; títulos grandes, uma ideia principal por cena; UI do celular ampliada.
- Artes: cabana/pixel, jardim/ukiyoe, lavanda/aqua, copiadas diretamente dos assets públicos do aplicativo.

## Storyboard — 4 + 6 + 6 + 4 = 20 s

### 1 — O clima mudou — 0,0–4,0 s

Título: “O clima mudou. / Sua tela também.” Marca Terra pequena. A cabana aparece no celular; entre 1,0 e 1,7 s, céu e chuva passam para o estado fictício de chuva. A identificação “Clima simulado” torna a aceleração explícita. Imagem e título assentados até 3,6 s. Entrada em 0,35 s, transição em 0,4 s.

Interação: nenhuma; condição ambiental simulada. Áudio: entrada suave da trilha, sem impacto agressivo.

### 2 — Dia e noite — 4,0–10,0 s

Título: “Do dia / à noite.” A mesma cabana passa para uma interpretação noturna, com lua e estrelas. Texto secundário: “O cenário acompanha a hora do dia.” Hora demonstrativa, sem localização real. A animação da prévia é uma recriação visual, não uma captura nem uma execução do motor Android. Segurar composição legível por pelo menos 3,0 s após a transformação.

Interação: passagem acelerada do tempo. Áudio: trilha contínua; acento suave opcional. Transição limpa para a UI.

### 3 — Escolha e aplique — 10,0–16,0 s

Título: “Escolha / seu cenário.” Subtítulo: “Veja a prévia. Defina o papel de parede.” Recriar Início com título Terra, card de clima estático, Meus cenários e CTA real. Dados fictícios: 22 °C, sensação 21 °C, vento 8 km/h, Cidade Demo. Seleção visual de Jardim japonês em 11,5 s; miniatura e borda de seleção mudam em 12,0 s. Cursor aproxima-se de “Definir papel de parede” em 14,0 s; em 14,5 s, enfatizar o botão. Cortar antes de uma confirmação do sistema; não afirmar ativação automática.

Interação: seleção e indicação do CTA. Áudio: dois toques discretos, associados à seleção e à indicação do botão. Todas as opções permanecem visíveis até o corte; não exibir um novo texto a cada batida.

### 4 — Terra — 16,0–20,0 s

Título: “Terra.” Subtítulo: “Cenário animado. Clima real.” Celular com jardim japonês; lâminas discretas de cabana e lavanda reforçam a coleção. Não exibir URL, selo de publicação, preço, contagem de usuários nem promessa de disponibilidade comercial.

Interação: nenhuma. Áudio: assinatura suave e fade-out de 18,7 a 20,0 s. Logo e imagem estáveis no encerramento.

## Áudio e cues

Trilha original gerada localmente por `composition/generate-audio.py`, somente com síntese matemática e biblioteca padrão do Python. Os WAV e o código de síntese original recebem CC0-1.0; as artes do projeto conservam seus direitos originais.

Base de 120 BPM; cues planejados em 4,0, 10,0 e 16,0 s. A grade conhecida da síntese evita análise externa. Ela é orientação, não obrigação: leitura e compreensão prevalecem. Limite de pico do mix planejado inferior a −6 dBFS; cama discreta e sem voz. Toques em 11,5 e 14,5 s; confirmação sonora de marca em 18,5 s.

Extração audio-reactive e sincronização final Hyperframes ainda não verificadas: faltam as skills e o runtime. A prévia de storyboard não usa a trilha como prova de mix final.

## Privacidade e fidelidade

Nenhuma conta, API, GPS, dispositivo ou sessão autenticada será usada. Toda execução de prévia ocorre em um navegador novo, com rede externa bloqueada e assets locais. O rodapé “Demonstração com dados fictícios” identifica a recriação. Não incluir nomes pessoais, contatos, coordenadas, métricas, depoimentos ou endpoints.

## Gate pendente

Faltam Hyperframes e as skills `hyperframes-core`, `hyperframes-animation`, `hyperframes-creative`, `hyperframes-keyframes`, `hyperframes-cli`. A Brag exige `hyperframes check` com zero erros antes de renderizar. Não substituir por brag-slim ou por exportador alternativo. `brag.mp4` e a capa extraída do melhor quadro do vídeo permanecem pendentes.
