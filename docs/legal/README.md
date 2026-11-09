# Fonte editorial dos documentos do Terra

## Proposta Pages — #89, 09/11/2026

Proposta de redação **Privacidade1.23/Termos1.13/Contato1.11**, base `d8ed440`,
para Cloudflare Pages em `https://terrawallpaper.com.br`, sem analytics. O aceite
anterior de1.22/1.12/1.10 não aprova este delta. `publicacao_web` no JSON conserva
o histórico Google Sites; domínio/URLs e `publicacao_proposta` indicam o destino
proposto, não publicação já feita. A nova redação requer aceite explícito via Friday.
`aprovado_para_publicacao=false`, `aprovacao_registro=null`, `vigencia=null` e
`versao_app=null` permanecem. Histórico editorial abaixo não prevalece sobre esta proposta.

Evento de vigência já escolhido pelo dono na #86: **dia real da publicação das
três páginas**, não dia do PR/build ou da geração local. Não preencher data agora.
**Procedimento proposto, ainda em decisão pela Friday:** revisar/aprovar o delta
identificado por SHA; este PR permanece em draft e sem merge conforme a ativação.
Antes de integrar/publicar as páginas legais revisadas, alinhar com a lead do site
como impedir publicação involuntária de rascunho. Esse bloqueio é uma proposta,
não política aprovada ou controle implementado; não impede preparação, build ou
landing por inferência. Workflow#109 ainda não executado nesta tarefa.
Com a decisão específica e publicação preparada, registrar aprovação/metadados/data
real, regenerar cópias e conferir as três páginas no evento autorizado, conciliando
app/site/Console no marco#89. Se o evento não ocorrer, não declarar publicação ou
vigência realizada. Este roteiro não cria hook, secret, workflow ou controle remoto.
O gerador exige coerência editorial, mas sua saída não autoriza publicação.
R03 do R2 fica preservado em seu escopo; mecanismo/configuração de Pages não é
certificado por aquele aceite de risco. Fontes oficiais e limites na entrega da #89.

