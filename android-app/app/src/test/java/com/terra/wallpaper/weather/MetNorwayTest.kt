package com.terra.wallpaper.weather

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Tradução da resposta da MET Norway para o vocabulário do app (2026-10-03).
 *
 * O ponto frágil de trocar de fornecedor é o que compila e mesmo assim erra:
 * nome de campo do JSON trocado (o Gson devolve null calado), símbolo mapeado
 * para a categoria errada, nascer do sol fora do lugar. Por isso a fixture é
 * uma resposta REAL (São Paulo, 04/10/2026 00:49 UTC, recortada em 4 passos),
 * e os horários de sol esperados são os do próprio endpoint `sunrise/3.0` da
 * MET. Dados MET Norway, licença CC BY 4.0.
 */
class MetNorwayTest {

    private fun fixture(): MetResposta {
        val json = javaClass.classLoader!!.getResource("met/compact-sao-paulo.json")!!.readText()
        return Gson().fromJson(json, MetResposta::class.java)
    }

    // ── JSON real → modelos ──────────────────────────────────────────────────

    @Test
    fun `resposta real preenche os campos que o app usa`() {
        val serie = fixture().properties?.timeseries
        assertNotNull(serie)
        assertEquals(4, serie!!.size)
        val p = serie[0]
        assertEquals("2026-10-04T00:00:00Z", p.time)
        val d = p.data!!.instant!!.details!!
        assertEquals(18.3, d.temperatura!!, 0.001)
        assertEquals(93.5, d.umidade!!, 0.001)
        assertEquals(2.9, d.ventoMs!!, 0.001)
        assertEquals(89.1, d.nuvens!!, 0.001)
        assertEquals("cloudy", p.data!!.proxima1h!!.summary!!.simbolo)
        assertEquals(0.0, p.data!!.proxima1h!!.details!!.mm!!, 0.001)
        // terceiro passo: pancada com 0,3 mm na hora
        assertEquals("rainshowers_night", serie[2].data!!.proxima1h!!.summary!!.simbolo)
        assertEquals(0.3, serie[2].data!!.proxima1h!!.details!!.mm!!, 0.001)
    }

    @Test
    fun `fim da serie nao tem proxima hora e nao quebra`() {
        val ultimo = fixture().properties!!.timeseries!!.last()
        assertNull(ultimo.data!!.proxima1h)
        assertNull(ultimo.data!!.proximas6h)
        assertNotNull(ultimo.data!!.instant!!.details!!.temperatura)
    }

    // ── Passo corrente ───────────────────────────────────────────────────────

    @Test
    fun `passo atual e o ultimo que ja comecou`() {
        val serie = fixture().properties!!.timeseries
        val p = passoAtual(serie, Instant.parse("2026-10-04T01:30:00Z"))
        assertEquals("2026-10-04T01:00:00Z", p!!.time)
    }

    @Test
    fun `no instante exato vale o proprio passo`() {
        val serie = fixture().properties!!.timeseries
        assertEquals("2026-10-04T02:00:00Z", passoAtual(serie, Instant.parse("2026-10-04T02:00:00Z"))!!.time)
    }

    @Test
    fun `relogio atrasado em relacao a serie pega o primeiro passo`() {
        val serie = fixture().properties!!.timeseries
        assertEquals("2026-10-04T00:00:00Z", passoAtual(serie, Instant.parse("2026-10-03T20:00:00Z"))!!.time)
    }

    @Test
    fun `serie vazia nao tem passo`() {
        assertNull(passoAtual(null, Instant.now()))
        assertNull(passoAtual(emptyList(), Instant.now()))
    }

    // ── symbol_code → WMO ────────────────────────────────────────────────────

    @Test
    fun `ceu e nuvens`() {
        assertEquals(0, simboloParaWmo("clearsky_day"))
        assertEquals(0, simboloParaWmo("clearsky_night"))
        assertEquals(1, simboloParaWmo("fair_polartwilight"))
        assertEquals(2, simboloParaWmo("partlycloudy_day"))
        assertEquals(3, simboloParaWmo("cloudy"))
        assertEquals(45, simboloParaWmo("fog"))
    }

    @Test
    fun `chuva continua e pancada por intensidade`() {
        assertEquals(61, simboloParaWmo("lightrain"))
        assertEquals(63, simboloParaWmo("rain"))
        assertEquals(65, simboloParaWmo("heavyrain"))
        assertEquals(80, simboloParaWmo("lightrainshowers_day"))
        assertEquals(81, simboloParaWmo("rainshowers_night"))
        assertEquals(82, simboloParaWmo("heavyrainshowers_day"))
    }

    @Test
    fun `chuva com neve vira chuva`() {
        assertEquals(61, simboloParaWmo("lightsleet"))
        assertEquals(65, simboloParaWmo("heavysleet"))
        assertEquals(81, simboloParaWmo("sleetshowers_day"))
    }

    @Test
    fun `neve por intensidade`() {
        assertEquals(71, simboloParaWmo("lightsnow"))
        assertEquals(73, simboloParaWmo("snow"))
        assertEquals(75, simboloParaWmo("heavysnow"))
        assertEquals(85, simboloParaWmo("snowshowers_day"))
        assertEquals(86, simboloParaWmo("heavysnowshowers_night"))
    }

