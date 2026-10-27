# ECA Digital e sinais de idade — avaliação do Terra

Revisão de 19/10/2026 sobre o rascunho da PR #52 (`c746d19`, incorporado em `1f21454`). Análise preparatória, sem conclusão de dispensa, aprovação jurídica ou SDK de idade implementado.

## Escopo e evidências

Terra é aplicativo Android com conteúdo visual, loja e localização opcional; não foram identificados conta própria, chat ou conteúdo publicado por usuários. Teste planejado: adultos no Brasil, compras de licença. O usuário relata classificação de 14 anos; resultado IARC/Console não foi conferido. Classificação de conteúdo, público pretendido e idade verificada são conceitos distintos.

O recrutamento adulto não prova ausência de acesso provável por adolescentes. O objetivo posterior 13+ exige conciliação com classificação efetiva, conteúdo, oferta e medidas aplicáveis antes de ampliação. Não basta trocar o texto para 18+ ou citar controles familiares da loja.

## Enquadramento a avaliar

- Arts. 1º–2º: considerar acesso provável, atratividade do catálogo, facilidade de uso/distribuição e riscos, inclusive compras, IP/localização e suporte. Não concluir dispensa apenas pela ausência de rede social ou por uma leitura isolada da conjunção do art. 1º.
- Art. 9º: a verificação a cada acesso se vincula ao conteúdo/serviço do caput. Não converter automaticamente em coleta de documento/biometria para todos os usuários do Terra; conferir o acervo realmente ofertado.
- Arts. 10–14: avaliar experiência adequada à idade e recebimento de sinais de lojas/SO, com minimização. Arts. 16 e seguintes: avaliar salvaguardas de supervisão proporcionais às funcionalidades. Controles da loja não foram equiparados a implementação própria.
- Art. 24 está no capítulo de redes sociais: registrar análise contextual, sem importar um modelo de contas/responsáveis inexistente nem declarar dispensa universal.

Referências: [lei compilada no Planalto](https://www.planalto.gov.br/ccivil_03/_ato2023-2026/2025/lei/l15211.htm) e [orientações/andamento da ANPD](https://www.gov.br/anpd/pt-br/assuntos/eca-digital), consultados em 19/10. Cronograma regulatório não foi tratado como suspensão geral das obrigações.

## API Play Age Signals: existência confirmada

A API existe. A [documentação oficial de solicitação](https://developer.android.com/google/play/age-signals/request-age-signals?hl=pt-br), consultada em 19/10, descreve SDK 0.0.4 com solicitação de acesso e consulta do sinal. Estados incluem `SHARED`, `NOT_SHARED` e `VERIFICATION_REQUIRED`; campos podem estar ausentes. Isso supera o rascunho que dizia não saber se Google disponibiliza API.

Ausência de sinal, erro, recusa ou faixa não compartilhada **não provam maioridade**. Avaliar disponibilidade na conta, distribuição/região e Play Store efetivos. A documentação não foi convertida em garantia de sinal para todo participante brasileiro. O SDK não foi adicionado nesta entrega.

## Especificação de integração, se adotada após avaliação

1. Solicitar somente faixa/sinal necessário à experiência, sem pedir data de nascimento, documento ou biometria diretamente pelo Terra por inferência.
2. Tratar sinal adulto, faixa adolescente, faixa que cruza o limite de idade, ausência de compartilhamento, verificação pendente e falha de serviço como casos distintos. Não liberar por padrão usando `null` como adulto.
3. Definir previamente comportamento seguro por caso e finalidade. Recrutamento privado e instrumentos de teste não substituem uma política para sinal desconhecido.
4. Evitar gravar faixa ou identificador de instalação em logs/feedback/R2. Se necessário manter dado, definir necessidade e descarte; atualizar mapa e declarações antes da distribuição.
5. Testar o fluxo com cenários simulados e na distribuição Play, incluindo atualização, recusa e revogação. Conferir a versão da API vigente na implementação.

O uso dos sinais é limitado a experiências adequadas à idade, conforme [termos oficiais](https://developer.android.com/google/play/age-signals/overview?hl=pt-br). A afirmação do Google sobre coleta pela biblioteca não dispensa analisar o tratamento dos campos pelo próprio app. Ver [respostas da API](https://developer.android.com/google/play/age-signals/understand-age-signals-responses?hl=pt-br).

## Aceites ainda pendentes

- [ ] Registrar enquadramento contextual e medidas aplicáveis, sem certificar conformidade por classificação 14.
- [ ] Confirmar resultado IARC/público-alvo efetivos e o recrutamento adulto.
- [ ] Decidir necessidade da integração, disponibilidade e tratamento de sinal desconhecido.
- [ ] Se integrada, atualizar código, dados/documentos e testes antes do AAB.
- [ ] Antes de ampliar público, refazer avaliação e [risco](avaliacao-risco.md).
