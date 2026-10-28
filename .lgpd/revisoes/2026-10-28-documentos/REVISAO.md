# Revisão independente dos documentos legais — rodada 1

Data: 28/10/2026. Escopo: Privacidade 1.14, Termos 1.8 e Contato 1.6, na árvore de trabalho de `0f7adc2a4a5bb29d91a92048c53180adb8df7356`. Candidato identificado no código: `versionCode 7`, `versionName 1.0.5`.

Resultado: leitura documental concluída, com decisões e evidências pendentes antes de fechar a redação. Esta entrega é uma análise preparatória; não é aprovação jurídica, declaração de conformidade, comprovação de distribuição ou autorização de publicação.

## Base e método

Leitura integral das três fontes canônicas em `docs/legal/`, de `README.md`, `revisao.json` e `manifesto-documentos.json`. Conferência pontual no código Android e consulta a fontes oficiais atuais. Os hashes e ferramentas estão em `baseline.json` nesta pasta.

Não foram consultados pareceres anteriores, arquivos de `.lgpd/` preexistentes, parecer privado do workflow, `docs/legal/revisao-2026-10-05/`, `docs/legal/historico/` nem os documentos internos de revisão referenciados pelo README. Os históricos editoriais que integram os próprios três textos foram lidos como conteúdo dos documentos, sem adotar suas conclusões. O relato do PR #60 não foi usado como evidência.

