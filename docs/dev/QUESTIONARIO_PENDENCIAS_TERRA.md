# Questionário das informações que ainda faltam — Terra

**Consolidação em 08/10/2026:** revisadas as conversas paralelas
“Esclarecer uso do catálogo” e “Organizar a estrutura da pasta”. As perguntas
1 (compra por wallpaper), 4 (catálogo), 5 (origem declarada das artes),
7 (e-mail do Willian) e 8 (canal de atendimento) estão respondidas.
Permanecem abertas as perguntas 2, 3, 6 e 9. Rafael esclareceu que “50%”
significa desconto, com alguns wallpapers sempre em oferta a R$ 0,99.
O `keystore.properties` foi recebido na raiz do workspace em 08/10/2026;
a configuração de assinatura deixou de ser uma informação pendente.
A organização da pasta não alterou o
projeto Android nem o caminho da chave e não resolve essas perguntas.

**Tarefas decorrentes das respostas, sem pedir nova decisão:** aplicar e
conferir as exclusões no manifesto remoto e no app oficial; preservar os
registros de geração disponíveis; alinhar a implementação ao Premium somente
de efeitos/estilos; completar os textos com os dados já conhecidos; publicar
as páginas e executar os testes em aparelho. O AAB gerado anteriormente não
comprova a aplicação dos novos cortes. Antes de sua entrega como versão final,
conferir o conteúdo; se houver alteração do app, gerar e validar outro bundle.

**AAB — atualização de 08/10/2026:** concluída a assinatura local de
`output/terra-aab-1.0.5-v6/terra-1.0.5-v6-ASSINADO.aab`, usando a chave e a
configuração fornecidas pelo titular. Verificada a assinatura criptográfica
em todas as 1.093 entradas de conteúdo e validada a estrutura pelo bundletool.
Certificado SHA-256:
`E018F8A8DB9B8BD3C207EAA447A53BD502F5F60AD7224948C532BAB0224DE316`.
A comparação com o certificado de upload no Console continua pendente:
o navegador não respondeu nesta consulta. Conferência dos novos cortes do
catálogo e testes em aparelho também continuam pendentes. Não houve envio ao
Console. Preservado o bundle sem assinatura como artefato de origem.

**Histórico da geração em 07/10/2026:** gerado e validado o bundle de release
1.0.5/código 6, ainda **sem assinatura**. Arquivo na raiz do workspace, em
`output/terra-aab-1.0.5-v6/terra-1.0.5-v6-SEM-ASSINATURA.aab`. Chave pública de
compras incluída e R8 9.1.29 aplicado somente na geração local, preservando
a main. Rafael forneceu `atmosfera-release.jks` na raiz do workspace em
07/10/2026. O arquivo local `keystore.properties`, com alias e configuração
de assinatura, foi recebido na raiz do workspace em 08/10/2026. O recebimento do .jks
não comprova a correspondência com o certificado de upload do Console.
Não solicitar senhas pelo chat nem incluir os arquivos privados no Git. Nenhum AAB foi
enviado ao Play Console; testes em aparelho continuam pendentes.

## Lista atual — após integração da PR #46

Conferido na main do GitHub em 05/10/2026. Esta lista substitui as perguntas
abertas no histórico abaixo; os registros anteriores foram preservados como
histórico, não como estado atual. Clima somente na home foi implementado,
testado e integrado à main pela PR #46. Aceite em aparelho e envio de AAB
continuam sendo tarefas de execução.

Pode responder pelo número, ou indicar “a definir”.

