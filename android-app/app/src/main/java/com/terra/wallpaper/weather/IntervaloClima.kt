package com.terra.wallpaper.weather

import android.content.Context
import androidx.preference.PreferenceManager

/**
 * Intervalo de atualização do clima escolhido pelo usuário (Ajustes → Cenário).
 * `object` sobre `SharedPreferences`, mesmo padrão de [com.terra.wallpaper.billing.Plano]
 * e `Cena` — o projeto não tem injeção de dependência.
 *
 * O intervalo só vale durante uma sessão elegível na home.
 */
object IntervaloClima {

    private const val KEY = "intervalo_clima_min"
    const val PADRAO_MIN = 30
    val OPCOES_MIN = listOf(15, 30, 60)

    fun atual(context: Context): Int =
        PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY, PADRAO_MIN)

    fun definir(context: Context, minutos: Int) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putInt(KEY, minutos).apply()
    }

    /**
     * Por quanto tempo o clima em cache conta como fresco.
     *
     * Sem margem de 10%: voltar à home não deve antecipar consultas.
     * WeatherCache considera vencido também o instante exato do prazo.
     */
    fun ttlMs(context: Context): Long = ttlMs(atual(context))

    fun ttlMs(minutos: Int): Long = minutos * 60_000L
}
