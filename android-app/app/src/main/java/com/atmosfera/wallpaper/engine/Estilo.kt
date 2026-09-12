package com.atmosfera.wallpaper.engine

import android.content.Context

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
)

object Estilos {
    private val map: Map<String, EstiloCfg> = mapOf(
        "pixel" to EstiloCfg("pixel", "sprites.png", 1, false, lampiaoSprite = true),
        "clay" to EstiloCfg("clay", "sprites_clay.png", 3, true),
        "bizantino" to EstiloCfg("bizantino", "sprites_bizantino.png", 3, false),
        "aqua" to EstiloCfg("aqua", "sprites_aqua.png", 3, true),
        "ukiyoe" to EstiloCfg("ukiyoe", "sprites_ukiyo.png", 3, true),
        "lowpoly" to EstiloCfg("lowpoly", "sprites_lowpoly.png", 3, true),
        "rpg" to EstiloCfg("rpg", "sprites_rpg.png", 3, true),
        "simplao" to EstiloCfg("simplao", "sprites_simplao.png", 3, true),
        // ── Packs novos (recorte 2026-07-27) ──
        "paper_cutout" to EstiloCfg("paper_cutout", "sprites_paper_cutout.png", 3, true),
        "paper_cutout_2" to EstiloCfg("paper_cutout_2", "sprites_paper_cutout_2.png", 3, true),
        "paper_cutout_3" to EstiloCfg("paper_cutout_3", "sprites_paper_cutout_3.png", 3, true),
        "rupestre_og" to EstiloCfg("rupestre_og", "sprites_rupestre_og.png", 3, true),
        "rupestre_1" to EstiloCfg("rupestre_1", "sprites_rupestre_1.png", 3, true),
        "rupestre_2" to EstiloCfg("rupestre_2", "sprites_rupestre_2.png", 3, true),
        "rupestre_gemini" to EstiloCfg("rupestre_gemini", "sprites_rupestre_gemini.png", 3, true),
        "point_gpt" to EstiloCfg("point_gpt", "sprites_point_gpt.png", 3, true),
        "point_gpt_2" to EstiloCfg("point_gpt_2", "sprites_point_gpt_2.png", 3, true),
        "pointilismo" to EstiloCfg("pointilismo", "sprites_pointilismo.png", 3, true),
        "talhe_doce_og" to EstiloCfg("talhe_doce_og", "sprites_talhe_doce_og.png", 3, true),
        "talhe_doce" to EstiloCfg("talhe_doce", "sprites_talhe_doce.png", 3, true),
        "pixel_art2" to EstiloCfg("pixel_art2", "sprites_pixel_art2.png", 3, false),
        "doodle" to EstiloCfg("doodle", "sprites_doodle.png", 3, true),
        "papel_mache" to EstiloCfg("papel_mache", "sprites_papel_mache.png", 3, true),
        "papel_mache_2" to EstiloCfg("papel_mache_2", "sprites_papel_mache_2.png", 3, true),
        "needle_felting" to EstiloCfg("needle_felting", "sprites_needle_felting.png", 3, true),
        "pixel_mario" to EstiloCfg("pixel_mario", "sprites_pixel_mario.png", 3, false),
        "pixel_zelda" to EstiloCfg("pixel_zelda", "sprites_pixel_zelda.png", 3, false),
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
