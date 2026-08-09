# Checklist de publicação — Google Play

> Ver também: [README.md](../../README.md) (visão geral + build de release).
>
> Este arquivo substitui o antigo `estrutura.md`, que era uma resposta
> genérica de IA sem nenhum dado real do projeto. Os itens abaixo são
> específicos do Atmosfera, levantados a partir do manifesto e do código atuais
> em `android-app/`.

## Permissões declaradas hoje (`AndroidManifest.xml`)

| Permissão | Usada onde | Justificativa pro Data Safety Form |
|---|---|---|
| `INTERNET` / `ACCESS_NETWORK_STATE` | `weather/WeatherRepository.kt` | Buscar o clima na Open-Meteo |
| `ACCESS_COARSE_LOCATION` | `weather/LocationHelper.kt` | O wallpaper reage ao clima da região do usuário — é a funcionalidade principal do app. **Só aproximada**: `ACCESS_FINE_LOCATION` foi removida em 2026-08-08 por não ter uso real (ver comentário no manifesto) |
| `RECEIVE_BOOT_COMPLETED` | `weather/BootReceiver.kt` | Reagendar a atualização periódica de clima após reiniciar o aparelho |

Nenhuma outra permissão deveria existir — se aparecer uma nova no manifesto,
confirme que tem uso real antes de publicar (o Google compara o Data Safety
Form declarado com o que o APK realmente pede).

## Dados coletados

> O texto público disso está em [PRIVACIDADE.md](../legal/PRIVACIDADE.md) — que é o
> documento que vai valer juridicamente. O resumo abaixo é a versão interna;
> se um mudar, mude o outro (ver "Política de privacidade" no fim deste arquivo).

- **Localização aproximada/precisa**: usada só localmente para consultar a
  Open-Meteo e cacheada em `SharedPreferences` (`WeatherCache`) — não é
  enviada a nenhum servidor próprio (o projeto não tem backend).
- **Nenhuma conta de usuário, nenhum identificador pessoal coletado.**
- **Compras**: processadas pelo Google Play Billing — o app não vê nem
  armazena dados de pagamento, só o resultado da compra.
- **Anúncios**: **nenhum SDK de ads/analytics está integrado**, e não há mais
  nem placeholder visual na UI (o antigo `AdMobBannerPlaceholder` foi removido
  do `MainScreen.kt` por ser código morto). A decisão de lançar sem anúncios
  está registrada em [SPEC.md](SPEC.md) → "Não-objetivos". No dia em que isso
  mudar, os itens abaixo (Data Safety Form, consentimento, política de
  privacidade) precisam ser atualizados antes do próximo release.

## Antes de publicar (fazer, não só declarar)

- [ ] **Keystore de release + `keystore.properties`** — gerar a keystore de
  produção com `keytool` e preencher `android-app/keystore.properties` (ver
  README.md → "Build de release"). Sem isso `assembleRelease` builda sem
  assinar. Guarde a keystore em local seguro fora do repo.
- [x] **Política de privacidade escrita** — [PRIVACIDADE.md](../legal/PRIVACIDADE.md)
  (texto canônico) + [`docs/privacidade/index.html`](docs/privacidade/index.html)
  (mesma coisa em HTML, pronta pra hospedar). Cobre coleta, finalidade, base
  legal LGPD, terceiros, transferência internacional, retenção, direitos do
  titular e contato.
- [x] **Termos de Uso escritos** — [TERMOS.md](../legal/TERMOS.md) (texto canônico) +
  [`docs/termos/index.html`](docs/termos/index.html). Cobrem licença de uso,
  compra única (sem assinatura), reembolso via Google Play, direito de
  arrependimento (art. 49 CDC), garantias, foro. **Rascunho não revisado
  juridicamente** — ver "Revisão jurídica" no fim deste arquivo.
- [x] **Página de contato escrita** — [CONTATO.md](../legal/CONTATO.md) +
  [`docs/contato/index.html`](docs/contato/index.html). Canais de suporte e
  privacidade + o que é feito com os dados de quem escreve (a política cobre o
  app, não o e-mail que o usuário manda).
