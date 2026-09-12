package com.atmosfera.wallpaper.ui

import android.content.Context

/**
 * Flag de "já passou pelo onboarding", persistida localmente.
 *
 * O handoff de design pedia DataStore. Ficou `SharedPreferences` de propósito:
 * é o que o projeto inteiro já usa para estado local ([com.atmosfera.wallpaper.billing.Plano],
 * `Cena`, `ArteFundo`, `DebugOverride`), e trazer DataStore só para um booleano
 * adicionaria dependência, coroutines no caminho de abertura e um segundo padrão
 * de persistência — sem resolver nada que este aqui não resolva.
 */
object Onboarding {
    private const val PREFS = "atmosfera_onboarding"
    private const val KEY_CONCLUIDO = "concluido"

    fun concluido(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_CONCLUIDO, false)

    fun marcarConcluido(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_CONCLUIDO, true).apply()
    }
}
