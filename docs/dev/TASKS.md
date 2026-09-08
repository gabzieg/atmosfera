# Atmosfera — Tasks (fase atual)

> Estado do trabalho em andamento, literalmente agora. Diferente do
> `ROADMAP.md` (fases do produto até publicar), isto é o dia a dia — o que
> está pela metade, o que decidir, o que testar. Atualizar sempre que algo
> muda de status, não deixar ficar mentiroso.

**Última atualização:** 2026-08-08

## Estado do ambiente (confirmado 2026-08-08)

Árvore limpa, `main` sincronizada com `origin/main`. A keystore de produção
foi perdida e regenerada em 2026-08-04 (máquina nova, nada publicado ainda —
troca gratuita); JDK 17 (Temurin) e o SDK Android foram reinstalados na
mesma sessão. Confirmado hoje, numa rodada limpa:
`testDebugUnitTest lintDebug assembleDebug` juntos (gate da CI) e
`bundleRelease` assinado com a keystore atual (`keytool -printcert -jarfile`
bate com o certificado de 04/08) — ver `ROADMAP.md` Fase 1.

## Em andamento

- [x] **Reconciliar docs com o código (dívida da mudança de intervalo).**
  A tabela de rastreio do `CHECKLIST_PUBLICACAO.md` manda corrigir a política
  no mesmo PR em que `BootReceiver`/`WeatherWorker` mudam — e eu tinha deixado
  passar.
  - `PRIVACIDADE.md` + espelho HTML + asset: 3 trechos que afirmavam "a cada 30
    minutos" agora dizem que o intervalo é escolhido pelo usuário (15/30/60,
    padrão 30). Data atualizada. **Versão mantida em 1.0** de propósito — o
    documento nunca foi publicado (`Vigente desde` ainda é `[PREENCHER]`), então
    subir pra 1.1 sugeriria um 1.0 público que mudou. Se o Willian preferir
    bumpar mesmo assim, é decisão dele.
  - Tabela de rastreio: linha do `WeatherCache` corrigida, `IntervaloClima.kt`
    adicionado, e nova linha ligando `BillingManager` (`INAPP`, sem `SUBS`) ao
    §5.1 dos Termos ("não há assinatura") — vender assinatura passa a invalidar
    os dois documentos.
  - Item obsoleto do `AdMobBannerPlaceholder` removido (o código já não existe).
  - "Link pra política dentro do app" marcado como feito, com a nota de que
    funciona offline e não depende da URL pública.
  - Termos e Contato entraram no checklist no mesmo formato que o Willian usou
    pra privacidade: seção de placeholders agora cobre os três de uma vez
    (incluindo foro, que só existe nos Termos), seção de hospedagem cita as três
    páginas, e a de revisão jurídica ganhou os pontos específicos dos Termos.
  - Seção nova explicando as **três cópias** de cada documento (canônica `.md`,
    web `docs/`, asset do APK) e que o teste de sincronia só cobre as duas
    últimas — o `.md` continua sendo responsabilidade de quem edita.

  Gate verde. Conferido que as 3 cópias batem byte a byte e que não sobrou
  nenhum "30 minutos" hardcoded.

- [x] **Bug: a opção de 15min não fazia nada** (`weather/`, `service/`, `ui/`).
  `WeatherWorker` só busca clima `if (cache.isStale(...))`, e `isStale` usava
  `CACHE_TTL_MS` fixo em 30min — escolhendo 15min o worker acordava, o cache
  ainda contava como fresco, e a busca era pulada. Passei o teste visual sem
  pegar porque só confirmei que o app não travava ao trocar o valor, nunca que
  a troca **produzia efeito**.
  - Novo `weather/IntervaloClima.kt` — `object` sobre `SharedPreferences`
    (padrão de `Plano`/`Cena`), centraliza a chave que estava repetida em 3
    arquivos e deriva o TTL do intervalo escolhido (90% do período: se fosse
    igual, a checagem cairia na fronteira e um atraso de ms pularia o ciclo).
  - `WeatherCache.isStale` agora recebe o TTL; 2 call sites atualizados
    (`WeatherWorker`, `AtmosferaWallpaperService`).
  - `IntervaloClimaTest` trava o invariante. **Verificado que pega a
    regressão**: revertendo o TTL pro valor fixo, 2 testes falham.

