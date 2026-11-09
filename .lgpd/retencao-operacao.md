# Retenção — execução e evidências

**Estado vigente em 09/11/2026, base `8c1a40e`, #91:** R05 resolvido no marco documental por declaração em 28/10: responsável e rotina de encerramento/exclusão definitiva em 60 dias, incluindo lixeira e cópias locais sob controle, com exceções restritas e justificadas. [Origem](revisoes/2026-10-28-documentos/REVISAO.md#continuação--informações-operacionais-do-usuário-28102026). Não solicitar novamente adoção/executor como pergunta genérica, nem exigir acesso a mensagens reais para aceitar essa declaração.

A [conferência A3 de 24–26/10](conferencia-a3-rafael-2026-10-26.md) também registrou código/agendamento/execuções e seus limites: contagens zero não provam exclusão diária real, e a origem dos dados de teste não foi demonstrada. A confirmação posterior da rotina supera a falta de resposta, não elimina esses limites nem atesta implementação corrigida de cada ponto técnico do código antigo. Esta sessão não acessou Gmail, Apps Script ou dados privados e não ensaiou descarte.

R04: lista no Console e retirada ao encerrar teste já definidos em 28/10; controle A4 separado, em arquivo protegido, com descarte ao encerrar salvo necessidade específica justificada, escolhido em 07/11. Não aplicar 60 dias por analogia. R02: justificativa do último clima/cache HTTP aceita para teste adulto, sem teto fixo; TTL não é apagamento. Fontes atuais: Privacidade 1.22/Termos 1.12/Contato 1.10, redação aceita ainda rascunho. Operação e lacunas remanescentes: [ROPA](ropa.md). A fila é TASKS; as caixas antigas abaixo são registro da preparação, não tarefas novas.

## Histórico de 19/10 — superado nos pontos delimitados acima

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
