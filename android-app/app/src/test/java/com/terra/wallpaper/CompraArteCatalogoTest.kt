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
        com.terra.wallpaper.debug.DebugOverride.setDestravarPagos(context, false)
        com.terra.wallpaper.debug.DebugOverride.setPremiumTeste(context, false)
        com.terra.wallpaper.billing.Plano.setPremium(context, false)
        Catalogo.carregar(context)
    }

    @Test fun catalogoTemSomenteArtesComArquivosEMiniatura() {
        assertEquals(69, Catalogo.cenarios.size)
        assertEquals(358, Catalogo.cenarios.sumOf { it.artes.size })
        assertEquals(353, Catalogo.produtosArtes().size)
        assertEquals(5, Catalogo.cenarios.sumOf { it.artesGratis.size })
        val excluidas = setOf("simpsons", "budokai", "konoha", "timessquare", "burj", "ogro", "eiffel", "estacionamento", "natal")
        assertFalse(Catalogo.cenarios.any { it.id in excluidas })
        Catalogo.cenarios.forEach { cena -> cena.artes.forEach { arte ->
            context.assets.open(cena.miniaturas.getValue(arte)).use { assertTrue(it.read() >= 0) }
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
        val cena = Catalogo.cenarios.first { it.artesGratis.isEmpty() }
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
                Acervo.validarArquivo(it.pack)
            }
            Acervo.validarArquivo(cena.cena)
        }
    }

    @Test fun cenasNativasRecebemVariantesRemotasSemDuplicacao() {
        assertEquals(69, Catalogo.cenarios.map { it.id }.toSet().size)
        Catalogo.embarcados.forEach { nativa ->
            val mesclada = Catalogo.por(nativa.id)!!
            assertEquals(nativa.artesGratis, mesclada.artesGratis)
            assertTrue(mesclada.artes.containsAll(nativa.artes))
            assertEquals(mesclada.artes.toSet() - nativa.artes.toSet(), mesclada.artesRemotas)
            assertTrue(mesclada.artesRemotas.isNotEmpty())
        }
    }

    @Test fun destraveDebugNaoAplicaArteRemotaAusente() {
        com.terra.wallpaper.debug.DebugOverride.setDestravarPagos(context, true)
        val cena = Catalogo.cenarios.first { it.artesGratis.isEmpty() }
        com.terra.wallpaper.engine.Cena.definir(context, cena.id)
        com.terra.wallpaper.engine.ArteFundo.definir(context, cena.artes.first())
        assertEquals(Catalogo.padrao.id, com.terra.wallpaper.engine.Cena.atual(context))
        assertEquals(Catalogo.padrao.artesGratis.first(), com.terra.wallpaper.engine.ArteFundo.atual(context))
        assertTrue(ComprasArte.produtos(context).isEmpty())
    }

    @Test fun agrupamentoVisualNaoConcedeArtesDoOutroAmbiente() {
        val cabana = Catalogo.por("cabana")!!
        val selva = Catalogo.por("cabana2")!!
        assertEquals(setOf("cabana", "cabana2"), Catalogo.ambientes("cabana").map { it.id }.toSet())
        assertTrue(AcessoArte.permitida(cabana, "pixel"))
        assertFalse(AcessoArte.permitida(selva, "pixel"))
        assertEquals("terra_arte_cabana2_pixel", Catalogo.produtoArte(selva, "pixel"))
    }

    @Test fun premiumSimuladoNaoLiberaArtes() {
        com.terra.wallpaper.debug.DebugOverride.setPremiumTeste(context, true)
        assertTrue(com.terra.wallpaper.billing.Plano.isPremium(context))
        assertFalse(AcessoArte.permitida(Catalogo.por("cabana2")!!, "pixel",
            com.terra.wallpaper.debug.DebugOverride.destravarPagos(context)))
    }

    @Test fun liberarArtesNaoSimulaPremium() {
        com.terra.wallpaper.debug.DebugOverride.setDestravarPagos(context, true)
        assertFalse(com.terra.wallpaper.billing.Plano.isPremium(context))
        assertTrue(AcessoArte.permitida(Catalogo.por("cabana2")!!, "pixel",
            com.terra.wallpaper.debug.DebugOverride.destravarPagos(context)))
    }
}
