package com.terra.wallpaper

import com.terra.wallpaper.engine.AcessoArte
import com.terra.wallpaper.engine.Catalogo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AcessoArteTest {
    @Test
    fun `cada amostra continua disponivel sem compra`() {
        Catalogo.cenarios.forEach { cenario ->
            cenario.artesGratis.forEach { arte ->
                assertTrue("${cenario.id}/$arte", AcessoArte.permitida(cenario, arte))
                assertEquals(arte, AcessoArte.selecionada(cenario, arte))
            }
        }
    }

    @Test
    fun `artes adicionais ficam bloqueadas sem um direito proprio`() {
        val pagas = Catalogo.cenarios.flatMap { cenario ->
            (cenario.artes - cenario.artesGratis).map { cenario to it }
        }
        assertTrue("A regressão precisa incluir artes pagas reais do catálogo", pagas.isNotEmpty())
        pagas.forEach { (cenario, arte) ->
            assertFalse("${cenario.id}/$arte", AcessoArte.permitida(cenario, arte))
        }
    }

    @Test
    fun `preferencia paga de versao anterior volta para a amostra do mesmo cenario`() {
        Catalogo.cenarios.forEach { cenario ->
            (cenario.artes - cenario.artesGratis).forEach { salva ->
                val fallback = AcessoArte.selecionada(cenario, salva)
                assertTrue("${cenario.id}/$fallback", fallback in cenario.artesGratis)
                assertTrue(AcessoArte.permitida(cenario, fallback))
            }
        }
    }

    @Test
    fun `arte gratuita em um cenario nao libera a mesma variante em outro`() {
        assertTrue(AcessoArte.permitida(Catalogo.por("cabana")!!, "pixel"))
        assertFalse(AcessoArte.permitida(Catalogo.por("bruxa")!!, "pixel"))
        assertEquals("clay", AcessoArte.selecionada(Catalogo.por("bruxa")!!, "pixel"))
    }

    @Test
    fun `preferencia inexistente nao passa nem no destrave de teste`() {
        Catalogo.cenarios.forEach { cenario ->
            assertFalse(AcessoArte.permitida(cenario, "inexistente", destraveTeste = true))
            assertTrue(AcessoArte.selecionada(cenario, "inexistente") in cenario.artesGratis)
        }
    }

    @Test
    fun `destrave explicito de debug pode testar arte paga sem alterar o padrao`() {
        val bruxa = Catalogo.por("bruxa")!!
        assertTrue(AcessoArte.permitida(bruxa, "pixel", destraveTeste = true))
        assertEquals("pixel", AcessoArte.selecionada(bruxa, "pixel", destraveTeste = true))
        assertFalse(AcessoArte.permitida(bruxa, "pixel"))
    }
}
