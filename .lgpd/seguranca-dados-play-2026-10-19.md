# Segurança dos dados — análise para o candidato do teste fechado

19/10/2026. Base: código da PR #50 e registros da PR #52 incorporados em `1f21454`. Esta é preparação para preenchimento, não formulário enviado ou declaração aprovada. O AAB local antigo, código 6, contém documentos desatualizados e não é o candidato final.

**Atualização em 26/10/2026:** esta matriz de 19/10 permanece preservada como histórico da análise de pendências e foi **superada como guia operacional de preenchimento** pelas [respostas conservadoras propostas para o candidato 7 (`1.0.5`, `versionCode 7`, `c1848a9`)](seguranca-dados-respostas-candidato-7-2026-10-26.md), que cobrem pergunta por pergunta o formulário *Segurança dos dados* e a declaração de *ID de publicidade*. Nada foi salvo, enviado ou aprovado no Google Play Console, e A3 continua pendente (`[ ]`).

## Decisões por fluxo

| Fluxo | O que já pode ser afirmado | O que precisa fechar no formulário |
|---|---|---|
| Coordenadas → MET | Há transmissão externa, mesmo sem backend do Terra; COARSE e arredondamento no código. Sem permissão usa referência fixa, que não é a localização real. MET registra IP/coordenadas em logs. | Localização aproximada coletada para funcionalidade. Não declarar processamento efêmero sem prova. Opcionalidade da localização por permissão deve ser separada de localização inferida por IP em outros fluxos. |
| Compartilhamento MET | É API externa com logs próprios; não comprovada atuação apenas sob instruções do Terra. Onboarding informa destinatário/finalidade e oferece alternativa. | Não usar automaticamente exceção de provedor de serviço. Na falta de exceção comprovada, tratar transmissão de localização a terceiro como compartilhamento. Exceção por ação do usuário/divulgação e consentimento precisa ser sustentada pela jornada real, não apenas existência de permissão Android. |
| Prévias/R2 | Abrir variante não instalada dispara pedido automaticamente; HD usa ação específica. IP/caminho/dados técnicos alcançam a infraestrutura. Código não acrescenta coordenadas, token ou e-mail. | Não chamar toda coleta de opcional só porque HD tem botão. Conferir possibilidade real de evitar/controlar cada tipo de dado. Avaliar categoria de IP pelo uso efetivo; caminho pode revelar interação com arte. |
| Compartilhamento R2 | Entrega de arquivos pode se enquadrar em provedor de serviço se processar em nome/sob instruções. Logs próprios e usos adicionais exigem distinção. | Exceção não é global por marca ou por citar DPA; Rafael precisa confirmar conta/contrato e usos. A exceção de compartilhamento não elimina automaticamente coleta. |
| Billing | App recebe produto/estado/comprovante/token e confirma/restaura pela Play. Não recebe cartão pelo Billing. | Avaliar histórico de compras e dados efetivamente recebidos/enviados; exclusão do cartão do formulário não exclui todo dado financeiro. Validar SDK no artefato. |
| Suporte | Rascunho inclui dados técnicos, usuário decide enviar em cliente externo. Atendimento recebe e-mail/mensagem/anexos. | Avaliar dados relevantes e exceção de transferência iniciada pelo usuário, mantendo transparência do atendimento e execução dos direitos. Não declarar coleta automática de anexos inexistente. |
| SDKs e IP | Manifesto fonte e dependências não identificam SDK dedicado de anúncios/analytics; há Play services, Billing e relatórios da plataforma. | Ausência de SDK dedicado não comprova ausência de todos os identificadores/diagnósticos. IP não é automaticamente “Identificadores do dispositivo e outros” nem automaticamente fora do formulário; classificar pelo uso. |

## Prévia automática e obrigatoriedade

O formulário considera controle real sobre coleta, não só o rótulo do botão. Para cada categoria, inventariar todos os caminhos, inclusive inicialização, clima, Play services, Billing e prévias. Se dados do mesmo tipo são coletados automaticamente sem opção efetiva, não declarar o tipo inteiro como opcional com base apenas no envio voluntário de suporte ou no download HD. A biblioteca/candidato e as práticas do fornecedor devem completar a evidência antes de marcar.

Alternativa de produto, se a revisão exigir controle adicional: permitir escolha antes de buscar prévia remota ou configuração equivalente. Isso não foi implementado nem escolhido nesta entrega; não acrescentar promessa de opt-out aos textos.

## Divulgação de localização

`ui/OnboardingScreen.kt` informa envio de localização aproximada à MET e oferece continuar com cidade padrão. `LocationHelper` pede COARSE, e o serviço limita consultas à home elegível. O aceite deve incluir: texto visível antes da permissão, negativa, concessão, revogação com cache, retorno à home e ausência de novas consultas quando inelegível. Não declarar adequação de todos os estados apenas pela leitura estática.

## Escopo do AAB final

1. Extrair manifesto mesclado/SDKs do binário, não copiar só a lista de permissões fonte. O AAB antigo já mostra permissões técnicas adicionadas por bibliotecas.
2. Conferir endpoint de release e jornada; um release sem acervo configurado não equivale ao debug.
3. Comparar os três HTMLs embutidos com as fontes 1.14/1.8/1.6 e com o site efetivamente republicado. Aprovação/vigência continuam pendentes.
4. Registrar respostas finais por tipo, finalidade, compartilhamento/exceção e obrigatoriedade com evidência; nenhum campo foi enviado ao Console por esta análise.

## Evidências que Rafael precisa confirmar

- Tratamento de IP/caminho e logs/exportações R2; contrato aplicável e usos próprios do fornecedor.
- Dados e práticas das versões de SDKs do candidato; não foi identificado o AAB selecionado no rascunho.
- Experiência real da divulgação/permissão e controles de coleta.
- Preenchimento efetivo do formulário, site público e dados do candidato.

Fontes oficiais consultadas em 19/10: [definições e exceções do formulário](https://support.google.com/googleplay/android-developer/answer/10787469?hl=pt-BR), [política de dados](https://support.google.com/googleplay/android-developer/answer/10144311?hl=pt-BR), [declaração de uso/Billing](https://developer.android.com/privacy-and-security/declare-data-use), [Play services](https://developers.google.com/android/guides/play-data-disclosure). As regras foram resumidas para o caso; não constituem respostas já aprovadas.
