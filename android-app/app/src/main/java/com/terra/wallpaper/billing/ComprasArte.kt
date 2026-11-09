package com.terra.wallpaper.billing

import android.content.Context

/** Posse por produto de arte. Nunca deriva do indicador Premium ou do arquivo baixado. */
object ComprasArte {
    private const val PREFS = "atmosfera_compras_artes"
    @Synchronized fun produtos(c: Context): Set<String> =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getStringSet("produtos", emptySet())!!.toSet()

    @Synchronized fun conceder(c: Context, produto: String) {
        gravar(c, produtos(c) + produto)
    }

    @Synchronized fun reconciliar(c: Context, ativos: Set<String>) {
        gravar(c, ativos)
    }

    private fun gravar(c: Context, produtos: Set<String>) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putStringSet("produtos", produtos.toSet()).apply()
    }
}