1. **Respondido por Rafael em 08/10/2026:** R$ 1,99 compra um único wallpaper/arte específica. Exemplo: “cabana em low poly”; não libera todas as cabanas nem todos os estilos/variantes do cenário. A compra da arte permanece independente do Premium de efeitos/estilos. Rafael pretende disponibilizar um lote de imagens por semana. Essa frequência é planejamento comercial, ainda não promessa contratual de entregas. Rafael esclareceu que “50%” se refere a desconto: manter sempre alguns wallpapers selecionados em oferta a **R$ 0,99**, com seleção rotativa. Registrar R$ 0,99 como preço final promocional e R$ 1,99 como preço regular informado; o percentual exato dessa redução é aproximadamente 50,25%. Não apresentar uma contagem regressiva ou prazo de promoção que não corresponda à oferta real. A configuração de produtos/preços e sua apresentação no app ainda precisam ser implementadas e conferidas; o registro desta decisão não altera os preços no Console.
2. Se forem adicionadas novas variantes ao mesmo cenário, quem já comprou recebe essas variantes sem pagar novamente? Novos efeitos/estilos futuros também estarão incluídos no Premium atual?
3. Haverá pacotes comerciais no lançamento? Se sim, quais cenários/artes entram em cada pacote e qual será o preço?
4. **Respondido por Rafael em 07/10/2026:** incluir todo o acervo e suas variantes atuais, exceto Simpsons (`simpsons`), Dragon Ball (`budokai`), Naruto (`konoha`), Times Square (`timessquare`), Burj Khalifa (`burj`), Ogro (`ogro`) e Torre Eiffel (`eiffel`). O inventário de 03/10 já excluía quatro desses temas; retirar também Times Square (4 artes), Ogro (4) e Eiffel (15) resulta em **71 cenas e 366 artes nesse inventário**. Os sprites `pixel_retro` e `pixel_retro_2` também ficam fora do app oficial; eventual uso informal interno não autoriza distribuição pública. Eiffel fica fora por enquanto, com revisão futura e aviso na sua oferta na loja: “Este wallpaper não inclui o show de brilhos noturnos da Torre Eiffel devido a restrições legais.” A decisão está registrada; falta aplicar e conferir o corte no manifesto de produção e no AAB. Isso não define o conteúdo de cada compra, packs ou direitos a variantes futuras.
5. **Respondido por Rafael em 07/10/2026:** todas as demais artes são próprias, geradas com GPT. A pergunta sobre comprovantes de licença de terceiros dessas artes está encerrada com base na declaração do titular. Preservar os arquivos originais e os registros de geração disponíveis como documentação de origem; não foi realizada auditoria independente de originalidade. As exclusões de cenas e dos sprites `pixel_retro`/`pixel_retro_2` continuam válidas.
6. Qual será o endereço público definitivo do site? Quais serão os links de Privacidade, Termos e Contato? Se a hospedagem ainda não estiver definida, informar isso.
7. **Respondido por Rafael em 08/10/2026:** e-mail do Willian para coordenar a publicação do site: **willian.gabriel.siq@gmail.com**. Registro do contato; não houve envio de mensagem.
8. **Respondido no retorno de Gabriel:** suporte, reembolso e privacidade usam o mesmo canal, suporteterrabr@gmail.com. Não perguntar novamente.
9. Qual é o link da branch, commit ou PR com os cinco documentos novos mostrados na foto de Gabriel? **A PR #43 não contém esses documentos:** conferência de arquivos mostra BUCKET-R2.md, CAMINHO_GITHUB_PLAY_TERRA.md, CHECKLIST_SITE_TERRA.md e três arquivos de ferramentas R2. A foto anterior mostrava a consolidação de produto/dados e documentos em docs/legal/revisao-2026-10-05/. Não confundir as duas entregas.

**Opcional, se forem ativados alertas automáticos:** qual e-mail deverá receber os alertas de consumo do R2? Rafael já está confirmado como responsável pelo acompanhamento; não é necessário repetir essa atribuição.

**Assinatura local — configuração recebida:** Rafael forneceu em 08/10/2026 o `keystore.properties`, na raiz do workspace, junto ao `atmosfera-release.jks` já recebido. Não solicitar novamente alias ou senhas pelo chat nem incluir os arquivos privados no Git. A assinatura isoladamente não exige recompilação; a adequação do bundle às exclusões decididas depois da geração ainda deve ser conferida, e alterações no app exigem novo bundle. Conferir a assinatura e sua correspondência com o certificado de upload do Console antes de envio.

**Atualização em 06/10/2026:** disponibilidade do código 6 conferida diretamente
no Play Console, em Todos os pacotes de apps: códigos 1, 3 e 5, três registros
no total. Não é necessário pedir novamente essa confirmação agora. Gabriel
relatou revisão da PR #46 e gate completo na main aprovado, sem impedimento
identificado no código. Continuam pendentes teste em aparelho e entrega do
AAB assinado. Registrar no teste: instalação nova sem clima, nome da cidade,
cadência após voltar à home, bloqueio/tela apagada/app por cima e AOD.

