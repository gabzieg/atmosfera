package com.atmosfera.wallpaper.debug

import android.content.Context
import com.atmosfera.wallpaper.billing.Plano
import com.atmosfera.wallpaper.engine.SceneState
import com.atmosfera.wallpaper.weather.DayPeriod
import com.atmosfera.wallpaper.weather.WeatherCondition
import com.atmosfera.wallpaper.weather.WeatherState

/**
 * Override de clima para TESTE (só em builds debug). Guarda condições forçadas
 * em SharedPreferences; o [com.atmosfera.wallpaper.service.AtmosferaWallpaperService]
 * as lê no lugar do clima real quando [ativo] está ligado.
 *
 * Serve para validar no aparelho cenários que não vão acontecer aqui
 * (ex.: temporal de neve, névoa densa) sem depender do clima real.
 */
object DebugOverride {
    private const val PREFS = "atmosfera_debug"
    private const val K_ATIVO = "ativo"
    private const val K_COND = "cond"           // ordinal de WeatherCondition
    private const val K_NIVEL_NEVE = "nivel_neve" // 1..3 (só p/ SNOW)
    private const val K_TEMP = "temp"
    private const val K_VENTO = "vento"
    private const val K_HORA = "hora"           // -1 = relógio real
    private const val K_NEVOA = "nevoa"         // -1 = derivada da condição

    private fun p(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    var ativoDefault = false

    fun ativo(c: Context) = p(c).getBoolean(K_ATIVO, false)
    fun setAtivo(c: Context, v: Boolean) = p(c).edit().putBoolean(K_ATIVO, v).apply()

    fun condicao(c: Context): WeatherCondition =
        WeatherCondition.entries[p(c).getInt(K_COND, WeatherCondition.SNOW.ordinal)]
    fun setCondicao(c: Context, w: WeatherCondition) = p(c).edit().putInt(K_COND, w.ordinal).apply()

    /** Nível de neve 1..3 (fraca/forte/temporal). */
    fun nivelNeve(c: Context) = p(c).getInt(K_NIVEL_NEVE, 3).coerceIn(1, 3)
    fun setNivelNeve(c: Context, v: Int) = p(c).edit().putInt(K_NIVEL_NEVE, v.coerceIn(1, 3)).apply()

    fun temp(c: Context) = p(c).getFloat(K_TEMP, -3f)
    fun setTemp(c: Context, v: Float) = p(c).edit().putFloat(K_TEMP, v).apply()

    fun vento(c: Context) = p(c).getFloat(K_VENTO, 0f)
    fun setVento(c: Context, v: Float) = p(c).edit().putFloat(K_VENTO, v).apply()

    /** Hora forçada 0..24, ou -1 para usar o relógio real. */
    fun hora(c: Context) = p(c).getFloat(K_HORA, -1f)
    fun setHora(c: Context, v: Float) = p(c).edit().putFloat(K_HORA, v).apply()

    /** Névoa forçada 0..1, ou -1 para deixar a condição decidir. */
    fun nevoa(c: Context) = p(c).getFloat(K_NEVOA, -1f)
    fun setNevoa(c: Context, v: Float) = p(c).edit().putFloat(K_NEVOA, v).apply()

    /** Código WMO equivalente ao nível de neve escolhido (casa com SceneState). */
    private fun weatherCodeNeve(nivel: Int) = when (nivel) {
        1 -> 71   // neve fraca  → presetFraca
        3 -> 75   // temporal    → presetTemporal
        else -> 73 // moderada   → presetForte
    }

    /** WeatherState sintético a partir dos valores forçados. */
    fun buildWeatherState(c: Context): WeatherState {
        val cond = condicao(c)
        return WeatherState(
            condition = cond,
            period = DayPeriod.AFTERNOON,
            temperatureCelsius = temp(c).toDouble(),
            feelsLikeCelsius = temp(c).toDouble(),
            description = "DEBUG",
            windspeedKmh = vento(c).toDouble(),
            humidity = 50,
            sunriseHour = 6.0f,
            sunsetHour = 18.5f,
            weatherCode = if (cond == WeatherCondition.SNOW) weatherCodeNeve(nivelNeve(c)) else 0,
        )
    }

    /** Aplica o override ao estado do motor (mesma pipeline do clima real). */
    fun aplicar(c: Context, estado: SceneState) {
        SceneState.aplicarClima(estado, buildWeatherState(c), Plano.isPremium(c))
        val nv = nevoa(c)
        if (nv >= 0f) estado.nevoa = nv
    }

    /** Hora a usar (respeita o override; senão o relógio real). */
    fun horaEfetiva(c: Context, real: Float): Float {
        val h = hora(c)
        return if (h >= 0f) h else real
    }
}
