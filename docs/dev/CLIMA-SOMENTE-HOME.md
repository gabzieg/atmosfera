# Clima somente na tela inicial

Implementação de 05/10/2026, baseada em main `0fa0f6b`. Candidato Android
1.0.5, versionCode 6; confirmar que esse código continua disponível no Play
Console antes de gerar o AAB assinado. Este documento não confirma publicação.

## Comportamento

O serviço do wallpaper inicia consultas de clima e localização somente quando
o Android informa que o wallpaper está visível, a tela está interativa, o
keyguard não está ativo, o engine não é uma prévia e nenhuma Activity do Terra
está aberta. A criação do serviço, o companion e as prévias apenas leem cache.
Ao voltar à home, o último estado salvo aparece antes da consulta. Cache ainda
válido ou freio da MET ativo evitam nova consulta e nova aquisição de localização.

`ClimaNaHome` controla uma sessão cancelável por engine. Ao ocultar o wallpaper,
apagar a tela ou abrir o companion, a sessão é cancelada. A elegibilidade é
verificada antes da localização/rede e após esperas. Cancelamento se propaga
até Retrofit; uma requisição já recebida pelo servidor não pode ser desfeita.
O serviço serializa consultas de engines que se sobreponham.

Os intervalos de 15/30/60 minutos valem durante a sessão visível; a duração
do cache local corresponde ao intervalo completo. Cache HTTP, identificação,
arredondamento de coordenadas e freio persistente de 429/403 são preservados.
Antes de uma atualização necessária, há uma espera aleatória cancelável de
até 15 segundos para distribuir o tráfego.

## Migração e documentação

Não há novos agendamentos de clima nem receiver próprio de boot. Ao iniciar
o processo, a Application cancela trabalhos antigos por nome único e pela
tag automática da classe. A classe WeatherWorker mantém o mesmo nome, mas
conclui sem rede/localização, inclusive se um trabalho persistido iniciar
antes do cancelamento. Outros trabalhos não são cancelados.

WorkManager permanece como dependência de compatibilidade e pode acrescentar
permissões técnicas ao manifesto mesclado. Nenhuma permissão de localização
em segundo plano foi adicionada. Termos, Privacidade, HTML offline/site,
Ajustes, tutoriais e guia do Console acompanham a regra da versão 1.0.5.
Os arquivos do site só entram no ar pelo fluxo de hospedagem; apps instalados
continuam com a cópia offline da versão que receberam.

## Verificação automatizada

Executar com JDK 17, no diretório android-app:

```text
gradlew testDebugUnitTest lintDebug assembleDebug
```

Os testes incluem elegibilidade, ciclo único, cancelamento/retorno sem
sobreposição, cancelamento HTTP real com MockWebServer, migração de trabalhos
periódicos e pontuais preservando outros trabalhos, TTL, permissões declaradas
e igualdade entre HTMLs offline e do site. Robolectric testa a migração em
Android simulado; isso não substitui validação do wallpaper em um aparelho.

## Aceite em aparelho antes do envio à loja

- Instalar sobre uma versão antiga com trabalhos agendados. Confirmar que
  nenhuma consulta parte deles após atualização ou reinicialização.
- Com cache vencido, aplicar o wallpaper e voltar à home desbloqueada:
  confirmar atualização automática sem abrir o companion. Com cache válido
  ou freio ativo, retornar repetidamente e confirmar ausência de novas consultas.
- Permanecer na home durante um intervalo: confirmar atualização sem duplicação.
- Abrir outro app, bloquear, apagar a tela e usar Always-on Display: confirmar
  ausência de novas consultas/localização. Fazer essas transições durante
  localização, espera aleatória e HTTP; depois retornar à home.
- Confirmar que companion, configurador e prévias não consultam clima/localização.
- Testar com internet indisponível e permissão aproximada negada/concedida.
  Sem localização utilizável, preservar o fallback de São Paulo.
- Testar os launchers/fabricantes suportados, inclusive apps transparentes e
  multiwindow: onVisibilityChanged descreve visibilidade do wallpaper, não
  identifica universalmente qual app está em primeiro plano. Não foram adicionados
  Usage Access, Accessibility ou coleta de histórico de aplicativos.
- Confirmar aquisição de localização aproximada durante uso do wallpaper nas
  versões Android suportadas, sem ampliar permissões para contornar restrições.

O escopo restringe consultas e ciclos de atualização climática. O comportamento
visual das prévias e da tela de bloqueio permanece baseado em dados disponíveis.
Não há push, abertura forçada do companion nem garantia de manter o processo vivo
quando o sistema o encerra.

## Referências

- [Visibilidade do wallpaper](https://developer.android.com/reference/android/service/wallpaper/WallpaperService.Engine#onVisibilityChanged(boolean))
- [Estado do keyguard](https://developer.android.com/reference/android/app/KeyguardManager#isKeyguardLocked())
- [Condições de uso da MET](https://api.met.no/doc/TermsOfService)