Ferramentas: pacote [goul4rt/lgpd-skills](https://github.com/goul4rt/lgpd-skills/tree/d85d79abeeb37cb99fc0785e735a9ca790698a77), fixado nesse commit e instalado localmente em pasta privada de trabalho. Aplicada `lgpd-privacy-policy`; consultadas as instruções de encarregado, transferências e menores e a referência normativa do pacote. A revisão é documental, sem iniciar o pipeline de auditoria integral do maestro. O ZIP `lgpd-escritorio.zip` foi lido integralmente e extraído na mesma pasta: seus critérios de inventário, retenção e evidência são úteis, mas não se transportam bases processuais, prazos sugeridos ou papéis de um escritório para um aplicativo.

As fontes Markdown e seus HTMLs não foram alterados. Nenhum dado privado de Console ou conteúdo de e-mail foi acessado.

## Cobertura de transparência

Matriz pelos sete elementos do art. 9º da [LGPD, texto atualizado da Câmara](https://www2.camara.leg.br/legin/fed/lei/2018/lei-13709-14-agosto-2018-787077-normaatualizada-pl.html). “Presente” indica conteúdo textual, sem certificar a execução operacional.

| Elemento | Local na Política | Leitura |
|---|---|---|
| Finalidades | §§ 2–7 | Presentes por operação |
| Forma e duração | §§ 2–6 e 9 | Parcial: faltam listas de teste e registros administrativos |
| Identificação do controlador | § 1 | Nome e condição de pessoa física presentes |
| Contato | §§ 1, 6 e 10 | Canal único presente nos três documentos |
| Compartilhamento e finalidade | §§ 2–4 e 8 | Fluxos descritos; confirmação administrativa pendente |
| Responsabilidades dos agentes | § 8 | Parcial: papéis ainda em aberto |
| Direitos, com referência ao art. 18 | § 10 | Presentes; alcance inclui registros fora do suporte |

## Achados e decisões necessárias

### R01 — Identificação do candidato e aprovação são coisas distintas

Evidência: cabeçalhos dos três textos e `revisao.json` ainda deixam a versão em aberto. `android-app/app/build.gradle:28` identifica `7 / 1.0.5`. O JSON mantém `aprovado_para_publicacao=false`, aprovação e vigência nulas.

Ajuste editorial possível: identificar o candidato como **1.0.5 (código 7), em revisão** e conciliar o campo de escopo do JSON. Vigência e aprovação exigem decisão real do responsável; não se deduzem da versão do build. Preservar a distinção entre data de revisão e início de vigência. Prioridade: antes da aprovação editorial.

### R02 — As bases legais continuam propostas

Evidência: `PRIVACIDADE.md:82` deixa expressamente pendente a adoção das bases da tabela. A redação é honesta sobre o estado de rascunho, mas não encerra a decisão por tratamento, especialmente localização opcional e participação no teste.

Solicitar ao responsável a decisão fundamentada por operação, com finalidade, necessidade e alternativa sem localização. Não transformar permissão Android em consentimento jurídico automático nem trocar toda a tabela por consentimento. Referência: art. 7º da [LGPD](https://www2.camara.leg.br/legin/fed/lei/2018/lei-13709-14-agosto-2018-787077-normaatualizada-pl.html). Prioridade: antes de fechar a política.

### R03 — Fornecedores e tratamento internacional exigem evidência da operação

Evidência: § 8 da Política informa que contratos, papéis, destinos e retenção dependem de confirmação. Google reúne Billing, localização, backup, Gmail e Sites, que precisam de exame por serviço. Política pública ou DPA disponível não comprova aplicação à conta usada.

Produzir matriz por serviço: dados, finalidade, papel de cada agente, acesso administrativo, destino, retenção e instrumento aplicável. Distinguir coleta internacional de transferência, conforme arts. 4º–9º da [Resolução 19/2024](https://www.gov.br/anpd/pt-br/acesso-a-informacao/institucional/atos-normativos/regulamentacoes_anpd/resolucao-cd-anpd-no-19-de-23-de-agosto-de-2024). Não exigir cláusulas-padrão indistintamente para qualquer conexão externa.

A referência à adequação da Noruega tem suporte: o art. 1º, parágrafo único, da Resolução 32/2026 inclui o país, conforme [tradução oficial publicada pela ANPD](https://www.gov.br/anpd/pt-br/centrais-de-conteudo/outros-documentos-e-publicacoes-institucionais/resolucao-no-32-decisao-de-adequacao-uniao-europeia-em-lingua-espanhola.pdf). Os [termos da MET](https://api.met.no/doc/TermsOfService) confirmam logs de IP e coordenadas e armazenamento em Oslo. Isso não resolve os demais fornecedores. Prioridade: antes de fechar a política.

### R04 — Retenção não cobre todos os dados que o próprio texto reconhece

Evidência: a participação em teste aparece na tabela de operações (`PRIVACIDADE.md:92`) e listas de participantes/registros administrativos aparecem no alcance dos direitos (`:135`); a tabela de retenção (§ 9) não lhes atribui prazo, gatilho ou destino.

Levantar quais registros existem sob controle do Terra e onde ficam. Acrescentar à tabela categorias reais, prazo ou critério determinável, marco inicial, descarte e exceções. Não estender automaticamente os 60 dias do suporte à lista do teste. Referência: art. 9º, II, da [LGPD](https://www2.camara.leg.br/legin/fed/lei/2018/lei-13709-14-agosto-2018-787077-normaatualizada-pl.html). Prioridade: antes de fechar a política.

### R05 — A promessa de exclusão do suporte precisa de rotina verificável

Evidência: Política § 6 e Contato § 3 prometem retenção de 60 dias após encerramento e eliminação de dados excessivos assim que identificados. Não foi acessada a conta para verificar execução. A [documentação do Gmail](https://support.google.com/mail/answer/7401?hl=pt-br) informa que mensagens excluídas podem permanecer na lixeira por até 30 dias.

Confirmar como se registra o encerramento, quem revisa os prazos, como se faz exclusão definitiva e como se tratam anexos/cópias e exceções. Se houver ciclo adicional de lixeira, descrevê-lo ou ajustar a operação à promessa. É uma lacuna de comprovação, não prova de descumprimento da conta. Referência: transparência sobre duração, art. 9º, II, da [LGPD](https://www2.camara.leg.br/legin/fed/lei/2018/lei-13709-14-agosto-2018-787077-normaatualizada-pl.html). Prioridade: antes de confirmar essa promessa.

### R06 — Resolver designação de encarregado ou enquadramento em dispensa

Evidência: Política § 1 e resumo de Contato explicitam que a avaliação está pendente. Ter nome e e-mail de contato não comprova ato de indicação.

O responsável deve registrar o enquadramento aplicável. A [Resolução 2/2022, art. 11](https://www.gov.br/anpd/pt-br/acesso-a-informacao/institucional/atos-normativos/regulamentacoes_anpd/resolucao-cd-anpd-no-2-de-27-de-janeiro-de-2022) prevê dispensa para os agentes abrangidos e exige canal de comunicação. Se houver indicação, observar o ato formal do art. 3º da [Resolução 18/2024](https://www.gov.br/anpd/pt-br/acesso-a-informacao/institucional/atos-normativos/regulamentacoes_anpd/encarregado-completo_ocultado.pdf). Não presumir dispensa só por ser pessoa física. Prioridade: decisão administrativa antes de fechar a redação correspondente.

### R07 — Identificação para comércio eletrônico precisa de conferência específica

Evidência: Termos § 1 identifica nome, pessoa física e e-mail, sem endereço físico ou número de inscrição. O [Decreto 7.962/2013, art. 2º, I–II](https://www2.camara.leg.br/legin/fed/decret/2013/decreto-7962-15-marco-2013-775557-publicacaooriginal-139266-pe.html) trata dessas informações nos meios utilizados para oferta ou contratação.

Conferir com o responsável jurídico como cumprir o dever no conjunto app/loja/página comercial, inclusive o que a Google Play efetivamente exibe. A ausência nos Termos é confirmada; a ausência em toda a oferta não foi verificada. Não inserir CPF ou endereço residencial em repositório, relatório ou política por inferência. Prioridade: antes de vendas com cobrança real.

### R08 — Continuidade comercial ainda não foi definida

Evidência: Termos § 8 deixa em estudo manutenção, retirada, restauração e encerramento; o próprio texto condiciona sua definição ao início de cobranças reais.

Fechar uma regra operacional viável de disponibilidade e tratamento de retirada/falha de restauração, com atendimento e remédios ao consumidor. Não prometer hospedagem vitalícia ou um prazo inventado. Referência: clareza de oferta e condições, art. 2º do [Decreto 7.962/2013](https://www2.camara.leg.br/legin/fed/decret/2013/decreto-7962-15-marco-2013-775557-publicacaooriginal-139266-pe.html). Prioridade: antes de vendas com cobrança real; não impede por si só uma leitura documental do teste.

### R09 — Separar teste adulto de futura distribuição para adolescentes

Evidência: Termos § 2 e Política § 11 distinguem teste planejado para adultos no Brasil e público futuro 13+. Ambos reconhecem ausência de aferição de idade e de controle parental próprio.

Manter essa distinção. Confirmar recrutamento/distribuição do teste com evidência administrativa e avaliar acesso provável por menores antes da ampliação. A [Lei 15.211/2025](https://www2.camara.leg.br/legin/fed/lei/2025/lei-15211-17-setembro-2025-797997-normaatualizada-pl.pdf), arts. 1º e 3º, exige análise do serviço e do acesso provável; escrever “18+” não a substitui. O art. 9º trata especificamente de oferta/acesso impróprio, inadequado ou proibido para menores: não fundamenta sozinho exigir documento ou biometria a cada acesso a este wallpaper. Prioridade: validar escopo do teste e reavaliar antes de ampliar o público.

### R10 — A versão pública segue divergente

Nova consulta HTTP sem credenciais em 28/10/2026 retornou 200 nas três páginas e confirmou [Privacidade 1.9](https://sites.google.com/view/terra-live-wallpaper/privacidade), [Termos 1.5](https://sites.google.com/view/terra-live-wallpaper/termos) e [Contato 1.2](https://sites.google.com/view/terra-live-wallpaper/contato), todas com indicação de revisão. O extrato técnico foi mantido apenas no workspace privado. A ferramenta web não abriu essas URLs; a conferência foi feita com `urllib.request` e extração do texto HTML. Nenhum HTML foi editado.

Após aprovação real, publicar a redação correspondente no Sites e conferir versão/conteúdo nas três URLs. A exportação local não atualiza o Sites. Prioridade: antes de tratar a nova redação como informação pública vigente. Referência: transparência, art. 9º da [LGPD](https://www2.camara.leg.br/legin/fed/lei/2018/lei-13709-14-agosto-2018-787077-normaatualizada-pl.html).

## Conferências técnicas desta rodada

| Afirmação documental | Evidência atual | Limite |
|---|---|---|
| Localização aproximada opcional | `AndroidManifest.xml:16`; `weather/LocationHelper.kt:39` | Leitura de código, sem teste em aparelho |
| Duas casas decimais e cache HTTP de 512 KiB | `weather/WeatherRepository.kt:295`, `:407` | Capacidade nominal, não prazo de exclusão |
| Worker não consulta clima | `weather/WeatherWorker.kt:15` | Serviço de wallpaper tem regras próprias |
| Condição de visibilidade/energia/bloqueio | `service/AtmosferaWallpaperService.kt:71`, rotina de clima a partir de `:214` | Não certifica todas as transições em runtime |
| Relato abre app externo, com campos descritos | `ui/components/ReportarProblemaDialog.kt:171` | Não envia automaticamente pela rotina lida |
| Prévia: cinco minutos/24 MiB, preservando uso | `engine/CachePrevias.kt:9` | Exclusão depende de chamada à limpeza |
| Exclusões de backup e transferência | `res/xml/backup_rules.xml`; `res/xml/data_extraction_rules.xml` | Não certifica comportamento de fabricantes |

Caminhos Java acima são relativos a `android-app/app/src/main/java/com/terra/wallpaper/`; XMLs a `android-app/app/src/main/`. Sem auditoria integral de Billing, do acervo, de dependências transitivas, do AAB assinado ou do Console. Não foi validado preço ativo, checkout de teste, classificação indicativa, envio ou início de distribuição.

## Cuidados com as ferramentas — origem: esta rodada, 28/10/2026

As skills são roteiros auxiliares, não fonte normativa. Dois pontos foram conferidos para evitar recomendações erradas nesta revisão:

- `lgpd-international-transfer` afirma inexistir lista oficial de adequação; essa afirmação está superada pela Resolução 32/2026. Conferir sempre a norma e seu alcance antes de exigir mecanismo contratual.
- `lgpd-eca-digital-minors` generaliza o art. 9º para aferição a cada acesso. Conferir o caput e a aplicabilidade ao produto antes de recomendar coleta de documento/biometria. O ZIP de escritório também não autoriza presumir que todo fornecedor seja operador ou que sempre falte encarregado.

Estas ressalvas documentam limitações do commit fixado, sem alterar as ferramentas de terceiros. Devem ser reavaliadas quando o pacote mudar. Não foram usados thresholds automáticos do pacote para declarar alto risco ou impor RIPD.

## Validação e próxima ação

`python3 scripts/sincronizar_documentos_legais.py --check`: passou, documentos e manifesto conferidos. A geração canônica é feita por esse Python; `docs/build-legal-pages.sh` apenas copia as três pastas de páginas para `_site`.

Próxima ação: obter as decisões/evidências de R02–R06 e confirmar o escopo de R09; então redigir uma proposta coesa nos três Markdown, identificar o candidato e regenerar as cópias. R07–R08 precisam estar resolvidos antes da oferta comercial; R10 exige publicação e conferência depois da aprovação. Não há data de vigência decidida por esta rodada.

## Continuação — identificação do candidato, 28/10/2026

O registro da rodada 1 e seu `baseline.json` acima permanecem como fotografia da entrada, antes das alterações editoriais desta continuação.

R01: identificação editorial concluída. As fontes agora são Privacidade 1.15, Termos 1.9 e Contato 1.7, com candidato 1.0.5 (código de versão 7) nos cabeçalhos. Atualizados `README.md`, data da revisão e identificação do app em `revisao.json`; regenerados manifesto e nove HTMLs pelo gerador canônico. O corpo e o histórico anterior dos três documentos foram preservados, com acréscimo de uma entrada editorial por documento.

Mantidos `status=rascunho_para_revisao`, `aprovado_para_publicacao=false`, `aprovacao_registro=null` e `vigencia=null`. Nenhuma confirmação de Console, aprovação jurídica, publicação ou distribuição foi acrescentada.

Validação: geração local e `python3 scripts/sincronizar_documentos_legais.py --check` passaram; `git diff --check` passou. Comparação com `HEAD` confirmou que, após o cabeçalho, a única mudança em cada Markdown foi a nova entrada de histórico. O diff das fontes e metadados foi inspecionado; as cópias geradas foram conferidas pelo gerador.

Próximo ponto de decisão: R04 e R05. Foram solicitadas ao usuário informações sobre locais/finalidades/descarte das listas de teste e sobre responsável/rotina de exclusão definitiva do suporte no Gmail. As respostas ainda não foram recebidas nesta continuação. Não foi definido prazo novo nem presumida execução de descarte. R02–R10 seguem pendentes nos limites descritos na rodada 1.

## Continuação — informações operacionais do usuário, 28/10/2026

Fonte: respostas diretas do usuário nesta sessão. Informou que as listas de participantes ficam somente no Google Play Console, com inclusão direta dos testadores por lá. Confirmou também que existe responsável e rotina para registrar o encerramento e excluir definitivamente mensagens e anexos do suporte após 60 dias, inclusive da lixeira e de cópias locais. Não foram fornecidos nomes de participantes, endereços de e-mail ou conteúdo de atendimentos.

R04: local de manutenção confirmado por declaração do usuário. A Política agora identifica o Console como local exclusivo da lista e inclui essa operação na descrição dos serviços Google. Ainda falta o prazo ou critério de retirada dos participantes; foi perguntado se ocorre no encerramento, após prazo ou se a lista é mantida para novas rodadas. Nenhuma alternativa foi presumida, e o achado de retenção continua aberto.

R05: pendência de informação operacional atendida pela declaração do usuário. Mantido o prazo de 60 dias já existente e explicitado que a exclusão alcança a lixeira e eventuais cópias locais sob controle do responsável pelo suporte. Não se afirmou auditoria da conta, execução de descarte de um atendimento específico ou exclusão de registros internos do provedor. A confirmação permite a redação documental; esta rodada não exigiu acesso a mensagens reais como condição para aceitá-la.

Fontes nesta continuação: Privacidade 1.16, Termos 1.9 e Contato 1.8. Preservadas as entradas editoriais anteriores; os novos fatos foram acrescentados. Mantidos candidato 1.0.5 (código 7), estado de rascunho, aprovação falsa e vigência nula. Nenhuma publicação realizada.

## Continuação — retenção da lista de teste definida, 28/10/2026

Fonte: resposta direta do usuário nesta sessão, que definiu a remoção dos participantes ao encerrar o teste fechado.

R04: critério de retenção da lista de participantes definido. A Política 1.17 passa a informar, na descrição da operação e na tabela de retenção, que a lista fica exclusivamente no Google Play Console durante o teste e que o responsável pelo Terra remove seus participantes ao encerrá-lo. A pendência sobre essa lista descrita em 1.16 fica superada, preservado o texto histórico. Não se estendeu o prazo do suporte à lista, não se presumiu reutilização em novas rodadas e não se afirmou exclusão de registros próprios do Google ou de compras. Eventuais outros registros administrativos continuam sujeitos ao levantamento indicado no achado original, sem prazo inventado para categorias não confirmadas.

Atualizados README, manifesto e as três cópias HTML da Política pelo gerador canônico. Termos 1.9 e Contato 1.8 permanecem com o mesmo conteúdo. O candidato segue 1.0.5 (código 7), sem aprovação ou vigência definida e sem publicação.

## R06 — verificação da dispensa de encarregado, 28/10/2026

Escopo desta avaliação: operação atual do Terra, oferecido por Rafael Huppes como pessoa física, e candidato 1.0.5 (código 7). Aplicada a skill `lgpd-dpo-encarregado` com consulta direta à norma vigente. Não se adotou a recomendação genérica da skill de indicar encarregado mesmo quando dispensado como requisito obrigatório do projeto.

Base: arts. 2º–4º, 11 e 16 da [Resolução CD/ANPD 2/2022, texto atualizado consultado em 28/10/2026](https://www.gov.br/anpd/pt-br/acesso-a-informacao/institucional/atos-normativos/regulamentacoes_anpd/resolucao-cd-anpd-no-2-de-27-de-janeiro-de-2022). Pessoas naturais podem se enquadrar. As exclusões abrangem receita acima do limite, grupo econômico acima do limite e tratamento de alto risco. Este último exige cumulatividade de critério geral e específico. A dispensa conserva o dever de canal de atendimento e pode ser afastada por determinação da ANPD. Limite geral de receita considerado: R$ 4,8 milhões anuais, conforme art. 3º, II, da [LC 123/2006, cópia oficial](https://www.gov.br/empresas-e-negocios/pt-br/forum-permanente/legislacao/lei/lcp-123-de-2006.pdf/@@display-file/file). Não se presumiu enquadramento especial como startup.

| Verificação | Evidência nesta sessão | Avaliação |
|---|---|---|
| Identidade do agente | `docs/legal/TERMOS.md`, § 1; `revisao.json`: Rafael como pessoa física | Compatível com a categoria de agente de pequeno porte |
| Grupo econômico | Usuário confirmou que não integra | Exclusão por grupo não identificada, com base na declaração |
| Receita anual | Pergunta inicial incluiu receita e grupo; usuário respondeu apenas que não integra grupo | Falta confirmação separada de receita até o limite; não foi interpretada como resposta financeira |
| Alcance atual | Usuário confirmou acesso somente a sete adultos do teste fechado | Não indica larga escala no cenário informado; não foi reutilizada a antiga contagem do JSON como prova atual |
| Determinação específica | Usuário confirmou não ter recebido determinação da ANPD | Não identificada essa restrição específica |
| Canal de atendimento | E-mail publicado nos três documentos; usuário confirmou rotina de suporte | Canal identificado; não foi enviado e-mail de teste |

Conferência técnica: `AndroidManifest.xml:16` declara apenas localização aproximada; `weather/LocationHelper.kt:39` e a alternativa de São Paulo tornam seu fornecimento opcional; `weather/WeatherRepository.kt:295,314–315` arredonda coordenadas antes da chamada à MET. IP e posição continuam sendo dados pessoais no fluxo; arredondamento não foi tratado como anonimização. `weather/WeatherCache.kt` guarda o último clima, sem histórico de trajetória nessa rotina. As dependências declaradas em `android-app/gradle/libs.versions.toml` e `app/build.gradle` não apresentam SDK de anúncios ou analytics. Isso é leitura estática, sem auditoria de todas as dependências transitivas ou da infraestrutura dos fornecedores.

Também foi considerado o Billing, que concede/reconcilia direitos e pode recusar comprovantes inválidos (`billing/BillingManager.kt:106–146,205–245`). Logo, não se usou uma alegação genérica de ausência de qualquer automação como prova de baixo risco. A rotina examinada valida compras, sem scoring ou perfilamento identificado nela. Falhas de acesso continuam exigindo atendimento; não foram encontrados elementos suficientes para caracterizar impacto significativo e critério específico cumulativos no teste restrito informado. Essa avaliação não se transfere automaticamente a vendas reais ou distribuição ampla.

Resultado técnico preliminar: não identificado tratamento de alto risco no escopo observado e informado. A faixa adulta não exclui, por si só, presença de idosos; futura distribuição 13+ tampouco significa alto risco automaticamente. Devem ser considerados todos os critérios em conjunto. A finalidade recreativa e a alternativa sem localização favorecem a avaliação, sem eliminar deveres de proteção e segurança.

Conclusão do enquadramento: favorável à dispensa para o cenário atual, condicionada à confirmação do requisito financeiro que ainda não foi respondido. Perguntado apenas se a receita fica até R$ 4,8 milhões anuais, sem solicitar valor exato, documentos financeiros ou dados de testadores. Política e Contato continuam sem declarar dispensa concluída até essa resposta. A ampliação de público, escala, operações ou mudança de agente exige nova avaliação.

## R06 — conclusão após confirmação do requisito financeiro, 28/10/2026

O usuário confirmou separadamente que a receita bruta anual do responsável nessa atividade fica até R$ 4,8 milhões. Fica superada a pendência financeira do registro anterior, que foi preservado. Não foram solicitados nem registrados valores exatos ou documentos financeiros.

Com essa resposta, os fatos administrativos considerados são: responsável pessoa física conforme as fontes do projeto; receita dentro do limite; ausência de grupo econômico; acesso restrito a sete adultos no teste fechado; ausência de determinação específica da ANPD. A avaliação técnica de risco e seus limites estão no registro imediatamente anterior. A conclusão considera a operação descrita e as declarações do usuário, sem afirmar auditoria financeira, inspeção do Console ou decisão emitida pela ANPD.

R06 resolvido para o escopo atual da revisão: a avaliação indica enquadramento do responsável pelo Terra na dispensa de indicação de encarregado do art. 11 da Resolução CD/ANPD 2/2022. Não é necessário criar um ato de indicação para atender esse ponto no cenário avaliado. Mantidos Rafael como controlador e o canal de atendimento aos titulares. Essa conclusão não afasta as demais obrigações de proteção de dados nem se estende automaticamente à distribuição ampla, público adolescente ou operações futuras.

Atualizadas Privacidade 1.18 e Contato 1.9, suas seis cópias HTML, README e manifesto. As versões públicas explicam o enquadramento sem divulgar contagem de participantes ou informação financeira. Termos 1.9 permanecem sem alteração nesta etapa. Estado geral continua de rascunho, sem aprovação editorial final, vigência ou publicação. Reavaliar o enquadramento diante de mudança de agente, receita/grupo, público, escala, operações ou determinação da ANPD.

## Integração A5 (parte 1) — pacote 8, 28/10/2026

Por decisão do usuário em 28/10, esta redação (Privacidade 1.18, Termos 1.9 e Contato 1.9) será embarcada no próximo pacote, código de versão 8, em preparação para teste fechado no Brasil. O `versionName` ainda não foi definido. Ajustados somente os campos “Aplica-se a” e o escopo correspondente em `revisao.json`; os registros anteriores de candidato 7 e o baseline permanecem como histórico da revisão. As nove cópias HTML e o manifesto foram regenerados pelo processo canônico. Sem aprovação, vigência, geração de AAB ou republicação no Google Sites nesta integração.
