package com.atmosfera.wallpaper

import org.junit.Assert.assertTrue
import java.io.File
import org.junit.Test

/**
 * Trava a **política de privacidade contra o manifesto**: a seção 5 do documento
 * público não pode listar permissão que o app não pede, nem omitir uma que pede.
 *
 * **Por que existe.** O [PermissoesDeclaradasTest] já garante que o manifesto não
 * muda sem alguém perceber. O que faltava era o outro lado: quando a permissão
 * muda de propósito, os documentos legais precisam mudar junto — e isso dependia
 * de alguém lembrar. Em 2026-08-28, uma auditoria manual foi necessária só para
 * responder "a política ainda está correta?". Este teste responde sozinho.
 *
 * O risco é concreto, não teórico: o Google compara o Data Safety Form com o que
 * o APK pede, e a política pública é o documento que sustenta esse formulário.
 * Uma política afirmando `ACCESS_FINE_LOCATION` depois de ela ter sido removida
 * seria uma declaração falsa num texto que vale juridicamente.
 *
 * **Cobre as três cópias** (canônica em `docs/legal/`, espelho web em `docs/` e
 * asset do APK), porque a sincronia entre o `.md` e o HTML é manual — o
 * [PaginasLegaisSincronizadasTest] compara web contra asset, mas nenhum dos dois
 * contra o Markdown.
 */
class PoliticaBatecomManifestoTest {

    /** Testes JVM rodam com working dir = `android-app/app`. */
    private val manifesto = File("src/main/AndroidManifest.xml")

    private val copias = listOf(
        File("../../docs/legal/PRIVACIDADE.md"),
        File("../../docs/privacidade/index.html"),
        File("src/main/assets/legal/privacidade/index.html"),
    )

    /**
     * Permissões que o app declara, sem o prefixo `android.permission.` — é
     * assim que elas aparecem escritas no texto público.
     */
    private fun declaradasCurtas(): Set<String> {
        assertTrue("Não achei o manifesto: ${manifesto.absolutePath}", manifesto.isFile)
        return Regex("""<uses-permission\s+android:name="android\.permission\.([^"]+)"""")
            .findAll(manifesto.readText())
            .map { it.groupValues[1] }
            .toSet()
    }

    /**
     * Toda permissão que existe no Android e que poderia aparecer citada por
     * engano. Não dá pra procurar "qualquer palavra maiúscula": o documento cita
     * constantes de código legitimamente (`PRIORITY_BALANCED_POWER_ACCURACY`).
     * Esta lista é o conjunto de nomes que, se aparecerem, precisam estar no
     * manifesto.
     */
    private val permissoesConhecidas = setOf(
        "INTERNET",
        "ACCESS_NETWORK_STATE",
        "ACCESS_COARSE_LOCATION",
        "ACCESS_FINE_LOCATION",
        "ACCESS_BACKGROUND_LOCATION",
        "RECEIVE_BOOT_COMPLETED",
        "POST_NOTIFICATIONS",
        "READ_EXTERNAL_STORAGE",
        "WRITE_EXTERNAL_STORAGE",
        "CAMERA",
        "RECORD_AUDIO",
        "READ_CONTACTS",
        "FOREGROUND_SERVICE",
        "WAKE_LOCK",
        "VIBRATE",
        "BILLING",
    )

    @Test
    fun `politica nao cita permissao que o app nao pede`() {
        val declaradas = declaradasCurtas()

        for (copia in copias) {
            assertTrue("Não achei a cópia: ${copia.absolutePath}", copia.isFile)
            val texto = copia.readText()

            val citadasIndevidamente = permissoesConhecidas
                .filter { it !in declaradas && texto.contains(it) }
                .toSet()

            assertTrue(
                "${copia.name} cita permissão que o app NÃO pede: $citadasIndevidamente.\n" +
                    "A política é documento público que sustenta o Data Safety Form — " +
                    "afirmar coleta que não existe é declaração falsa, e divergir do " +
                    "que o APK pede é a causa nº1 de rejeição na Play.\n" +
                    "Se a permissão foi removida do manifesto, remova das TRÊS cópias " +
                    "do documento no mesmo commit.",
                citadasIndevidamente.isEmpty(),
            )
        }
    }

    @Test
    fun `politica cita todas as permissoes que o app pede`() {
        val declaradas = declaradasCurtas()

        for (copia in copias) {
            val texto = copia.readText()

            val ausentes = declaradas.filterNot { texto.contains(it) }.toSet()

            assertTrue(
                "${copia.name} NÃO menciona permissão que o app pede: $ausentes.\n" +
                    "A seção 5 da política precisa listar todas — omitir uma é o mesmo " +
                    "problema de divergência, só na direção contrária.\n" +
                    "Permissão nova exige atualizar as três cópias do documento, a " +
                    "tabela em docs/dev/CHECKLIST_PUBLICACAO.md e as respostas em " +
                    "docs/dev/GUIA_PLAY_CONSOLE.md.",
                ausentes.isEmpty(),
            )
        }
    }
}
