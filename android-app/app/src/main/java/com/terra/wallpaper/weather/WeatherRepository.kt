package com.terra.wallpaper.weather

import android.content.Context
import android.util.Log
import com.terra.wallpaper.BuildConfig
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.math.sin

// ─── Fornecedor: MET Norway (Locationforecast 2.0) ───────────────────────────
//
// Trocado da Open-Meteo em 2026-10-03: a API gratuita da Open-Meteo é só para
// uso NÃO comercial, e o Terra vende Premium e cenários. A MET Norway (instituto
// meteorológico estatal da Noruega) libera uso comercial de graça, com quatro
// condições — todas atendidas aqui, não remova nenhuma sem reler os termos
// (https://api.met.no/doc/TermsOfService):
//   1. User-Agent identificando o app + contato ([USER_AGENT]); sem ele, 403.
//   2. Cache respeitando `Expires`/`Last-Modified` — o `Cache` do OkHttp faz
//      isso sozinho, inclusive o `If-Modified-Since`.
//   3. Coordenadas com no máximo 4 casas (usamos 2 — ver [coordenada]).
//   4. Atribuição CC BY 4.0 — nas páginas legais e na ajuda do app.
//
// Para não mexer no resto do app, a resposta é traduzida para o MESMO
// vocabulário de antes: código WMO + `WeatherState`. Mapeamento clima→cena,
// motor e testes de condição continuam valendo sem alteração.

// ─── Modelos de resposta (formato `compact`) ─────────────────────────────────
// Tudo nulável: o Gson ignora a nulidade do Kotlin, e um campo ausente na
// resposta viraria NullPointerException longe daqui.

data class MetResposta(
    @SerializedName("properties") val properties: MetPropriedades? = null,
)

data class MetPropriedades(
    @SerializedName("timeseries") val timeseries: List<MetPasso>? = null,
)

/** Um passo da série. `time` é UTC ("2026-10-04T00:00:00Z"), de hora em hora no começo. */
data class MetPasso(
    @SerializedName("time") val time: String? = null,
    @SerializedName("data") val data: MetDados? = null,
)

data class MetDados(
    @SerializedName("instant") val instant: MetInstante? = null,
    @SerializedName("next_1_hours") val proxima1h: MetPeriodo? = null,
    // Só no fim da série (além de ~2,5 dias) a próxima hora some e sobra a de 6 h.
    @SerializedName("next_6_hours") val proximas6h: MetPeriodo? = null,
)

data class MetInstante(
    @SerializedName("details") val details: MetDetalhes? = null,
)

data class MetDetalhes(
    @SerializedName("air_temperature") val temperatura: Double? = null,       // °C
    @SerializedName("relative_humidity") val umidade: Double? = null,         // %
    @SerializedName("wind_speed") val ventoMs: Double? = null,                // m/s
    @SerializedName("cloud_area_fraction") val nuvens: Double? = null,        // %
)

data class MetPeriodo(
    @SerializedName("summary") val summary: MetResumo? = null,
    @SerializedName("details") val details: MetPrecipitacao? = null,
)

data class MetResumo(
    @SerializedName("symbol_code") val simbolo: String? = null,
)

data class MetPrecipitacao(
    @SerializedName("precipitation_amount") val mm: Double? = null,
)

// ─── Retrofit interface ───────────────────────────────────────────────────────

interface MetNorwayApi {
    @GET("weatherapi/locationforecast/2.0/compact")
    suspend fun previsao(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
    ): MetResposta
}

// ─── Mapeamento de WMO Weather Code → WeatherCondition ───────────────────────
// O app pensa em código WMO (era o que a Open-Meteo devolvia); a MET responde
// com `symbol_code`, convertido por [simboloParaWmo].

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

