> Registro historico, versionado em 11/10/2026 para consulta. Propostas, valores, fornecedores e pendencias abaixo refletem a epoca do documento; nao substituem o [checklist atual](../../dev/CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md) nem as fontes canonicas de docs/legal. Nao importar estes rascunhos como documentos atuais do app/site.

# Política de Privacidade — Terra

> **Rascunho de 05/10/2026, sem vigência.** Não substitui as páginas atuais. Conferir as pendências do [README](README.md) antes de publicar. As notas de revisão são instruções internas e não integram um documento público aprovado.

**Aplicativo:** Terra — Live Wallpaper (`com.terra.wallpaper`).
**Responsável indicado:** Rafael Huppes, pessoa física.
**Canal de privacidade:** suporteterrabr@gmail.com.
**Versão aplicável do app, versão desta política, vigência e endereço público:** a definir na aprovação.

## 1. Escopo e resumo

Esta política explica os dados tratados no uso do Terra e no atendimento prestado por seu responsável. A seção 8 delimita o que ainda precisa ser definido para a página de apresentação do aplicativo.

O Terra utiliza localização aproximada para consultar a previsão da região e ajustar a cena do papel de parede. A permissão é opcional; sem uma nova localização autorizada, a consulta pode usar São Paulo, SP, como local padrão.

Não há cadastro próprio, anúncios ou analytics integrados ao aplicativo examinado. Isso não significa ausência de tratamento de dados: consultas de clima, serviços do Android, compras e mensagens de suporte envolvem dados e fornecedores distintos.

As preferências e o cache são armazenados no aparelho. Dependendo das configurações do Android, parte desses dados pode ser copiada pelo mecanismo de backup ou transferência. Provedores também podem conservar registros das requisições. Limpar os dados ou desinstalar o app remove o armazenamento local, mas não elimina automaticamente backups, pedidos da Google Play ou registros de terceiros.

## 2. Responsável e contato

Rafael Huppes é o responsável indicado pelo Terra e pelo atendimento no endereço **suporteterrabr@gmail.com**. Para solicitações sobre dados pessoais, use esse canal e informe o suficiente para identificar o assunto, sem enviar documentos ou informações financeiras desnecessárias.

**Nota de revisão — P10:** confirmar o enquadramento como controlador e os papéis dos fornecedores por fluxo. Verificar se cabe dispensa de indicação de encarregado e, caso não caiba, identificar o profissional. Este rascunho não declara uma dispensa automática.

## 3. Localização, previsão e nome da cidade

### 3.1 Obtenção de localização

O aplicativo pede apenas a permissão de localização aproximada do Android e utiliza o serviço de localização do Google Play Services. As coordenadas são usadas para consultar o clima e calcular elementos da cena. O Terra não pede localização precisa nem permissão de localização em segundo plano no manifesto próprio examinado.

Ao abrir o app ou quando o wallpaper fica visível, o aplicativo pode obter uma localização autorizada. Uma rotina periódica também consulta o clima usando a última coordenada guardada, sem obter uma nova localização nessa execução. O intervalo configurável é de 15, 30 ou 60 minutos; o padrão é 30. A execução efetiva depende da rede e do agendamento do Android.

### 3.2 Consulta à MET Norway

As consultas são feitas por HTTPS ao Instituto Meteorológico da Noruega, **MET Norway**. Antes do envio, latitude e longitude são arredondadas para duas casas decimais, uma redução de resolução da ordem de um quilômetro, variável conforme a latitude. Isso reduz a precisão transmitida, mas não torna a requisição anônima.

A MET recebe as coordenadas consultadas, o IP da conexão e a identificação técnica do aplicativo, que inclui nome, versão e o contato do responsável. A previsão recebida e as coordenadas mais recentes ficam no cache local para evitar consultas desnecessárias. Sensação térmica e horários de nascer/pôr do sol são calculados no aparelho.

