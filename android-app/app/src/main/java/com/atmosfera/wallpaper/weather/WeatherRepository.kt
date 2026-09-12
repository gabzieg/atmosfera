package com.atmosfera.wallpaper.weather

import android.util.Log
import com.atmosfera.wallpaper.BuildConfig
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
import java.util.Locale
import java.util.concurrent.TimeUnit

// ─── Modelos de resposta Open-Meteo ──────────────────────────────────────────

data class OpenMeteoResponse(
    @SerializedName("current") val current: CurrentWeather,
    @SerializedName("current_units") val units: CurrentUnits? = null,
    @SerializedName("daily") val daily: DailyResponse? = null,
    @SerializedName("minutely_15") val minutely15: Minutely15Response? = null
)

/**
 * Bloco de 15 MINUTOS.
 *
 * MEDIDO em 03/09, contra o que eu supunha: o `current` do Open-Meteo NÃO é a
 * hora cheia — ele volta com `interval: 900` e o mesmo timestamp do bloco
 * corrente do `minutely_15`. Conferido em 16 pontos do globo, incluindo dois
 * com chuva na hora (Reiquiavique 51/0,10 mm e Buenos Aires 95/0,60 mm): código
 * e lâmina batem nos dois lados. Ou seja, ler daqui NÃO deixa o app mais rápido.
 *
 * O que esta leitura acrescenta de verdade é a **lâmina em mm** do quarto de
 * hora — que a chamada antiga nem pedia. O código WMO é uma categoria, e
 * categoria arredonda: chuva fina de 0,1 mm cabe num código de céu. Com a mm na
 * mão dá pra ligar a chuva pelo dado bruto e escolher a intensidade por ela, em
 * vez de depender de o modelo ter escolhido 61 em vez de 3.
 *
 * Fica também como cinto de segurança: se a API mudar o passo do `current`, o
 * bloco de 15 min continua sendo o dado mais novo. Pode não existir em toda
 * região, então tudo aqui é nulável e o `current` volta a mandar.
 */
data class Minutely15Response(
    @SerializedName("time") val time: List<String>? = null,
    @SerializedName("precipitation") val precipitation: List<Double?>? = null,
    @SerializedName("weather_code") val weatherCode: List<Int?>? = null
)

data class DailyResponse(
    @SerializedName("sunrise") val sunrise: List<String>? = null,
    @SerializedName("sunset") val sunset: List<String>? = null
)

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("apparent_temperature") val apparentTemperature: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("is_day") val isDay: Int,         // 1 = dia, 0 = noite
    // Lâmina de chuva do passo corrente (mm). Serve de fallback quando a região
    // não tem `minutely_15` — a mm liga a chuva que o código WMO arredondou.
    @SerializedName("precipitation") val precipitation: Double = 0.0,
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
        @Query("current") current: String = "temperature_2m,apparent_temperature,weather_code,wind_speed_10m,relative_humidity_2m,is_day,precipitation",
        @Query("minutely_15") minutely15: String = "precipitation,weather_code",
        @Query("past_minutely_15") pastMinutely15: Int = 1,
        @Query("forecast_minutely_15") forecastMinutely15: Int = 2,
        @Query("daily") daily: String = "sunrise,sunset",
        @Query("forecast_days") forecastDays: Int = 1,
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

/** O que o quarto de hora CORRENTE diz: código do tempo e chuva acumulada. */
data class Agora(val weatherCode: Int?, val precipMm: Double)

/**
 * Acha o bloco de 15 min que contém o instante `agoraLocal` ("2026-09-02T14:07"
 * → o bloco das 14:00).
 *
 * Os horários vêm em hora LOCAL (a chamada usa `timezone=auto`) e em formato
 * ISO de largura fixa, então comparar como texto ordena igual a comparar como
 * data — e evita `SimpleDateFormat` e fuso horário no meio do caminho. Pego o
 * ÚLTIMO bloco que já começou; pedir `past_minutely_15=1` garante que exista um
 * mesmo que a série comece adiante do relógio.
 */
fun blocoAtual(m: Minutely15Response?, agoraLocal: String): Agora? {
    val ts = m?.time ?: return null
    var idx = -1
    for (i in ts.indices) if (ts[i] <= agoraLocal) idx = i else break
    if (idx < 0) return null
    val code = m.weatherCode?.getOrNull(idx)
    val mm = m.precipitation?.getOrNull(idx) ?: 0.0
    return Agora(code, mm)
}

