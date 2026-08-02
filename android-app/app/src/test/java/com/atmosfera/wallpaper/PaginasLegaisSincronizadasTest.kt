package com.atmosfera.wallpaper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.io.File
import org.junit.Test

/**
 * As páginas legais existem em dois lugares por necessidade real:
 *
 *  - `docs/<pagina>/index.html` — publicado na web (GitHub Pages), é a URL que
 *    o Play Console exige;
 *  - `app/src/main/assets/legal/<pagina>/index.html` — embutido no APK, é o que
 *    a WebView de Ajustes abre offline.
 *
 * Já tentamos eliminar a duplicação com uma task Gradle de cópia. Quebrou duas
 * vezes de formas diferentes (uma delas deixando o build VERDE e o APK sem os
 * assets), então a cópia voltou a ser física e a garantia virou este teste: se
 * alguém editar um lado e esquecer o outro, o gate falha antes do merge.
 *
 * **Editou uma página?** Copie pro outro caminho no mesmo commit. O texto
 * canônico continua sendo o `.md` na raiz (`PRIVACIDADE.md`, `TERMOS.md`,
 * `CONTATO.md`).
 */
class PaginasLegaisSincronizadasTest {

    private val paginas = listOf("privacidade", "termos", "contato")

    /** Testes JVM rodam com working dir = `android-app/app`. */
    private val raizRepo = File("../..")

    @Test
    fun `pagina publicada e pagina embutida no app sao identicas`() {
        paginas.forEach { pagina ->
            val web = File(raizRepo, "docs/$pagina/index.html")
            val asset = File(raizRepo, "android-app/app/src/main/assets/legal/$pagina/index.html")

            assertTrue("Não achei a página publicada: ${web.path}", web.isFile)
            assertTrue("Não achei o asset embutido: ${asset.path}", asset.isFile)

            assertEquals(
                "docs/$pagina/index.html e o asset embutido divergiram — copie um " +
                    "sobre o outro no mesmo commit (ver KDoc desta classe)",
                web.readText(),
                asset.readText(),
            )
        }
    }

    @Test
    fun `paginas embutidas nao estao vazias`() {
        paginas.forEach { pagina ->
            val asset = File(raizRepo, "android-app/app/src/main/assets/legal/$pagina/index.html")
            assertTrue(
                "Asset de $pagina está vazio ou truncado",
                asset.length() > 1_000,
            )
        }
    }
}
