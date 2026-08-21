# Atmosfera — Tasks (fase atual)

> Estado do trabalho em andamento, literalmente agora. Diferente do
> `ROADMAP.md` (fases do produto até publicar), isto é o dia a dia — o que
> está pela metade, o que decidir, o que testar. Atualizar sempre que algo
> muda de status, não deixar ficar mentiroso.
>
> **Histórico de trabalho concluído não mora aqui** — mora no `git log`, que
> não desatualiza. Este arquivo ficou 19 commits atrás da realidade entre
> 2026-08-08 e 2026-08-10 justamente por acumular item feito.

**Última atualização:** 2026-08-10

## Estado do ambiente (confirmado 2026-08-10)

Trabalho todo na branch **`front/kotlin-2-billing-9`**, 27 commits à frente da
`origin/main` e 10 ainda não pushados. Árvore limpa fora de dois documentos de
análise não rastreados (`ANALISE-RETOMADA.md`, `brief-claude-design.md`).

Gate verde numa rodada limpa hoje: `testDebugUnitTest lintDebug assembleDebug`.
Emulador `Pixel_8` (API 36, `google_apis_playstore`) funcionando — build,
instalação, navegação e captura de tela via `adb` estão todos operacionais sem
precisar abrir o Android Studio.

**Push está quebrado deste lado:** o Git Credential Manager trava tentando abrir
janela em sessão não interativa. Push é ação do usuário, fora do alcance do
agente.

## Em andamento

- [ ] **A branch inteira ainda não virou PR.** São 27 commits — migração de
  Kotlin/Billing, onboarding, tela de Premium, merge do snapshot do motor,
  correções de Loja e a otimização de desempenho. O usuário já sinalizou que a
  revisão é superficial e que um PR único serve, mas ele **não existe** ainda.
  Enquanto isso, `main` não tem nada disso.

- [ ] **A correção de desempenho não tem guarda de regressão.** Commit `d7a75d5`
  tirou o `EngineLivePreview` da Home porque o motor na thread de UI deixava a
  tela pastosa (medido: ~300 quadros/12 s e 86% de jank, contra ZERO em
  Ajustes). Se alguém puser a prévia de volta na Home, **nada acusa** — só o
  comentário no código. O padrão que funcionaria já existe no projeto:
  `PaginasLegaisSincronizadasTest` e `PermissoesDeclaradasTest` leem arquivo e
  travam um invariante. Decidir se vale um teste do mesmo tipo aqui.

- [ ] **Decisão pendente com o usuário: tonalidade dia/noite na Home.** Com a
  miniatura estática, o card da Home não escurece à noite — mostra a arte diurna
  às 6h da manhã, enquanto o wallpaper de verdade está escuro. A prévia ao vivo
  acompanhava a hora; a estática não. Resolve com um gradiente por horário, sem
  motor nenhum. Perguntado, ainda não respondido.

- [ ] **Validar o formato de docs inspirado no workflow do Chris Titus.**
  `SPEC.md`/`ROADMAP.md`/`TASKS.md` commitados em `02360bf`; falta a rodada de
  uso real com o time (Rafael, Willian) pra decidir se vira convenção fixa. Esta
  reconciliação de 2026-08-10 é a primeira prova de que o formato exige
  manutenção ativa pra não mentir.

## Bloqueado, esperando o usuário

- [ ] **Abrir a conta do Google Play Console** (~US$ 25, aprovação em dias).
  Adiada conscientemente em 2026-08-08 ("deixar o app 100% antes"). É o maior
  lead time do caminho crítico e bloqueia as Fases 2, 6 e metade da 3. Lembrete
  honesto: a Fase 2 **não tem como** ficar 100% antes da conta — compra real só
  fecha com produto criado no console.
- [ ] **Mover a senha da keystore pra um gerenciador de senhas e apagar o
  `.txt`.** A senha já é forte (28 caracteres, gerada em 2026-08-04) — o que
  falta é parar de deixá-la em texto puro em
  `C:\Users\gbrus\Chaves\SENHA-LEIA-E-APAGUE.txt`. O agente evita ler ou
  manipular esse arquivo por princípio de segredo.
