# Terra — Tasks (fase atual)

> Estado do trabalho em andamento, literalmente agora. Diferente do
> `ROADMAP.md` (fases do produto até publicar), isto é o dia a dia — o que
> está pela metade, o que decidir, o que testar. Atualizar sempre que algo
> muda de status, não deixar ficar mentiroso.
>
> **Histórico de trabalho concluído não mora aqui** — mora no `git log`, que
> não desatualiza. Este arquivo ficou dois meses parado (2026-08-10 → 2026-10-09)
> justamente por acumular item feito e referenciar uma branch que já tinha sido
> mesclada.

**Última atualização:** 2026-10-09 · `main` @ `2d01d16`

## Estado (confirmado em 2026-10-09)

- **App**: versão **1.0.5, versionCode 6** na `main`. AAB assinado gerado em 06/10 (SHA-256 `19b88b3d…e353e6bb`). **Não está confirmado** que foi enviado ao Console nem que o código 6 está livre — só o Gabriel vê o Console.
- **Gate**: `testDebugUnitTest lintDebug assembleDebug` verde, **69 testes** unitários (14 classes), CI verde na `main`.
- **Teste em aparelho do clima só na home (PR #46)**: **ainda não feito.** O emulador não sobe a partir do agente (virtualização negada no Windows); o Gabriel precisa ligá-lo, ou testar pelo teste interno em um celular. Roteiro em [CLIMA-SOMENTE-HOME.md](CLIMA-SOMENTE-HOME.md).
- **Rafael**: sem commits novos desde o #46 (05/10 22:25).
- **PRs abertos**: #43 (Rafael, **rascunho**: documentação do R2), #48 (**rascunho**: estrutura `.lgpd/` e índice `docs/README.md`), #45 e #47 (Dependabot — **#47 espera teste em celular**; mexe em bibliotecas do app).

## Decisões pendentes do Gabriel

Detalhe e opções em `.lgpd/gaps.md` e `.lgpd/STATUS.md` — **ainda no PR #48 (rascunho), não na `main`**.

1. **Escopo do lançamento** — 5 cenas embutidas agora e packs depois, ou catálogo remoto completo. O SPEC (29/09) diz catálogo completo; o Gabriel disse 5 cenas (05/10); o **Rafael não confirmou a mudança**. A Loja remota **não existe** no app (só o painel de debug baixa; `Acervo.BASE_PADRAO` vazio).
2. **O que o Premium libera nas 5 cenas** — o código e os Termos §5.1 dizem "todas as artes"; a decisão de 04/10 e o SPEC dizem "só efeitos e estilos". Conferir no Console se houve compra real antes de mudar.
3. **Público 13+ ou 18+** (ECA Digital).
4. **Encarregado** — designar o Rafael ou manter a dispensa de ATPP.
5. **Retenção do atendimento** — 60 dias após resolver?
6. **Qual texto legal vale** — o `.md` canônico (com `[PREENCHER]`) e os HTML divergem; e o que fazer com `docs/legal/revisao-2026-10-05/` (fora do Git).
7. **Correções pequenas de código** (G05 e-mail do "Reportar problema", G08 backup, G09 log, G16 código morto).
8. **Advogado** — quem revisa (G03, G11, G12, G20) e quando.

## Em andamento / a fazer

- [ ] **Teste em aparelho do #46**: clima atualizando ao voltar à home; instalação nova sem cache (a tela Início mostra "Carregando clima…" até o wallpaper rodar?); nome da cidade (a função que o resolvia não é mais chamada); nenhuma consulta com app aberto, tela apagada ou bloqueada.
- [ ] **Subir o AAB 1.0.5 no teste interno** e conferir o código 6.
- [ ] **Play Console**: Data Safety, IARC, público-alvo, ficha da loja, contato do app (o perfil público ainda usa e-mail pessoal), chave de licença `playLicensePublicKey`, produtos (hoje só `terra_premium`).
- [ ] **Teste fechado**: 12 testadores por 14 dias seguidos; **o coordenador ainda não foi definido** (pergunta 22 do Rafael).
- [ ] **Medir o motor em aparelho antigo** — decide o `minSdk 26`.
- [ ] **Compra de verdade no teste**: precisa de conta Google logada; o AVD não tem.
- [ ] **Ficha da loja** ([loja/FICHA.md](../loja/FICHA.md)): ainda descreve cenários que não estão no app; revisar quando o escopo (decisão 1) fechar.
- [ ] **Registro de autoria/licença das artes e sprites** (pergunta 19 do Rafael). Os estilos `pixel_mario` e `pixel_zelda` já saíram do código; `Estilo.kt` tem 16 estilos.
- [ ] **Conformidade**: lacunas G01–G21 em `.lgpd/gaps.md`.

## Esperando outras pessoas

- **Willian** — domínio, hospedagem e publicação das 3 páginas legais (a política precisa de URL pública para o Console).
- **Rafael** — marcar o #43 como pronto; registro de licenças; revisão jurídica; operação do R2 (só se o catálogo remoto entrar).
- **Segurança da chave de upload** (ainda aberto; o agente não lê o arquivo): a senha continua em texto em `C:\Users\gbrus\Chaves\SENHA-LEIA-E-APAGUE.txt` — mover para um gerenciador de senhas e apagar o `.txt`; e **backup do `.jks` em dois lugares**. Não enviar a chave ao Rafael: o AAB pode ser gerado por aqui ou por um fluxo na CI.

## Decisões tomadas (não reabrir sem motivo novo)

- **Clima**: MET Norway (2026-10-03, a API gratuita da Open-Meteo é só não comercial); consulta **só com o wallpaper visível na home, tela acordada e desbloqueada** (2026-10-05, PR #46); freio persistente em 429/403.
- **Monetização** (SPEC 29/09, reconfirmada 04/10): Premium **R$ 49,90**, compra única, só efeitos e estilos; cenários avulsos **R$ 1,99**. Packs comerciais ainda indefinidos. **O código ainda não separa os dois** (decisão pendente 2).
- **Acervo remoto (R2)**: objetivo de lançamento desde 29/09, a pedido do Rafael — **sujeito à decisão pendente 1**.
- **Público 13+** (2026-10-04) — sujeito à decisão pendente 3.
- **Prévia ao vivo fora da Home** (2026-08-10): fica no onboarding, no detalhe da Loja e no comparador do Premium.
- **Descartado: desenhar a cena em bitmap reduzido e ampliar** (`Canvas(Bitmap)` é software; piorou de 25 ms para 38 ms).
- **Cenário sem asset some da Loja por verificação real** (`cenarioTemAsset` em `ui/components/SceneThumbnail.kt`), não por lista fixa.
- **Toda mudança na `main` entra por PR**, 0 aprovações, CI `build` obrigatória, merge commit (ver `CLAUDE.md`).
- **Sem anúncios** no lançamento; **CodeQL descartado** (repositório privado em plano free).
- **AGP 9 não** (testado e revertido em 09/2026: passou na CI e o app não abria) — ver [DECISAO-AGP-9-MIGRACAO.md](DECISAO-AGP-9-MIGRACAO.md). O Dependabot está configurado para não propor os majors que quebram.

## Quem toca o quê

Fonte completa em [SPEC.md](SPEC.md) e `.github/CODEOWNERS`.

| Setor | Dono | Estado |
|---|---|---|
| `ui/`, `weather/`, `service/`, `billing/`, `debug/` | Gabriel | Ativo |
| `engine/` + `assets/atmosfera/` | Gabriel e Rafael | O Rafael voltou a corrigir o motor em 23/09; combinar quem mexe em qual arquivo |
| Clima só na home (`ClimaNaHome`) | Rafael (autor do #46) | Mesclado sem revisão de outra pessoa; falta o teste em aparelho |
| Documentos legais e Data Safety | Gabriel | Faltam os `[PREENCHER]`, a hospedagem e a revisão jurídica |
| Site de apresentação e hospedagem | Willian | Retomou em outubro; sem domínio definido |
| Conta Play Console | Gabriel | Ativa (o produto `terra_premium` existe) |
| Cloudflare R2 | Rafael (titular) | Bucket de teste; sem URL de produção |

## Backlog (não puxar sem avisar)

- Loja remota no app: listar o manifesto, comprar, baixar com progresso e aplicar; preencher `BASE_PADRAO`; manifesto assinado. **Só depois da decisão pendente 1.**
- Cobertura de teste de comportamento na UI (hoje só testes JVM; zero instrumentado).
- Revisão cruzada entre `TERMOS.md` e `PRIVACIDADE.md`: foram escritos por autores diferentes, em momentos diferentes (o `.lgpd/` já cobre a Política; falta cruzar com os Termos §5).
- Módulos Gradle `:engine`/`:app`.
