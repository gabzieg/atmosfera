package com.atmosfera.wallpaper.weather

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import com.google.gson.Gson

/**
 * Cache leve em SharedPreferences para evitar chamadas desnecessárias à API.
 * O clima é re-buscado apenas se passaram mais de 30 minutos desde a última atualização.
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
        private const val CACHE_TTL_MS = 30 * 60 * 1000L // 30 minutos
        private const val LOCATION_DELTA = 0.05           // ~5 km
    }

    fun save(state: WeatherState, lat: Double, lon: Double) {
        prefs.edit()
            .putString(KEY_WEATHER_JSON, gson.toJson(state))
            .putLong(KEY_LAST_FETCH, System.currentTimeMillis())
            .putFloat(KEY_LAT, lat.toFloat())
            .putFloat(KEY_LON, lon.toFloat())
            .apply()
    }

    fun get(): WeatherState? {
        val json = prefs.getString(KEY_WEATHER_JSON, null) ?: return null
        return try {
            gson.fromJson(json, WeatherState::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun isStale(lat: Double, lon: Double): Boolean {
        val lastFetch = prefs.getLong(KEY_LAST_FETCH, 0L)
        val cachedLat = prefs.getFloat(KEY_LAT, Float.MIN_VALUE).toDouble()
        val cachedLon = prefs.getFloat(KEY_LON, Float.MIN_VALUE).toDouble()

        val tooOld = System.currentTimeMillis() - lastFetch > CACHE_TTL_MS
        val movedFar = Math.abs(lat - cachedLat) > LOCATION_DELTA ||
                       Math.abs(lon - cachedLon) > LOCATION_DELTA
        return tooOld || movedFar
    }

    fun clear() = prefs.edit().clear().apply()
}
