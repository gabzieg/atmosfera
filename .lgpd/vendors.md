# Fornecedores, retenção e fluxos internacionais

**Conciliação em 15/10:** Rafael confirmou que logs, retenção e acessos dos fornecedores ainda não foram definidos/conferidos. O texto público 1.12 não presume papéis únicos, adesão ao DPA ou ausência de controle administrativo. As pendências abaixo continuam abertas.

Análise preliminar em 15/10/2026. Não foram acessados painéis, contratos privados ou faturas. Papéis não são atribuídos apenas pela marca do serviço. Referências: [fontes](fontes.md), LGPD arts. 9º, 33–39; Resolução 19/2024, arts. 4º–6º do regulamento.

| Serviço | O que está comprovado | O que falta comprovar |
|---|---|---|
| MET Norway | Código consulta API diretamente, com arredondamento e identificação do app. Termos oficiais dizem que IP e possíveis coordenadas da consulta são registrados em logs. | Prazo de retenção e papéis por operação; destinatários/local efetivo. Se o fluxo caracterizar transferência, analisar adequação cobrindo a Noruega, não presumir necessidade de SCC para esse destino. |
| Google Play/Billing | Código consulta/valida/confirma compras, restaura direitos e não recebe dados de cartão pelo Billing. | Instrumentos aplicáveis à conta, práticas do SDK/artefato final, permissões e configuração de testadores. Não equiparar conta de testador à conta de licença. |
| Play services localização/Android backup | Uso do provedor de localização e exclusões declaradas de clima/direitos/acervo. | Práticas dos SDKs e comportamento do candidato/versão Android; cópias antigas não são apagadas por nova regra. |
| Gmail/Google Sites | Canal e hospedagem registrados pelo titular; Sites informado como publicado. | Tipo de conta/termos aplicáveis, acessos autorizados, exclusão do atendimento, cookies/recursos do site e verificação pública. Não presumir contrato de Workspace numa conta Gmail. |
| Cloudflare R2 | Requisições de arquivos, caminhos e User-Agent no código; bucket de testes público. Existe DPA público da Cloudflare. | Aplicabilidade e versão contratada, mecanismo de transferência, destinos, subprocessadores, logs/exportações/retenção e acesso administrativo. Página pública de DPA não comprova adesão da conta. |

## Enquadramento internacional

Separar coleta direta por agente estrangeiro de transferência entre agentes, conforme arts. 4º–6º do regulamento da Resolução 19/2024. Uma requisição para domínio estrangeiro, por si só, não encerra essa análise nem elimina deveres de transparência.

A Resolução 32/2026 reconhece adequação abrangendo UE e países do EEE indicados, incluindo Noruega. Sua existência corrige a referência desatualizada da skill. É necessário confirmar o fluxo e o destinatário da MET; não se estende a qualquer tratamento Google/Cloudflare ou a transferências posteriores sem avaliação.

## Evidências administrativas mínimas, sem segredos

1. Por serviço: tipo de conta, instrumento/versão aplicável e registro da adesão, com dados pessoais omitidos em eventual resumo público.
2. Logs: categorias, acesso, exportações habilitadas, duração configurada ou indicação de que o prazo pertence ao serviço e onde é divulgado.
3. Suporte: responsáveis autorizados, critério de encerramento e execução da regra de 60 dias/exceções.
4. Fluxos internacionais: origem/destino, papéis e mecanismo aplicável, incluindo transferências posteriores relevantes.

Não é preciso publicar contratos integrais, usuários das contas ou capturas contendo dados pessoais no GitHub. A existência de pendência documental não prova que o contrato não exista; registrar como não comprovado, não como inexistente.
