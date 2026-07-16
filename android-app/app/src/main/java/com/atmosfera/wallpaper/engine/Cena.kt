package com.atmosfera.wallpaper.engine

import android.content.Context

/**
 * CONTRATO (congelado) — cenário ATIVO escolhido pelo usuário, persistido.
 *
 * O FRONT grava aqui quando o usuário troca de wallpaper (loja/seletor);
 * o serviço do MOTOR lê [atual] para saber quais assets carregar.
 * Sempre valide contra o [Catalogo] antes de gravar um id pago não comprado.
 */
object Cena {
    private const val PREFS = "atmosfera_cena"
    private const val KEY = "atual"

    fun atual(c: Context): String =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, Catalogo.padrao.id)
            ?: Catalogo.padrao.id

    fun definir(c: Context, id: String) {
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, id).apply()
    }
}
