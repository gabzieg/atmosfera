# Fechamento do Premium — Play Console

Estado em 2026-09-20: o app usa Google Play Billing 9.1.0 e o produto
`terra_premium` como compra única não consumível. O código de seleção de
estilos, prévia, revogação e verificação local da assinatura está implementado.
**A compra real ainda não foi validada**: falta preencher a chave de licenciamento
local e confirmar o produto e a distribuição no Play Console. O proprietário
confirmou que a conta de desenvolvedor está ativa, mas o app ainda não havia
sido criado no Console no início deste passo a passo. O acesso à conta está com
Rafael; os passos no Console aguardam acesso autorizado.

## Acesso ao Console sem compartilhar senha

O Rafael pode ir a **Usuários e permissões** no Play Console e convidar o e-mail
Google do Gabriel. Para criar o app, as permissões de conta **Ver informações
do app e baixar relatórios em massa** e **Criar, editar e excluir apps em
rascunho** são necessárias. Depois, para configurar o produto e o teste interno,
conferir **Gerenciar presença na loja**, **Lançar apps em faixas de teste** e
**Gerenciar faixas de teste e listas de testadores**. Configuração do perfil de
pagamentos pode exigir o titular da conta. O Rafael pode limitar o acesso ao
app depois da criação. Fonte: [permissões do Play Console](https://support.google.com/googleplay/android-developer/answer/9844686).

## Passo a passo conjunto

1. Em **Todas as aplicações > Criar app**, use idioma **Português (Brasil)**,
   nome **Terra - Live Wallpaper** (conforme `docs/loja/FICHA.md`), tipo
   **Aplicativo**, opção **Grátis** e um e-mail de suporte real. Aceite as
   declarações exigidas e conclua a criação. O pacote
   `com.terra.wallpaper` será associado ao cadastro no primeiro AAB.
   Confirme também que o perfil de pagamentos está habilitado para vender
   produtos.
2. Em **Monetizar com o Google Play > Produtos > Produtos de compra única**,
   crie ou confira `terra_premium`. Use uma opção de **compra** permanente
   (não aluguel, pré-venda nem assinatura), com o nome **Terra Premium** e a
   descrição: **"Desbloqueia todas as artes dos cinco cenários, todos os estilos
   e os oito efeitos vivos do Terra."**
   Defina os países e o preço; a SPEC prevê R$ 49,90 no Brasil. Ative/publice
   o produto. A interface do Play Console pode variar; confirme a opção ativa
   e elegível para o país da conta de teste.
3. Em **Monetizar com o Google Play > Configuração de monetização > Licenciamento**,
   copie a chave pública Base64. No computador, abra
   `android-app/keystore.properties` e acrescente uma linha
   `playLicensePublicKey=CHAVE_BASE64`, sem aspas, espaços ou quebras de linha.
   Esse arquivo já é local e ignorado pelo Git. Sem a chave, o app desabilita
   a compra e recusa assinaturas de compra; não publique um build nessa condição.
4. Gere um novo AAB assinado com `cd android-app` e
   `./gradlew bundleRelease` (no Windows, `./gradlew.bat bundleRelease`).
   Envie o AAB para **Teste interno** no Play Console. O build instalado
   diretamente por `adb` verifica a UI, mas a compra precisa ser testada via
   distribuição do Google Play.
5. Em **Configurações > Teste de licença**, inclua a conta Google do testador.
   Adicione a mesma conta à lista da faixa de teste interno, publique a versão
   de teste e instale pelo link de participação. O produto precisa estar
   publicado para as compras de teste funcionarem.
6. No aparelho de teste, abra **Loja > Premium**. Confira o preço localizado;
   toque em um estilo bloqueado e confira a prévia; compre com o método de
   pagamento de teste. O app deve mostrar Premium ativo, liberar os oito
   efeitos e permitir selecionar um estilo pago no wallpaper.
7. Feche e reabra o app, depois reinstale pela Play Store na mesma conta.
   O acesso deve ser restaurado sem nova cobrança. Teste também cancelamento,
   pagamento pendente e erro de rede: nenhum deles deve liberar Premium antes
   de `PURCHASED` validado. Em teste, faça reembolso/estorno de uma compra
   de teste e confira a revogação no próximo retorno com conexão; o estilo
   ativo deve voltar para Pixel.

## Critério de saída

- [x] Estilo pago recusado sem Premium no seletor e no serviço do wallpaper.
- [x] Toque em estilo pago abre prévia com caminho para compra.
- [x] Assinatura inválida ou chave ausente não libera acesso.
- [x] Preço e token da mesma oferta são usados no checkout.
- [x] Compra pendente, cancelada e falha têm feedback no app.
- [x] Indicador local de Premium excluído do backup e da transferência entre aparelhos.
- [x] Verificação RSA coberta por teste local com recibo válido, alterado e chave errada.
- [ ] Produto ativo e chave pública configurada no build de teste.
- [ ] Compra de teste, restauração, cancelamento, pendência e revogação validados
  num aparelho com Google Play pela faixa de teste.

A verificação RSA no aparelho é uma proteção básica, mas não substitui a
verificação do token em um servidor nem notificações de compra/reembolso em
tempo real. O projeto ainda não tem esse backend; a revogação hoje ocorre quando
`queryPurchasesAsync` retorna com sucesso na abertura/retomada do app.

Fontes oficiais: [produtos de compra única](https://support.google.com/googleplay/android-developer/answer/16430488),
[ofertas e token de compra](https://developer.android.com/google/play/billing/one-time-product-multi-purchase-options-offers),
[chave de licenciamento](https://support.google.com/googleplay/android-developer/answer/186113),
[teste de licença](https://support.google.com/googleplay/android-developer/answer/6062777),
[verificação em backend](https://developer.android.com/google/play/billing/backend).
