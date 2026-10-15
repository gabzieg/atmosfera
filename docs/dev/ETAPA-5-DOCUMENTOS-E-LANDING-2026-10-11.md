# Etapa 5 — revisão editorial e integração com a landing

## Site publicado em 15/10/2026

Rafael informou a publicação do Google Sites. Links conferidos na navegação, documentados sem seleção de conta:

| Destino | URL |
|---|---|
| Site | https://sites.google.com/view/terra-live-wallpaper |
| Política de Privacidade — campo do Console | https://sites.google.com/view/terra-live-wallpaper/privacidade |
| Termos | https://sites.google.com/view/terra-live-wallpaper/termos |
| Contato | https://sites.google.com/view/terra-live-wallpaper/contato |

Compras do teste: somente contas testadoras de licença, conforme decisão do titular em 15/10. Falta conferir a configuração efetiva das contas e dos instrumentos de pagamento. O site está publicado com metadados de rascunho; publicação não significa aprovação jurídica, vigência do candidato, cadastro no Console ou começo dos 14 dias. Ainda é necessário conferir o acesso sem login em sessão anônima e conciliar versão/vigência/textos do artefato. Alterações nas fontes locais exigem republicação no Google Sites; gerar os HTMLs não modifica o site.


Preparação concluída em 11/10/2026; **aceite jurídico e publicação pendentes**. Revisão para o candidato da PR #50, não alteração retroativa de contratos de compras anteriores.

## Alterações concretas

- Identificação: Rafael Huppes, pessoa física; canal único `suporteterrabr@gmail.com`. Removida afirmação automática de dispensa de encarregado; designação formal/enquadramento ainda precisa de avaliação.
- Oferta: Premium só efeitos/estilos; uma arte por compra; novas variantes separadas; sem packs comerciais. Não promete efeitos futuros ou lançamentos semanais. Valores registrados como preço-base planejado, sem substituir checkout da Play.
- Entrega: miniaturas locais, HD solicitado, restauração de direitos separada dos arquivos, falhas e uso offline. Não afirma que conteúdo adquirido pode ser removido sem atendimento nem que haverá servidor eterno.
- Privacidade: inclui tokens/comprovantes da Play, dados técnicos/IP/caminho de arte no R2, Gmail no atendimento, backup e limites da exclusão. Removidas alegações de que servidor estático não trata dados pessoais ou que a desinstalação elimina tudo.
- Atendimento: 60 dias após encerramento, ressalvadas obrigação legal/defesa justificada. Não é prazo universal de logs e não é limpeza automática do e-mail.
- Direitos: contato também atende reembolso/entrega; retiradas a exclusividade de responsabilidade do Google, foro imposto e promessa de 15 dias para qualquer solicitação. Permissão Android não é certificação automática de consentimento LGPD.
- Fonte canônica e nove HTMLs sincronizados; versões, hashes e status em `docs/legal/manifesto-documentos.json`. Histórico anterior preservado; rascunhos pessoais de 05/10 não publicados.

## Entrega para Willian, sem alterar o front-end

