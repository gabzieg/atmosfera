# Proposta para o Premium e o acervo remoto — 28/09/2026

**Estado:** proposta para discussão; nenhuma mudança de produto ou de entrega foi implementada por este documento. O pedido de Rafael substitui a premissa do plano de 23/09 de que os 69 cenários restantes seriam uma evolução posterior. O preço informado é **R$ 49,90 por compra única**; esta proposta não reabre a decisão de preço.

## 1. O que o pedido significa para o usuário

1. O usuário instala um app leve e vê o catálogo completo por miniaturas pequenas, sem baixar as artes de alta resolução.
2. Antes de baixar um cenário, vê uma prévia suficiente para decidir. A prévia precisa ter qualidade e comportamento definidos; uma miniatura ampliada não equivale a uma demonstração viva.
3. A compra única `terra_premium` libera os **efeitos** (os 8 efeitos vivos + todos os estilos de efeito, em qualquer cenário) — **não** libera os cenários/artes do acervo em si, que continuam compra separada (avulsa ou em pack). Comprar Premium e baixar um cenário são direitos independentes: ter Premium não dá acesso ao acervo remoto, e comprar um cenário não dá os efeitos vivos nele. Um pedido de teste da Play deve produzir o mesmo direito no app que uma compra normal, sem cobrança real quando feito com método de pagamento de teste por testador de licença.
4. Ao escolher uma arte para usar, o app baixa **somente** os arquivos necessários para essa arte e os dados comuns da cena, verifica a integridade, instala localmente e só então permite aplicá-la como wallpaper vivo.
5. O que já foi baixado continua funcionando sem internet. Em outro aparelho ou após reinstalação, a compra deve ser restaurada pela Play e a arte pode ser baixada novamente.
6. Pagamento pendente, falha, cancelamento ou reembolso não devem conceder ou manter indevidamente o direito Premium.

## 2. Estado verificado na cópia local

| Área | Hoje | Lacuna em relação ao pedido |
|---|---|---|
| Compra | `terra_premium` é compra única; o app valida assinatura local, concede `Plano` e consulta compras na retomada. Rafael informa que há pedido de teste no Console e que vagalumes/lampiões foram observados. | Um pedido no Console e dois efeitos ativos são evidência parcial. Falta verificar no aparelho o estado da compra, confirmação (`acknowledge`), restauração, revogação e liberação de conteúdo remoto. |
| Catálogo | `Catalogo.kt` lista cinco cenários; `StoreTab.kt` só mostra entradas com `fundo.png` local. | Os outros 69 não aparecem para o usuário. |
| Miniaturas | `SceneThumbnail.kt` reduz os `fundo.png` locais para exibição; não há arquivo pequeno separado para cada cenário. | Gerar miniaturas próprias para o catálogo completo e embuti-las no app, conforme pedido. |
| Conteúdo | O AAB 1.0.3 inclui nove artes de fundo dos cinco cenários. `content-packs/cenarios-futuros/` guarda 69 pastas, cada uma com `pixel/fundo.png`. | Retirar as artes HD da base na configuração final, publicar packs no bucket e verificar a completude de cada cena. Existir um `fundo.png` não comprova que a cena funciona no celular. |
| Download | `Acervo.kt` já tem manifesto, `thumb`/`preview`, ZIP por arte e download com hash; `EffectEngine.kt` pode ler `filesDir/acervo`. | `Acervo.BASE_PADRAO` está vazio; somente o painel de debug chama o download. A Loja, a prévia e a aplicação ainda não fazem a jornada completa. |
| Publicação do acervo | Documentos antigos descrevem `pacote_cenas.py`, `subir_acervo.py` e `dist/`. | Esses scripts e o `dist/` não estão nesta cópia do repositório. Precisamos recuperá-los ou recriar a pipeline e verificar sua compatibilidade com as 69 cenas atuais. Bucket/URL publicados não foram comprovados. |

O AAB 1.0.3 pode servir para testar a compra atual, mas **não representa a oferta de mais de 70 cenários sob demanda** e não deve ser tratado como candidato final dessa oferta.