Revisão de trabalho de 08/11/2026, consolidada a partir da rodada de 28/10 e das decisões registradas na [#77](https://github.com/gabzieg/atmosfera/issues/77), em proposta local para a [#78](https://github.com/gabzieg/atmosfera/issues/78). Fontes únicas: `TERMOS.md` (1.13), `PRIVACIDADE.md` (1.23) e `CONTATO.md` (1.11). A [nova revisão documental](../../.lgpd/revisoes/2026-10-28-documentos/REVISAO.md) registra achados e decisões pendentes; os registros anteriores abaixo são históricos, enquanto as decisões consideradas nesta consolidação estão referenciadas na #77, sem adotar conteúdo antigo como instrução. Os HTMLs são exportações; não editar cada cópia separadamente. O [registro de refinamento](../dev/REFINAMENTO-EDITORIAL-LEGAL-2026-10-15.md) reúne instruções internas e decisões pendentes. As versões refinadas não foram republicadas no Google Sites por esta execução.

```powershell
python scripts/sincronizar_documentos_legais.py
python scripts/sincronizar_documentos_legais.py --check
```

O gerador usa somente a biblioteca padrão Python e o subconjunto Markdown dos três documentos. Preserva os estilos das páginas locais no modelo; não altera a apresentação da landing. Produz as nove cópias em `docs/`, `public_html/` e assets do app, e `manifesto-documentos.json` com versões e hashes. `--check` falha se fonte, modelo, manifesto ou uma cópia divergirem. A CI executa essa conferência; o teste Android também compara `public_html`.

## Sincronização nos pedidos ao agente

A regra "Documentos legais" do [`AGENTS.md`](../../AGENTS.md) inclui geração e conferência das cópias no próprio pedido de atualização dos documentos; todo agente a lê. Não é necessário pedir a exportação separadamente. (Até 02/11/2026 isso era a skill `terra-documentos-legais`.) A regra foi registrada a pedido do usuário em 28/10/2026.

Ao modificar uma fonte, o agente atualiza a versão/data correspondente, concilia os documentos relacionados, executa o gerador e `--check`, e entrega Markdown, manifesto e HTMLs coerentes. Consultas sem atualização permanecem somente leitura. Edições feitas manualmente fora desse fluxo continuam exigindo os comandos acima; a CI recusa exportações divergentes.

Essa automação cobre os arquivos locais versionados. Não publica no Google Sites nem atualiza aplicativos já instalados.

## Estado editorial

**Aceite parcial de redação em 08/11/2026:** [registro e hashes do conjunto 1.22/1.12/1.10](https://github.com/gabzieg/atmosfera/issues/77#issuecomment-6729200287). Aprova somente a redação identificada, sem comprovar leitura individual, aprovação jurídica integral, mecanismo internacional, vigência, publicação ou envio. Os metadados públicos continuam rascunho e não foram promovidos a aprovação final. A composição versionada é preparada na [#82](https://github.com/gabzieg/atmosfera/issues/82); a #78 preserva a origem local do conjunto.

Estado editorial **rascunho para revisão**. O titular publicou o site no Google Sites em 15/10/2026. O endereço público foi registrado; os textos revisados destinam-se ao próximo pacote (código de versão 8), em preparação para teste fechado no Brasil; `versionName` ainda não definido. O evento de vigência escolhido é a disponibilização efetiva da versão final do teste, após aprovação e publicação dos textos; a data real ainda não foi definida. Não retroage sobre compras anteriores nem suspende a transparência de tratamentos anteriores. `vigencia`, `aprovacao_registro` e `versao_app` continuam nulos; não preencher pela data desta revisão. Publicação web não comprova aprovação jurídica, início do teste ou envio ao Console. A configuração editorial `revisao.json` mantém `aprovado_para_publicacao=false`; o manifesto é gerado a partir dela. As versões são lidas dos textos, sem manter outra lista no código.

Depois da avaliação e decisão do responsável, atualizar textos e `revisao.json`: registro de aprovação, vigência ISO, versão do app, domínio HTTPS e estado aprovado. O gerador recusa aprovação sem esses campos ou com metadados de rascunho ainda no texto. Preencher o JSON não executa revisão jurídica nem publica páginas; o registro deve corresponder a uma aprovação real.

## Limites da consolidação de 07/11 e do delta de 08/11

Decisões de procedimento ou declaração não certificam execução ou aprovação integral. O responsável escolheu decidir com as recomendações fundamentadas da lead; não há contratação ou revisor externo previsto. O [aceite R02 de 07/11](https://github.com/gabzieg/atmosfera/issues/78#issuecomment-6703628811) encerrou a pendência da justificativa de retenção do último clima/cache HTTP para o teste adulto, sem prazo máximo fixo e com informação/exclusão preservadas; não se estende a novas finalidades, logs externos ou público futuro. A [conclusão Cloudflare aceita](https://github.com/gabzieg/atmosfera/issues/77#issuecomment-6714896460) sustenta transparência factual sobre self-service/DPA, logs opcionais desativados e critérios de retenção, mas mantém o residual material de enquadramento/mecanismo internacional para o aceite operacional. Não exigir assinatura avulsa, prazo numérico universal ou discriminação de cada pacote como bloqueios editoriais autônomos. A [decisão R03 de 08/11](https://github.com/gabzieg/atmosfera/issues/77#issuecomment-6729114471) está registrada como escolha de negócio; o mecanismo internacional segue não demonstrado, sem base nova adotada ou fechamento desse requisito. Não há nova pergunta de escolha ao dono nem autorização de contato com fornecedor, publicação, Console ou envio. A4 da fase adulta foi concluída por [decisão de Gabriel](https://github.com/gabzieg/atmosfera/issues/78#issuecomment-6728437711) e [revisão da lead](https://github.com/gabzieg/atmosfera/issues/78#issuecomment-6728454405), como decisão contextual do responsável, sem parecer externo ou conformidade integral; o ensaio complementar relatado produziu NOT_SHARED, sem comprovar cobertura 13+. A ampliação 13+ permanece bloqueada e a integração documental pendente. As confirmações e seus limites estão nos registros internos pertinentes, sem dados de participantes.

A entrega da #78 é diff editorial local revisável. Somente o gerador canônico atualiza manifesto/nove HTMLs; não se exige novo build/APK/AAB nesta preparação nem se abre PR antes do gate final obrigatório. A aprovação/publicação e o último arquivo continuam posteriores às lacunas que possam alterar seu conteúdo.

## Endereços publicados

- Site: [https://sites.google.com/view/terra-live-wallpaper](https://sites.google.com/view/terra-live-wallpaper).
- Privacidade (URL a usar no campo correspondente do Console): [https://sites.google.com/view/terra-live-wallpaper/privacidade](https://sites.google.com/view/terra-live-wallpaper/privacidade).
- Termos: [https://sites.google.com/view/terra-live-wallpaper/termos](https://sites.google.com/view/terra-live-wallpaper/termos).
- Contato: [https://sites.google.com/view/terra-live-wallpaper/contato](https://sites.google.com/view/terra-live-wallpaper/contato).

Usar URLs sem `authuser`, que apenas seleciona uma conta Google. O nome `dominio_publico` no JSON aceita esta URL-base com caminho do Google Sites; não implica compra de domínio próprio. Fontes/HTMLs locais agora incluem os links públicos. A consulta registrada na revisão de 28/10 descreveu páginas em revisão e vigência a definir; a publicação não foi reconferida nesta consolidação. Mudanças na fonte precisam ser aplicadas/republicadas no Sites manualmente. Não há sincronização automática com o Google Sites ou com apps já instalados.

## Histórico

`historico/anterior-a-2026-10-11/` conserva as três fontes e três exportações anteriores exatamente como encontradas. Elas podem conter campos pendentes e afirmações corrigidas na revisão atual; não são fontes vigentes para nova exportação. Seu arquivamento não comprova que tenham sido publicadas ou aceitas por usuários. O Git preserva também o histórico das cópias do app.

Os rascunhos locais de 05/10 foram preservados e, por solicitação do usuário em 11/10, versionados como registros históricos, sem vigência ou aprovação para uso público no app/site. A fonte desta revisão é a pasta canônica acima. Não distribuir referências que dependam de arquivos privados locais.

Para aceite e entrega ao site, consultar [ETAPA-5-DOCUMENTOS-E-LANDING-2026-10-11.md](../dev/ETAPA-5-DOCUMENTOS-E-LANDING-2026-10-11.md).

## Respostas do titular integradas em 15/10

Preservado o refinamento editorial de Gabriel em `1bd5a70`. As respostas posteriores restringem o teste a adultos no Brasil e definem futuros efeitos/estilos inclusos no Premium. Gabriel é o responsável pela revisão/aprovação final; esse aceite não foi presumido. Detalhes e pendências em [registro de respostas](../dev/RESPOSTAS-RAFAEL-TESTE-FECHADO-2026-10-15.md).