/**
 * `symbol_code` da MET → código WMO equivalente. Lista de símbolos:
 * https://api.met.no/weatherapi/weathericon/2.0/documentation
 *
 * O símbolo é composto ("heavyrainshowersandthunder_night"), então a leitura é
 * por partes em vez de uma tabela com ~40 linhas: sufixo de período fora,
 * intensidade pelo prefixo, tipo pelo miolo. Dois desvios deliberados:
 *  - **sleet (chuva com neve) vira chuva**: o WMO não tem categoria própria e,
 *    nas regiões onde o app é usado, o que cai é mais água que neve;
 *  - **qualquer "thunder" vira 95**: o motor trata toda trovoada igual.
 * A MET tem dois erros de grafia históricos ("lightssleet…", "lightssnow…"),
 * cobertos pelo `startsWith("light")`.
 *
 * Símbolo desconhecido → null; quem chama decide o fallback.
 */
fun simboloParaWmo(simbolo: String?): Int? {
    if (simbolo.isNullOrBlank()) return null
    val s = simbolo.substringBefore('_')
    val nivel = when {
        s.startsWith("heavy") -> 2
        s.startsWith("light") -> 0
        else -> 1
    }
    val pancada = "showers" in s
    return when {
        s == "clearsky" -> 0
        s == "fair" -> 1
        s == "partlycloudy" -> 2
        s == "cloudy" -> 3
        s == "fog" -> 45
        "thunder" in s -> 95
        "snow" in s -> if (pancada) (if (nivel == 2) 86 else 85) else intArrayOf(71, 73, 75)[nivel]
        "rain" in s || "sleet" in s ->
            if (pancada) intArrayOf(80, 81, 82)[nivel] else intArrayOf(61, 63, 65)[nivel]
        else -> null
    }
}

/** Sem símbolo reconhecível: o céu sai da cobertura de nuvens (%), que é dado bruto. */
fun codigoPorNuvens(nuvens: Double?): Int = when {
    nuvens == null -> 3
    nuvens < 12.5 -> 0
    nuvens < 37.5 -> 1
    nuvens < 75.0 -> 2
    else -> 3
}

/** Código WMO de chuva pela intensidade do quarto de hora (mm no bloco). */
fun codigoPorChuva(mm: Double): Int = when {
    mm >= 1.5 -> 65   // > 6 mm/h — forte
    mm >= 0.4 -> 63   // moderada
    else -> 61        // fraca
}

/** Código WMO que significa precipitação caindo (garoa, chuva, neve, trovoada). */
fun Int.temPrecipitacao(): Boolean = this >= 51

/**
 * A ÁGUA MANDA NA CHUVA, não a categoria (regra de 03/09, mantida na troca de
 * fornecedor). O símbolo é classificação, e classificação arredonda: uma garoa
 * fina cabe num símbolo de céu encoberto, e aí o wallpaper fica seco com chuva
 * lá fora. Se a lâmina diz que cai água, chove na cena, e a intensidade sai da
 * própria lâmina. Sem água, vale o código — inclusive pra manter trovoada/neve,
 * que a lâmina não pode rebaixar.
 */
fun codigoEfetivo(codigo: Int, mm15: Double): Int =
    if (mm15 >= 0.05 && !codigo.temPrecipitacao()) codigoPorChuva(mm15) else codigo

/**
 * O passo que vale AGORA: o último que já começou. A série vem em ordem e em
 * UTC; a MET costuma começar pela hora corrente, mas se começar adiante do
 * relógio (relógio do aparelho atrasado), o primeiro passo é o melhor que há.
 */
fun passoAtual(serie: List<MetPasso>?, agora: Instant): MetPasso? {
    if (serie.isNullOrEmpty()) return null
    var atual: MetPasso? = null
    for (p in serie) {
        val t = p.time?.let { runCatching { Instant.parse(it) }.getOrNull() } ?: continue
        if (t <= agora) atual = p else break
    }
    return atual ?: serie.first()
}

/**
 * Sensação térmica pela fórmula de Steadman usada pelo Bureau of Meteorology
 * australiano (a mesma família da `apparent_temperature` que a Open-Meteo
 * entregava pronta). A MET não fornece esse campo.
 */
