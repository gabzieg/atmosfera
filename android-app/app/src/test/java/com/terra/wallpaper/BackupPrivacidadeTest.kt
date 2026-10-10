package com.terra.wallpaper

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element

/** Regressão: os dois formatos Android devem preservar as exclusões de privacidade. */
class BackupPrivacidadeTest {
    private fun xml(path: String) = DocumentBuilderFactory.newInstance().apply {
        isNamespaceAware = true
    }.newDocumentBuilder().parse(File(path))

    private fun conferirExclusoes(section: Element) {
        val nodes = section.getElementsByTagName("exclude")
        val exclusions = (0 until nodes.length).map { nodes.item(it) as Element }
            .filter { it.getAttribute("domain") == "sharedpref" }
            .map { it.getAttribute("path") }.toSet()
        assertTrue("Não transportar coordenadas", "atmosfera_weather_cache.xml" in exclusions)
        assertTrue("Não transportar pausas da MET", "atmosfera_met_freio.xml" in exclusions)
        assertTrue("Restaurar Premium pela Play", "atmosfera_plano.xml" in exclusions)
        assertFalse("Preservar preferências comuns", "." in exclusions)
        assertEquals("Não substituir backup por lista parcial", 0,
            section.getElementsByTagName("include").length)
    }

    @Test fun `backup legado exclui localizacao freio e premium`() {
        val rules = xml("src/main/res/xml/backup_rules.xml")
        assertEquals("full-backup-content", rules.documentElement.tagName)
        conferirExclusoes(rules.documentElement)
    }

    @Test fun `nuvem e transferencia excluem os mesmos dados locais`() {
        val rules = xml("src/main/res/xml/data_extraction_rules.xml")
        for (mode in listOf("cloud-backup", "device-transfer")) {
            val sections = rules.getElementsByTagName(mode)
            assertEquals(mode, 1, sections.length)
            conferirExclusoes(sections.item(0) as Element)
        }
    }

    @Test fun `manifesto usa as duas regras verificadas`() {
        val app = xml("src/main/AndroidManifest.xml")
            .getElementsByTagName("application").item(0) as Element
        val android = "http://schemas.android.com/apk/res/android"
        assertEquals("@xml/backup_rules", app.getAttributeNS(android, "fullBackupContent"))
        assertEquals("@xml/data_extraction_rules", app.getAttributeNS(android, "dataExtractionRules"))
        assertEquals("true", app.getAttributeNS(android, "allowBackup"))
    }
}
