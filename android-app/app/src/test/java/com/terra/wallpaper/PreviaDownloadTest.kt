package com.terra.wallpaper

import com.terra.wallpaper.engine.Acervo
import java.net.InetAddress
import java.net.ServerSocket
import java.security.MessageDigest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class PreviaDownloadTest {
    private val dados = byteArrayOf(1, 2, 3, 4)
    private fun hash() = MessageDigest.getInstance("SHA-256").digest(dados).joinToString("") { "%02x".format(it) }
    private fun servidor(action: (String) -> Unit) {
        val servidor = ServerSocket(0, 1, InetAddress.getByName("127.0.0.1"))
        val thread = Thread {
            servidor.accept().use { socket ->
                val reader = socket.getInputStream().bufferedReader()
                while (!reader.readLine().isNullOrEmpty()) { }
                socket.getOutputStream().use { output ->
                    output.write("HTTP/1.1 200 OK\r\nContent-Length: ${dados.size}\r\nConnection: close\r\n\r\n".toByteArray())
                    output.write(dados); output.flush()
                }
            }
        }.apply { isDaemon = true; start() }
        try { action("http://127.0.0.1:${servidor.localPort}/preview.webp") }
        finally { servidor.close(); thread.join(2000) }
    }

    @Test fun recebePreviaQuandoTamanhoEHashConferem() {
        servidor { url -> runBlocking {
            assertArrayEquals(dados, Acervo.baixarImagem(url, Acervo.ArquivoRemoto(bytes = 4, sha256 = hash())))
        } }
    }
    @Test fun recusaHashErradoSemAceitarImagem() {
        servidor { url -> runBlocking {
            assertTrue(runCatching { Acervo.baixarImagem(url, Acervo.ArquivoRemoto(bytes = 4, sha256 = "0".repeat(64))) }.isFailure)
        } }
    }
    @Test fun limitaRespostaMaiorQueDescritor() {
        servidor { url -> runBlocking {
            assertTrue(runCatching { Acervo.baixarImagem(url, Acervo.ArquivoRemoto(bytes = 2, sha256 = hash())) }.isFailure)
        } }
    }
}
