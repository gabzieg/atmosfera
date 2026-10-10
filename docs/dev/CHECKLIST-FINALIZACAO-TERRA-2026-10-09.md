# Checklist de finalização do Terra — 09/10/2026

**Regra de trabalho:** executar uma etapa por vez, registrar a evidência e só marcar conclusão quando seu critério de aceite estiver atendido. Código preparado, teste local, teste pela Play e publicação são estados distintos.

**Orientação do usuário em 10/10:** avaliações no aplicativo serão feitas pelo Rafael. Podemos avançar na implementação das próximas etapas, mantendo o aceite em aparelho das anteriores explicitamente pendente. Não houve envio de mensagem ao Rafael por esta execução.

**Base da conferência:** `main` em `e7129f6`; questionário do Rafael no commit `c1b95ec`, fora da `main`; decisões confirmadas nesta conversa. Este checklist não comprova o estado atual do Play Console nem substitui testes em aparelho.

**Atualização em 10/10/2026:** etapa 1 implementada na árvore local e verificada com 75 testes unitários, lint e build de debug aprovados. Aceite em aparelho/Play pendente; mudanças ainda não incorporadas à `main` nem publicadas.

**Etapa 2 em 10/10:** correções locais de suporte, backup, logs e remoção da geocodificação sem uso implementadas; **78 testes unitários, lint e build de debug aprovados**. Política técnica de backup atualizada para revisão 1.5, com três HTMLs sincronizados. Conferência em aparelho atribuída ao Rafael; não é validação jurídica integral da política.

**Verificação adicional da etapa 2:** `compileReleaseKotlin` aprovado. Isso confere a compilação do código de release; não representa geração de um novo AAB nem teste de execução em aparelho.

**Roteiro para encaminhamento:** [prompt para Rafael](PROMPT-RAFAEL-AVALIACAO-ETAPAS-1-2-E-CATALOGO-2026-10-10.md), com avaliação das duas etapas e inventário/decisões para a etapa 3. A entrega de código e checklist será compartilhada na branch `fix/premium-privacidade-etapas-1-2`; a avaliação em aparelho e a publicação seguem pendentes. Os rascunhos jurídicos de 05/10 e a consolidação de 04/10 continuam locais nesta entrega; não presumir que estejam disponíveis no GitHub.

## Decisões registradas — concluídas

- [x] Premium `terra_premium`: compra única de R$ 49,90, apenas efeitos e estilos. Não libera artes pagas.
- [x] Compra avulsa registrada pelo titular: R$ 1,99 por arte específica, não por todas as variantes do cenário.
- [x] Canal oficial de suporte e privacidade: `suporteterrabr@gmail.com`.
- [x] Responsável indicado: Rafael Huppes, pessoa física. Validar os papéis jurídicos na etapa 5.
- [x] Público registrado: 13+. Avaliar obrigações e conferir declaração na Play na etapa 5.
- [x] Retenção do atendimento registrada: até 60 dias após resolução, ressalvadas obrigação legal ou defesa em processo. Aplicar aos registros controlados pela equipe.
- [x] **Confirmado pelo usuário em 09/10: ocorreram somente compras de teste, sem cobrança real.** Não foi identificado comprador real a migrar nesta informação; o relato não é uma exportação auditada do Console.
- [x] Provedor de clima: MET Norway. Código passou a consultar somente com wallpaper na home, tela acordada e desbloqueada; worker legado não consulta. Falta aceite em aparelho.

## 1. Corrigir a separação entre Premium e artes — implementação local validada

**Objetivo:** comprar Premium nunca concede o direito a uma arte paga. As cinco amostras continuam utilizáveis.

