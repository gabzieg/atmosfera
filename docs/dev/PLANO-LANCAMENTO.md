# Plano até a Google Play — reconferido em 18/09/2026

Base técnica **a61c387**. [Auditoria consolidada](AUDITORIA-PUBLICACAO-2026-09-17.md). **Não liberado para produção.** `[x]` significa evidência conferida; `[ ]` continua pendente. Código implementado não equivale a fluxo comercial aprovado.

**Escopo registrado em 18/09:** lançamento com artes embarcadas. R2/PAD/download são evolução adiada. A SPEC ainda contradiz essa decisão e deve ser harmonizada. Não há motivo para refazer o motor ou descartar as correções preservadas.

## Fases e dependências

| Fase | Situação | Próxima saída verificável |
|---|---|---|
| 0 — Oferta e serviços | Parcial | Lista real de artes vendidas, escopo documentado, licença/custos do clima. |
| 1 — Entrega local | Parcial, prioridade imediata | Nenhuma variante ofertada sem arquivos/renderização; cinco amostras funcionando. |
| 2 — Premium e compras | Parcial | Direito verificado na UI/serviço; pagamento e restauração aprovados pela Play. |
| 3 — Legal e ficha | Parcial | HTML + Markdown coerentes, sem placeholders, URLs públicas e Console preenchido. |
| 4 — Release | Build aprovado; comportamento reprovado no clima | Consulta real funcional no release, matriz de aparelhos e jornadas aprovada. |
| 5 — Teste fechado | Não comprovado | Testers/feedback/requisitos da conta e acesso à produção aprovados. |
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

- [x] Reconferir **74 cenários + Premium**, cinco amostras com fundo e 16 folhas de estilo existentes.
- [x] Regerar [CSV de revisão](../loja/PRODUTOS-REVISAO.csv), incluindo variantes declaradas, presentes e ausentes.
- [ ] Resolver **257 variantes sem fundo local**, dentre 335 declaradas: embarcar o que será vendido ou retirar essas variantes da oferta.
- [ ] Validar demais arquivos além de `fundo.png`, renderização, preview e aplicação de cada variante ofertada.
- [ ] Remover repetição de pixel em `artesDoCenario()` e tratar miniaturas “Em breve”.
- [ ] Atualizar SPEC/checklist/ficha para entrega embarcada e oferta exata; confirmar eventual subconjunto de lançamento.
- [ ] Conferir direitos comerciais de todas as artes, fontes e sprites; cortes no catálogo não substituem revisão dos arquivos distribuídos.
- [ ] Medir download comprimido por aparelho no Console/bundletool e instalação em pouco espaço. O AAB local atual tem 405,60 MiB.

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
- [ ] Conferir 75 IDs ou limitar oferta explicitamente; preços/países/disponibilidade no Console.
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

- [x] 34 testes JVM aprovados; lint debug sem erros (20 avisos); builds debug/release/AAB concluídos em 18/09.
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
- [ ] Teste interno com candidato corrigido; depois teste fechado com roteiro e evidências.
- [ ] Cumprir a exigência aplicável à conta: para contas pessoais novas abrangidas pela regra, 12 testers inscritos continuamente por 14 dias e solicitação de acesso à produção; prazo sozinho não garante aprovação. [Requisitos oficiais](https://support.google.com/googleplay/android-developer/answer/14151465).
- [ ] Tratar feedback, obter acesso à produção e enviar ficha/AAB para revisão.
- [ ] Conferir primeira publicação nos países escolhidos; distribuição percentual é para updates, não para a primeira publicação. [Releases](https://support.google.com/googleplay/android-developer/answer/9859348).
- [ ] Registrar data real e iniciar [operação de 90 dias](OPERACAO-POS-LANCAMENTO.md).

## Evolução adiada — entrega remota

Manifesto, CDN, empacotador, compra → download → aplicação, integridade, gestão de armazenamento e rollback serão necessários **se a decisão de entrega remota for retomada**. Antes disso, atualizar privacidade e Data Safety. Essas tarefas não devem aparecer como concluídas nem impedir o MVP embarcado por uma exigência documental antiga.
