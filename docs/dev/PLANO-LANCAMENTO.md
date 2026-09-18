# Plano até a Google Play — Terra

Base: [auditoria de 17/09/2026](AUDITORIA-PUBLICACAO-2026-09-17.md). Status inicial: **não liberado para produção**. Caixas abaixo são critérios de aceite, não declarações de trabalho concluído.

## Ordem e responsáveis

Responsável técnico: Gabriel, conforme documentação do projeto. Titular da conta: confirmar no Console; identidade legal não é inferida. Conteúdo: responsável pelos packs a confirmar; a documentação recente menciona Rafael.

| Fase | Entrega | Depende de | Quem executa | Critério de saída |
|---|---|---|---|---|
| 0 | Oferta e infraestrutura definidas | Nada | Responsável pelo produto + titular | Marca Terra, catálogo inicial, direitos Premium, custos do clima e entrega R2 definidos por escrito. |
| 1 | Fluxo de conteúdo completo | 0 | Desenvolvimento + responsável pelos packs | Descobrir, comprar, baixar, aplicar e usar offline funcionando numa instalação limpa. |
| 2 | Compras confiáveis | 0; integração com 1 | Desenvolvimento + titular Console | Produtos ativos, posse reativa, confirmação/repetição e sandbox aprovados. |
| 3 | Privacidade e ficha coerentes | Arquitetura final de 0–2 | Titular + desenvolvimento | Textos sincronizados, URLs públicas e declarações coerentes com tráfego/SDKs. |
| 4 | Candidato de release validado | 1–3 | Desenvolvimento + testers | Testes/lint verdes, release assinado, tamanho medido, matriz abaixo aprovada. |
| 5 | Teste fechado e acesso à produção | 4 e configuração do Console | Titular + testers | Feedback tratado e requisitos da conta cumpridos; acesso à produção aprovado. |
| 6 | Revisão e primeira publicação | 5 | Titular | Release revisado e publicado nos países escolhidos; operação de suporte iniciada. |

Conta, perfil de pagamentos, verificação de identidade/dispositivo quando solicitada, reserva dos IDs de produtos e recrutamento de testers podem avançar em paralelo às fases 1–3. Não há estimativa confiável de data antes de definir o catálogo e obter acesso ao Console.

## Fase 0 — Fechar escopo sem acrescentar recursos desnecessários

- [ ] Confirmar Terra como marca pública; manter `com.atmosfera.wallpaper` se esse for o pacote registrado.
- [ ] Confirmar cinco artes gratuitas: cabana/pixel, jardim/ukiyoe, bruxa/clay, esfinge/vangogh e lavanda/aqua.
- [ ] Definir estilos de efeito livres e exclusivos; Premium não inclui compra das cenas.
- [ ] Escolher catálogo inicial dentre os 74 cenários; adiar venda de packs se a regra comercial ainda não estiver definida.
- [ ] Regularizar provedor meteorológico para uso comercial e projetar custo mensal por usuário ativo.
- [ ] Confirmar entrega R2/CDN, responsável pela publicação, domínio HTTPS e política de logs.
- [ ] Confirmar titular legal, suporte e direitos de distribuição comercial das artes, fontes e sprites.

## Fases 1 e 2 — Fechar o que acontece depois do pagamento

- [ ] Integrar manifesto remoto com a vitrine, miniaturas e prévias; não depender de PNG pago embarcado.
- [ ] Versionar a ferramenta de empacotamento ou documentar origem e procedimento reproduzível.
- [ ] Publicar packs e manifesto compatíveis, validar hashes/estrutura e configurar endpoint de produção.
- [ ] Implementar estados: indisponível, não adquirido, comprando, pendente, adquirido/não baixado, baixando, pronto e falha com nova tentativa.
- [ ] Baixar somente após direito confirmado; aplicar somente depois de conteúdo validado. Preservar wallpaper anterior se falhar.
- [ ] Implementar política Premium na aplicação real, incluindo restauração e perda do direito; manter prévia de venda separada.
- [ ] Remover artes pagas completas do release base; preservar amostras e assets necessários ao primeiro uso offline.
- [ ] Cadastrar SKUs selecionados e `atmosfera_premium`; não gerar IDs por suposição: `timessquare`, por exemplo, usa `cenario_novayork`.
- [ ] Conferir o [inventário de 75 produtos](../loja/PRODUTOS-REVISAO.csv), escolher os que entram no lançamento e registrar preço/país/disponibilidade no Console. CSV é referência de revisão, não formato de importação da Play.
- [ ] Configurar verificação de compra e impedir release que pule essa verificação silenciosamente.
- [ ] Tratar confirmação de compra, falhas/repetição, estados de pagamento, posse observável e consulta ao retornar ao app.
- [ ] Testar compra, cancelamento, pendência aprovada/negada, restauração, reembolso, reinstalação, troca de aparelho e offline com testadores de licença.

