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
  publicado aparecendo na Loja sem tratamento (hoje: filtro
  `SEM_ASSET_PUBLICADO` em `StoreTab.kt` cobre `fiordes`)

Checklist completo e detalhado continua em
[CHECKLIST_PUBLICACAO.md](CHECKLIST_PUBLICACAO.md) — este SPEC lista só o que
bloqueia o "pronto", não todo o passo a passo.

## Arquitetura (resumo — detalhe completo em HANDOFF-FRONTEND.md)

| Pacote | Dono | Fronteira |
|---|---|---|
| `engine/` + `assets/atmosfera/` | Rafael | Congelado por convenção — front lê a API pública (`EffectEngine.carregar/draw/pronto`, `Catalogo`, `Cena`, `Cenas`, `Estilos`), não edita os arquivos |
| `ui/`, `weather/`, `service/`, `billing/` | Gabriel | Front. `billing/` era do Willian; voltou pro Gabriel em 2026-08, com a integração pronta |
| `debug/` | Gabriel | Ferramenta interna, só builds debug |
| Documentos legais (`PRIVACIDADE.md`, `TERMOS.md`, `CONTATO.md`, `docs/`, `assets/legal/`) | Willian | Textos públicos + espelhos HTML |
| Site de apresentação/marketing | Willian | Fora deste repo — repositório próprio (nome a definir), stack web |

### Publicação na Play Store — quem faz o quê

Compliance não é de um dono só: parte é texto, parte é declaração sobre o
código, parte é titularidade legal.

| Item | Quem | Por quê |
|---|---|---|
| Textos legais e mantê-los em dia com o código | Willian | Mesmo dono dos arquivos acima |
| **Data Safety Form** | Gabriel (com apoio do Willian) | É declaração sobre o que o **código** coleta. Declarar diferente do que o APK pede é a causa nº1 de rejeição — exige conhecer `weather/`, `LocationHelper` e o manifesto. A tabela de rastreio em `CHECKLIST_PUBLICACAO.md` é a fonte |
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
