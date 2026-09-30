package com.terra.wallpaper

import com.terra.wallpaper.engine.Catalogo
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogoLancamentoTest {
    @Test
    fun `a versao inicial publica somente os cinco cenarios definidos`() {
        val idsEsperados = listOf("cabana", "bruxa", "lavanda", "esfinge", "jardim")

        assertEquals(idsEsperados, Catalogo.cenarios.map { it.id })
        assertTrue(Catalogo.cenarios.all { it.artesGratis.size == 1 })
        assertTrue(Catalogo.cenarios.all { it.productId == null })

        val pastaCenas = File("src/main/assets/atmosfera/cenas")
        assertEquals(idsEsperados.sorted(), pastaCenas.listFiles { file -> file.isDirectory }
            ?.map { it.name }
            ?.sorted())

        Catalogo.cenarios.forEach { cenario ->
            val artesEmDisco = File(pastaCenas, cenario.id)
                .listFiles { file -> file.isDirectory }
                ?.map { it.name }
                ?.sorted()
            assertEquals(cenario.id, cenario.artes.sorted(), artesEmDisco)
            assertTrue(cenario.artes.containsAll(cenario.artesGratis))
        }
    }
}
