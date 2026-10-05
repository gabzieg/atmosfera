package com.terra.wallpaper.weather

import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import retrofit2.HttpException
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * As condições dos termos da MET Norway, provadas contra um servidor HTTP falso
 * (https://api.met.no/doc/TermsOfService). Sem isto, tudo o que o app promete à
 * MET — identificar-se, truncar coordenadas, não repetir pedido antes do
 * `Expires`, parar em 429 — ficaria só em comentário.
 */
class MetHttpTest {

    @get:Rule val pasta = TemporaryFolder()

    private lateinit var servidor: MockWebServer
    private lateinit var repo: WeatherRepository
    private var relogio = 5_000_000L

    private class ArmazemFalso : FreioMet.Armazem {
        override var ateMs = 0L
        override var falhasSeguidas = 0
    }
    private val freio = FreioMet(ArmazemFalso()) { relogio }

    private val corpo: String by lazy {
        javaClass.classLoader!!.getResource("met/compact-sao-paulo.json")!!.readText()
    }

    private fun rfc1123(minutosAFrente: Long) = DateTimeFormatter.RFC_1123_DATE_TIME
        .format(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(minutosAFrente))

    private fun respostaOk() = MockResponse()
        .setHeader("Content-Type", "application/json")
        .setHeader("Last-Modified", rfc1123(-10))
        .setHeader("Expires", rfc1123(30))      // como a API real: ~30 min de validade
        .setBody(corpo)

    @Before fun subir() {
        servidor = MockWebServer().also { it.start() }
        val api = WeatherRepository.criarApi(
            pasta.newFolder("cache"), servidor.url("/").toString(), "Terra-Teste/9.9 (teste; contato@exemplo.com)")
        repo = WeatherRepository(api, freio)
    }

    @After fun descer() { servidor.shutdown() }

    private fun buscar() = runBlocking { repo.fetchWeather(-23.5505199, -46.6333094) }

    @Test
    fun `identifica o app e manda coordenadas com 2 casas`() {
        servidor.enqueue(respostaOk())
        val r = buscar()
        assertTrue("falhou: ${r.exceptionOrNull()}", r.isSuccess)

        val req = servidor.takeRequest()
        assertEquals("Terra-Teste/9.9 (teste; contato@exemplo.com)", req.getHeader("User-Agent"))
        assertTrue(req.path!!.startsWith("/weatherapi/locationforecast/2.0/compact?"))
        assertEquals("-23.55", req.requestUrl!!.queryParameter("lat"))
        assertEquals("-46.63", req.requestUrl!!.queryParameter("lon"))
        // A MET devolve 403 acima de 4 casas decimais; ficar em 2 é folga.
        assertFalse(req.path!!.contains("5199"))
    }

    @Test
    fun `nao repete o pedido antes do Expires`() {
        servidor.enqueue(respostaOk())
        assertTrue(buscar().isSuccess)
        assertTrue(buscar().isSuccess)
        assertTrue(buscar().isSuccess)

        assertEquals("o cache HTTP devia ter servido as 2 últimas", 1, servidor.requestCount)
    }

    @Test
    fun `429 para o trafego na hora e nao bate na API de novo`() {
        servidor.enqueue(MockResponse().setResponseCode(429))
        servidor.enqueue(respostaOk())      // não pode ser consumida

        val primeira = buscar()
        assertTrue(primeira.exceptionOrNull() is HttpException)
        assertTrue(freio.bloqueado())

        val segunda = buscar()
        assertTrue(segunda.exceptionOrNull() is MetBloqueadaException)
        assertEquals("nenhuma requisição depois do 429", 1, servidor.requestCount)
    }

    @Test
    fun `403 tambem liga o freio`() {
        servidor.enqueue(MockResponse().setResponseCode(403))
        assertTrue(buscar().isFailure)
        assertTrue(freio.bloqueado())
        assertTrue(buscar().exceptionOrNull() is MetBloqueadaException)
        assertEquals(1, servidor.requestCount)
    }

    @Test
    fun `depois do prazo volta a consultar e o sucesso zera o freio`() {
        servidor.enqueue(MockResponse().setResponseCode(429))
        servidor.enqueue(respostaOk())

        assertTrue(buscar().isFailure)
        relogio += FreioMet.BASE_MS + 1
        assertFalse(freio.bloqueado())

        assertTrue(buscar().isSuccess)
        assertEquals(2, servidor.requestCount)
        freio.aoSerBarrado()
        assertEquals("sucesso zerou: volta à espera-base", relogio + FreioMet.BASE_MS, freio.bloqueadoAteMs())
    }

    @Test
    fun `erro de servidor 500 nao liga o freio`() {
        servidor.enqueue(MockResponse().setResponseCode(500))
        servidor.enqueue(respostaOk())

        assertTrue(buscar().isFailure)
        assertFalse("500 não é pedido da MET para parar", freio.bloqueado())
        assertTrue(buscar().isSuccess)
        assertNotNull(servidor.takeRequest())
    }
}