- [x] Conferir os caminhos de seleção, aplicação e leitura pelo serviço, incluindo `isSceneUnlocked()`, `isArtUnlocked()` e `ArteFundo.atual()`.
- [x] Separar autorização de arte da autorização de efeito. UI e serviço usam `AcessoArte`; Premium não participa da autorização de arte. O antigo indicador de compra por cenário não libera variantes.
- [x] Impedir que uma arte paga previamente selecionada por compra de teste continue aplicada sem direito válido após a atualização: leitura retorna a amostra do mesmo cenário.
- [x] Manter seleção/uso das cinco amostras gratuitas independente do Premium. A regra de autorização é local e não exige rede; aceite offline em aparelho ainda faz parte da conferência abaixo.
- [x] Corrigir descrições em Premium, Loja e tutoriais e a cláusula Premium dos termos (revisão 1.3, três cópias HTML sincronizadas).
- [x] Corrigir o caminho de arte bloqueada: ações ficam indisponíveis, com explicação de que Premium não inclui a arte e a compra individual ainda não existe nesta versão. O acesso ao Premium pelos estilos continua disponível.
- [x] Especificar regressões: seis testes em `AcessoArteTest` cobrem amostras, variantes pagas, preferências antigas, troca entre cenários, arte inexistente e destrave de debug. Seleção/aplicação valida o direito no código; fluxo de compra/restauração e interface serão conferidos em aparelho.
- [x] Executar testes unitários, lint e build de debug apropriados à mudança: **75 testes, zero falhas/erros; lint sem erros (com warnings existentes); assembleDebug aprovado**. Testes/build repetidos depois da atualização dos termos.
- [ ] Registrar resultado em aparelho/versão Play antes do aceite final de publicação.

**Aceite:** UI e wallpaper aplicam o mesmo direito; Premium afeta somente efeitos/estilos; o usuário não é induzido a comprar Premium para receber arte.

**Limite:** esta etapa não conclui a compra individual nem a loja remota. Esses direitos e fluxos serão implementados nas etapas 3–4.

### Conferência manual do Rafael que falta para encerrar o aceite

1. Instalar o APK de debug da árvore atual em um aparelho/emulador. Deixar o destrave de conteúdo do painel de debug **desligado** para testar a autorização normal.
2. Sem Premium, conferir as cinco amostras e alternar entre elas; repetir sem rede.
3. Com Premium de teste restaurado pela Play em uma futura versão interna, conferir efeitos/estilos e verificar que as variantes pagas continuam bloqueadas. Repetir após revogar/restaurar o Premium.
4. Em Bruxa, Lavanda, Esfinge e Jardim, selecionar a variante pixel paga: conferir a prévia, o aviso e as ações indisponíveis; não deve abrir compra de Premium por essa arte.
5. Atualizar uma instalação de teste anterior que tinha uma dessas artes pagas selecionadas: conferir a volta à amostra correta, tanto no app quanto no wallpaper.
6. Conferir que uma arte gratuita selecionada em outro cenário pode ser aplicada corretamente e que a preferência pixel gratuita da Cabana não autoriza pixel pago nos demais.

Não havia aparelho/emulador conectado na execução de 10/10. Artefato para a conferência visual: `android-app/app/build/outputs/apk/debug/app-debug.apk`. Este APK não é o AAB destinado ao Play Console e seu destrave de debug não comprova comportamento de release.

## 2. Corrigir os quatro pontos técnicos de privacidade — implementação local validada

- [x] G05: “Reportar problema” passa a usar `suporteterrabr@gmail.com`, igual ao canal oficial. Conferência no cliente de e-mail fica no roteiro do Rafael.
- [x] G08: excluir `atmosfera_weather_cache.xml` e `atmosfera_met_freio.xml` do backup legado, cloud-backup e device-transfer. Manter exclusão de `atmosfera_plano.xml` e preferências comuns elegíveis. Três testes verificam as regras e sua ligação ao manifesto; não apaga automaticamente backups antigos.
- [x] G09: remover o log com latitude/longitude; falhas de localização usam mensagem genérica. Detalhes do estado meteorológico e das exceções ficam restritos a debug; release registra apenas diagnóstico genérico/tipo da falha, além dos códigos de bloqueio do provedor.
- [x] G16: remover `nomeDoLugar()`, imports e tabela de estados sem uso. Não foi introduzido outro serviço de localização/geocodificação. São Paulo/SP permanece o local padrão; a cidade real não passa a ser resolvida por este ajuste. Nomes legados no cache podem aparecer enquanto ainda aplicáveis.
- [x] Conferir estaticamente o fluxo atual: sem permissão, `LocationHelper` retorna São Paulo/SP; `ClimaNaHome` limita consultas; o worker legado não consulta. O teste dinâmico de revogação e transições de tela segue pendente.
- [x] Executar os checks afetados: **78 testes unitários, zero falhas/erros; lint sem erros e build de debug aprovados**. Alterações nas regras de backup agora invalidam o resultado dos testes no Gradle.
- [x] Atualizar a seção de backup da política e manter os HTMLs de `docs/`, `public_html/` e assets idênticos. Demais refinamentos jurídicos/editoriais continuam na etapa 5.
- [ ] Rafael: validar destinatário, restauração/backup, diagnóstico de release, indicação do local e localização negada/revogada em aparelho, conforme roteiro abaixo.

