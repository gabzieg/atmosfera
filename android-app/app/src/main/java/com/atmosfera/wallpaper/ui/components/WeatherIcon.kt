package com.atmosfera.wallpaper.ui.components

import com.atmosfera.wallpaper.weather.WeatherCondition

/**
 * Emoji por condição, só para a UI do companion app (não mexe no pacote
 * `weather`, que é responsabilidade do "front" mas tem contrato próprio).
 */
fun WeatherCondition.emoji(): String = when (this) {
    WeatherCondition.SUNNY -> "☀️"
    WeatherCondition.PARTLY_CLOUDY -> "⛅"
    WeatherCondition.CLOUDY -> "☁️"
    WeatherCondition.LIGHT_RAIN -> "🌦"
    WeatherCondition.HEAVY_RAIN -> "🌧"
    WeatherCondition.STORM -> "⛈"
    WeatherCondition.FOGGY -> "🌫"
    WeatherCondition.SNOW -> "❄️"
    WeatherCondition.CLEAR_NIGHT -> "🌙"
}
