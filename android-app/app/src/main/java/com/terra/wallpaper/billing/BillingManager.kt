package com.terra.wallpaper.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.terra.wallpaper.engine.Catalogo
import com.terra.wallpaper.engine.Cena
import com.terra.wallpaper.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BillingManager(
    private val context: Context,
    private val onMudou: (Boolean) -> Unit,
) : PurchasesUpdatedListener, BillingClientStateListener {

    companion object {
        const val PRODUTO_PREMIUM = "terra_premium"
        const val PREF_AVULSO_PREFIX = "comprou_avulso_"
        private const val TAG = "BillingManager"

    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private var detalhesMap: Map<String, ProductDetails> = emptyMap()
    private var ofertasMap: Map<String, ProductDetails.OneTimePurchaseOfferDetails> = emptyMap()
    val premiumConfigurado: Boolean = BuildConfig.PLAY_LICENSE_PUBLIC_KEY.isNotBlank()

    private val _mensagemPremium = MutableStateFlow<String?>(null)
    val mensagemPremium: StateFlow<String?> = _mensagemPremium
    fun limparMensagemPremium() { _mensagemPremium.value = null }

    private val _precos = MutableStateFlow<Map<String, String>>(emptyMap())

    /**
     * `productId` → preço formatado, como veio do Google Play. Fica **vazio**
     * enquanto a consulta não responde e continua vazio se o produto não existe,
     * se não há Play Store no device, ou se está offline — a UI trata mapa vazio
     * como "compras indisponíveis" em vez de oferecer um botão que não faz nada.
     *
     * É um fluxo (não um getter) porque a consulta é assíncrona: lida direto na
     * composição, ela sempre voltaria nula e o preço nunca apareceria.
     */
    val precos: StateFlow<Map<String, String>> = _precos

    private val client = BillingClient.newBuilder(context)
        .setListener(this)
        // Billing 8 removeu o `enablePendingPurchases()` sem parâmetro. Passar
        // `enableOneTimeProducts()` é o equivalente exato do comportamento antigo
        // — o Terra só vende compra única (INAPP), nunca assinatura.
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        // Reconexão automática: sem isso, uma queda do serviço do Play deixava o
        // cliente morto até alguém chamar `conectar()` de novo.
        .enableAutoServiceReconnection()
        .build()

    fun conectar() {
        if (client.isReady) { consultar(); restaurar() } else client.startConnection(this)
    }

    override fun onBillingSetupFinished(result: BillingResult) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
            consultar(); restaurar()
        }
    }

    override fun onBillingServiceDisconnected() { /* reconecta no próximo conectar() */ }

    private fun consultar() {
        val skus = mutableListOf(PRODUTO_PREMIUM)
        Catalogo.cenarios.mapNotNull { it.productId }.forEach { skus.add(it) }

        val productList = skus.map {
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(it)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }

        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()
        // Billing 8 trocou a lista crua do callback por um QueryProductDetailsResult,
        // que separa o que veio (`productDetailsList`) do que NÃO veio
        // (`unfetchedProductList`, com o motivo). Antes, produto inexistente
        // simplesmente sumia da resposta sem deixar rastro.
        client.queryProductDetailsAsync(params) { resultado, detalhes ->
            if (resultado.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.w(TAG, "Consulta de produtos falhou: ${resultado.debugMessage}")
                return@queryProductDetailsAsync
            }
            detalhes.unfetchedProductList.forEach {
                Log.w(TAG, "Produto não encontrado no Play: ${it.productId} (status ${it.statusCode})")
            }
            detalhesMap = detalhes.productDetailsList.associateBy { it.productId }
            // Seleciona a opção de compra permanente, sem aluguel ou pré-venda.
            // O mesmo token que define o preço mostrado deve abrir o checkout.
            ofertasMap = detalhesMap.mapNotNull { (id, pd) ->
                val oferta = pd.oneTimePurchaseOfferDetailsList
                    ?.firstOrNull { it.rentalDetails == null && it.preorderDetails == null && it.offerId == null }
                    ?: pd.oneTimePurchaseOfferDetails?.takeIf {
                        it.rentalDetails == null && it.preorderDetails == null
                    }
                oferta?.let { id to it }
            }.toMap()
            _precos.value = ofertasMap.mapValues { it.value.formattedPrice }
        }
    }

    fun restaurar() {
        client.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP).build()
        ) { resultado, compras ->
            if (resultado.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.w(TAG, "Restauração de compras falhou: ${resultado.debugMessage}")
                return@queryPurchasesAsync
            }
            // `aplicarCena = false`: restaurar acontece a cada abertura do app.
            // Trocar o cenário aqui sobrescreveria a escolha do usuário toda vez.
            compras.forEach { processar(it, aplicarCena = false) }
            reconciliar(compras)
        }
    }

    /**
     * Revoga o que NÃO está mais entre as compras ativas — reembolso, estorno,
     * ou cancelamento pelo Google. Sem isto, [processar] só concede e nunca tira:
     * quem pedisse reembolso ficaria com Premium para sempre.
     *
     * **Só é chamado quando a consulta voltou OK.** Essa condição é o ponto
     * central: se rodasse também no erro, um usuário legítimo offline (ou num
     * device sem Play Store, como o emulador daqui) perderia o que pagou toda
     * vez que abrisse o app sem rede. Na dúvida, mantém o acesso concedido.
     */
    private fun reconciliar(compras: List<Purchase>) {
        val ativos = compras
            .filter { it.purchaseState == Purchase.PurchaseState.PURCHASED && assinaturaValida(it) }
            .flatMap { it.products }
            .toSet()

        if (PRODUTO_PREMIUM !in ativos && Plano.isPremium(context)) {
            Log.w(TAG, "Premium não consta mais nas compras ativas — revogando.")
            Plano.setPremium(context, false)
            onMudou(false)
        }

        Catalogo.cenarios.forEach { cenario ->
            val pid = cenario.productId ?: return@forEach
            if (pid !in ativos && isAvulsoDesbloqueado(cenario.id)) {
                Log.w(TAG, "Cenário ${cenario.id} não consta mais nas compras ativas — revogando.")
                prefs.edit().putBoolean(PREF_AVULSO_PREFIX + cenario.id, false).apply()
                // Se o cenário revogado é justamente o que está no ar, o wallpaper
                // ficaria exibindo conteúdo pago não mais possuído. Volta pro padrão.
                if (Cena.atual(context) == cenario.id) {
                    Log.w(TAG, "Cenário revogado estava ativo — voltando para ${Catalogo.padrao.id}.")
                    Cena.definir(context, Catalogo.padrao.id)
                }
            }
        }
    }

    fun comprar(activity: Activity, productId: String = PRODUTO_PREMIUM) {
        if (!premiumConfigurado) {
            if (productId == PRODUTO_PREMIUM) {
                _mensagemPremium.value = "Compra indisponível: configuração do Google Play pendente."
            }
            return
        }
        val pd = detalhesMap[productId]
        val oferta = ofertasMap[productId]
        if (pd == null || oferta == null) {
            Log.w(TAG, "Compra de $productId ignorada: produto ou oferta não carregado do Play.")
            if (productId == PRODUTO_PREMIUM) _mensagemPremium.value = "O Google Play ainda não carregou o Premium. Tente novamente."
            return
        }
        if (productId == PRODUTO_PREMIUM) _mensagemPremium.value = null
        val produtoParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(pd)
        oferta.offerToken?.let { produtoParams.setOfferToken(it) }
        val paramsList = listOf(produtoParams.build())
        val flow = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(paramsList).build()
        // O resultado é síncrono e diz se a tela de compra chegou a abrir. Ignorá-lo
        // reproduzia o bug do botão mudo: serviço caído ou produto indisponível
        // devolvia erro aqui e nada aparecia pro usuário.
        val resultado = client.launchBillingFlow(activity, flow)
        if (resultado.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.w(TAG, "Não foi possível abrir a compra de $productId: ${resultado.debugMessage}")
            if (productId == PRODUTO_PREMIUM) _mensagemPremium.value = "Não foi possível abrir a compra. Verifique a Play Store e tente novamente."
        }
    }

    override fun onPurchasesUpdated(result: BillingResult, compras: MutableList<Purchase>?) {
        // Antes só o OK era tratado — cancelamento, item já possuído e erro real
        // ficavam em silêncio total (nem log), o que era exatamente o achado da
        // auditoria: a UI nunca sabe por que a compra não completou.
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                // Compra recém-fechada: aqui aplicar o cenário é o comportamento
                // desejado — o usuário acabou de comprar aquele cenário.
                compras?.forEach { processar(it, aplicarCena = true) }
                if (compras.isNullOrEmpty()) _mensagemPremium.value = "A compra não foi confirmada. Tente restaurar ou comprar novamente."
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.i(TAG, "Usuário cancelou o fluxo de compra.")
                _mensagemPremium.value = "Compra cancelada."
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                // Pode acontecer se o front ficou dessincronizado da posse real
                // (ex.: compra feita em outro device). Reconsulta pra alinhar.
                Log.i(TAG, "Item já possuído — restaurando para sincronizar o estado local.")
                _mensagemPremium.value = "Compra já registrada. Restaurando seu acesso…"
                restaurar()
            }
            else -> {
                Log.w(TAG, "Compra não concluída (${result.responseCode}): ${result.debugMessage}")
                _mensagemPremium.value = "Compra não concluída pelo Google Play. Tente novamente."
            }
        }
    }

    /**
     * Verifica a assinatura RSA da compra contra a chave de licenciamento do Play Console.
     * Sem chave configurada, recusa a compra.
     */
    private fun assinaturaValida(p: Purchase): Boolean {
        val valida = AssinaturaCompra.valida(
            BuildConfig.PLAY_LICENSE_PUBLIC_KEY, p.originalJson, p.signature
        )
        if (!valida) Log.w(TAG, "Assinatura inválida ou chave ausente na compra ${p.orderId}.")
        return valida
    }

    private fun processar(p: Purchase, aplicarCena: Boolean) {
        if (p.purchaseState == Purchase.PurchaseState.PENDING && PRODUTO_PREMIUM in p.products) {
            _mensagemPremium.value = "Pagamento pendente. O Premium será liberado após a confirmação pelo Google Play."
            return
        }
        if (p.purchaseState == Purchase.PurchaseState.PURCHASED && !assinaturaValida(p)) {
            Log.w(TAG, "Compra ${p.orderId} rejeitada: assinatura inválida.")
            if (PRODUTO_PREMIUM in p.products) _mensagemPremium.value = "Não foi possível validar a compra. Verifique a configuração do Google Play."
            return
        }
        if (p.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!p.isAcknowledged) {
                client.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(p.purchaseToken).build()
                ) { resultado ->
                    // Falha aqui não é fatal: a Play reembolsa automaticamente uma
                    // compra não confirmada em até 3 dias, e restaurar() tenta de
                    // novo em toda reconexão/abertura do app (a compra continua
                    // isAcknowledged=false até um ack bem-sucedido). Mas silenciar
                    // isso escondia exatamente o cenário que o prazo de 3 dias
                    // pune — logar é o mínimo pra dar pra diagnosticar em campo.
                    if (resultado.responseCode != BillingClient.BillingResponseCode.OK) {
                        Log.w(TAG, "Falha ao confirmar compra ${p.orderId}: ${resultado.debugMessage}")
                        if (PRODUTO_PREMIUM in p.products) {
                            _mensagemPremium.value = "Premium liberado, mas a confirmação ao Google Play falhou. Abra o app novamente para tentar de novo."
                        }
                    }
                }
            }
            
            p.products.forEach { productId ->
                if (productId == PRODUTO_PREMIUM) {
                    Plano.setPremium(context, true)
                    onMudou(true)
                    _mensagemPremium.value = null
                } else {
                    val cenario = Catalogo.cenarios.firstOrNull { it.productId == productId }
                    if (cenario != null) {
                        prefs.edit().putBoolean(PREF_AVULSO_PREFIX + cenario.id, true).apply()
                        if (aplicarCena) Cena.definir(context, cenario.id)
                    }
                }
            }
        }
    }

    fun isAvulsoDesbloqueado(cenarioId: String): Boolean {
        return prefs.getBoolean(PREF_AVULSO_PREFIX + cenarioId, false)
    }

    /**
     * Encerra sempre, sem checar `isReady`. Com [enableAutoServiceReconnection]
     * ligado, um cliente que nunca conectou (offline, device sem Play Store)
     * fica retentando para sempre — e `isReady` nunca vira true, então o guarda
     * antigo justamente NÃO fechava exatamente o cliente que mais precisava ser
     * fechado, vazando um por ViewModel destruído.
     */
    fun encerrar() { client.endConnection() }
}
