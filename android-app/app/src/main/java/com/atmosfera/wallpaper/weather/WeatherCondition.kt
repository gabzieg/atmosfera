package com.atmosfera.wallpaper.weather

/**
 * Representa todas as condições climáticas mapeadas às imagens do pack.
 * O nome do arquivo de asset segue o padrão: {condition}_{period}.webp
 */
enum class WeatherCondition(val code: String) {
    SUNNY("sunny"),
    PARTLY_CLOUDY("partly_cloudy"),
    CLOUDY("cloudy"),
    LIGHT_RAIN("light_rain"),
    HEAVY_RAIN("heavy_rain"),
    STORM("storm"),
    FOGGY("foggy"),
    SNOW("snow"),
    CLEAR_NIGHT("clear_night");
}

enum class DayPeriod(val code: String) {
    MORNING("morning"),
    AFTERNOON("afternoon"),
    NIGHT("night");
}

data class WeatherState(
    val condition: WeatherCondition,
    val period: DayPeriod,
    val temperatureCelsius: Double,
    val feelsLikeCelsius: Double,
    val description: String,
    val windspeedKmh: Double = 0.0,
    val humidity: Int = 0,
)

/**
 * Retorna o nome do arquivo de asset correspondente, ex: "sunny_morning.webp"
 * Lida com combinações especiais como clear_night que só existe à noite.
 */
fun WeatherState.assetFileName(): String {
    val cond = when {
        condition == WeatherCondition.SUNNY && period == DayPeriod.NIGHT -> WeatherCondition.CLEAR_NIGHT
        condition == WeatherCondition.CLEAR_NIGHT && period != DayPeriod.NIGHT -> WeatherCondition.PARTLY_CLOUDY
        else -> condition
    }
    val per = when {
        cond == WeatherCondition.CLEAR_NIGHT -> "night"
        else -> period.code
    }
    // Foggy não tem versão noturna → usa afternoon
    val safePeriod = if (cond == WeatherCondition.FOGGY && per == "night") "afternoon" else per
    return "wallpapers/${cond.code}_${safePeriod}.webp"
}
