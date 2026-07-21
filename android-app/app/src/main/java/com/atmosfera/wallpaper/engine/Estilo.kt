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
)

object Estilos {
    private val map: Map<String, EstiloCfg> = mapOf(
        "pixel" to EstiloCfg("pixel", "sprites.png", 1, false),
        "clay" to EstiloCfg("clay", "sprites_clay.png", 3, true),
        "bizantino" to EstiloCfg("bizantino", "sprites_bizantino.png", 3, false),
        "aqua" to EstiloCfg("aqua", "sprites_aqua.png", 3, true),
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
