package com.terra.wallpaper.weather

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
    // Nascer/pôr do sol como hora fracionária local (ex.: 6.2 = 06:12).
    val sunriseHour: Float = 6.0f,
    val sunsetHour: Float = 18.5f,
    // Código WMO (p/ diferenciar intensidade, ex.: níveis de neve). A MET Norway
    // responde com `symbol_code`, traduzido por `simboloParaWmo`.
    val weatherCode: Int = 0,
    // Chuva estimada para o quarto de hora, em mm (a MET dá mm/hora; aqui é /4).
    val precipMm15: Double = 0.0,
    // De onde veio a condição: o `symbol_code` da MET, ou "nuvens" quando o
    // símbolo não foi reconhecido e o céu saiu da cobertura de nuvens. Só pra
    // diagnóstico no log — quando o wallpaper discorda do app de clima, essa
    // linha diz o que a API respondeu.
    val fonte: String? = null,
)
