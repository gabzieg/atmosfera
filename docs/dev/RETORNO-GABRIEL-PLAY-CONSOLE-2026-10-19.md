# Retorno para Gabriel — Play Console e teste fechado

Consolidado em 19/10/2026 a partir das respostas de Rafael e dos prints apresentados entre 17 e 19/10. Registro administrativo e roteiro de revisão, não aprovação jurídica nem comprovação de execução no AAB final. Nenhuma conta, recibo, credencial ou captura com e-mails pessoais foi incluída no Git.

## Situação atual e alcance

- Rafael informou avanço de **3/11 para 9/11** nas tarefas de configuração do app. Não houve confirmação posterior do contador nem identificação definitiva dos dois itens restantes. Segurança dos dados e detalhes da loja foram trabalhados, mas a conclusão técnica/legal não deve ser inferida do contador.
- Escopo aprovado: teste fechado no **Brasil**, somente **adultos**, compras com **contas de licença e instrumentos de teste**, sem cobrança real. Ainda não há início confirmado dos 14 dias nem vigência final dos documentos.
- Gabriel continua responsável pela revisão/aprovação final dos textos e identificação do candidato. Depois da aprovação, sincronizar fontes, HTMLs/assets, Google Sites e Console; a edição do repositório não republica o Sites.
- Premium inclui futuros efeitos/estilos sem nova compra de Premium; artes/variantes continuam separadas. Não há promessa de frequência de lançamentos. Plano de continuidade/retirada de artes ainda deve ser concluído antes de cobrança real.

## Bloqueio da versão de teste fechado — prioridade técnica

O print da criação da versão mostra dois erros do Console:

> Não é possível lançar esta versão porque ela não permite que os usuários existentes façam a atualização para os novos pacotes de apps.

> Esta versão não adiciona nem remove pacotes de apps.

O print não contém os detalhes do pacote e **não permite atribuir uma causa única**. Gabriel deve:

1. Conferir se o AAB correto foi enviado, terminou de processar e está efetivamente incluído na versão, incluindo os últimos ajustes do projeto.
2. Conferir o maior `versionCode` já usado no Console e o código do candidato; não reservar um número apenas por builds locais ou pacote de avaliação.
3. Verificar identidade do pacote, assinatura, composição da versão, compatibilidade e segmentação de dispositivos, inclusive caminhos de atualização dos usuários existentes.
4. Corrigir a composição/pacote conforme o diagnóstico, salvar e reconferir a validação no Console.
5. Informar **branch, hash do commit, versionName e versionCode** do artefato enviado. Não foi confirmado um AAB novo nesta conversa.

## Países — configuração divergente do escopo

Rafael informou em 19/10 que selecionou **todos os países disponíveis**. A orientação foi revisar e restringir a **Brasil**; a execução da correção ainda não foi confirmada.

- No teste fechado: Gerenciar faixa > Países/regiões; verificar sincronização com produção antes de editar.
- Conferir separadamente disponibilidade de produção e eventual pré-registro. Não presumir que alterar uma tela corrigiu todas as faixas.
- A segmentação usa o país da conta Google Play do participante, não apenas sua localização física.
- A análise de documentos feita para Brasil não representa aprovação de distribuição internacional.

