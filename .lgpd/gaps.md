# Lacunas de conformidade e plano de remediação

**Versão**: v0.1 (rascunho) · **Data**: 2026-10-08 · **Código**: `main` @ `2d01d16` (1.0.5, versionCode 6) · Skills `lgpd-legacy-retrofit` + `lgpd-audit`.
**Cenário adotado**: *B/C adaptado* — app **ainda não publicado** (teste interno), com documentação já existente. Roda a auditoria como "legado" (auditar o que existe) e mantém a mentalidade de privacy-by-design para o que vem depois.
**Limite**: análise estática de código e documentos. Não comprova o Play Console, o bucket nem o site. **Não é parecer jurídico** — o que depende de advogado está marcado **[JURÍDICO]**.

## Resumo

- **O que está bom**: o produto é minimalista em dados (sem conta, anúncios, analytics ou backend); localização aproximada e opcional; coordenadas arredondadas a ~1 km; consulta só com o wallpaper visível na home; freio contra bloqueio da MET; compras 100% no Google; Política em PT-BR, com resumo, versão, histórico e direitos do Art. 18.
- **O que falta antes de publicar**: identificação/contato no texto canônico, URL pública no ar, correção do fundamento de transferência internacional, alinhamento da oferta Premium (Termos × código), e a decisão sobre público 13+ / ECA Digital.
- **Os registros que a LGPD pede e não existiam** (ROPA, fornecedores, transferências, incidente, direitos do titular, dispensa de RIPD) foram criados como rascunho neste diretório.

## Legenda

Severidade: **Crítico** (resolver antes de publicar) · **Alto** (antes de publicar, ou logo depois) · **Médio** (próximas semanas) · **Baixo** (backlog). Esforço: B/M/A.

## Lacunas

### Crítico

| ID | Lacuna | Evidência | Ação | Esforço | Quem |
|---|---|---|---|---|---|
| **G01** | **Identificação do controlador e contato ausentes no texto canônico.** O Art. 9º, III e IV exige. O `.md` canônico tem `[PREENCHER]` (`docs/legal/PRIVACIDADE.md:56,58,60,363,384,419`; Termos §1); os HTML já têm "Rafael Huppes" e o e-mail. **Canônico e HTML divergem.** | Leitura direta | Preencher o canônico e **escolher uma fonte única** (o teste de sincronia não cobre o `.md`) | B | Gabriel + Rafael |
| **G02** | **Nenhuma URL pública no ar.** O Console exige a URL da política. O host `terra-livewallpaper.pages.dev` não resolveu em 04/10; domínio e hospedagem **não decididos**. | `docs/legal/PRIVACIDADE.md` §15; consolidação de 04/10 §6 | Definir domínio/hospedagem e publicar as 3 páginas (Willian) | M | Willian + Gabriel |
| **G03** | **Fundamento de transferência internacional citado de forma incorreta.** §7 cita "art. 33, II, alíneas 'a' e 'f'" — **a alínea "f" não existe**. E o inciso VIII (consentimento específico) não corresponde ao que se descreve. | `docs/legal/PRIVACIDADE.md` §7 (e HTML) | Decidir a hipótese por destino ([transfers/README.md](transfers/README.md)) e reescrever **[JURÍDICO]** | M | Advogado + Gabriel |
| **G04** | **Oferta do Premium incoerente entre código, tela e Termos.** Código, `PremiumScreen` e Termos §5.1 dizem que o Premium libera **todas as artes dos 5 cenários**. A decisão de 04/10 e o SPEC dizem: **Premium = efeitos e estilos; cenários vendidos à parte (R$ 1,99)**. `Catalogo.kt` tem `productId = null` e o Console só tem o Premium. Promessa pública ≠ entrega é risco de consumo. | `MainViewModel.isSceneUnlocked` (Premium ⇒ verdadeiro); `docs/dev/SPEC.md` (29/09); consolidação §2 | **[DECISÃO]** (a) alinhar texto ao código para o lançamento de 5 cenas, ou (b) separar no código e criar produtos avulsos. Antes, conferir no Console se há **compra real** sob a oferta antiga. | M–A | Gabriel |

### Alto

| ID | Lacuna | Evidência | Ação | Esforço | Quem |
|---|---|---|---|---|---|
| **G05** | **"Reportar problema" abre o e-mail pessoal do Rafael**, não o canal oficial. Além de contradizer os textos, **expõe o e-mail pessoal** (o app de e-mail mostra o destinatário). | `ui/components/ReportarProblemaDialog.kt` (`Suporte.EMAIL`) | Trocar a constante por `suporteterrabr@gmail.com` e testar | B | Gabriel |
| **G06** | **O atendimento por e-mail não está descrito na Política v1.4**, que diz (§4) que o app não coleta e-mail. O relato do app envia versão, modelo, Android, cenário/arte/estilo. Art. 9º, I, II e V. | `docs/legal/PRIVACIDADE.md` §4, §8; `ReportarProblemaDialog.kt` | Nova seção de atendimento (dados, Gmail, retenção) — ver [policies/README.md](policies/README.md) | B | Gabriel + advogado |
| **G07** | **"Legítimo interesse" declarado sem teste (LIA)** em §3.2 (IP) e §3.6 (backup). O Art. 10 exige LIA; e o IP não é tratado pelo Terra. | `docs/legal/PRIVACIDADE.md` §3.2, §3.6 | Remover a base nesses dois itens ([legal-basis.md](legal-basis.md)) | B | Gabriel |
| **G10** | **Encarregado: texto × indicação.** A Política diz "Não exigido (ATPP)"; você indicou Rafael como encarregado. | §1, §14; resposta à pergunta 3 | [encarregado.md](encarregado.md) — designar e publicar, ou manter a dispensa | B | Gabriel + Rafael |
| **G11** | **Público 13+ sem nenhum mecanismo no app + ECA Digital.** Lei 15.211/2025 vigente desde 17/03/2026 para serviços de "acesso provável" por menores. O app não tem tela de idade; a "autorização de responsável" está só nos Termos. ANPD priorizou crianças/adolescentes em 2026–27. | [eca-digital.md](eca-digital.md); grep de idade em `ui/` | **[JURÍDICO]** alcance real para este produto; **[DECISÃO]** 13+ ou 18+ | M | Advogado + Gabriel |
| **G12** | **Fornecedores sem registro nem classificação de papel.** A Política chama todos de "operadores técnicos". MET, Google e Cloudflare têm papéis diferentes; **não há DPA com ninguém**. | `docs/legal/PRIVACIDADE.md` §6 | [vendors/](vendors/) criados; **[JURÍDICO]** validar | M | Advogado |