- [ ] **Preencher os `[PREENCHER: …]`** dos três documentos (controlador/
  desenvolvedor, e-mail de contato, encarregado/DPO, URL, foro, datas de
  vigência) — ver "Política de privacidade" no fim deste arquivo. São os
  **mesmos dados** nos três; preencha de uma vez. Sem isso nada pode ser
  publicado.
- [ ] **Hospedar as três páginas e ter as URLs** — a da política vai no Play
  Console (Política do app → Privacidade) e na ficha da loja; as outras duas
  são linkadas a partir dela e dos termos.
- [x] **Política acessível dentro do app** — Ajustes → Informações → Política
  de Privacidade abre a página numa WebView sobre asset local
  (`ui/LegalWebViewScreen.kt`), então funciona offline e **não depende da URL
  pública existir**. Termos de Uso e Contato entram pelo mesmo caminho.
- [ ] **Data Safety Form** (Play Console) — declarar coleta de **localização
  aproximada** apenas. `ACCESS_FINE_LOCATION` foi **removida do manifesto** em
  2026-08-08 (não tinha uso real: o portão é `LocationHelper.hasPermission()`,
  que só checa COARSE, e a busca pede `PRIORITY_BALANCED_POWER_ACCURACY`), então
  declarar "precisa" agora seria inconsistente com o APK — e inconsistência é a
  causa nº 1 de rejeição. Finalidade "funcionalidade do app", **não**
  compartilhada com terceiros para publicidade, compartilhada com a Open-Meteo
  para a funcionalidade, criptografada em trânsito, coleta **opcional** (o app
  funciona sem permissão, com fallback pra Guarapuava/PR).
- [ ] **Content Rating Questionnaire** (IARC) — preencher no Play Console.

> **Respostas prontas para os dois formulários acima**, derivadas do código e
> com a linha que sustenta cada uma: [GUIA_PLAY_CONSOLE.md](GUIA_PLAY_CONSOLE.md).
- [ ] **Ficha da loja**: título, descrição, screenshots (usar o app real, não
  só o `wallpaper_thumbnail.png`), ícone.
- [ ] **Tamanho de `assets/atmosfera/`** — saltou de 14,7 MB pra **140 MB** na
  integração do motor novo de 2026-08-09 (6 cenários + 12 estilos, muitos com
  `frente.png`/`fundo.png` de 2-4 MB cada, mais dezenas de folhas de sprite
  experimentais em `sprites_*.png`). Tudo em `assets/` embarca em TODA variante
  do APK — não tem split por densidade/ABI como recurso teria.

  Ainda não é bloqueio confirmado (não conferi o limite atual do Play pra AAB
  sem Play Asset Delivery configurado, e o projeto não usa PAD), mas é
  desproporcional pra um wallpaper — a maioria dos concorrentes fica na casa
  de dezenas de MB, não centenas. Antes de publicar: (1) confirmar se algum
  `sprites_*.png`/pasta de cena não tem `Cenario`/`Estilo` te referenciando
  (órfão, cabe apagar) — `git ls-tree -r -l HEAD -- android-app/app/src/main/assets/atmosfera
  | sort -k4 -nr` lista os maiores; (2) avaliar Play Asset Delivery pros
  cenários pagos (baixar sob demanda em vez de embarcar todos no install
  inicial); (3) comprimir os PNGs mais pesados sem perder a arte. Decisão de
  motor (`engine/`/`assets/`) — alinhar com o Rafael antes de apagar qualquer
  asset.
- [ ] **Produtos no Play Console**: criar `atmosfera_premium` e
  `cenario_tanque` (INAPP, não-consumíveis) antes de testar compras — ver
  `Catalogo.kt` para os IDs valendo.
- [ ] **Chave de licenciamento**: colar a chave pública Base64 (Play Console →
  Monetizar → Configuração de monetização → Chave de licença) em
  `BillingManager.LICENSE_PUBLIC_KEY_BASE64` — sem ela, a verificação de
  assinatura das compras fica desligada.
