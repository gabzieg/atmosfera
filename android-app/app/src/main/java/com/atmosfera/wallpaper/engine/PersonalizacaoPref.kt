package com.atmosfera.wallpaper.engine

import android.content.Context
import androidx.preference.PreferenceManager

/**
 * Ajustes de personalização visual do wallpaper (Ajustes → Geral): brilho e
 * rolagem lateral. Mesmo padrão `object` sobre `SharedPreferences` de
 * [com.atmosfera.wallpaper.weather.IntervaloClima]/`Cena`/`Plano` — o projeto
 * não tem injeção de dependência.
 */
object PersonalizacaoPref {

    private const val KEY_BRILHO = "personalizacao_brilho"
    private const val KEY_PARALLAX = "personalizacao_parallax"
    const val BRILHO_PADRAO = 0

    /** 0..100. */
    fun brilho(context: Context): Int =
        PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_BRILHO, BRILHO_PADRAO)

    fun definirBrilho(context: Context, valor: Int) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putInt(KEY_BRILHO, valor.coerceIn(0, 100)).apply()
    }

    fun parallaxAtivo(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_PARALLAX, false)

    fun definirParallax(context: Context, ativo: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putBoolean(KEY_PARALLAX, ativo).apply()
    }
}
