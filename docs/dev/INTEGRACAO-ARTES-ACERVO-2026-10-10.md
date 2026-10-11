# Etapa 4 — compras de artes e entrega do acervo

Implementação local de 10/10/2026. Não houve upload, cadastro de produtos nem publicação na Play. Este documento complementa o checklist; não certifica o aceite no Android.

## O que foi integrado

- Premium continua independente: somente efeitos e estilos. Uma compra de arte concede apenas aquela combinação de cenário e variante.
- A Loja consulta preços e ofertas elegíveis na Google Play em lotes. Pagamento pendente não concede acesso; compras confirmadas passam pela verificação RSA já existente. Restauração bem-sucedida reconcilia os direitos; falha na consulta preserva o acesso local anterior.
- Comprar ou restaurar não baixa nem aplica automaticamente. A pessoa solicita **Baixar e aplicar**, vê tamanho/progresso, pode cancelar e repetir. O wallpaper só muda após instalação e nova conferência do direito.
- Catálogo e miniaturas ficam no aplicativo. Há 71 cenas/75 artes com arquivos disponíveis nesta máquina, cinco amostras gratuitas e 70 IDs pagos propostos. As 66 cenas adicionais não têm HD dentro do aplicativo. Miniaturas JPEG de até 240 pixels de largura somam aproximadamente 2 MB.
- Pacotes usam PNGs originais, sem transcodificação de arte. ZIPs têm SHA-256 completo fixado no manifesto embarcado, limites de tamanho, proteção contra extração fora da pasta e instalação temporária. Recibos identificam a versão instalada. Limpar downloads preserva compras e exige trocar primeiro uma arte remota ativa por uma amostra embutida.
- Direitos locais e arquivos do acervo são excluídos do backup/transferência do Android. Reinstalação exige restaurar compras pela Play e baixar novamente as artes remotas escolhidas.

O fluxo de compras usa validação no cliente; **não foi implementado servidor de validação de pedidos**. Um bucket público não controla direitos de compra nem impede obtenção externa dos arquivos. Os hashes embarcados conferem a correspondência do pacote com a versão do app. Se for necessária proteção da distribuição, será preciso servidor que valide a compra e emita acesso temporário; isso não está neste fluxo.

## Preparar e conectar o bucket

**Confirmado pelo usuário em 10/10:** ainda não existe bucket configurado. Referências históricas a uma conta R2 do Rafael não comprovam um bucket disponível. Cloudflare R2 é a proposta já registrada; nenhuma conta, cobrança ou recurso externo foi criado nesta execução.

**Primeiro passo acompanhado:** entrar na conta Cloudflare que será responsável pelo Terra, abrir **R2 Object Storage** e criar o bucket `terra-acervo`. Se o painel exigir ativação do serviço ou faturamento, conferir as condições exibidas com o titular. A criação mantém o bucket privado por padrão; preparar os próximos passos antes de habilitar acesso público ou enviar conteúdo.

