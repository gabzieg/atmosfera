package com.terra.wallpaper.weather

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClimaNaHomeTest {
    @Test fun `somente home visivel desbloqueada e sem previa pode consultar`() {
        assertTrue(ClimaNaHome.podeAtualizar(true, true, false, false, false))
        assertFalse(ClimaNaHome.podeAtualizar(false, true, false, false, false))
        assertFalse(ClimaNaHome.podeAtualizar(true, false, false, false, false))
        assertFalse(ClimaNaHome.podeAtualizar(true, true, true, false, false))
        assertFalse(ClimaNaHome.podeAtualizar(true, true, false, true, false))
        assertFalse(ClimaNaHome.podeAtualizar(true, true, false, false, true))
    }

    @Test fun `nao consulta fora da home nem acumula ciclos ao reavaliar`() = runTest {
        var visivel = false
        var chamadas = 0
        val controle = ClimaNaHome(this, { visivel }, { chamadas++ }, { 1_000L })
        controle.reavaliar()
        advanceTimeBy(10_000)
        assertEquals(0, chamadas)
        visivel = true
        repeat(10) { controle.reavaliar() }
        runCurrent()
        assertEquals(1, chamadas)
        advanceTimeBy(999)
        runCurrent()
        assertEquals(1, chamadas)
        advanceTimeBy(1)
        runCurrent()
        assertEquals(2, chamadas)
        visivel = false
        controle.reavaliar()
        advanceTimeBy(10_000)
        assertEquals(2, chamadas)
        controle.parar()
    }

    @Test fun `sair cancela consulta em curso e retorno nao sobrepoe sessoes`() = runTest {
        var visivel = true
        var emCurso = 0
        var maximo = 0
        var canceladas = 0
        val controle = ClimaNaHome(this, { visivel }, {
            emCurso++
            maximo = maxOf(maximo, emCurso)
            try { awaitCancellation() } finally { emCurso--; canceladas++ }
        }, { 1_000L })
        controle.reavaliar()
        runCurrent()
        assertEquals(1, emCurso)
        visivel = false
        controle.reavaliar()
        visivel = true
        controle.reavaliar()
        runCurrent()
        assertEquals(1, canceladas)
        assertEquals(1, emCurso)
        assertEquals(1, maximo)
        controle.parar()
        runCurrent()
        assertEquals(0, emCurso)
    }

    @Test fun `reavalia elegibilidade antes do proximo ciclo mesmo sem callback`() = runTest {
        var elegivel = true
        var chamadas = 0
        val controle = ClimaNaHome(this, { elegivel }, { chamadas++ }, { 1_000L })
        controle.reavaliar()
        runCurrent()
        elegivel = false
        advanceTimeBy(10_000)
        runCurrent()
        assertEquals(1, chamadas)
        controle.parar()
    }
}
