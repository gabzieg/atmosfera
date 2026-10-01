# Materiais de composição — Terra

Esta pasta contém uma prévia visual de storyboard, assets reais do projeto, a timeline e fontes originais de áudio. O fluxo completo Brag ficou bloqueado na dependência Hyperframes; não há MP4 final nem validação `hyperframes check`.

Abra `index.html` em um navegador local para examinar a prévia de 20 s; use os controles de cena, reproduzir e áudio. A prévia é uma recriação baseada em código e não uma gravação do Android. Ela não é um substituto do runtime Hyperframes.

Arquivos:

- `index.html`: storyboard visual determinístico, sem dependências externas; parâmetro `?t=13` seleciona tempo para captura.
- `timeline.json`: roteiro e cues editáveis, em pt-BR.
- `generate-audio.py`: síntese original com a biblioteca padrão do Python, salva apenas em assets/audio e ../work.
- `assets/art/`: fundos originais copiados do aplicativo, sem alterações.
- `assets/audio/`: música e SFX originais, licença documentada em LICENSE-audio.txt.
- `../work/capture-preview.cjs`: captura local por Playwright já instalado, com perfil isolado e rede externa bloqueada. Aceita diretório de Playwright e executável Chromium como argumentos, sem downloads.
- `../work/verification.json`: resultados da inspeção da prévia, sem equivalência a aprovação do vídeo final.

Reprodução da síntese: `python generate-audio.py`. Não instala dependências. Antes de executar qualquer helper, revisar o arquivo e seus caminhos; só executar em uma cópia íntegra desta entrega.

Para renderizar pelo fluxo autorizado, seguir composition-brief.md após disponibilizar Hyperframes e suas cinco domain skills. Não usar npx como instalador implícito. Todos os intermediários ficam em ../work.

brag.jpg é apenas capa provisória de storyboard. Substituir pelo melhor quadro do MP4 validado e incorporar ao frame zero quando o gate Hyperframes estiver concluído.
