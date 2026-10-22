# Guia de Segurança dos dados e público — candidato Terra para teste fechado

**Revisão em 19/10:** usar a [matriz atual](../../.lgpd/seguranca-dados-play-2026-10-19.md) para MET/R2, exceções, IPs, prévias automáticas e SDKs, e o [diagnóstico da versão](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md). Fontes atuais: Privacidade 1.14, Termos 1.8, Contato 1.6. Sete contas na lista de licença foram informadas por Rafael, não verificadas no Console; não equivalem a adesões na faixa. Classificação 14 relatada não é prova de aferição de idade. Nenhum formulário enviado por esta preparação; registros anteriores abaixo preservam histórico. **22/10:** o artefato a conferir é o candidato 7 (`c1848a9`), registrado no [diagnóstico](DIAGNOSTICO-TESTE-FECHADO-2026-10-19.md#atualização-em-22102026--candidato-7-identificado); o preenchimento no Console é do Rafael, único com acesso.

## Respostas de Rafael em 15/10 — atualização do escopo

Teste fechado exclusivamente no Brasil, somente com adultos e contas de licença. Gabriel fará a revisão/aprovação final dos textos, ainda pendente. O candidato é a próxima versão após os ajustes; número/versionCode e data inicial dependem de conferência no Console. Quantidade de contas e configuração efetiva de licença não confirmadas. Premium inclui futuros efeitos e estilos, preservadas artes/variantes separadas. Intenção de não retirar conteúdo, com plano de exceção ainda em estudo. Logs, retenção e acesso dos fornecedores seguem sem definição/verificação.

Este registro supera as pendências de decisão sobre país, participantes menores, responsável pela revisão e inclusão de efeitos/estilos futuros nos registros anteriores. Não confirma configuração do Console, aprovação jurídica ou início da contagem. Fontes locais 1.11/1.7/1.3 precisam de revisão e republicação no Sites; geração de HTML não altera páginas públicas.

Detalhe: [respostas e roteiro para iniciar o teste](RESPOSTAS-RAFAEL-TESTE-FECHADO-2026-10-15.md).


## Site publicado em 15/10/2026

Rafael informou a publicação do Google Sites. Links conferidos na navegação, documentados sem seleção de conta:

| Destino | URL |
|---|---|
| Site | https://sites.google.com/view/terra-live-wallpaper |
| Política de Privacidade — campo do Console | https://sites.google.com/view/terra-live-wallpaper/privacidade |
| Termos | https://sites.google.com/view/terra-live-wallpaper/termos |
| Contato | https://sites.google.com/view/terra-live-wallpaper/contato |

Compras do teste: somente contas testadoras de licença, conforme decisão do titular em 15/10. Falta conferir a configuração efetiva das contas e dos instrumentos de pagamento. O site está publicado com metadados de rascunho; publicação não significa aprovação jurídica, vigência do candidato, cadastro no Console ou começo dos 14 dias. Ainda é necessário conferir o acesso sem login em sessão anônima e conciliar versão/vigência/textos do artefato. Alterações nas fontes locais exigem republicação no Google Sites; gerar os HTMLs não modifica o site.


Atualizado em 15/10/2026 para a PR #50, base `a809f30` com v2 integrada. É uma matriz de preparação; as respostas finais dependem do artefato distribuído e das configurações de fornecedores/Console. Não copiar como declaração já aprovada.

O guia anterior de outubro descrevia apenas uma flag local de compras e afirmava que desinstalar apagava todos os dados. Foi superado por compras por arte, tokens/comprovantes, prévias automáticas R2 e suporte. Essas conclusões não devem ser usadas no formulário.

## Pré-requisitos do teste fechado

Política pública ativa e acessível sem login/geobloqueio, em formato de página (não PDF), também disponível no app. Domínio próprio não é obrigatório: endereço público do Google Sites ou Blogger pode ser utilizado. Identificar Terra e responsável/canal. Preencher Segurança dos dados também para a faixa fechada; somente faixa exclusivamente interna tem a exceção indicada pela Play.

Ver [preparação legal do teste](PREPARACAO-LEGAL-TESTE-FECHADO-2026-10-14.md). As cópias atuais ainda são rascunhos. Definir aprovação, versão distribuída e vigência reais antes de apresentá-las como documentos aplicáveis ao teste.

## Matriz de dados e evidências

| Operação | Evidência do candidato | Orientação para declaração |
|---|---|---|
| Localização/clima | `LocationHelper.kt`, `MetNorway.kt`, `WeatherRepository.kt`, `ClimaNaHome.kt`; COARSE, coordenadas arredondadas a duas casas para MET. Sem permissão, São Paulo/SP. | Localização aproximada: coletada por transmissão externa; opcional, funcionalidade. Conferir compartilhamento/exceções e retenção externa. Não classificar como efêmera sem comprovar uso somente em memória pelos destinatários. |
| Localização precisa | Manifesto fonte não declara FINE nem localização em segundo plano. | Não identificado envio de localização precisa no código conferido; confirmar permissões mescladas e SDKs do artefato final. A precisão transmitida é relevante além da permissão nominal. |
| Compras | `BillingManager.kt`, `ComprasArte.kt`, `Plano.kt`: produtos/estados, token e comprovante/assinatura para validação, confirmação e restauração. | A ausência de cartão no app não permite excluir automaticamente histórico de compras. Conferir os dados efetivamente transmitidos por Billing e as orientações de pagamentos/SDKs antes de fechar a categoria e compartilhamento. Direitos tratados só no aparelho têm tratamento distinto da transmissão externa. |
| Prévias e HD | `Acervo.kt` e `ScenePreview.kt`: prévia intermediária automática da arte aberta; HD solicitado. R2 recebe IP, caminho e dados técnicos; não recebe coordenadas/token de compra pelo código. | Avaliar IP e usos reais (localização inferida/identificadores), caminho da arte/atividade e dados de diagnóstico conforme configurações/SDKs. Não dizer que IP inerente é automaticamente fora de escopo. |
| Suporte/feedback | `ReportarProblemaDialog.kt`, canal Gmail; mensagem, anexos e eventual pedido enviados voluntariamente. | Política descreve atendimento. Conferir dados incluídos na ação e eventual exceção de compartilhamento iniciado pelo usuário; não presumir ausência de coleta/atendimento porque não há conta própria. Lista de testadores/Console é operação adicional a documentar. |
| Preferências/cache/downloads | Armazenamento privado, backup seletivo; cache clima/coordenadas e direitos/acervo excluídos pelas regras. | Processamento exclusivamente local não precisa ser declarado como coleta no formulário. Conferir backup/SDKs e distinguir fornecedores externos. Desinstalar não apaga e-mails/pedidos/logs de fornecedores. |
| Analytics/anúncios/falhas | Nenhum SDK dedicado de anúncios, analytics ou crash reporting foi identificado na revisão; há mecanismos e relatórios da plataforma. | Conferir dependências/manifesto final e práticas publicadas dos SDKs; não usar “tudo o mais: não” como conclusão geral sem essa conferência. |

Google define coleta como transmissão para fora do aparelho, inclusive por SDK e para terceiros. Transferência para provedor de serviços ou por ação esperada do usuário pode ter exceção de compartilhamento; isso não elimina automaticamente coleta. As classificações do formulário não substituem o conteúdo mais amplo da política.

## Práticas de segurança e direitos

- HTTPS é usado para MET/R2 no candidato; confirmar URLs, SDKs e configuração do artefato antes de declarar criptografia de todos os dados em trânsito. HTTPS não significa que o destinatário não possa ler os dados.
- Solicitação de direitos/exclusão por `suporteterrabr@gmail.com`; implementar o atendimento e retenção declarados. Não apresentar limpeza/desinstalação como mecanismo que apaga tudo nos fornecedores.
- O Terra não oferece criação de conta própria nesta revisão. A exigência específica de apagar conta precisa ser avaliada pelo fluxo real; direitos sobre dados de suporte/fornecedores permanecem.
- Nenhuma avaliação independente de segurança foi comprovada; não declarar certificação.

## Público, classificação e acesso

O público pretendido registrado é 13+. Conciliar seleção de faixas no Console, IARC e obrigações aplicáveis de proteção de adolescentes antes de incluir esses participantes. Não inventar classificação indicativa nem afirmar que ausência de login/ads dispensa controles. Os nomes/miniaturas reais do candidato devem orientar a classificação; nomes ajustados durante o teste exigem reconferência quando mudarem conteúdo/oferta.

Testes práticos de wallpapers/efeitos/nomes durante os 14 dias não dispensam política e declarações corretas no início. Para testar compras sem cobrar, inscrição na faixa e cadastro como testador de licença são configurações distintas; conferir a indicação de compra de teste no checkout. Não enviar ao Console o APK local com pacote `.avaliacao` ou seu destrave inicial como se fosse o candidato de distribuição.

## Registro para concluir o formulário

Gabriel/responsável pelo Console: registrar commit, versão/código, permissões mescladas, lista e práticas dos SDKs, fluxos externos, configurações/retencão de fornecedores, respostas escolhidas com justificativa, URL pública e data da conferência. Rafael: confirmar suporte, público, compras, fornecedores e evidências. Manter registros pessoais de testadores fora de documentos públicos.

## Fontes oficiais

- [Formulário: coleta, compartilhamento, dados e exceções](https://support.google.com/googleplay/android-developer/answer/10787469?hl=pt-BR).
- [Declaração de uso: Billing e histórico de compras](https://developer.android.com/privacy-and-security/declare-data-use).
- [Dados de SDKs Google Play services](https://developers.google.com/android/guides/play-data-disclosure).
- [Política de dados e privacidade pública](https://support.google.com/googleplay/android-developer/answer/10144311?hl=pt-BR).
- [Teste de compras/licenças](https://developer.android.com/google/play/billing/test?hl=pt-br).

Nenhum formulário enviado nesta preparação. Conferência técnica/editorial não registra parecer jurídico ou aprovação do Google.