- [x] **Empacotamento das páginas legais — task Gradle trocada por cópia física
  + teste de sincronia.** A task `copiarPaginasLegais` quebrou de 3 formas:
  (1) `lintAnalyze` lia o diretório sem declarar dependência, quebrando
  `lint + assemble` juntos; (2) apontar o `srcDir` pra própria TaskProvider
  deixou o **build verde com o APK sem os assets**; (3) via provider de
  `destinationDir`, a task parou de rodar. Abandonada.
  Agora: HTML real em `app/src/main/assets/legal/` +
  `PaginasLegaisSincronizadasTest` falhando se divergir de `docs/`. As duas
  pastas viraram `inputs` da task de teste — sem isso o Gradle marcava
  UP-TO-DATE e o teste nem rodava (falso verde local). **Verificado**: com
  divergência proposital o gate falha sem precisar de `--rerun-tasks`.
  Efeito colateral bom: `build.gradle` volta a ficar quase intocado (só a
  dependência de ícones + os `inputs`).

- [x] **Ajustes: unidade de temperatura + intervalo de atualização do clima**
  (`ui/SettingsTab.kt`, `ui/MainViewModel.kt`, `ui/HomeTab.kt`,
  `weather/BootReceiver.kt`, `ui/MainActivity.kt`). `WeatherWorker.schedule()`
  agora aceita o intervalo e usa `ExistingPeriodicWorkPolicy.UPDATE` (era
  `KEEP` fixo em 30min); `MainActivity`/`BootReceiver` leem o intervalo salvo
  em vez do default, senão cada abertura do app resetava pra 30min. Escopo
  decidido com o usuário via plan mode (comparação com 5 apps concorrentes em
  `C:\Users\gbrus\Downloads\teste`): sem link de privacidade nesta rodada
  (política já existe em `PRIVACIDADE.md`, falta só publicar — decisão do
  usuário), sem economia de bateria (dependeria do motor, fora de escopo).
- [x] **Ajustes com escopo completo (3ª rodada)** — o usuário apontou que as
  2 rodadas anteriores só tinham "Restaurar compras" + "Exibir em Fahrenheit",
  e que **Fahrenheit não faz sentido**: público inicial é brasileiro. Removido
  de `MainViewModel`/`HomeTab`/`SettingsTab` (temperatura volta a ser sempre
  °C). Adicionado, tudo baseado nas referências `IMG_2161`/`IMG_2162`:
  - **Limpar cache** — limpa só o `cacheDir` (cache do WebView, temporários);
    **não** limpa o `WeatherCache` de propósito: apesar do nome, é estado
    funcional, e apagá-lo deixaria quem está offline sem clima. Tamanho
    aparece na própria linha, como na referência.
  - **Como funciona** (`ui/TutoriaisScreen.kt`) — FAQ expansível com 10
    perguntas escritas a partir do comportamento real do app (fallback de
    Guarapuava, compra única sem assinatura, cenário × arte × estilo,
    restrição de bateria de fabricante).
  - **Termos de Uso** — `TERMOS.md` (canônico) + `docs/termos/index.html`
    (espelho), redigidos do zero. **Rascunho não revisado juridicamente** e
    com `[PREENCHER: …]` (desenvolvedor, e-mail, foro, data), igual ao
    `PRIVACIDADE.md`.
  - **Contato** — `CONTATO.md` + `docs/contato/index.html`. Não é `mailto:`
    por decisão do usuário: a página também explica o uso de dados de quem
    escreve (o que a política de privacidade não cobre, por tratar do app).
  - **Política de Privacidade** — passa a abrir no app, sem depender da URL
    pública existir.
  - `ui/LegalWebViewScreen.kt` — WebView sobre asset local, JS desligado,
    navegação presa a `file:///android_asset/`. **`allowFileAccess` precisou
    ficar ligado**: com ele desligado a página vinha em branco, apesar da doc
    do Android dizer que `android_asset` continua acessível.
  - Task Gradle `copiarPaginasLegais` (`app/build.gradle`) copia
    `docs/<pagina>/index.html` pros assets a cada build — evita uma terceira
    cópia manual do mesmo texto pra manter em sincronia.
  - `SettingsTab.kt` ganhou navegação interna própria (padrão da Loja) e 5
    grupos: Cenário / Geral / Ajuda / Informações / Sobre.

  - **Correção de CSS responsivo** nos 3 HTML (`docs/*/index.html`): as
    tabelas chave/valor tinham `min-width:30rem` e ficavam **cortadas na
    horizontal** na WebView (valor ilegível, ex. "[PREENCHER: e-mail de c…").
    Media query `max-width:34rem` empilha th/td em blocos. Corrige também a
    página de privacidade já existente, na web e no app.

  `assembleDebug` verde. **Verificado visualmente no emulador**: lista com os
  5 grupos, tamanho real do cache na linha (22 KB), FAQ expandindo, e as 3
  WebViews (Privacidade, Termos, Contato) renderizando com tema escuro e sem
  corte horizontal.

