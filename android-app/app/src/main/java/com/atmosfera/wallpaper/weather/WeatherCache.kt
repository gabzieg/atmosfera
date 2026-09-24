package com.atmosfera.wallpaper.weather

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import com.google.gson.Gson

/**
 * Cache leve em SharedPreferences para evitar chamadas desnecessárias à API.
 * O clima é re-buscado quando o dado vence (TTL) ou quando o aparelho se
 * deslocou o bastante. O TTL vem do intervalo escolhido pelo usuário
 * ([IntervaloClima.ttlMs]) — não é fixo, senão a opção de 15min não teria
 * efeito nenhum.
 */
class WeatherCache(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("atmosfera_weather_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_WEATHER_JSON = "weather_state"
        private const val KEY_LAST_FETCH = "last_fetch_ms"
        private const val KEY_LAT = "cached_lat"
        private const val KEY_LON = "cached_lon"
        private const val KEY_LUGAR = "cached_lugar"
        private const val KEY_PADRAO = "cached_local_padrao"
        private const val LOCATION_DELTA = 0.05           // ~5 km
    }

    /**
     * @param lugar nome resolvido pelo Geocoder. Só o app faz essa consulta; o
     *   serviço do wallpaper salva com `null` e aí o nome ANTERIOR é mantido —
     *   desde que o aparelho não tenha se deslocado, senão ele viraria mentira
     *   e é melhor não mostrar nome nenhum até o app resolver o novo.
     */
    fun save(state: WeatherState, lat: Double, lon: Double,
             lugar: String? = null, localPadrao: Boolean = false) {
        val nome = lugar ?: if (mesmoLugar(lat, lon)) prefs.getString(KEY_LUGAR, null) else null
        prefs.edit()
            .putString(KEY_WEATHER_JSON, gson.toJson(state))
            .putLong(KEY_LAST_FETCH, System.currentTimeMillis())
            .putFloat(KEY_LAT, lat.toFloat())
            .putFloat(KEY_LON, lon.toFloat())
            .putString(KEY_LUGAR, nome)
            .putBoolean(KEY_PADRAO, localPadrao)
            .apply()
    }

    private fun mesmoLugar(lat: Double, lon: Double): Boolean {
        if (prefs.getLong(KEY_LAST_FETCH, 0L) == 0L) return false
        val cLat = prefs.getFloat(KEY_LAT, 0f).toDouble()
        val cLon = prefs.getFloat(KEY_LON, 0f).toDouble()
        return Math.abs(lat - cLat) <= LOCATION_DELTA && Math.abs(lon - cLon) <= LOCATION_DELTA
    }

    /** Quando o clima em cache foi buscado (0 = nunca). */
    fun ultimaBuscaMs(): Long = prefs.getLong(KEY_LAST_FETCH, 0L)

    /** Nome do lugar da última busca ("Guarapuava, PR"), se o Geocoder deu. */
    fun lugar(): String? = prefs.getString(KEY_LUGAR, null)

    /** A última busca caiu nas coordenadas de fallback? */
    fun localPadrao(): Boolean = prefs.getBoolean(KEY_PADRAO, false)

    fun get(): WeatherState? {
        val json = prefs.getString(KEY_WEATHER_JSON, null) ?: return null
        return try {
            gson.fromJson(json, WeatherState::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * @param ttlMs por quanto tempo o dado conta como fresco. Vem do intervalo
     *   escolhido em Ajustes — ver [IntervaloClima.ttlMs].
     */
    fun isStale(lat: Double, lon: Double, ttlMs: Long): Boolean {
        val lastFetch = prefs.getLong(KEY_LAST_FETCH, 0L)
        if (lastFetch == 0L) return true // nunca cacheado

        val tooOld = System.currentTimeMillis() - lastFetch > ttlMs
        val cachedLat = prefs.getFloat(KEY_LAT, 0f).toDouble()
        val cachedLon = prefs.getFloat(KEY_LON, 0f).toDouble()
        val movedFar = Math.abs(lat - cachedLat) > LOCATION_DELTA ||
                       Math.abs(lon - cachedLon) > LOCATION_DELTA
        return tooOld || movedFar
    }

    fun clear() = prefs.edit().clear().apply()
}
