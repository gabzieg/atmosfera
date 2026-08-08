package com.atmosfera.wallpaper.billing

import android.app.Activity
import android.content.Context
import android.util.Base64
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
import com.atmosfera.wallpaper.engine.Catalogo
import com.atmosfera.wallpaper.engine.Cena
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

class BillingManager(
    private val context: Context,
    private val onMudou: (Boolean) -> Unit,
) : PurchasesUpdatedListener, BillingClientStateListener {

    companion object {
        const val PRODUTO_PREMIUM = "atmosfera_premium"
        const val PREF_AVULSO_PREFIX = "comprou_avulso_"
        private const val TAG = "BillingManager"

        /**
         * Chave pública de licenciamento (Base64) do Play Console — Monetizar >
         * Configuração de monetização > Chave de licença. Sem ela, a verificação
         * de assinatura é pulada (comportamento atual, sem bloqueio); com ela,
         * compras com assinatura inválida são rejeitadas antes de liberar o plano.
         */
        private const val LICENSE_PUBLIC_KEY_BASE64 = ""
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private var detalhesMap: Map<String, ProductDetails> = emptyMap()

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
        // — o Atmosfera só vende compra única (INAPP), nunca assinatura.
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
            _precos.value = detalhesMap.mapNotNull { (id, pd) ->
                pd.oneTimePurchaseOfferDetails?.formattedPrice?.let { id to it }
            }.toMap()
        }
    }

    fun restaurar() {
        client.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP).build()
        ) { _, compras -> compras.forEach { processar(it) } }
    }

    fun comprar(activity: Activity, productId: String = PRODUTO_PREMIUM) {
        val pd = detalhesMap[productId] ?: return
        val paramsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(pd).build()
        )
        val flow = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(paramsList).build()
        client.launchBillingFlow(activity, flow)
    }

    override fun onPurchasesUpdated(result: BillingResult, compras: MutableList<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && compras != null) {
            compras.forEach { processar(it) }
        }
    }

    /**
     * Verifica a assinatura RSA da compra contra [LICENSE_PUBLIC_KEY_BASE64].
     * Sem chave configurada, não bloqueia (loga aviso) — evita quebrar compras
     * reais antes de alguém colar a chave certa do Play Console.
     */
    private fun assinaturaValida(p: Purchase): Boolean {
        if (LICENSE_PUBLIC_KEY_BASE64.isBlank()) {
            Log.w(TAG, "Chave de licenciamento não configurada — pulando verificação de assinatura da compra.")
            return true
        }
        return try {
            val keySpec = X509EncodedKeySpec(Base64.decode(LICENSE_PUBLIC_KEY_BASE64, Base64.DEFAULT))
            val publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec)
            val sig = Signature.getInstance("SHA1withRSA")
            sig.initVerify(publicKey)
            sig.update(p.originalJson.toByteArray())
            sig.verify(Base64.decode(p.signature, Base64.DEFAULT))
        } catch (e: Exception) {
            Log.e(TAG, "Falha ao verificar assinatura da compra ${p.orderId}", e)
            false
        }
    }

    private fun processar(p: Purchase) {
        if (p.purchaseState == Purchase.PurchaseState.PURCHASED && !assinaturaValida(p)) {
            Log.w(TAG, "Compra ${p.orderId} rejeitada: assinatura inválida.")
            return
        }
        if (p.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!p.isAcknowledged) {
                client.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(p.purchaseToken).build()
                ) { /* confirmado */ }
            }
            
            p.products.forEach { productId ->
                if (productId == PRODUTO_PREMIUM) {
                    Plano.setPremium(context, true)
                    onMudou(true)
                } else {
                    val cenario = Catalogo.cenarios.firstOrNull { it.productId == productId }
                    if (cenario != null) {
                        prefs.edit().putBoolean(PREF_AVULSO_PREFIX + cenario.id, true).apply()
                        Cena.definir(context, cenario.id)
                    }
                }
            }
        }
    }

    fun isAvulsoDesbloqueado(cenarioId: String): Boolean {
        return prefs.getBoolean(PREF_AVULSO_PREFIX + cenarioId, false)
    }

    fun encerrar() { if (client.isReady) client.endConnection() }
}