- [x] **Redesign de `SettingsTab.kt` — lista de linhas agrupadas** (2ª
  rodada, usuário rejeitou o primeiro layout em card-com-parágrafo e pediu o
  padrão de `IMG_2161`/`IMG_2162` em `C:\Users\gbrus\Downloads\teste`: rótulo
  pequeno maiúsculo acima de um cluster arredondado, linhas ícone+rótulo+
  trailing switch/valor/chevron, sem parágrafo nem botão de largura total).
  Componentes novos: `GrupoAjustes`, `LinhaAjuste`, `SeletorIntervaloDialog`
  (troca as pílulas 15/30/60min por um diálogo de escolha única, ativado pela
  linha "Atualizar clima"). **Adicionou dependência nova**:
  `androidx.compose.material:material-icons-extended` (`libs.versions.toml` +
  `app/build.gradle`) — gerenciada pelo mesmo BOM do Compose já em uso, não
  mexe nas versões travadas de Kotlin/Billing, mas **toca `build.gradle`,
  área de risco pela `abrir-pr`** — exige PR. Verificado visualmente no emulador — layout bate
  com a referência, diálogo de intervalo funciona, prefs persistem entre
  navegações.

- [x] **Preview ao vivo do motor + confirmação antes de aplicar wallpaper**
  (`ui/components/EngineLivePreview.kt`, `ConfirmarWallpaperDialog.kt`,
  `SceneDetailScreen.kt`, `HomeTab.kt`). Verificado no emulador (o diálogo
  mostrou o motor desenhando chuva noturna real por cima do `SceneThumbnail`)
  e **commitado** em `08bd72f`.

- [x] **Banner Premium menciona compra avulsa** (`StoreTab.kt`,
  `PremiumBanner`) — verificado no emulador, commitado em `08bd72f`.

- **Itens avaliados e descartados por ora** (2026-07-30, pedido do Gabriel de
  listar melhorias de front): permissão de localização com contexto antes do
  prompt do sistema já existe (`PermissionOnboardingCard` em `HomeTab.kt`,
  nenhuma mudança necessária); prova social (nota/avaliação) não tem dado
  real pra mostrar ainda, adiado pro pós-lançamento; link de política de
  privacidade na tela de Ajustes fica de fora até a URL existir (Fase 3 do
  `ROADMAP.md`) — linkar agora seria link morto; loading do preview ao vivo
  já é coberto honestamente pelo `SceneThumbnail` por baixo, não achei
  lacuna real aí.

- [ ] **Reestruturação de docs inspirada no workflow do Chris Titus**
  (`SPEC.md`, `ROADMAP.md`, `TASKS.md` — este arquivo). Commitados em
  `02360bf`; falta a 1ª rodada de uso real pra validar se o formato cola com o
  time (Rafael, Willian) antes de virar convenção fixa.

- [x] **`.github/dependabot.yml`** — commitado em `4be1ac2` (Gradle + GitHub
  Actions, semanal, PR próprio passando pelo gate). **Ainda não pushado**:
  o commit está local na branch `chore/github-config-billing-dependabot`, e o
  Dependabot só liga de verdade quando o arquivo chegar na `main`.

