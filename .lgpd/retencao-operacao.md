# Retenção — execução e evidências

Revisão 19/10/2026 do rascunho importado da PR #52. Rotina ainda não adotada/executada por esta sessão. Texto público atual: Privacidade 1.14, seção 9. Procedimento operacional canônico: [rotinas-atendimento-retencao.md](rotinas-atendimento-retencao.md).

## Correções sobre a proposta anterior

- Conferência mensal poderia ultrapassar 60 dias após encerramento; usar fila de vencimentos revisada diariamente para atender ao prazo declarado.
- `older_than:60d` usa idade da mensagem, não data de encerramento do atendimento. Não usar essa busca como prova do vencimento correto. Marcadores não substituem o registro privado da data de encerramento/data-limite.
- Excluir só os itens pertinentes vencidos; não esvaziar indiscriminadamente toda a Lixeira. Considerar mensagens enviadas, anexos e cópias sob controle da equipe. Não prometer remoção instantânea de backups do provedor.
- Registro mínimo de pedidos/execução pode continuar identificável ou vinculável. Não qualificá-lo automaticamente como anônimo. Prazo de guarda precisa de justificativa própria, sem copiar cinco anos dos incidentes para todas as planilhas.

## Limites e pendências

Os 60 dias são regra confirmada do suporte; lista/licença de testadores, logs R2 e exportações exigem critérios próprios. Existem cache de último clima, cache HTTP e cache de prévias, com mecanismos diferentes; ver [mapa](data-map.md).

O prazo normativo de registro de incidentes permanece em [incidentes-registro.md](incidentes-registro.md); dados reais e evidências ficam em registro privado. Não declarar descarte executado por existir um procedimento escrito.

- [ ] Rafael confirmar executor, substituto, controle de vencimentos e locais de cópias.
- [ ] Adotar e testar a rotina em cenário fictício; registrar separadamente execuções reais.
- [ ] Definir critério de retenção de participantes e evidência mínima das rotinas.
- [ ] Conferir fornecedores no [roteiro administrativo](conferencia-administrativa.md).
