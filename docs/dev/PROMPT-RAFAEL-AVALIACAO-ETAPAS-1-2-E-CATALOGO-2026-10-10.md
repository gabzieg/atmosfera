# Prompt para Rafael — avaliação das etapas 1 e 2 e preparação da etapa 3

Copie o texto abaixo para a sessão que Rafael usa no projeto. A branch compartilhada é `fix/premium-privacidade-etapas-1-2`; usar sua revisão mais recente e registrar o SHA testado. Ela ainda não foi incorporada à `main`.

---

Precisamos avaliar as correções das etapas 1 e 2 do Terra e preparar os dados da etapa 3 para continuar o lançamento.

## 1. Fonte correta e estado atual

- Repositório: `https://github.com/gabzieg/atmosfera`.
- Branch a avaliar: **`fix/premium-privacidade-etapas-1-2`**. Atualize essa referência e trabalhe em checkout separado se houver alterações pendentes na sua cópia. Preserve seu trabalho existente.
- Leia `docs/dev/CHECKLIST-FINALIZACAO-TERRA-2026-10-09.md` e registre o SHA exato usado.
- Gabriel implementou localmente as etapas 1 e 2: **78 testes unitários aprovados, lint de debug sem erros, APK de debug compilado e `compileReleaseKotlin` aprovado**. Isso não comprova comportamento no aparelho.
- A compra por arte e a Loja remota ainda não estão implementadas. Nesta versão, artes adicionais ficam bloqueadas com aviso de indisponibilidade.
- Os AABs antigos 1.0.5/código 6 não contêm automaticamente estas correções. Gere os artefatos a partir desta branch para avaliar; confirme o código disponível antes de qualquer envio posterior autorizado à Play.
- Use o JDK e a configuração Gradle versionados/documentados no projeto. Registre qualquer alteração necessária, inclusive R8; não tratar um build com ferramentas alteradas só localmente como equivalente ao build reproduzível da branch.

## 2. Decisões já confirmadas — aplicar sem perguntar novamente

- Premium `terra_premium`: compra única de **R$ 49,90**, somente os oito efeitos vivos e todos os estilos de efeito. Não compra arte.
- **R$ 1,99 compra uma arte específica**, por exemplo Cabana/low poly. Não compra todas as variantes do cenário.
- Promoções registradas: seleção rotativa de artes por R$ 0,99; não anunciar calendário ou contagem regressiva sem operação correspondente.
- Amostras gratuitas: Cabana/pixel, Bruxa/clay, Lavanda/aqua, Esfinge/vangogh e Jardim/ukiyoe.
- Canal oficial de suporte e privacidade: **suporteterrabr@gmail.com**.
- Responsável indicado: Rafael Huppes, pessoa física. Público registrado: 13+. Retenção do atendimento: até 60 dias após resolução, com exceções justificadas.
- Gabriel confirmou que houve **somente compras de teste, sem cobrança real**.
- Avaliações no aplicativo ficam sob responsabilidade do Rafael. Um teste não executado deve ser marcado **PENDENTE**, com motivo.

## 3. Avaliação da etapa 1 — Premium separado das artes

1. Verifique se UI e serviço usam a autorização compartilhada em `engine/AcessoArte.kt`; confira também `MainViewModel` e `ArteFundo.atual()`.
2. Em aparelho/emulador, use a autorização normal. O destrave de conteúdo do painel de debug deve ficar **desligado**; ativá-lo libera artes para teste e invalida essa verificação.
3. Sem Premium, confira aplicação e troca das cinco amostras gratuitas, inclusive offline. Teste especialmente sair de Cabana/pixel para Bruxa/clay e para Jardim/ukiyoe.
4. Selecione as variantes pixel pagas em Bruxa, Lavanda, Esfinge e Jardim: a prévia pode existir, mas a aplicação deve ficar bloqueada. O aviso deve explicar que a arte não faz parte do Premium e que a compra individual não está disponível nesta versão. A ação de arte bloqueada não deve abrir compra de Premium.
5. Confira textos da Loja, tela Premium, tutoriais e termos: Premium oferece efeitos/estilos, sem promessa de liberar todas as artes.
6. Confira que estilos Premium ainda podem levar à tela Premium e que seus efeitos funcionam nos cenários gratuitos autorizados.
7. Atualize uma instalação anterior de teste que tinha uma arte paga selecionada: app e wallpaper devem voltar à amostra correta do cenário. Registre versão anterior, assinatura, arte selecionada e resultado da atualização. Se assinaturas forem diferentes, reinstalação limpa não comprova essa migração.
8. Com um artefato correspondente distribuído pela Play e conta habilitada para teste de licença, teste compra, confirmação, restauração e revogação do Premium. Efeitos/estilos seguem o estado da compra; artes pagas permanecem bloqueadas sem direito próprio. Um APK debug não substitui a conferência da versão Play.
9. Verifique que acessar uma prévia não grava/aplica indevidamente uma arte paga no wallpaper ativo.

## 4. Avaliação da etapa 2 — suporte, backup, logs e localização

