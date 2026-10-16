# Hipóteses de bases legais — para avaliação

**Etapa 2:** hipóteses abaixo confrontadas com as [evidências de implementação e serviços](evidencias-privacidade-etapa2.md). A preparação não escolhe base em nome do responsável. O teste atual é de adultos/Brasil; nenhuma nova coleta ou finalidade foi autorizada por esta análise.

**Conciliação com `0d36b4a`:** recrutamento do teste restrito a adultos no Brasil. As hipóteses abaixo não foram aprovadas só por terem sido inseridas na política. Privacidade 1.12 identifica esse caráter proposto; eventual ampliação a 13+ mantém análise específica pendente.

15/10/2026. Proposta analítica, não decisão já adotada pelo controlador nem ROPA final. Correspondência com [mapa](data-map.md). Referências oficiais em [fontes](fontes.md), especialmente LGPD arts. 7º–10, 14–16 e 18–19. Permissão Android, inscrição na faixa e uso do app não são consentimento genérico.

| Atividade | Hipótese a avaliar | Justificativa e condição de fechamento |
|---|---|---|
| A01 — clima/localização | Art. 7º, V, se demonstrada necessidade para a função específica solicitada; art. 7º, I, se a opção de localização for estruturada em consentimento válido | Comparar a função solicitada, minimização, alternativa São Paulo e aviso; registrar uma base coerente por operação. Não declarar ambas como autorização cumulativa automática. Se consentimento, prever informação, revogação e evidência proporcional. Retenção local após revogação merece avaliação. |
| A02 — preferências/HD local | Art. 7º, V, para execução da função solicitada quando houver dado pessoal | Vínculo e necessidade devem estar demonstrados. Conteúdo genérico isolado não é automaticamente dado pessoal; backup e dados técnicos são operações próprias. |
| A03 — compras | Art. 7º, V | Verificação/entrega/restauração sustentam o serviço solicitado. Obrigações legais específicas exigem identificar a norma; não justificar retenção ilimitada apenas com compra ou prescrição. Play tem operações próprias. |
| A04 — prévias e entrega | Art. 7º, V, para conteúdo solicitado | Identificar necessidade dos dados técnicos e explicar que abrir variante provoca prévia automática. Logs usados para finalidade adicional de segurança exigem análise própria e não herdam automaticamente a base de entrega. |
| A05 — suporte/direitos | Art. 7º, V, para suporte ligado ao serviço; II para obrigação legal identificada; VI para defesa em processo concreto | Separar finalidades, mínimo de dados e exceções de guarda. Não usar os três fundamentos indiscriminadamente para toda mensagem. Sensíveis incidentais exigem tratamento restrito e hipótese do art. 11, se necessária sua conservação. |
| A06 — navegação no site | Pendente por operação e configuração | Identificar cookies/recursos e papéis do Sites. Não atribuir consentimento ou legítimo interesse ao Google em nome dele. Avaliar o tratamento decidido pelo responsável do Terra. |
| A07 — participantes/feedback | Art. 7º, V, como hipótese para participação solicitada; demais finalidades separadas | Formalizar finalidade, acesso e término da lista; não usar e-mails para marketing por extensão. Participantes adolescentes exigem melhor interesse e avaliação específica. |
| A08 — diagnóstico/segurança | Art. 7º, IX e art. 10 somente se necessidade e balanceamento forem demonstrados; V quando estritamente ligado ao suporte solicitado | Nenhuma LIA foi concluída. Primeiro conferir campos, destinatários e retenção. A existência do log não aprova a finalidade ou a base. |

## O que já pode ser aproveitado

Para adotar cada hipótese, registrar finalidade específica, dados mínimos, necessidade, alternativa menos intrusiva, retenção e quem aprovou. Em A01, diferenciar obtenção de localização, consulta externa e cache; o cache HTTP adicional não herda um prazo de descarte do último registro. Em A04, diferenciar entrega de conteúdo e logs de segurança. Em A05, distinguir suporte solicitado, pedido legal e defesa concreta. Em A07, distinguir cadastro da faixa, licença e feedback; a rotina proposta ainda exige critério de retenção confirmado por Rafael. A06/A08 não recebem fundamento genérico antes da conferência dos recursos e finalidades efetivos. Não foram preenchidos registros de aprovação nem emitida LIA final.

As bases para preferências, entrega, compra e suporte podem ser propostas com as evidências atuais; não é necessário reinventar arquitetura ou perguntar novamente sobre a oferta. Localização, logs, site e adolescentes exigem análise mais específica. Não foi implementado ledger, coleta de documento, biometria ou nova interface de consentimento por esta entrega.

Enunciado ANPD 1/2023 admite hipóteses dos arts. 7º e 11 para crianças e adolescentes, com prevalência do melhor interesse avaliado no caso. Isso não dispensa requisitos específicos do ECA Digital ou de consentimento quando aplicáveis.