Não são novas perguntas: identidade/endereço do titular, canal de suporte,
retenção de atendimento de 60 dias, Premium separado dos cenários, preços já
confirmados, público 13+, cidade padrão São Paulo, ausência de anúncios,
responsável pelo teste fechado e acompanhamento do R2. Aplicar os dados já
conhecidos aos textos/Console, conferir o artefato assinado, realizar testes
em aparelho, publicar páginas e concluir o teste fechado são tarefas.

Referências atuais: PR #46 integrada; PR #43 de documentação/publicação do R2
ainda aberta. SPEC/CONTATO atuais contêm registros antigos e campos não
preenchidos; não usar isso para pedir novamente informações já confirmadas.

### Retorno de Gabriel fotografado às 22h37

- Premium somente efeitos/estilos já está decidido pelo titular. A alternativa
  de incluir artes das cinco cenas não deve ser apresentada como decisão nova
  obrigatória. O código atual de ArteFundo.atual, em engine/Estilo.kt, ainda
  libera arte por Plano.isPremium: divergência de implementação a corrigir
  mantendo o modelo autorizado, com análise de ofertas já disponibilizadas.
- A sugestão de lançamento em fases e de não vender pacotes nessa etapa foi
  relatada por Gabriel. Posteriormente, Rafael confirmou todo o acervo com as
  exclusões registradas na pergunta 4, encerrando a decisão sobre o catálogo.
  Rafael também confirmou em 08/10/2026 a compra por wallpaper individual
  (pergunta 1). Inclusão de variantes futuras e os pacotes comerciais
  continuam em aberto nas perguntas 2–3, sem reabrir a separação Premium/cenários.
- A resposta de contato elimina a pergunta 8. A identificação dos cinco novos
  documentos não foi resolvida pela referência à PR #43; os caminhos diferem.

---

