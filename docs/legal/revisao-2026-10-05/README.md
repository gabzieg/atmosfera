> Registro historico, versionado em 11/10/2026 para consulta. Propostas, valores, fornecedores e pendencias abaixo refletem a epoca do documento; nao substituem o [checklist atual](../../dev/CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md) nem as fontes canonicas de docs/legal. Nao importar estes rascunhos como documentos atuais do app/site.

# Revisão dos textos legais — 05/10/2026

**Estado: rascunhos para conferência técnica, operacional e jurídica. Não vigentes; não publicar ou importar para a landing/app.**

Estes arquivos são a entrega preparatória da etapa 2. As fontes e páginas atualmente usadas pelo aplicativo não foram substituídas. A documentação de origem continua em `docs/legal/*.md`; a promoção destes rascunhos ocorrerá depois da resolução das pendências.

## Decisões incorporadas

- Responsável pelo Terra: **Rafael Huppes, pessoa física**.
- Canal oficial de suporte e privacidade: **suporteterrabr@gmail.com**.
- Oferta alvo: **Premium de efeitos e estilos por compra única de R$ 49,90; cenários vendidos separadamente**. Os rascunhos dos termos destinam-se à futura versão que entregue essa oferta, não alteram a oferta de versões anteriores.
- Domínio e hospedagem da landing **ainda não foram decididos**. Não há URL definitiva presumida nestes rascunhos.
- Aplicativo sem conta própria, anúncios ou analytics; previsão por MET Norway; amostras gratuitas e entrega remota a conferir por versão.

## Documentos preparados

| Arquivo | Principais refinamentos |
|---|---|
| [PRIVACIDADE.md](PRIVACIDADE.md) | MET, geocodificação, IP, compras, backup, atendimento, escopo do site e distinção entre remoção local e retenção por terceiros. |
| [TERMOS.md](TERMOS.md) | Oferta confirmada, direitos separados, compras pendentes, restauração, suporte a reembolso, licenças e continuidade de conteúdo adquirido. |
| [CONTATO.md](CONTATO.md) | Canal oficial, dados voluntários do atendimento, provedor de e-mail, direitos e reembolsos. |

Não foi atribuída uma versão definitiva nem uma data de vigência a esses textos. O número da revisão deve ser fixado na promoção à fonte canônica, preservando o histórico anterior. Datas de edição não são datas de entrada em vigor.

## Pendências antes de virar documento público

| ID | Pendência | Quem/como resolve |
|---|---|---|
| P01 | Definir URL pública, hospedagem, fornecedores, registros e cookies da landing. | Responsável pelo projeto com Willian; conferir a implantação real. |
| P02 | Definir versão do app à qual cada documento se aplica e vigência, incluindo testes. | Responsável pela publicação com conferência do histórico de distribuição. |
| P03 | Separar efeitos Premium das artes/cenários no código, vitrine e produtos Play. Definir quais variantes cada cenário comprado inclui. | Implementação e teste real de compra/restauração. |
| P04 | Verificar vendas reais anteriores e fixar a transição de benefícios conforme a oferta original. | Conferência no Console e revisão de consumo. |
| P05 | Corrigir/verificar o reenvio de coordenadas salvas pelo worker depois da revogação. Avaliar atualização em segundo plano segundo os termos da MET. | Implementação e teste no aparelho; análise do contrato do fornecedor. |
| P06 | Confirmar provedores do `Geocoder` nos aparelhos suportados e revisar logs da versão release. | Conferência técnica; não tratar a geocodificação como operação garantidamente offline. |
| P07 | Alinhar o destinatário de “Reportar problema” ao e-mail oficial. O código atual ainda usa o e-mail pessoal do Rafael. | Alteração de código e teste do destinatário. |
| P08 | Confirmar configuração, URL e operação do R2, instalação/limpeza de downloads e conteúdo que estará disponível. | Integração do acervo e teste da jornada; até lá, manter o recurso identificado como previsto. |
| P09 | Definir retenção, acesso, descarte e preservação do atendimento e dos registros de infraestrutura. | Responsável operacional e advogado; não inventar um prazo uniforme. |
| P10 | Validar bases legais, transferências, papéis dos fornecedores, encarregado e procedimentos de direitos/incidentes. | Profissional jurídico responsável. |
| P11 | Validar público 13+, países de distribuição e obrigações de proteção de menores, inclusive ECA Digital. | Revisão jurídica e conferência do Play Console. |
| P12 | Validar proveniência/licenças de arte, código, fontes e atribuição meteorológica. | Inventário de licenças e conferência dos créditos. |
| P13 | Fechar atendimento em indisponibilidade/descontinuação de conteúdo comprado. | Decisão operacional e revisão contratual; não prometer acesso remoto eterno sem meios de entrega. |