1. Abra “Algo errado neste cenário?” e prepare o relato. O aplicativo de e-mail deve mostrar **suporteterrabr@gmail.com**. Preparar o relato não deve enviar uma mensagem automaticamente; não é necessário enviar e-mail para comprovar o destinatário.
2. Confira as regras `backup_rules.xml` e `data_extraction_rules.xml`: `atmosfera_weather_cache.xml`, `atmosfera_met_freio.xml` e `atmosfera_plano.xml` estão excluídos. Preferências comuns continuam elegíveis.
3. Teste backup/restauração ou transferência em versões representativas do Android, indicando versão e mecanismo usado. Coordenadas/clima, freio da MET e flag Premium não devem migrar pelas novas regras; restauração da compra depende da Play e consulta nova de clima ocorre na home. Registre diferenças de fabricante/Android e restauração offline. Essas regras não comprovam exclusão de backups antigos.
4. Na versão release correspondente, confira logs: não deve existir o antigo registro de latitude/longitude; detalhes meteorológicos e de requisições não devem aparecer como diagnóstico de produção. Debug pode ter diagnóstico adicional; não usar log de debug como prova do comportamento de release.
5. Verifique que `nomeDoLugar()` e a chamada ao `Geocoder` foram removidos. Não implementar um novo provedor de nome de cidade nesta avaliação.
6. Instalação sem permissão de localização: depois de ativar o wallpaper e voltar à home, conferir São Paulo/SP como local padrão. Ao permitir localização, não apresentar São Paulo como se fosse a cidade real de todo usuário. Sem nome resolvido, a tela pode mostrar apenas o horário; nomes de cache legado podem permanecer enquanto aplicáveis.
7. Conceder e revogar localização, voltar à home e verificar ausência de crash, fallback correto e controle das consultas. Conferir tela apagada, bloqueio, outro app por cima, companion aberto e prévias: chamadas de clima somente quando o wallpaper estiver visível na home, com aparelho acordado e desbloqueado.

## 5. Adiantar a etapa 3 — inventário e oferta do lançamento

O questionário `c1b95ec` registra todo o acervo com exclusões e menciona 71 cenas/366 artes. Essa é uma contagem de inventário; ainda precisamos comprovar a composição e conciliar a publicação completa com eventual entrega em lotes.

1. Localize o inventário/manifesto mais recente, informe caminho e revisão e recalcule a contagem após os cortes. Não presumir que a pasta local e o bucket contêm o mesmo material.
2. Conferir exclusões: `simpsons`, `budokai`, `konoha`, `timessquare`, `burj`, `ogro` e `eiffel`; sprites `pixel_retro` e `pixel_retro_2` fora da distribuição oficial. Não reincluir um item só porque existe arquivo no bucket.
3. Produza **CSV ou JSON por arte** com: identificador estável, cenário, variante, amostra gratuita/paga/excluída, proposta de ID do produto Play, preço regular, preço promocional quando aplicável, caminhos de miniatura e arquivos, tamanho, hash quando disponível, camadas/máscaras necessárias, resultado da renderização no Android e pendências. IDs são proposta até verificar/cadastrar no Console; não marcar produto como ativo sem evidência.
4. Use “testado no Android”, “não testado”, “falhou” e “excluído” como estados distintos. Existência de fundo PNG ou hash válido não comprova renderização.
5. Validar um lote representativo de artes no motor Android: camadas, céu, frente, luzes, máscaras, recorte em celular, transição dia/noite e efeitos. O lote serve para conferir a jornada e não representa validação automática do acervo inteiro.
6. Preservar registros de origem/geração e licenças disponíveis. A declaração de geração com GPT não será registrada como auditoria independente de originalidade ou aprovação jurídica.

Devolver decisões explícitas sobre os pontos que faltam:

- **Oferta pública inicial:** todas as artes aprovadas de uma vez ou um primeiro lote? Se houver mudança da decisão de catálogo completo, registrar quais itens ficam para depois e conciliar com Gabriel antes de mudar oferta/app. “Catálogo alvo completo” e “lote de validação” são coisas distintas.
- **Conteúdo futuro:** uma nova variante será outra compra? Novos efeitos/estilos futuros estarão incluídos no Premium atual? Separar as duas respostas.
- **Packs:** haverá packs comerciais no lançamento? Se sim, listar conteúdo, preço e tratamento de quem já possui alguma arte incluída. Se não, registrar explicitamente a ausência nessa versão.
- **Promoções:** indicar seleção inicial, responsável pela rotação e como preço/oferta serão conferidos no Console; não criar promessa semanal no texto do produto.
- **Países:** listar os países realmente configurados/pretendidos e o que foi conferido no painel. “Sem restrição” não comprova configuração mundial concluída.

## 6. Limites e entrega do retorno

- Fazer avaliação e inventário. Se houver defeito, registrar reprodução, arquivo provável e proposta de correção; não misturar mudança de modelo comercial, migração de build ou implantação de loja remota nessa avaliação.
- Não publicar uma versão de produção, mudar ofertas no Console, enviar mensagens a terceiros ou subir/apagar conteúdo do bucket por este roteiro. Essas ações precisam de instrução específica do responsável pela publicação.
- Não enviar `.jks`, senhas, `keystore.properties`, tokens, dados fiscais, e-mails pessoais, pedidos identificáveis ou localização real em documentação pública. Guarde evidências sensíveis fora do Git e redija o resumo público sem esses dados.
- Scripts e inventário publicáveis podem ir em uma branch própria/PR; preserve a branch de origem e trabalhos existentes. Não commitar APK/AAB ou prints com dados pessoais no repositório.

Entregue:

1. **Tabela dos testes:** item, SHA/versão/build, aparelho/Android, resultado PASSOU/FALHOU/PENDENTE, evidência sanitizada e observação.
2. **Falhas encontradas:** passos de reprodução, esperado/observado, impacto e sugestão de correção.
3. **Inventário por arte:** arquivo, contagens recalculadas, exclusões e lista dos itens ainda sem teste/arquivos.
4. **Decisões da etapa 3:** respostas dos cinco pontos acima, com dúvidas que dependem do titular separadas de tarefas técnicas.
5. **Referência de entrega:** branch/commit/PR com material publicável ou caminho dos arquivos locais; informar exatamente o que permanece só local.

Não declarar etapas 1/2 encerradas se o teste correspondente não ocorreu. Não declarar etapa 3 concluída apenas porque o inventário existe: oferta, arquivos e resultado de avaliação precisam concordar.