### Médio

| ID | Lacuna | Evidência | Ação | Esforço | Quem |
|---|---|---|---|---|---|
| **G08** | **Backup inclui o cache de coordenadas.** As regras excluem só `atmosfera_plano.xml`; `atmosfera_weather_cache.xml` (lat/lon) pode ir ao backup do Google. | `res/xml/backup_rules.xml`, `data_extraction_rules.xml` | Excluir `atmosfera_weather_cache.xml` e `atmosfera_met_freio.xml` das duas regras; simplifica a Política | B | Gabriel |
| **G09** | **`Log.d` com coordenadas permanece no release** (`LocationHelper.kt:72`); não há regra R8 de remoção. | `weather/LocationHelper.kt:72`; `proguard-rules.pro` | Restringir a `BuildConfig.DEBUG` ou `-assumenosideeffects` | B | Gabriel |
| **G13** | **Registros obrigatórios ou recomendados não existiam**: ROPA (Art. 37), dispensa de RIPD, retenção, incidente (5 anos), fluxo de direitos. | — | Criados como rascunho neste diretório; **falta aprovação** | B | Rafael + advogado |
| **G14** | **Integridade do acervo**: manifesto e pacotes ficam no **mesmo bucket**; o app confere o hash do pacote contra o manifesto de lá. Quem controla o bucket controla os dois. Token de upload **sem validade**. | `engine/Acervo.kt`; PR #43 (rascunho) | Antes de ativar: manifesto assinado ou checagem independente; token com validade | M | Gabriel + Rafael |
| **G15** | **A Política descreve um download remoto que a 1.0.5 não faz** (`BASE_PADRAO` vazio). | `engine/Acervo.kt:86`; Política §3.8/§6.5 | Dizer que vale **a partir da versão que ativar** | B | Gabriel |
| **G17** | **Landing com textos antigos** (política 1.1 e termos 1.0 no repositório do site) e **três cópias dos HTML** sincronizadas só por teste parcial. | consolidação de 04/10 §4–5 | Fluxo de exportação conferido; teste cobrindo `public_html/` e o Markdown | M | Gabriel + Willian |
| **G18** | **Documentação desatualizada ou fora do Git.** `OPERACAO-POS-LANCAMENTO.md` ainda diz "conteúdo embarcado, sem CDN" (18/09); rascunhos `revisao-2026-10-05` e a consolidação de 04/10 estão **só na máquina do Gabriel** e são anteriores ao PR #46 (falam do worker). | `git ls-files`; docs | Atualizar, decidir o que entra no repositório, usar o índice [`docs/README.md`](../docs/README.md) | M | Gabriel |
| **G20** | **Pontos de consumo nos Termos para o advogado**: arrependimento (CDC art. 49) aplicado a compra digital pelo Google; reembolso "com o Google, não conosco"; foro. | `docs/legal/TERMOS.md` §5.4, §12 | **[JURÍDICO]** | M | Advogado |

### Baixo

| ID | Lacuna | Evidência | Ação | Esforço | Quem |
|---|---|---|---|---|---|
| **G16** | **Código morto com impacto de privacidade**: `LocationHelper.nomeDoLugar()` (Geocoder) não é chamado em lugar nenhum. Se voltar, o Geocoder pode enviar coordenadas a um provedor de rede do aparelho — a Política diz que vão "apenas" à MET. | `weather/LocationHelper.kt` | Remover ou documentar | B | Gabriel |

### Fora do escopo das skills LGPD (anotado para não se perder)

- **G21** — Licenças e autoria das artes, sprites e fontes; propriedade intelectual dos estilos de efeito (`engine/Estilo.kt`, já no checklist); atribuição CC BY 4.0 da MET. Pendência P12 da revisão de 05/10 e perguntas 19/20 do Rafael.

## Plano

**Esta semana (antes de gerar um AAB para público)**: G05, G07, G08, G09, G16 (código e textos curtos) · decidir G04, G10, G11 · iniciar G02 com o Willian.
**Antes de publicar**: G01, G03, G06, G12, G15, G20 · promover a Política v1.5 ([policies/README.md](policies/README.md)).
**Antes de ativar a Loja de packs**: G14, DPA/cláusulas da Cloudflare, atualizar [vendors/cloudflare-r2.md](vendors/cloudflare-r2.md).
**Revisão semestral**: todo o diretório.
