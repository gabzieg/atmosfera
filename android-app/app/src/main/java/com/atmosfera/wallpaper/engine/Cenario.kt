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

/**
 * Farol: a lanterna que gira. [x]/[y] = posição da lanterna (coords da cena),
 * [periodo] = segundos por volta completa, [alcance] = comprimento do facho em
 * frações da largura da cena.
 */
data class Feixe(val x: Float, val y: Float, val periodo: Float = 9f, val alcance: Float = 1.5f)

/**
 * Bandeira no mastro, desenhada pelo motor (o pano NÃO está pintado na arte).
 * Parada ela cai ao longo do mastro; ventando estica e ondula.
 * [x]/[y] = ponto de amarra no mastro · [comp]/[alt] = pano em coords da cena.
 */
data class Bandeira(val x: Float, val y: Float, val comp: Float,
                    /** folha de frames: "pixel" (padrão) ou "clay". */
                    val folha: String = "pixel")
/**
 * Arte de fundo alternativa. [sol]/[lua] são OPCIONAIS: quando a arte tem
 * composição própria (fiordes clay, com a montanha bem mais alta), o arco da
 * cena deixava a lua passar NA FRENTE do relevo — aí a arte traz o seu.
 */
data class VarFundo(val prefixo: String, val v: Int,
                    val sol: Astro? = null, val lua: Astro? = null,
                    /** sprite de remo desta arte (o navio ao longe usa o menor). */
                    val remoSprite: String? = null,
                    /** true = esta arte já tem a bandeira PINTADA, não desenhar. */
                    val semBandeira: Boolean = false,
                    /** bandeira própria desta arte (folha/posição diferentes). */
                    val bandeira: Bandeira? = null)

class CeuMovel(val vel: Float = 8f)

/**
 * REMOS (navio viking). A primeira cena em que o que se mexe faz parte do
 * OBJETO, e não do clima: a arte vem SEM remo nenhum, só com as portinholas
 * (ver `Ppt - Navio Viking.txt`), e o motor põe UM sprite girado uma vez por
 * portinhola, com um atraso entre um e o vizinho. É o atraso que faz a
 * centopeia. Os eixos e a linha d'água saem de `tools/remos.py`, na
 * `remos.json` de cada ARTE.
 *
 * @param comp comprimento do remo em ESPAÇAMENTOS de portinhola (num drakkar o
 *   remo dá umas 5 vezes a distância entre um banco e o outro) — amarrar no
 *   espaçamento faz o navio de longe herdar a proporção sem número novo.
 * @param pivo onde fica o eixo, em fração do sprite. 0.05 e não 0.30 (que é
 *   onde o remo de verdade apoia) porque a parte de DENTRO fica escondida no
 *   casco: desenhá-la punha o cabo por cima da fileira de escudos.
 * @param passo fração de ciclo entre um remo e o vizinho. 0 = remada militar
 *   (todos juntos), 0.055 = a centopeia.
 */
class CfgRemos(
    val sprite: String = "remo.png",
    val comp: Float = 4.6f,
    val pivo: Float = 0.05f,
    val ang: Float = 134f,
    val curso: Float = 14f,
    val periodo: Float = 3600f,
    val passo: Float = 0.055f,
    val espuma: Float = 0.22f,
)

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
    /**
     * Multiplicador do tamanho do respingo, por cena. Existe porque o "cover"
     * escala diferente conforme a PROPORÇÃO da arte: cena 9:20 (841x1870) chega
     * na tela em 1.28x, cena 3:4 (1086x1448) em 1.66x — e o respingo sairia 30%
     * maior nas 3:4 sem nada mudar no sprite. Espelha o `escImpacto` do tester.
     */
    val escImpacto: Float = 1f,
    val faixas: Faixas? = null,    // profundidade → escala do impacto (tanque)
    val feixe: Feixe? = null,      // farol: lanterna giratória
    val bandeira: Bandeira? = null,// mastro com pano desenhado pelo motor
    // CÉU ROLANTE ("panorama rolante", o pano de teatro num rolo): quando a
    // cena tem `ceu.png`, o motor deixa de desenhar o céu PINTADO e passa essa
    // tira larga em loop atrás da silhueta. `vel` em px de cena por segundo.
    val ceuMovel: CeuMovel? = null,
    /** Cena com REMOS animados (navio viking). Ver [CfgRemos]. */
    val remos: CfgRemos? = null,
    /**
     * Cena de INTERIOR com janelão: tem `vidro.png` (máscara de onde há vidro,
     * gerada por tools/vidro.py) e ganha a água escorrendo. Ver o apê fino.
     */
    val vidro: Boolean = false,
    /**
     * Multiplicador do tamanho do PINGO por cena. No apê fino a chuva que
     * aparece está do outro lado do vidro, a dezenas de metros: com o pingo do
     * tamanho padrão parecia chuva caindo dentro da sala.
     */
    val escChuva: Float = 1f,
    /**
     * CÉU POLAR: cortinas de aurora à noite, desenhadas pelo motor (a arte
     * entrega o céu vazio). A banda vai do topo da arte até o horizonte que
     * sol/lua já declaram em `yBase`. Ver EffectEngine.desenharAurora.
     */
    val aurora: Boolean = false,
    val variantes: Map<String, VarFundo> = emptyMap(),
) {
    /** Pasta do fundo/frente para a ARTE escolhida (variante ou base=pixel). */
    /**
     * Pasta da ARTE ativa. Layout: `cenas/<cena>/` guarda o que é COMPARTILHADO
     * (zonas, profundidade, névoa) e `cenas/<cena>/<arte>/` guarda fundo/frente/
     * luzes_off. A arte base é a pasta `pixel`. Arte que a cena não tem cai na
     * base — antes caía no prefixo compartilhado, que já não tem fundo.png.
     */
    fun fundoPrefixo(arte: String): String =
        variantes[arte]?.prefixo ?: variantes["pixel"]?.prefixo ?: prefixo

    /** Sprite de remo da ARTE ativa (o navio ao longe usa o menor). */
    fun remoSpriteDe(arte: String): String =
        variantes[arte]?.remoSprite ?: remos?.sprite ?: "remo.png"

    /** Trajetória do sol/lua da ARTE ativa (a variante pode ter a sua). */
    fun solDe(arte: String): Astro = variantes[arte]?.sol ?: sol
    fun luaDe(arte: String): Astro = variantes[arte]?.lua ?: lua

    /** Bandeira da ARTE ativa (a variante pode já ter a dela pintada). */
    fun bandeiraDe(arte: String): Bandeira? {
        val v = variantes[arte]
        if (v?.semBandeira == true) return null
        return v?.bandeira ?: bandeira
    }
}

