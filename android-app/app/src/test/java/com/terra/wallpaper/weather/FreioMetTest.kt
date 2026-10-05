package com.terra.wallpaper.weather

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * O freio que cumpre a regra dos termos da MET Norway: "limit traffic
 * immediately" ao receber 429. Lógica pura, sem Android nem rede.
 */
class FreioMetTest {

    private class ArmazemFalso : FreioMet.Armazem {
        override var ateMs = 0L
        override var falhasSeguidas = 0
    }

    private var relogio = 1_000_000L
    private val armazem = ArmazemFalso()
    private val freio = FreioMet(armazem) { relogio }

    private val min = 60_000L

    @Test
    fun `espera dobra a cada falha seguida e para no teto de 6 horas`() {
        assertEquals(30 * min, FreioMet.esperaMs(1, null))
        assertEquals(60 * min, FreioMet.esperaMs(2, null))
        assertEquals(120 * min, FreioMet.esperaMs(3, null))
        assertEquals(240 * min, FreioMet.esperaMs(4, null))
        assertEquals(360 * min, FreioMet.esperaMs(5, null))   // 480 min cortados no teto
        assertEquals(360 * min, FreioMet.esperaMs(50, null))  // sem estouro de shift
    }

    @Test
    fun `Retry-After maior que a espera calculada vence, mas respeita o teto`() {
        assertEquals(120 * min, FreioMet.esperaMs(1, 7200))        // 2 h pedidas
        assertEquals(30 * min, FreioMet.esperaMs(1, 60))           // 1 min pedido: fica a base
        assertEquals(360 * min, FreioMet.esperaMs(1, 1_000_000))   // absurdo: teto
        assertEquals(30 * min, FreioMet.esperaMs(1, -5))           // lixo não encurta
    }

    @Test
    fun `livre no começo`() {
        assertFalse(freio.bloqueado())
        assertEquals(0L, freio.bloqueadoAteMs())
    }

    @Test
    fun `ser barrado bloqueia ate o prazo e depois libera`() {
        freio.aoSerBarrado()
        assertTrue(freio.bloqueado())
        assertEquals(relogio + 30 * min, freio.bloqueadoAteMs())

        relogio += 29 * min
        assertTrue(freio.bloqueado())

        relogio += 1 * min
        assertFalse(freio.bloqueado())
    }

    @Test
    fun `barrado de novo logo depois da liberacao espera o dobro`() {
        freio.aoSerBarrado()
        relogio += 31 * min
        assertFalse(freio.bloqueado())

        freio.aoSerBarrado()
        assertEquals(relogio + 60 * min, freio.bloqueadoAteMs())
    }

    @Test
    fun `um sucesso zera o historico`() {
        freio.aoSerBarrado()
        relogio += 31 * min
        freio.aoSucesso()

        freio.aoSerBarrado()
        assertEquals(relogio + 30 * min, freio.bloqueadoAteMs())   // voltou à base
    }
}
