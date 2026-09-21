package com.terra.wallpaper.billing

import android.content.Context
import com.terra.wallpaper.engine.EstiloEfeito

/** Estado do plano (Premium) persistido localmente. Lido pelo motor/serviço. */
object Plano {
    private const val PREFS = "atmosfera_plano"
    private const val KEY_PREMIUM = "premium"

    fun isPremium(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_PREMIUM, false)

    fun setPremium(context: Context, valor: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_PREMIUM, valor).apply()
        if (!valor) EstiloEfeito.definir(context, EstiloEfeito.GRATIS)
    }
}