object Cenas {
    private val map: Map<String, CenaCfg> = mapOf(
        "cabana" to CenaCfg(
            id = "cabana", prefixo = "cenas/cabana/", tipo = "cabana",
            cenaW = 688f, cenaH = 1538f,
            astros = Astros(-10f, 698f),
            sol = Astro(2f, 320f, 60f),
            lua = Astro(1.5f, 320f, 45f, fadeY = 215f),
            temAcumulo = true, luzesCabana = true, chamine = true, vagalumes = true,
            taxaParcial = 1f, taxaCompleto = 1f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/cabana/pixel/", 18),
                "aqua" to VarFundo("cenas/cabana/aqua/", 1),
                "clay" to VarFundo("cenas/cabana/clay/", 1),
                "ukiyoe" to VarFundo("cenas/cabana/ukiyoe/", 1),
                "needle" to VarFundo("cenas/cabana/needle/", 1),
                "doodle" to VarFundo("cenas/cabana/doodle/", 1),
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
            // artes novas: composição própria; efeitos genéricos por ora
            variantes = mapOf(
                "pixel" to VarFundo("cenas/tanque/pixel/", 6),
                "needle" to VarFundo("cenas/tanque/needle/", 1),
                "pixelart" to VarFundo("cenas/tanque/pixelart/", 1),
            ),
        ),
        "fiordes" to CenaCfg(
            id = "fiordes", prefixo = "cenas/fiordes/", tipo = "fiordes",
            cenaW = 841f, cenaH = 1870f,
            astros = Astros(-12f, 853f),
            sol = Astro(2.4f, 570f, 340f),
            lua = Astro(1.9f, 570f, 365f, fadeY = 540f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 4f, taxaCompleto = 6f,
            aurora = true,                           // céu polar
            // mastro em x≈737: o pano é do motor (a arte clay tem a dela pintada)
            bandeira = Bandeira(737f, 516f, 110f),   // comp = pano esticado
            variantes = mapOf(
                "pixel" to VarFundo("cenas/fiordes/pixel/", 5),
                "clay" to VarFundo(
                    "cenas/fiordes/clay/", 3,
                    sol = Astro(2.0f, 420f, 150f),
                    lua = Astro(1.6f, 420f, 165f, fadeY = 240f),
                    // mastro clay em x≈798 (o pano pintado saiu da arte)
                    bandeira = Bandeira(800f, 286f, 105f, "clay"),
                ),
            ),
        ),
        // CABANA 2 — cabanas de composição própria como ARTES. As zonas vêm da
        // marcação à mão do usuário (zonas.png/json), e com elas as LUZES (3
        // janelas) e a SAÍDA DE FUMAÇA da cena, sem nada hardcoded.
        "cabana2" to CenaCfg(
            id = "cabana2", prefixo = "cenas/cabana2/", tipo = "cabana2",
            cenaW = 688f, cenaH = 1538f,
            astros = Astros(-10f, 698f),
            sol = Astro(2f, 320f, 60f),
            lua = Astro(1.5f, 320f, 45f, fadeY = 215f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 1f, taxaCompleto = 1f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/cabana2/pixel/", 2),
                "poly2" to VarFundo("cenas/cabana2/poly2/", 1),
                "ukiyoe" to VarFundo("cenas/cabana2/ukiyoe/", 1),
                "ukiyogpt" to VarFundo("cenas/cabana2/ukiyogpt/", 1),
            ),
        ),
        // JARDIM JAPONÊS — 6 artes da MESMA composição (zonas servem às 6).
        "jardim" to CenaCfg(
            id = "jardim", prefixo = "cenas/jardim/", tipo = "jardim",
            cenaW = 841f, cenaH = 1870f,
            astros = Astros(-12f, 853f),
            sol = Astro(2.2f, 500f, 120f),
            lua = Astro(1.7f, 500f, 135f, fadeY = 470f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/jardim/pixel/", 3),
                "pixel2" to VarFundo("cenas/jardim/pixel2/", 1),
                "clay" to VarFundo("cenas/jardim/clay/", 1),
                "needle" to VarFundo("cenas/jardim/needle/", 1),
                "doodle" to VarFundo("cenas/jardim/doodle/", 1),
                "ukiyoe" to VarFundo("cenas/jardim/ukiyoe/", 1),
            ),
        ),
        // MURALHA DA CHINA — 8 artes, ainda SEM marcação dele. Céu, água e luzes
        // vieram de medição minha (tools/cena_auto.py → MEDIDO): o rio tem o mesmo
        // azul da serra e precisou de sementes conferidas na arte; as lanternas do
        // portão são vermelhas, que o detector automático não pega. A distância é
        // geométrica (horizonte coluna a coluna), não marcada.
        "muralha" to CenaCfg(
            id = "muralha", prefixo = "cenas/muralha/", tipo = "muralha",
            cenaW = 841f, cenaH = 1870f,
            astros = Astros(-12f, 853f),
            sol = Astro(2.2f, 520f, 110f),
            lua = Astro(1.7f, 520f, 125f, fadeY = 500f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/muralha/pixel/", 1),
                "pixel2" to VarFundo("cenas/muralha/pixel2/", 1),
                "clay" to VarFundo("cenas/muralha/clay/", 1),
                "doodle" to VarFundo("cenas/muralha/doodle/", 1),
                "doodleinf" to VarFundo("cenas/muralha/doodleinf/", 1),
                "point" to VarFundo("cenas/muralha/point/", 1),
                "point2" to VarFundo("cenas/muralha/point2/", 1),
                "vangogh" to VarFundo("cenas/muralha/vangogh/", 1),
            ),
        ),
        // CASTELO NA MONTANHA — lote 3:4 (1086x1448), sem marcação dele: zona de pingo
        // derivada do terreno e distância geométrica. escImpacto 0.78 porque nesta
        // proporção o cover escala 1.66x (contra 1.28x das cenas 9:20).
        "castelo" to CenaCfg(
            id = "castelo", prefixo = "cenas/castelo/", tipo = "castelo",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 700f, 130f),
            lua = Astro(1.7f, 700f, 145f, fadeY = 690f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/castelo/pixel/", 1),
                "clay" to VarFundo("cenas/castelo/clay/", 1),
                "doodle" to VarFundo("cenas/castelo/doodle/", 1),
                "doodle2" to VarFundo("cenas/castelo/doodle2/", 1),
                "needle" to VarFundo("cenas/castelo/needle/", 1),
                "papel" to VarFundo("cenas/castelo/papel/", 1),
            ),
        ),
        // BECO JAPONÊS — lote 3:4 (1086x1448), sem marcação dele: zona de pingo
        // derivada do terreno e distância geométrica. escImpacto 0.78 porque nesta
        // proporção o cover escala 1.66x (contra 1.28x das cenas 9:20).
        "beco" to CenaCfg(
            id = "beco", prefixo = "cenas/beco/", tipo = "beco",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 420f, 90f),
            lua = Astro(1.7f, 420f, 105f, fadeY = 400f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/beco/pixel/", 1),
                "noite" to VarFundo("cenas/beco/noite/", 1),
                "chibi" to VarFundo("cenas/beco/chibi/", 1),
                "kodomo" to VarFundo("cenas/beco/kodomo/", 1),
                "seinen" to VarFundo("cenas/beco/seinen/", 1),
                "impress" to VarFundo("cenas/beco/impress/", 1),
                "ukiyoe" to VarFundo("cenas/beco/ukiyoe/", 1),
            ),
        ),
        // TEMPLO GREGO — lote 3:4 (1086x1448), sem marcação dele: zona de pingo
        // derivada do terreno e distância geométrica. escImpacto 0.78 porque nesta
        // proporção o cover escala 1.66x (contra 1.28x das cenas 9:20).
        "templo" to CenaCfg(
            id = "templo", prefixo = "cenas/templo/", tipo = "templo",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 560f, 120f),
            lua = Astro(1.7f, 560f, 135f, fadeY = 540f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/templo/pixel/", 1),
                "clay" to VarFundo("cenas/templo/clay/", 1),
                "cozy" to VarFundo("cenas/templo/cozy/", 1),
                "lowpoly" to VarFundo("cenas/templo/lowpoly/", 1),
                "point" to VarFundo("cenas/templo/point/", 1),
                "point2" to VarFundo("cenas/templo/point2/", 1),
                "ukiyoe" to VarFundo("cenas/templo/ukiyoe/", 1),
                "vivid" to VarFundo("cenas/templo/vivid/", 1),
            ),
        ),
        // VALE ALPINO — lote 3:4 (1086x1448), sem marcação dele: zona de pingo
        // derivada do terreno e distância geométrica. escImpacto 0.78 porque nesta
        // proporção o cover escala 1.66x (contra 1.28x das cenas 9:20).
        "vale" to CenaCfg(
            id = "vale", prefixo = "cenas/vale/", tipo = "vale",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 430f, 110f),
            lua = Astro(1.7f, 430f, 125f, fadeY = 410f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/vale/pixel/", 1),
                "cartoon" to VarFundo("cenas/vale/cartoon/", 1),
                "clay" to VarFundo("cenas/vale/clay/", 1),
                "impress" to VarFundo("cenas/vale/impress/", 1),
                "lowpoly" to VarFundo("cenas/vale/lowpoly/", 1),
                "cutout" to VarFundo("cenas/vale/cutout/", 1),
                "cartoon" to VarFundo("cenas/vale/cartoon/", 1),
            ),
        ),
        // REFÚGIO ÉLFICO — lote 3:4. SEM LUZ: o prompt V2 pede a luminária
        // DESENHADA APAGADA, então não sobra brilho pra detectar. Luz aqui só com
        // a marcação dele.
        "elfico" to CenaCfg(
            id = "elfico", prefixo = "cenas/elfico/", tipo = "elfico",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 430f, 95f),
            lua = Astro(1.7f, 430f, 110f, fadeY = 410f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/elfico/pixel/", 1),
                "cartoon" to VarFundo("cenas/elfico/cartoon/", 1),
                "clay" to VarFundo("cenas/elfico/clay/", 1),
                "cera" to VarFundo("cenas/elfico/cera/", 1),
                "lowpoly" to VarFundo("cenas/elfico/lowpoly/", 1),
                "sfumato" to VarFundo("cenas/elfico/sfumato/", 1),
                "vangogh" to VarFundo("cenas/elfico/vangogh/", 1),
            ),
        ),
        // CIDADE TOMADA — lote 3:4. SEM LUZ: o prompt V2 pede a luminária
        // DESENHADA APAGADA, então não sobra brilho pra detectar. Luz aqui só com
        // a marcação dele.
        "postapoc" to CenaCfg(
            id = "postapoc", prefixo = "cenas/postapoc/", tipo = "postapoc",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 600f, 120f),
            lua = Astro(1.7f, 600f, 135f, fadeY = 580f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/postapoc/pixel/", 1),
                "cozy" to VarFundo("cenas/postapoc/cozy/", 1),
                "iso" to VarFundo("cenas/postapoc/iso/", 1),
                "needle" to VarFundo("cenas/postapoc/needle/", 1),
                "cutout" to VarFundo("cenas/postapoc/cutout/", 1),
                "vangogh" to VarFundo("cenas/postapoc/vangogh/", 1),
            ),
        ),
        // NAVIO VIKING — a primeira cena em que o que se mexe faz parte do
        // OBJETO, e não do clima: os remos remam e a espuma quebra no casco.
        // Duas artes com ENQUADRAMENTO diferente (perto e longe), então cada
        // uma tem zona e fila de remos próprias.
        "navio" to CenaCfg(
            id = "navio", prefixo = "cenas/navio/", tipo = "navio",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 440f, 110f),
            lua = Astro(1.7f, 440f, 125f, fadeY = 430f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 14f, escImpacto = 0.78f,
            remos = CfgRemos(),
            variantes = mapOf(
                "pixel" to VarFundo("cenas/navio/pixel/", 1),
                "longe" to VarFundo("cenas/navio/longe/", 1, remoSprite = "remo_longe.png"),
            ),
        ),
        // SAVANA — lote 3:4, sem marcação. Luz zerada de propósito
        // (a luminária vem apagada; ver MAPA.md).
        "savana" to CenaCfg(
            id = "savana", prefixo = "cenas/savana/", tipo = "savana",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 820f, 140f),
            lua = Astro(1.7f, 820f, 155f, fadeY = 800f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/savana/pixel/", 1),
                "clay" to VarFundo("cenas/savana/clay/", 1),
                "impress" to VarFundo("cenas/savana/impress/", 1),
                "papel" to VarFundo("cenas/savana/papel/", 1),
                "point" to VarFundo("cenas/savana/point/", 1),
                "rupestre" to VarFundo("cenas/savana/rupestre/", 1),
                "ukiyoe" to VarFundo("cenas/savana/ukiyoe/", 1),
            ),
        ),
        // OÁSIS — lote 3:4, sem marcação. Luz zerada de propósito
        // (a luminária vem apagada; ver MAPA.md).
        "oasis" to CenaCfg(
            id = "oasis", prefixo = "cenas/oasis/", tipo = "oasis",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 560f, 110f),
            lua = Astro(1.7f, 560f, 125f, fadeY = 540f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/oasis/pixel/", 1),
                "clay" to VarFundo("cenas/oasis/clay/", 1),
                "clay2" to VarFundo("cenas/oasis/clay2/", 1),
                "cera" to VarFundo("cenas/oasis/cera/", 1),
                "cutout" to VarFundo("cenas/oasis/cutout/", 1),
                "pixel2" to VarFundo("cenas/oasis/pixel2/", 1),
                "xilo" to VarFundo("cenas/oasis/xilo/", 1),
            ),
        ),
        "esfinge" to CenaCfg(
            id = "esfinge", prefixo = "cenas/esfinge/", tipo = "esfinge",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 700f, 120f),
            lua = Astro(1.7f, 700f, 135f, fadeY = 680f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/esfinge/pixel/", 1),
                "giz" to VarFundo("cenas/esfinge/giz/", 1),
                "needle" to VarFundo("cenas/esfinge/needle/", 1),
                "ukiyoe" to VarFundo("cenas/esfinge/ukiyoe/", 1),
                "vangogh" to VarFundo("cenas/esfinge/vangogh/", 1),
                "xilo" to VarFundo("cenas/esfinge/xilo/", 1),
            ),
        ),
        "sitio" to CenaCfg(
            id = "sitio", prefixo = "cenas/sitio/", tipo = "sitio",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 620f, 110f),
            lua = Astro(1.7f, 620f, 125f, fadeY = 600f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/sitio/pixel/", 1),
                "clau" to VarFundo("cenas/sitio/clau/", 1),
                "impamer" to VarFundo("cenas/sitio/impamer/", 1),
                "impress" to VarFundo("cenas/sitio/impress/", 1),
                "pixel2" to VarFundo("cenas/sitio/pixel2/", 1),
                "simpsons" to VarFundo("cenas/sitio/simpsons/", 1),
                "vangogh" to VarFundo("cenas/sitio/vangogh/", 1),
                "xilo" to VarFundo("cenas/sitio/xilo/", 1),
            ),
        ),
        "versalhes" to CenaCfg(
            id = "versalhes", prefixo = "cenas/versalhes/", tipo = "versalhes",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 570f, 110f),
            lua = Astro(1.7f, 570f, 125f, fadeY = 550f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/versalhes/pixel/", 1),
                "pixel2" to VarFundo("cenas/versalhes/pixel2/", 1),
                "clay" to VarFundo("cenas/versalhes/clay/", 1),
                "giz" to VarFundo("cenas/versalhes/giz/", 1),
                "xilo" to VarFundo("cenas/versalhes/xilo/", 1),
            ),
        ),
        "babilonia" to CenaCfg(
            id = "babilonia", prefixo = "cenas/babilonia/", tipo = "babilonia",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 520f, 105f),
            lua = Astro(1.7f, 520f, 120f, fadeY = 500f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/babilonia/pixel/", 1),
                "pixel2" to VarFundo("cenas/babilonia/pixel2/", 1),
                "clay" to VarFundo("cenas/babilonia/clay/", 1),
                "papel" to VarFundo("cenas/babilonia/papel/", 1),
            ),
        ),
        "telhados" to CenaCfg(
            id = "telhados", prefixo = "cenas/telhados/", tipo = "telhados",
            ceuMovel = CeuMovel(vel = 8f),
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 640f, 120f),
            lua = Astro(1.7f, 640f, 135f, fadeY = 620f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            // 21 artes. A base virou a EMOLDURADA (a vista sai por um arco de
            // pedra), que é como vieram 20 das 21; a antiga sem moldura ficou
            // como `terraco`.
            variantes = mapOf(
                "pixel" to VarFundo("cenas/telhados/pixel/", 1),
                "terraco" to VarFundo("cenas/telhados/terraco/", 1),
                "anime" to VarFundo("cenas/telhados/anime/", 1),
                "cartoon" to VarFundo("cenas/telhados/cartoon/", 1),
                "clay" to VarFundo("cenas/telhados/clay/", 1),
                "cozy" to VarFundo("cenas/telhados/cozy/", 1),
                "cutout" to VarFundo("cenas/telhados/cutout/", 1),
                "dark" to VarFundo("cenas/telhados/dark/", 1),
                "doodle" to VarFundo("cenas/telhados/doodle/", 1),
                "doodle2" to VarFundo("cenas/telhados/doodle2/", 1),
                "giz" to VarFundo("cenas/telhados/giz/", 1),
                "impress" to VarFundo("cenas/telhados/impress/", 1),
                "lowpoly" to VarFundo("cenas/telhados/lowpoly/", 1),
                "needle" to VarFundo("cenas/telhados/needle/", 1),
                "papel" to VarFundo("cenas/telhados/papel/", 1),
                "point" to VarFundo("cenas/telhados/point/", 1),
                "puppet" to VarFundo("cenas/telhados/puppet/", 1),
                "ukiyoe" to VarFundo("cenas/telhados/ukiyoe/", 1),
                "vangogh" to VarFundo("cenas/telhados/vangogh/", 1),
                "vangoghnoite" to VarFundo("cenas/telhados/vangoghnoite/", 1),
                "xilo" to VarFundo("cenas/telhados/xilo/", 1),
            ),
        ),
        "forte" to CenaCfg(
            id = "forte", prefixo = "cenas/forte/", tipo = "forte",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 620f, 115f),
            lua = Astro(1.7f, 620f, 130f, fadeY = 600f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/forte/pixel/", 1),
                "clay" to VarFundo("cenas/forte/clay/", 1),
                "cutout" to VarFundo("cenas/forte/cutout/", 1),
                "puppet" to VarFundo("cenas/forte/puppet/", 1),
                "ukiyoe" to VarFundo("cenas/forte/ukiyoe/", 1),
                "xilo" to VarFundo("cenas/forte/xilo/", 1),
            ),
        ),
        "camboja" to CenaCfg(
            id = "camboja", prefixo = "cenas/camboja/", tipo = "camboja",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 530f, 105f),
            lua = Astro(1.7f, 530f, 120f, fadeY = 510f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/camboja/pixel/", 1),
            ),
        ),
        // LOTE "SÓ PIXEL" — a cena nasce só na arte pixel e as outras vêm
        // depois. Zonas derivadas da arte (cena_auto), sem marcação ainda.
        "caverna" to CenaCfg(
            id = "caverna", prefixo = "cenas/caverna/", tipo = "caverna",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 600f, 115f),
            lua = Astro(1.7f, 600f, 130f, fadeY = 580f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/caverna/pixel/", 1),
            ),
        ),
        "estacionamento" to CenaCfg(
            id = "estacionamento", prefixo = "cenas/estacionamento/", tipo = "estacionamento",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 660f, 120f),
            lua = Astro(1.7f, 660f, 135f, fadeY = 640f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/estacionamento/pixel/", 1),
            ),
        ),
        "mureta" to CenaCfg(
            id = "mureta", prefixo = "cenas/mureta/", tipo = "mureta",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 700f, 125f),
            lua = Astro(1.7f, 700f, 140f, fadeY = 680f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/mureta/pixel/", 1),
                "pixel2" to VarFundo("cenas/mureta/pixel2/", 1),
                "pixel16" to VarFundo("cenas/mureta/pixel16/", 1),
                "bordado" to VarFundo("cenas/mureta/bordado/", 1),
                "ceramica" to VarFundo("cenas/mureta/ceramica/", 1),
                "ceramica2" to VarFundo("cenas/mureta/ceramica2/", 1),
                "clay" to VarFundo("cenas/mureta/clay/", 1),
                "clay2" to VarFundo("cenas/mureta/clay2/", 1),
                "cozy" to VarFundo("cenas/mureta/cozy/", 1),
                "fauvismo" to VarFundo("cenas/mureta/fauvismo/", 1),
                "giz" to VarFundo("cenas/mureta/giz/", 1),
                "impalemao" to VarFundo("cenas/mureta/impalemao/", 1),
                "impress" to VarFundo("cenas/mureta/impress/", 1),
                "lowpoly" to VarFundo("cenas/mureta/lowpoly/", 1),
                "point" to VarFundo("cenas/mureta/point/", 1),
                "simpsons" to VarFundo("cenas/mureta/simpsons/", 1),
                "tapecaria" to VarFundo("cenas/mureta/tapecaria/", 1),
                "vangogh" to VarFundo("cenas/mureta/vangogh/", 1),
                // xilogravura: veio dele em CAMADAS separadas por profundidade
                // (céu/colinas/árvore/muro/primeiro plano). As camadas têm vãos
                // retangulares — não fecham a imagem —, então usei a composta e
                // recortei o céu por cor. As camadas seguem guardadas: se um dia
                // o motor ganhar parallax de verdade, é daqui que ele sai.
                "xilo" to VarFundo("cenas/mureta/xilo/", 1),
            ),
        ),
        "porto" to CenaCfg(
            id = "porto", prefixo = "cenas/porto/", tipo = "porto",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 690f, 120f),
            lua = Astro(1.7f, 690f, 135f, fadeY = 670f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/porto/pixel/", 1),
            ),
        ),
        "bruxa" to CenaCfg(
            id = "bruxa", prefixo = "cenas/bruxa/", tipo = "bruxa",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 700f, 120f),
            lua = Astro(1.7f, 700f, 135f, fadeY = 680f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/bruxa/pixel/", 1),
                "16bits" to VarFundo("cenas/bruxa/16bits/", 1),
                "16bits2" to VarFundo("cenas/bruxa/16bits2/", 1),
                "clay" to VarFundo("cenas/bruxa/clay/", 1),
                "needlefelting" to VarFundo("cenas/bruxa/needlefelting/", 1),
                "papelmache" to VarFundo("cenas/bruxa/papelmache/", 1),
            ),
        ),
        "eiffel" to CenaCfg(
            id = "eiffel", prefixo = "cenas/eiffel/", tipo = "eiffel",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 700f, 120f),
            lua = Astro(1.7f, 700f, 135f, fadeY = 680f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/eiffel/pixel/", 1),
                "16bits" to VarFundo("cenas/eiffel/16bits/", 1),
                "8bits" to VarFundo("cenas/eiffel/8bits/", 1),
                "8bits2" to VarFundo("cenas/eiffel/8bits2/", 1),
                "bordado1" to VarFundo("cenas/eiffel/bordado1/", 1),
                "bordado2" to VarFundo("cenas/eiffel/bordado2/", 1),
                "gizcera" to VarFundo("cenas/eiffel/gizcera/", 1),
                "impressionista" to VarFundo("cenas/eiffel/impressionista/", 1),
                "needlefelting" to VarFundo("cenas/eiffel/needlefelting/", 1),
                "papelmache" to VarFundo("cenas/eiffel/papelmache/", 1),
                "papercutout" to VarFundo("cenas/eiffel/papercutout/", 1),
                "ukiyoe" to VarFundo("cenas/eiffel/ukiyoe/", 1),
                "ukiyoe2" to VarFundo("cenas/eiffel/ukiyoe2/", 1),
                "vangogh" to VarFundo("cenas/eiffel/vangogh/", 1),
                "xilogravura" to VarFundo("cenas/eiffel/xilogravura/", 1),
            ),
        ),
        "simpsons" to CenaCfg(
            id = "simpsons", prefixo = "cenas/simpsons/", tipo = "simpsons",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 620f, 110f),
            lua = Astro(1.7f, 620f, 125f, fadeY = 600f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/simpsons/pixel/", 1),
                "16bits" to VarFundo("cenas/simpsons/16bits/", 1),
                "8bits" to VarFundo("cenas/simpsons/8bits/", 1),
                "bordado" to VarFundo("cenas/simpsons/bordado/", 1),
                "clay" to VarFundo("cenas/simpsons/clay/", 1),
                "gizcera" to VarFundo("cenas/simpsons/gizcera/", 1),
                "impressionista" to VarFundo("cenas/simpsons/impressionista/", 1),
                "mosaicobizantino" to VarFundo("cenas/simpsons/mosaicobizantino/", 1),
                "needlefelting" to VarFundo("cenas/simpsons/needlefelting/", 1),
                "papelmache" to VarFundo("cenas/simpsons/papelmache/", 1),
                "papercutout" to VarFundo("cenas/simpsons/papercutout/", 1),
                "simpsons" to VarFundo("cenas/simpsons/simpsons/", 1),
                "tapecaria" to VarFundo("cenas/simpsons/tapecaria/", 1),
                "ukiyoe" to VarFundo("cenas/simpsons/ukiyoe/", 1),
                "vangogh" to VarFundo("cenas/simpsons/vangogh/", 1),
                "xilogravura" to VarFundo("cenas/simpsons/xilogravura/", 1),
            ),
        ),
        "budokai" to CenaCfg(
            id = "budokai", prefixo = "cenas/budokai/", tipo = "budokai",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 640f, 115f),
            lua = Astro(1.7f, 640f, 130f, fadeY = 620f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/budokai/pixel/", 1),
                "anime" to VarFundo("cenas/budokai/anime/", 1),
                "impressionista" to VarFundo("cenas/budokai/impressionista/", 1),
                "ukiyoe" to VarFundo("cenas/budokai/ukiyoe/", 1),
                "xilogravura" to VarFundo("cenas/budokai/xilogravura/", 1),
            ),
        ),
        "konoha" to CenaCfg(
            id = "konoha", prefixo = "cenas/konoha/", tipo = "konoha",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 600f, 110f),
            lua = Astro(1.7f, 600f, 125f, fadeY = 580f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/konoha/pixel/", 1),
                "8bits" to VarFundo("cenas/konoha/8bits/", 1),
                "8bits2" to VarFundo("cenas/konoha/8bits2/", 1),
                "anime" to VarFundo("cenas/konoha/anime/", 1),
                "anime1" to VarFundo("cenas/konoha/anime1/", 1),
                "clay" to VarFundo("cenas/konoha/clay/", 1),
                "clay2" to VarFundo("cenas/konoha/clay2/", 1),
            ),
        ),
        "trincheira" to CenaCfg(
            id = "trincheira", prefixo = "cenas/trincheira/", tipo = "trincheira",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 680f, 120f),
            lua = Astro(1.7f, 680f, 135f, fadeY = 660f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/trincheira/pixel/", 1),
                "16bits" to VarFundo("cenas/trincheira/16bits/", 1),
                "8bits" to VarFundo("cenas/trincheira/8bits/", 1),
                "clay" to VarFundo("cenas/trincheira/clay/", 1),
            ),
        ),
        "cofre" to CenaCfg(
            id = "cofre", prefixo = "cenas/cofre/", tipo = "cofre",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 300f, 60f),
            lua = Astro(1.7f, 300f, 75f, fadeY = 120f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/cofre/pixel/", 1),
            ),
        ),
        "ruinas" to CenaCfg(
            id = "ruinas", prefixo = "cenas/ruinas/", tipo = "ruinas",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 640f, 115f),
            lua = Astro(1.7f, 640f, 130f, fadeY = 620f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/ruinas/pixel/", 1),
                "8bits" to VarFundo("cenas/ruinas/8bits/", 1),
                "clay" to VarFundo("cenas/ruinas/clay/", 1),
                "dragao" to VarFundo("cenas/ruinas/dragao/", 1),
                "dragao2" to VarFundo("cenas/ruinas/dragao2/", 1),
                "dragao3" to VarFundo("cenas/ruinas/dragao3/", 1),
                "papelmache" to VarFundo("cenas/ruinas/papelmache/", 1),
                "pixelv0" to VarFundo("cenas/ruinas/pixelv0/", 1),
            ),
        ),
        "baobas" to CenaCfg(
            id = "baobas", prefixo = "cenas/baobas/", tipo = "baobas",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 700f, 120f),
            lua = Astro(1.7f, 700f, 135f, fadeY = 680f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/baobas/pixel/", 1),
                "mache" to VarFundo("cenas/baobas/mache/", 1),
            ),
        ),
        "cafeparis" to CenaCfg(
            id = "cafeparis", prefixo = "cenas/cafeparis/", tipo = "cafeparis",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 520f, 100f),
            lua = Astro(1.7f, 520f, 115f, fadeY = 500f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/cafeparis/pixel/", 1),
                "16bits" to VarFundo("cenas/cafeparis/16bits/", 1),
                "clay" to VarFundo("cenas/cafeparis/clay/", 1),
                "mache" to VarFundo("cenas/cafeparis/mache/", 1),
            ),
        ),
        "estrada" to CenaCfg(
            id = "estrada", prefixo = "cenas/estrada/", tipo = "estrada",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 680f, 120f),
            lua = Astro(1.7f, 680f, 135f, fadeY = 660f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/estrada/pixel/", 1),
            ),
        ),
        "praiaeuropa" to CenaCfg(
            id = "praiaeuropa", prefixo = "cenas/praiaeuropa/", tipo = "praiaeuropa",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 640f, 115f),
            lua = Astro(1.7f, 640f, 130f, fadeY = 620f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/praiaeuropa/pixel/", 1),
                "16bits" to VarFundo("cenas/praiaeuropa/16bits/", 1),
                "clay" to VarFundo("cenas/praiaeuropa/clay/", 1),
                "mache" to VarFundo("cenas/praiaeuropa/mache/", 1),
            ),
        ),
        // CYBERPUNK — duas cenas irmãs, mesma cidade: canal cheio e rua seca.
        // Separadas porque só a inundada tem pingo na água.
        "cyberpunk" to CenaCfg(
            id = "cyberpunk", prefixo = "cenas/cyberpunk/", tipo = "cyberpunk",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 700f, 120f),
            lua = Astro(1.7f, 700f, 135f, fadeY = 680f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/cyberpunk/pixel/", 1),
                "pixel16" to VarFundo("cenas/cyberpunk/pixel16/", 1),
                "cozy" to VarFundo("cenas/cyberpunk/cozy/", 1),
                "impress" to VarFundo("cenas/cyberpunk/impress/", 1),
            ),
        ),
        "cyberseco" to CenaCfg(
            id = "cyberseco", prefixo = "cenas/cyberseco/", tipo = "cyberseco",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 650f, 110f),
            lua = Astro(1.7f, 650f, 125f, fadeY = 630f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/cyberseco/pixel/", 1),
                "pixel2" to VarFundo("cenas/cyberseco/pixel2/", 1),
                "cozy" to VarFundo("cenas/cyberseco/cozy/", 1),
                "cozynoite" to VarFundo("cenas/cyberseco/cozynoite/", 1),
                "xilo" to VarFundo("cenas/cyberseco/xilo/", 1),
            ),
        ),
        // DUOMO — lote de 01/09, entrou DERIVADA (zonas do cena_auto.py).
        "duomo" to CenaCfg(
            id = "duomo", prefixo = "cenas/duomo/", tipo = "duomo",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 547f, 109f),
            lua = Astro(1.7f, 547f, 124f, fadeY = 527f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/duomo/pixel/", 1),
            ),
        ),
        // COLISEU — lote de 01/09, entrou DERIVADA (zonas do cena_auto.py).
        "coliseu" to CenaCfg(
            id = "coliseu", prefixo = "cenas/coliseu/", tipo = "coliseu",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 511f, 102f),
            lua = Astro(1.7f, 511f, 117f, fadeY = 491f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/coliseu/pixel/", 1),
            ),
        ),
        // NOTRE DAME — lote de 01/09, entrou DERIVADA (zonas do cena_auto.py).
        "notredame" to CenaCfg(
            id = "notredame", prefixo = "cenas/notredame/", tipo = "notredame",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 798f, 160f),
            lua = Astro(1.7f, 798f, 175f, fadeY = 778f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/notredame/pixel/", 1),
            ),
        ),
        // CIDADE PROIBIDA — lote de 01/09, entrou DERIVADA (zonas do cena_auto.py).
        "cidadeproibida" to CenaCfg(
            id = "cidadeproibida", prefixo = "cenas/cidadeproibida/", tipo = "cidadeproibida",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 618f, 124f),
            lua = Astro(1.7f, 618f, 139f, fadeY = 598f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/cidadeproibida/pixel/", 1),
            ),
        ),
        // BURJ KHALIFA — lote de 01/09, entrou DERIVADA (zonas do cena_auto.py).
        "burj" to CenaCfg(
            id = "burj", prefixo = "cenas/burj/", tipo = "burj",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 1010f, 202f),
            lua = Astro(1.7f, 1010f, 217f, fadeY = 990f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/burj/pixel/", 1),
            ),
        ),
        // TIMES SQUARE — lote de 01/09, entrou DERIVADA (zonas do cena_auto.py).
        "timessquare" to CenaCfg(
            id = "timessquare", prefixo = "cenas/timessquare/", tipo = "timessquare",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 522f, 104f),
            lua = Astro(1.7f, 522f, 119f, fadeY = 502f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/timessquare/pixel/", 1),
            ),
        ),
        // PIZZA AUTÊNTICA — lote de 01/09, entrou DERIVADA (zonas do cena_auto.py).
        "pizzaria" to CenaCfg(
            id = "pizzaria", prefixo = "cenas/pizzaria/", tipo = "pizzaria",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 307f, 61f),
            lua = Astro(1.7f, 307f, 76f, fadeY = 287f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/pizzaria/pixel/", 1),
            ),
        ),
        // VILA VIKING — lote de 01/09, entrou DERIVADA (zonas do cena_auto.py).
        "vilaviking" to CenaCfg(
            id = "vilaviking", prefixo = "cenas/vilaviking/", tipo = "vilaviking",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 593f, 119f),
            lua = Astro(1.7f, 593f, 134f, fadeY = 573f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            aurora = true,                           // vila nórdica: mesma aurora do fiorde
            variantes = mapOf(
                "pixel" to VarFundo("cenas/vilaviking/pixel/", 1),
            ),
        ),
        // APÊ FINO — lote de 01/09, entrou DERIVADA (zonas do cena_auto.py).
        "apefino" to CenaCfg(
            id = "apefino", prefixo = "cenas/apefino/", tipo = "apefino",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 719f, 144f),
            lua = Astro(1.7f, 719f, 159f, fadeY = 699f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            vidro = true, escChuva = 0.5f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/apefino/pixel/", 2),
            ),
        ),
        "terrao" to CenaCfg(
            id = "terrao", prefixo = "cenas/terrao/", tipo = "terrao",
            cenaW = 1086f, cenaH = 1448f,
            astros = Astros(-12f, 1098f),
            sol = Astro(2.2f, 800f, 135f),
            lua = Astro(1.7f, 800f, 150f, fadeY = 780f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 6f, escImpacto = 0.78f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/terrao/pixel/", 1),
            ),
        ),
        // VELHO OESTE — da marcação (.psd): pingo sólido em tudo (sem água) e
        // distância em 6 níveis. Céu derivado da arte (ele não marcou).
        "velhooeste" to CenaCfg(
            id = "velhooeste", prefixo = "cenas/velhooeste/", tipo = "velhooeste",
            cenaW = 841f, cenaH = 1870f,
            astros = Astros(-12f, 853f),
            sol = Astro(2.2f, 560f, 110f),
            lua = Astro(1.7f, 560f, 125f, fadeY = 540f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 6f, taxaCompleto = 3f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/velhooeste/pixel/", 1),
            ),
        ),
        // DESCANSO DO HERÓI — da marcação (.psd): céu, pingo sólido, riacho,
        // 1 luz de noite toda e distância em 5 níveis.
        "heroi" to CenaCfg(
            id = "heroi", prefixo = "cenas/heroi/", tipo = "heroi",
            cenaW = 688f, cenaH = 1536f,
            astros = Astros(-10f, 698f),
            sol = Astro(2f, 520f, 90f),
            lua = Astro(1.5f, 520f, 100f, fadeY = 500f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 3f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/heroi/pixel/", 1),
            ),
        ),
        // ── Cenas montadas da DEMARCAÇÃO (.paint) / chroma — 688×1536 ──
        "pantano" to CenaCfg(
            id = "pantano", prefixo = "cenas/pantano/", tipo = "pantano",
            cenaW = 688f, cenaH = 1536f,
            astros = Astros(-10f, 698f),
            sol = Astro(2f, 496f, 80f),                 // horizonte ~496
            lua = Astro(1.5f, 496f, 65f, fadeY = 250f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 6f, taxaCompleto = 4f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/pantano/pixel/", 5),
            ),
        ),
        "farol" to CenaCfg(
            id = "farol", prefixo = "cenas/farol/", tipo = "farol",
            cenaW = 688f, cenaH = 1536f,
            astros = Astros(-10f, 698f),
            sol = Astro(2.2f, 618f, 90f),               // linha do mar ~618
            lua = Astro(1.7f, 618f, 110f, fadeY = 612f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 5f, taxaCompleto = 5f,
            feixe = Feixe(415f, 478f),   // lanterna (x 397..434, y 466..491)
            variantes = mapOf(
                "pixel" to VarFundo("cenas/farol/pixel/", 4),
            ),
        ),
        "praia" to CenaCfg(
            id = "praia", prefixo = "cenas/praia/", tipo = "praia",
            cenaW = 688f, cenaH = 1536f,
            astros = Astros(-10f, 698f),
            sol = Astro(2.2f, 560f, 80f),               // horizonte ~560
            lua = Astro(1.7f, 560f, 70f, fadeY = 540f),
            temAcumulo = false, luzesCabana = false, chamine = false, vagalumes = false,
            taxaParcial = 6f, taxaCompleto = 4f,
            variantes = mapOf(
                "pixel" to VarFundo("cenas/praia/pixel/", 1),
            ),
        ),
    )

    fun por(id: String): CenaCfg = map[id] ?: map.getValue("cabana")
}
