package com.atmosfera.wallpaper.engine

/**
 * Atlas de sprites e constantes geométricas do motor de efeitos.
 * Porte fiel do ATLAS/constantes do protótipo web (atmosfera 2.0/index.js).
 * Todas as coordenadas são em px na folha `sprites.png` e no fundo 688×1538.
 */

/** Retângulo de origem de um sprite dentro de `sprites.png`. */
data class Sprite(val x: Int, val y: Int, val w: Int, val h: Int)

/** Posição/área de uma luz da cabana (coords do fundo 688×1538). */
data class Luz(val x: Float, val y: Float, val w: Float, val h: Float)

object Atlas {

    // Dimensões nativas do cenário (o fundo é desenhado nesse espaço lógico).
    const val CENA_W = 688f
    const val CENA_H = 1538f

    // ── Todos os sprites, por nome (lookup dinâmico p/ nuvens, lua, folha…) ──
    private val map: Map<String, Sprite> = mapOf(
        // chuva
        "pingo" to Sprite(28, 50, 5, 15),
        "parcial_1" to Sprite(108, 74, 7, 15),
        "parcial_2" to Sprite(125, 80, 7, 9),
        "completa_1" to Sprite(140, 84, 9, 6),
        "completa_2" to Sprite(158, 83, 11, 7),
        // sol (meio-dia / manhã-tarde / nascer-pôr)
        "sol_1" to Sprite(200, 762, 34, 34),
        "sol_2" to Sprite(244, 762, 34, 34),
        "sol_3" to Sprite(288, 762, 34, 34),
        // raios
        "raio_a1" to Sprite(16, 758, 28, 48),
        "raio_a2" to Sprite(56, 758, 28, 48),
        "raio_b1" to Sprite(96, 766, 22, 40),
        "raio_b2" to Sprite(128, 766, 22, 40),
        // neve (flocos + tufos)
        "floco_g" to Sprite(16, 836, 7, 7),
        "floco_m" to Sprite(34, 838, 5, 5),
        "floco_p" to Sprite(46, 840, 2, 2),
        "tufo_g" to Sprite(64, 836, 16, 6),
        "tufo_m" to Sprite(90, 838, 10, 4),
        "tufo_p" to Sprite(110, 838, 6, 3),
        // nuvens leves
        "nuvem_1" to Sprite(16, 878, 82, 16),
        "nuvem_2" to Sprite(110, 878, 66, 14),
        "nuvem_3" to Sprite(188, 878, 83, 13),
        "nuvem_0" to Sprite(284, 878, 127, 37),
        "nuvem_4" to Sprite(424, 878, 50, 19),
        // nuvens escuras
        "nuvem_e1" to Sprite(16, 966, 82, 16),
        "nuvem_e2" to Sprite(110, 966, 66, 14),
        "nuvem_e3" to Sprite(188, 966, 83, 13),
        "nuvem_e0" to Sprite(284, 966, 127, 37),
        "nuvem_e4" to Sprite(424, 966, 50, 19),
        // nuvens médias
        "nuvem_m1" to Sprite(16, 1054, 82, 16),
        "nuvem_m2" to Sprite(110, 1054, 66, 14),
        "nuvem_m3" to Sprite(188, 1054, 83, 13),
        "nuvem_m0" to Sprite(284, 1054, 127, 37),
        "nuvem_m4" to Sprite(424, 1054, 50, 19),
        // noite
        "glow_janela" to Sprite(16, 1120, 56, 56),
        "glow_lampiao" to Sprite(84, 1128, 40, 40),
        "estrela_1" to Sprite(140, 1130, 3, 3),
        "estrela_2" to Sprite(160, 1128, 5, 5),
        "estrela_3" to Sprite(184, 1126, 7, 7),
        "vagalume" to Sprite(216, 1130, 9, 9),
        "cadente" to Sprite(244, 1128, 34, 12),
        // lua (7 fases)
        "lua_1" to Sprite(16, 1200, 33, 33),
        "lua_2" to Sprite(56, 1200, 33, 33),
        "lua_3" to Sprite(96, 1200, 33, 33),
        "lua_4" to Sprite(136, 1200, 33, 33),
        "lua_5" to Sprite(176, 1200, 33, 33),
        "lua_6" to Sprite(216, 1200, 33, 33),
        "lua_7" to Sprite(256, 1200, 33, 33),
        // fumaça + folhas
        "fumaca_1" to Sprite(16, 1256, 26, 26),
        "fumaca_2" to Sprite(52, 1256, 28, 28),
        "fumaca_3" to Sprite(92, 1256, 24, 24),
        "folha_1" to Sprite(140, 1262, 15, 9),
        "folha_2" to Sprite(164, 1262, 13, 8),
        "folha_3" to Sprite(186, 1262, 14, 8),
        // estalactite de gelo (pendura do beiral)
        "estalactite" to Sprite(232, 1250, 10, 46),
    )