fun sensacaoTermica(tempC: Double, umidade: Double?, ventoMs: Double?): Double {
    val rh = umidade ?: return tempC
    val vapor = rh / 100.0 * 6.105 * exp(17.27 * tempC / (237.7 + tempC))
    return tempC + 0.33 * vapor - 0.70 * (ventoMs ?: 0.0) - 4.00
}

/**
 * Nascer e pôr do sol (hora fracionária no fuso `zona`) pela equação do nascer
 * do sol (aproximação do NOAA, erro de ~1–2 min). Calculado no aparelho: a
 * Open-Meteo mandava isso pronto, a MET tem endpoint separado — e uma segunda
 * chamada só pra isso seria mais uma requisição levando a localização.
 *
 * Devolve null em dia/noite polar (o sol não cruza o horizonte).
 */
fun nascerEPorDoSol(lat: Double, lon: Double, data: LocalDate, zona: ZoneId): Pair<Float, Float>? {
    val rad = Math.PI / 180.0
    val n = (data.toEpochDay() - 10957L).toDouble()              // dias desde 2000-01-01
    val jEstrela = n - lon / 360.0                                // meio-dia solar médio
    val m = (357.5291 + 0.98560028 * jEstrela).mod(360.0)
    val c = 1.9148 * sin(m * rad) + 0.02 * sin(2 * m * rad) + 0.0003 * sin(3 * m * rad)
    val lambda = (m + c + 180.0 + 102.9372).mod(360.0)
    val jTransito = 2451545.0 + jEstrela + 0.0053 * sin(m * rad) - 0.0069 * sin(2 * lambda * rad)
    val declinacao = asin(sin(lambda * rad) * sin(23.4397 * rad))
    val cosOmega = (sin(-0.833 * rad) - sin(lat * rad) * sin(declinacao)) /
        (cos(lat * rad) * cos(declinacao))
    if (cosOmega < -1.0 || cosOmega > 1.0) return null
    val omega = acos(cosOmega) / rad
    fun horaLocal(juliano: Double): Float {
        val ms = ((juliano - 2440587.5) * 86_400_000.0).toLong()
        val z = ZonedDateTime.ofInstant(Instant.ofEpochMilli(ms), zona)
        return z.hour + z.minute / 60f + z.second / 3600f
    }
    return horaLocal(jTransito - omega / 360.0) to horaLocal(jTransito + omega / 360.0)
}

/**
 * É dia? Pelo sol calculado; em dia/noite polar (sem nascer/pôr), pelo sufixo
 * do símbolo, que a MET já dá por período; sem nenhum dos dois, 6h–18h.
 */
fun ehDia(hora: Float, sol: Pair<Float, Float>?, simbolo: String?): Boolean = when {
    sol != null -> hora >= sol.first && hora < sol.second
    simbolo?.endsWith("_day") == true -> true
    simbolo?.endsWith("_night") == true || simbolo?.endsWith("_polartwilight") == true -> false
    else -> hora in 6f..18f
}

fun getDayPeriod(dia: Boolean, hora: Float): DayPeriod = when {
    !dia -> DayPeriod.NIGHT
    hora < 12f -> DayPeriod.MORNING
    else -> DayPeriod.AFTERNOON
}

/**
 * Duas casas decimais (~1,1 km). A MET aceita no máximo 4 (5+ dá 403); 2 basta
 * para o clima — a grade do modelo é bem mais larga que isso — e manda menos
 * da posição do usuário para fora do aparelho. Também melhora o acerto do
 * cache da própria MET.
 */
fun coordenada(x: Double): Double = (x * 100.0).roundToInt() / 100.0

// ─── Repositório ──────────────────────────────────────────────────────────────

class WeatherRepository(context: Context) {
    private val TAG = "WeatherRepository"
    private val api: MetNorwayApi = apiCompartilhada(context)

