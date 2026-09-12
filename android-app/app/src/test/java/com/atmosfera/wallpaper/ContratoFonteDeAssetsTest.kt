package com.atmosfera.wallpaper

import org.junit.Assert.assertTrue
import java.io.File
import org.junit.Test

/**
 * Trava o contrato de leitura de conteúdo do motor: `engine/` lê por
 * [com.atmosfera.wallpaper.engine.FonteDeAssets], **nunca** por `AssetManager`.
 *
 * **Por que um teste e não confiança.** O Rafael entrega o `engine/` por
 * *snapshot*, não por diff: chega o arquivo inteiro, substituindo o nosso. Se um
 * snapshot vier com a assinatura antiga (`carregar(assets: AssetManager, …)`),
 * a mudança some **em silêncio** — o build continua verde, porque o front só
 * precisaria voltar a passar `context.assets`, e ninguém percebe até o conteúdo
 * pago não carregar em produção.
 *
 * Isso vale com ou sem revisor humano: é problema de merge, não de revisão.
 *
 * **Por que importa.** Conteúdo pago não pode embarcar no APK (ver
 * `docs/dev/ROADMAP.md` → Fase 4). Ele baixa sob demanda via Play Asset
 * Delivery, e asset pack "on-demand" **não é visível** em `context.assets`:
 * vive num armazenamento à parte, lido pelo `AssetPackManager`, que devolve
 * caminho de arquivo. Um motor acoplado a `AssetManager` simplesmente não
 * consegue ler conteúdo comprado.
 *
 * **Mudou de propósito?** Então a Fase 4 foi abandonada ou repensada. Atualize
 * o `ROADMAP.md` no mesmo commit — não só este teste.
 */
class ContratoFonteDeAssetsTest {

    /** Testes JVM rodam com working dir = `android-app/app`. */
    private val engine = File("src/main/java/com/atmosfera/wallpaper/engine")

    private fun fontesDoEngine(): List<File> {
        assertTrue("Não achei o pacote engine: ${engine.absolutePath}", engine.isDirectory)
        return engine.walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()
    }

    /**
     * Procura o **uso** do tipo, não a palavra: importado ou totalmente
     * qualificado. Citar `AssetManager` num comentário explicando por que ele
     * saiu do motor é legítimo e não pode quebrar o gate — a própria
     * `FonteDeAssets.kt` faz isso.
     */
    private fun usaAssetManager(texto: String): Boolean =
        texto.contains("import android.content.res.AssetManager") ||
            texto.contains("android.content.res.AssetManager")

    @Test
    fun `engine nao conhece AssetManager`() {
        val culpados = fontesDoEngine()
            .filter { usaAssetManager(it.readText()) }
            .map { it.name }

        assertTrue(
            "Estes arquivos de engine/ voltaram a mencionar AssetManager: $culpados.\n" +
                "O motor precisa ler por FonteDeAssets para conseguir abrir conteúdo " +
                "baixado sob demanda — asset pack on-demand NÃO aparece em " +
                "context.assets. Se isto veio de um snapshot do motor, reaplique a " +
                "migração em vez de reverter o front: o adaptador do APK vive em " +
                "ConteudoEmbarcado.kt (front), fora do engine.",
            culpados.isEmpty(),
        )
    }

    @Test
    fun `carregar recebe FonteDeAssets`() {
        val fonte = File(engine, "EffectEngine.kt")
        assertTrue("Não achei ${fonte.absolutePath}", fonte.isFile)

        val assinatura = fonte.readText()
            .lineSequence()
            .firstOrNull { it.contains("fun carregar(") }

        assertTrue(
            "Não achei a declaração de EffectEngine.carregar(). Se ela mudou de " +
                "forma, este teste precisa acompanhar — mas confirme antes que o " +
                "motor ainda lê por FonteDeAssets.",
            assinatura != null,
        )
        assertTrue(
            "EffectEngine.carregar() não recebe mais FonteDeAssets: «$assinatura». " +
                "Ver o KDoc desta classe de teste para o porquê.",
            assinatura!!.contains("FonteDeAssets"),
        )
    }

    @Test
    fun `leitura da marcacao recebe FonteDeAssets`() {
        val fonte = File(engine, "Marcacao.kt")
        assertTrue("Não achei ${fonte.absolutePath}", fonte.isFile)

        val assinatura = fonte.readText()
            .lineSequence()
            .firstOrNull { it.contains("fun ler(") }

        assertTrue(
            "DadosMarcacao.ler() não recebe FonteDeAssets: «$assinatura». É o " +
                "segundo ponto onde o motor lê arquivo — se ele voltar a usar " +
                "AssetManager, zonas.json não é lido de cenário baixado.",
            assinatura != null && assinatura.contains("FonteDeAssets"),
        )
    }

    @Test
    fun `a interface existe onde o motor espera`() {
        assertTrue(
            "engine/FonteDeAssets.kt sumiu. Ela é o contrato que permite o motor " +
                "ler conteúdo de fora do APK; sem ela a Fase 4 não fecha.",
            File(engine, "FonteDeAssets.kt").isFile,
        )
    }
}
