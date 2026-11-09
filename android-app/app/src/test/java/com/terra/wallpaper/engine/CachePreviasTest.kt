package com.terra.wallpaper.engine

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CachePreviasTest {
    @get:Rule val pasta = TemporaryFolder()

    @Test fun prazoContaDepoisDaSaidaEDaUltimaVisita() {
        var agora = 10_000L
        val cache = CachePrevias(pasta.root, { agora }, ttl = 100)
        val chave = "cabana__pixel"
        val arquivo = cache.arquivo(chave, "a".repeat(64)).apply { writeText("imagem") }
        cache.reter(chave); cache.tocar(arquivo)
        agora += 200
        cache.limpar()
        assertTrue(arquivo.exists()) // Não apagar a prévia aberta.
        cache.soltar(chave)
        agora += 99; cache.limpar()
        assertTrue(arquivo.exists())
        cache.reter(chave); cache.soltar(chave)
        agora += 100; cache.limpar()
        assertFalse(arquivo.exists())
    }

    @Test fun limiteDeDiscoRemoveAntigasSemApagarPreviaAtiva() {
        var agora = 20_000L
        val cache = CachePrevias(pasta.root, { agora }, ttl = 10_000, limite = 4)
        val antiga = cache.arquivo("cabana__pixel", "a".repeat(64)).apply { writeBytes(ByteArray(4)) }
        cache.tocar(antiga); agora++
        cache.reter("bruxa__clay")
        val ativa = cache.arquivo("bruxa__clay", "b".repeat(64)).apply { writeBytes(ByteArray(4)) }
        cache.tocar(ativa); cache.limpar()
        assertFalse(antiga.exists())
        assertTrue(ativa.exists())
    }

    @Test fun limpezaDepoisDeReabrirRemovePreviaVencida() {
        var agora = 30_000L
        val primeiro = CachePrevias(pasta.root, { agora }, ttl = 100)
        val arquivo = primeiro.arquivo("cabana__pixel", "a".repeat(64)).apply { writeText("imagem") }
        primeiro.tocar(arquivo)
        agora += 101
        CachePrevias(pasta.root, { agora }, ttl = 100).limpar()
        assertFalse(arquivo.exists())
    }
}
