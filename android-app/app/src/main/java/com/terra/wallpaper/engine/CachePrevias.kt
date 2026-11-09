package com.terra.wallpaper.engine

import java.io.File

/** Cache descartável: prazo após o último uso, sem apagar a imagem em exibição. */
internal class CachePrevias(
    private val pasta: File,
    private val agora: () -> Long = { System.currentTimeMillis() },
    private val ttl: Long = 5 * 60 * 1000L,
    private val limite: Long = 24 * 1024 * 1024L,
) {
    private val ativos = mutableMapOf<String, Int>()

    @Synchronized fun reter(chave: String) {
        ativos[chave] = (ativos[chave] ?: 0) + 1
        limpar()
    }

    @Synchronized fun soltar(chave: String) {
        val quantidade = (ativos[chave] ?: 1) - 1
        if (quantidade <= 0) ativos.remove(chave) else ativos[chave] = quantidade
        pasta.listFiles()?.filter { it.name.substringBefore('.') == chave }
            ?.forEach { it.setLastModified(agora()) }
    }

    @Synchronized fun arquivo(chave: String, hash: String): File {
        require(chave.matches(Regex("[a-z0-9_]+__[a-z0-9_]+")))
        require(hash.matches(Regex("[a-fA-F0-9]{64}")))
        pasta.mkdirs()
        return File(pasta, "$chave.${hash.lowercase()}.webp")
    }

    @Synchronized fun tocar(arquivo: File) { arquivo.setLastModified(agora()) }

    @Synchronized fun limpar() {
        val arquivos = pasta.listFiles()?.filter { it.isFile }?.sortedBy { it.lastModified() }.orEmpty()
        var bytes = arquivos.sumOf { it.length() }
        for (arquivo in arquivos) {
            if ((ativos[arquivo.name.substringBefore('.')] ?: 0) > 0) continue
            if (agora() - arquivo.lastModified() >= ttl || bytes > limite) {
                val tamanho = arquivo.length()
                if (arquivo.delete()) bytes -= tamanho
            }
        }
    }
}
