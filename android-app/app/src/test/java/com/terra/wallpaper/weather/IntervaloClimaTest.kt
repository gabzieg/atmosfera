package com.terra.wallpaper.weather

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * O cache cobre todo o intervalo escolhido: sair e voltar à home não antecipa
 * consulta como acontecia com a antiga margem de 10% do worker.
 */
class IntervaloClimaTest {

    @Test
    fun `cache cobre todo o intervalo escolhido`() {
        IntervaloClima.OPCOES_MIN.forEach { minutos ->
            val periodoMs = minutos * 60_000L
            val ttl = IntervaloClima.ttlMs(minutos)
            assertEquals(periodoMs, ttl)
        }
    }

    @Test
    fun `TTL nao e curto demais a ponto de buscar antes da hora`() {
        // Cada preferência precisa preservar sua duração real.
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
