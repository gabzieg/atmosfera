# Plano até a Google Play — reconferido em 23/09/2026

> **⚠️ Escopo reaberto em 28-29/09/2026.** O parágrafo "Escopo registrado em
> 23/09" abaixo **não vale mais** — o acervo completo (69 cenários) voltou a
> ser objetivo de lançamento, entregue sob demanda via Cloudflare R2, não mais
> adiado. Ver
> [PROPOSTA-PREMIUM-ACERVO-REMOTO-2026-09-28.md](PROPOSTA-PREMIUM-ACERVO-REMOTO-2026-09-28.md)
> (decisões confirmadas §5.1) e a monetização atualizada em
> [SPEC.md](SPEC.md#monetização--o-que-é-grátis-e-o-que-é-pago). Premium dá
> só os efeitos, nunca cenário/pack. A sequência de trabalho da proposta
> (Etapas A-G) é mais atual que as "Fases" abaixo, que descrevem o plano do
> escopo local-only já superado — ainda úteis para B02-B07 (Premium, compras,
> legal, release), não para B01 (entrega de conteúdo).

Base técnica: árvore de trabalho da versão **3 (1.0.2)**. [Auditoria consolidada](AUDITORIA-PUBLICACAO-2026-09-17.md). **Ainda em teste interno.** `[x]` significa evidência conferida; `[ ]` continua pendente.

**Escopo registrado em 23/09 (superado, ver aviso acima):** cinco cenários embarcados, cada um com uma arte grátis. O Premium libera as demais artes disponíveis, todos os estilos e os oito efeitos vivos. Os outros 69 cenários ficam em `content-packs/cenarios-futuros/`; PAD/download são evolução posterior.

## Fases e dependências

| Fase | Situação | Próxima saída verificável |
|---|---|---|
| 0 — Oferta e serviços | Parcial | Lista real de artes vendidas, escopo documentado, licença/custos do clima. |
| 1 — Entrega local | Superada — ver Etapas A-G da proposta de 28/09 | O AAB local-only não é mais o alvo final; entrega passa a ser local + acervo remoto. |
| 2 — Premium e compras | Produto ativo; teste pendente | Compra, liberação e restauração aprovadas pela Play. Premium agora só libera efeitos — conferir que o código não libera cenário nenhum via Premium. |
| 3 — Legal e ficha | Parcial | HTML + Markdown coerentes, sem placeholders, URLs públicas e Console preenchido. Política de privacidade já atualizada pro servidor R2 (29/09). |
| 4 — Release | Build aprovado; comportamento reprovado no clima | Consulta real funcional no release, matriz de aparelhos e jornadas aprovada. |
| 5 — Teste interno | Ativo | Testers já instalaram; publicar versão 2 e validar a compra real. |
| 6 — Publicação | Não comprovada | Revisão e distribuição pública concluídas; inicia D0 da operação. |

Responsável técnico conforme documentos: Gabriel. Titular/publicador e responsável legal devem ser confirmados pela conta, não inferidos; os HTML identificam Rafael Huppes. A conta, produtos e recrutamento de testers podem avançar em paralelo ao código. Não fixar data de lançamento antes de resolver P0 e conferir o Console.

## Prioridade 1 — Clima funcional no release (B07)

- [x] Gerar APK/AAB release e instalar o APK no emulador.
- [x] Confirmar duas aberturas e navegação Home → Loja → Premium.
- [x] Confirmar regra de preservação do construtor Room no código.
- [ ] Corrigir `Class cannot be cast to ParameterizedType` reproduzido nas duas aberturas; investigar reflexão/tipos genéricos e compatibilidade de versões com stack trace.
- [ ] Fazer a Home sair de “Carregando clima…” em falha e mostrar condição útil de erro/cache.
- [ ] Receber dado meteorológico válido, atualizar cache e comprovar efeito no wallpaper no release.
- [ ] Validar rede indisponível, retry do Worker, permissão negada/revogada, posição indisponível, fuso e cache antigo.
- [ ] Se ajustar Kotlin/AGP/R8, fazer alteração isolada e validar em execução antes de integrá-la; não repetir migração ampla apenas para zerar warning.

Referência: [matriz Kotlin/R8](https://developer.android.com/build/kotlin-support). Build verde não substitui esta etapa.

## Prioridade 2 — Oferta e entrega embarcadas (B01)

- [x] Limitar o catálogo embarcado a **cinco cenários**: cabana, bruxa, lavanda, esfinge e jardim.
- [x] Regerar o [CSV de revisão](../loja/PRODUTOS-REVISAO.csv) com a oferta real.
- [x] Ofertar somente variantes com fundo local e impedir telas “Em breve” dentro dos cinco cenários.
- [x] Mover 69 cenários para `content-packs/cenarios-futuros/`, fora do AAB.
- [x] Atualizar SPEC/checklist/ficha para o novo escopo.
- [ ] Conferir direitos comerciais de todas as artes, fontes e sprites; cortes no catálogo não substituem revisão dos arquivos distribuídos.
- [x] Medir o AAB local: **82,36 MiB**, contra 405,60 MiB na versão anterior.
- [ ] Conferir o tamanho de download por aparelho no Play Console e testar instalação em pouco espaço.

Campos do CSV são inventário local: `artes_com_fundo_local` não significa renderização aprovada; `status_console=nao_verificado` não significa produto inexistente. CSV não é formato oficial de importação.

## Prioridade 3 — Premium e compras (B02/B03)

- [x] Gate de estilos em `setEffectStyle`: pixel livre; outros exigem Premium.
- [x] Cadeados na UI e entrada do comparador Premium navegável.
- [x] Consulta de compras em `onResume`, restauração de item já possuído e logs de cancelamento/erro/acknowledge.
- [ ] Confirmar regra comercial de estilos; unificar e aplicar direito também no WallpaperService e após revogação/restauração.
- [ ] Implementar prévia/explicação/compra ao tocar estilo bloqueado, sem aplicação permanente indevida.
- [ ] Configurar verificação de compras; eliminar aceitação silenciosa por chave vazia em produção.
- [ ] Posse de cenários observável e estados visíveis de pendência, cancelamento, falha e sucesso.
- [ ] Confirmação com tratamento/repetição confiável; log sozinho não resolve falha de acknowledge.
- [x] Limitar a oferta ao SKU `terra_premium`; produto e opção de compra ativos no Brasil por R$ 49,90.
- [ ] Testar compra → liberação → aplicação local; pendência aprovada/negada; reembolso; reinstalação; outro aparelho; offline.

Usar testadores de licença e a distribuição Play apropriada. [Integração Billing](https://developer.android.com/google/play/billing/integrate).

## Prioridade 4 — Clima comercial, legal e ficha (B04–B06)

- [ ] Regularizar o provedor para uso comercial, confirmar atribuição e custos. [Open-Meteo](https://open-meteo.com/en/terms).
- [x] Três conjuntos HTML sincronizados manualmente e teste docs/assets aprovado.
- [x] Identidade/contato/URL preenchidos nos HTML — validade e correspondência com titular ainda precisam de conferência.
- [ ] Atualizar os Markdown canônicos, ainda com placeholders e divergências.
- [ ] Adicionar `public_html` ao teste e às entradas do Gradle; impedir placeholders visíveis no material publicado.
- [ ] Definir vigência verdadeira da política usada no teste, sem inventar data de lançamento; revisar enquadramento legal citado.
- [ ] Unificar suporte do app/site e corrigir texto de reembolso.
- [ ] Concluir rebrand: Home ainda mostra “Atmosfera”; conferir tutoriais, mensagens e screenshots.
- [ ] Publicar e verificar HTTPS/HTTP e conteúdo de privacidade, termos e contato sem login. DNS da URL citada falhou na reconferência local.
- [ ] Preencher Data Safety conforme arquitetura embarcada e provedor de clima final; revisar localização, Geocoder, logs, backup, suporte e SDKs.
- [ ] Preencher IARC, anúncios, público-alvo, acesso ao app e demais declarações mostradas pelo Console; avaliar cenas de guerra/armas, sem assumir classificação.
- [ ] Revisar ficha, preços reais, ícone 512×512, gráfico 1024×500 e screenshots atuais; não anunciar variantes ausentes.

## Prioridade 5 — Aceite técnico completo

- [x] Testes JVM aprovados e AAB release assinado concluído em 23/09; lint vital sem erros.
- [x] Alinhamento ZIP do APK release passou em `zipalign -P 16 -c 4`.
- [ ] Confirmar alinhamento ELF e execução em ambiente de 16 KB; emulador usado tem páginas de 4 KB.
- [ ] Validar APKs derivados do AAB/canal interno, Play App Signing, assinatura de upload, backup de chave e versionCode disponível.
- [ ] Arquivar AAB/mapping/hash e notas da versão candidata após as correções.

| Teste obrigatório | Critério de aceite |
|---|---|
| Instalação limpa online/offline | Onboarding e amostras utilizáveis; sem carregamento infinito. |
| Clima no release | Consulta válida/cache/erro controlado; Worker não relata sucesso indevido. |
| Todas as artes ofertadas | Preview e aplicação corretos, arquivos completos, sem conteúdo vendido inacessível. |
| Premium e compras | Compra, confirmação, restauração e revogação corretas na UI e serviço. |
| Ciclo do wallpaper | Trocas rápidas, tela apagada, home, preview, reinício, superfície recriada, sem loop duplicado/crash. |
| Bateria e memória | Sessão prolongada, pouca RAM, comparação com wallpaper estático e registro de consumo. |
| Compatibilidade | API 26, intermediária e atual; aparelho físico; 16 KB; diferentes fabricantes. |
| Acessibilidade | TalkBack, fonte ampliada, tela grande/rotação e controles sem cortes. |
| Pré-lançamento Play | Relatório avaliado e teste manual de wallpaper complementando automação. |

## Prioridade 6 — Teste e publicação

- [ ] Confirmar titular, tipo/data da conta, perfil de pagamentos e verificações solicitadas.
- [x] Primeira versão distribuída em teste interno e instalada pelos testadores.
- [ ] Enviar a versão 3 corrigida ao teste interno e repetir o roteiro, incluindo compra Premium, brilho e rolagem lateral.
- [ ] Cumprir a exigência aplicável à conta: para contas pessoais novas abrangidas pela regra, 12 testers inscritos continuamente por 14 dias e solicitação de acesso à produção; prazo sozinho não garante aprovação. [Requisitos oficiais](https://support.google.com/googleplay/android-developer/answer/14151465).
- [ ] Tratar feedback, obter acesso à produção e enviar ficha/AAB para revisão.
- [ ] Conferir primeira publicação nos países escolhidos; distribuição percentual é para updates, não para a primeira publicação. [Releases](https://support.google.com/googleplay/android-developer/answer/9859348).
- [ ] Registrar data real e iniciar [operação de 90 dias](OPERACAO-POS-LANCAMENTO.md).

## Evolução adiada — entrega remota

Manifesto, CDN, empacotador, compra → download → aplicação, integridade, gestão de armazenamento e rollback serão necessários **se a decisão de entrega remota for retomada**. Antes disso, atualizar privacidade e Data Safety. Essas tarefas não devem aparecer como concluídas nem impedir o MVP embarcado por uma exigência documental antiga.
