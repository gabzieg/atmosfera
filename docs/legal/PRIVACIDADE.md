# Política de Privacidade — Terra

**Página pública:** [https://sites.google.com/view/terra-live-wallpaper/privacidade](https://sites.google.com/view/terra-live-wallpaper/privacidade)

**Versão da política:** 1.10
**Última atualização:** 15 de outubro de 2026
**Estado:** Documento em revisão; ainda não aprovado para publicação definitiva.
**Aplica-se a:** Revisão candidata com entrega remota de artes. A versão de distribuição ainda será definida. Não descreve automaticamente o funcionamento de versões anteriores.
**Vigência:** A definir após aprovação e identificação da versão correspondente.

## Resumo

- O responsável é **Rafael Huppes, pessoa física**. Canal: [suporteterrabr@gmail.com](mailto:suporteterrabr@gmail.com).
- Localização aproximada é opcional e usada para consultar clima na **MET Norway**. Coordenadas saem por HTTPS, arredondadas a duas casas decimais; sem autorização, a referência é **São Paulo, SP**.
- O app não possui conta própria, anúncios ou SDK de analytics. Compras utilizam Google Play; prévias automáticas e downloads HD solicitados utilizam **Cloudflare R2**.
- Internet revela IP e dados técnicos aos fornecedores. Coordenadas de clima, comprovantes e tokens de compra não são enviados ao R2.
- Preferências e clima ficam no aparelho, com as ressalvas de backup abaixo. E-mails enviados ao suporte e registros de fornecedores não são apagados pela desinstalação.

## 1. Responsável e alcance

**Rafael Huppes** é o responsável pelo tratamento realizado pelo Terra. Para dúvidas e solicitações sobre dados pessoais, escreva a [suporteterrabr@gmail.com](mailto:suporteterrabr@gmail.com).

A política abrange aplicativo, wallpaper, páginas de documentos e atendimento por e-mail. As páginas são hospedadas no Google Sites, que pode tratar dados técnicos de navegação conforme a política do Google. Não foram adicionados formulários, anúncios ou analytics próprios às páginas. O tratamento realizado por MET Norway, Google e Cloudflare é descrito nas seções correspondentes e nas políticas desses fornecedores.

## 2. Localização e meteorologia

O app solicita apenas **ACCESS_COARSE_LOCATION**, usando localização aproximada para previsão. Consultas automáticas de clima/localização só são iniciadas com o wallpaper visível na tela inicial, aparelho acordado e desbloqueado. Intervalo escolhido, cache e restrições do fornecedor limitam atualizações. Prévias e tela do Terra usam o último clima salvo; não há worker periódico buscando clima em segundo plano.

Coordenadas são arredondadas a duas casas decimais, aproximadamente 1 km conforme latitude, e enviadas diretamente à MET Norway por HTTPS. A requisição inclui identificação do aplicativo e contato de suporte. MET recebe IP e coordenadas da consulta e pode registrá-los em seus logs. Esses dados não passam pelo servidor de conteúdo do Terra.

O último clima e as coordenadas utilizadas ficam no armazenamento privado do app, substituídos nas novas consultas bem-sucedidas. Revogue a autorização nas configurações Android; sem ela, o app usa São Paulo, SP. Sem rede/cache, usa estado padrão. Revogar a permissão não apaga automaticamente o cache existente; limpar os dados ou desinstalar remove o armazenamento local.

## 3. Preferências, compras e diagnóstico

Preferências de cenário, arte, estilo e clima ficam no armazenamento privado. O app mantém um indicador de Premium e um conjunto de produtos de arte liberados. A Google Play mantém pedidos e fornece ao app produtos, estados, comprovantes assinados e tokens usados para verificar e confirmar compras. O app não recebe dados de cartão, CPF ou endereço de cobrança; não possui servidor próprio de validação de pedidos nesta revisão.

Compras podem ser restauradas com a mesma conta Google. Consulta bem-sucedida reconcilia os direitos; falha na consulta não deve apagar o acesso anteriormente validado. Direitos locais não são prova de que os arquivos HD estejam instalados.

Não há SDK de publicidade, analytics ou relatório de falhas integrado ao Terra. Logs de diagnóstico ficam no Android; relatórios da plataforma e eventuais dados de qualidade no Play Console seguem os mecanismos e regras do Google. Um relato enviado por você ao suporte é tratado como atendimento, conforme seção 6.

## 4. Entrega de artes pelo Cloudflare R2

As miniaturas e o índice conferido acompanham o aplicativo. Visualizar o catálogo não exige download de HD. Quando você solicita **Baixar e aplicar**, o app pede os arquivos comuns do cenário e da arte que faltam. Comprar ou restaurar não baixa automaticamente todo o acervo. A instalação confere hash completo e arquivos necessários antes de aplicar.

Ao abrir uma arte ainda não instalada ou selecionar outra variante na tela do cenário, o app solicita automaticamente uma prévia de resolução intermediária ao Cloudflare R2, sem exigir compra. Essa prévia não instala nem concede a arte completa. Ela fica em cache temporário, com limite de espaço e limpeza após cerca de cinco minutos sem uso. A imagem aberta é preservada; se o processo for encerrado pelo Android, a limpeza pendente ocorre na próxima abertura. Falhas de rede mantêm a miniatura local como alternativa. As requisições de prévia revelam os dados técnicos descritos abaixo, sem acrescentar localização ou dados da compra.

A infraestrutura recebe IP, caminho do arquivo solicitado, horário e outros dados técnicos da conexão; o caminho pode revelar qual cenário/variante foi solicitado. O Terra não acrescenta coordenadas, clima, e-mail do usuário, token ou comprovante da compra a essas requisições. Cloudflare pode tratar dados de rede e segurança segundo sua política e configurações contratadas. Não afirmamos que seu servidor deixe de tratar dados pessoais ou que não existam logs acessíveis ao administrador.

Arquivos ficam na pasta privada do app. Apagar downloads preserva compras; a limpeza de uma arte remota ativa exige trocar primeiro para uma amostra nativa. Arquivos instalados e direitos são independentes. O bucket atual de testes é público; acesso ao arquivo fora do app não concede direito dentro do Terra.

## 5. Backup, permissões e segurança

O Android pode incluir preferências comuns em backup ou transferência, sob seu controle. As regras do Terra excluem cache de clima/coordenadas, pausa da MET, indicador Premium, direitos locais de artes e pasta do acervo baixado. Reinstalação exige restaurar compras pela Play e baixar novamente as artes remotas escolhidas. Não controlamos cópias geradas por versões anteriores.

Além da localização opcional, INTERNET e ACCESS_NETWORK_STATE permitem consultar clima, faturamento e arquivos. Bibliotecas podem acrescentar permissões técnicas ao manifesto mesclado; isso não autoriza coleta de clima fora das condições descritas.

Comunicações externas usam HTTPS na configuração de distribuição. Assinaturas de compras e hashes dos pacotes são verificados pelo app; arquivos locais usam armazenamento privado. Esses controles reduzem riscos, sem prometer segurança absoluta. Incidentes devem ser avaliados e comunicados quando a legislação exigir.

## 6. Atendimento por e-mail

Em [suporteterrabr@gmail.com](mailto:suporteterrabr@gmail.com), recebemos remetente, mensagem, anexos e eventual identificador de pedido necessário ao caso. Finalidades: atender, corrigir defeitos, responder direitos e cumprir obrigações ou exercer direitos. Gmail/Google processa essas mensagens; acesso é limitado ao responsável e colaboradores autorizados que precisem do caso. Não usamos atendimento para publicidade.

Retenção definida: **60 dias após encerrar o atendimento**, salvo obrigação legal ou necessidade justificada de defesa em processo. Nessas exceções, a conservação deve se limitar aos dados necessários, com motivo, prazo e acesso registrados. A exclusão é realizada pelo responsável pelo atendimento, não automaticamente pelo app. Não envie senhas, dados de cartão ou documentos sem necessidade demonstrada. Veja [Contato](CONTATO.md).

## 7. Finalidades e limites de uso

Localização e clima são usados para adaptar o wallpaper; preferências preservam as escolhas no app; dados de compras verificam, confirmam e restauram os produtos adquiridos; requisições ao R2 entregam prévias e arquivos escolhidos. Mensagens de suporte são usadas para atendimento, investigação de defeitos, pedidos de direitos e obrigações ou defesa em processo quando necessárias.

O Terra não utiliza esses dados para publicidade, venda de perfis ou treinamento de inteligência artificial. Não há conta própria nem rastreamento entre aplicativos. A permissão de localização é opcional; sua concessão no Android não substitui outros requisitos legais aplicáveis. Esta revisão permanece em avaliação: as bases legais por operação e os procedimentos relativos a adolescentes ainda não foram aprovados.

## 8. Fornecedores e tratamento internacional

| Fornecedor | Operação e dados envolvidos | Referência |
|---|---|---|
| MET Norway | Clima: coordenadas arredondadas, IP e identificação técnica do app; acesso direto ao serviço na Noruega | [Termos da API](https://api.met.no/doc/TermsOfService) |
| Google | Play/Billing: pedidos e dados da compra; localização e backup: dados necessários a essas funções; Gmail: remetente, mensagens e anexos; Sites: dados técnicos de navegação | [Política do Google](https://policies.google.com/privacy?hl=pt-BR) |
| Cloudflare | Entrega R2: IP, caminho da arte e dados técnicos; armazenamento e segurança da infraestrutura | [Política da Cloudflare](https://www.cloudflare.com/privacypolicy/) |

Os serviços podem tratar dados fora do Brasil; a API meteorológica é acessada diretamente na Noruega. Google e Cloudflare não têm um único país de processamento identificado nesta revisão. A identificação dos papéis de cada fornecedor e dos mecanismos aplicáveis de transferência internacional permanece pendente de validação; os links acima não substituem essa análise.

## 9. Retenção e exclusão

| Informação | Retenção ou controle |
|---|---|
| Clima/coordenadas locais | Último registro, atualizado por consulta; removido ao limpar dados ou desinstalar |
| Preferências e direitos locais | Até limpeza/desinstalação, com possível backup de preferências comuns |
| Artes baixadas | Até limpeza dos downloads, dados ou desinstalação |
| Atendimento | 60 dias após encerramento, ressalvadas exceções justificadas da seção 6 |
| Pedidos, backups e logs de fornecedores | Conforme políticas/configurações e obrigações de cada serviço; não apagados pelo app |

O prazo de 60 dias refere-se ao atendimento sob nossa responsabilidade. Não se aplica automaticamente a pedidos da Play, backups ou registros dos fornecedores. Os prazos e a eventual exportação de logs na infraestrutura R2 ainda não foram confirmados nesta revisão. Apagar o app não elimina esses registros externos.

## 10. Direitos e solicitações

Solicite informações, confirmação/acesso, correção, eliminação e outros direitos aplicáveis em [suporteterrabr@gmail.com](mailto:suporteterrabr@gmail.com). Confirmação/acesso simplificado deve ser atendido imediatamente quando possível; declaração completa segue o prazo legal aplicável, em regra até 15 dias. Outros pedidos seguem seus requisitos e prazos, sem promessa uniforme para todos os casos. Eventual identificação adicional deve ser proporcional.

Explique a solicitação sem enviar documentos desnecessários. Exclusão pode ter limites por obrigações legais ou defesa de direitos, que devem ser informados. Você pode procurar a [ANPD](https://www.gov.br/anpd). Não existe conta própria a excluir; isso não afasta direitos sobre atendimento ou tratamento efetivamente realizado.

## 11. Adolescentes

Público pretendido: **13 anos ou mais**. Entre 13 e 17 anos, uso e compras devem contar com autorização e acompanhamento do responsável. Não coletamos data de nascimento nem oferecemos conta social. Não afirmamos que essas escolhas dispensam obrigações de proteção de adolescentes, aferição de idade ou supervisão quando aplicáveis.

A indicação 13+ descreve o público pretendido; não comprova que controles de idade e supervisão estejam implementados. O enquadramento e as medidas aplicáveis de proteção de adolescentes permanecem em avaliação nesta revisão. Para dúvidas ou dados enviados por menores, procure o canal oficial.

## 12. Alterações e histórico editorial

Mudanças materiais serão refletidas nos documentos e informadas antes ou junto da versão correspondente. Consentimento será solicitado quando exigido; uso continuado não o substitui. A cópia offline acompanha a versão instalada e não muda apenas porque o site foi atualizado. Vigência e versão aplicável serão identificadas na aprovação desta revisão.

Revisão 1.8 de 12/10/2026: prévias intermediárias automáticas e cache temporário. Revisão 1.7 de 11/10/2026: responsável/canal, atendimento de 60 dias, compras por arte, miniaturas/HD, dados de rede e limites de promessas. Revisão anterior 1.6 de 10/10: exclusões de backup do acervo e direitos. Histórico editorial não comprova publicação ou vigência das cópias anteriores.

Revisão 1.9 de 15/10/2026: preparação no Google Sites e escopo de compras somente com contas de licença durante o teste fechado; vigência e identificação do artefato ainda pendentes.

Revisão 1.10 de 15/10/2026: simplificação editorial, explicitação das prévias no resumo e separação das instruções internas de teste. Permanecem as pendências indicadas nesta revisão; sem aprovação ou nova vigência.