    suspend fun fetchWeather(latitude: Double, longitude: Double): Result<WeatherState> =
        withContext(Dispatchers.IO) {
            try {
                val lat = coordenada(latitude)
                val lon = coordenada(longitude)
                val resposta = api.previsao(lat, lon)
                val passo = passoAtual(resposta.properties?.timeseries, Instant.now())
                    ?: error("resposta sem série temporal")
                val dados = passo.data ?: error("passo sem dados")
                val detalhes = dados.instant?.details ?: error("passo sem medições")
                val temperatura = detalhes.temperatura ?: error("passo sem temperatura")

                val simbolo = (dados.proxima1h ?: dados.proximas6h)?.summary?.simbolo
                // A MET dá mm por HORA; o app raciocina em quarto de hora (a régua
                // de `codigoPorChuva` foi calibrada assim), então divide por 4.
                val mmHora = dados.proxima1h?.details?.mm ?: 0.0
                val mm15 = mmHora / 4.0
                val codigo = codigoEfetivo(
                    simboloParaWmo(simbolo) ?: codigoPorNuvens(detalhes.nuvens), mm15)

                val zona = ZoneId.systemDefault()
                val agoraLocal = ZonedDateTime.now(zona)
                val hora = agoraLocal.hour + agoraLocal.minute / 60f
                val sol = nascerEPorDoSol(lat, lon, agoraLocal.toLocalDate(), zona)
                val dia = ehDia(hora, sol, simbolo)

                val condition = codigo.toWeatherCondition()
                // À noite, céu limpo = clear_night
                val finalCondition = if (!dia && condition == WeatherCondition.SUNNY)
                    WeatherCondition.CLEAR_NIGHT else condition

                val state = WeatherState(
                    condition = finalCondition,
                    period = getDayPeriod(dia, hora),
                    temperatureCelsius = temperatura,
                    feelsLikeCelsius = sensacaoTermica(temperatura, detalhes.umidade, detalhes.ventoMs),
                    description = codigo.toWeatherDescription(),
                    windspeedKmh = (detalhes.ventoMs ?: 0.0) * 3.6,
                    humidity = detalhes.umidade?.roundToInt() ?: 0,
                    sunriseHour = sol?.first ?: 6.0f,
                    sunsetHour = sol?.second ?: 18.5f,
                    weatherCode = codigo,
                    precipMm15 = mm15,
                    fonte = "MET Norway · ${simbolo ?: "nuvens"}",
                )
                Log.d(TAG, "Clima obtido: $state")
                Result.success(state)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao buscar clima: ${e.message}")
                Result.failure(e)
            }
        }

    companion object {
        /**
         * Exigido pela MET: nome do app + contato. Sem isso a API responde 403.
         * O e-mail é o de suporte público (o mesmo da ficha da Play Store).
         */
        const val CONTATO = "suporteterrabr@gmail.com"
        val USER_AGENT = "Terra/${BuildConfig.VERSION_NAME} (${BuildConfig.APPLICATION_ID}; $CONTATO)"

        /**
         * UM cliente por processo. O serviço do wallpaper, a tela e o worker
         * criam cada um o seu `WeatherRepository`; se cada um abrisse o próprio
         * `Cache` do OkHttp na mesma pasta, os journals brigariam — o OkHttp
         * exige uma única instância por diretório.
         */
        @Volatile private var compartilhada: MetNorwayApi? = null

        private fun apiCompartilhada(context: Context): MetNorwayApi =
            compartilhada ?: synchronized(this) {
                compartilhada ?: criarApi(context.applicationContext).also { compartilhada = it }
            }

        private fun criarApi(app: Context): MetNorwayApi {
            // BASIC loga a URL da requisição (inclui lat/lon do usuário) — só em debug.
            val logging = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
            }
            val client = OkHttpClient.Builder()
                .cache(Cache(File(app.cacheDir, "clima-met"), 512L * 1024))
                .addInterceptor { chain ->
                    chain.proceed(chain.request().newBuilder().header("User-Agent", USER_AGENT).build())
                }
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://api.met.no/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(MetNorwayApi::class.java)
        }
    }
}