P01, P02, P09, P10, P11 e P13 precisam produzir informações concretas para os textos. Não basta remover esta lista e publicar o restante. As notas de revisão presentes nos rascunhos também precisam ser resolvidas e retiradas na versão aprovada.

## Encaminhamento para revisão jurídica

Enviar ao profissional os três rascunhos, a [consolidação factual](../../dev/CONSOLIDACAO-PRODUTO-DADOS-E-TEXTOS-LEGAIS-2026-10-04.md) e a oferta/comportamento da versão candidata. Não foi feito envio a terceiros nesta rodada.

Solicitar análise específica de LGPD e transferências internacionais, direitos de compra e mudança de oferta, reembolso/arrependimento, atendimento/retenção, proteção de adolescentes, licenças, disponibilidade de conteúdo e limites de responsabilidade. A revisão não deve ficar restrita à redação: os processos e o aplicativo precisam cumprir o que o texto disser.

Fontes oficiais usadas para delimitar as questões:

- [LGPD](https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709compilado.htm).
- [Código de Defesa do Consumidor](https://www.planalto.gov.br/ccivil_03/leis/l8078compilado.htm).
- [Transferências internacionais — Resolução ANPD nº 19/2024](https://www.gov.br/anpd/pt-br/acesso-a-informacao/institucional/atos-normativos/regulamentacoes_anpd/resolucao-cd-anpd-no-19-de-23-de-agosto-de-2024).
- [Agentes de pequeno porte — Resolução ANPD nº 2/2022](https://www.gov.br/anpd/pt-br/acesso-a-informacao/institucional/atos-normativos/regulamentacoes_anpd/resolucao-cd-anpd-no-2-de-27-de-janeiro-de-2022).
- [ECA Digital — Lei nº 15.211/2025](https://www.planalto.gov.br/ccivil_03/_ato2023-2026/2025/lei/l15211.htm) e [orientações da ANPD](https://www.gov.br/anpd/pt-br/assuntos/eca-digital/).
- [Google Play — dados do usuário](https://support.google.com/googleplay/android-developer/answer/10144311?hl=pt-BR) e [gerenciamento de pedidos/reembolsos](https://support.google.com/googleplay/android-developer/answer/2741495?hl=pt-BR).
- [MET — termos](https://api.met.no/doc/TermsOfService), [privacidade](https://www.met.no/en/About-us/privacy) e [licença](https://api.met.no/doc/License).
- [Android — Geocoder](https://developer.android.com/reference/android/location/Geocoder).

## Integração com o projeto do Willian

Após aprovação, promover as fontes, reconciliar os HTMLs e exportar uma revisão identificada para o importador `scripts/prepare-documents.mjs` do projeto `terra.app`. O importador já existe; não precisa redesenhar a landing para receber textos atualizados.

Conferir três coisas separadamente: integridade do HTML de origem, fidelidade do texto/metadados após adaptação de apresentação e aplicabilidade à versão do app. Manter os assets offline e o histórico de revisões. Atualizar um site não altera o documento dentro de um app já instalado.

O desenho detalhado da sincronização está na seção 5 da consolidação. A pipeline e a publicação ainda não foram implementadas nesta rodada.