    operator fun get(name: String): Sprite = map.getValue(name)

    // ── Grupos ──────────────────────────────────────────────────────
    val nuvensLeves = listOf("nuvem_0", "nuvem_1", "nuvem_2", "nuvem_3", "nuvem_4")
    val nuvensMedias = listOf("nuvem_m0", "nuvem_m1", "nuvem_m2", "nuvem_m3", "nuvem_m4")
    val nuvensEscuras = listOf("nuvem_e0", "nuvem_e1", "nuvem_e2", "nuvem_e3", "nuvem_e4")
    fun cloudSet(nome: String) = when (nome) {
        "medias" -> nuvensMedias
        "escuras" -> nuvensEscuras
        else -> nuvensLeves
    }

    val raioModelos = listOf(listOf("raio_a1", "raio_a2"), listOf("raio_b1", "raio_b2"))
    val luaFases = listOf("lua_1", "lua_2", "lua_3", "lua_4", "lua_5", "lua_6", "lua_7")
    val fumacaSprites = listOf("fumaca_1", "fumaca_2", "fumaca_3")
    val folhasSprites = listOf("folha_1", "folha_2", "folha_3")
    val flocosSprites = listOf("floco_p", "floco_m", "floco_g")

    val seqTelhado = listOf("parcial_1", "parcial_2")
    val seqLago = listOf("parcial_1", "parcial_2", "completa_1", "completa_2")

    // ── Geometria ───────────────────────────────────────────────────
    /** dx/dy da arte do pingo — a física da chuva segue a inclinação da arte. */
    const val SLANT = 4f / 14f

    // Sol: parábola calculada. Nasce à DIREITA, se põe à esquerda (igual à lua).
    object SolCfg {
        const val nascer = 6.0f
        const val por = 18.5f
        const val x0 = -10f
        const val x1 = 698f
        const val yBase = 320f
        const val yPico = 60f
        const val escala = 2f
    }

    // Lua: mesma parábola, cruzando a noite (pôr → nascer).
    object LuaCfg {
        const val yBase = 320f
        const val yPico = 45f
        const val escala = 1.5f
    }
    val NOITE_DUR = (24f - SolCfg.por) + SolCfg.nascer

    // Luzes da cabana — áreas completas (marcadas em laranja no FUNDO_MARCACAO).
    val janelas = listOf(
        Luz(299f, 771f, 20f, 33f), // sótão
        Luz(120f, 878f, 27f, 39f), // lateral esquerda
        Luz(324f, 865f, 21f, 38f), // lateral direita
    )
    val lampioes = listOf(
        Luz(225f, 884f, 13f, 12f), // parede
        Luz(269f, 969f, 11f, 11f), // chão
    )
    const val SPILL_VIDRO = 2.1f
    const val SPILL_LAMPIAO = 3.6f

    // Chaminé: boca de onde a fumaça sobe (x, y, largura da boca).
    object Chamine {
        const val x = 166f
        const val y = 649f
        const val w = 24f
    }

    // Pontos do beiral do telhado onde as estalactites de gelo penduram
    // (topo do pingente em coords do fundo 688×1538).
    val estalactites = listOf(
        49f to 808f, 65f to 812f, 81f to 817f, 97f to 821f,
        113f to 824f, 129f to 825f, 145f to 830f, 161f to 833f,
        177f to 839f, 193f to 845f, 209f to 848f, 225f to 842f,
    )

    const val VENTO_MIN = 8f     // km/h a partir do qual o vento aparece
    const val NEVE_TEMP = 1f     // ≤ isto, precipitação vira neve
    const val TEMP_FOGO = 15f    // chaminé acende com frio
    const val TEMP_INTENSO = 5f  // frio intenso → fumaça densa
}
