# Atmosfera — Roadmap

> Fases com critério de saída explícito. Uma fase só é considerada concluída
> quando **todo** item do critério de saída é verdadeiro — não "na prática já
> dá pra seguir". Detalhe de cada pendência de publicação em
> [CHECKLIST_PUBLICACAO.md](CHECKLIST_PUBLICACAO.md); o que "pronto" significa
> no total, em [SPEC.md](SPEC.md).

## Status atual (2026-08-28)

App **funcionalmente completo** para o que não depende do Google: onboarding de
3 passos, Home com seletor de cenários, Loja, tela de venda do Premium, Ajustes,
páginas legais offline. Branch `front/kotlin-2-billing-9`, **39 commits à frente
da `main`** — e ainda sem virar PR, o que segue sendo o maior risco acumulado.

**A Fase 5 (ficha da loja) está quase fechada.** Prontos e validados: 6
screenshots, ícone 512×512, feature graphic 1024×500, título e as duas
descrições. Falta um item só, e ele não é nosso: a lista de cenários dentro da
descrição longa depende de quais cenários entram no lançamento.

**A Fase 4 destravou pela metade.** O contrato do motor já migrou (`a20732d`):
`carregar()` recebe `FonteDeAssets`, os 3 pontos de contato saíram do
`AssetManager` e o `ContratoFonteDeAssetsTest` trava a regressão. O que falta
agora é decisão, não código — e desde 2026-09-11 essas decisões são nossas (o
motor virou do Gabriel; o Rafael só publica packs). **Reavaliação 2026-09-11:**
o lançamento vai bundle local (a biblioteca em WebP empacotado cabe sob 500 MB) e
o download fica pra depois — ver [DECISAO-ENTREGA-DE-ARTE.md](DECISAO-ENTREGA-DE-ARTE.md).

**Essas decisões voltaram pro Gabriel (2026-09-11)** — estiveram com o Rafael de
2026-08-28 a 2026-09-11, e hoje ele só publica packs de conteúdo:
1. **Tamanho/agrupamento dos packs** — o Rafael ainda produz as ~215 imagens, mas
   o agrupamento e a estratégia de entrega são decisão nossa (ver a Fase 4 e a
   [DECISAO-ENTREGA-DE-ARTE.md](DECISAO-ENTREGA-DE-ARTE.md)).
2. **O que é grátis e o que é pago** — incluindo fases da lua e os demais
   efeitos: curadoria do Gabriel. Substitui a regra provisória que está em `SPEC.md`.

**O caminho crítico segue parado numa decisão, não num impedimento técnico:** a
conta do Play Console (US$ 25), adiada em 2026-08-08 ("deixar o app 100% antes").
Ela bloqueia as Fases 2, 6 e metade da 3.

"100% antes" tem teto: a Fase 2 (compra real fechando) **só pode ser validada com
produto criado no console**. E o teste fechado exige **12 testers por 14 dias
contínuos**, com uso real verificado — um relógio de calendário que só começa
depois da conta aprovada e do AAB no canal fechado. Adiar a conta adia semanas,
não dinheiro.

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

### Decisão de mecanismo (2026-08-25): Play Asset Delivery, modo on-demand

Um **asset pack por cenário pago**, baixado só depois da compra. Limites da
Play: **50 packs e 2 GB no total**.

### Tamanho dos packs — a restrição não é a que parecia (2026-08-28)

O Rafael produz **~215 imagens**; o agrupamento (de 10 em 10 ou de 5 em 5) é
decisão nossa desde 2026-09-11. Medido contra o repo de hoje (123 imagens,
139,6 MB, média de 1,14 MB por arquivo):

| Agrupamento | Packs | Folga até o teto de 50 |
|---|---|---|
| 10 imagens por pack | 22 | 28 |
| 5 imagens por pack | **43** | **7** |

**O gargalo é a quantidade de packs, não o tamanho.** 215 imagens projetam ~244
MB — pouco mais de um décimo dos 2 GB permitidos. Já 43 packs consomem 86% do
teto de 50, e o catálogo continua crescendo: cada cenário ou arte nova empurra
esse número, e não há como passar de 50 sem reestruturar tudo.