Revisão do Console em 1º de outubro, R2 em 3 de outubro e GitHub em **4 de outubro de 2026** (`origin/main` em `d3b0cc2`, PR #41), complementada pelas quatro fotos enviadas pelo titular. Pode responder pelo número. Se ainda não existir, escreva **“não existe”** ou **“a definir”**. As perguntas técnicas podem ser encaminhadas ao responsável indicado. Detalhes: [REVISAO_GITHUB_GABRIEL_2026-10-04.md](REVISAO_GITHUB_GABRIEL_2026-10-04.md).

## Já confirmado — não precisa responder novamente

- Titular, fornecedor e controlador: **Rafael Huppes**, pessoa física. Fonte: esclarecimento do titular, dados da conta e BIEL/passo-a-passo-rafael-publicacao.md.
- Endereço: o cadastrado no Console, já transcrito nos rascunhos legais.
- Canal do produto: **suporteterrabr@gmail.com**, Gmail/Google. Confirmado pelo titular e pela identificação do proprietário no Console.
- E-mail pessoal **rafael.huppes@gmail.com**: ainda aparece como contato com o Google e no perfil público do desenvolvedor. Não foi confundido com o canal escolhido para o produto.
- Nome: **Terra - Live Wallpaper**. Pacote cadastrado: **com.terra.wallpaper**.
- Premium cadastrado: **terra_premium**, compra **compra-permanente**, ativa no Brasil por **R$ 49,90**. Descrição: oito efeitos vivos e todos os estilos de efeito; não inclui cenários extras.
- A chave pública RSA está disponível em **Configuração de monetização**. Não é necessário pedir que o titular a forneça novamente; deve ser integrada à versão correta.
- Última versão interna: **1.0.4, código 5**, disponível para testadores internos. Produção inativa.
- Exigência mostrada para esta conta: **12 testadores por pelo menos 14 dias continuamente** no teste fechado; **0 participantes** naquele teste na consulta.
- Sem anúncios, sem conta própria e sem assinaturas: informado no contexto e compatível com esta cópia local. A versão 1.0.4 ainda precisa de conferência técnica.
- Os documentos BIEL já registram o corte de cenas de franquias. Não se pede novamente uma decisão genérica sobre esses mesmos cortes; falta validar o catálogo efetivamente distribuído.
- Cloudflare: conta de Rafael Huppes (`rafael.huppes@gmail.com`), bucket **terra-acervo**, classe **Standard**, região automática escolhida como Eastern North America. Acesso de upload **terra-acervo-upload**, leitura/escrita somente nesse bucket, sem expiração, autorizado pelo titular. A credencial fica criptografada pelo Windows fora do projeto e não integra o app.
- Catálogo preparado para publicação: **74 cenas e 389 artes**, excluindo `simpsons`, `budokai`, `konoha` e `burj`. Inventário: `output/terra-r2-publicacao-2026-10-03/manifest.json`; resultado do upload: `output/terra-r2-publicacao-2026-10-03-relatorio.json`, na raiz do workspace. Isso não substitui a confirmação de licenças nem define os produtos comerciais do lançamento.
- GitHub atualizado: [gabzieg/atmosfera](https://github.com/gabzieg/atmosfera), branches `main` e `integracao/lancamento-teste`, ambas em `d3b0cc2`. Commit `958d97e` registra o build 1.0.4/código 5; a árvore atual confirma `com.terra.wallpaper` e `terra_premium`. Mudanças posteriores não comprovam atualização do AAB no Console.
- Gabriel confirmou: Premium somente efeitos; cenários avulsos por R$ 1,99; packs comerciais ainda indefinidos; prévia estática suficiente; Cabana embarcada para uso inicial offline; bucket público sem backend nesta etapa; miniaturas no AAB, com atualização do app para acrescentar cenas.
- Fornecedor de clima atual no GitHub: **MET Norway**, no commit `d1829a2`, incorporado pela PR #40. As perguntas sobre contratar a Open-Meteo foram superadas para esse código; não se aplica essa conclusão automaticamente ao AAB anterior.
- Responsável técnico e pelos textos legais: **Gabriel Brustolin Ziegemann**, e-mail registrado nos commits `gbrustolinz@outlook.com`. Isso não designa responsável pelo atendimento, encarregado, advogado revisor ou dono da conta; o titular confirmado continua Rafael.
- Responsável pelo acompanhamento do consumo do R2: **Rafael Huppes**, confirmado diretamente em 04/10. Falta escolher o e-mail de destino dos alertas; acompanhamento automático ainda não ativado.
- Rafael informou possível dificuldade do Gabriel em enviar a última versão ao GitHub. A revisão em `2038c73` retrata somente o código remoto disponível; divergências podem já estar corrigidas em trabalho ainda não enviado. Conferir a próxima atualização antes de repetir correções.
- Atualização posterior: PR #41 confirmada na main (`d3b0cc2`), política v1.3 e público-alvo **13+**. Fotos do retorno recebido por Gabriel registram decisão de usar **São Paulo/SP** como cidade padrão, mantendo Novo Hamburgo/RS como foro dos Termos; a divergência anterior sobre a cidade está superada por essa decisão relatada. Não foi alterado código local antigo nesta revisão.
- Uso de `r2.dev` escolhido pelo titular para a etapa inicial de testes. É endpoint de desenvolvimento, com limitação de requisições independente da franquia de armazenamento; não está aprovado tecnicamente como solução de produção. Domínio próprio permanece tarefa anterior ao lançamento público conforme orientação da Cloudflare e trava local de release.

## Código correto — respondido

1. **Qual é o link do repositório e o nome da branch, ou a pasta local, que gerou a versão 1.0.4 (código 5) enviada ao Console?**

   Respondido: repositório e branches acima; commit de build `958d97e`. As referências remotas atualizadas usam os identificadores corretos. A árvore de trabalho local antiga foi preservada. Transportar as correções locais para a fonte atual e conferir o AAB é tarefa técnica, não nova pergunta ao titular.

## Atendimento e identificação

2. **Quem vai responder suporte, pedidos de reembolso e mensagens de privacidade recebidas em suporteterrabr@gmail.com? Qual é o nome e o e-mail dessa pessoa?**

   Respondido nas fotos: **Gabriel**, pelo canal **suporteterrabr@gmail.com**, para suporte. Atendimento de privacidade atribuído a Rafael, abaixo. Reembolsos devem ser encaminhados conforme o fluxo Google Play e a legislação aplicável, sem presumir dispensa das obrigações do fornecedor.

3. **Já existe uma pessoa designada para o atendimento de proteção de dados/encarregado? Se sim, qual é o nome e o e-mail? Se não, responda “a definir”.**

   Fotos indicam **Rafael Huppes** para atendimento de privacidade. Pergunta restante: **qual e-mail Rafael usará para pedidos de privacidade: suporteterrabr@gmail.com ou rafael.huppes@gmail.com?** A atribuição do atendimento não substitui a validação do enquadramento jurídico de encarregado/dispensa.

4. **Em qual arquivo local está o CPF do fornecedor que deve completar a identificação dos Termos?**

   Respondido nas fotos: não há arquivo no projeto; dado no perfil de pagamentos do Console. Não precisa responder novamente esta pergunta.

   Localização informada nas fotos: **perfil de pagamentos do Play Console**; não existe arquivo no projeto. Recuperar esse dado com autorização adequada é tarefa de preenchimento, não motivo para pedir novamente a localização ao titular. Não copiar documentos de identidade ou dados bancários para o repositório.

5. **Por quanto tempo as mensagens e anexos de atendimento serão guardados depois de resolver o pedido?**

   Respondido nas fotos: **até 60 dias após a resolução**, ressalvadas obrigação legal ou necessidade de defesa em processo. Aplicar a regra aos registros controlados pela equipe, sem prometer exclusão dos registros autônomos do Google.

## Links legais

6. **Qual é o link público da Política de Privacidade?**

   Link registrado em `docs/legal/PRIVACIDADE.md` §15: `https://terra-livewallpaper.pages.dev/privacidade/`. Em 04/10 o host não resolveu nesta verificação; publicação não comprovada. Corrigir a hospedagem/confirmar o destino é tarefa de publicação.

7. **Qual é o link público dos Termos de Uso?**

   Rota prevista no mesmo host, conforme a estrutura de hospedagem documentada: `https://terra-livewallpaper.pages.dev/termos/`. Mesmo resultado de host não resolvido; link ainda não validado como público.

8. **Qual é o link público da página de Contato?**

   Rota prevista: `https://terra-livewallpaper.pages.dev/contato/`. Mesmo resultado de host não resolvido; link ainda não validado como público.

9. **Se essas páginas ainda não estão publicadas, quem vai hospedá-las e qual é o e-mail dessa pessoa?**

   Responsável informado nas fotos: **Willian**. Falta somente: **qual é o e-mail do Willian e qual endereço público ficará ativo após a hospedagem?**

   O campo de URL da Privacidade estava vazio no Console. Os arquivos locais existem, mas arquivo local não comprova publicação. A data de vigência será preenchida na publicação efetiva; não é necessário inventar uma data agora.

## Acervo e fornecedores

10. **Qual é a URL HTTPS de produção do acervo no Cloudflare R2?**

    **Ativado com confirmação do titular:** https://pub-722473b758b144279fc34ecb931a6045.r2.dev/ . URL de desenvolvimento/testes, com limites próprios; domínio de produção continua pendente. Qualquer pessoa com o endereço de um objeto pode baixá-lo; o direito de uso/desbloqueio dentro do app é separado.

11. **Qual é o link do manifest.json de produção?**

    Link de testes: https://pub-722473b758b144279fc34ecb931a6045.r2.dev/manifest.json . Resultado da verificação pública registrado no relatório de publicação; não é URL de produção.

12. **Qual e-mail deverá receber os alertas de consumo do R2: rafael.huppes@gmail.com ou suporteterrabr@gmail.com?**

    Responsável pelo acompanhamento: **Rafael Huppes**, confirmado em 04/10. Resposta sobre o e-mail: ______________________________________________

    O monitor ainda não está ativado. A responsabilidade pelo acompanhamento não altera, sozinha, os responsáveis por desenvolvimento, suporte ou atualização do acervo.

13. **Open-Meteo — pergunta superada para o código atual.** Gabriel migrou para MET Norway. Licença: https://api.met.no/doc/License; condições: https://api.met.no/doc/TermsOfService. O AAB anterior permanece sujeito ao fornecedor que efetivamente utiliza.

14. **Adequação da MET Norway — tarefa técnica atribuída ao responsável pelo código.** Conferir atribuição, cache, consultas apenas durante uso, tratamento de 429, distribuição de requisições no tempo e limite agregado de 20 req/s. Não presumir que a troca de fornecedor encerra a validação operacional. Ver relatório desta revisão.

## Lançamento e conteúdo

15. **O catálogo preparado de 74 cenas e 389 artes será todo incluído no lançamento? Se não, quais itens devem ficar de fora?**

    Resposta: ______________________________________________

    **Respondido em 07/10/2026:** todo o acervo e suas variantes atuais, exceto `simpsons`, `budokai`, `konoha`, `timessquare`, `burj`, `ogro` e `eiffel`. Sprites `pixel_retro` e `pixel_retro_2` fora do app oficial. Ver item 4 da lista atual. Falta aplicar e validar o corte nos artefatos distribuídos.

16. **Qual é a lista de artes incluídas em cada compra avulsa de R$ 1,99? Haverá packs comerciais já no lançamento? Se sim, quais itens e preços?**

    Resposta: ______________________________________________

    Preço avulso já respondido pelo Gabriel: R$ 1,99. Packs comerciais explicitamente indefinidos na SPEC. Ainda falta discriminar se cada SKU inclui uma variante ou todas as artes da cena, finalizar a composição e cadastrar os produtos; o Console consultado anteriormente tinha somente Premium.

17. **Qual é a idade mínima do público para o qual o Terra foi desenvolvido e será divulgado?**

   Respondido e confirmado no GitHub: **13+**, com autorização/supervisão de responsável entre 13 e 17 anos; Política v1.3 na PR #41. Não confundir público-alvo com resultado da classificação IARC; não prometer aprovação automática pelo Google.

18. **O lançamento será somente no Brasil? Se não, quais outros países?**

   Decisão relatada nas fotos: **sem restrição de países**. Ainda exige configurar países/territórios efetivamente disponíveis no Console, preços e adequação legal/etária nos mercados alcançados. A oferta anteriormente verificada do Premium no Brasil não comprova distribuição mundial já ativada.

19. **Qual é o link ou arquivo com os registros de autoria/licença das artes e sprites que permanecerão no lançamento?**

    Resposta: ______________________________________________

    **Respondido por Rafael em 07/10/2026:** todas as demais artes são próprias, geradas com GPT; a pergunta sobre comprovantes de licença de terceiros dessas artes está encerrada pela declaração do titular. Preservar os arquivos originais e registros de geração disponíveis. As folhas `pixel_retro`/`pixel_retro_2` ficam desconsideradas no app oficial; eventual uso informal interno apenas. Conferir a ausência das folhas excluídas nos artefatos finais.

20. **Artes, variantes ou efeitos futuros estarão incluídos nas compras atuais? Quais?**

    Resposta: ______________________________________________

    Até haver definição, os textos não prometem que todo conteúdo futuro será gratuito para compradores anteriores.

## Responsáveis pela conclusão

21. **Quem fará a revisão jurídica final e qual é o e-mail dessa pessoa?**

   Fotos relatam resposta de Rafael: **“EU”**. Rafael assume a coordenação/validação final; e-mail ainda a escolher. Essa resposta não comprova revisão por profissional habilitado nem deve ser apresentada como parecer jurídico independente.

22. **Quem vai coordenar o teste fechado com os 12 participantes e os testes reais de compra, localização e download? Qual é o e-mail dessa pessoa?**

   Responsável informado nas fotos: **Rafael Huppes**. Falta somente confirmar qual e-mail usará na coordenação.

## Trabalhos que não são perguntas ao titular

- Fonte correta identificada no GitHub atualizado; transportar as correções com revisão, sem sobrescrever trabalhos existentes. São Paulo está alinhado à decisão posterior relatada nas fotos. WallpaperService ainda chama LocationHelper; worker usa cache. Reconciliar a regra de aquisição de localização com o fluxo autorizado; evitar transportar o fallback Novo Hamburgo da cópia antiga sobre a decisão mais recente.
- Integrar a chave pública do Console ao app correto e reconciliar `terra_premium` com o código; não solicitar chave privada ou senha.
- Preencher o contato da página do app e revisar o perfil público do desenvolvedor: no Console consultado, o contato do app estava vazio e o perfil público ainda usava o e-mail pessoal. Esta revisão não alterou esses cadastros.
- Fazer as declarações de anúncios, ID de publicidade, login, recursos financeiros, governo e saúde conforme o produto real, além de completar Privacidade, Público-alvo, IARC e Data Safety. O Console mostrava 10 declarações precisando de atenção.
- Verificar logs/retenção/contratos de fornecedores, papel de cada serviço, transferências internacionais e eventual dispensa de encarregado com o responsável jurídico; isso não se deduz do nome da conta.
- Validar cancelamento HTTP, limites e integridade dos pacotes em `engine/Acervo.kt`, miniaturas remotas e inventário antes de reduzir o APK.
- Conferir AAB/manifesto/tráfego da versão distribuída. Os 36 testes da cópia local não validam automaticamente a versão 1.0.4 do Console.

Nenhum formulário foi salvo no Console, nem mensagem enviada a terceiros nesta revisão.

## Envio ao GitHub em 04/10

- Atualização técnica enviada na branch `codex/terra-r2-publicacao`, commit `7032240`, PR em rascunho: https://github.com/gabzieg/atmosfera/pull/43 . Ainda não incorporada à main.
- Inclui guia do R2, checklist do site, caminho GitHub/Play e ferramenta parametrizada de upload. Este questionário e o relatório com contatos pessoais ficaram somente locais porque o repositório é público.
- Acervo validado offline por tamanho/SHA-256: 74 cenas, 389 artes, 1242 arquivos. Nenhum novo upload de artes nesta atualização.
- Play Console consultado novamente: teste interno ativo, versão 1.0.4, código 5. Não foi enviado AAB nem criada versão vazia.
- Falta o AAB assinado da versão atual, após integração/testes de download; a chave de upload não foi encontrada neste workspace e a documentação a situa na máquina do responsável pelo build. Pergunta objetiva: **qual é o caminho ou link do AAB assinado atualizado e qual é seu versionCode?** Não enviar chave privada ou senhas pelo Git/chat.

## Retorno fotografado de Gabriel — 05/10/2026

- A foto informa consolidação de produto/fluxos de dados e pacote de rascunhos de Privacidade, Termos e Contato em `docs/legal/revisao-2026-10-05/`, além de `docs/dev/CONSOLIDACAO-PRODUTO-DADOS-E-TEXTOS-LEGAIS-2026-10-04.md`. Isso confirma trabalho relatado, não publicação nem aprovação dos textos. Esses arquivos não foram encontrados na main e nas referências remotas consultadas nesta revisão.
- Endereço público e hospedagem da landing page ainda não foram definidos; existe somente o escopo, segundo a resposta fotografada. As URLs pages.dev anteriores são propostas, não destino confirmado.
- Foi proposta fonte única de textos aprovados, com importador no site e cópias HTML no aplicativo. Falta conferir versão/fidelidade do importador e os arquivos efetivos; a igualdade das cópias é relato da foto, ainda não verificação nossa. Atualizar o site não altera o texto offline de aplicativo já instalado.
- Premium somente efeitos/estilos por R$ 49,90 e cenários separados foram reafirmados. Ainda falta definir a composição de cada compra e inclusão de variantes futuras.
- Revisão remota: main e integracao/lancamento-teste em `0fa0f6b`. PR #42 implementou freio persistente para 429/403, Retry-After e jitter do worker, com testes. Isso supera parte da pendência técnica nº 14; não valida o AAB anterior nem resolve todas as condições de uso do fornecedor.
- PR #44 registra decisão de manter consultas periódicas com app fechado. Não tratar como conformidade encerrada: os termos da MET, seção Traffic, restringem a obtenção de dados novos por apps móveis fora de uso. Wallpaper visível pode representar uso efetivo; worker com tela apagada/fundo não se justifica automaticamente por isso. Cache de coordenadas evita nova aquisição de GPS, mas não elimina a consulta de rede ao fornecedor. Freio após 429/403 não substitui o cumprimento prévio dessa regra. Referência conferida: https://api.met.no/doc/TermsOfService .

Perguntas objetivas restantes desse retorno:

1. **Cada compra avulsa de R$ 1,99 inclui uma arte/variante específica ou todas as variantes atuais do cenário?**
2. **Novas variantes adicionadas futuramente ao mesmo cenário estarão incluídas nessa compra?**
3. **Qual é o link da branch, commit ou PR que contém os cinco documentos novos mostrados na foto?** Se ainda não enviados, esse envio permanece pendente; não é necessário refazer rascunhos antes de recebê-los.

Não foi alterado código Android, enviada mensagem a terceiros, feita nova publicação no GitHub ou salvo formulário no Console a partir desta foto.

## Decisão posterior do titular — clima somente na home

Em 05/10/2026, o titular esclareceu diretamente que o Terra só deve atualizar enquanto o wallpaper estiver visível na home; outro app aberto, tela apagada ou celular bloqueado não devem gerar consultas. Isso substitui a decisão anterior de manter consultas periódicas fora de uso. Instrução pronta para encaminhamento: [INSTRUCAO_CLIMA_SOMENTE_HOME_2026-10-05.md](INSTRUCAO_CLIMA_SOMENTE_HOME_2026-10-05.md). Código e textos ainda exigem implementação e validação; não considerar essa decisão, sozinha, uma correção já entregue.
