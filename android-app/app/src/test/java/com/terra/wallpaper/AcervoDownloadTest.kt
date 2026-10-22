package com.terra.wallpaper

import com.terra.wallpaper.engine.Acervo
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.InetAddress
import java.net.ServerSocket
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class AcervoDownloadTest {
    @get:Rule val pasta = TemporaryFolder()

    private fun zip(): ByteArray = ByteArrayOutputStream().also { buf ->
        ZipOutputStream(buf).use { z ->
            z.putNextEntry(ZipEntry("fundo.webp"))
            z.write("arte".toByteArray()); z.closeEntry()
        }
    }.toByteArray()

    private fun servidor(body: ByteArray, action: (String) -> Unit) {
        val s = ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"))
        val thread = Thread {
            s.accept().use { socket ->
                val reader = socket.getInputStream().bufferedReader()
                while (true) { val line = reader.readLine(); if (line == null || line.isEmpty()) break }
                socket.getOutputStream().use { output ->
                    output.write("HTTP/1.1 200 OK\r\nContent-Length: ${body.size}\r\nConnection: close\r\n\r\n".toByteArray())
                    output.write(body); output.flush()
                }
            }
        }.apply { isDaemon = true; start() }
        try { action("http://127.0.0.1:${s.localPort}/pack.zip") } finally {
            s.close(); thread.join(2000)
        }
    }

    @Test fun downloadConfereHashEInstalaRecibo() {
        val body = zip()
        val hash = MessageDigest.getInstance("SHA-256").digest(body).joinToString("") { "%02x".format(it) }
        val destino = File(pasta.root, "arte")
        var recebido = 0L
        servidor(body) { url -> runBlocking {
            Acervo.baixarZip(url, hash, destino) { recebido = it }
        } }
        assertEquals(body.size.toLong(), recebido)
        assertEquals("arte", File(destino, "fundo.webp").readText())
        assertEquals(hash, File(destino, ".sha256").readText())
        assertFalse(pasta.root.listFiles()!!.any { it.name.endsWith(".download") })
    }

    @Test fun downloadComHashErradoPreservaArteAnterior() {
        val destino = File(pasta.root, "arte").apply { mkdirs() }
        File(destino, "fundo.webp").writeText("anterior")
        servidor(zip()) { url -> runBlocking {
            assertTrue(runCatching { Acervo.baixarZip(url, "0".repeat(64), destino) {} }.isFailure)
        } }
        assertEquals("anterior", File(destino, "fundo.webp").readText())
        assertFalse(pasta.root.listFiles()!!.any { it.name.endsWith(".download") })
    }
}
