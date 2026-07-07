package com.atmosfera.wallpaper.billing

import android.content.Context

/** Estado do plano (Premium) persistido localmente. Lido pelo motor/serviço. */
object Plano {
    private const val PREFS = "atmosfera_plano"
    private const val KEY_PREMIUM = "premium"

    fun isPremium(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_PREMIUM, false)

    fun setPremium(context: Context, valor: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_PREMIUM, valor).apply()
    }
}
