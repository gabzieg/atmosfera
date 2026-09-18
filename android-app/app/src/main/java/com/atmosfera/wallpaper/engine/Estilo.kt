package com.atmosfera.wallpaper.engine

import android.content.Context
import android.graphics.Color

/**
 * Estilos de arte dos EFEITOS — porte das ESTILOS do protótipo web.
 * Todos os packs usam AS MESMAS coordenadas do atlas; só a FOLHA e a
 * resolução ([res]) mudam. Trocar de estilo = trocar a folha + multiplicar
 * o src-rect por [res]. [suave] liga a suavização (pixel = cru/nearest).
 */
class EstiloCfg(
    val id: String,
    val arquivo: String,
    val res: Int,
    val suave: Boolean,
    /**
     * Só a folha PIXEL tem um GLOW de verdade no slot `glow_lampiao`; os packs
     * de estilo trazem uma LANTERNA (objeto), que colada na parede fica
     * esquisita à noite — e a ARTE da cena já desenha o lampião. Quando false
     * (padrão), o motor ACENDE o lampião com um halo radial quente.
     */
    val lampiaoSprite: Boolean = false,
    /**
     * Receita da RAJADA de vento. Cada [FitaVento] é uma passada de pincel sobre
     * a mesma curva, deslocada PERPENDICULAR ao traço — é o que empilha fitas de
     * tinta lado a lado (Van Gogh). null = risco claro único (padrão).
     */
    val wisp: List<FitaVento>? = null,
    /** 0..1: quanto o alpha oscila ao longo da curva (pincelada em toques). */
    val wispDab: Float = 0f,
)

/** Uma passada de pincel da rajada: cor, peso do alpha, deslocamento e largura. */
class FitaVento(val cor: Int, val aMul: Float, val off: Float, val wMul: Float)

object Estilos {
    private val map: Map<String, EstiloCfg> = mapOf(
        "pixel" to EstiloCfg("pixel", "sprites.png", 1, false, lampiaoSprite = true),
        "clay" to EstiloCfg("clay", "sprites_clay.png", 3, true),
        "bizantino" to EstiloCfg("bizantino", "sprites_bizantino.png", 3, false),
        "aqua" to EstiloCfg("aqua", "sprites_aqua.png", 3, true),
        "ukiyoe" to EstiloCfg("ukiyoe", "sprites_ukiyo.png", 3, true),
        "low_poly" to EstiloCfg("low_poly", "sprites_low_poly.png", 3, true),
        "fantasia" to EstiloCfg("fantasia", "sprites_fantasia.png", 3, true),
        "minimalista" to EstiloCfg("minimalista", "sprites_minimalista.png", 3, true),
        "papel_recortado" to EstiloCfg("papel_recortado", "sprites_papel_recortado.png", 3, true),
        "rupestre" to EstiloCfg("rupestre", "sprites_rupestre.png", 3, true),
        "pontilhismo" to EstiloCfg("pontilhismo", "sprites_pontilhismo.png", 3, true),
        "talhe_doce" to EstiloCfg("talhe_doce", "sprites_talhe_doce.png", 3, true),
        "doodle" to EstiloCfg("doodle", "sprites_doodle.png", 3, true),
        "papel_mache" to EstiloCfg("papel_mache", "sprites_papel_mache.png", 3, true),
        "feltro" to EstiloCfg("feltro", "sprites_feltro.png", 3, true),
        "van_gogh" to EstiloCfg("van_gogh", "sprites_van_gogh.png", 3, true,
            wisp = listOf(
                FitaVento(Color.rgb(24, 40, 96), 0.60f, 2.0f, 1.7f),
                FitaVento(Color.rgb(58, 96, 168), 0.50f, -1.9f, 1.2f),
                FitaVento(Color.rgb(246, 238, 186), 1.00f, 0.0f, 0.85f),
                FitaVento(Color.rgb(250, 206, 88), 0.85f, 1.1f, 0.55f)),
            wispDab = 0.55f),
    )

    fun por(id: String): EstiloCfg = map[id] ?: map.getValue("pixel")
    val ids get() = map.keys.toList()
}

/** Estilo de efeito ativo (persistido). O front grava; o serviço lê. */
object EstiloEfeito {
    private const val PREFS = "atmosfera_estilo"
    private const val KEY = "efeito"
    fun atual(c: Context): String =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "pixel") ?: "pixel"
    fun definir(c: Context, id: String) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, id).apply()
}

/** Arte do FUNDO da cena (independente do estilo dos efeitos). Default pixel. */
object ArteFundo {
    private const val PREFS = "atmosfera_estilo"
    private const val KEY = "arte_fundo"
    fun atual(c: Context): String =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "pixel") ?: "pixel"
    fun definir(c: Context, id: String) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, id).apply()
}
