package com.terra.wallpaper.engine

import android.content.Context
import android.graphics.Color
import com.terra.wallpaper.billing.Plano
import com.terra.wallpaper.debug.DebugOverride

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
    const val GRATIS = "pixel"

    /** O serviço também usa esta leitura: um estilo pago salvo nunca contorna a posse. */
    fun atual(c: Context): String {
        val salvo = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, GRATIS) ?: GRATIS
        return if (salvo in Estilos.ids && (salvo == GRATIS || Plano.isPremium(c))) salvo else GRATIS
    }

    /** Recusa IDs desconhecidos e estilos pagos sem Premium, inclusive fora da UI. */
    fun definir(c: Context, id: String): Boolean {
        if (id !in Estilos.ids || (id != GRATIS && !Plano.isPremium(c))) return false
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, id).apply()
        return true
    }
}

/** Arte do FUNDO da cena (independente do estilo dos efeitos). Default pixel. */
object ArteFundo {
    private const val PREFS = "atmosfera_estilo"
    private const val KEY = "arte_fundo"
    /**
     * O serviço também usa esta leitura. Uma arte removida ou Premium sem posse
     * cai na arte grátis do cenário, inclusive logo após atualizar o app.
     */
    fun atual(c: Context): String {
        val salvo = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY, "pixel") ?: "pixel"
        val cenario = Catalogo.por(Cena.atual(c)) ?: Catalogo.padrao
        val liberado = salvo in cenario.artes && (
            cenario.gratis ||
                salvo in cenario.artesGratis ||
                Plano.isPremium(c) ||
                DebugOverride.destravarPagos(c)
            )
        return if (liberado) salvo else cenario.artesGratis.firstOrNull() ?: cenario.artes.first()
    }
    fun definir(c: Context, id: String) =
        c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, id).apply()
}
