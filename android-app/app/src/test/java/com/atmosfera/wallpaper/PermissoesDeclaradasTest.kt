package com.atmosfera.wallpaper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import java.io.File
import org.junit.Test

/**
 * Trava a lista de permissões do `AndroidManifest.xml`.
 *
 * **Por que um teste e não confiança:** o Google compara o que o Data Safety
 * Form declara com o que o APK realmente pede, e divergência entre os dois é a
 * causa nº 1 de rejeição na Play Store. Uma permissão adicionada sem querer —
 * por um merge, por um snapshot do motor, por copiar exemplo de doc — não
 * quebra nada em tempo de build e passa despercebida até a revisão do Google
 * reprovar. Aqui ela quebra o gate na hora.
 *
 * `ACCESS_FINE_LOCATION` é o caso concreto que motivou isto: ficou declarada e
 * era PEDIDA ao usuário por muito tempo, sem que nenhum caminho do código
 * exigisse precisão fina (o portão é `LocationHelper.hasPermission()`, que só
 * checa COARSE, e a busca pede `PRIORITY_BALANCED_POWER_ACCURACY`). Foi
 * removida em 2026-08-08; readicioná-la obriga a declarar "localização precisa"
 * no formulário e a rever a política de privacidade.
 *
 * **Mudou permissão de propósito?** Atualize a lista abaixo E, no mesmo commit:
 *  - a tabela de permissões em `docs/dev/CHECKLIST_PUBLICACAO.md`;
 *  - as respostas em `docs/dev/GUIA_PLAY_CONSOLE.md`;
 *  - a seção de permissões em `docs/legal/PRIVACIDADE.md` e seus dois espelhos.
 */
class PermissoesDeclaradasTest {

    /**
     * O conjunto exato esperado. Cada uma com o motivo de existir — se você não
     * consegue escrever o motivo de uma nova, ela provavelmente não deveria
     * entrar.
     */
    private val esperadas = setOf(
        // Buscar o clima na Open-Meteo.
        "android.permission.INTERNET",
        "android.permission.ACCESS_NETWORK_STATE",
        // Localização APROXIMADA apenas — escolhe a cidade do clima.
        // Não readicionar ACCESS_FINE_LOCATION sem uso real (ver KDoc).
        "android.permission.ACCESS_COARSE_LOCATION",
        // Reagendar a atualização periódica de clima após reiniciar o aparelho.
        "android.permission.RECEIVE_BOOT_COMPLETED",
    )

    /** Testes JVM rodam com working dir = `android-app/app`. */
    private val manifesto = File("src/main/AndroidManifest.xml")

    private fun declaradas(): Set<String> {
        assertTrue("Não achei o manifesto: ${manifesto.absolutePath}", manifesto.isFile)
        // `uses-permission` só — ignora `permission` de assinatura (ex.: o
        // BIND_WALLPAPER exigido do sistema, que o app não PEDE, e sim requer).
        return Regex("""<uses-permission\s+android:name="([^"]+)"""")
            .findAll(manifesto.readText())
            .map { it.groupValues[1] }
            .toSet()
    }

    @Test
    fun `manifesto declara exatamente as permissoes esperadas`() {
        val atuais = declaradas()

        val novas = atuais - esperadas
        assertTrue(
            "Permissão NOVA no manifesto: $novas. Isso muda o Data Safety Form — " +
                "declarar diferente do que o APK pede é a causa nº1 de rejeição. " +
                "Se for intencional, atualize a lista deste teste e a documentação " +
                "citada no KDoc, no mesmo commit.",
            novas.isEmpty(),
        )

        val removidas = esperadas - atuais
        assertTrue(
            "Permissão SUMIU do manifesto: $removidas. Se foi de propósito, tire " +
                "da lista deste teste e revise a política de privacidade junto.",
            removidas.isEmpty(),
        )

        assertEquals(esperadas, atuais)
    }

    @Test
    fun `nao pede localizacao precisa`() {
        assertTrue(
            "ACCESS_FINE_LOCATION voltou ao manifesto. O app não precisa dela — " +
                "o portão é LocationHelper.hasPermission(), que checa só COARSE, e a " +
                "busca usa PRIORITY_BALANCED_POWER_ACCURACY (~100 m). Mantê-la obriga " +
                "a declarar 'localização precisa' no Data Safety Form, que atrai " +
                "escrutínio maior sem ganho funcional. Ver GUIA_PLAY_CONSOLE.md.",
            "android.permission.ACCESS_FINE_LOCATION" !in declaradas(),
        )
    }
}
