package com.atmosfera.wallpaper.engine

/**
 * Config de RENDER de cada cenário — porte das CENAS do protótipo web
 * (atmosfera 2.0/index.js). Separado do [Catalogo] (que é a vitrine/loja).
 *
 * Cada cena tem seu tamanho lógico, trajetória de sol/lua e flags do que
 * desenhar (luzes/chaminé/vagalumes/acúmulo são específicos da cabana).
 * [variantes] = artes de FUNDO alternativas (clay/aqua) — o mesmo layout,
 * só a arte do fundo muda; zonas/luzes/profundidade continuam compartilhadas.
 */
data class Astros(val x0: Float, val x1: Float)
data class Astro(val escala: Float, val yBase: Float, val yPico: Float, val fadeY: Float = 215f)
data class Faixas(val horizonte: Int, val supCen: Int, val cenInf: Int)
data class VarFundo(val prefixo: String, val v: Int)

class CenaCfg(
    val id: String,
    val prefixo: String,           // pasta base dos assets ("" = raiz/cabana)
    val tipo: String,              // "cabana" | "tanque" | "fiordes"
    val cenaW: Float,
    val cenaH: Float,
    val astros: Astros,
    val sol: Astro,
    val lua: Astro,
    val temAcumulo: Boolean,       // neve assenta (overlay próprio)
    val luzesCabana: Boolean,      // janelas/lampiões
    val chamine: Boolean,          // fumaça
    val vagalumes: Boolean,        // vagalumes na vegetação
    val taxaParcial: Float,        // multiplicador do spawn de impacto parcial
    val taxaCompleto: Float,
    val faixas: Faixas? = null,    // profundidade → escala do impacto (tanque)
    val variantes: Map<String, VarFundo> = emptyMap(),
) {
    /** Pasta do fundo/frente para a ARTE escolhida (variante ou base=pixel). */
    fun fundoPrefixo(arte: String): String = (variantes[arte]?.prefixo) ?: prefixo
}

object Cenas {
    private val map: Map<String, CenaCfg> = mapOf(
        "cabana" to CenaCfg(
            id = "cabana", prefixo = "", tipo = "cabana",
            cenaW = 688f, cenaH = 1538f,
            astros = Astros(-10f, 698f),
            sol = Astro(2f, 320f, 60f),
            lua = Astro(1.5f, 320f, 45f, fadeY = 215f),
            temAcumulo = true, luzesCabana = true, chamine = true, vagalumes = true,
            taxaParcial = 1f, taxaCompleto = 1f,
            variantes = mapOf(
                "clay" to VarFundo("cenas/cabana_clay/", 1),
                "aqua" to VarFundo("cenas/cabana_aqua/", 1),
            ),
        ),
        "tanque" to CenaCfg(
            id = "tanque", prefixo = "cenas/tanque/", tipo = "tanque",
            cenaW = 688f, cenaH = 1536f,
            astros = Astros(-10f, 698f),
            sol = Astro(3.2f, 540f, 90f),
            lua = Astro(2.4f, 540f, 70f, fadeY = 470f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 9f, taxaCompleto = 3f,
            faixas = Faixas(566, 750, 1088),
        ),
        "fiordes" to CenaCfg(
            id = "fiordes", prefixo = "cenas/fiordes/", tipo = "fiordes",
            cenaW = 841f, cenaH = 1870f,
            astros = Astros(-12f, 853f),
            sol = Astro(2.4f, 570f, 340f),
            lua = Astro(1.9f, 570f, 365f, fadeY = 540f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 4f, taxaCompleto = 6f,
        ),
    )

    fun por(id: String): CenaCfg = map[id] ?: map.getValue("cabana")
}
