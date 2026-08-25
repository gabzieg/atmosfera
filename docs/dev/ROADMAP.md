# Atmosfera — Roadmap

> Fases com critério de saída explícito. Uma fase só é considerada concluída
> quando **todo** item do critério de saída é verdadeiro — não "na prática já
> dá pra seguir". Detalhe de cada pendência de publicação em
> [CHECKLIST_PUBLICACAO.md](CHECKLIST_PUBLICACAO.md); o que "pronto" significa
> no total, em [SPEC.md](SPEC.md).

## Status atual (2026-08-10)

App **funcionalmente completo** para o que não depende do Google: onboarding de
3 passos, Home com seletor de cenários, Loja com 8 cenários e 12 estilos de
efeito, tela de venda do Premium, Ajustes, páginas legais offline. Trabalho
recente na branch `front/kotlin-2-billing-9` (27 commits à frente da `main`).

**O caminho crítico está parado numa decisão, não num impedimento técnico:** a
conta do Play Console (US$ 25) foi adiada pelo usuário em 2026-08-08 ("deixar o
app 100% antes"). Ela bloqueia as Fases 2, 6 e metade da 3.

Vale saber que "100% antes" tem teto: a Fase 2 (compra real fechando) **só pode
ser validada com produto criado no console**. Dá pra deixar o app completo em
tudo, menos exatamente na parte que precisa da conta.

**Destravado e sem depender de ninguém:** Fase 4 (conteúdo sob demanda, precisa
alinhar com o Rafael) e Fase 5 (ficha da loja).

## Fase 0 — Infra de qualidade ✅ concluída (2026-07-28)

**Objetivo:** parar de commitar direto sem rede de segurança; garantir que
regressão de teste/lint/segredo seja pega antes do merge, não depois.

**Critério de saída:**
- [x] `test/` existe com cobertura da lógica pura de clima (mapeamento WMO,
  parse de hora) — `WeatherMappingTest.kt`
- [x] CI roda em `pull_request`, não só `push` na `main`
- [x] CI roda testes + lint (com baseline) + `assembleDebug` + varredura de
  segredo (`gitleaks`) em toda mudança
- [x] Versões centralizadas em `gradle/libs.versions.toml`
- [x] Fluxo de PR documentado (`.claude/skills/abrir-pr/`, `CODEOWNERS`,
  template de PR)

**Limite conhecido desta fase — o gate não vê comportamento.** Ele prova que
compila, que linta e que função pura mapeia certo. Duas regressões já passaram
por ele inteiras: a opção de "15 min" que não fazia nada (`IntervaloClima`,
2026-08-05) e a lentidão da prévia ao vivo (2026-08-10) — as duas compilavam,
rodavam e pareciam certas. Ver "Trabalho contínuo" no fim deste arquivo.

## Fase 1 — Build de release assinável ✅ concluída (2026-08-01, reconfirmada 2026-08-08)

**Objetivo:** existir um caminho real de `assembleRelease` → AAB assinado,
publicável na Play Store.

**Critério de saída:**
- [x] `signingConfig` no `build.gradle` lendo `keystore.properties`
  (gitignored) — código pronto, mergeado
- [x] Keystore de produção gerada (`keytool`, RSA 2048, validade até dez/2053)
  e guardada **fora da árvore do repositório** (`C:\Users\gbrus\Chaves\`) —
  regenerada em 2026-08-04 (a original desapareceu desta máquina; nada tinha
  sido publicado ainda, então a troca foi gratuita), com senha forte
  (28 caracteres, alfanumérica + símbolos) já definida na regeneração
- [x] `keystore.properties` preenchido localmente e `bundleRelease` testado:
  gera `app-release.aab` (~22,2 MB) assinado, conferido com
  `keytool -printcert -jarfile` em 2026-08-08 — certificado SHA256withRSA
  `Válido de: 04/08/2026`, batendo com a keystore atual (não a antiga)

**Pendência de higiene (não bloqueia a fase):** a senha forte gerada em
2026-08-04 está isolada em texto puro em
`C:\Users\gbrus\Chaves\SENHA-LEIA-E-APAGUE.txt`. Mover pra um gerenciador de
senhas, apagar o arquivo, e fazer um segundo backup offline do `.jks` — ação
do usuário, ver `TASKS.md`.

**Atenção — o tamanho do AAB mudou desde a medição.** Os ~22,2 MB são de antes
do snapshot do motor de 2026-08-09, que levou `assets/atmosfera/` de 14,7 MB pra
~140 MB. Refazer a medição faz parte do critério da Fase 4.

## Fase 2 — Billing testável ponta a ponta ⏳ não iniciada

**Objetivo:** compra real fechando, não só código que compila.

**Critério de saída:**
- [ ] Produtos `atmosfera_premium` e `cenario_tanque` criados no Play Console
  (tipo INAPP, não-consumível — ver `Catalogo.kt` pros IDs valendo)
- [ ] `LICENSE_PUBLIC_KEY_BASE64` colada em `BillingManager.kt`
- [ ] Fluxo de compra testado em teste fechado/sandbox: comprar, restaurar,
  confirmar que `Plano.setPremium`/`Cena.definir` disparam certo

**Bloqueia em:** conta do Play Console (ver "Status atual").

**O código já está pronto** — Billing 9.1.0, preços reativos, reconciliação de
reembolso, botão de compra que explica o motivo quando a loja não responde. O
que falta é cadastro, não implementação. Para testar a UI de conteúdo pago sem
a conta, use o painel de debug → "Destravar cenários pagos (teste)".

## Fase 3 — Compliance de publicação 🔄 em andamento

**Objetivo:** itens que a Play Store exige antes de aceitar qualquer release,
independente de qualidade de código.

**Critério de saída:**
- [ ] Política de privacidade publicada por URL (cobre coleta de localização)
  — texto pronto; falta preencher os `[PREENCHER]` e **decidir a hospedagem**
  (repo é privado no plano free: GitHub Pages exige Pro ou repo público;
  alternativa é Netlify/Cloudflare Pages)
- [ ] Data Safety Form preenchido no Play Console — respostas derivadas do
  código em [GUIA_PLAY_CONSOLE.md](GUIA_PLAY_CONSOLE.md); **só aparece depois
  do app criado no console**
- [ ] Content Rating Questionnaire (IARC) preenchido — respostas prontas no
  mesmo guia; espera-se Livre/L

**Já feito além do critério:** Termos de Uso e página de Contato escritos
(`TERMOS.md`, `CONTATO.md` + espelhos), os três documentos acessíveis dentro
do app por WebView sobre asset local — funciona offline, não depende da URL.
`ACCESS_FINE_LOCATION` foi removida do manifesto (o app nunca precisou dela) e
`PermissoesDeclaradasTest` trava isso — mudança de permissão agora quebra o
gate, forçando a revisão do Data Safety junto.

**Pode rodar em paralelo** com a Fase 2 — não depende dela.

**Caminho crítico:** a decisão de hospedagem pode ser tomada hoje, sem a conta.
O Data Safety, não: exige o app criado no console **e** a URL da política já no
ar.

## Fase 4 — Entrega de conteúdo pago sob demanda ⏳ não iniciada

**Objetivo:** arte de cenário pago não embarcar no APK/AAB base — baixar só
depois da compra.

**Por que virou fase (2026-08-09):** requisito do usuário ("as imagens não podem
ficar no app, tem que ser baixado só se adquirido"), registrado como critério de
pronto em [SPEC.md](SPEC.md). O snapshot do motor levou `assets/atmosfera/` de
14,7 MB pra ~140 MB (6 cenários + 12 estilos): embarcar tudo é peso morto no
install de quem não comprou, e distribui de graça o conteúdo que deveria ser
pago.

**Critério de saída:**
- [ ] Mapa explícito de grátis vs pago, por cenário e por estilo (hoje isso só
  existe implícito no `Catalogo.gratis`/`productId`)
- [ ] Asset packs configurados; **AAB base medido** e sem arte paga dentro
- [ ] `EffectEngine.carregar(...)` aceita conteúdo fora do `AssetManager` —
  contrato novo, **acordado com o Rafael antes de escrever código**
- [ ] Compra → download → cenário aplicável, testado ponta a ponta
- [ ] Falha/interrupção de download tratada na UI, sem crash e sem cenário
  meio-carregado

**Bloqueia em:** conversa com o Rafael. Asset pack "on-demand" **não é visível
por `context.assets`** — é lido pelo `AssetPackManager`, que devolve caminho de
arquivo. Ou seja, `carregar(assets: AssetManager, …)`, documentada como
congelada em [HANDOFF-FRONTEND.md](HANDOFF-FRONTEND.md) §3.1, é exatamente o que
precisa mudar. Não é ajuste de Gradle.

**Não bloqueia o desenvolvimento:** build de debug segue com tudo embarcado, e
isso está combinado. Bloqueia só o release destinado à Play Store.

## Fase 5 — Ficha da loja ⏳ não iniciada

**Objetivo:** listagem pronta pra revisão do Google.

**Critério de saída:**
- [x] Screenshots reais do app rodando — **6 capturas em `docs/loja/`**
  (onboarding, wallpaper aplicado no launcher, Início, Loja, detalhe com
  prévia ao vivo, Premium), feitas em 2026-08-25 e validadas uma a uma contra
  as regras da Play: 1080×1920, razão 1,78 (teto é 2:1), PNG 24-bit sem canal
  alfa. Ver [docs/loja/README.md](../loja/README.md)
- [ ] **Feature graphic 1024×500** — obrigatório em toda ficha, não existe
  ainda. É peça de arte; não dá pra derivar de screenshot
- [ ] **Ícone 512×512** — o adaptive icon é 100% vetorial (`mipmap-*/*.xml`,
  nenhum PNG no repo), então o ícone da ficha precisa ser renderizado
- [ ] Descrição curta (80 caracteres) + longa (4.000)

**Não depende de nada** — nem da conta, nem do Rafael. É a fase com o menor
custo de entrada hoje.

> **Aprendido ao capturar:** a captura crua do `Pixel_8` sai 1080×2400, que é
> 2,22:1 e **seria rejeitada** pela Play. O emulador precisa ir pra 1080×1920
> antes. E o `screencap` grava com canal alfa, que a Play também não aceita.
> Detalhes e o passo a passo pra refazer em `docs/loja/README.md`.

## Fase 6 — Teste fechado → produção ⏳ não iniciada

**Objetivo:** cumprir a exigência do Google de teste fechado com testers
reais antes de liberar produção, pegar bug de última hora.

**Critério de saída:**
- [ ] Faixa de teste fechado criada no Play Console, testers convidados
- [ ] Sem crash/ANR novo relatado durante o período mínimo de teste
- [ ] Promoção pra produção

**Bloqueia em:** Fases 1–5 completas.

---

## Fora das fases (trabalho contínuo, não bloqueia publicação)

- **Cobertura de teste de comportamento.** Hoje são 4 testes unitários
  (`WeatherMappingTest`, `IntervaloClimaTest`, `PaginasLegaisSincronizadasTest`,
  `PermissoesDeclaradasTest`) e **zero instrumentado**. As duas regressões que
  passaram pelo gate (ver Fase 0) eram de comportamento, não de compilação. Os
  dois testes que **de fato** pegam regressão hoje são os que leem arquivo do
  disco e travam um invariante — é o padrão a repetir quando o invariante não
  couber num teste de função pura.
- **Prévia ao vivo do motor** (`EngineLivePreview.kt`) — commitada e em uso no
  onboarding, no detalhe da Loja e no comparador do Premium. **Removida da Home
  em 2026-08-10 por custo medido**: com ela, a Início desenhava ~300 quadros a
  cada 12 s (mediana 26 ms, 86% de jank) contra ZERO da tela de Ajustes; o
  motor roda na thread de UI, então isso deixava rolagem e toque pastosos na
  tela em que o usuário mais fica.
- **Setor do Willian** (site de apresentação, repo próprio) — não iniciado,
  repo ainda não criado.
- **Dependabot** — ✅ ativo, `.github/dependabot.yml` está na `main`.
- **CodeQL — descartado, não pendente.** Verificado 2026-07-29: exige GitHub
  Advanced Security pra rodar em repo privado, indisponível no plano free
  deste repo. Não vale reabrir sem antes decidir tornar o repo público ou
  assinar um plano pago — decisão de negócio, não técnica.
- **Módulos Gradle (`:engine`/`:app`)** — transformaria a fronteira do motor
  em contrato de compilador, não só convenção de pasta. Precisa de
  coordenação com o Rafael antes de qualquer execução, e agora concorre com a
  Fase 4, que já vai mexer no contrato dele.