O importador público `scripts/prepare-documents.mjs` de [uWillianG/terra.app](https://github.com/uWillianG/terra.app) foi lido em 11/10. Ele lê os três `public_html/<pagina>/index.html` de um checkout do Atmosfera, adapta links e renderiza privacidade no layout da landing.

Foi executado localmente em uma pasta temporária de build: os três documentos foram importados e todos os parágrafos da fonte foram preservados (28 de privacidade, 21 de termos e 11 de contato). Nenhum arquivo do repositório de Willian foi alterado. As referências entre páginas usam caminhos relativos aceitos pelo importador. Isso verifica o contrato técnico atual, não hospedagem, aparência final ou privacidade do site.

Roteiro para Willian executar no repositório dele:

```powershell
node scripts/prepare-documents.mjs CAMINHO_DO_CHECKOUT_ATMOSFERA
```

Usar o checkout da **revisão identificada e aprovada**, conferir o manifesto e preservar as versões/status. A branch de trabalho atual é `fix/premium-privacidade-etapas-1-2`; ainda não está aprovada para publicação definitiva. O importador aponta a fonte para `main` por padrão; ao importar um rascunho de outra branch, conferir essa referência para não atribuir o documento a uma revisão diferente. Este ajuste é responsabilidade da integração do site, não foi feito no front-end nesta execução.

A mesma exportação segue para o app no próximo build. O app instalado mantém sua cópia offline e não busca automaticamente um documento novo no site. Uma atualização da landing não corrige documentos de APKs/AABs já distribuídos.

## Pendências com responsáveis e evidências

| Ponto | Responsável e próxima evidência |
|---|---|
| Vigência e versão aplicável | Gabriel/Rafael: identificar candidato aprovado e data real de disponibilização, incluindo regra para teste e compras anteriores. Não preencher retroativamente com data editorial. |
| Domínio, hospedagem e páginas públicas | Willian/responsável: URL definitiva, importação da revisão, links HTTPS sem login, confirmação de cookies, formulários, analytics, registros e terceiros reais. |
| Continuidade das compras | Rafael: prazo operacional de disponibilidade, arquivo/versionamento, restauração, interrupções e procedimento de restituição. Faltam meios concretos além de redação. |
| Efeitos/estilos futuros | Rafael: decidir inclusão no Premium; até lá nenhuma inclusão prometida. |
| Logs R2 e fornecedores | Rafael: verificar configurações, acesso, exportação e retenção na conta; contratos e regiões de tratamento. Não há prazo universal confirmado para a Cloudflare. |
| Atendimento de 60 dias | Rafael: executar rotina de exclusão, registrar encerramento/exceções, revisar acesso à caixa Gmail e anexos; não criar cópias descontroladas em issues públicas. |
| Papéis, bases e transferências | Responsável pela avaliação jurídica: conferir tratamento por operação, fundamento e mecanismo internacional aplicável; revisar contratos e adequação às resoluções ANPD. |
| Público 13+ | Rafael e avaliação jurídica: conferir LGPD/ECA Digital, aferição/supervisão aplicáveis, classificação e Console. Uma frase no termo ou ausência de login não prova cumprimento. |
| Licenças e proveniência | Rafael: inventário verificável das artes, exclusões, geração/licenciamento, código, fontes e créditos MET. Origem por IA não comprova todos os direitos. |
| Avaliação dos textos | Registrar avaliação jurídica efetiva e aceite das promessas/obrigações; esta revisão editorial não constitui parecer ou certificação. |
| Documentos internos públicos | Decidir tratamento do PR #48 e questionário da branch do Rafael sem presumir apagamento do histórico; nenhum deles foi mesclado por esta revisão. |

## Fontes oficiais consultadas

- [LGPD](https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709compilado.htm): finalidade, direitos e confirmação/acesso; procedimentos devem refletir a operação real.
- [CDC](https://www.planalto.gov.br/ccivil_03/leis/l8078compilado.htm): oferta e direitos obrigatórios não devem ser excluídos por contrato.
- [ANPD, Resolução 2/2022](https://www.gov.br/anpd/pt-br/acesso-a-informacao/institucional/atos-normativos/regulamentacoes_anpd/resolucao-cd-anpd-no-2-de-27-de-janeiro-de-2022): pequeno porte exige avaliar requisitos e não afasta obrigações gerais.
- [ANPD, Resolução 19/2024](https://www.gov.br/anpd/pt-br/acesso-a-informacao/institucional/atos-normativos/regulamentacoes_anpd/resolucao-cd-anpd-no-19-de-23-de-agosto-de-2024): transferências internacionais precisam de mecanismo e documentação aplicáveis.
- [ANPD, ECA Digital](https://www.gov.br/anpd/pt-br/assuntos/eca-digital): obrigações e orientações de proteção/idade exigem enquadramento específico do produto.
- [Cloudflare](https://www.cloudflare.com/privacypolicy/): dados de rede/logs e papéis variam por operação; não presumir ausência de tratamento.
- [MET Norway](https://api.met.no/doc/TermsOfService): chamadas diretas expõem IP e coordenadas aos logs do serviço.
- [Google Play, reembolso](https://support.google.com/googleplay/answer/2479637?hl=pt-BR): suporte do desenvolvedor também é canal de resolução.

## Verificações realizadas

Gerador e `--check` aprovados; nove cópias e hashes conferidos. Teste Android `PaginasLegaisSincronizadasTest` aprovado, agora inclui `public_html`. Importador do site executado localmente com três páginas e preservação dos parágrafos. HTML sem scripts ou recursos remotos adicionados nesta exportação. Não houve publicação, alteração do site, novo AAB ou envio a terceiros.