Referência: [distribuição por país](https://support.google.com/googleplay/android-developer/answer/7550024?hl=pt-br).

## Testadores e compras simuladas

- Print de Teste de licença mostra a lista **Testadores internos Terra** selecionada, com **sete contas**, e resposta `RESPOND_NORMALLY`. O nome da lista não determina a faixa em que será usada. Não há comprovação posterior de salvamento nem de checkout com instrumento de teste.
- O painel anterior do teste fechado mostrava **zero participantes no momento** e exigia **12 participantes por 14 dias** para solicitar acesso à produção. As sete contas cadastradas não equivalem a adesões ou instalações; não houve nova contagem confirmada.
- Completar o grupo elegível, publicar o candidato, disponibilizar o link de adesão e acompanhar o requisito efetivo do Console. Teste interno e licença não substituem adesão à faixa fechada.
- Conferir compra, restauração, cancelamento/erro, download e direitos com conta e instrumento de teste. Não interpretar compra simulada como aquisição comercial definitiva.

## Segurança dos dados — rascunho a validar

A prévia apresentada mostrou: histórico de compras, local aproximado, e-mails, fotos, diagnóstico e interações no app; declaração de criptografia em trânsito; link para exclusão; e a frase **"Os dados não são compartilhados com terceiros"**. Isso descreve o formulário preenchido, não uma conclusão de auditoria.

Pontos que Gabriel precisa resolver antes do envio:

1. **MET Norway:** o candidato envia localização aproximada diretamente à API. A MET informa registro de IP e possíveis coordenadas. Não declarar processamento efêmero. O onboarding fonte informa o envio à MET e permite aceitar localização ou usar São Paulo; conferir essa jornada no artefato final e avaliar a exceção de compartilhamento por ação informada/consentida. Permissão Android sozinha não resolve o enquadramento. A frase de ausência de compartilhamento permanece pendente de validação.
2. **Cloudflare:** conferir finalidade dos IPs, caminhos e demais metadados, logs/exportações e papel contratual do serviço. Avaliar compartilhamento, identificadores e coleta de interações. Prévia é solicitada automaticamente ao abrir a arte; não classificar todo o fluxo como opcional apenas porque o relato de suporte é voluntário.
3. **Suporte:** o código abre cliente externo com cena/arte/efeito, sintomas, versão do app, fabricante/modelo e Android; usuário pode editar e anexar prints antes de enviar. Foram propostas categorias de e-mails, fotos e diagnóstico, opcionais. Validar o alcance efetivo e as exceções aplicáveis; não declarar leitura automática da caixa de e-mail ou galeria. Separar conteúdo de e-mail de **endereço de e-mail**: o segundo não apareceu na prévia e sua inclusão foi solicitada, mas não foi confirmada.
4. **Billing:** proposta de histórico de compras coletado, não efêmero, para funcionalidade/validação e prevenção de fraude, sem dados de cartão no app. Validar os dados enviados/recebidos pela integração final e o caráter obrigatório/opcional: há consulta/restauração de direitos e não basta presumir opcionalidade porque existe uso gratuito.
5. **Artefato:** conferir SDKs/dependências e manifesto mesclado, endpoints HTTPS, ausência de configuração de servidor HTTP de teste no fluxo de distribuição, e prática de criptografia para todos os dados declarados. Não foi realizada auditoria do AAB final nesta atualização.
6. **Exclusão:** canal previsto é `suporteterrabr@gmail.com`; confirmar acompanhamento e execução. Não afirmar exclusão automática universal em 90 dias nem que o Terra apaga registros mantidos por Google/MET/Cloudflare sob suas próprias regras.

As escolhas sugeridas na conversa são material para revisão, não instruções para declarar coleta inexistente ou omitir fluxo efetivo. Conferir finalidades e obrigatoriedade em "Mostrar detalhes" e exportar o CSV para revisão sem dados pessoais antes da submissão final.

Referências: [Segurança dos dados](https://support.google.com/googleplay/android-developer/answer/10787469?hl=pt-BR), [termos MET](https://api.met.no/doc/TermsOfService), [uso de dados Android](https://developer.android.com/privacy-and-security/declare-data-use).

## Classificação, contato e página da loja

- O resultado IARC apresentado foi **14 anos no Brasil**, sem descritores de conteúdo, com elemento interativo de compras no aplicativo. Rafael informou resultados Livre/3 anos nas outras regiões. Resultado não equivale a aprovação do público-alvo, aferição de idade ou conclusão da revisão do catálogo.
- Questionário preenchido com conteúdo online e compras digitais; compras não aleatórias. Conferir se as respostas correspondem ao catálogo completo e ao artefato.
- Configuração mostrada: **App / Personalização**. Contato orientado: `suporteterrabr@gmail.com`; site `https://sites.google.com/view/terra-live-wallpaper`. Salvamento final dos contatos não foi comprovado.
- A prévia da política mostrava `?authuser=1`; foi orientado corrigir o campo de Política de Privacidade para `https://sites.google.com/view/terra-live-wallpaper/privacidade`. O link de exclusão já aparecia sem o parâmetro. Correção final não confirmada; o parâmetro não comprova página privada.
- Foram preparados textos de breve descrição e descrição completa. Antes de usar, validar recursos, oferta e ausência de promessas não entregues no candidato.
- Pendências visuais apresentadas: ícone oficial 512 × 512, gráfico 1024 × 500 e capturas reais da versão enviada (2 a 8 para telefone, conforme a tela). Não houve produção/upload desses recursos nesta conversa. Vídeo é opcional. Capturas para tablet têm pendência separada no checklist e não foram produzidas aqui.

## Requisitos do Google para Brasil e idade

- A página regional do Google exige verificações para comerciantes que vendem no Brasil, conforme tipo de perfil e solicitações do Console/Google Payments. Rafael deve conferir alertas, tipo de conta e perfil; Gabriel deve considerar isso antes de cobrança real. Não há evidência de verificação concluída, nem conclusão de que é obrigatório abrir CNPJ apenas para este teste.
- Contas pessoais e de organização podem monetizar com perfil para pagamentos; essa possibilidade não elimina documentação fiscal/comercial efetivamente exigível ao titular.
- **ECA Digital:** registrar a avaliação do alcance ao TERRA e do possível acesso por menores, mesmo com recrutamento atual só de adultos. Classificação de 14 anos não comprova aferição de idade. Avaliar necessidade e desenho de integração de sinais de idade com biblioteca atualmente suportada; não implementar por cópia de referência antiga à versão 0.0.3.
- Não há loot boxes no modelo de compra informado: usuário escolhe arte/recurso conhecido.
- As regras territoriais para outros países não foram adotadas como escopo de lançamento. Corrigir a seleção ampla antes de tratar distribuição como exclusivamente brasileira.

Referências consultadas na conversa: [requisitos por país](https://support.google.com/googleplay/android-developer/answer/6223646?hl=pt-BR), [tipos de conta](https://support.google.com/googleplay/android-developer/answer/13634885?hl=pt-BR), [ANPD/ECA Digital](https://www.gov.br/anpd/pt-br/assuntos/eca-digital), [Play Age Signals](https://developer.android.com/google/play/age-signals/overview). Conferir normas e documentação técnica vigentes na decisão final; os links não substituem o registro de enquadramento.

## Divisão dos próximos passos

**Rafael:** confirmar países restritos a Brasil; alertas e perfil de pagamentos; estado final de Data Access Logs; tokens/intermediários e encaminhamento Gmail ainda pendentes; acompanhamento do suporte; participantes/licenças/adesões; recursos da loja.

**Gabriel:** diagnosticar erros da versão e identificar AAB/commit; validar declaração de dados e fornecedores/SDKs; revisar público/ECA Digital e textos; registrar aprovação e vigência do candidato; sincronizar entregas e confirmar condições para envio.

Evidência administrativa anterior: [conferência de Rafael](../../.lgpd/conferencia-rafael-2026-10-17.md). Decisões de produto preservadas em [respostas de 15/10](RESPOSTAS-RAFAEL-TESTE-FECHADO-2026-10-15.md). Nenhuma mensagem foi enviada ao Gabriel por esta atualização; o compartilhamento é pelo Git.
