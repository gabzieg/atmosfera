# Retenção e eliminação

**Versão**: v0.1 (rascunho) · **Data**: 2026-10-08 · Skill `lgpd-retention-erasure` (Arts. 15–16 LGPD).
Princípio: **retenção legal obrigatória > retenção contratual > vontade do titular** (Art. 18, VI). Sem prazo escrito, "guardar para sempre" é uma decisão tomada por omissão.

## Tabela

| Dado | Onde | Prazo | Gatilho | Destino | Quem executa |
|---|---|---|---|---|---|
| Coordenadas e último clima (A001) | Aparelho do usuário | Só o último registro | Sobrescrito a cada consulta bem-sucedida | Apagado ao limpar dados do app ou desinstalar | O próprio usuário |
| Preferências e flags (A003) | Aparelho | Enquanto instalado | — | Idem | O próprio usuário |
| Backup do Android (A006) | Conta Google do usuário | Política do Google | — | Sob controle do usuário. **Reduzível**: excluir o cache de clima do backup (G08) | Usuário / Gabriel (código) |
| Logs da MET Norway (A001/A002) | Servidores da MET | Até 90 dias (política da MET) | — | Eliminação pela MET | MET |
| Logs de acesso da Cloudflare (A007, inativa) | Cloudflare | Conforme política da Cloudflare | — | — | Cloudflare |
| Histórico de compra (A004) | Conta Google do usuário | Política do Google | — | — | Google |
| **E-mails de atendimento** (A005) | Caixa Gmail do suporte | **Proposta: 60 dias após resolver o pedido** **[DECISÃO]** | Data da resolução | Eliminação, **incluindo a lixeira do Gmail** (a lixeira guarda por cerca de 30 dias, então o descarte efetivo leva até ~90 dias) | Responsável pela caixa |
| E-mail que comprove atendimento a pedido de titular, reembolso ou reclamação | Idem | Pode ser mantido pelo prazo necessário para **defesa em processo** ou obrigação legal; registrar a razão | Fim do prazo da obrigação | Eliminação | **[JURÍDICO]** |
| Registro mínimo de pedidos de titular (data, tipo, resposta; **sem** conteúdo) | Planilha local do responsável | 5 anos (prova de conformidade; alinha ao Art. 10 da Res. 15/2024) **[DECISÃO]** | Data da resposta | Eliminação | Encarregado |
| Registro de incidentes (notificados ou não) | [incidents/log.md](incidents/log.md) | **5 anos** (Res. CD/ANPD nº 15/2024, art. 10) | Data do incidente | Eliminação | Encarregado |

## Regras de execução

- **Digital**: apagar da caixa de entrada, enviados, marcadores e lixeira; apagar anexos baixados em pastas locais.
- **Cópias e backups**: o prazo de eliminação efetivo inclui o ciclo do fornecedor (Gmail/Google). Ao responder a um titular, dizer isso — não prometer eliminação instantânea.
- **Conservação apesar do pedido de eliminação**: quando houver obrigação legal ou defesa em processo, informar ao titular **o que** foi mantido e **por quê** (Art. 16).
- **Eliminação periódica**: revisão **mensal** da caixa de suporte (filtro por rótulo "resolvido" com mais de 60 dias). Não há job automático porque não há banco de dados.

## Pendências

- [ ] **[DECISÃO]** Confirmar os 60 dias e a forma de execução (quem revisa, com que frequência).
- [ ] **[JURÍDICO]** Hipóteses de conservação para atendimento de consumo e reembolso.
- [ ] Alinhar a Política §8 (hoje não lista o atendimento) e a página de Contato.
