package com.atmosfera.wallpaper.weather

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Primeiros testes do projeto. Cobrem a tradução do código WMO cru da Open-Meteo
 * para o domínio do app — lógica pura, sem Android, o ponto onde um número errado
 * passa despercebido (o `assembleDebug` compila mesmo com o mapa trocado).
 */
class WeatherMappingTest {

    @Test
    fun `codigo WMO mapeia para a condicao certa`() {
        // Céu / nuvens
        assertEquals(WeatherCondition.SUNNY, 0.toWeatherCondition())
        assertEquals(WeatherCondition.SUNNY, 1.toWeatherCondition())
        assertEquals(WeatherCondition.PARTLY_CLOUDY, 2.toWeatherCondition())
        assertEquals(WeatherCondition.CLOUDY, 3.toWeatherCondition())
        // Névoa
        assertEquals(WeatherCondition.FOGGY, 45.toWeatherCondition())
        assertEquals(WeatherCondition.FOGGY, 48.toWeatherCondition())
        // Chuva fraca vs forte
        assertEquals(WeatherCondition.LIGHT_RAIN, 51.toWeatherCondition())
        assertEquals(WeatherCondition.LIGHT_RAIN, 61.toWeatherCondition())
        assertEquals(WeatherCondition.LIGHT_RAIN, 80.toWeatherCondition())
        assertEquals(WeatherCondition.HEAVY_RAIN, 55.toWeatherCondition())
        assertEquals(WeatherCondition.HEAVY_RAIN, 65.toWeatherCondition())
        assertEquals(WeatherCondition.HEAVY_RAIN, 82.toWeatherCondition())
        // Neve (todas as faixas)
        assertEquals(WeatherCondition.SNOW, 71.toWeatherCondition())
        assertEquals(WeatherCondition.SNOW, 77.toWeatherCondition())
        assertEquals(WeatherCondition.SNOW, 85.toWeatherCondition())
        assertEquals(WeatherCondition.SNOW, 86.toWeatherCondition())
        // Tempestade
        assertEquals(WeatherCondition.STORM, 95.toWeatherCondition())
        assertEquals(WeatherCondition.STORM, 96.toWeatherCondition())
        assertEquals(WeatherCondition.STORM, 99.toWeatherCondition())
    }

    @Test
    fun `codigo WMO desconhecido cai em CLOUDY`() {
        assertEquals(WeatherCondition.CLOUDY, 7.toWeatherCondition())
        assertEquals(WeatherCondition.CLOUDY, (-1).toWeatherCondition())
        assertEquals(WeatherCondition.CLOUDY, 100.toWeatherCondition())
    }

    @Test
    fun `descricao acompanha o codigo e tem fallback`() {
        assertEquals("Céu limpo", 0.toWeatherDescription())
        assertEquals("Nublado", 3.toWeatherDescription())
        assertEquals("Neve", 75.toWeatherDescription())
        assertEquals("Trovoada com granizo", 99.toWeatherDescription())
        assertEquals("Condição desconhecida", 12345.toWeatherDescription())
    }

    @Test
    fun `horaDeIso extrai hora fracionaria do ISO`() {
        assertEquals(6.2f, horaDeIso("2026-07-07T06:12")!!, 0.001f)
        assertEquals(18.0f, horaDeIso("2026-07-07T18:00")!!, 0.001f)
        assertEquals(0.5f, horaDeIso("2026-01-01T00:30")!!, 0.001f)
    }

    @Test
    fun `horaDeIso devolve null em entrada invalida`() {
        assertNull(horaDeIso(null))
        assertNull(horaDeIso("2026-07-07"))        // sem parte de hora (sem 'T')
        assertNull(horaDeIso("2026-07-07T"))       // 'T' mas nada depois
        assertNull(horaDeIso("2026-07-07Txx:yy"))  // hora não numérica
    }
}
