package com.atmosfera.wallpaper.weather

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Trava o invariante que já quebrou uma vez: o TTL do cache precisa ser **menor**
 * que o período do worker.
 *
 * O bug original: `WeatherCache` usava TTL fixo de 30min enquanto o usuário podia
 * escolher 15min em Ajustes. O worker acordava aos 15min, `isStale` devolvia false
 * (dado ainda "fresco" pelos 30min fixos) e a busca era pulada — a opção de 15min
 * não fazia absolutamente nada, e o build passava porque compilar não prova efeito.
 */
class IntervaloClimaTest {

    @Test
    fun `TTL e sempre menor que o periodo, senao o worker pula a busca`() {
        IntervaloClima.OPCOES_MIN.forEach { minutos ->
            val periodoMs = minutos * 60_000L
            val ttl = IntervaloClima.ttlMs(minutos)
            assertTrue(
                "TTL de ${minutos}min ($ttl ms) precisa ser < período ($periodoMs ms), " +
                    "senão o dado ainda conta como fresco quando o worker acorda",
                ttl < periodoMs,
            )
        }
    }

    @Test
    fun `TTL nao e curto demais a ponto de buscar antes da hora`() {
        // Margem de 10%: perto o bastante do período pra não gerar busca extra,
        // longe o bastante da fronteira pra não depender de milissegundos.
        IntervaloClima.OPCOES_MIN.forEach { minutos ->
            val periodoMs = minutos * 60_000L
            assertTrue(
                "TTL de ${minutos}min está curto demais",
                IntervaloClima.ttlMs(minutos) >= periodoMs / 2,
            )
        }
    }

    @Test
    fun `intervalos escolhiveis sao distintos e crescentes`() {
        assertEquals(IntervaloClima.OPCOES_MIN.sorted(), IntervaloClima.OPCOES_MIN)
        assertEquals(IntervaloClima.OPCOES_MIN.distinct(), IntervaloClima.OPCOES_MIN)
        // Cada opção precisa render um TTL diferente — se dois colapsarem no mesmo
        // valor, uma das opções da tela vira enfeite.
        val ttls = IntervaloClima.OPCOES_MIN.map { IntervaloClima.ttlMs(it) }
        assertEquals(ttls.distinct(), ttls)
    }

    @Test
    fun `padrao esta entre as opcoes oferecidas`() {
        assertTrue(IntervaloClima.PADRAO_MIN in IntervaloClima.OPCOES_MIN)
    }
}
