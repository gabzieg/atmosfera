package com.atmosfera.wallpaper.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams

/**
 * Google Play Billing — Premium por COMPRA ÚNICA (produto in-app não consumível).
 * Cadastre o produto `atmosfera_premium` (tipo "Produto") no Play Console.
 *
 * @param onMudou chamado (na thread do billing) quando o Premium é confirmado.
 */
class BillingManager(
    private val context: Context,
    private val onMudou: (Boolean) -> Unit,
) : PurchasesUpdatedListener, BillingClientStateListener {

    companion object {
        const val PRODUTO_PREMIUM = "atmosfera_premium"
    }

    private var detalhes: ProductDetails? = null

    private val client = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
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
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUTO_PREMIUM)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            ).build()
        client.queryProductDetailsAsync(params) { _, lista -> detalhes = lista.firstOrNull() }
    }

    /** Restaura a compra (ex.: reinstalação / novo aparelho). */
    fun restaurar() {
        client.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP).build()
        ) { _, compras -> compras.forEach { processar(it) } }
    }

    fun comprar(activity: Activity) {
        val pd = detalhes ?: return
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

    private fun processar(p: Purchase) {
        if (p.purchaseState == Purchase.PurchaseState.PURCHASED &&
            p.products.contains(PRODUTO_PREMIUM)
        ) {
            if (!p.isAcknowledged) {
                client.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(p.purchaseToken).build()
                ) { /* confirmado */ }
            }
            Plano.setPremium(context, true)
            onMudou(true)
        }
    }

    fun temProduto(): Boolean = detalhes != null
    fun precoFormatado(): String? = detalhes?.oneTimePurchaseOfferDetails?.formattedPrice

    fun encerrar() { if (client.isReady) client.endConnection() }
}
