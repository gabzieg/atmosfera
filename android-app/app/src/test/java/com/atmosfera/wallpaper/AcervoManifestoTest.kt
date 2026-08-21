package com.atmosfera.wallpaper

import com.atmosfera.wallpaper.engine.Acervo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * O manifesto é o índice do acervo baixado (gerado por `tools/pacote_cenas.py`).
 * Estes testes rodam em JVM puro — o que se garante aqui é o que o app faz ANTES
 * de ter rede: ler o índice em cache e responder tamanho/arquivo sem explodir.
 */
class AcervoManifestoTest {

    /** Recorte real da saída do pacote_cenas.py (cena porto, arte pixel). */
    private val json = """
        {"versao":1,"formato":"webp","qualidade":92,
         "cenas":{"porto":{
           "cena":{"arquivo":"pack/porto/_cena.zip","bytes":111983,"sha256":"c4ae17b8b2890a17"},
           "artes":{"pixel":{
             "pack":{"arquivo":"pack/porto/pixel.zip","bytes":515376,"sha256":"544246b70dbe99f6"},
             "thumb":{"arquivo":"thumb/porto__pixel.webp","bytes":10080,"sha256":"3fe2e5f29bb04b51"},
             "preview":{"arquivo":"preview/porto__pixel.webp","bytes":55226,"sha256":"bb3bb2bf158326e4"}}}}}}
    """.trimIndent()

    @Test
    fun `le cena e arte do manifesto`() {
        val m = Acervo.Manifesto.parse(json)
        assertEquals(1, m.versao)
        val arte = m.arte("porto", "pixel")
        assertEquals("pack/porto/pixel.zip", arte?.pack?.arquivo)
        assertEquals(10080L, arte?.thumb?.bytes)
    }

    @Test
    fun `arte que nao existe devolve null em vez de estourar`() {
        val m = Acervo.Manifesto.parse(json)
        assertNull(m.arte("porto", "clay"))
        assertNull(m.arte("inexistente", "pixel"))
    }

    /** O pack comum da cena só entra na conta quando ainda não está no aparelho
     *  — é o número que a Loja mostra no "baixar (X MB)". */
    @Test
    fun `tamanho do download soma o pack da cena so na primeira arte`() {
        val m = Acervo.Manifesto.parse(json)
        assertEquals(515376L + 111983L, m.bytesDe("porto", "pixel", jaTemCena = false))
        assertEquals(515376L, m.bytesDe("porto", "pixel", jaTemCena = true))
        assertEquals(0L, m.bytesDe("cena_que_nao_existe", "pixel", jaTemCena = true))
    }

    /** Índice corrompido no cache não pode derrubar a Loja: vira vazio. */
    @Test
    fun `json quebrado vira manifesto vazio`() {
        assertTrue(Acervo.Manifesto.parse("{isso nao e json").cenas.isEmpty())
        assertTrue(Acervo.Manifesto.parse("").cenas.isEmpty())
    }

    // ── extração do pack ────────────────────────────────────────────────────
    private fun zip(vararg itens: Pair<String, String>): ByteArray {
        val buf = java.io.ByteArrayOutputStream()
        java.util.zip.ZipOutputStream(buf).use { z ->
            for ((nome, conteudo) in itens) {
                z.putNextEntry(java.util.zip.ZipEntry(nome))
                z.write(conteudo.toByteArray()); z.closeEntry()
            }
        }
        return buf.toByteArray()
    }

    private fun sha16(b: ByteArray): String {
        val d = java.security.MessageDigest.getInstance("SHA-256").digest(b)
        return d.joinToString("") { "%02x".format(it) }.take(16)
    }

    @Test
    fun `pack extrai os arquivos na pasta da arte`() {
        val dados = zip("fundo.webp" to "F", "frente.webp" to "R")
        val destino = java.io.File(temp(), "pack/porto/pixel")
        Acervo.extrairZip(dados, sha16(dados), destino)
        assertEquals("F", java.io.File(destino, "fundo.webp").readText())
        assertEquals("R", java.io.File(destino, "frente.webp").readText())
    }

    /** Download truncado tem que falhar ANTES de virar cena — senão o motor
     *  carrega um fundo pela metade e o usuário vê arte quebrada. */
    @Test
    fun `pack corrompido nao grava nada`() {
        val dados = zip("fundo.webp" to "F")
        val destino = java.io.File(temp(), "pack/porto/pixel")
        val erro = runCatching { Acervo.extrairZip(dados, "0000000000000000", destino) }
        assertTrue(erro.isFailure)
        assertTrue(!destino.exists())
    }

    /** Zip Slip: entrada com `..` escreveria fora da pasta do acervo. */
    @Test
    fun `entrada de zip que escapa da pasta e ignorada`() {
        val raiz = temp()
        val dados = zip("../fora.txt" to "X", "fundo.webp" to "F")
        val destino = java.io.File(raiz, "pack/porto/pixel")
        Acervo.extrairZip(dados, sha16(dados), destino)
        assertTrue(java.io.File(destino, "fundo.webp").exists())
        assertTrue(!java.io.File(raiz, "pack/porto/fora.txt").exists())
        assertTrue(!java.io.File(raiz, "fora.txt").exists())
    }

    /** Rebaixar a mesma arte substitui a pasta inteira (arte atualizada não
     *  pode ficar misturada com a antiga). */
    @Test
    fun `extrair de novo troca o conteudo antigo`() {
        val destino = java.io.File(temp(), "pack/porto/pixel")
        val v1 = zip("fundo.webp" to "velho", "sobra.webp" to "S")
        Acervo.extrairZip(v1, sha16(v1), destino)
        val v2 = zip("fundo.webp" to "novo")
        Acervo.extrairZip(v2, sha16(v2), destino)
        assertEquals("novo", java.io.File(destino, "fundo.webp").readText())
        assertTrue(!java.io.File(destino, "sobra.webp").exists())
    }

    private fun temp(): java.io.File =
        java.nio.file.Files.createTempDirectory("acervo").toFile().also { it.deleteOnExit() }
}
