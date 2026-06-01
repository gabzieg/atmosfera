package com.atmosfera.wallpaper.weather

import android.util.Log
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Calendar
import java.util.concurrent.TimeUnit

// ─── Modelos de resposta Open-Meteo ──────────────────────────────────────────

data class OpenMeteoResponse(
    @SerializedName("current") val current: CurrentWeather,
    @SerializedName("current_units") val units: CurrentUnits? = null
)

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("apparent_temperature") val apparentTemperature: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("is_day") val isDay: Int,         // 1 = dia, 0 = noite
)

data class CurrentUnits(
    @SerializedName("temperature_2m") val temperatureUnit: String = "°C"
)

// ─── Retrofit interface ───────────────────────────────────────────────────────

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,apparent_temperature,weather_code,wind_speed_10m,relative_humidity_2m,is_day",
        @Query("wind_speed_unit") windSpeedUnit: String = "kmh",
        @Query("timezone") timezone: String = "auto",
    ): OpenMeteoResponse
}

// ─── Mapeamento de WMO Weather Code → WeatherCondition ───────────────────────
// Referência: https://open-meteo.com/en/docs#weathervariables

fun Int.toWeatherCondition(): WeatherCondition = when (this) {
    0 -> WeatherCondition.SUNNY           // Clear sky
    1 -> WeatherCondition.SUNNY           // Mainly clear
    2 -> WeatherCondition.PARTLY_CLOUDY   // Partly cloudy
    3 -> WeatherCondition.CLOUDY          // Overcast
    45, 48 -> WeatherCondition.FOGGY      // Fog
    51, 53 -> WeatherCondition.LIGHT_RAIN // Drizzle light/moderate
    55 -> WeatherCondition.HEAVY_RAIN     // Drizzle dense
    61, 63 -> WeatherCondition.LIGHT_RAIN // Rain slight/moderate
    65 -> WeatherCondition.HEAVY_RAIN     // Rain heavy
    71, 73 -> WeatherCondition.SNOW       // Snow fall slight/moderate
    75, 77 -> WeatherCondition.SNOW       // Snow fall heavy / snow grains
    80, 81 -> WeatherCondition.LIGHT_RAIN // Rain showers slight/moderate
    82 -> WeatherCondition.HEAVY_RAIN     // Rain showers violent
    85, 86 -> WeatherCondition.SNOW       // Snow showers
    95 -> WeatherCondition.STORM          // Thunderstorm slight/moderate
    96, 99 -> WeatherCondition.STORM      // Thunderstorm with hail
    else -> WeatherCondition.CLOUDY
}

fun Int.toWeatherDescription(): String = when (this) {
    0 -> "Céu limpo"
    1 -> "Principalmente limpo"
    2 -> "Parcialmente nublado"
    3 -> "Nublado"
    45, 48 -> "Neblina"
    51, 53, 55 -> "Garoa"
    61, 63 -> "Chuva fraca"
    65 -> "Chuva forte"
    71, 73, 75, 77 -> "Neve"
    80, 81 -> "Pancadas de chuva"
    82 -> "Chuva intensa"
    85, 86 -> "Neve"
    95 -> "Trovoada"
    96, 99 -> "Trovoada com granizo"
    else -> "Condição desconhecida"
}

fun getDayPeriod(isDay: Int): DayPeriod {
    if (isDay == 0) return DayPeriod.NIGHT
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return if (hour < 12) DayPeriod.MORNING else DayPeriod.AFTERNOON
}

// ─── Repositório ──────────────────────────────────────────────────────────────

class WeatherRepository {
    private val TAG = "WeatherRepository"

    private val api: OpenMeteoApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(OpenMeteoApi::class.java)
    }

    suspend fun fetchWeather(latitude: Double, longitude: Double): Result<WeatherState> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getCurrentWeather(latitude, longitude)
                val current = response.current
                val condition = current.weatherCode.toWeatherCondition()

                // À noite, céu limpo = clear_night
                val finalCondition = if (current.isDay == 0 && condition == WeatherCondition.SUNNY)
                    WeatherCondition.CLEAR_NIGHT else condition

                val state = WeatherState(
                    condition = finalCondition,
                    period = getDayPeriod(current.isDay),
                    temperatureCelsius = current.temperature,
                    feelsLikeCelsius = current.apparentTemperature,
                    description = current.weatherCode.toWeatherDescription(),
                    windspeedKmh = current.windSpeed,
                    humidity = current.humidity,
                )
                Log.d(TAG, "Clima obtido: $state")
                Result.success(state)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao buscar clima: ${e.message}")
                Result.failure(e)
            }
        }
}