/** "2026-09-02T14:07" do relógio local, pra casar com o `time` da API. */
fun agoraIsoLocal(c: Calendar = Calendar.getInstance()): String = String.format(
    Locale.US, "%04d-%02d-%02dT%02d:%02d",
    c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE))

/** Código WMO de chuva pela intensidade do quarto de hora (mm no bloco). */
fun codigoPorChuva(mm: Double): Int = when {
    mm >= 1.5 -> 65   // > 6 mm/h — forte
    mm >= 0.4 -> 63   // moderada
    else -> 61        // fraca
}

/** Código WMO que significa precipitação caindo (garoa, chuva, neve, trovoada). */
fun Int.temPrecipitacao(): Boolean = this >= 51

fun getDayPeriod(isDay: Int): DayPeriod {
    if (isDay == 0) return DayPeriod.NIGHT
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return if (hour < 12) DayPeriod.MORNING else DayPeriod.AFTERNOON
}

/** Converte ISO local "2026-07-07T06:12" em hora fracionária (6.2). */
fun horaDeIso(iso: String?): Float? {
    if (iso == null) return null
    val t = iso.substringAfter('T', "")
    val partes = t.split(':')
    if (partes.size < 2) return null
    val h = partes[0].toIntOrNull() ?: return null
    val m = partes[1].take(2).toIntOrNull() ?: return null
    return h + m / 60f
}

// ─── Repositório ──────────────────────────────────────────────────────────────

class WeatherRepository {
    private val TAG = "WeatherRepository"

    private val api: OpenMeteoApi by lazy {
        // BASIC loga a URL da requisição (inclui lat/lon do usuário) — só em debug.
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
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

                // A ÁGUA MANDA NA CHUVA, não a categoria.
                // O código WMO é classificação, e classificação arredonda: uma
                // garoa de 0,1 mm cabe num código de céu encoberto, e aí o
                // wallpaper fica seco com chuva fina lá fora. A lâmina em mm do
                // quarto de hora é o dado bruto — se está caindo água, chove na
                // cena, e a intensidade sai da própria lâmina.
                //
                // Regra, na ordem:
                //  1. sem `minutely_15` (região sem esse passo): fica o current;
                //  2. o bloco acusa mm mas o código não classificou como chuva:
                //     a mm manda, e ela escolhe fraca/moderada/forte;
                //  3. sem mm: vale o código do bloco — inclusive pra DESLIGAR
                //     uma chuva que o código anterior ainda anunciava.
                val bloco = blocoAtual(response.minutely15, agoraIsoLocal())
                val codigoBloco = bloco?.weatherCode ?: current.weatherCode
                val mm = bloco?.precipMm ?: current.precipitation
                val codigoEfetivo =
                    if (mm >= 0.05 && !codigoBloco.temPrecipitacao()) codigoPorChuva(mm)
                    else codigoBloco
                val fonte = if (bloco == null) "modelo horário" else "modelo 15 min"
                val condition = codigoEfetivo.toWeatherCondition()

                // À noite, céu limpo = clear_night
                val finalCondition = if (current.isDay == 0 && condition == WeatherCondition.SUNNY)
                    WeatherCondition.CLEAR_NIGHT else condition

                val state = WeatherState(
                    condition = finalCondition,
                    period = getDayPeriod(current.isDay),
                    temperatureCelsius = current.temperature,
                    feelsLikeCelsius = current.apparentTemperature,
                    description = codigoEfetivo.toWeatherDescription(),
                    windspeedKmh = current.windSpeed,
                    humidity = current.humidity,
                    sunriseHour = horaDeIso(response.daily?.sunrise?.firstOrNull()) ?: 6.0f,
                    sunsetHour = horaDeIso(response.daily?.sunset?.firstOrNull()) ?: 18.5f,
                    weatherCode = codigoEfetivo,
                    precipMm15 = mm,
                    fonte = fonte,
                )
                Log.d(TAG, "Clima obtido: $state")
                Result.success(state)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao buscar clima: ${e.message}")
                Result.failure(e)
            }
        }
}
