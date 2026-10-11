package com.terra.wallpaper

import android.app.Application
import com.terra.wallpaper.billing.ComprasArte
import com.terra.wallpaper.engine.AcessoArte
import com.terra.wallpaper.engine.Acervo
import com.terra.wallpaper.engine.Catalogo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = Application::class)
class CompraArteCatalogoTest {
    private val context get() = RuntimeEnvironment.getApplication()

    @Before fun preparar() {
        ComprasArte.reconciliar(context, emptySet())
        Catalogo.carregar(context)
    }

    @Test fun catalogoTemSomenteArtesComArquivosEMiniatura() {
        assertEquals(71, Catalogo.cenarios.size)
        assertEquals(75, Catalogo.cenarios.sumOf { it.artes.size })
        assertEquals(70, Catalogo.produtosArtes().size)
        assertEquals(5, Catalogo.cenarios.sumOf { it.artesGratis.size })
        val excluidas = setOf("simpsons", "budokai", "konoha", "timessquare", "burj", "ogro", "eiffel")
        assertFalse(Catalogo.cenarios.any { it.id in excluidas })
        Catalogo.cenarios.forEach { cena -> cena.artes.forEach { arte ->
            context.assets.open("atmosfera/miniaturas/${cena.id}__$arte.jpg").use { assertTrue(it.read() >= 0) }
        } }
    }

    @Test fun compraLiberaSomenteAVarianteCorrespondente() {
        val cena = Catalogo.por("bruxa")!!
        val produto = Catalogo.produtoArte(cena, "pixel")!!
        ComprasArte.conceder(context, produto)
        assertEquals(setOf(produto), ComprasArte.produtos(context))
        assertEquals("pixel", AcessoArte.selecionada(cena, "pixel", compradas = setOf("pixel")))
        assertFalse(AcessoArte.permitida(Catalogo.por("lavanda")!!, "pixel"))
        ComprasArte.reconciliar(context, emptySet())
        assertTrue(ComprasArte.produtos(context).isEmpty())
        assertEquals("clay", AcessoArte.selecionada(cena, "pixel"))
    }

    @Test fun metadadosNaoConcedemCompraNemInstalacao() {
        val cena = Catalogo.cenarios.first { it.artesRemotas.isNotEmpty() }
        assertTrue(ComprasArte.produtos(context).isEmpty())
        assertNull(AcessoArte.selecionada(cena, cena.artes.first()))
        assertFalse(Acervo.temArte(context, cena.id, cena.artes.first()))
    }

    @Test fun manifestoFixaHashCompletoParaTodosOsPacotes() {
        val manifesto = context.assets.open("atmosfera/catalogo/manifest.json")
            .bufferedReader().use { Acervo.Manifesto.parse(it.readText()) }
        assertEquals(71, manifesto.cenas.size)
        manifesto.cenas.values.forEach { cena ->
            assertTrue(cena.cena.sha256.matches(Regex("[a-f0-9]{64}")))
            cena.artes.values.forEach {
                assertTrue(it.pack.sha256.matches(Regex("[a-f0-9]{64}")))
                assertTrue(it.pack.bytes > 0)
            }
        }
    }
}