Confirmação precisa ocorrer no prazo documentado pela Play; testar só a abertura do diálogo não valida pagamento. [Integração de Billing](https://developer.android.com/google/play/billing/integrate).

## Fase 3 — Preparar o envio para revisão

- [ ] Resolver diferenças sem sobrescrever informações legais por suposição; aprovar uma fonte canônica e sincronizar Markdown, `docs`, `public_html` e assets.
- [ ] Incluir `public_html` na verificação de sincronização e nas entradas do Gradle; testar também ausência de placeholders visíveis no release.
- [ ] Publicar privacidade, termos e contato; verificar HTTPS, resposta HTTP, conteúdo e navegação em sessão sem login.
- [ ] Revisar Data Safety após concluir clima e CDN: fornecedores, retenção, localização, compras/SDKs, suporte e backup.
- [ ] Corrigir orientação de reembolso e confirmar canal para solicitações de dados.
- [ ] Preencher acesso ao app, anúncios, público-alvo, IARC e demais declarações efetivamente apresentadas pelo Console. Não pré-assumir classificação Livre: revisar também cenas de guerra/armas e conteúdo fantástico.
- [ ] Revisar nome, descrição curta/longa, categoria, contato, países, idiomas, preços e screenshots atuais do **release**. Não prometer packs, bateria excepcional ou atualizações instantâneas sem comprovação.
- [ ] Confirmar arte promocional 1024×500, ícone 512×512 e screenshots aceitos pelo Console; não reutilizar capturas com marca antiga ou menus de teste.

As respostas de segurança devem representar o app e os SDKs reais, inclusive fornecedores externos. [Data Safety](https://support.google.com/googleplay/android-developer/answer/10787469).

## Fase 4 — Matriz mínima de aceite

Para cada linha, registrar versão/versionCode, aparelho/API, data, resultado e evidência. Falha em pagamento, aplicação ou integridade de conteúdo impede avançar.

| Cenário | Resultado esperado |
|---|---|
| Primeira abertura offline | Amostra utilizável, sem tela vazia ou carregamento infinito. |
| Negar/revogar localização; localização indisponível | Estado/fallback explícito; comportamento de cache e consultas coerente com a política. |
| Cache antigo, erro de rede, servidor 429/500 | Último clima preservado e informação honesta; repetição controlada. |
| Fusos diferentes, mudança de dia, horário de verão | Hora e bloco meteorológico coerentes. |
| Aplicar cada arte gratuita | Cenário correto e acesso gratuito preservado após reiniciar. |
| Compra aprovada/pendente/cancelada/erro | Direito só no estado correto; UI informa resultado; confirmação registrada. |
| Restaurar após reinstalar ou em outro aparelho | Mesma conta recupera compras e pode baixar novamente. |
| Reembolso ou revogação | Direitos reconciliados sem apagar indevidamente compra por falha de rede. |
| Download interrompido, ZIP inválido, sem espaço, dois toques | Sem pack parcial, travamento ou perda da cena anterior; permite repetir. |
| CDN indisponível e usuário com conteúdo baixado | Wallpaper continua funcionando offline. |
| Troca rápida de cena/estilo; sair/voltar; reiniciar aparelho | Sem corrida de bitmaps, duplicação de loop, crash ou seleção errada. |
| Tela apagada e wallpaper invisível | Sem renderização contínua desnecessária; consumo comparado com wallpaper estático. |
| Sessão prolongada com efeitos intensos | Sem crescimento contínuo de memória, aquecimento anormal ou ANR. |
| Android 8/API 26, versões intermediárias e Android 16/API 36 | Instalação, localização, UI e wallpaper validados; incluir aparelho com pouca RAM. |
| Ambiente com páginas de 16 KB e ABI distribuídas | Bibliotecas nativas alinhadas e execução aprovada. |
| Tela grande/rotação, fonte ampliada, TalkBack | Conteúdo e controles acessíveis sem cortes. |
| Release minificado instalado pela Play | Mesmas jornadas aprovadas; sem caminho de desbloqueio debug. |

- [ ] Rodar `testDebugUnitTest lintDebug assembleDebug`, depois validação de release e `bundleRelease`; não ignorar falhas existentes.
- [ ] Conferir assinatura do AAB, Play App Signing, backup seguro da chave de upload, `versionCode` disponível e mapping do R8 arquivado.
- [ ] Medir tamanho comprimido por dispositivo e comparar limites vigentes do Console; tamanho bruto do AAB não basta.
- [ ] Avaliar relatório de pré-lançamento; complementar manualmente o teste do WallpaperService, que a exploração automática pode não cobrir.
- [ ] Registrar evidências das correções do serviço, Worker e Acervo listadas na auditoria.

## Fases 5 e 6 — Console, teste e publicação

Para **contas pessoais criadas após 13/11/2023**, a regra consultada exige ao menos **12 testers inscritos continuamente por 14 dias**, seguidos de solicitação de acesso à produção. Não é uma regra universal para toda conta; cumprir o prazo não garante aprovação automática. Teste interno não substitui o fechado. [Regra oficial](https://support.google.com/googleplay/android-developer/answer/14151465).

- [ ] Confirmar tipo/data da conta e exigências mostradas no Console.
- [ ] Distribuir primeiro em teste interno, depois fechado quando o candidato estiver utilizável.
- [ ] Entregar roteiro aos testers e registrar uso, feedback, defeitos e correções; planejar participantes de reserva sem presumir que substituem os dias contínuos exigidos.
- [ ] Solicitar acesso à produção quando aplicável; responder com evidências reais.
- [ ] Revalidar URLs, produtos, CDN e pacote final; enviar para revisão e tratar eventuais exigências.
- [ ] Preparar notas da versão e atendimento antes de publicar.
- [ ] Registrar data/hora, países e versão publicada; executar verificação pela listagem pública.

**Primeira publicação:** não planejar distribuição percentual de 5%/10%. Essa opção é para atualizações; a primeira versão de produção é distribuída nos países selecionados. Limitar países ou prolongar o teste fechado são formas de reduzir o alcance inicial. [Publicação oficial](https://support.google.com/googleplay/android-developer/answer/9859348).

Depois de publicar, executar [OPERACAO-POS-LANCAMENTO.md](OPERACAO-POS-LANCAMENTO.md). Nenhuma etapa que dependa da publicação foi marcada como executada antecipadamente.