- [ ] **Backup da keystore** — dois lugares offline. Perder a chave de upload
  exige reset via suporte do Google Play (dias de espera), ainda que com Play
  App Signing não seja definitivo como o README sugere.
- [ ] **Decidir a hospedagem das páginas legais** (Netlify / Cloudflare Pages /
  tornar o repo público). Não depende da conta do Console e destrava metade da
  Fase 3.

## Decisões tomadas (não re-abrir sem motivo novo)

- **Conteúdo pago baixa sob demanda** (2026-08-09) — virou a Fase 4 do
  `ROADMAP.md`. Muda o contrato do motor; exige alinhar com o Rafael antes de
  qualquer código. Sem urgência pro build de debug.
- **Prévia ao vivo fora da Home** (2026-08-10) — decisão do usuário depois da
  medição. Continua no onboarding, no detalhe da Loja e no comparador do
  Premium, que são os momentos em que ela vende.
- **Descartado: desenhar a cena em bitmap reduzido e ampliar.** Piorou a mediana
  de 25 ms pra 38 ms — `Canvas(Bitmap)` é software. Motivo comentado no código.
- **Cenário sem asset some da Loja por verificação real** (`cenarioTemAsset` em
  `ui/components/SceneThumbnail.kt`), não por lista fixa. Substituiu o antigo
  `SEM_ASSET_PUBLICADO`, que só ficava correto enquanto alguém lembrasse de
  editá-lo — foi por isso que `fiordes` apareceu sozinho quando o Rafael
  publicou a arte dele.
- **Áreas de risco encolhidas pra 3** (2026-08-09): `engine/`,
  `assets/atmosfera/`, `.github/`. Saíram manifesto (coberto por teste),
  `build.gradle` (coberto pela CI) e `billing/` (voltou pro Gabriel).
- Preço: cenário avulso R$ 9,90–19,90, Premium R$ 39,90–59,90 (vitalício, não
  assinatura — `BillingManager` só suporta `INAPP`).
- Sem anúncios no lançamento — a maior brecha de mercado encontrada é
  justamente a fadiga de anúncio dos concorrentes.
- CodeQL descartado (repo privado + plano free). Ver `ROADMAP.md`.

## Setorização (quem toca o quê)

Fonte completa em [SPEC.md](SPEC.md) → arquitetura e "Publicação na Play
Store"; mapeamento executável em `.github/CODEOWNERS`.

| Setor | Dono | Estado |
|---|---|---|
| `engine/` + `assets/atmosfera/` | Rafael | **Snapshot novo entregue e mergeado em 2026-08-09** — 6 cenários, 12 estilos, marcação por cena. Próximo assunto com ele é o contrato de `carregar()` (Fase 4) |
| `ui/`, `weather/`, `service/` | Gabriel | Ativo |
| `billing/` | Gabriel (era do Willian) | Integração pronta — Billing 9.1.0, preços reativos, reconciliação de reembolso. Falta cadastrar os produtos no Play Console |
| Documentos legais | Willian | Textos prontos, faltam os `[PREENCHER]` e a hospedagem |
| Data Safety Form | Gabriel (+ Willian conferindo) | Não iniciado — depende do app existir no console |
| Content Rating (IARC) | Quem abrir o console | Não iniciado |
| Conta Play Console | Gabriel | Não delegável, **adiada** |
| Site de apresentação | Willian | Repo próprio, não criado |

## Backlog (não é a fase atual, não puxar sem avisar)

- Ampliar cobertura de teste pra comportamento (ver `ROADMAP.md` → trabalho
  contínuo). Hoje: 4 testes unitários, zero instrumentado.
- Módulos Gradle `:engine`/`:app` — precisa alinhar com o Rafael, e agora
  concorre com a Fase 4, que já vai mexer no contrato dele.
- Setor do Willian — site em repo próprio, ainda não criado.
