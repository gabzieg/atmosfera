> Registro historico, versionado em 11/10/2026 para consulta. Propostas, valores, fornecedores e pendencias abaixo refletem a epoca do documento; nao substituem o [checklist atual](CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md) nem as fontes canonicas de docs/legal. Nao importar estes rascunhos como documentos atuais do app/site.

# Consolidação do produto, dos dados e dos textos legais — 04/10/2026

**Etapa:** 1 — levantamento factual e decisões para o refinamento.
**Estado:** levantamento concluído; oferta Premium, responsável em pessoa física e canal oficial confirmados pelo usuário nesta conversa. Usuário informou que domínio e hospedagem da landing ainda não foram decididos. Rascunhos da etapa 2 preparados para revisão; essas definições e questões operacionais abaixo precisam ser fechadas antes da publicação.
**Atualização desta consolidação:** 05/10/2026.
**Escopo desta rodada:** organizar a base para revisar os textos e sincronizar aplicativo/site. Não altera o comportamento do app, não publica documentos e não modifica o front-end do Willian.

## 1. Referências e limites da verificação

- Aplicativo: código local no commit `0fa0f6b9140efb8ff3bbf473d65fd25ee602702d` (merge da documentação sobre MET e atualização em segundo plano).
- Landing page: [uWillianG/terra.app](https://github.com/uWillianG/terra.app), examinada no commit `d083c243b7c8e02a41564c3242a2ea7f53c8987c`. Não houve nova consulta remota nesta rodada; a referência é a revisão examinada na análise anterior.
- Documentos comerciais: [SPEC.md](SPEC.md) e [proposta do acervo remoto](PROPOSTA-PREMIUM-ACERVO-REMOTO-2026-09-28.md).
- Fontes editoriais: [PRIVACIDADE.md](../legal/PRIVACIDADE.md), [TERMOS.md](../legal/TERMOS.md) e [CONTATO.md](../legal/CONTATO.md).
- A conferência é estática. Não comprova o conteúdo do AAB instalado, a configuração do Play Console, a infraestrutura do bucket nem uma implantação pública da landing page.

## 2. Oferta de produto

### Decisão reconfirmada em 04/10/2026

O usuário escolheu: **“Efeitos e estilos; cenários vendidos separadamente, conforme a especificação de 29/09.”**

- `terra_premium`: compra única de **R$ 49,90**, pelos oito efeitos vivos e todos os estilos de efeito.
- Premium **não inclui a compra de cenários ou artes**. Os cinco cenários continuam oferecendo as amostras gratuitas previstas no catálogo.
- O preço de **R$ 1,99 por cenário** está registrado na decisão de 29/09; não foi reaberto nesta rodada.
- Compras de cenários e Premium representam direitos distintos. Agrupamentos comerciais em packs ainda dependem de definição.
- Manter a regra registrada de miniaturas locais, arquivos HD sob demanda e uso offline do conteúdo instalado. A existência desses requisitos não significa que a implementação esteja concluída.

### Diferenças que precisam ser corrigidas na implementação e nos textos

| Área | Estado observado | Ação posterior |
|---|---|---|
| Catálogo | `Catalogo.kt` tem cinco cenários e todos os `productId` estão nulos. | Associar os produtos avulsos e definir exatamente quais artes cada produto inclui. |
| Desbloqueio | `MainViewModel.isSceneUnlocked()` retorna verdadeiro para Premium; `isArtUnlocked()` usa esse resultado. | Separar o direito aos efeitos do direito à arte/cenário. |
| Oferta na tela | `PremiumScreen.kt` promete todas as artes dos cinco cenários. | Ajustar a descrição e comprovar que o benefício comprado é o benefício entregue. |
| Termos | A seção de Premium mantém a oferta antiga. | Refinar após fixar o contrato de cada produto. |
| Conteúdo remoto | `Acervo.BASE_PADRAO` está vazio; a jornada principal não integra o download do catálogo completo. | Não apresentar esse fluxo como funcionalidade já disponível nesta versão. |
| Catálogo futuro | A especificação trata 69 cenários como objetivo de lançamento. | Anunciar somente o lote validado no Android e efetivamente disponível para entrega. |

Antes de mudar a oferta, verificar no Play Console se houve **compras reais** sob a descrição antiga. Pedidos de teste não comprovam a existência de vendas reais. Se houver compradores anteriores, definir uma transição compatível com a oferta original; não remover benefícios silenciosamente.

## 3. Inventário dos fluxos de dados

**Identidade e contato confirmados pelo usuário:** Rafael Huppes, pessoa física, como responsável pelo Terra; `suporteterrabr@gmail.com` como canal oficial de suporte e privacidade. Essa confirmação permite preencher os dados editoriais; o enquadramento jurídico de controlador/fornecedor e eventuais operadores continua sujeito à análise dos fluxos e à revisão jurídica.

Os campos abaixo descrevem o comportamento observado e as lacunas. Não fixam, por si, bases legais, papéis contratuais ou prazos jurídicos de retenção.

| Fluxo | Dados/finalidade | Destino e armazenamento | Consequência para os textos |
|---|---|---|---|
| Obtenção da localização | Coordenadas aproximadas, via Google Play Services e permissão `ACCESS_COARSE_LOCATION`. Sem permissão, localização padrão de São Paulo, SP. | Coordenadas e indicação de local padrão podem ficar no cache local de clima. | Descrever localização aproximada; evitar tratar o recurso exclusivamente como leitura de GPS. |
| Consulta meteorológica | Coordenadas arredondadas para duas casas decimais; IP da conexão; identificação do app e contato no User-Agent. | API da MET Norway; resposta armazenada no aparelho. MET informa registros de acesso com IP/coordenadas. | Substituir Open-Meteo e suas informações por MET, seus registros e sua atribuição de licença. Não prometer anonimato absoluto. |
| Nome da cidade | `nomeDoLugar()` recebe coordenadas para obter cidade/UF pelo `Geocoder` do Android. | O serviço pode usar rede; o provedor depende da implementação do aparelho. Nome resolvido fica no cache local. | Não afirmar que coordenadas são enviadas exclusivamente à MET sem esclarecer também este fluxo. |
| Atualização periódica | `WeatherWorker` reutiliza coordenadas em cache, com intervalos e jitter. | Nova consulta à MET, mesmo sem uma nova leitura de localização. | Distinguir aquisição da localização e reenvio de coordenadas salvas. A rotina atual não revalida a permissão antes do reenvio. |
| Preferências | Cenário, arte, estilo, personalização, intervalo de clima e indicadores de direitos. | Armazenamento privado do aplicativo. | Diferenciar armazenamento local de possível cópia por backup/transferência do Android. |
| Backup e transferência | `allowBackup=true`; regras excluem `atmosfera_plano.xml`, mas não todo o cache/preferências. | Backup/transferência administrados pelo Android, conforme configurações e comportamento do sistema. | Não afirmar que todas as preferências e coordenadas permanecem exclusivamente no aparelho. Não afirmar que o backup restaura a compra Premium. |
| Compras | Estado da compra, produtos, token, resposta assinada e eventual ID do pedido; confirmação e restauração via Play Billing. | Google Play; indicadores de direito guardados localmente. Não foi encontrado backend próprio de validação de compra neste fluxo. | Separar dados de cartão, aos quais o app não tem acesso, dos dados de transação tratados por Billing/Console. |
| Download de conteúdo | Requisição de arquivos e IP da conexão. Coordenadas não são necessárias para baixar arte. | Cloudflare R2 previsto; URL padrão vazia no código. Arquivos baixados são instalados no armazenamento privado. | R2 não equivale a “nenhum tratamento de dado pessoal”. Confirmar configuração e registros da infraestrutura antes de descrever retenção. |
| Atendimento voluntário | E-mail do remetente, relato, cenário/arte/estilo e anexos enviados por escolha do usuário. | Cliente de e-mail do usuário e provedor da caixa de suporte. | Explicar recebimento e retenção do atendimento sem confundir com coleta automática pelo app. |
| Visita à landing | Requisições HTTP, IP e possíveis registros de acesso da hospedagem. Não foram encontrados analytics nos arquivos examinados. | Usuário confirmou que hospedagem e domínio ainda não foram decididos; existe apenas o escopo da landing. | Incluir o escopo do site e seus fornecedores depois de confirmar a implantação. Não inferir ausência de logs por ausência de analytics. |

### Pontos técnicos a resolver antes das promessas definitivas

1. **Revogação da permissão:** conferir e corrigir o reenvio pelo worker de coordenadas anteriores após a revogação. Um teste no aparelho deve verificar também o período sem reabrir o app/wallpaper.
2. **Geocoder:** confirmar o fluxo do provedor do Android e refletir sua possibilidade de comunicação em rede na política.
3. **Termos do fornecedor de clima:** avaliar o comportamento em segundo plano em relação às orientações de uso móvel da MET, preservando a decisão de produto apenas com uma solução compatível.
4. **Logs:** `LocationHelper` registra coordenadas em `Log.d` e Billing registra IDs em alguns caminhos. Verificar o que permanece na versão de release e nos relatos de diagnóstico. Não há comprovação de envio desses logs a um servidor próprio.
5. **Canal de suporte:** documentos HTML e landing usam `suporteterrabr@gmail.com`, mas `Suporte.EMAIL`, no diálogo de reportar problemas, ainda usa `[canal antigo omitido; corrigido na etapa 2]`. O refinamento deve alinhar o canal aprovado com o destino real.

## 4. Documentos e sincronização existentes

| Local | Papel atual | Estado verificado |
|---|---|---|
| `docs/legal/*.md` | Fontes declaradas como canônicas. | Há placeholders e diferenças editoriais em relação aos HTMLs, inclusive na identificação do controlador. |
| `docs/<pagina>/index.html` | HTMLs usados para publicação e conferência local. | Privacidade 1.3 (04/10); termos 1.1 (03/10); contato atualizado em 03/10. |
| `public_html/<pagina>/index.html` | Cópias usadas na importação pelo projeto do Willian. | SHA-256 idêntico ao HTML correspondente em `docs/` e nos assets para as três páginas, nesta conferência. |
| `android-app/app/src/main/assets/legal/<pagina>/index.html` | Documentos offline incluídos no app. | Mesmas cópias HTML locais. Mudanças exigem uma nova compilação e atualização do aplicativo instalado. |
| `terra.app/public/documentos/<pagina>/index.html` | Documentos apresentados pela landing. | Na revisão examinada: privacidade 1.1 (29/09), termos 1.0; dados/fornecedor anteriores. |

O aplicativo abre `file:///android_asset/legal/...`. Não consulta automaticamente a versão do site. Links externos são bloqueados pela WebView atual.

**Não existe a task `copiarPaginasLegais` no `app/build.gradle` examinado**, embora um comentário em `LegalWebViewScreen.kt` ainda a mencione. O teste `PaginasLegaisSincronizadasTest` explica que a tentativa de cópia automática foi abandonada e compara as cópias físicas de `docs/` e assets. Atualmente ele não inclui `public_html` nem o Markdown canônico.

No projeto do Willian, `scripts/prepare-documents.mjs` já importa os três HTMLs de `public_html` de um checkout do Atmosfera. Ele adapta links e apresentação; o texto de privacidade passa por `render-privacy.mjs`. Isso permite compartilhar conteúdo preservando o layout. O importador ainda substitui datas pendentes por avisos de lançamento; a publicação definitiva precisará de metadados preenchidos e de uma revisão identificada.

## 5. Proposta de ligação entre aplicativo e landing

**Estado: desenho proposto; não implementado nesta etapa.**

```text
Textos canônicos revisados em docs/legal/
                 |
       revisão aprovada e identificada
                 |
        HTMLs de publicação em docs/
          /                  \
 assets locais do app      public_html/
          |                  |
      próximo AAB     importador de terra.app
                             |
                  layout mantido por Willian
```

### Regras propostas

1. Manter as três fontes editoriais em `docs/legal/`. Conciliar Markdown e HTML antes de liberar qualquer pacote de documentos.
2. Preparar um processo **explícito de geração/exportação**, fora de uma cópia implícita no build Android. O build deverá verificar a existência, integridade e correspondência dos assets; a experiência anterior não deve ser repetida sem corrigir a causa das falhas.
3. Identificar cada revisão aprovada com commit e metadados: versão de cada documento, data de revisão, vigência, escopo e versões do aplicativo às quais se aplica. Privacidade e termos podem ter números de versão diferentes.
4. Exportar os HTMLs e um manifesto de integridade dessa revisão. O site importa um commit/revisão aprovado, em vez de publicar automaticamente qualquer alteração em `main`.
5. Preservar o HTML/layout de apresentação do Willian. Comparar conteúdo normalizado, metadados e links; comparar o hash do HTML de entrada no manifesto. O HTML final do site poderá ter hash diferente por causa da apresentação.
6. Ampliar a verificação local para `docs/`, `public_html/` e assets; acrescentar verificação da fidelidade editorial à fonte canônica. Não confundir sincronização com validação jurídica.
7. Manter histórico acessível das revisões e das versões aplicáveis. A versão mais recente deve informar seu escopo, sem substituir silenciosamente o contrato de instalações/compras anteriores.
8. Depois de definido o domínio, usar a URL pública estável da política no Play Console e nas referências públicas. O app continua com a cópia offline correspondente à sua versão; um eventual botão “Ver versão publicada” exige implementação específica de abertura no navegador.

O site pode receber atualização independente do aplicativo, mas uma atualização no site **não atualiza os assets de um app já instalado**. Alterações de tratamento/funcionalidade precisam ser acompanhadas de atualização e comunicação adequadas, não apenas de uma importação de textos.

## 6. Pendências para o refinamento jurídico/editorial

### Confirmações de operação

- [x] Confirmar os dados editoriais do responsável e canal oficial: usuário confirmou Rafael Huppes, pessoa física, e `suporteterrabr@gmail.com`. Validar o enquadramento jurídico dos papéis na etapa 3.
- [ ] Definir domínio público e serviço de hospedagem da landing. Usuário confirmou que ainda não foram decididos; essa informação não será inventada nos textos.
- [ ] Confirmar quem administra o bucket e a caixa de suporte, pessoas com acesso e registros de cada fornecedor.
- [ ] Conferir países de distribuição, público-alvo e declarações do Play Console. O registro local é 13+, ainda sem conferência do painel nesta rodada.
- [ ] Confirmar primeira disponibilização aos testadores e vigência correta; não inventar uma data de lançamento.
- [ ] Verificar compras reais anteriores e definir eventual transição de benefícios.
- [ ] Definir unidade comercial do cenário: quais artes/variantes são incluídas por produto e regra para futuras inclusões. Não prometer “todas as artes futuras” sem uma decisão.
- [ ] Definir critérios de retenção do atendimento, configuração de logs e continuidade de downloads de conteúdo comprado.

### Questões para validação jurídica

- Fundamentos e mecanismos reais de transferência internacional; a referência atual ao art. 33, II, alínea “f” não existe, e o inciso VIII não corresponde à execução do contrato.
- Papéis e bases legais por fluxo, incluindo atendimento, geocodificação, infraestrutura de downloads e hospedagem.
- Enquadramento para dispensa de encarregado e canal de atendimento; evitar afirmar dispensa antes de validar os requisitos.
- Reembolso, arrependimento, transição da oferta antiga e indisponibilidade/descontinuação de conteúdo pago.
- Público adolescente, autorização de responsáveis e alinhamento com países de distribuição, considerando também o [ECA Digital (Lei nº 15.211/2025)](https://www.planalto.gov.br/ccivil_03/_ato2023-2026/2025/lei/l15211.htm) e as [orientações da ANPD](https://www.gov.br/anpd/pt-br/assuntos/eca-digital/). “13+” sozinho não encerra a análise.
- Propriedade intelectual/licenças das artes, bibliotecas, fontes e dados meteorológicos; créditos aplicáveis.
- Retenção, exclusão, exercício de direitos e resposta a incidentes; eliminar promessas absolutas incompatíveis com backups e logs externos.
- Limitações de responsabilidade e foro, preservando direitos obrigatórios dos consumidores.

Fontes oficiais já consultadas: [LGPD](https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709compilado.htm), [transferências internacionais — ANPD](https://www.gov.br/anpd/pt-br/acesso-a-informacao/institucional/atos-normativos/regulamentacoes_anpd/resolucao-cd-anpd-no-19-de-23-de-agosto-de-2024), [agentes de pequeno porte — ANPD](https://www.gov.br/anpd/pt-br/acesso-a-informacao/institucional/atos-normativos/regulamentacoes_anpd/resolucao-cd-anpd-no-2-de-27-de-janeiro-de-2022), [reembolsos no Play Console](https://support.google.com/googleplay/android-developer/answer/2741495?hl=pt-BR), [MET — termos](https://api.met.no/doc/TermsOfService), [MET — privacidade](https://www.met.no/en/About-us/privacy), [MET — licença](https://api.met.no/doc/License) e [Android — Geocoder](https://developer.android.com/reference/android/location/Geocoder).

## 7. Sequência e critérios de conclusão

| Etapa | Entrega | Critério de conclusão |
|---|---|---|
| **1. Consolidar fatos e oferta — esta rodada** | Este inventário e registro da oferta reconfirmada. | Implementado, planejado e pendente separados; decisões em aberto visíveis. As confirmações de operação continuam pendentes. |
| **2. Refinar os textos de origem — rascunhos preparados** | [Pacote de revisão de 05/10](../legal/revisao-2026-10-05/README.md), com privacidade, termos e contato; fontes/páginas atuais preservadas. | Resolver as pendências editoriais, operacionais e técnicas antes de promover os rascunhos à fonte canônica; nenhuma promessa depende de suposição não registrada. |
| **3. Validar juridicamente** | Revisão por profissional responsável e resolução dos pontos legais. | Revisão efetivamente recebida e incorporada. A assistência técnica/editorial não será registrada como aprovação jurídica. |
| **4. Sincronizar e publicar** | Exportação conferida, integração com importador do Willian, assets do próximo AAB e referências da Play. | Mesma revisão aplicável em cada canal, URL pública validada e histórico preservado. Preparação local não será registrada como publicação realizada. |

Correções técnicas necessárias para cumprir os textos (direitos de compra, revogação, canal de suporte e entrega remota) terão de ser verificadas antes de declarar a versão pronta. Esta consolidação não as marca como concluídas.