**Aceite:** canal, backup, diagnósticos e localização correspondem ao comportamento descrito nos textos refinados.

### Roteiro de avaliação do Rafael — etapas 1 e 2

Registrar versão/commit e resultado por item. O APK de debug em `android-app/app/build/outputs/apk/debug/app-debug.apk` foi atualizado com as duas etapas; a avaliação de release/Play exige artefato posterior correspondente.

1. Executar a conferência da etapa 1 acima, com o destrave de debug desligado.
2. Abrir “Algo errado neste cenário?” e preparar um relato: o cliente de e-mail deve mostrar o canal oficial; preparar o relato não deve enviá-lo automaticamente.
3. Testar instalação sem permissão de localização e voltar à home com o wallpaper ativo: o local padrão deve ser São Paulo/SP. Conceder e depois revogar a permissão; conferir consulta e indicação de local sem atribuir São Paulo à localização real.
4. Conferir retomada da home, tela apagada, bloqueio e app por cima: chamadas de clima devem ocorrer somente na condição elegível. Usar a versão de release correspondente para conferir ausência de latitude/longitude e detalhes de requisição nos logs de produção.
5. Testar backup/restauração ou transferência em versões representativas do Android: preferências comuns podem voltar; coordenadas/clima, freio da MET e flag Premium não devem voltar pelas novas regras. Restaurar a compra pela Play e deixar a nova consulta acontecer na home; testar também restauração sem rede.