Para teste, a Cloudflare oferece URL de desenvolvimento `r2.dev`. Ela tem limites e não é indicada para produção; o lançamento precisa de domínio próprio conectado ao bucket ou outra entrega de produção adequada. O domínio do Terra ainda não foi definido. Referências: [criar bucket](https://developers.cloudflare.com/r2/buckets/create-buckets/) e [acesso público](https://developers.cloudflare.com/r2/buckets/public-buckets/).

1. Rafael precisa reconciliar o acervo completo e avaliar as artes no Android. O catálogo local não comprova as 366 artes mencionadas nem aprova todas as 75 disponíveis. As sete famílias excluídas não entram no manifesto.
2. Regenerar o inventário quando os arquivos de origem mudarem:

   ```powershell
   python scripts/inventariar_artes.py --data 2026-10-10 --saida docs/loja/catalogo-2026-10-10
   python scripts/preparar_acervo.py
   powershell -File scripts/gerar_miniaturas.ps1
   ```

3. Arquivos preparados para hospedagem: **`dist/acervo-preparado/`**. Os scripts não fazem upload. Confirmar bucket/provedor, URL pública HTTPS e autorização de publicação. Preservar caminhos `pack/<cena>/_cena.zip` e `pack/<cena>/<arte>.zip`, com os mesmos bytes do manifesto. Não substituir arquivos usados por versões já distribuídas; manter arquivo das versões e definir caminhos versionados antes do lançamento.
4. No build destinado ao teste, passar `-PterraAcervoUrl=https://DOMINIO-REAL/CAMINHO-VERSIONADO/`. A URL é pública e não leva credenciais. O exemplo é um marcador, não um servidor configurado. Sem URL, compras remotas ficam desabilitadas; as miniaturas continuam visíveis. Release ignora mudanças locais de servidor; debug admite HTTP apenas no computador local/emulador.
5. Conferir disponibilidade, HTTPS, hashes e custos do provedor. O app consulta os ZIPs diretamente; o manifesto de produção confiável é o que acompanha o app. Alterar catálogo/hashes exige nova versão do aplicativo neste modelo.

## Configurar produtos na Play

1. Usar o inventário `docs/loja/catalogo-2026-10-10/ARTES.csv`, coluna `product_id_proposto`, e conferir cada arte/variante. IDs seguem `terra_arte_<cenario>_<variante>`; nenhuma compra de arte libera o cenário inteiro.
2. Criar/ativar produtos únicos não consumíveis com opção de compra permanente, preço-base R$ 1,99 e países acordados. Miniaturas não demonstram que o SKU está cadastrado. A promoção R$ 0,99 depende de oferta configurada e elegível na Play; não há rotação automática implementada.
3. Manter `terra_premium` separado, R$ 49,90, com descrição de efeitos/estilos. Não prometer inclusão de efeitos futuros enquanto Rafael não decidir.
4. Distribuir o candidato pela faixa de teste após revisar a etapa 6 e conferir `versionCode`. Usar testadores de licença; os APKs locais não são candidatos autorizados para publicação.

## Roteiro do Rafael — evidências necessárias

1. Instalação limpa: cinco amostras, todas as miniaturas disponíveis sem download HD, artes pagas bloqueadas mesmo com Premium. Verificar legibilidade e nomes do catálogo.
2. Comprar uma arte nativa e uma remota: preço da tela igual ao checkout, pendência sem acesso, confirmação libera somente a arte comprada. Testar cancelamento e item já possuído.
3. Download remoto: tamanho/progresso, cancelamento, retomada por nova tentativa, sem rede, falta de espaço e pacote incorreto. Arte anterior continua aplicada em falha; arquivos incompletos não podem aparecer como instalados.
4. Aplicar remotamente e reiniciar app/aparelho. Testar wallpaper, frente, zonas, profundidade e efeitos **em cada arte ofertada**, incluindo diferentes proporções de tela. Testes de arquivo não certificam a renderização.
5. Offline: usar arte instalada e direito previamente validado. Não remover acesso quando a consulta de compras falha. Após reembolso e consulta bem-sucedida, negar arte e voltar à amostra utilizável.
6. Reinstalação: restaurar pela Play, conferir posse sem HD instalado, baixar novamente e aplicar. Limpar downloads preserva compra; tentar limpar com arte remota ativa deve ser recusado.
7. Registrar aparelho/Android, revisão do app, produto, resultado e captura, sem tokens, dados pessoais de pedidos ou chaves privadas.

## Pendências para concluir

URL/upload do bucket; produtos e países reais; inventário completo aprovado; nomes e apresentação final das artes; confirmação das promoções e continuidade/versionamento do conteúdo adquirido; teste ponta a ponta pela Play e avaliação do Rafael. Documentos sobre o fornecedor real e tráfego da entrega serão refinados na etapa 5 antes da publicação.

Referências técnicas: [integração de compras](https://developer.android.com/google/play/billing/integrate) e [segurança de faturamento](https://developer.android.com/google/play/billing/security).

## Verificacao local

84 testes unitarios aprovados, sem falhas. Lint debug sem erros, com 31 avisos (um aviso de uso de KTX na nova configuracao; baseline existente preservada). Todos os 146 ZIPs gerados (71 comuns e 75 de arte) conferidos por tamanho, SHA-256 e leitura ZIP; 75 miniaturas JPEG presentes e tres HTMLs de privacidade identicos. Builds debug e release aprovados (release com minificacao R8).

O build de release foi executado com a cadeia de ferramentas existente; R8 emite avisos de leitura de metadados Kotlin. Antes de distribuir um candidato, avaliar compatibilidade da cadeia e testar a versao minificada no aparelho. Nenhuma validacao de checkout, HTTP real, cancelamento em rede ou renderizacao do acervo remoto foi feita nesta maquina.