- [x] **Passo de revisão do diff (`/code-review`) no `abrir-pr`** (seção 3.5)
  — commitado em `3cda685`. É autorrevisão (mesmo modelo), não substitui
  revisão humana em área de risco; limite documentado na própria seção.

## Decisões tomadas nesta sessão (não re-abrir sem motivo novo)

- Análise de concorrência concluída (`analise-concorrentes-atmosfera.pdf`) —
  preço recomendado: cenário avulso R$ 9,90–19,90, Premium R$ 39,90–59,90
  (vitalício, não assinatura — `BillingManager` só suporta `INAPP`).
- Sem anúncios no lançamento — maior brecha de mercado encontrada é
  justamente a fadiga de anúncio dos concorrentes.
- `AdPlaceholder` removido do `MainScreen.kt` (código morto).
- `fiordes` escondido da Loja via filtro no front (`StoreTab.kt`,
  `SEM_ASSET_PUBLICADO`) — sem editar `engine/Catalogo.kt`.
- CLAUDE.md/AGENTS.md do Chris Titus revisado — nosso `CLAUDE.md` e skills
  (`run`, `abrir-pr`) mantidos como estão, já mais tailored que o
  equivalente genérico dele. Não copiar estrutura dele onde já temos.
- CodeQL descartado (não é pendência) — repo privado + plano free não
  permite rodar sem GitHub Advanced Security. Ver `ROADMAP.md`.

## Setorização (quem toca o quê)

Fonte completa em [SPEC.md](SPEC.md) → arquitetura e "Publicação na Play
Store"; mapeamento executável em `.github/CODEOWNERS`.

| Setor | Dono | Estado |
|---|---|---|
| `engine/` + `assets/atmosfera/` | Rafael | Congelado, sem trabalho aberto |
| `ui/`, `weather/`, `service/` | Gabriel | Ativo |
| `billing/` | Gabriel (era do Willian) | **Integração pronta** — Billing 9.1.0, preços reativos, reconciliação de reembolso. Falta cadastrar os produtos no Play Console |
| Documentos legais | Willian | Textos prontos, faltam os `[PREENCHER]` |
| Data Safety Form | Gabriel (+ Willian conferindo) | **Em andamento** |
| Content Rating (IARC) | Quem abrir o console | Não iniciado |
| Conta Play Console | Gabriel | Não delegável |
| Site de apresentação | Willian | Repo próprio, não criado |

## Bloqueado, esperando o usuário

- [ ] **Mover a senha da keystore pra um gerenciador de senhas e apagar o
  `.txt`.** A senha já é forte (28 caracteres, gerada na regeneração de
  2026-08-04) — o que falta é só parar de deixá-la em texto puro em
  `C:\Users\gbrus\Chaves\SENHA-LEIA-E-APAGUE.txt`. Ação do usuário: mover pro
  gerenciador e apagar o arquivo (Claude Code evita ler/manipular esse
  arquivo por princípio de segredo).
- [ ] **Backup da keystore** — dois lugares offline. Perder a chave de upload
  exige reset via suporte do Google Play (dias de espera), ainda que com Play
  App Signing não seja definitivo como o README sugere.
- [ ] **Abrir a conta do Google Play Console** (~US$25, aprovação em dias) —
  não delegável (`SPEC.md` → "Publicação na Play Store"), maior lead time do
  caminho crítico, bloqueia Fases 2 a 4.
- Criar produtos no Play Console (Fase 2) — precisa da conta acima.
- Ligar o emulador `Pixel_8` pra eu conseguir testar visualmente o preview
  ao vivo.

## Backlog (não é a fase atual, não puxar sem avisar)

- Dependabot + CodeQL no CI (identificado, aguardando liberação de commit).
- Módulos Gradle `:engine`/`:app` (precisa alinhar com Rafael antes).
- Setor do Willian — site em repo próprio, ainda não criado.
- Expandir cobertura de teste além do mapeamento de clima (hoje 5 casos).