    @Test
    fun `toda trovoada vira 95, inclusive as grafias erradas da MET`() {
        assertEquals(95, simboloParaWmo("rainandthunder"))
        assertEquals(95, simboloParaWmo("heavyrainshowersandthunder_day"))
        assertEquals(95, simboloParaWmo("snowandthunder"))
        assertEquals(95, simboloParaWmo("lightssleetshowersandthunder_night"))
        assertEquals(95, simboloParaWmo("lightssnowshowersandthunder_day"))
    }

    @Test
    fun `simbolo desconhecido ou ausente devolve null e o ceu sai das nuvens`() {
        assertNull(simboloParaWmo(null))
        assertNull(simboloParaWmo(""))
        assertNull(simboloParaWmo("tornado"))
        assertEquals(0, codigoPorNuvens(5.0))
        assertEquals(1, codigoPorNuvens(30.0))
        assertEquals(2, codigoPorNuvens(53.1))
        assertEquals(3, codigoPorNuvens(89.1))
        assertEquals(3, codigoPorNuvens(null))
    }

    @Test
    fun `cada simbolo cai numa condicao que o motor desenha`() {
        assertEquals(WeatherCondition.CLOUDY, simboloParaWmo("cloudy")!!.toWeatherCondition())
        assertEquals(WeatherCondition.LIGHT_RAIN, simboloParaWmo("rainshowers_night")!!.toWeatherCondition())
        assertEquals(WeatherCondition.HEAVY_RAIN, simboloParaWmo("heavyrain")!!.toWeatherCondition())
        assertEquals(WeatherCondition.SNOW, simboloParaWmo("snow")!!.toWeatherCondition())
        assertEquals(WeatherCondition.STORM, simboloParaWmo("rainandthunder")!!.toWeatherCondition())
        assertEquals(WeatherCondition.FOGGY, simboloParaWmo("fog")!!.toWeatherCondition())
    }

    // ── Sol, dia/noite ───────────────────────────────────────────────────────

    private fun minutos(h: Float) = h * 60.0

    @Test
    fun `nascer e por do sol batem com o endpoint da MET em Sao Paulo`() {
        // MET sunrise/3.0: 05:44 e 18:06 (-03:00) em 03/10/2026.
        val sol = nascerEPorDoSol(-23.55, -46.63, LocalDate.of(2026, 10, 3), ZoneId.of("America/Sao_Paulo"))
        assertNotNull(sol)
        assertEquals((5 * 60 + 44).toDouble(), minutos(sol!!.first), 3.0)
        assertEquals((18 * 60 + 6).toDouble(), minutos(sol.second), 3.0)
    }

    @Test
    fun `nascer e por do sol batem com o endpoint da MET em Londres no solsticio`() {
        // MET sunrise/3.0: 04:43 e 21:21 (+01:00) em 21/06/2026.
        val sol = nascerEPorDoSol(51.5, -0.13, LocalDate.of(2026, 6, 21), ZoneId.of("Europe/London"))
        assertEquals((4 * 60 + 43).toDouble(), minutos(sol!!.first), 3.0)
        assertEquals((21 * 60 + 21).toDouble(), minutos(sol.second), 3.0)
    }

    @Test
    fun `sol da meia-noite nao tem nascer nem por`() {
        assertNull(nascerEPorDoSol(69.65, 18.96, LocalDate.of(2026, 6, 21), ZoneId.of("Europe/Oslo")))
    }

    @Test
    fun `dia e noite pelo sol, e pelo simbolo quando nao ha sol calculavel`() {
        val sol = 5.7f to 18.1f
        assertTrue(ehDia(12f, sol, "cloudy"))
        assertFalse(ehDia(5.5f, sol, "clearsky_day"))   // o sol calculado manda
        assertFalse(ehDia(18.1f, sol, null))
        assertTrue(ehDia(2f, null, "clearsky_day"))     // sol da meia-noite
        assertFalse(ehDia(13f, null, "fair_polartwilight"))
        assertFalse(ehDia(13f, null, "clearsky_night")) // noite polar
        assertTrue(ehDia(13f, null, "cloudy"))          // sem pista: 6h–18h
        assertEquals(DayPeriod.NIGHT, getDayPeriod(false, 13f))
        assertEquals(DayPeriod.MORNING, getDayPeriod(true, 9f))
        assertEquals(DayPeriod.AFTERNOON, getDayPeriod(true, 15f))
    }

    // ── Sensação térmica e coordenadas ───────────────────────────────────────

    @Test
    fun `sensacao termica de Steadman`() {
        // 25 °C, 50 %, 2 m/s → 24,8 °C (conta feita à mão com a fórmula do BoM)
        assertEquals(24.8, sensacaoTermica(25.0, 50.0, 2.0), 0.1)
        // calor úmido sem vento esquenta
        assertTrue(sensacaoTermica(32.0, 80.0, 0.0) > 32.0)
        // sem umidade, não inventa: devolve a temperatura
        assertEquals(18.3, sensacaoTermica(18.3, null, 2.9), 0.0)
    }

    @Test
    fun `coordenada sai com no maximo duas casas`() {
        assertEquals(-23.55, coordenada(-23.5505199), 0.0)
        assertEquals(-46.63, coordenada(-46.6333094), 0.0)
        assertEquals("-46.63", coordenada(-46.6333094).toString())
        assertEquals(0.01, coordenada(0.0149), 0.0)
    }
}