Isso inverte a premissa que abriu esta fase. O problema que nos trouxe aqui era
**peso** (os 140 MB no install); o problema que limita a solução é **contagem**.

**Critério para decidir, que vale mais que o número redondo:** o pack deve ser a
mesma unidade da compra. Se o usuário compra um cenário, ele baixa aquele
cenário — nem mais, nem menos. Agrupar por quantidade fixa de imagens só funciona
se cada grupo corresponder a algo que se vende; senão, o usuário baixa conteúdo
que não comprou (o que anula o objetivo) ou precisa de dois packs para um item só
(o que gasta o teto duas vezes mais rápido).

**Por que não servidor próprio.** Quatro custos que se somam, e o terceiro é o
que costuma passar despercebido:

1. *Operacional* — hospedagem, uptime, TLS, CDN viram responsabilidade
   permanente num projeto que hoje não tem infraestrutura nenhuma.
2. *Segurança* — para o download ser realmente gated, o servidor precisa
   verificar a compra pela Google Play Developer API, com conta de serviço e
   credencial privada. Passa a existir um segredo de produção para administrar.
3. *Compliance* — a política de privacidade afirma hoje que **o projeto não tem
   backend** e que nada sai do aparelho além da consulta de clima, e está
   amarrada ao código pela tabela de rastreio do `CHECKLIST_PUBLICACAO.md`.
   Subir um servidor invalida a política **e** o Data Safety Form: os dois
   teriam de ser reescritos e reenviados. Trocaríamos um problema de entrega
   por um problema jurídico.
4. *Manutenção* — servidor fora do ar significa conteúdo pago inacessível,
   reembolso e avaliação de uma estrela.

A PAD elimina os quatro: é o CDN do Google, sai de graça, já vem no AAB e não
muda uma linha da política.

**Por que on-demand e não os outros dois modos.** `install-time` entra no
install — é exatamente o problema que estamos resolvendo. `fast-follow` baixa
sozinho logo após instalar, para todo mundo: colocaria arte paga no aparelho de
quem não comprou e ainda gastaria a internet da pessoa. Só o `on-demand` baixa
quando o app pede.

**Por que um pack por cenário.** O pack passa a ser a mesma unidade da compra:
comprou o Farol, baixa o Farol. Agrupar faria baixar conteúdo não comprado, e aí
a separação viraria teatro.

**Por que uma interface, e não um caminho de arquivo.** Se `carregar()` receber
um `File`, o motor precisa saber qual é o caso — assets ou disco — e isso mete
um `if` de modo de entrega dentro do `engine/`, arrastando conceito de Play
Asset Delivery para a área do Rafael. Com uma interface de um método só, o motor
mantém a forma que já tem e nunca descobre que asset pack existe:

```kotlin
fun interface FonteDeAssets { fun abrir(caminho: String): InputStream }
fun carregar(fonte: FonteDeAssets, cenaId: String, arte: String, estilo: String)
```

São **três pontos de contato**, não uma refatoração: `EffectEngine.kt:126`
(`bmp()`), `EffectEngine.kt:143` (`DadosMarcacao.ler`) e `Marcacao.kt:47` — os
três já têm a forma `abrir(caminho) → InputStream`.

Efeito colateral que vale por si: uma `FonteDeAssets` falsa permite testar
`carregar()` **sem aparelho**. Hoje não existe um único teste tocando o motor,
justamente porque tocá-lo exige `AssetManager`.

**Critério de saída:**
- [ ] Mapa explícito de grátis vs pago, por cenário e por estilo — **curadoria do
  Gabriel (desde 2026-09-11)**, incluindo fases da lua e os demais efeitos. A
  "Regra de estilos" em [SPEC.md](SPEC.md) é provisória e vale até essa definição chegar
- [ ] Tamanho do pack decidido (ver "Tamanho dos packs" acima) — o teto de 50
  packs é a restrição real, não o de 2 GB
