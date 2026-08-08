# Atmosfera — Roadmap

> Fases com critério de saída explícito. Uma fase só é considerada concluída
> quando **todo** item do critério de saída é verdadeiro — não "na prática já
> dá pra seguir". Detalhe de cada pendência de publicação em
> [CHECKLIST_PUBLICACAO.md](CHECKLIST_PUBLICACAO.md); o que "pronto" significa
> no total, em [SPEC.md](SPEC.md).

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

## Fase 2 — Billing testável ponta a ponta ⏳ não iniciada

**Objetivo:** compra real fechando, não só código que compila.

**Critério de saída:**
- [ ] Produtos `atmosfera_premium` e `cenario_tanque` criados no Play Console
  (tipo INAPP, não-consumível — ver `Catalogo.kt` pros IDs valendo)
- [ ] `LICENSE_PUBLIC_KEY_BASE64` colada em `BillingManager.kt`
- [ ] Fluxo de compra testado em teste fechado/sandbox: comprar, restaurar,
  confirmar que `Plano.setPremium`/`Cena.definir` disparam certo

**Bloqueia em:** Fase 1 (precisa de build assinado pra registrar o app no
Play Console com o `applicationId` de produção).

## Fase 3 — Compliance de publicação 🔄 em andamento

**Objetivo:** itens que a Play Store exige antes de aceitar qualquer release,
independente de qualidade de código.

**Critério de saída:**
- [ ] Política de privacidade publicada por URL (cobre coleta de localização)
  — texto pronto; falta preencher os `[PREENCHER]` e **decidir a hospedagem**
  (repo é privado no plano free: GitHub Pages exige Pro ou repo público;
  alternativa é Netlify/Cloudflare Pages)
- [ ] Data Safety Form preenchido no Play Console — **em andamento**;
  respostas derivadas do código em [GUIA_PLAY_CONSOLE.md](GUIA_PLAY_CONSOLE.md)
- [ ] Content Rating Questionnaire (IARC) preenchido — respostas prontas no
  mesmo guia; espera-se Livre/L

**Já feito além do critério:** Termos de Uso e página de Contato escritos
(`TERMOS.md`, `CONTATO.md` + espelhos), os três documentos acessíveis dentro
do app por WebView sobre asset local — funciona offline, não depende da URL.

**Pode rodar em paralelo** com a Fase 2 — não depende dela.

**Caminho crítico:** conta de desenvolvedor no Play Console (US$ 25, aprovação
pode levar dias) e a decisão de hospedagem. O Data Safety só aparece depois do
app criado no console, e o formulário **exige a URL da política já no ar**.

## Fase 4 — Ficha da loja ⏳ não iniciada

**Objetivo:** listagem pronta pra revisão do Google.

**Critério de saída:**
- [ ] Screenshots reais do app rodando (Home, Loja, tela de detalhe com
  preview ao vivo, Ajustes) — não só `wallpaper_thumbnail.png`
- [ ] Descrição curta + longa da ficha
- [ ] Ícone final validado (adaptive icon já existe, confirmar em contexto
  real de launcher)

## Fase 5 — Teste fechado → produção ⏳ não iniciada

**Objetivo:** cumprir a exigência do Google de teste fechado com testers
reais antes de liberar produção, pegar bug de última hora.

**Critério de saída:**
- [ ] Faixa de teste fechado criada no Play Console, testers convidados
- [ ] Sem crash/ANR novo relatado durante o período mínimo de teste
- [ ] Promoção pra produção

**Bloqueia em:** Fases 1–4 completas.

---

## Fora das fases (trabalho contínuo, não bloqueia publicação)

- **Preview ao vivo do motor na tela de detalhe** (`EngineLivePreview.kt`) —
  implementado, aguardando verificação visual num emulador. **Mantido local,
  não commitado** por instrução do usuário (2026-07-29) — ver `TASKS.md`.
- **Setor do Willian** (site de apresentação, repo próprio) — não iniciado,
  repo ainda não criado.
- **Dependabot** — arquivo `.github/dependabot.yml` escrito (local, não
  commitado). Passa a valer assim que for commitado/pushado — GitHub liga
  sozinho ao ver o arquivo na `main`, não precisa de passo extra.
- **CodeQL — descartado, não pendente.** Verificado 2026-07-29: exige GitHub
  Advanced Security pra rodar em repo privado, indisponível no plano free
  deste repo. Não vale reabrir sem antes decidir tornar o repo público ou
  assinar um plano pago — decisão de negócio, não técnica.
- **Módulos Gradle (`:engine`/`:app`)** — transformaria a fronteira do motor
  em contrato de compilador, não só convenção de pasta. Precisa de
  coordenação com o Rafael antes de qualquer execução.