- [ ] **Teste fechado** antes de produção — Google exige um período de teste
  fechado com testers reais para apps novos.
- [ ] **Medir o motor num aparelho ANTIGO de verdade** — decide se `minSdk 26`
  se sustenta. É o único teste que ainda não temos dado nenhum: tudo até hoje
  rodou em emulador Pixel 8, que é hardware moderno.

  Por que importa mais aqui do que num app comum: o Atmosfera é live wallpaper,
  desenha ~30 fps em `Canvas` continuamente, em segundo plano. Num aparelho de
  2017 (o piso do `minSdk 26`) isso pode engasgar ou consumir bateria de forma
  perceptível — e aí vira **avaliação 1 estrela**, não incompatibilidade. A
  análise de concorrência (ver [SPEC.md](SPEC.md) → "Não-objetivos") aponta
  review ruim como o eixo mais sensível deste mercado.

  O que medir, com o wallpaper aplicado e a tela ligada por alguns minutos:
  taxa de quadros estável (sem engasgo visível ao rolar a home), consumo em
  Configurações → Bateria, e aquecimento. Vale testar o cenário mais pesado
  (tanque, com chuva/neve forte pelo painel de debug).

  **Como decidir:** se segurar, mantenha `minSdk 26` — hoje ele cobre ~96% dos
  aparelhos e **não custa uma linha de código** (o projeto não tem nenhum
  `SDK_INT`/`@RequiresApi`, então subir não apagaria complexidade nenhuma;
  subir pra 28 jogaria fora ~2,6% dos aparelhos em troca de nada). Se NÃO
  segurar, aí subir o `minSdk` passa a ter justificativa — baseada nesta
  medição, não em preferência. Números de alcance: [apilevels.com](https://apilevels.com/).
- [x] **Tela grande / orientação no Android 16** — verificado em 2026-08-08 num
  AVD real de tela grande (API 36, 1280×800dp, páginas de 16 KB).

  Confirmado na prática, não em teoria: o Android 16 **ignora** a trava
  `android:screenOrientation="portrait"` do manifesto quando a tela tem ≥600dp —
  o app abriu em **paisagem**. Simular com `wm size` num AVD de celular NÃO
  reproduz isso (o sistema manteve o retrato lá); é preciso AVD de tela grande.

  Resultado: sem crash, layout legível. O teto de 600dp centralizado
  (`MainScreen`) é o que evita o conteúdo esticar — Início com a arte na
  proporção certa, Loja com mosaico em duas colunas e chips numa linha.

  Segue **não** otimizado pra tablet (coluna única, muito espaço vertical
  ocioso). Layout de duas colunas é decisão de produto em aberto, não bloqueio.
- [x] **Páginas de memória de 16 KB** — exigido pelo Google para app que target
  API 35+ e embarca biblioteca nativa em 64 bits, **prazo 1º/fev/2027**. Nos
  três critérios o Atmosfera se encaixa: `targetSdk` 36 e
  `libandroidx.graphics.path.so` (puxada pelo Compose) nas 4 ABIs.

  **Já conforme**, verificado em 2026-08-08 de duas formas: estaticamente com
  `zipalign -c -P 16 -v 4 app-debug.apk` (os quatro `.so` respondem `OK`,
  "Verification successful") e **em execução**, num AVD com páginas de 16 KB de
  verdade (`getconf PAGE_SIZE` → 16384): o app abre e roda sem erro de `dlopen`
  ou `UnsatisfiedLinkError`. Veio de graça com a migração — o alinhamento é
  automático a partir da AGP 8.5.1 e estamos na 8.13.2. Reconferir se alguma
  dependência nova trouxer `.so` próprio. Doc:
  [page-sizes](https://developer.android.com/guide/practices/page-sizes).
- [x] **Billing Library v8+ e `targetSdk` 36+** — as duas exigências do Google
  com prazo em **31/ago/2026** (extensão mediante pedido até 01/nov/2026).
  Atendidas em 2026-08-08: Billing 9.1.0 e `targetSdk` 36. Fontes:
  [deprecation-faq](https://developer.android.com/google/play/billing/deprecation-faq)
  e [política de target API](https://support.google.com/googleplay/android-developer/answer/11926878).
- [ ] **Logar uma conta Google no emulador para testar compra** — o AVD
  `Pixel_8` responde `In-app billing API version 3 is not supported on this
  device`, mas **não** é falta de Play Store: a imagem é
  `android-34/google_apis_playstore` e o `com.android.vending` está instalado.
  Falta **conta logada** (`adb shell dumpsys account` volta vazio). Logar em
  Configurações → Contas destrava o serviço de billing. (Produto criado no Play
  Console segue sendo requisito separado pra compra real.)
- [ ] **Confirmar se o uso da Open-Meteo se enquadra como "comercial"** —
  os termos do tier gratuito dizem "you may only use the free API services
  for non-commercial purposes" e listam apps "com assinaturas ou anúncios"
  como exemplo de uso comercial
  ([open-meteo.com/en/terms](https://open-meteo.com/en/terms)). O Atmosfera
  não tem assinatura nem anúncio, mas vende IAP (IAP não é mencionado
  explicitamente nos termos — zona cinzenta, não uma violação confirmada).
  Antes de publicar: confirmar com o Open-Meteo (contato deles) ou avaliar
  contratar o plano comercial (sem limite diário) — ver
  [open-meteo.com/en/pricing](https://open-meteo.com/en/pricing). Rate limit
  do tier grátis hoje: 10.000 chamadas/dia, 5.000/hora, 600/minuto.

## Antes de cada release (recorrente)

- [ ] `./gradlew assembleDebug` limpo (mínimo — é o que a CI valida).
- [ ] Testar o fluxo completo num emulador/aparelho: permissão de localização,
  "Definir papel de parede", troca de cenário na Loja, restaurar compras.
- [ ] Conferir se alguma permissão nova foi introduzida sem necessidade.
- [ ] Conferir se o Billing Library e o `targetSdk` ainda atendem o mínimo
  exigido pelo Google — os dois têm prazo com data marcada e mudam sozinhos com
  o tempo, sem ninguém mexer no código. Tabela em `CLAUDE.md`.
- [ ] Se houve mudança em `weather/`, `billing/`, no manifesto ou entrou um SDK
  novo: revisar [PRIVACIDADE.md](../legal/PRIVACIDADE.md) contra a tabela de rastreio
  abaixo, subir a versão da política e atualizar o Data Safety Form.

## Documentos legais (privacidade, termos, contato)

### Onde cada texto vive

Cada documento existe em **três** cópias, e as três precisam bater:

| Cópia | Caminho | Para quê |
|---|---|---|
| Canônica | `PRIVACIDADE.md` · `TERMOS.md` · `CONTATO.md` | Fonte da verdade, é o que se edita |
| Web | `docs/<pagina>/index.html` | Publicada (GitHub Pages) — é a URL que o Play Console exige |
| App | `android-app/app/src/main/assets/legal/<pagina>/index.html` | Embutida no APK, aberta offline em Ajustes |

A cópia web e a cópia do app são idênticas byte a byte, e
`PaginasLegaisSincronizadasTest` **quebra o gate** se divergirem — mas ele não
sabe comparar o `.md` com o HTML. Editou o Markdown? Replique no HTML e copie
pro asset no mesmo commit.

### Placeholders a preencher antes de publicar

Os **mesmos dados** aparecem nos três documentos (nos HTML estão destacados em
amarelo). Preencha de uma vez:

| Placeholder | O que entra | Onde |
|---|---|---|
| Controlador / desenvolvedor | Nome completo (pessoa física) ou razão social + CNPJ | Privacidade §1, Termos §1 |
| E-mail de contato | Caixa que alguém realmente lê. Prefira um endereço do produto (ex. `contato@…`) a um pessoal | Privacidade §1/10/11/14, Termos §1/13, Contato |
| Encarregado (DPO) | Nome + e-mail. Pode ser a mesma pessoa; a LGPD (art. 41) exige o canal, não um cargo dedicado | Privacidade §1, §14 |
| URL pública | A URL onde cada página for hospedada | Privacidade §1, Termos §1 |
| Foro / comarca | Cidade do desenvolvedor — ressalvado o foro do consumidor (art. 101, I do CDC) | Termos §12 |
| Data de vigência / histórico | Data da primeira publicação na Play | Privacidade, Termos |

### Como hospedar

Duas opções, ambas dão a URL que o Play Console pede:

1. **GitHub Pages neste repo** (mais rápido): Settings → Pages → Source
   "Deploy from a branch", branch `main`, pasta `/docs`. As páginas saem em
   `https://<user>.github.io/<repo>/privacidade/`, `/termos/` e `/contato/`.
2. **No site de apresentação** (destino final): copiar as três pastas de
   `docs/` pro repo do site quando ele existir. Se a URL mudar depois de
   publicado, atualize o Play Console — a política precisa continuar acessível
   na URL declarada.

Os HTML são autocontidos (CSS inline, sem fontes/scripts externos), responsivos
e seguem claro/escuro, então funcionam em qualquer host estático.

> Não é preciso hospedar para o app funcionar: as três páginas já são lidas do
> APK. A URL é exigência da Play Store, não do aplicativo.

### Rastreio código → política

Se qualquer linha da coluna da esquerda mudar, a política mente e precisa ser
corrigida no mesmo PR.

| Código | O que a política afirma |
|---|---|
| `AndroidManifest.xml` (permissões) | Seção 5 (tabela de permissões) e 3.1 |
| `LocationHelper.kt` — `PRIORITY_BALANCED_POWER_ACCURACY`, fallback Guarapuava | 3.1 ("precisão balanceada", "é opcional"), 10 |
| `WeatherRepository.kt` — base URL `api.open-meteo.com`, HTTPS, sem chave de API | 3.1, 6.1, 9 |
| `WeatherCache.kt` — TTL derivado do intervalo escolhido, delta ~5 km, só o registro mais recente | 3.1, 8 |
| `IntervaloClima.kt` — opções 15/30/60 min, padrão 30 | 3.1 ("intervalo escolhido por você"), 8 |
| `BootReceiver.kt` / `WeatherWorker` — período = intervalo escolhido | 3.1 ("verificação periódica") |
| `BillingManager.kt` — só `ProductType.INAPP`, compra única, sem `SUBS` | Privacidade 3.5 e 9; **Termos §5.1** ("não há assinatura") — passar a vender assinatura invalida os dois |
| `BillingManager.kt` — só resultado da compra + verificação de assinatura | 3.5, 9 |
| `Plano.kt`, `Cena.kt`, `Estilo.kt` — prefs locais | 3.4 |
| `AndroidManifest.xml` — `allowBackup="true"` | 3.6 |
| Ausência de SDK de ads/analytics | 4 e 12 — **integrar um SDK invalida a política** |

Nota de debug (não vai na política pública, porque não vale pro APK publicado):
`WeatherRepository` liga o `HttpLoggingInterceptor` em nível `BASIC` só quando
`BuildConfig.DEBUG`, o que escreve lat/lon no logcat. Em release fica `NONE` —
se isso mudar, a seção 3.7 deixa de ser verdade.

### Revisão jurídica

Os textos foram escritos a partir do comportamento real do código e cobrem o
que a LGPD e a Play exigem, mas **nenhum deles é parecer jurídico**. Antes de
publicar, vale a leitura de um advogado:

- **Privacidade** — identificação do controlador (pessoa física vs. empresa) e
  a base legal escolhida para cada dado (consentimento para localização,
  legítimo interesse para IP e backup).
- **Termos** — cláusula de limitação de responsabilidade (§9) diante do CDC,
  eleição de foro (§12), e a licença de uso (§4) sobre a arte dos cenários.
  Escrito depois da privacidade, por outro autor e sem revisão cruzada; vale
  conferir se as duas contam a mesma história sobre compras e dados.
