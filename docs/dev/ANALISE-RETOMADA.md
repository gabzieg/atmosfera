> Registro historico, versionado em 11/10/2026 para consulta. Propostas, valores, fornecedores e pendencias abaixo refletem a epoca do documento; nao substituem o [checklist atual](CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md) nem as fontes canonicas de docs/legal. Nao importar estes rascunhos como documentos atuais do app/site.

# Análise de retomada — 2026-08-05

> Gerado numa sessão de diagnóstico + retomada de ambiente. Não substitui
> `SPEC.md`/`ROADMAP.md`/`TASKS.md` — é um snapshot pontual, com citação de
> arquivo e linha pra cada afirmação de fato. Mantido local na sessão original; versionado em 11/10/2026 por solicitação atual do usuário.

## 1. O que o projeto é e onde parou

Live wallpaper Android que desenha uma cena animada reagindo ao clima real
(Open-Meteo) — chuva, neve, nuvens, sol/lua, vento, névoa, fumaça de chaminé —
via motor de partículas próprio em `Canvas`, não vídeo/imagem
([docs/dev/SPEC.md:11-15](SPEC.md#L11-L15)). App companion em Compose
(Início/Loja/Ajustes) pra ativar o wallpaper e vender Premium + cenários
extras via Google Play Billing (mesma referência).

Onde parou, segundo `docs/dev/ROADMAP.md`:

- **Fase 0 — Infra de qualidade**: marcada ✅ concluída
  ([ROADMAP.md:9](ROADMAP.md#L9)).
- **Fase 1 — Build de release assinável**: marcada ✅ concluída em 2026-08-01
  ([ROADMAP.md:24](ROADMAP.md#L24)) — mas a keystore que essa fase declarava
  pronta **não existia mais nesta máquina** no início desta sessão (seção 2).
  Foi regenerada durante a sessão (seção 4).
- **Fase 2 — Billing testável ponta a ponta**: ⏳ não iniciada
  ([ROADMAP.md:44](ROADMAP.md#L44)), bloqueada pela Fase 1
  ([ROADMAP.md:55-56](ROADMAP.md#L55-L56)).
- **Fase 3 — Compliance de publicação**: 🔄 em andamento
  ([ROADMAP.md:58](ROADMAP.md#L58)) — é a fase corrente na prática.
- **Fases 4 e 5** (ficha da loja, teste fechado → produção): não iniciadas.

Build de verdade testado nesta sessão (seção 4): `testDebugUnitTest` e
`installDebug` passam. `lintDebug` e `bundleRelease` **não foram confirmados
verdes** depois dos consertos de ambiente (seção 3).

## 2. Código vs. documentação — onde os dois divergem

Verificações feitas rodando comando real ou lendo o arquivo, não por memória.

### 2.1 Keystore de produção — ROADMAP dizia pronta, não estava

`docs/dev/ROADMAP.md:33` afirma: "Keystore de produção gerada (`keytool`, RSA
2048, validade até dez/2053) e guardada **fora da árvore do repositório**
(`%USERPROFILE%\Chaves\`)". No início desta sessão, `%USERPROFILE%\Chaves\`
**não existia** — busca recursiva por `*.jks`/`*.keystore` em `C:\` e `D:\`
não encontrou nada além de um `debug.keystore` de template React Native de
outro projeto (`ReadAfter`), sem relação com o Atmosfera. O item de backup em
duplicidade, "Backup da keystore — dois lugares offline"
([docs/dev/TASKS.md:230](TASKS.md#L230)), também nunca tinha sido marcado
como feito.

`android-app/keystore.properties` (gitignored,
[.gitignore:48](../../.gitignore#L48)) apontava pra
`C:/Users/gbrus/Chaves/atmosfera-release.jks` — arquivo inexistente,
confirmando que o `assembleRelease`/`bundleRelease` não conseguiria assinar
nada nesta máquina até a chave ser recriada.

**Estado após esta sessão**: keystore regenerada em
`%USERPROFILE%\Chaves\atmosfera-release.jks` (RSA 2048, SHA256withRSA,
validade 10.000 dias, alias `atmosfera`, confirmado com
`keytool -list -v`), senha nova e forte (28 caracteres, alfanumérica +
símbolos) gravada em `android-app/keystore.properties` e isolada em
`%USERPROFILE%\Chaves\SENHA-LEIA-E-APAGUE.txt` para você mover a um
gerenciador de senhas. **A senha nunca foi impressa no terminal nem nesta
conversa** — só uma vez, por engano, ao ler o `keystore.properties` de volta
para conferir o path (a keystore gerada naquele momento foi descartada e
regenerada do zero por precaução).

### 2.2 `local.properties` — path correto, mas SDK físico ausente até o usuário instalar

`CLAUDE.md:36-37` afirma: "SDK Android local: `android-app/local.properties`
(`sdk.dir`) — já configurado nesta máquina, não precisa de `ANDROID_HOME`".
`android-app/local.properties:1` contém
`sdk.dir=C\:\\Users\\gbrus\\AppData\\Local\\Android\\Sdk` — o path em si
estava correto (idêntico ao path final, ver seção 4), mas **o diretório não
existia no disco** no início da sessão: `Test-Path` retornou `False`, e busca
recursiva por `adb.exe`/`platform-tools` em `C:\` e `D:\` não encontrou
nenhuma instalação de SDK. `./gradlew testDebugUnitTest` falhava com "SDK
location not found" citando exatamente esse arquivo.

**Não é mais divergência**: o usuário instalou o SDK via Android Studio
durante a sessão, incluindo a SDK Platform API 34 (confirmado:
`android-app/app/build.gradle:20` fixa `compileSdk 34`,
`android-app/app/build.gradle:25` fixa `targetSdk 34`; hoje
`platforms/` no SDK contém `android-34` e `android-37.0`). A afirmação do
CLAUDE.md volta a ser verdadeira agora, mas não era no momento em que este
diagnóstico começou.

### 2.3 JDK local — Java 25 no PATH, projeto exige 17, CLAUDE.md não cobre isso

`CLAUDE.md` não faz nenhuma afirmação sobre versão de JDK — é uma lacuna, não
uma contradição. Mas a CI trava explicitamente
`java-version: '17'`/`distribution: temurin`
([.github/workflows/build.yml:27-28](../../.github/workflows/build.yml#L27-L28))
com `gradle-version: '8.6'`
([.github/workflows/build.yml:33](../../.github/workflows/build.yml#L33)) —
mesma versão do wrapper local
([android-app/gradle/wrapper/gradle-wrapper.properties:3](../../android-app/gradle/wrapper/gradle-wrapper.properties#L3)).
O único JDK presente nesta máquina no início da sessão era o JBR embutido no
Android Studio (`C:\Program Files\Android\Android Studio\jbr`), que reportou
`openjdk version "25.0.2"` — Gradle 8.6 rejeita com `BUG! exception in phase
'semantic analysis' [...] Unsupported class file major version 69`.
`JAVA_HOME` estava `unset`.

**Estado após esta sessão**: Temurin JDK 17.0.20 instalado via `winget`
(`C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot`), `JAVA_HOME`
setado a nível de usuário no Windows. **Ressalva**: variáveis de ambiente
persistidas no registro do Windows só valem pra processos novos — nesta
sessão precisei re-exportar `JAVA_HOME` manualmente em cada chamada de
`gradlew`, porque a shell que o Bash/PowerShell tool reusa já tinha herdado o
ambiente antigo. Uma sessão de terminal nova (ou reiniciar o Android Studio)
deve pegar o valor persistido sem precisar disso.

### 2.4 `TASKS.md` — auto-descrito como podendo "ficar mentiroso", e estava

`docs/dev/TASKS.md:1-6` avisa explicitamente: "Atualizar sempre que algo muda
de status, não deixar ficar mentiroso." Mas
[TASKS.md:10-19](TASKS.md#L10-L19) afirma (datado de 2026-08-01) que havia
"**30 arquivos** (18 modificados, 12 novos)" sem commit e um commit local não
pushado (`4be1ac2`) na branch `chore/github-config-billing-dependabot`.

No início desta sessão, `git status --short` voltou vazio (árvore limpa) e
essa branch **não existe** no repo local nem remoto (`git branch -vv --all`
não a lista). Ou o trabalho descrito já foi consolidado por outro caminho
(commits posteriores como `1963b29`/`206834f` chegaram à `main` via PR,
conforme `git log --oneline`), ou o texto nunca foi atualizado depois do
merge. De qualquer forma, a seção está desatualizada e deveria ser reescrita
ou removida — não reflete o estado real do repo nesta data.

### 2.5 `LICENSE_PUBLIC_KEY_BASE64` — código bate com o que os docs dizem

Sem divergência aqui, só confirmação: `ROADMAP.md:51` e `SPEC.md:44` listam
"colar/preencher `LICENSE_PUBLIC_KEY_BASE64`" como pendência. O código confere
— `BillingManager.kt:40` define
`private const val LICENSE_PUBLIC_KEY_BASE64 = ""` (vazia), e
`BillingManager.kt:108-109` mostra que, vazia, a verificação de assinatura é
pulada (`assinaturaValida` retorna `true` sem checar nada). Ainda bloqueado
em "esperando conta no Play Console" — não é algo a corrigir no código agora.

### 2.6 Higiene de git — branches órfãs, sem relação com código/doc mas achado durante a sessão

`main` local estava 1 commit atrás de `origin/main` (`git status --short
--branch` mostrava `[behind 1]`). Havia duas branches locais já mergeadas por
squash na `main` — confirmado por `git diff --stat <branch> origin/main`
retornando vazio ou só o delta de commits posteriores, não conteúdo
divergente — e ambas tinham `[origin/...: gone]` no `git branch -vv`,
confirmando que o remoto já tinha apagado os branches após merge. Corrigido
nesta sessão: fast-forward pra `origin/main`, branches órfãs apagadas.

## 3. Premissas que não consegui validar

- **`lintDebug` não foi rodado com sucesso depois do JDK/SDK corrigidos.**
  Rodei `testDebugUnitTest` (passou, `BUILD SUCCESSFUL in 21s`) e
  `installDebug` (passou, `BUILD SUCCESSFUL in 2m 8s`, instalado em
  `Pixel_8(AVD) - 14`) separadamente, mas nunca os três gates da CI juntos
  (`testDebugUnitTest lintDebug assembleDebug`,
  [.github/workflows/build.yml:35-47](../../.github/workflows/build.yml#L35-L47))
  numa única rodada limpa após consertar o ambiente. `installDebug` builda a
  mesma APK que `assembleDebug` internamente (roda `packageDebug`), então a
  compilação em si está coberta — mas o **lint com baseline**
  (`app/lint-baseline.xml`, citado em
  [android-app/CLAUDE.md — comando `lintDebug`](../../CLAUDE.md)) nunca foi
  confirmado verde nesta máquina.
- **`bundleRelease` (assinatura de release) não foi confirmado de ponta a
  ponta com a keystore nova.** A primeira tentativa falhou por um bug meu (BOM
  UTF-8 no `keystore.properties` corrompendo a leitura da propriedade
  `storeFile` pelo `Properties.load` do Java — `path may not be null or empty
  string. path='null'`). Corrigi a causa (regravei sem BOM) mas fui
  interrompido antes de rodar `bundleRelease` de novo pra confirmar que
  assina de verdade.
- **Não sei se a keystore antiga (a que o ROADMAP.md dizia existir) foi
  perdida, nunca existiu nesta máquina, ou está em outro lugar não
  pesquisado** (outra máquina do usuário, pendrive, backup na nuvem). Minha
  busca cobriu só `C:\` e `D:\` desta máquina. Isso importa só se o app já
  tiver sido instalado em algum device de teste com a keystore antiga — nesse
  caso, a nova chave gera um app com fingerprint diferente, e updates não
  instalariam por cima do anterior. **Não verifiquei isso.**
- **O timeout de rede visto no logcat**
  (`WeatherRepository: Erro ao buscar clima: timeout`) — não investiguei se é
  flutuação pontual da rede do emulador ou um problema real de timeout
  configurado curto demais em `WeatherRepository.kt`. O clima carregou com
  sucesso na tela logo depois, então não bloqueou o teste visual, mas a causa
  raiz do erro isolado não foi apurada.
- **Não testei o fluxo de compra/billing**, `EngineLivePreview`, nem os
  cenários pagos na Loja além do que aparece na primeira dobra — a
  verificação visual desta sessão cobriu só Início → conceder permissão →
  Loja → Ajustes.

## 4. Próximo passo proposto, com justificativa

Ordem por dependência real, não por número de fase do ROADMAP:

1. **Rodar `testDebugUnitTest lintDebug assembleDebug` juntos, numa sessão de
   terminal nova** (pra pegar o `JAVA_HOME` persistido sem precisar
   re-exportar manualmente), e só então marcar o ambiente como
   "confirmado verde" de novo. Justificativa: é literalmente o gate que a CI
   exige antes de qualquer merge
   ([.github/workflows/build.yml:35-47](../../.github/workflows/build.yml#L35-L47))
   — sem isso, qualquer outro trabalho local corre o risco de "passar aqui"
   e falhar na CI por uma diferença de ambiente ainda não pega.
2. **Rodar `bundleRelease` até o fim e verificar a assinatura com
   `keytool -printcert -jarfile`**, do jeito que o ROADMAP.md já descrevia
   como critério de saída da Fase 1 ([ROADMAP.md:34-37](ROADMAP.md#L34-L37)).
   Justificativa: a Fase 1 está marcada ✅ mas a keystore por trás dela foi
   trocada nesta sessão — o critério de saída não foi re-verificado com a
   chave nova.
3. **Mover a senha de `SENHA-LEIA-E-APAGUE.txt` pra um gerenciador de senhas,
   apagar o arquivo, e fazer um segundo backup offline do `.jks`.**
   Justificativa: hoje a troca de chave é gratuita porque nada foi publicado;
   depois de registrar o app no Play Console isso deixa de ser verdade —
   perder a chave de novo exigiria reset via suporte do Google (dias de
   espera). É a janela mais barata que existe pra resolver isso.
4. **Reescrever `TASKS.md:10-19`** pra refletir o estado real (git limpo,
   sem branch pendente) em vez do que ficou obsoleto desde o merge.
   Justificativa: o próprio arquivo se declara fonte de verdade do "agora" e
   pede pra não ficar mentiroso ([TASKS.md:1-6](TASKS.md#L1-L6)) — deixar como
   está engana quem ler antes de mim na próxima sessão.
5. **Abrir a conta do Google Play Console (US$ 25).** Justificativa: é o
   único item do caminho crítico que não depende de nenhum outro trabalho
   técnico e tem o maior lead time (aprovação em dias) — Fases 2, 3 e 4 do
   ROADMAP.md dependem dela direta ou indiretamente
   ([ROADMAP.md:55-56](ROADMAP.md#L55-L56),
   [ROADMAP.md:79-81](ROADMAP.md#L79-L81)). Não é delegável
   ([SPEC.md:81](SPEC.md#L81)).

Itens 3 e 5 podem rodar em paralelo com 1 e 2 — não têm dependência entre si.