- [ ] Asset packs configurados; **AAB base medido** e sem arte paga dentro
- [x] `carregar()` recebendo `FonteDeAssets` em vez de `AssetManager` — feito em
  2026-08-28. Os 3 pontos de contato migrados (`EffectEngine.bmp()`,
  `DadosMarcacao.ler()`), os 4 chamadores do front atualizados, e o adaptador do
  APK movido pra fora do motor (`ConteudoEmbarcado.kt`)
- [x] **Teste travando essa assinatura** — `ContratoFonteDeAssetsTest`.
  **Verificado que pega a regressão**: reintroduzindo `import
  android.content.res.AssetManager` em `Marcacao.kt`, o gate falha
- [ ] Compra → download → cenário aplicável, testado ponta a ponta
- [ ] Falha/interrupção de download tratada na UI, sem crash e sem cenário
  meio-carregado

**Guarda obrigatória, não opcional.** Mesmo agora que o `engine/` é nosso
(2026-09-11) e não chega mais por snapshot do Rafael, um refactor futuro pode
reintroduzir `AssetManager` sem querer — e aí o conteúdo pago para de carregar em
produção sem ninguém perceber, porque compila e roda no debug. Um teste que
falhe quando a assinatura voltar a receber `AssetManager` direto é o que
transforma isso em erro visível — mesmo padrão do
`PermissoesDeclaradasTest`. Isto vale com ou sem revisor: é problema de merge,
não de revisão.

### O que a PAD **não** resolve

**Não é proteção de conteúdo.** O pack não é trancado por compra — quem souber
pedir, baixa. O que impede é o app só pedir depois que `Plano`/compra confirma.
Alguém determinado consegue a arte sem pagar. Proteção real exigiria servidor
autenticado, ou seja, os quatro custos acima. Para wallpaper isso é o padrão do
mercado, mas fica registrado como **escolha consciente**, não descuido.

**O pack é preso à versão do app.** Cenário novo = versão nova publicada. Não dá
para soltar conteúdo sem update. Se um dia isso for requisito, aí sim entra
servidor próprio — e a conta muda.

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
- [x] **Feature graphic 1024×500** — `docs/loja/feature-graphic-1024x500.png`,
  composto a partir da arte existente: fundo escuro do app, três ladrilhos
  (cabana/fiordes/tanque) e o bloco kicker+título+tagline no padrão do próprio
  app. PNG 24-bit sem alfa, validado
- [x] **Ícone 512×512** — `docs/loja/icone-512.png`: recorte da cabana, escolhido
  por legibilidade no tamanho real de ícone. Substituiu a montanha vetorial, que
  era genérica e tinha o sol fora da zona segura do adaptive icon. **O ícone do
  app foi trocado junto** (`res/mipmap-*`), senão ficha e aparelho mostrariam
  ícones diferentes. Verificado na gaveta de apps do emulador
- [x] Descrição curta (80 caracteres) + longa (4.000) — rascunho em
  [docs/loja/FICHA.md](../loja/FICHA.md), com contagem de caracteres conferida
  e uma tabela ligando cada afirmação ao código que a sustenta. **Falta a
  revisão do Gabriel** antes de ir pro Console

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
- **Site de apresentação — sem dono.** Era do Willian, que saiu do projeto em
  2026-08-28 sem ter começado. Não bloqueia a publicação: a Play exige a URL da
  política de privacidade, não um site institucional. Só reabrir se virar
  prioridade de marketing.
- **Dependabot** — ✅ ativo, `.github/dependabot.yml` está na `main`.
- **CodeQL — descartado, não pendente.** Verificado 2026-07-29: exige GitHub
  Advanced Security pra rodar em repo privado, indisponível no plano free
  deste repo. Não vale reabrir sem antes decidir tornar o repo público ou
  assinar um plano pago — decisão de negócio, não técnica.
- **Módulos Gradle (`:engine`/`:app`)** — transformaria a fronteira do motor
  em contrato de compilador, não só convenção de pasta. Desde 2026-09-11 o motor
  é nosso, então não depende mais de coordenar com o Rafael; concorre com a
  Fase 4, que já vai mexer no mesmo contrato do motor.
