# Consulta manual de idade — diagnóstico debug

Ferramenta de ensaio Play Age Signals, disponível somente em builds **debug**.
Não aplica bloqueios etários nem altera loja, compras, acervo ou localização.
A integração usa o SDK 0.0.4 apenas em `debugImplementation`; a Activity e o
launcher vivem em `src/debug`. Release não contém essa entrada nem o SDK.

## Pré-condições

- Windows com JDK 17/Android SDK do projeto e Android conectado por USB/adb.
- Play Store e Google Play Services presentes/atualizados e conexão disponível.
- Pacote `com.terra.wallpaper`, igual ao app do Console; conta Google ativa no
  aparelho cadastrada como **testadora de licença** nessa conta desenvolvedora.
  Não basta adesão ao teste interno/fechado ou presença numa lista de convites.
- Testador adulto já autorizado para o ensaio. Não recrutar menores para esta
  consulta nem registrar e-mail, instalação, faixa real ou conta no Git/suporte.

[Documentação oficial de teste](https://developer.android.com/google/play/age-signals/test-age-signals-api#test-with-license-testers):
com testador de licença, a Play permite sideload e assinatura debug sem upload
de versão. Fora dessa condição, `APP_NOT_OWNED` pode ser um resultado esperado.
Isso não garante disponibilidade de sinal ou compartilhamento no aparelho.

## Build e instalação pelo Windows

Na pasta `android-app`, PowerShell:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug assembleRelease --console=plain
adb devices
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n com.terra.wallpaper/.debug.ConsultaIdadeLauncher
```

**Pare se a instalação indicar conflito de assinatura**, como
`INSTALL_FAILED_UPDATE_INCOMPATIBLE`. Uma instalação da Play/release geralmente
não aceita atualização com assinatura debug. Não desinstale, limpe dados ou
remova downloads para resolver isso. Outro usuário/perfil no mesmo Android não
resolve: o pacote instalado e sua assinatura valem para o aparelho inteiro. Use
outro aparelho ou emulador já autorizado, com Play Store e conta testadora de
licença, ou coordene uma instalação compatível antes do ensaio. O comando `-r`
não torna assinaturas diferentes compatíveis; mantenha o app atual intacto.

Também é possível abrir **“Terra — Idade (teste)”** na lista de aplicativos.
A Activity não faz nenhuma consulta ao abrir. Leia a explicação e toque em
**“Consultar faixa na Play”**. A Play pode mostrar seu próprio pedido de
compartilhamento; em conta supervisionada, o responsável gerencia essa escolha.

## O que observar

- `SHARED`: consulta a faixa; mostra limites inclusivos e origem quando presentes.
  Limite superior ausente com inferior presente significa faixa aberta; faixa
  ausente/inconsistente continua desconhecida. O diagnóstico não libera nada.
- `NOT_SHARED`: não consulta faixa e não repete o pedido sozinho.
- `VERIFICATION_REQUIRED`: orienta resolver a situação na Play Store.
- Estado desconhecido ou falha: mostra etapa/código, sem presumir maioridade.
- Toques repetidos durante a consulta não abrem solicitações concorrentes.
- Sair/mandar ao fundo apaga o resultado; voltar não repete um pedido de acesso.
  Se a própria interface da Play suspender a tela, uma consulta autorizada fica
  pendente até o retorno. Se a resposta etária chegar no fundo, é descartada.
  Rotacionar/fechar invalida callbacks da tela antiga, sem repetir automaticamente.

Resultado somente em memória na tela, sem exportação, logs ou identificador de
instalação. Capturas/gravação e prévia do sistema são bloqueadas por `FLAG_SECURE`.
Para evidência do ensaio, registrar apenas sucesso/falha e código técnico
sanitizado quando necessário; não anexar faixa real ou identificação da conta.

## Validação e limites

`ConsultaIdadeTest` usa fonte sintética para sequência, erros, limites e lifecycle;
não comprova recebimento de sinal real. O ensaio no Android e o comportamento
concreto do prompt Play precisam ser conferidos separadamente. Acrescentar a
consulta debug não encerra A4, não aprova texto público e não equivale à adoção
de controles para distribuição 13+.