Os termos da MET informam registros de acesso que podem conter IP e coordenadas, armazenados em seu centro de dados em Oslo. Sua política informa retenção de IP por até 90 dias nos serviços públicos de dados meteorológicos. Os dados meteorológicos têm licença e créditos próprios. Consulte [privacidade da MET](https://www.met.no/en/About-us/privacy), [termos da API](https://api.met.no/doc/TermsOfService) e [licenciamento](https://api.met.no/doc/License).

### 3.3 Nome da cidade

Para mostrar cidade/UF, o aplicativo consulta o `Geocoder` do Android com as coordenadas. O serviço pode acessar a rede e depender de um provedor de geocodificação do aparelho. Por isso, esta operação não é descrita como garantidamente offline nem como envio exclusivo à MET. O nome obtido pode ficar no cache local com o clima.

**Nota de revisão — P06:** complementar fornecedores e informações sobre esse tratamento conforme a implementação dos aparelhos suportados.

### 3.4 Controle da localização

Você pode negar ou revogar a permissão em Configurações do Android → Apps → Terra → Permissões → Localização. Também pode remover o cache e as preferências por Configurações do Android → Apps → Terra → Armazenamento → Limpar dados. Isso poderá exigir configurar novamente o wallpaper e restaurar as compras.

**Nota de revisão — P05:** no código examinado, a rotina periódica pode continuar enviando coordenadas guardadas após a revogação até a substituição/remoção do cache. Corrigir e verificar esse comportamento antes da publicação. A versão final deverá explicar o controle efetivo sem afirmar uma interrupção imediata ainda não comprovada.

## 4. Preferências, backup e diagnóstico

### 4.1 Preferências locais

O aplicativo guarda escolhas de cenário, arte, estilo, personalização, intervalo de clima e indicadores de direitos de compra em armazenamento privado do app. Essas escolhas não são enviadas a um serviço próprio de analytics. O armazenamento privado utiliza as proteções do Android e não constitui garantia absoluta contra acesso em um aparelho comprometido.

### 4.2 Backup e transferência

O aplicativo permite backup pelo Android. Preferências e cache de clima, incluindo coordenadas guardadas, podem participar do backup ou da transferência entre aparelhos, conforme regras e configurações do sistema. O arquivo específico do indicador Premium é excluído pelas regras examinadas; o direito de compra é restaurado pelo Google Play.

O responsável pelo Terra não tem acesso ao conteúdo do backup por esse mecanismo. Para gerenciá-lo, consulte as configurações do Android e os controles da sua conta. A exclusão do app não deve ser interpretada como exclusão de todas as cópias do sistema.

### 4.3 Diagnóstico

Não foi identificado SDK próprio de analytics ou envio automático de falhas no aplicativo examinado. O app escreve mensagens de diagnóstico no log do Android. Alguns caminhos do código podem registrar coordenadas e IDs de pedidos. Se você enviar um relatório ou anexo ao suporte, os dados ali contidos também farão parte do atendimento.

Serviços do Android/Google podem ter mecanismos próprios de diagnóstico, regidos por suas configurações e políticas.

**Nota de revisão — P06:** conferir os logs do AAB release e minimizar informações que não sejam necessárias. Não afirmar que a equipe jamais recebe diagnósticos quando o usuário pode encaminhá-los voluntariamente.

## 5. Compras e Google Play

Compras são processadas pelo Google Play Billing. O aplicativo trata as informações necessárias para verificar, confirmar e restaurar o direito comprado: produto, estado da compra, token, resposta assinada e, quando disponível, ID do pedido. Indicadores de direito são guardados localmente.

O app não pede dados de cartão ou acesso ao meio de pagamento. Isso é diferente dos dados de pedido: pessoas com acesso autorizado ao Play Console podem consultar informações disponibilizadas pelo Google para gerenciamento, suporte e reembolso de compras.

O histórico de pedidos permanece sob as regras da Google Play e da conta do comprador. A ausência de cadastro próprio do Terra não elimina esse histórico. Consulte a [política do Google](https://policies.google.com/privacy).

**Nota de revisão — P10:** conferir campos efetivamente acessíveis à equipe no Console, permissões e procedimentos de uso/retenção. Não atribuir ao responsável acesso a campos não verificados.

## 6. Conteúdo remoto

**Recurso previsto, ainda não integrado ao fluxo principal na versão examinada.** A redação abaixo só deve ser apresentada como funcionalidade disponível quando houver entrega e teste na versão correspondente.

O catálogo poderá usar miniaturas incluídas no instalador e arquivos de arte baixados após sua escolha. O download é independente do direito ao Premium: a verificação de compra determina o acesso ao cenário e aos efeitos, cada qual conforme sua oferta.

A infraestrutura prevista é Cloudflare R2. Uma requisição de download informa o arquivo solicitado e expõe o IP da conexão ao provedor. A previsão e as coordenadas do clima não são necessárias para essa entrega e não integram o pedido de download do fluxo examinado. A infraestrutura pode tratar registros técnicos de acesso e segurança; arquivo público não significa ausência de dados pessoais.

Os arquivos instalados ficam no aparelho. A política final deverá explicar a opção real de limpeza e o efeito sobre o cenário ativo, sem indicar menus ainda não implementados.

**Nota de revisão — P08/P09:** confirmar domínio, configuração de registros, pessoas com acesso e retenção. Se a revisão futura incluir autenticação no servidor ou validação remota de tokens, descrever os novos dados antes de disponibilizar esse fluxo. [Privacidade da Cloudflare](https://www.cloudflare.com/privacypolicy/).

## 7. Atendimento

Quando você escreve ao suporte, recebemos seu endereço de e-mail, mensagem e anexos enviados voluntariamente. O objetivo é responder, investigar o problema, atender direitos ou tratar questões de compra. O aplicativo pode abrir seu cliente de e-mail com um relato preparado, mas não envia a mensagem automaticamente nem obtém o endereço do remetente por esse ato.

O canal oficial usa Gmail, serviço do Google. O atendimento pode ser acessado pelo responsável e por pessoas autorizadas que precisem tratar o caso. Não usamos essas mensagens para publicidade nem listas de marketing.

Detalhes e cuidados para envio estão em [Contato](CONTATO.md). Mensagens, anexos e eventuais registros necessários para comprovar o atendimento têm critérios próprios de retenção; não são apagados pela desinstalação do aplicativo.

**Nota de revisão — P07/P09:** alinhar o destinatário no app; identificar pessoas autorizadas, critérios de descarte e necessidade de preservação legal antes de tornar o texto vigente.

## 8. Site de apresentação

A landing apresenta o aplicativo e seus documentos. Na revisão do site examinada, não há cadastro, formulário de envio, analytics ou consulta real de clima/localização do visitante; a demonstração é simulada.

O acesso ao site ainda envolve requisições à hospedagem e pode gerar registros técnicos, como IP e recurso acessado. O responsável ainda não definiu domínio nem hospedagem, portanto não há fornecedor ou prazo de retenção confirmado para publicar nesta seção.

**Nota de revisão — P01:** completar a descrição com a implantação real, seus fornecedores, registros, cookies e controles. Se houver nova ferramenta de métricas ou formulário, atualizar esta seção antes da coleta. Não inserir um banner de consentimento sem analisar as tecnologias realmente utilizadas.

## 9. Fundamentos e transferências internacionais

O tratamento deve ser limitado às finalidades descritas: clima/localização, funcionamento do app, compras, entrega de conteúdo, segurança e atendimento. A permissão de localização autoriza tecnicamente o acesso pelo Android; a aceitação de termos não representa autorização genérica para qualquer coleta.

As consultas à MET e o uso de fornecedores Google/Cloudflare podem envolver processamento fora do Brasil. O fluxo meteorológico usa a Noruega; as demais localidades dependem dos serviços e contratos correspondentes.

**Nota de revisão — P10, bloqueio de publicação:** estabelecer e documentar a base legal de cada finalidade e o mecanismo de transferência aplicável a cada fluxo. Verificar garantias/contratos e decisões de adequação pertinentes. Este rascunho elimina a referência inexistente ao art. 33, II, “f”, mas não inventa uma nova justificativa nem trata políticas de fornecedores como prova suficiente de regularidade.

## 10. Retenção e remoção

| Categoria | Armazenamento/controle | O que se sabe e o que falta definir |
|---|---|---|
| Coordenadas e clima recentes | Cache local | Atualizado após consultas bem-sucedidas; consultas podem ocorrer antes do intervalo em mudanças de localização. Removido com limpeza dos dados/desinstalação. |
| Preferências e arquivos instalados | Aparelho | Persistem enquanto mantidos no armazenamento; a limpeza específica de downloads depende da implementação confirmada. |
| Backup/transferência | Sistema/conta do usuário | Conforme configurações e política do sistema; não há acesso do responsável pelo Terra por esse mecanismo. |
| Registros da MET | Provedor | IP por até 90 dias conforme sua política; consultar termos para composição dos registros. |
| Pedidos e histórico de compras | Google Play | Conforme regras do Google e obrigações aplicáveis; não desaparecem pela desinstalação. |
| Atendimento e registros necessários à comprovação | Caixa/canais de suporte | Critérios e períodos pendentes de definição operacional e jurídica — P09. |
| Registros de downloads/site | Infraestrutura | Configuração e retenção pendentes — P01/P08/P09. |

Solicitações de exclusão serão analisadas conforme o dado, o controle que temos sobre ele e a necessidade de preservação legal. Quando o dado estiver sob gestão de outro serviço, informaremos o caminho aplicável, sem prometer exclusão automática de registros que não controlamos.

## 11. Direitos e segurança

Para solicitações de acesso, correção, exclusão e demais direitos aplicáveis, escreva para **suporteterrabr@gmail.com**. Não exigiremos informações excessivas para identificar a solicitação. Se uma providência não puder ser atendida de imediato, explicaremos o motivo e o encaminhamento aplicável.

Pedidos serão tratados de acordo com os prazos legais pertinentes. Não atribuímos a todos os pedidos um mesmo prazo legal de 15 dias. A confirmação/acesso tem as formas e prazos específicos previstos na LGPD; a operação de atendimento deve refletir essas diferenças.

O app utiliza armazenamento privado do Android, HTTPS para a consulta meteorológica e verificação da assinatura das compras. Essas medidas reduzem riscos; não constituem promessa de segurança absoluta. Procedimentos da equipe para acesso à caixa, registros e incidentes precisam ser definidos e mantidos.

Você também pode procurar a [ANPD](https://www.gov.br/anpd) e órgãos competentes de defesa do consumidor.

## 12. Público e alterações

O público pretendido é **13 anos ou mais**. Entre 13 e 17 anos, o uso e as compras devem observar a participação do responsável legal e os controles aplicáveis da plataforma. O aplicativo não cria perfis publicitários ou canais de interação entre usuários.

**Nota de revisão — P11:** conferir a declaração na Play, medidas pertinentes à proteção de menores e ao ECA Digital e tratamento adequado de pedidos de responsáveis. A simples declaração “13+” não encerra essa avaliação.

Mudanças relevantes no tratamento serão refletidas nesta política, identificadas por versão e escopo e comunicadas adequadamente. Quando houver necessidade de uma autorização específica, ela será tratada separadamente; uso continuado não substitui essa autorização.

Manteremos uma referência às versões anteriores e à versão aplicável do aplicativo. A cópia offline de um app instalado pode corresponder à sua versão, enquanto o site apresenta revisão posterior com escopo explícito.