Conferir separadamente backups produzidos por versões antigas: a nova configuração não comprova que essas cópias tenham sido apagadas. Referência técnica das regras: [documentação de backup do Android](https://developer.android.com/identity/data/autobackup).

## 3. Congelar o catálogo e os produtos do lançamento

- [ ] Conciliar a decisão registrada pelo Rafael de acervo completo com eventual lançamento em fases. Documentar uma regra definitiva antes de anunciar o catálogo.
- [ ] Conferir o inventário efetivo após as exclusões; não tratar “71 cenas/366 artes” como contagem já validada no aplicativo.
- [ ] Separar amostras gratuitas, artes pagas e itens excluídos; conferir o corte também nos arquivos distribuídos e manifestos.
- [ ] Criar tabela de cada arte ofertada: identificador, cenário/variante, miniatura, arquivos, produto Play, preço e resultado de teste.
- [ ] Definir a regra de novas variantes e novos efeitos/estilos futuros. Não prometer inclusões automáticas sem essa definição.
- [ ] Definir packs ou registrar expressamente que não serão ofertados nesta versão.
- [ ] Configurar preço regular e ofertas reais de R$ 0,99, sem confundir preço promocional com uma nova posse.
- [ ] Conferir países de oferta/distribuição e preços correspondentes.
- [ ] Preservar registros de origem e licenças das artes, fontes, sprites e dados meteorológicos, com revisão dos itens efetivamente distribuídos.

**Aceite:** existe uma lista única do que o usuário pode experimentar, comprar e receber. O inventário não contém produto sem entrega correspondente.

## 4. Implementar compra por arte e entrega remota

**Dependência:** etapa 3 concluída. Começar por um lote representativo; ampliar apenas após validar a jornada.

- [ ] Modelar posse por cenário + arte e associar os produtos pagos correspondentes; não usar um direito por cenário para liberar todas as variantes.
- [ ] Integrar carregamento de produtos/preços, compra, estados pendentes, confirmação, restauração e revogação.
- [ ] Implementar catálogo e miniaturas sem embutir todo o acervo HD.
- [ ] Configurar endpoint de produção do bucket e conferir controles de upload/acesso. Não incluir credenciais no aplicativo.
- [ ] Verificar origem/integridade de manifesto e pacotes, versionamento e compatibilidade com o motor.
- [ ] Implementar download solicitado pelo usuário: tamanho, progresso, cancelamento, repetição, instalação segura e falhas compreensíveis.
- [ ] Preservar o wallpaper ativo quando houver falta de rede/espaço, interrupção ou pacote inválido.
- [ ] Implementar limpeza de arquivos e comprovar uso offline do conteúdo instalado.
- [ ] Testar compra → download → aplicação → reinício → reinstalação/restauração → novo download.
- [ ] Validar visualmente cada arte anunciada no Android; ampliar catálogo por lotes conferidos.

**Aceite:** cada arte paga é entregue e utilizável; Premium e posse da arte permanecem independentes em todos os estados.

## 5. Refinar, revisar e sincronizar documentos

- [ ] Reconciliar `SPEC.md`, `TASKS.md`, plano de lançamento e pós-lançamento com as decisões atuais.
- [ ] Atualizar os [rascunhos de 05/10](../legal/revisao-2026-10-05/README.md) para a mudança de clima do PR #46, a compra por arte, o canal oficial e a retenção definida.
- [ ] Consolidar fonte canônica de privacidade, termos e contato; definir versões, aplicabilidade e vigência real, inclusive no teste.
- [ ] Validar papéis dos fornecedores, bases legais, transferências, proteção de menores, reembolso, licenças e continuidade de conteúdo adquirido. Registrar revisão jurídica efetiva; “respondido pelo titular” não equivale a parecer jurídico.
- [ ] Definir domínio e hospedagem com Willian; conferir registros, cookies e tratamento real da landing.
- [ ] Preparar exportação de uma revisão aprovada para os HTMLs do app e o importador do site, preservando a apresentação do Willian.
- [ ] Conferir texto, metadados, links e histórico; ampliar a verificação para `docs/`, `public_html/` e assets. Atualizar o site não altera apps já instalados.
- [ ] Publicar e verificar as três páginas por HTTPS, sem login e sem campos pendentes.
- [ ] Revisar o que pode ser público nos documentos e no PR #48, retirando dados pessoais internos desnecessários. Não confundir remoção da branch com apagamento de histórico/cópias.

**Aceite:** comportamento, oferta, textos e páginas públicas são coerentes para a mesma versão e escopo.

## 6. Preparar e validar uma única versão candidata

- [ ] Resolver defeitos conhecidos das jornadas ofertadas; não confundir CI aprovada com aceite do produto.
- [ ] Validar instalação limpa online/offline, onboarding e atualização de uma versão de teste anterior.
- [ ] Testar clima na home, tela bloqueada/apagada, app por cima, retomada e instalação sem cache.
- [ ] Testar efeitos, brilho, rolagem, trocas de arte e recriação do wallpaper.
- [ ] Medir memória, bateria, estabilidade e compatibilidade em aparelhos representativos; verificar requisitos de páginas de memória de 16 KB aplicáveis.
- [ ] Conferir certificado de upload, Play App Signing, acesso autorizado às chaves e armazenamento seguro, sem ler/publicar senhas no relatório.
- [ ] Conferir `versionCode` disponível no Console antes do build destinado ao envio.
- [ ] Gerar AAB da revisão aprovada com configuração versionada; arquivar commit, hash, certificado, mapping e notas de versão.
- [ ] Testar o artefato distribuído pela Play, incluindo compra/restauração e relatórios de pré-lançamento.

**Aceite:** há um único candidato identificado e testado, sem defeito conhecido que impeça usar o gratuito ou receber o conteúdo comprado.

## 7. Concluir configuração e teste fechado no Play Console

- [ ] Conferir titularidade, verificações e perfil de pagamentos efetivamente exigidos pela conta.
- [ ] Configurar produtos e preços da oferta final; conferir chave pública de compras no build sem expor configuração privada.
- [ ] Concluir Segurança dos dados, classificação IARC, público-alvo, anúncios, acesso e demais declarações pertinentes.
- [ ] Atualizar ficha, contato, ícone, gráfico e screenshots do app real; não anunciar artes ou recursos indisponíveis.
- [ ] Definir roteiro e registrar coordenação do teste fechado pelo responsável já indicado.
- [ ] Cumprir o requisito aplicável de pelo menos 12 participantes inscritos continuamente por 14 dias; teste interno não substitui essa faixa.
- [ ] Registrar feedback, correções e uso real; solicitar acesso à produção quando elegível.

**Aceite:** Console sem pendências impeditivas, teste exigido comprovado e acesso à produção aprovado.

Referência: [requisitos oficiais de teste](https://support.google.com/googleplay/android-developer/answer/14151465?hl=en). O prazo não garante aprovação automática.

## 8. Publicar e verificar a primeira distribuição

- [ ] Enviar a versão aprovada e acompanhar revisão/respostas do Google.
- [ ] Conferir países, oferta e primeira disponibilidade pública.
- [ ] Instalar pela listagem pública e verificar versão, links legais, preço, amostras, compra, entrega e aplicação.
- [ ] Registrar a data real de lançamento e os canais de atendimento/operação.

**Aceite:** aplicativo aprovado e distribuído com oferta e conteúdo conferidos; envio para revisão não será marcado como publicação concluída.

## 9. Operar depois da publicação

- [ ] D0–D2: conferir instalação pública, falhas, compras, suporte, clima e downloads.
- [ ] D3–D7: priorizar falhas de pagamento/entrega, crashes, ANRs e consumo excessivo; validar correções antes de distribuir updates.
- [ ] Semanalmente: revisar custos do bucket, disponibilidade, pedidos/reembolsos e feedback; ampliar artes apenas com teste e oferta configurada.
- [ ] Aplicar retenção de 60 dias ao atendimento encerrado, com preservação justificada das exceções e controle de acesso.
- [ ] Manter histórico do acervo e arquivos necessários à restauração de compras; definir recuperação de falha e continuidade.
- [ ] Revisar documentos e declarações quando houver nova finalidade, fornecedor ou funcionalidade.

**Aceite:** responsáveis executam a rotina e mantêm evidências. A frequência de novas artes não será tratada como promessa automática ao consumidor.

## Acompanhamento

| Etapa | Estado em 09/10 | Evidência |
|---|---|---|
| Decisões registradas | Concluído o registro acima | Conversa e questionário do titular; não valida configuração do Console. |
| 1 — Premium × artes | Implementação local e checks concluídos; aceite em aparelho pendente | `AcessoArte` compartilhado, seleção/aplicação protegidas, oferta corrigida, 75 testes aprovados, lint e build debug aprovados em 10/10. Não publicado. |
| 2 — Privacidade técnica | Implementação local e checks concluídos; avaliação do Rafael pendente | Canal oficial, exclusões de backup/transferência, logs minimizados, Geocoder sem uso removido, política sincronizada e 78 testes aprovados em 10/10. |
| 3 — Catálogo/produtos | Pendente | Escopo e oferta registrados em documentos diferentes; falta lista final reconciliada. |
| 4 — Compra/entrega remota | Pendente | Base de acervo existe; jornada principal não concluída. |
| 5 — Documentos/site | Rascunhos existentes; conclusão pendente | Revisões locais não vigentes, domínio indefinido. |
| 6 — Candidato validado | Pendente | CI anterior aprovada; não substitui candidato após correções e teste real. |
| 7 — Console/teste fechado | Pendente de evidência atual | Sem acesso ao painel nesta rodada. |
| 8 — Publicação | Pendente | Nenhuma publicação realizada nesta rodada. |
| 9 — Operação | Planejada | Começa após a publicação real. |
