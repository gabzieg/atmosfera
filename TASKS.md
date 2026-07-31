# Atmosfera — Tasks (fase atual)

> Estado do trabalho em andamento, literalmente agora. Diferente do
> `ROADMAP.md` (fases do produto até publicar), isto é o dia a dia — o que
> está pela metade, o que decidir, o que testar. Atualizar sempre que algo
> muda de status, não deixar ficar mentiroso.

**Última atualização:** 2026-07-30

## ⚠️ Modo local-only ativo

Por instrução do usuário (2026-07-29): **nada deste momento em diante deve
ser commitado ou pushado pro GitHub** até ele liberar de novo. Toda mudança
abaixo fica só no working tree local. Não rodar `git add`/`commit`/`push` nem
abrir PR sem confirmação explícita revogando isso.

## Em andamento

- [ ] **Preview ao vivo do motor na tela de detalhe da Loja**
  (`ui/components/EngineLivePreview.kt` + `SceneDetailScreen.kt`).
  Implementado, gate local verde (`testDebugUnitTest`/`lintDebug`/`assembleDebug`
  = BUILD SUCCESSFUL). **Falta verificação visual** — nenhum emulador/device
  conectado (`adb devices` vazio). Pendente: usuário ligar o `Pixel_8` no
  Android Studio → instalar → abrir Loja → cabana → conferir se o motor
  desenha por cima do `SceneThumbnail` sem quebrar layout.
  **Não commitar** (modo local-only).

- [ ] **Tela de confirmação antes de "Definir wallpaper"**
  (`ui/components/ConfirmarWallpaperDialog.kt`, ligado em `SceneDetailScreen.kt`
  e `HomeTab.kt`). Antes o botão pulava direto pro seletor do Android às
  cegas; agora mostra o `EngineLivePreview` do cenário/arte/estilo atual num
  diálogo de confirmação antes de disparar a intent. `assembleDebug` verde.
  **Falta verificação visual** — mesmo bloqueio do item acima (emulador).
  **Não commitar.**

- [ ] **Banner Premium menciona compra avulsa** (`StoreTab.kt`,
  `PremiumBanner`) — linha extra "Ou compre só o cenário que quiser, dentro
  dele" pra deixar claro que existe opção mais barata que Premium global
  (compra por cenário já existe na tela de detalhe, só não era mencionada
  no banner). `assembleDebug` verde. **Não commitar.**

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
  (`SPEC.md`, `ROADMAP.md`, `TASKS.md` — este arquivo). Escritos, precisam de
  1ª rodada de uso real pra validar se o formato cola com o time (Rafael,
  Willian) antes de virar convenção fixa.
- [ ] **`.github/dependabot.yml`** escrito (local, não commitado) — Gradle
  (`android-app/`) + GitHub Actions, semanal, PR próprio passando pelo gate
  normal. Só liga de verdade quando for commitado/pushado pra `main`.

- [ ] **Passo de revisão do diff (`/code-review`) adicionado ao `abrir-pr`**
  (seção 3.5, local, não commitado) — gate de build passa a não ser mais o
  único crivo antes de PR. É autorrevisão (mesmo modelo), não substitui
  revisão humana em área de risco — ver limite documentado na própria seção.

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

## Bloqueado, esperando o usuário

- Gerar keystore de produção + preencher `keystore.properties` (Fase 1 do
  `ROADMAP.md`) — ação fora do que código resolve.
- Criar produtos no Play Console (Fase 2) — precisa de conta/acesso do
  usuário.
- Ligar o emulador `Pixel_8` pra eu conseguir testar visualmente o preview
  ao vivo.

## Backlog (não é a fase atual, não puxar sem avisar)

- Dependabot + CodeQL no CI (identificado, aguardando liberação de commit).
- Módulos Gradle `:engine`/`:app` (precisa alinhar com Rafael antes).
- Setor do Willian — site em repo próprio, ainda não criado.
- Expandir cobertura de teste além do mapeamento de clima (hoje 5 casos).
