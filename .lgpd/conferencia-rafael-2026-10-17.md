# Retorno administrativo de Rafael — 17/10/2026

Registro das informações relatadas pelo responsável e de dois prints fornecidos na conversa. Não representa inspeção direta das contas, aprovação legal ou autorização para publicação. Nenhuma configuração remota foi alterada pelo assistente. Não contém lista de participantes ou credenciais.

**Atualização de 19/10:** as constatações abaixo preservam a sequência histórica de 17/10. O print posterior de licenças confirmou a seleção de uma lista com sete contas; Rafael informou 9/11 no cadastro e seleção de todos os países, divergente do teste apenas Brasil. Salvamento, checkout, adesões novas e correção dos países não confirmados. Consulte o [consolidado para Gabriel](../docs/dev/RETORNO-GABRIEL-PLAY-CONSOLE-2026-10-19.md) para o estado mais recente, erros do AAB e revisão do formulário.

## Cloudflare / R2

- Rafael relata que Data Access Logs estava Disabled e foi alterado por ele para Enabled durante a conferência. Estado atual relatado: Enabled; retorno a Disabled recomendado por ausência de finalidade definida, mas ainda não confirmado. Não registrar como desativado antes de nova confirmação.
- Localização informada: General / Eastern North America (ENAM). O dado não comprova localização de todo tratamento ou dos logs; referências de localização e restrições de jurisdição são distintas.
- Print de Event Notifications oferece automação com Workers/Queues mediante plano pago. Não demonstra quais Workers estão implantados, nem ausência de intermediários ou exportações. Verificação continua pendente.
- Em retorno posterior, Rafael confirmou apenas sua conta pessoal na conferência de membros. Membros humanos conferidos por relato do responsável; tokens e escopos ainda não conferidos. Isso não comprova ausência de acesso programático.

## Suporte Gmail

- Rafael relata ausência de delegações, uso pelo navegador e ausência de downloads de mensagens/anexos. Ainda não houve atendimento relevante, segundo seu relato; houve mensagem referente a pagamento ao Google.
- Print confirma POP desativado. Opções de comportamento IMAP aparecem, mas não identificam clientes conectados. Gmail pessoal mantém IMAP disponível; isso não comprova sincronização por terceiros.
- Encaminhamento ainda não confirmado. Orientação: manter desativado se não houver necessidade operacional. Não conceder delegação apenas para completar a conferência; Gabriel somente precisa de acesso se assumir atendimento.
- Rotina de atendimento, direitos, encerramento e retenção de 60 dias ainda não adotada/testada. Rafael propôs tratar o item posteriormente. Não marcar como implementado.

## Google Sites

- Rafael informa Analytics próprio inativo e páginas abrindo corretamente.
- Orientação: manter integração Analytics desativada e campo de identificação vazio. Isto não equivale a afirmar ausência de registros/cookies da plataforma Google.
- Incorporações e formulários não foram reconferidos neste retorno.

## Play Console

- Rafael informa sete endereços na lista de testadores. Isto não confirma sete adesões ao teste, instalações ou testadores de licença.
- Adesão efetiva, quantidade de contas licenciadas e checkout com instrumento de teste ainda pendentes.
- Orientação: na área da conta de desenvolvedor, fora da tela de um app, abrir Configurações > Teste de licença, selecionar a lista apropriada e salvar. Conferir checkout com a mesma conta participante e instrumento de teste antes de testar compras.
- Se a conta estiver sujeita ao requisito de produção de novas contas pessoais, sete endereços não bastam para comprovar os 12 participantes inscritos continuamente por 14 dias.
- Cópias administrativas de lista/feedback, responsáveis e retenção ainda pendentes; não publicar endereços no repositório.

### Complemento: três prints do painel do Play Console

- O painel específico do app mostra três de onze tarefas de configuração concluídas: política de privacidade, detalhes de login e anúncios. Marcação do Console não equivale à aprovação final dos textos.
- Permanecem visíveis como pendentes: classificação de conteúdo, público-alvo, segurança dos dados, apps governamentais, recursos financeiros, saúde, categoria/contato e página de detalhes do app.
- O teste fechado aparece bloqueado com a instrução de terminar a configuração do app. Países, testadores e etapas de criação/envio da versão aparecem com cadeados.
- O painel de acesso à produção informa zero testadores participando no momento e exige pelo menos 12 participantes por 14 dias. Os sete endereços da lista não representam adesões efetivas.
- Os prints são da área específica do TERRA; o botão Todos os apps, no alto à esquerda, permite retornar à área geral da conta para procurar Configurações > Teste de licença. Cadastro de licença ainda não conferido.

## Próximas confirmações

1. Estado final de Data Access Logs após a orientação; membros/tokens e intermediários efetivos do R2.
2. Encaminhamento do Gmail e responsabilidade mínima pelo atendimento durante o teste.
3. Lista selecionada para licenças e compra usando instrumento de teste; adesões efetivas separadas dos sete endereços cadastrados.

Não altera os textos públicos nem encerra a revisão final atribuída a Gabriel.

## Referências consultadas

- [Cloudflare: Data Access Logs](https://developers.cloudflare.com/r2/buckets/data-access-logs/)
- [Cloudflare: localização R2](https://developers.cloudflare.com/r2/reference/data-location/)
- [Cloudflare: membros da conta](https://developers.cloudflare.com/fundamentals/manage-members/manage/)
- [Google: Gmail e IMAP](https://support.google.com/mail/answer/7126229?hl=pt-BR)
- [Google: testes de licença](https://support.google.com/googleplay/android-developer/answer/6062777?hl=pt-BR)
- [Google: requisitos de teste para novas contas pessoais](https://support.google.com/googleplay/android-developer/answer/14151465?hl=pt-BR)