## 3. Contrato proposto para catálogo e entrega

**No AAB:** código, folhas de efeitos compartilhadas, catálogo mínimo e miniaturas pequenas de todos os cenários anunciados. A exigência de Rafael é não incluir wallpapers HD. Se for indispensável que o app aplique um wallpaper na primeira abertura sem internet, será preciso aprovar explicitamente uma exceção para uma arte inicial; sem ela, o usuário offline poderá navegar pelas miniaturas, mas não aplicar uma cena que ainda não baixou.

**No bucket:** manifesto versionado, previews leves e packs de arte em alta resolução com fundo, frente, luzes e arquivos de marcação/máscara necessários. Cada pack deve ter tamanho e hash no manifesto. Os arquivos precisam permanecer acessíveis por versão para não quebrar instalações com catálogo em cache.

**No aparelho:** miniatura imediatamente; preview leve quando houver rede, com fallback para a miniatura; download HD apenas após o comando de usar/aplicar; progresso, cancelamento, repetição e mensagem de falha; conteúdo confirmado guardado em armazenamento persistente. O wallpaper ativo jamais deve ser substituído por uma cena incompleta.

**Direito de acesso:** o Google Play continua sendo a fonte de verdade — mas para **dois** direitos independentes, não um só: `terra_premium` (efeitos vivos, todos os estilos) e a posse de cada cenário/pack (o que dá acesso a baixar aquela arte do acervo). Baixar do bucket é sempre condicionado à posse do cenário/pack, não ao Premium. A presença de um arquivo no disco não concede acesso. Se o bucket for público, a restrição existe dentro do app, não no endereço do arquivo: quem descobrir a URL poderá copiar o pack. Para restringir o download no servidor, será necessário um backend que valide o token da compra e emita acesso temporário; a chave de escrita do bucket nunca deve ir no aplicativo. A [documentação do Google](https://developer.android.com/google/play/billing/security) recomenda verificar tokens de compra no backend quando possível.

Para tráfego de produção no Cloudflare R2, preferir domínio próprio. A [Cloudflare reserva a URL pública `r2.dev` para desenvolvimento/teste](https://developers.cloudflare.com/r2/buckets/public-buckets/).

## 4. Sequência de trabalho e aceite

| Etapa | Entrega verificável | Critério de aceite |
|---|---|---|
| A. Congelar contrato | Relação de cenas/artes incluídas no Premium, regra de futuras inclusões, política de prévia e acesso ao bucket. | Texto da oferta no app, Play Console e documentos concorda com o conteúdo entregue. |
| B. Fechar compra | Teste de licença em versão da Play com pedido de teste identificado, `PURCHASED`, confirmação, restauração em reinstalação/outro aparelho e revogação. | Premium funciona por conta Google; um erro ou pagamento pendente não libera acesso. [Teste oficial de faturamento](https://developer.android.com/google/play/billing/test). |
| C. Auditar as 69 cenas | Inventário de camadas, máscaras, metadados, orientação, tamanho e renderização no motor Android. | Cada cena candidata tem um resultado de teste no celular; falhas são corrigidas ou a cena não é anunciada. |
| D. Pipeline de conteúdo | Recuperar/criar empacotador, manifesto e upload com hashes; gerar miniaturas nativas e previews leves. | Todos os itens do catálogo apontam para arquivos existentes e verificáveis; publicação do manifesto ocorre depois dos packs. |
| E. Integrar a jornada | Catálogo completo, miniaturas locais, preview leve, botão de baixar/aplicar e estados de rede/armazenamento. | Selecionar uma arte baixa só seu pack HD; outra arte não é baixada por antecipação. |
| F. Testar em aparelhos | Teste interno com dispositivos distintos, inclusive pouco espaço, rede lenta/interrompida e Android antigo/atual. | Instalação, compra, busca, prévia, download, aplicação, reinício, offline, restauração e limpeza não produzem tela vazia, crash ou perda do wallpaper ativo. |
| G. Sincronizar publicação | Política de privacidade, Data Safety, ficha da Play, instruções de suporte e operação do bucket. | O material público descreve corretamente a entrega remota e o escopo de R$ 49,90. |

Os 69 cenários são **acervo a validar**, não 69 cenários já aprovados para celular. A primeira entrega ao teste interno pode usar um pequeno lote representativo antes de abrir o catálogo completo. A publicação pública só deve anunciar os cenários que passaram no teste.

## 5. Decisões a confirmar antes da implementação

1. **Alcance da compra:** “R$ 49,90 por tudo” inclui automaticamente todas as futuras cenas e artes adicionadas ao acervo, ou apenas o catálogo anunciado no lançamento? Recomendação: definir uma regra única, sem vender separadamente algo prometido como parte do Premium.
2. **Qualidade da prévia:** miniatura nativa + imagem intermediária remota e estática é suficiente? Uma prévia viva antes do HD exige outro pacote leve com camadas/máscaras ou um vídeo de demonstração.
3. **Primeira abertura offline:** permitir uma arte HD inicial embarcada como exceção ou aceitar que nenhum wallpaper novo possa ser aplicado até o primeiro download?
4. **Proteção do bucket:** acesso público com bloqueio no app, mais simples, ou download restrito por backend, mais complexo? A escolha define segurança, operação e custo.
5. **Publicação do catálogo:** colocar miniaturas de todos os cenários no AAB significa que uma cena nova, para aparecer com miniatura nativa, exigirá atualização do aplicativo. Se o objetivo for publicar cenas sem atualização, as novas miniaturas também terão de vir remotamente, com cache local. Definir qual regra prevalece.

## 5.1 Decisões confirmadas — 29/09/2026 (Gabriel)

1. **Alcance da compra — corrigido em 29/09**: `terra_premium` (R$49,90) dá
   os **efeitos** (8 efeitos vivos + todos os estilos, em qualquer cenário),
   não os cenários/packs. Isso é o modelo já registrado em `SPEC.md` (13/09),
   não uma decisão nova — a proposta original tinha misturado os dois.
   Cenários e packs do acervo continuam compra separada (avulsa R$1,99 ou em
   pack, "a definir"). **Resolve a colisão** com "Packs" como eixo de
   monetização: os dois modelos são ortogonais, não concorrentes — Premium
   liga a *capacidade* de efeito, cenário/pack dá a *cena* em si.

   O quanto de risco/prazo ("temos ~3 semanas, precisamos do app redondo")
   segue valendo como princípio geral desta rodada de decisões: fechar rápido,
   sem reabrir depois — só a resposta específica de alcance mudou.

2. **Prévia: miniatura estática basta.** As artes gratuitas já cumprem o papel
   de "demonstração viva" — o usuário generaliza a partir delas o que as
   demais vão entregar. Não é necessário pacote de prévia "viva" separado.

3. **Primeira abertura offline: Cabana continua embarcada como exceção**,
   confirmando o padrão já em uso hoje. O onboarding já conduz o usuário a
   escolher e aplicar um wallpaper no primeiro uso — a exceção existe
   justamente para esse caminho não ficar vazio.

4. **Proteção do bucket: público por agora**, sem backend de validação de
   token. Aberto para evoluir depois se cópia não autorizada do acervo virar
   problema real — ver nota acima sobre isso ser extensão, não retrabalho, do
   que for construído agora.

5. **Miniaturas do catálogo completo ficam embarcadas no AAB** (não remotas).
   Cena nova publicada exige atualização do app para ganhar miniatura nativa
   — aceito.

## 6. Documentos que precisarão ser reconciliados após a decisão

`PLANO-LANCAMENTO.md`, `SPEC.md`, `DECISAO-ENTREGA-DE-ARTE.md`, `ENTREGA-DE-ARTE.md`, `BUCKET-R2.md`, ficha da loja e textos legais ainda descrevem escopos ou fases diferentes. Esta proposta registra a nova direção sem marcar nenhuma dessas etapas como concluída.
