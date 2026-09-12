# Atmosfera — Especificação

> Consolida num só lugar o que "pronto" significa, o que está fora de escopo,
> e as travas técnicas/segurança que já foram decididas. Detalhe de arquitetura
> fica em [HANDOFF-FRONTEND.md](HANDOFF-FRONTEND.md); comandos/convenções do
> dia a dia ficam em [CLAUDE.md](../../CLAUDE.md). Este arquivo responde "o que
> estamos construindo e onde termina", não "como".

## O que é

Live wallpaper Android que desenha uma cena animada reagindo ao clima real
(Open-Meteo) — chuva, neve, nuvens, sol/lua, vento, névoa, fumaça de chaminé —
via motor de partículas próprio em `Canvas` (não vídeo/imagem). App companion
em Compose pra ativar o wallpaper e vender Premium + cenários extras via
Google Play Billing.

## Não-objetivos (decidido, não é falta de tempo)

- **Não compete em resolução de imagem estática** (não é proposta "wallpaper
  4K/6K/8K"). Confirmado pela análise de concorrência
  (`analise-concorrentes-atmosfera.pdf`): esse é o eixo de venda dos apps de
  wallpaper genérico (Wallcraft, DopeWalls), não o nosso.
- **Não é fotorrealismo.** Estética "comfy"/aconchegante, arte pintada +
  sprites — não IA generativa de imagem, não still fotográfico.
- **Sem assinatura recorrente no lançamento.** Monetização = Premium (compra
  única global) + cenários avulsos (compra única por cenário). Ver
  `billing/BillingManager.kt` — só usa `ProductType.INAPP`, nenhum `SUBS`.
  Se isso mudar no futuro é decisão explícita, não default.
- **Sem anúncios no lançamento.** Decisão tomada com base na pesquisa de
  concorrência (Seção 6 do PDF): anúncio é a reclamação nº1 do mercado
  mapeado, e o formato "ambiente contínuo" do Atmosfera piora a fricção de
  interstitial. Reavaliar só com dado real de conversão pós-lançamento — não
  antes.

## Monetização — o que é grátis e o que é pago

Três eixos de venda, independentes entre si:

| Eixo | Regra |
|---|---|
| **Cenários** | A cabana é grátis. Os demais são compra avulsa (`productId` em `engine/Catalogo.kt`) |
| **Premium** | Compra única global. Liga os 8 efeitos vivos em **todos** os cenários |
| **Estilos de efeito** | 4 livres, o restante no Premium — ver a regra abaixo |

### Regra de estilos — provisória (padrão até a curadoria fechar)

> **Atualização 2026-09-11:** a definição do que é grátis e do que é pago —
> incluindo fases da lua e os demais efeitos — é decisão do Gabriel (curadoria de
> produto). Esteve com o Rafael entre 2026-08-28 e 2026-09-11, quando ele passou
> a só publicar packs de conteúdo. A regra abaixo continua valendo como padrão
> até a curadoria fechar, e o raciocínio dela (por que mostrar bloqueado, por que
> prévia em vez de parede) segue válido independentemente de onde a linha for
> traçada.

**4 estilos livres, os demais no Premium**, sobre um catálogo **curado para ~12**
(hoje `engine/Estilo.kt` tem 28 arquivos, mas ~15 ideias visuais — o resto é
histórico de iteração; ver `TASKS.md`).

Três condições, e cada uma existe por um motivo:

**Os 4 livres precisam ser diferentes entre si.** Pixel, aquarela, clay e doodle,
por exemplo — não 4 variações do mesmo traço. Diversidade comunica "isto é um
produto"; similaridade comunica "isto é uma amostra". A conta que o usuário faz
não é de quantidade, é de variedade.

**Estilo pago aparece na lista, não é escondido.** Esconder significa que ninguém
descobre que o Premium existe. E o risco de afastar cliente não está aqui: a
análise de concorrência aponta **anúncio** como a reclamação nº 1 deste mercado,
não paywall — mostrar conteúdo bloqueado não entra no ponto de fricção do setor.

**Tocar num estilo pago dá prévia, não parede.** O estilo é aplicado na prévia ao
vivo, com a marca "Premium" e um caminho claro para comprar. O usuário **recebe
antes de ser convidado a pagar** — que é o oposto de insistente. Bloquear no
toque é o que gera sensação de muro, e é o comportamento a evitar. Mesma lógica
do comparador que já existe em `ui/PremiumScreen.kt`.

> O que perde cliente não é o bloqueio visível: é a razão parecer demonstração
> ("3 de 28") e a interrupção se repetir. Curar o catálogo resolve a primeira;
> prévia em vez de bloqueio resolve a segunda.

## Critério de "pronto" (MVP publicável)

Todo item abaixo precisa estar **verdadeiro**, não só "parece pronto":

- [ ] `./gradlew testDebugUnitTest lintDebug assembleDebug` verde (gate da CI)
- [ ] `assembleRelease` gera APK/AAB **assinado** (keystore de produção real,
  não só o mecanismo de código em `keystore.properties.example`)
- [ ] Produtos `atmosfera_premium` e `cenario_tanque` criados no Play Console
  e testados com compra real (sandbox ou teste fechado)
- [ ] `LICENSE_PUBLIC_KEY_BASE64` preenchida em `BillingManager.kt`
- [ ] Política de privacidade publicada por URL, cobrindo a coleta de
  localização aproximada
- [ ] Data Safety Form + Content Rating Questionnaire preenchidos no Console
- [ ] Ficha da loja com screenshots reais do app rodando (não só
  `wallpaper_thumbnail.png`)
- [ ] Teste fechado concluído com testers reais (exigência do Google pra
  apps novos)
- [ ] Nenhum cenário no catálogo do motor (`engine/Catalogo.kt`) sem asset
  publicado aparecendo na Loja sem tratamento (hoje: cobertura via
  `cenarioTemAsset` em `ui/components/SceneThumbnail.kt`)
- [ ] **Arte de cenário PAGO não embarca no APK/AAB base** — baixa sob demanda
  só depois da compra. Decisão de 2026-08-09: `assets/atmosfera/` saltou pra
  ~140 MB com o motor novo (6 cenários + 12 estilos), e isso não pode ir pra
  quem não comprou. Só a cabana (grátis) e o essencial do onboarding ficam
  embarcados. Ver `CHECKLIST_PUBLICACAO.md` para o porquê isto não é ajuste de
  Gradle — muda o contrato do motor documentado em `HANDOFF-FRONTEND.md` §3.1.

Checklist completo e detalhado continua em
[CHECKLIST_PUBLICACAO.md](CHECKLIST_PUBLICACAO.md) — este SPEC lista só o que
bloqueia o "pronto", não todo o passo a passo.

## Arquitetura (resumo — detalhe completo em HANDOFF-FRONTEND.md)

**O time são Gabriel e Rafael, mas desde 2026-09-11 é efetivamente solo**
(Gabriel + Claude): o Rafael ficou só com a publicação de releases de novos packs
de conteúdo, e todo o código passou a ser do Gabriel. O Willian já havia saído
(2026-08-28), com as áreas dele indo pro Gabriel.

| Pacote | Dono | Fronteira |
|---|---|---|
| `engine/` + `assets/atmosfera/` | Gabriel | Era do Rafael (congelado por convenção) até 2026-09-11; agora do Gabriel, editável direto. O front lê a API pública (`EffectEngine.carregar/draw/pronto`, `Catalogo`, `Cena`, `Cenas`, `Estilos`) |
| `ui/`, `weather/`, `service/`, `billing/` | Gabriel | Front |
| `debug/` | Gabriel | Ferramenta interna, só builds debug |
| Documentos legais (`docs/legal/*.md`, `docs/<pagina>/index.html`, `assets/legal/`) | Gabriel | Textos públicos + espelhos HTML |
| Site de apresentação/marketing | — | Sem dono. Era do Willian, nunca começou |

> **Sobre a saída do Willian.** Ele escreveu a política de privacidade
> (`2bb49ff`, 29/jul) e nada mais: `TERMOS.md` e `CONTATO.md` foram redigidos
> depois, sem ele, e o `PRIVACIDADE.md` foi corrigido pelo Gabriel em agosto. A
> divisão de trabalho ficou desatualizada por um mês, e o efeito prático disso é
> pior que a ausência: **todo mundo achava que alguém estava olhando os arquivos
> legais, e ninguém estava.** Dono errado no papel é pior que nenhum dono.

### Publicação na Play Store — quem faz o quê

Compliance não é uma coisa só: parte é texto, parte é declaração sobre o
código, parte é titularidade legal.

| Item | Quem | Por quê |
|---|---|---|
| Textos legais e mantê-los em dia com o código | Gabriel | Mesmo dono dos arquivos acima |
| **Data Safety Form** | Gabriel | É declaração sobre o que o **código** coleta. Declarar diferente do que o APK pede é a causa nº1 de rejeição — exige conhecer `weather/`, `LocationHelper` e o manifesto. A tabela de rastreio em `CHECKLIST_PUBLICACAO.md` é a fonte |
| Content Rating (IARC) | Quem estiver com o console aberto | Questionário de conteúdo, baixo risco |
| **Conta do Google Play Console** | Gabriel | **Não delegável.** O titular é o publicador legal — recebe os pagamentos, assina os formulários fiscais, e é o **controlador** nomeado na política de privacidade e nos termos |

## Versões travadas (não subir sem ler o motivo)

| Dependência | Versão presa | Por quê |
|---|---|---|
| Billing | 9.1.0 | **Exigência do Google**: v8+ obrigatório para app novo/update em 31/ago/2026. v9 vale até 31/ago/2028 |
| targetSdk / compileSdk | 36 | **Exigência do Google**: app novo precisa targetar API 36+ em 31/ago/2026 |
| AGP | 8.13.2 | Última da linha 8.x; suporta compileSdk 36 e evita as quebras da AGP 9.x (que ainda exigiria Gradle 9.5) |
| lifecycle | 2.10.0 | 2.11.0 exige compileSdk 37, acima do máximo da AGP 8.13.x — confirmado quebrando o build |
| minSdk | 26 | Cobre ~96% dos aparelhos e **não custa complexidade**: o projeto não tem um único `SDK_INT`/`@RequiresApi`, então subir não apagaria código — só perderia usuário (28 → ~93,5%). Baixar também não paga: 24 daria só +0,5%. Reavaliar **apenas** se a medição de desempenho em aparelho antigo reprovar (ver `CHECKLIST_PUBLICACAO.md`) |

**`targetSdk` alto não briga com `minSdk` baixo** — é confusão comum. `targetSdk`
declara contra qual comportamento o app foi testado; o sistema aplica modos de
compatibilidade em aparelhos mais velhos. `targetSdk 36` + `minSdk 26` roda no
Android 8 normalmente. O único eixo que decide alcance é o `minSdk`.

O compilador do Compose deixou de ter versão própria: do Kotlin 2.0 em diante
ele é o plugin `org.jetbrains.kotlin.plugin.compose`, sempre na versão do
Kotlin. Não há mais um par `kotlin`/`composeCompiler` para manter em sincronia.

Fonte da verdade dessas versões: `android-app/gradle/libs.versions.toml`
(comentário no topo do arquivo). Subir qualquer uma exige subir as
dependentes junto — nunca isolado.

## Requisitos de segurança

- Segredo nunca entra no diff: `*.jks`, `*.keystore`, `keystore.properties`,
  `local.properties`, `secrets.properties` — cobertos pelo `.gitignore`, e a
  CI roda `gitleaks` (job `secret-scan`) como segunda camada.
- Localização aproximada é o único dado sensível coletado — usada só
  localmente (Open-Meteo + cache), nunca enviada a servidor próprio (o
  projeto não tem backend).
- Permissão nova no `AndroidManifest.xml` = precisa justificativa no PR e
  atualização do Data Safety Form (`CHECKLIST_PUBLICACAO.md`) antes de
  publicar — não depois.

## Como este arquivo se relaciona com os outros

- **Mudou o critério de "pronto" ou um não-objetivo?** Edita aqui.
- **Mudou a fronteira motor/front ou a interface entre eles?** Edita
  `HANDOFF-FRONTEND.md`, resume aqui se afetar o critério de pronto.
- **Qual é a fase atual e o que falta nela?** Ver `ROADMAP.md`.
- **O que está em andamento agora, literalmente hoje?** Ver `TASKS.md`.
