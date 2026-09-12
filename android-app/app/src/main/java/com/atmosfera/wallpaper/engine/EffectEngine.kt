package com.atmosfera.wallpaper.engine

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import java.io.File
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Motor de efeitos — porte do protótipo web (atmosfera 2.0/index.js) para
 * Android Canvas. Desenha o cenário em coords lógicas 688×1538 (modo "cover").
 *
 * Uso: [carregar] uma vez; a cada frame [draw](canvas, w, h, tsMs).
 */
class EffectEngine(val estado: SceneState = SceneState()) {

    // ── Bitmaps ─────────────────────────────────────────────────────
    private lateinit var fundo: Bitmap
    private var luzesOff: Bitmap? = null      // luzes da arte na versão apagada
    private var ceu: Bitmap? = null           // céu rolante (panorama), opcional
    private var ceuOff = 0f                   // o quanto a tira já rolou, em px de cena
    private var bandPixel: Bitmap? = null     // tira de frames do pano (arte pixel)
    private var bandClay: Bitmap? = null      // idem, arte clay
    private lateinit var frente: Bitmap
    private lateinit var sprites: Bitmap
    private lateinit var neve: Bitmap
    private lateinit var neveForte: Bitmap
    private lateinit var nevoa: Bitmap
    // MAPA DE LUZ (ver desenharMapaLuz): o tint de noite deixa de ser chapado
    private var lmBmp: Bitmap? = null
    private var lmCv: Canvas? = null
    // AURORA BOREAL (ver desenharAurora): a cortina é montada num bitmap à
    // parte e recortada pela silhueta da arte antes de somar na cena.
    private var auBmp: Bitmap? = null
    private var auCv: Canvas? = null
    private var auTira: Bitmap? = null       // o "pano" de uma coluna, em cache
    private var auroraT = 0f
    private val auFitas = ArrayList<FitaAurora>()
    private var vidro: Bitmap? = null         // máscara do VIDRO (cena de interior)
    private var vidroBmp: Bitmap? = null      // camada solta onde o escorrido é pintado
    private var vidroCv: Canvas? = null
    private var aoMask: Bitmap? = null        // onde a superfície VÊ O CÉU
    private var aoCob = 1f                    // quanto da tela a zona cobre
    var pronto = false; private set

    // ── Cena / estilo ativos (multi-cenário + multi-estilo) ─────────
    private var cenaCfg: CenaCfg = Cenas.por("cabana")
    private var estiloCfg: EstiloCfg = Estilos.por("pixel")
    private var RES = 1               // src-rect × RES (folha do pack em res×)
    private var cenaW = 688f
    private var cenaH = 1538f
    var cenaId = "cabana"; private set
    var arteId = "pixel"; private set
    var estiloId = "pixel"; private set

    // ── Marcação da cena (luzes/fumaça mapeadas à mão) ──────────────
    private var marca: DadosMarcacao = DadosMarcacao.VAZIO
    /** Tem luz? A cabana tem as constantes; as outras, o que foi marcado. */
    private fun temLuzes() = cenaCfg.luzesCabana || marca.luzes.isNotEmpty()
    private fun temFumaca() = cenaCfg.chamine || marca.fumaca.isNotEmpty()

    // ── Zonas de impacto (coords da imagem) ─────────────────────────
    private val roofPts = ArrayList<IntArray>()
    private val lakePts = ArrayList<IntArray>()

    // ── Partículas / estado de animação ─────────────────────────────
    private val drops = ArrayList<Drop>()
    private val impacts = ArrayList<Impact>()
    private val clouds = ArrayList<Cloud>()
    private var bolt: Bolt? = null
    private var boltTimer = 3f
    private val stars = ArrayList<Star>()
    private val fireflies = ArrayList<Firefly>()
    private var cadente: Cadente? = null
    private var cadenteTimer = 6f
    private val puffs = ArrayList<Puff>()
    private var puffAcc = 0f
    // praia: brilho d'água + fumaça permanente do vulcão
    private val brilhos = ArrayList<Brilho>()
    /** Risco claro descendo dentro de uma cachoeira (ver Marcacao.Queda). */
    private class RiscoAgua(var q: Int, var i: Float, var vel: Float,
                            var comp: Float, var a: Float)
    private val riscosAgua = ArrayList<RiscoAgua>()
    private val puffsVulcao = ArrayList<Puff>()
    private var puffVulcAcc = 0f
    private val leaves = ArrayList<Leaf>()
    private val wisps = ArrayList<Wisp>()
    private val flakes = ArrayList<Flake>()
    private val fogBanks = ArrayList<FogBank>()
    private var snowAccum = 0f
    private var roofAcc = 0f
    private var lakeAcc = 0f
    private var lastTs = 0L

    // ── Paints / buffers reutilizados ───────────────────────────────
    private val pSprite = Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = false }
    private val pSmooth = Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }
    private val pFill = Paint()
    private val pStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeCap = Paint.Cap.ROUND; strokeJoin = Paint.Join.ROUND
    }
    private val src = Rect()
    private val dst = RectF()
    private val path = Path()
    private val rnd = Random.Default
    // Serializa carregar/draw/liberar: sem isso, recarregar (thread de fundo)
    // recicla um bitmap que o draw (thread de UI) ainda está usando → crash.
    private val lock = Any()
    private val ADD = PorterDuffXfermode(PorterDuff.Mode.ADD)
    // halo do lampião (gradiente radial; xfermode ADD setado junto com pSprite)
    private val pGlow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)
    }
    private val MULT = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
    // recorte da aurora pela silhueta (apaga o que a arte cobre)
    private val DSTOUT = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    private val pAurora = Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }

    private class Tf(val s: Float, val ox: Float, val oy: Float)

    // ─────────────────────────────────────────────────────────────────
    //  Carregamento
    // ─────────────────────────────────────────────────────────────────
    /** Carrega os assets do cenário [cenaId] (arte de fundo [arte]) + o pack de
     *  sprites do estilo de efeito [estilo]. Pode ser chamado de novo p/ trocar. */
    /**
     * Mesmo carregamento, resolvendo o ACERVO baixado a partir do [Context] —
     * é por aqui que entra cena que não veio dentro do app. Cena sem download
     * cai nos assets embutidos (o wallpaper grátis), então nunca fica preto.
     */
    fun carregar(c: Context, cenaId: String = "cabana",
                 arte: String = "pixel", estilo: String = "pixel") =
        carregar(c.assets, cenaId, arte, estilo, Acervo.raiz(c))

    fun carregar(assets: AssetManager, cenaId: String = "cabana",
                 arte: String = "pixel", estilo: String = "pixel",
                 raizAcervo: File? = null) = synchronized(lock) {
        pronto = false
        liberarBitmaps()
        this.cenaId = cenaId; this.arteId = arte; this.estiloId = estilo
        cenaCfg = Cenas.por(cenaId)
        estiloCfg = Estilos.por(estilo)
        RES = estiloCfg.res
        cenaW = cenaCfg.cenaW; cenaH = cenaCfg.cenaH
        pSprite.isFilterBitmap = estiloCfg.suave   // pixel = cru; clay/aqua = suave
        fun bmp(nome: String) = assets.open("atmosfera/$nome").use { BitmapFactory.decodeStream(it) }
        val fp = cenaCfg.fundoPrefixo(arte)         // variante (clay/aqua) ou base
        // ACERVO: a arte baixada (WebP, em filesDir) tem precedência sobre a
        // embutida (PNG, nos assets). Só o wallpaper grátis vem dentro do app —
        // ver Acervo.kt e docs/dev/ENTREGA-DE-ARTE.md.
        val dArte = raizAcervo?.let { Acervo.pastaArte(it, cenaId, arte) }
        val dCena = raizAcervo?.let { Acervo.pastaCena(it, cenaId) }
        fun baixado(d: File?, base: String): Bitmap? {
            val f = File(d ?: return null, "$base.webp")
            return if (f.exists()) BitmapFactory.decodeFile(f.path) else null
        }
        fundo = baixado(dArte, "fundo") ?: bmp(fp + "fundo.png")
        frente = baixado(dArte, "frente") ?: bmp(fp + "frente.png")
        // opcional por ARTE (nem toda cena tem luz pintada) — ver tools/luzes_off.py
        luzesOff = baixado(dArte, "luzes_off")
            ?: try { bmp(fp + "luzes_off.png") } catch (e: Exception) { null }
        sprites = bmp(estiloCfg.arquivo)
        neve = bmp("neve_acumulo.png")
        neveForte = bmp("neve_acumulo_forte.png")
        nevoa = bmp("nevoa.png")
        bandPixel = try { bmp("bandeira_pixel.png") } catch (e: Exception) { null }
        bandClay = try { bmp("bandeira_clay.png") } catch (e: Exception) { null }
        roofPts.clear(); lakePts.clear()
        // CÉU ROLANTE (opcional, por CENA): tira larga que substitui o céu
        // pintado — ver tools/ceu_movel.py.
        ceu = if (cenaCfg.ceuMovel == null) null else
            baixado(dCena, "ceu") ?: try { bmp(cenaCfg.prefixo + "ceu.png") } catch (e: Exception) { null }
        ceuOff = 0f
        // ZONAS POR ARTE: arte com enquadramento próprio (a cabana doodle bate
        // só 0.58 com a base) não pode usar a zona da CENA — o pingo cairia no
        // lugar errado. Se a pasta da arte tem zonas, ela MANDA.
        val zonasArte = baixado(dArte, "zonas")
            ?: try { bmp(fp + "zonas.png") } catch (e: Exception) { null }
        extrairZonas(zonasArte ?: baixado(dCena, "zonas") ?: bmp(cenaCfg.prefixo + "zonas.png"))
        carregarProf(baixado(dCena, "profundidade")
            ?: try { bmp(cenaCfg.prefixo + "profundidade.png") } catch (e: Exception) { null })
        // luzes e saída de fumaça da MARCAÇÃO da cena (cena sem isso cai nas
        // constantes da cabana no Atlas — ver Marcacao.kt)
        val zonasBaixado = (dArte?.let { File(it, "zonas.json") }?.takeIf { it.exists() }
            ?: dCena?.let { File(it, "zonas.json") }?.takeIf { it.exists() })
        // mesma precedência do zonas.png: a marcação da ARTE ganha da cena
        val marcaArte = if (zonasArte != null) DadosMarcacao.ler(assets, fp) else DadosMarcacao.VAZIO
        marca = when {
            zonasBaixado != null -> DadosMarcacao.ler(zonasBaixado.readText())
            marcaArte !== DadosMarcacao.VAZIO -> marcaArte
            else -> DadosMarcacao.ler(assets, cenaCfg.prefixo)
        }
        // IMPACTO DENTRO DA CHUVA. Cena de INTERIOR (faixas `chuva`) só tem
        // pingo sob a claraboia/vidro, mas o RESPINGO nascia da zona amarela
        // inteira — no apê fino ela é derivada e cobre a sala toda: respingo no
        // tapete e no braço da poltrona, com a chuva lá fora. Onde o pingo não
        // chega, não há o que respingar. Apê fino 8.122 -> 1.396 pontos; no
        // cofre não muda nada (73 de 73 já estavam sob a claraboia).
        if (marca.chuva.isNotEmpty()) {
            val rp = roofPts.filter { chuvaAqui(it[0].toFloat(), it[1].toFloat()) }
            val lp = lakePts.filter { chuvaAqui(it[0].toFloat(), it[1].toFloat()) }
            roofPts.clear(); roofPts.addAll(rp)
            lakePts.clear(); lakePts.addAll(lp)
        }
        // estado dependente da cena/dimensões
        clouds.clear(); drops.clear(); flakes.clear(); impacts.clear()
        brilhos.clear(); puffsVulcao.clear(); puffVulcAcc = 0f
        riscosAgua.clear()
        fogBanks.clear(); leaves.clear(); wisps.clear(); puffs.clear()
        bolt = null; snowAccum = 0f; roofAcc = 0f; lakeAcc = 0f; lastTs = 0L
        initStars()
        if (cenaCfg.vagalumes) initFireflies() else fireflies.clear()
        carregarRemos(assets, fp)
        vidro = if (!cenaCfg.vidro) null else
            baixado(dCena, "vidro") ?: try { bmp(cenaCfg.prefixo + "vidro.png") } catch (e: Exception) { null }
        escorridos.clear(); grudadas.clear()
        if (vidro != null) initEscorridos()
        pronto = true
    }

    // ── REMOS (navio viking) ─────────────────────────────────────────
    // A arte vem SEM remo nenhum, só com as portinholas: remo pintado é remo
    // parado para sempre. Quem põe o remo é o motor — UM sprite girado uma vez
    // por portinhola, com um atraso entre um e o vizinho, e é o atraso que faz
    // a centopeia. Eixos e linha d'água saem de `tools/remos.py`. Porte do
    // index.js: os dois têm de desenhar igual.
    private var remoBmp: Bitmap? = null
    private var remoPivos: FloatArray = FloatArray(0)   // x0,y0,x1,y1,...
    private var remoEsp = 0f
    private var linhaDagua: FloatArray = FloatArray(0)

    private fun carregarRemos(assets: AssetManager, fp: String) {
        remoBmp?.recycle(); remoBmp = null
        remoPivos = FloatArray(0); linhaDagua = FloatArray(0); remoEsp = 0f
        cenaCfg.remos ?: return
        try {
            val o = org.json.JSONObject(
                assets.open("atmosfera/" + fp + "remos.json")
                    .bufferedReader().use { it.readText() })
            val pv = o.optJSONArray("pivos") ?: return
            val out = FloatArray(pv.length() * 2)
            for (i in 0 until pv.length()) {
                val p = pv.getJSONArray(i)
                out[i * 2] = p.getDouble(0).toFloat()
                out[i * 2 + 1] = p.getDouble(1).toFloat()
            }
            remoPivos = out
            remoEsp = o.optDouble("espacamento", 30.0).toFloat()
            o.optJSONArray("linhaDagua")?.let { ld ->
                val l = FloatArray(ld.length() * 2)
                for (i in 0 until ld.length()) {
                    val p = ld.getJSONArray(i)
                    l[i * 2] = p.getDouble(0).toFloat()
                    l[i * 2 + 1] = p.getDouble(1).toFloat()
                }
                linhaDagua = l
            }
            val spr = cenaCfg.remoSpriteDe(arteId)
            remoBmp = assets.open("atmosfera/" + fp + spr).use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            remoBmp = null; remoPivos = FloatArray(0); linhaDagua = FloatArray(0)
        }
    }

    /** Fase da remada de um remo: 0..1, com o atraso que faz a onda andar. */
    private fun remoFase(i: Int, ts: Long, r: CfgRemos): Float {
        val f = (ts / r.periodo + i * r.passo) % 1f
        return if (f < 0) f + 1f else f
    }

    /**
     * Ângulo do remo nessa fase. A remada NÃO é senoide: a puxada (pá na água)
     * é mais lenta que o recuo (pá no ar). 55% de puxada e 45% de recuo, com
     * easing em cada trecho — sem isso o movimento fica de metrônomo, e é a
     * diferença entre os dois tempos que dá vida.
     */
    private fun remoAng(f: Float, r: CfgRemos): Float {
        val puxa = 0.55f
        var u = if (f < puxa) f / puxa else 1f - (f - puxa) / (1f - puxa)
        u = u * u * (3f - 2f * u)
        val rep = Math.toRadians(r.ang.toDouble()).toFloat()
        val cur = Math.toRadians(r.curso.toDouble()).toFloat()
        return rep - cur + 2f * cur * u
    }

    private val mRemo = Matrix()

    private fun desenharRemos(c: Canvas, tf: Tf, ts: Long) {
        val r = cenaCfg.remos ?: return
        val bm = remoBmp ?: return
        if (remoPivos.isEmpty()) return
        val comp = remoEsp * r.comp
        val esc = comp / bm.width * tf.s
        val pivoX = bm.width * r.pivo
        for (i in 0 until remoPivos.size / 2) {
            val ang = remoAng(remoFase(i, ts, r), r)
            mRemo.reset()
            mRemo.postTranslate(-pivoX, -bm.height / 2f)
            mRemo.postScale(esc, esc)
            mRemo.postRotate(Math.toDegrees(ang.toDouble()).toFloat())
            mRemo.postTranslate(tf.ox + remoPivos[i * 2] * tf.s,
                                tf.oy + remoPivos[i * 2 + 1] * tf.s)
            c.drawBitmap(bm, mRemo, pSmooth)
        }
    }

    /** Respingo na PÁ: nasce na entrada e na saída da pá, quase nada no meio. */
    private fun desenharRespingoRemo(c: Canvas, tf: Tf, ts: Long) {
        val r = cenaCfg.remos ?: return
        if (remoPivos.isEmpty()) return
        val comp = remoEsp * r.comp
        val fora = comp * (1f - r.pivo)
        pGlow.xfermode = ADD
        for (i in 0 until remoPivos.size / 2) {
            val f = remoFase(i, ts, r)
            if (f >= 0.55f) continue                     // pá no ar
            var u = f / 0.55f
            u = u * u * (3f - 2f * u)
            val forca = max(0f, 1f - abs(u - 0.5f) * 2.4f)
            val a = (1f - forca) * 0.5f
            if (a < 0.04f) continue
            val ang = remoAng(f, r)
            val bx = tf.ox + (remoPivos[i * 2] + cos(ang) * fora) * tf.s
            val by = tf.oy + (remoPivos[i * 2 + 1] + sin(ang) * fora) * tf.s
            val rr = comp * 0.10f * tf.s * (0.7f + 0.6f * (1f - forca))
            pGlow.shader = RadialGradient(bx, by, max(1f, rr),
                Color.argb((a * 140).toInt(), 255, 255, 255), Color.TRANSPARENT,
                Shader.TileMode.CLAMP)
            c.drawOval(bx - rr, by - rr * 0.55f, bx + rr, by + rr * 0.55f, pGlow)
        }
        pGlow.shader = null; pGlow.xfermode = null
    }

    /**
     * Espuma na LINHA D'ÁGUA: a onda quebrando no costado. Três ondas de
     * períodos diferentes ao longo do casco — pulsa sem repetir. Achatada e
     * larga de propósito: redonda demais vira nuvem boiando.
     */
    private fun desenharEspumaCasco(c: Canvas, tf: Tf, ts: Long) {
        val r = cenaCfg.remos ?: return
        if (linhaDagua.isEmpty()) return
        val esc = remoEsp * 0.42f * tf.s
        val t = ts / 1000f
        pGlow.xfermode = ADD
        for (k in 0 until linhaDagua.size / 2) {
            val x = linhaDagua[k * 2]
            val v = sin(x * 0.06f - t * 1.7f) * 0.5f +
                    sin(x * 0.021f - t * 1.05f) * 0.32f +
                    sin(x * 0.13f - t * 2.6f) * 0.18f
            val a = max(0f, v) * r.espuma
            if (a < 0.03f) continue
            val px = tf.ox + x * tf.s
            val py = tf.oy + linhaDagua[k * 2 + 1] * tf.s
            val rx = esc * 2.4f
            val ry = esc * 0.42f
            pGlow.shader = RadialGradient(px, py, max(1f, rx),
                Color.argb((a * 255).toInt(), 255, 255, 255), Color.TRANSPARENT,
                Shader.TileMode.CLAMP)
            c.drawOval(px - rx, py - ry, px + rx, py + ry, pGlow)
        }
        pGlow.shader = null; pGlow.xfermode = null
    }

    private fun liberarBitmaps() {
        if (::fundo.isInitialized) fundo.recycle()
        luzesOff?.recycle(); luzesOff = null
        ceu?.recycle(); ceu = null
        bandPixel?.recycle(); bandPixel = null
        bandClay?.recycle(); bandClay = null
        if (::frente.isInitialized) frente.recycle()
        if (::sprites.isInitialized) sprites.recycle()
        if (::neve.isInitialized) neve.recycle()
        if (::neveForte.isInitialized) neveForte.recycle()
        if (::nevoa.isInitialized) nevoa.recycle()
        aoMask?.recycle(); aoMask = null
        lmBmp?.recycle(); lmBmp = null; lmCv = null
        auBmp?.recycle(); auBmp = null; auCv = null
        auTira?.recycle(); auTira = null
    }

    // ── PROFUNDIDADE (mapa cinza da cena): 0 = longe, 1 = perto, -1 = céu.
    // Serve p/ o respingo diminuir com a distância. Guardo numa grade pequena
    // (o mapa é suave; grade de ~96 px de largura basta e não pesa na memória).
    private var profW = 0
    private var profH = 0
    private var profG: ByteArray? = null

    private fun carregarProf(bm: Bitmap?) {
        profG = null; profW = 0; profH = 0
        val b = bm ?: return
        val w = 96
        val h = maxOf(1, w * b.height / b.width)
        val g = Bitmap.createScaledBitmap(b, w, h, true)
        val px = IntArray(w * h)
        g.getPixels(px, 0, w, 0, 0, w, h)
        val out = ByteArray(w * h)
        for (i in px.indices) {
            val a = (px[i] ushr 24) and 0xFF
            // alpha 0 = céu (sem profundidade); senão o tom de cinza é o nível
            out[i] = if (a < 128) -1 else (px[i] ushr 16 and 0xFF).toByte()
        }
        profG = out; profW = w; profH = h
        g.recycle(); b.recycle()
    }

    /** Profundidade em coords da CENA: 0=longe .. 1=perto; -1 = céu/sem mapa. */
    private fun profEm(ix: Float, iy: Float): Float {
        val g = profG ?: return -1f
        val x = (ix / cenaW * profW).toInt()
        val y = (iy / cenaH * profH).toInt()
        if (x < 0 || y < 0 || x >= profW || y >= profH) return -1f
        val v = g[y * profW + x].toInt()
        return if (v < 0) -1f else (v and 0xFF) / 255f
    }

    private fun extrairZonas(z: Bitmap) {
        val w = z.width; val h = z.height
        val px = IntArray(w * h)
        z.getPixels(px, 0, w, 0, 0, w, h)
        val step = 3
        var y = 0
        while (y < h) {
            var x = 0
            while (x < w) {
                val c = px[y * w + x]
                val a = (c ushr 24) and 0xFF
                if (a >= 128) {
                    val r = (c ushr 16) and 0xFF; val g = (c ushr 8) and 0xFF; val b = c and 0xFF
                    // amarelo = parcial (telhado/terreno); vermelho E laranja =
                    // completo (lago/poças) — o laranja é a poça de temporal.
                    if (r > 200 && g > 200 && b < 100) roofPts.add(intArrayOf(x, y))
                    else if (r > 200 && b < 100 && g < 200) lakePts.add(intArrayOf(x, y))
                }
                x += step
            }
            y += step
        }
        // MÁSCARA DE CÉU-VISÍVEL. Onde ele marcou pingo é onde a chuva bate, ou
        // seja, onde a superfície VÊ O CÉU — e de noite é o céu que ilumina a
        // rua vazia. Serve de oclusão de ambiente (ver desenharMapaLuz). Vale só
        // quando a zona veio da MÃO: zona derivada cobre 60-90% da tela e não
        // separa nada.
        aoCob = (roofPts.size + lakePts.size) * (step * step).toFloat() /
            (w * h).toFloat()
        aoMask?.recycle()
        aoMask = if (aoCob > 0.002f && aoCob < AO_LIMIAR)
            Bitmap.createScaledBitmap(z, maxOf(8, w / 14), maxOf(8, h / 14), true)
        else null
        z.recycle()
    }

    // ─────────────────────────────────────────────────────────────────
    //  Loop
    // ─────────────────────────────────────────────────────────────────
    fun draw(canvas: Canvas, cw: Float, ch: Float, tsMs: Long) = synchronized(lock) {
        if (!pronto) return@synchronized
        var dt = if (lastTs == 0L) 0f else (tsMs - lastTs) / 1000f
        lastTs = tsMs
        if (dt > 0.05f) dt = 0.05f
        val ts = tsMs

        val tf = cover(cw, ch)
        val escuro = nightFactor(estado.hora)

        // fundo
        canvas.drawColor(Color.BLACK)
        val tira = ceu
        if (tira != null) {
            // CÉU ROLANTE no lugar do pintado. A troca é segura porque a
            // `frente` é a arte inteira com só o céu transparente — ela repõe
            // tudo que o fundo desenhava fora do céu. O loop fecha porque a
            // tira é espelhada (tools/ceu_movel.py).
            ceuOff = (ceuOff + (cenaCfg.ceuMovel?.vel ?: 8f) * dt) % tira.width
            val w = tira.width * tf.s
            var x = tf.ox - ceuOff * tf.s - w
            val lim = tf.ox + fundo.width * tf.s
            while (x < lim) {
                dst.set(x, tf.oy, x + w, tf.oy + tira.height * tf.s)
                canvas.drawBitmap(tira, null, dst, pSmooth)
                x += w
            }
        } else {
            blitFull(canvas, fundo, tf, pSmooth)
        }

        // updates
        atualizar(dt, cw, ch, escuro)
        updateVulcao(dt)
        updateBrilhos(dt)
        updateCachoeiras(dt)
        updateGoteiras(dt)

        // 1a0. tint de clima (só o céu — antes do sol/nuvens/frente)
        val st = estado.skyTint
        if (st.a > 0f) {
            pFill.xfermode = null
            pFill.color = Color.argb((st.a * 255).toInt(), st.r, st.g, st.b)
            canvas.drawRect(0f, 0f, cw, ch, pFill)
        }

        desenharSol(canvas, tf)
        desenharNuvens(canvas, tf)

        // 1d. frente + neve acumulada + estalactites + névoa
        // feixe do farol ANTES da frente: o penhasco/casa occluem o facho
        desenharFeixe(canvas, tf, escuro, ts)
        // fumaça do vulcão ANTES da frente: a vegetação passa na frente dela,
        // que é o que dá a sensação de distância
        desenharVulcao(canvas, tf)
        blitFull(canvas, frente, tf, pSmooth)
        // Luzes APAGADAS enquanto é dia: janelas/lampiões vêm PINTADOS acesos na
        // arte. O overlay some ao anoitecer, quando o glow entra por cima.
        // TEM DE SER AQUI, depois da frente: a frente é a MESMA arte com só o
        // céu transparente (medido: opaca + céu = 100% em toda cena), então ela
        // cobre toda janela/lampião. Enquanto este overlay era desenhado logo
        // após o fundo (como nasceu), ficava 100% coberto — no tester, alternar
        // o overlay mudava ZERO pixel.
        luzesOff?.let { lo ->
            // normalizado: escuro não chega a 1 (a noite fecha em ~0.85), então
            // à noite o overlay some DE VEZ e a luz pintada aparece inteira.
            val dia = max(0f, 1f - escuro / 0.75f)
            if (dia > 0.01f) {
                setA(pSmooth, dia)
                blitFull(canvas, lo, tf, pSmooth)
                setA(pSmooth, 1f)
            }
        }
        // bandeira por cima da frente: é o objeto mais à frente naquele ponto
        desenharBandeira(canvas, tf, ts)
        // brilho d'água por cima da frente: ali o mar É a camada de cima
        desenharBrilhos(canvas, tf)
        // água correndo: por cima da frente, que ali É a rocha da queda
        desenharCachoeiras(canvas, tf)
        // remo: sai do costado e a pá cai na água À FRENTE do casco, então nada
        // da arte passa por cima dele. A espuma vem ANTES do remo, senão a faixa
        // clara apagaria a pá que está entrando ali.
        desenharEspumaCasco(canvas, tf, ts)
        desenharRemos(canvas, tf, ts)
        desenharRespingoRemo(canvas, tf, ts)
        // goteira por cima da frente: a folhagem da frente.png é toda opaca ali
        desenharGoteiras(canvas, tf)
        desenharAcumulo(canvas, tf)
        desenharEstalactites(canvas, tf)
        desenharNevoa(canvas, tf, cw, ch)

        desenharFumaca(canvas, tf)
        desenharVento(canvas, tf)

        // impactos (só chuva, não neve)
        if (!estado.nevando()) desenharImpactos(canvas, tf)

        // precipitação
        if (estado.nevando()) desenharFlocos(canvas, tf) else desenharPingos(canvas, tf)

        // água escorrendo no vidro (cena de interior com janelão)
        desenharVidro(canvas, tf, cw, ch)

        // raio + clarão
        var flash = 0f
        bolt?.let { b ->
            val fase = faseRaio(b.t, b.dupla)
            if (fase != null) {
                flash = fase.flash
                if (fase.frame >= 0) {
                    val sp = Atlas[b.frames[fase.frame]]
                    val sc = tf.s * 2.5f
                    blit(canvas, sp, tf.ox + b.ix * tf.s - sp.w * sc / 2f,
                        tf.oy + b.iy * tf.s, sp.w * sc, sp.h * sc, pSprite)
                }
            }
        }

        // Tint da hora (multiply) sobre tudo. De noite, numa cena COM luz
        // mapeada, o mesmo multiply passa a ser o MAPA DE LUZ — é o que acende
        // a parede em volta do lampião em vez de só somar brilho por cima.
        val tc = tintColor(estado.hora)
        if (escuro >= 0.12f && marca.luzes.isNotEmpty()) {
            desenharMapaLuz(canvas, tf, escuro, ts, cw, ch, tc)
        } else if (tc[0] != 255 || tc[1] != 255 || tc[2] != 255) {
            pFill.xfermode = MULT
            pFill.color = Color.rgb(tc[0], tc[1], tc[2])
            canvas.drawRect(0f, 0f, cw, ch, pFill)
            pFill.xfermode = null
        }

        // noite (aditivo, por cima da escuridão)
        if (st.a < 0.3f) {
            // aurora ANTES das estrelas: elas seguem visíveis através dela
            desenharAurora(canvas, tf, escuro, cw, ch)
            desenharEstrelas(canvas, tf, escuro, ts)
            desenharLua(canvas, tf, escuro)
            desenharCadente(canvas, tf)
        }
        desenharLuzes(canvas, tf, escuro, ts)
        desenharVagalumes(canvas, tf, escuro)

        // clarão do raio (fura a escuridão)
        if (flash > 0f) {
            pFill.xfermode = null
            pFill.color = Color.argb((flash * 255).toInt(), 245, 248, 255)
            canvas.drawRect(0f, 0f, cw, ch, pFill)
        }
    }

    private fun atualizar(dt: Float, cw: Float, ch: Float, escuro: Float) {
        for (nv in clouds) {
            nv.ix += nv.v * dt
            val w = Atlas[nv.sp].w * nv.escala
            if (nv.ix > cenaW + 20) nv.ix = -w - 20
        }
        if (clouds.isEmpty()) initClouds()
        updateFireflies(dt)
        updateCadente(dt, escuro)
        updateFumaca(dt)
        updateVento(dt)
        updateAurora(dt)
        updateNevoa(dt)
        updateEscorridos(dt)

        if (estado.clima == "chuva" && estado.nevando()) {
            bolt = null; impacts.clear()
            updateSnow(dt, cw, ch)
        } else if (estado.clima == "chuva") {
            updateImpactSpawners(dt)
            updateBolt(dt)
            if (drops.isEmpty()) initDrops(cw, ch)
            for (d in drops) {
                d.x += d.vx * dt; d.y += d.vy * dt
                if (d.y > ch + 30) makeDrop(d, cw, ch, false)
                if (d.x > cw + 30) d.x = -30f
            }
            val it = impacts.iterator()
            while (it.hasNext()) { val im = it.next(); im.t += dt * 1000f; if (im.t >= im.seq.size * estado.frameMs) it.remove() }
        } else { drops.clear() }

        // acúmulo de neve é recurso Premium (no free o floco cai mas não assenta)
        val podeAcumular = estado.nevando() && estado.premium
        snowAccum = if (podeAcumular) min(1f, snowAccum + dt / 4f) else max(0f, snowAccum - dt / 3f)
    }

    /** Chamar quando o clima real muda: refaz partículas com os novos parâmetros. */
    fun aoMudarClima() {
        clouds.clear(); drops.clear(); flakes.clear(); impacts.clear()
        brilhos.clear(); puffsVulcao.clear(); puffVulcAcc = 0f; bolt = null
        gotas.clear(); gotAcc.clear()
        roofAcc = 0f; lakeAcc = 0f; puffAcc = 0f
    }

    /** Libera os bitmaps (chamar no onDestroy do wallpaper). */
    fun liberar() = synchronized(lock) { pronto = false; liberarBitmaps() }

    // ── Transform "cover" ───────────────────────────────────────────
    private fun cover(cw: Float, ch: Float): Tf {
        val s = max(cw / cenaW, ch / cenaH)
        return Tf(s, (cw - cenaW * s) / 2f, (ch - cenaH * s) / 2f)
    }

    private fun blitFull(c: Canvas, b: Bitmap, tf: Tf, p: Paint) {
        dst.set(tf.ox, tf.oy, tf.ox + b.width * tf.s, tf.oy + b.height * tf.s)
        c.drawBitmap(b, null, dst, p)
    }

    private fun blit(c: Canvas, sp: Sprite, dx: Float, dy: Float, dw: Float, dh: Float, p: Paint) {
        src.set(sp.x * RES, sp.y * RES, (sp.x + sp.w) * RES, (sp.y + sp.h) * RES)
        dst.set(dx, dy, dx + dw, dy + dh)
        c.drawBitmap(sprites, src, dst, p)
    }

    private fun setA(p: Paint, a: Float) { p.alpha = (a.coerceIn(0f, 1f) * 255f).toInt() }

    // ─────────────────────────────────────────────────────────────────
    //  Sol / Lua / céu diurno
    /**
     * TELA MAIS LARGA QUE A ARTE (tablet, ou celular 16:9): o "cover" escala pela
     * LARGURA, a arte fica mais alta que a tela e o corte come o TOPO — que é
     * exatamente onde o astro passa no zênite. Medido: num tablet 4:3 a arte
     * perde 374px em cima e o sol da cabana fica fora da tela 11,8 das 12 horas
     * de dia (num celular 16:9, 7,3h). Aqui o arco é comprimido pra dentro da
     * faixa de céu que sobrou — entre o topo visível e o solo/silhueta, porque
     * não serve trazer o astro pra tela e deixá-lo atrás da montanha.
     *
     * Não conserta cena cuja faixa de céu foi cortada INTEIRA (cabana e tanque
     * num 4:3 ficam com 0% de céu): ali só arte mais larga resolve.
     */
    private fun arcoVisivel(tf: Tf, ch: Float, yBase: Float, yPico: Float,
                            raio: Float, ySolo: Float): Pair<Float, Float> {
        val topo = -tf.oy / tf.s
        val base = (ch - tf.oy) / tf.s
        if (yPico >= topo + raio) return yBase to yPico
        val teto = topo + raio
        val piso = min(base - raio, ySolo - raio)
        if (piso <= teto) return yBase to yPico
        val yb = min(max(yBase, teto), piso)
        val yp = min(max(yPico, teto), max(teto, yb - 20f))
        return yb to yp
    }

    // ─────────────────────────────────────────────────────────────────
    private fun desenharSol(c: Canvas, tf: Tf) {
        if (estado.clima != "seco") return
        val A = cenaCfg.astros; val S = cenaCfg.solDe(arteId)
        val t = (estado.hora - estado.nascer) / (estado.por - estado.nascer)
        if (t < -0.02f || t > 1.02f) return
        val ix = A.x1 + (A.x0 - A.x1) * t              // nasce à direita
        val (yb, yp) = arcoVisivel(tf, c.height.toFloat(), S.yBase, S.yPico,
                                   Atlas["sol_2"].h * S.escala / 2f, S.yBase)
        val iy = yb - (yb - yp) * 4f * t * (1 - t)
        val s = 1 - abs(2 * t - 1)                     // gradiente simétrico
        val base: String; val sobre: String; val k: Float
        if (s < 0.5f) { base = "sol_3"; sobre = "sol_2"; k = s * 2 }
        else { base = "sol_2"; sobre = "sol_1"; k = (s - 0.5f) * 2 }
        val sc = tf.s * S.escala
        pSprite.xfermode = null
        for ((nome, alpha) in listOf(base to 1f, sobre to k)) {
            if (alpha <= 0.01f) continue
            val sp = Atlas[nome]; setA(pSprite, alpha)
            blit(c, sp, tf.ox + ix * tf.s - sp.w * sc / 2f, tf.oy + iy * tf.s - sp.h * sc / 2f, sp.w * sc, sp.h * sc, pSprite)
        }
        setA(pSprite, 1f)
    }

    private fun luaProgresso(h: Float): Float? {
        val por = estado.por; val nascer = estado.nascer
        val noite = (24f - por) + nascer
        if (h >= por) return (h - por) / noite
        if (h <= nascer) return (h + 24 - por) / noite
        return null
    }

    private fun desenharLua(c: Canvas, tf: Tf, escuro: Float) {
        if (escuro < 0.25f) return
        val t = luaProgresso(estado.hora) ?: return
        val A = cenaCfg.astros; val L = cenaCfg.luaDe(arteId)
        val ix = A.x1 + (A.x0 - A.x1) * t              // nasce à direita, põe à esquerda
        val (yb, yp) = arcoVisivel(tf, c.height.toFloat(), L.yBase, L.yPico,
                                   Atlas["lua_4"].h * L.escala / 2f,
                                   if (L.fadeY > 0f) L.fadeY else L.yBase)
        val iy = yb - (yb - yp) * 4f * t * (1 - t)
        val fadeAlt = ((L.fadeY - iy) / 35f).coerceIn(0f, 1f)  // some atrás da silhueta
        if (fadeAlt <= 0.01f) return
        val idx = (estado.luaFase * (Atlas.luaFases.size - 1)).toInt().coerceIn(0, Atlas.luaFases.size - 1)
        val sp = Atlas[Atlas.luaFases[idx]]
        val sc = tf.s * L.escala
        val px = tf.ox + ix * tf.s; val py = tf.oy + iy * tf.s
        pSprite.xfermode = ADD; setA(pSprite, escuro * 0.22f * fadeAlt)
        blit(c, sp, px - sp.w * sc, py - sp.h * sc, sp.w * sc * 2, sp.h * sc * 2, pSprite)
        pSprite.xfermode = null; setA(pSprite, min(1f, escuro * 1.1f) * fadeAlt)
        blit(c, sp, px - sp.w * sc / 2f, py - sp.h * sc / 2f, sp.w * sc, sp.h * sc, pSprite)
        setA(pSprite, 1f)
    }

    // ─────────────────────────────────────────────────────────────────
    //  Nuvens
    // ─────────────────────────────────────────────────────────────────
    private fun initClouds() {
        val nomes = Atlas.cloudSet(estado.cloudSet)
        val fechado = estado.cloudSet != "leves"
        val n = estado.cloudN
        val amp = estado.cloudMax - estado.cloudMin
        clouds.clear()
        for (i in 0 until n) {
            clouds.add(Cloud(
                sp = nomes[i % nomes.size],
                ix = (i * 700f) / n + rnd.nextFloat() * 120 - 60,
                iy = 10f + rnd.nextFloat() * (if (fechado) 200f else 280f),
                v = 2f + rnd.nextFloat() * 4,
                flip = rnd.nextBoolean(),
                escala = estado.cloudMin + rnd.nextFloat() * amp,
            ))
        }
    }

    private fun desenharNuvens(c: Canvas, tf: Tf) {
        pSprite.xfermode = null; setA(pSprite, 1f)
        for (nv in clouds) {
            val sp = Atlas[nv.sp]
            val px = tf.ox + nv.ix * tf.s; val py = tf.oy + nv.iy * tf.s
            val dw = sp.w * tf.s * nv.escala; val dh = sp.h * tf.s * nv.escala
            if (nv.flip) {
                c.save(); c.translate(px + dw, py); c.scale(-1f, 1f)
                blit(c, sp, 0f, 0f, dw, dh, pSprite); c.restore()
            } else blit(c, sp, px, py, dw, dh, pSprite)
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Chuva (pingos + impactos + raio)
    // ─────────────────────────────────────────────────────────────────
    private fun makeDrop(d: Drop, cw: Float, ch: Float, randomY: Boolean) {
        val vy = estado.speed * (0.8f + rnd.nextFloat() * 0.4f)
        d.x = rnd.nextFloat() * (cw * 1.3f) - cw * 0.2f
        d.y = if (randomY) rnd.nextFloat() * ch else -30f - rnd.nextFloat() * ch * 0.3f
        d.vy = vy; d.vx = vy * Atlas.SLANT
    }
    private fun initDrops(cw: Float, ch: Float) {
        drops.clear(); for (i in 0 until estado.dropCount) { val d = Drop(); makeDrop(d, cw, ch, true); drops.add(d) }
    }
    /**
     * CHUVA DE INTERIOR: cena de dentro de um ambiente (o cofre é a primeira)
     * não pode chover na tela inteira — choveria dentro da câmara-forte. Quando
     * a cena traz as faixas da CLARABOIA (`chuva` no zonas.json, mesmo formato
     * do `mar`), o pingo só aparece ali. Sem isso, chove em tudo como sempre.
     */
    private fun chuvaAqui(ix: Float, iy: Float): Boolean {
        val f = marca.chuva
        if (f.isEmpty()) return true
        val y = iy.toInt()
        // VÁRIAS FAIXAS NA MESMA LINHA. A claraboia do cofre é um buraco só, e a
        // busca binária achava a faixa da linha e pronto. O apê fino tem TRÊS
        // vidros lado a lado: a mesma linha aparece 3 vezes e a binária devolvia
        // uma delas por acaso — o pingo sumia em 2 dos 3 vidros. Acha a linha e
        // depois varre as vizinhas com o mesmo y.
        var lo = 0; var hi = f.size - 1; var achou = -1
        while (lo <= hi) {
            val md = (lo + hi) ushr 1; val fa = f[md]
            when {
                fa.y < y -> lo = md + 1
                fa.y > y -> hi = md - 1
                else -> { achou = md; break }
            }
        }
        if (achou < 0) return false
        var i = achou
        while (i >= 0 && f[i].y.toInt() == y) {
            if (ix >= f[i].x0 && ix <= f[i].x1) return true
            i--
        }
        i = achou + 1
        while (i < f.size && f[i].y.toInt() == y) {
            if (ix >= f[i].x0 && ix <= f[i].x1) return true
            i++
        }
        return false
    }


    // ── ÁGUA ESCORRENDO NO VIDRO ─────────────────────────────────────
    // Porte do mesmo efeito do tester (index.js → drawVidro). Pedido dele pro
    // apê fino (01/09): a cena é vista de DENTRO, então a chuva que importa não
    // é o pingo lá fora — é a água correndo no vidro.
    //
    //  · o escorrido não é sprite: a cabeça anda e o RASTRO é o caminho que ela
    //    já fez, o que dá comprimento livre. Sprite pronto exigiria um por
    //    comprimento.
    //  · a gota não desce reta — trava, deslancha e escorrega de lado. É isso
    //    que separa "chuva na janela" de "risco branco caindo".
    //  · as GRUDADAS (as que não descem) são metade do efeito: vidro molhado é
    //    quase todo gota parada.
    //  · tudo vai numa camada solta e é recortado pela máscara `vidro.png` com
    //    DST_IN — o mesmo que o `destination-in` do canvas web.
    private class Escorrido(var x: Float, var y: Float, var v: Float, var r: Float,
                            var travado: Float, var desvio: Float, var a: Float) {
        val rastro = ArrayList<Float>(64)
    }
    private class Grudada(val x: Float, val y: Float, val r: Float, val a: Float, var fase: Float)
    private val escorridos = ArrayList<Escorrido>()
    private val grudadas = ArrayList<Grudada>()
    private val pVidro = Paint()
    private val pRecorte = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN) }

    private fun initEscorridos() {
        grudadas.clear()
        val w = cenaCfg.cenaW; val h = cenaCfg.cenaH
        repeat(170) {
            grudadas.add(Grudada(rnd.nextFloat() * w, rnd.nextFloat() * h, 1.6f + rnd.nextFloat() * 3.2f,
                0.20f + rnd.nextFloat() * 0.35f, rnd.nextFloat() * 6.28f))
        }
    }

    private fun novoEscorrido(meio: Boolean): Escorrido = Escorrido(
        x = rnd.nextFloat() * cenaCfg.cenaW,
        y = if (meio) rnd.nextFloat() * cenaCfg.cenaH else -10f - rnd.nextFloat() * 60f,
        v = 0f, r = 2.4f + rnd.nextFloat() * 4.4f,
        travado = rnd.nextFloat() * 0.5f, desvio = (rnd.nextFloat() - 0.5f) * 22f,
        a = 0.30f + rnd.nextFloat() * 0.45f)

    private fun alvoEscorridos(): Int {
        if (estado.clima != "chuva" || estado.nevando()) return 0
        return (6 + (estado.dropCount / 180f) * 42f).toInt()
    }

    private fun updateEscorridos(dt: Float) {
        if (vidro == null) return
        val alvo = alvoEscorridos()
        while (escorridos.size < alvo) escorridos.add(novoEscorrido(escorridos.size < 8))
        while (escorridos.size > alvo) escorridos.removeAt(escorridos.size - 1)
        for (e in escorridos) {
            if (e.travado > 0f) { e.travado -= dt; continue }
            e.v = min(430f, e.v + (150f + e.r * 55f) * dt)
            val ant = e.y
            e.y += e.v * dt
            e.x += sin(e.y / 90f) * e.desvio * dt
            e.rastro.add(ant)
            if (e.rastro.size > 40) e.rastro.removeAt(0)
            if (rnd.nextFloat() < dt * 0.55f) { e.travado = 0.10f + rnd.nextFloat() * 0.45f; e.v *= 0.35f }
            if (e.y > cenaCfg.cenaH + 20f) {
                val n = novoEscorrido(false)
                e.x = n.x; e.y = n.y; e.v = 0f; e.r = n.r
                e.travado = n.travado; e.desvio = n.desvio; e.a = n.a
                e.rastro.clear()
            }
        }
        for (g in grudadas) g.fase += dt * 1.6f
    }

    private fun desenharVidro(c: Canvas, tf: Tf, cw: Float, ch: Float) {
        val mask = vidro ?: return
        if (estado.clima != "chuva" || estado.nevando()) return
        val w = cw.toInt(); val h = ch.toInt()
        if (w <= 0 || h <= 0) return
        var bmp = vidroBmp
        if (bmp == null || bmp.width != w || bmp.height != h) {
            bmp?.recycle()
            bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            vidroBmp = bmp; vidroCv = Canvas(bmp)
        }
        val g = vidroCv ?: return
        bmp.eraseColor(Color.TRANSPARENT)
        val molhado = min(1f, 0.35f + estado.dropCount / 260f)

        // 1) gotas GRUDADAS: o vidro molhado inteiro, sem nenhuma correr
        for (gt in grudadas) {
            val r = gt.r * tf.s
            val brilho = 0.75f + 0.25f * sin(gt.fase)
            pVidro.color = Color.rgb(240, 249, 255)
            setA(pVidro, gt.a * molhado * brilho * 0.85f)
            val x = tf.ox + gt.x * tf.s; val y = tf.oy + gt.y * tf.s
            g.drawRect(x - r / 2f, y - r / 2f, x + r / 2f, y + r / 2f, pVidro)
        }
        // 2) o RASTRO — cada segmento FECHA até o seguinte, senão vira tracejado
        //    (a 430 px/s a gota anda 14 px entre quadros)
        pVidro.color = Color.rgb(226, 240, 252)
        for (e in escorridos) {
            val n = e.rastro.size
            for (i in 0 until n) {
                val k = (i + 1f) / n
                val larg = max(1f, e.r * (0.30f + 0.50f * k) * tf.s)
                val y0 = e.rastro[i]
                val y1 = if (i + 1 < n) e.rastro[i + 1] else e.y
                val alt = max(1f, (y1 - y0) * tf.s + 1f)
                setA(pVidro, e.a * k * 0.80f)
                val x = tf.ox + e.x * tf.s; val y = tf.oy + y0 * tf.s
                g.drawRect(x - larg / 2f, y, x + larg / 2f, y + alt, pVidro)
            }
        }
        // 3) a CABEÇA: fio escuro em cima e clarão embaixo (a luz atravessa a
        //    lente da gota) — é o que dá volume no pixel art
        for (e in escorridos) {
            val r = e.r * tf.s
            val x = tf.ox + e.x * tf.s; val y = tf.oy + e.y * tf.s
            pVidro.color = Color.rgb(30, 45, 60); setA(pVidro, e.a * 0.45f)
            g.drawRect(x - r / 2f, y - r, x + r / 2f, y, pVidro)
            pVidro.color = Color.WHITE; setA(pVidro, min(0.95f, e.a + 0.25f))
            g.drawRect(x - r / 2f, y - r * 0.35f, x + r / 2f, y + r * 0.55f, pVidro)
        }
        // 4) recorta pelo vidro e joga na cena
        dst.set(tf.ox, tf.oy, tf.ox + mask.width * tf.s, tf.oy + mask.height * tf.s)
        g.drawBitmap(mask, null, dst, pRecorte)
        c.drawBitmap(bmp, 0f, 0f, null)
    }

    private fun desenharPingos(c: Canvas, tf: Tf) {
        val sp = Atlas.get("pingo"); val sc = tf.s * estado.scaleMult
        val dw = sp.w * sc * cenaCfg.escChuva; val dh = sp.h * sc * cenaCfg.escChuva
        pSprite.xfermode = null; setA(pSprite, 1f)
        val interior = marca.chuva.isNotEmpty()
        for (d in drops) {
            if (interior && !chuvaAqui((d.x - tf.ox) / tf.s, (d.y - tf.oy) / tf.s)) continue
            blit(c, sp, d.x - dw / 2, d.y - dh / 2, dw, dh, pSprite)
        }
    }
    private fun updateImpactSpawners(dt: Float) {
        roofAcc += estado.roofRate * cenaCfg.taxaParcial * dt
        lakeAcc += estado.lakeRate * cenaCfg.taxaCompleto * dt
        while (roofAcc >= 1) { spawnImpact(roofPts, Atlas.seqTelhado); roofAcc -= 1 }
        while (lakeAcc >= 1) { spawnImpact(lakePts, Atlas.seqLago); lakeAcc -= 1 }
    }
    private fun spawnImpact(pts: List<IntArray>, seq: List<String>) {
        if (pts.isEmpty()) return
        val p = pts[rnd.nextInt(pts.size)]
        val x = p[0].toFloat(); val y = p[1].toFloat()
        val d = profEm(x, y)
        if (d in 0f..0.12f) return          // horizonte: não se vê respingo
        impacts.add(Impact(x, y, seq, 0f, escalaProf(d)))
    }

    /** Respingo longe é menor (0.35× no horizonte, 1× no primeiro plano). */
    private fun escalaProf(d: Float): Float = if (d < 0f) 1f else 0.35f + 0.65f * d
    private fun desenharImpactos(c: Canvas, tf: Tf) {
        val sc = tf.s * estado.scaleMult
        pSprite.xfermode = null; setA(pSprite, 1f)
        for (im in impacts) {
            val fi = min((im.t / estado.frameMs).toInt(), im.seq.size - 1)
            val sp = Atlas[im.seq[fi]]
            val px = tf.ox + im.ix * tf.s; val py = tf.oy + im.iy * tf.s
            val e = im.esc * cenaCfg.escImpacto
            val dw = sp.w * sc * e; val dh = sp.h * sc * e
            blit(c, sp, px - dw / 2, py - dh, dw, dh, pSprite)
        }
    }
    private class FaseRaio(val frame: Int, val flash: Float)
    private fun faseRaio(t: Float, dupla: Boolean): FaseRaio? = when {
        t < 110 -> FaseRaio(0, 0.40f)
        t < 250 -> FaseRaio(1, 0.12f)
        !dupla -> null
        t < 320 -> FaseRaio(-1, 0.05f)
        t < 430 -> FaseRaio(0, 0.28f)
        t < 560 -> FaseRaio(1, 0.08f)
        else -> null
    }
    private fun updateBolt(dt: Float) {
        if (!estado.raios) { bolt = null; return }
        val b = bolt
        if (b != null) {
            b.t += dt * 1000f
            if (faseRaio(b.t, b.dupla) == null) { bolt = null; boltTimer = 2.5f + rnd.nextFloat() * 5 }
            return
        }
        boltTimer -= dt
        if (boltTimer <= 0) bolt = Bolt(
            Atlas.raioModelos[rnd.nextInt(Atlas.raioModelos.size)],
            60f + rnd.nextFloat() * 540, 15f + rnd.nextFloat() * 140,
            rnd.nextFloat() < 0.35f, 0f)
    }

    // ─────────────────────────────────────────────────────────────────
    //  Neve (flocos zigzag + acúmulo)
    // ─────────────────────────────────────────────────────────────────
    private fun makeFlake(f: Flake, cw: Float, ch: Float, randomY: Boolean) {
        val cam = rnd.nextFloat()
        f.sp = if (cam < 0.5f) "floco_p" else if (cam < 0.82f) "floco_m" else "floco_g"
        f.baseX = rnd.nextFloat() * (cw + 60) - 30
        f.y = if (randomY) rnd.nextFloat() * ch else -20f - rnd.nextFloat() * 40
        f.vy = 30f + cam * 70 + rnd.nextFloat() * 20
        f.swayAmp = 8f + rnd.nextFloat() * 26; f.swayFreq = 0.5f + rnd.nextFloat() * 1.1f
        f.phase = rnd.nextFloat() * 6.283f; f.drift = 6f + rnd.nextFloat() * 10
        f.esc = 1f + cam * 1.6f; f.giro = rnd.nextFloat() * 6.283f; f.vgiro = (rnd.nextFloat() - 0.5f) * 1.5f
    }
    private fun updateSnow(dt: Float, cw: Float, ch: Float) {
        while (flakes.size < estado.dropCount) { val f = Flake(); makeFlake(f, cw, ch, flakes.isEmpty()); flakes.add(f) }
        while (flakes.size > estado.dropCount) flakes.removeAt(flakes.size - 1)
        for (f in flakes) {
            f.y += f.vy * dt; f.phase += f.swayFreq * dt; f.baseX += f.drift * dt; f.giro += f.vgiro * dt
            if (f.y > ch + 20) makeFlake(f, cw, ch, false)
            if (f.baseX > cw + 40) f.baseX = -40f
        }
    }
    private fun desenharFlocos(c: Canvas, tf: Tf) {
        pSprite.xfermode = null; setA(pSprite, 1f)
        for (f in flakes) {
            val sp = Atlas[f.sp]
            val x = f.baseX + sin(f.phase) * f.swayAmp
            val dw = sp.w * tf.s * f.esc; val dh = sp.h * tf.s * f.esc
            if (f.esc > 2f) {
                c.save(); c.translate(x, f.y); c.rotate(Math.toDegrees(f.giro.toDouble()).toFloat())
                blit(c, sp, -dw / 2, -dh / 2, dw, dh, pSprite); c.restore()
            } else blit(c, sp, x - dw / 2, f.y - dh / 2, dw, dh, pSprite)
        }
    }
    // ── Névoa: bancos translúcidos derivando devagar, densos perto do chão ──
    private fun initNevoa() {
        fogBanks.clear()
        val n = 7
        for (i in 0 until n) fogBanks.add(FogBank(
            (i * 700f) / n + rnd.nextFloat() * 140 - 70,
            620f + rnd.nextFloat() * 780,
            3f + rnd.nextFloat() * 5,
            2.4f + rnd.nextFloat() * 2.2f,
            rnd.nextFloat() * 6.283f,
            0.3f + rnd.nextFloat() * 0.4f,
            0.5f + rnd.nextFloat() * 0.5f))
    }
    private fun updateNevoa(dt: Float) {
        if (estado.nevoa <= 0.01f) return
        if (fogBanks.isEmpty()) initNevoa()
        for (f in fogBanks) {
            f.x += f.v * dt; f.fase += f.velFase * dt
            val w = nevoa.width * f.esc
            if (f.x - w / 2 > cenaW + 40) f.x = -w / 2 - 40
        }
    }
    private fun desenharNevoa(c: Canvas, tf: Tf, cw: Float, ch: Float) {
        if (estado.nevoa <= 0.01f) return
        pSmooth.xfermode = null
        for (f in fogBanks) {
            val dw = nevoa.width * tf.s * f.esc; val dh = nevoa.height * tf.s * f.esc
            val prof = min(1f, (f.y - 500f) / 900f)
            val pulso = 0.75f + 0.25f * sin(f.fase)
            setA(pSmooth, min(1f, estado.nevoa * f.aBase * (0.5f + 0.5f * prof) * pulso))
            c.drawBitmap(nevoa, null, RectF(tf.ox + f.x * tf.s - dw / 2, tf.oy + f.y * tf.s - dh / 2,
                tf.ox + f.x * tf.s + dw / 2, tf.oy + f.y * tf.s + dh / 2), pSmooth)
        }
        // véu suave geral
        pFill.xfermode = null
        pFill.color = Color.argb((estado.nevoa * 0.12f * 255).toInt(), 230, 234, 240)
        c.drawRect(0f, 0f, cw, ch, pFill)
        setA(pSmooth, 1f)
    }

    // Nível da neve (0..1) pela intensidade (fraca 0 · forte .5 · temporal 1).
    private fun nivelNeve(): Float = ((estado.dropCount - 60) / 120f).coerceIn(0f, 1f)

    private fun desenharAcumulo(c: Canvas, tf: Tf) {
        if (!cenaCfg.temAcumulo) return          // acúmulo é overlay próprio da cabana
        if (snowAccum <= 0.01f) return
        pSmooth.xfermode = null
        setA(pSmooth, snowAccum * 0.9f)          // acúmulo leve (sempre)
        blitFull(c, neve, tf, pSmooth)
        val nf = nivelNeve()                     // acúmulo pesado (nível 3 = manto)
        if (nf > 0.01f) { setA(pSmooth, snowAccum * nf); blitFull(c, neveForte, tf, pSmooth) }
        setA(pSmooth, 1f)
    }

    // Estalactites de gelo crescendo do beiral com a neve acumulada.
    // Só na neve 2+ (nivelNeve ≥ .4): médias na 2, longas na 3.
    private fun desenharEstalactites(c: Canvas, tf: Tf) {
        if (!cenaCfg.temAcumulo) return          // beiral da cabana
        if (snowAccum <= 0.02f) return
        val nv = nivelNeve()
        if (nv < 0.4f) return
        val sp = Atlas["estalactite"]
        val cresc = min(1f, snowAccum * 1.4f)                     // brotam com o acúmulo
        val compNivel = 0.55f + 0.45f * min(1f, (nv - 0.4f) / 0.6f) // média→longa
        pSprite.xfermode = null
        val pts = Atlas.estalactites
        for (i in pts.indices) {
            if (nv < 0.75f && i % 2 == 1) continue                // neve 2: metade dos pingentes
            val (ix, iy) = pts[i]
            val jitter = 0.82f + 0.36f * ((i * 47) % 100) / 100f  // variação de comprimento
            val dw = sp.w * tf.s
            val dh = sp.h * compNivel * cresc * jitter * tf.s
            setA(pSprite, min(1f, cresc * 1.1f))
            blit(c, sp, tf.ox + ix * tf.s - dw / 2, tf.oy + iy * tf.s, dw, dh, pSprite)
        }
        setA(pSprite, 1f)
    }

    // ─────────────────────────────────────────────────────────────────
    //  Céu noturno (estrelas, cadente, vagalumes, luzes)
    // ─────────────────────────────────────────────────────────────────
    private fun nightFactor(h: Float): Float {
        val t = tintColor(h); val lum = (t[0] + t[1] + t[2]) / 3f
        return ((215f - lum) / 155f).coerceIn(0f, 1f)
    }
    private fun initStars() {
        stars.clear(); val r = Random(20260704)
        val ceuAlt = cenaH * 0.13f               // estrelas no céu alto da cena
        for (i in 0 until 90) {
            val twinkle = r.nextFloat() < 0.4f
            stars.add(Star(r.nextFloat() * cenaW, r.nextFloat() * ceuAlt + 8,
                if (r.nextFloat() < 0.7f) "estrela_1" else if (r.nextFloat() < 0.8f) "estrela_2" else "estrela_3",
                twinkle, r.nextFloat() * 6.283f, 1.5f + r.nextFloat() * 2, 0.5f + r.nextFloat() * 0.5f))
        }
    }
    // ── AURORA BOREAL ───────────────────────────────────────────────
    // Porte do `drawAurora` do tester. As duas decisões do protótipo valem
    // igual aqui:
    //
    // 1. ONDE ENTRA NA PILHA. A aurora é LUZ. Desenhada antes da `frente` (o
    //    jeito fácil de o relevo occluí-la), o multiply da noite (rgb 55,63,101
    //    à meia-noite) cortaria o verde a ~25% e ela sairia verde-chumbo. Então
    //    vai DEPOIS do tint, junto das estrelas, e a oclusão vem por outro
    //    caminho: monto a cortina num bitmap à parte e APAGO dele a silhueta com
    //    DST_OUT + `frente` — a arte inteira com só o céu transparente.
    //
    // 2. RESOLUÇÃO. O bitmap auxiliar é 1/AU_ESC da tela, igual ao mapa de luz:
    //    o borrão do upscale É o degradê, e a silhueta apagada em baixa
    //    resolução deixa um sangramento curto de luz na crista da montanha —
    //    que é o que a aurora faz atrás do relevo.
    private class FitaAurora(
        val topo: Float, val alt: Float, val amp: Float,
        val xa: Float, val xb: Float,
        val k1: Float, val k2: Float, val k3: Float,
        val k4: Float, val k5: Float, val k6: Float,
        val v1: Float, val v2: Float, val v3: Float,
        val v4: Float, val v5: Float, val v6: Float,
        val fase: Float, val peso: Float, val deriva: Float,
    )

    /** Uma cortina = um feixe de colunas verticais que sobem e descem em onda.
     *  Os parâmetros são FRAÇÕES da banda de céu, não pixels: a mesma cortina
     *  serve o fiorde (841 de largura) e a vila viking (1086).
     *  `xa`/`xb` dão a cada cortina um TRECHO do céu — as três atravessando a
     *  tela inteira lado a lado somavam num véu verde parelho, sem começo. */
    private fun initAurora() {
        auFitas.clear()
        val r = Random(20260904)
        for (i in 0 until AU_FITAS) {
            auFitas.add(FitaAurora(
                topo = 0.05f + i * 0.20f + r.nextFloat() * 0.06f,
                alt = 0.30f + r.nextFloat() * 0.18f,
                amp = 0.07f + r.nextFloat() * 0.06f,
                xa = -0.20f + r.nextFloat() * 0.22f,
                xb = 0.96f + r.nextFloat() * 0.24f,
                k1 = 0.5f + r.nextFloat() * 0.5f,
                k2 = 1.4f + r.nextFloat() * 1.0f,
                k3 = 1.4f + r.nextFloat() * 1.4f,
                k4 = 5f + r.nextFloat() * 6f,
                k5 = 13f + r.nextFloat() * 10f,
                k6 = 8.7f + r.nextFloat() * 7f,
                v1 = 0.05f + r.nextFloat() * 0.05f,
                v2 = -0.03f - r.nextFloat() * 0.05f,
                v3 = 0.02f + r.nextFloat() * 0.03f,
                v4 = 0.05f + r.nextFloat() * 0.06f,
                v5 = -0.08f - r.nextFloat() * 0.07f,
                v6 = 0.04f + r.nextFloat() * 0.05f,
                fase = r.nextFloat() * 6.2832f,
                peso = 0.5f + r.nextFloat() * 0.5f,
                deriva = (if (r.nextFloat() < 0.5f) -1f else 1f) * (0.005f + r.nextFloat() * 0.008f),
            ))
        }
    }

    /** Tira vertical em cache: é o "pano" de UMA coluna. Violeta ralo em cima,
     *  turquesa no meio, verde forte embaixo e a borda inferior nítida — que é
     *  a leitura da aurora real (o corte de baixo é onde ela bate no ar). */
    private fun tiraAurora(): Bitmap {
        auTira?.let { if (!it.isRecycled) return it }
        val bm = Bitmap.createBitmap(4, 128, Bitmap.Config.ARGB_8888)
        val c = Canvas(bm)
        val p = Paint(Paint.ANTI_ALIAS_FLAG)
        p.shader = android.graphics.LinearGradient(0f, 0f, 0f, 128f,
            intArrayOf(
                Color.argb(0, 140, 80, 210),
                Color.argb(36, 150, 88, 215),
                Color.argb(82, 80, 205, 190),
                Color.argb(199, 80, 250, 150),
                Color.argb(255, 190, 255, 215),
                Color.argb(0, 190, 255, 215),
            ),
            floatArrayOf(0f, 0.12f, 0.45f, 0.80f, 0.95f, 1f),
            Shader.TileMode.CLAMP)
        c.drawRect(0f, 0f, 4f, 128f, p)
        auTira = bm
        return bm
    }

    private fun auroraLigada() = cenaCfg.aurora

    private fun updateAurora(dt: Float) {
        if (auroraLigada()) auroraT += dt
    }

    private fun desenharAurora(c: Canvas, tf: Tf, escuro: Float, cw: Float, ch: Float) {
        if (!auroraLigada() || !::frente.isInitialized || frente.isRecycled) return
        if (auFitas.isEmpty()) initAurora()
        // Sobe com a noite e apaga na névoa. O piso 0.45 é alto de propósito:
        // até as ~20h o tint ainda está roxo/laranja de crepúsculo, e verde por
        // cima daquilo não lê como aurora, lê como mancha.
        val forca = ((escuro - 0.45f) / 0.40f).coerceIn(0f, 1f) *
            (1f - 0.7f * estado.nevoa.coerceIn(0f, 1f))
        if (forca <= 0.01f) return

        // Banda de céu VISÍVEL: do topo da arte até o horizonte da cena
        // (o `yBase` que sol/lua já declaram). Recortada pela tela porque no
        // cover a arte sangra para fora em cima.
        val base = cenaCfg.luaDe(arteId).yBase
        val y0 = max(0f, tf.oy)
        val y1 = min(ch, tf.oy + base * tf.s)
        if (y1 - y0 < 8f) return

        val w = maxOf(1, (cw / AU_ESC).toInt())
        val h = maxOf(1, ((y1 - y0) / AU_ESC).toInt())
        var bm = auBmp
        if (bm == null || bm.isRecycled || bm.width != w || bm.height != h) {
            bm?.recycle()
            bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            auBmp = bm; auCv = Canvas(bm)
        }
        val ac = auCv ?: return
        ac.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val tira = tiraAurora()
        val tsrc = Rect(0, 0, tira.width, tira.height)
        val hb = base                       // altura da banda, em coords da cena
        val t = auroraT
        val tau = 6.2832f

        // ADD dentro do auxiliar: cortinas que se cruzam somam como luz, e o
        // alpha soma junto — é ele que o DST_OUT recorta depois.
        pAurora.xfermode = ADD
        for (f in auFitas) {
            val xa = f.xa * cenaW; val xb = f.xb * cenaW
            val passo = (xb - xa) / (AU_COLS - 1)
            val colW = max(1f, passo * tf.s / AU_ESC * 1.35f)   // sobreposição curta
            for (i in 0 until AU_COLS) {
                val u = i.toFloat() / (AU_COLS - 1)
                val p = u + t * f.deriva            // a cortina desliza de lado
                val onda = sin(f.k1 * p * tau + t * f.v1 * tau + f.fase) * 0.65f +
                    sin(f.k2 * p * tau + t * f.v2 * tau) * 0.35f
                val ay = (f.topo + f.amp * onda) * hb
                val ah = f.alt * hb *
                    (0.62f + 0.38f * sin(f.k3 * p * tau + t * f.v3 * tau + f.fase))
                if (ah <= 1f) continue
                // pontas ralas + dobras. TRÊS frequências sem relação inteira
                // entre si: com duas, os raios saem em listra regular e a
                // cortina vira cortina de banheiro.
                val env = Math.pow(sin(Math.PI.toFloat() * u).toDouble(), 0.6).toFloat()
                val r1 = 0.5f + 0.5f * sin(f.k4 * p * tau + t * f.v4 * tau + f.fase)
                val r2 = 0.5f + 0.5f * sin(f.k5 * p * tau + t * f.v5 * tau)
                val r3 = 0.5f + 0.5f * sin(f.k6 * p * tau + t * f.v6 * tau + 1.7f)
                val a = 0.21f * f.peso * env *
                    (0.15f + 0.85f * Math.pow(r1.toDouble(), 1.4).toFloat()) *
                    (0.55f + 0.45f * r2) * (0.70f + 0.30f * r3)
                if (a < 0.004f) continue
                val lx = (tf.ox + (xa + i * passo) * tf.s) / AU_ESC - colW / 2f
                val ly = (tf.oy + ay * tf.s - y0) / AU_ESC
                val lh = ah * tf.s / AU_ESC
                if (lx > w || lx + colW < 0 || ly > h || ly + lh < 0) continue
                pAurora.alpha = (a * 255f).toInt().coerceIn(0, 255)
                dst.set(lx, ly, lx + colW, ly + lh)
                ac.drawBitmap(tira, tsrc, dst, pAurora)
            }
        }
        pAurora.xfermode = null
        pAurora.alpha = 255

        // OCLUSÃO: apaga do auxiliar tudo que a arte cobre. `frente` é a cena
        // inteira com só o céu transparente — o que sobra é exatamente o céu.
        pSmooth.xfermode = DSTOUT
        src.set(0, 0, frente.width, frente.height)
        dst.set(tf.ox / AU_ESC, (tf.oy - y0) / AU_ESC,
            (tf.ox + cenaW * tf.s) / AU_ESC, (tf.oy + cenaH * tf.s) / AU_ESC)
        ac.drawBitmap(frente, src, dst, pSmooth)
        pSmooth.xfermode = null

        pSmooth.xfermode = ADD
        pSmooth.alpha = (forca * 255f).toInt().coerceIn(0, 255)
        src.set(0, 0, w, h)
        dst.set(0f, y0, cw, y1)
        c.drawBitmap(bm, src, dst, pSmooth)   // o borrão do upscale é o brilho
        pSmooth.xfermode = null
        pSmooth.alpha = 255
    }

    private fun desenharEstrelas(c: Canvas, tf: Tf, escuro: Float, ts: Long) {
        if (escuro < 0.15f) return
        pSprite.xfermode = null
        for (s in stars) {
            var a = s.base * escuro
            if (s.twinkle) a *= 0.45f + 0.55f * (0.5f + 0.5f * sin(ts / 1000f * s.vel + s.fase))
            if (a < 0.02f) continue
            val sp = Atlas[s.sp]; val sc = tf.s * 1.2f; setA(pSprite, min(1f, a))
            blit(c, sp, tf.ox + s.x * tf.s - sp.w * sc / 2, tf.oy + s.y * tf.s - sp.h * sc / 2, sp.w * sc, sp.h * sc, pSprite)
        }
        setA(pSprite, 1f)
    }
    private fun initFireflies() {
        fireflies.clear(); val r = Random(777)
        for (i in 0 until 9) fireflies.add(Firefly(r.nextFloat() * 620 + 34, r.nextFloat() * 340 + 1040,
            r.nextFloat() * 6.283f, 8f + r.nextFloat() * 10, r.nextFloat() * 6.283f, 2f + r.nextFloat() * 2.5f))
    }
    private fun updateFireflies(dt: Float) {
        for (f in fireflies) {
            f.ang += (rnd.nextFloat() - 0.5f) * 2.4f * dt
            f.x += cos(f.ang) * f.vel * dt; f.y += sin(f.ang) * f.vel * dt * 0.6f
            if (f.x < 20) f.x = 20f; if (f.x > 668) f.x = 668f
            if (f.y < 1010) f.y = 1010f; if (f.y > 1420) f.y = 1420f
            f.fase += f.velFase * dt
        }
    }
    private fun desenharVagalumes(c: Canvas, tf: Tf, escuro: Float) {
        if (!cenaCfg.vagalumes || escuro < 0.2f || !estado.premium) return
        val sp = Atlas.get("vagalume"); pSprite.xfermode = ADD
        for (f in fireflies) {
            val b = 0.35f + 0.65f * max(0f, sin(f.fase)); val sc = tf.s * 1.3f
            setA(pSprite, min(1f, escuro * b))
            blit(c, sp, tf.ox + f.x * tf.s - sp.w * sc / 2, tf.oy + f.y * tf.s - sp.h * sc / 2, sp.w * sc, sp.h * sc, pSprite)
        }
        pSprite.xfermode = null; setA(pSprite, 1f)
    }
    private fun updateCadente(dt: Float, escuro: Float) {
        if (escuro < 0.5f || !estado.premium) { cadente = null; cadenteTimer = 6f; return }
        val cd = cadente
        if (cd != null) {
            cd.t += dt; cd.x += cd.vx * dt; cd.y += cd.vy * dt
            if (cd.t > cd.dur) { cadente = null; cadenteTimer = 8f + rnd.nextFloat() * 18 }
            return
        }
        cadenteTimer -= dt
        if (cadenteTimer <= 0) cadente = Cadente(120f + rnd.nextFloat() * 400, 20f + rnd.nextFloat() * 120,
            260f + rnd.nextFloat() * 140, 90f + rnd.nextFloat() * 60, 0f, 0.8f + rnd.nextFloat() * 0.5f)
    }
    private fun desenharCadente(c: Canvas, tf: Tf) {
        val cd = cadente ?: return
        val sp = Atlas.get("cadente"); val prog = cd.t / cd.dur
        val a = sin(prog * Math.PI).toFloat()
        val ang = atan2(cd.vy, cd.vx); val sc = tf.s * 1.4f
        c.save(); pSprite.xfermode = ADD; setA(pSprite, a)
        c.translate(tf.ox + cd.x * tf.s, tf.oy + cd.y * tf.s)
        c.rotate(Math.toDegrees(ang.toDouble()).toFloat())
        blit(c, sp, -sp.w * sc, -sp.h * sc / 2, sp.w * sc, sp.h * sc, pSprite)
        c.restore(); pSprite.xfermode = null; setA(pSprite, 1f)
    }
    /**
     * Halo radial quente do lampião: miolo âmbar + derrame ao redor (ilumina a
     * parede/chão em volta). Porte do `glowQuente` do protótipo web. O alpha já
     * entra nas cores; desenhar com o canvas em modo ADD.
     */
    private fun glowQuente(c: Canvas, px: Float, py: Float, r: Float, a: Float) {
        val raio = r * 1.9f
        if (raio <= 0f || a <= 0.01f) return
        pGlow.shader = android.graphics.RadialGradient(
            px, py, raio,
            intArrayOf(
                Color.argb((0.95f * a * 255).toInt(), 255, 214, 150),
                Color.argb((0.55f * a * 255).toInt(), 255, 186, 104),
                Color.argb((0.16f * a * 255).toInt(), 255, 150, 60),
                Color.argb(0, 255, 140, 50),
            ),
            floatArrayOf(0f, 0.18f, 0.55f, 1f),
            android.graphics.Shader.TileMode.CLAMP,
        )
        c.drawCircle(px, py, raio, pGlow)
        pGlow.shader = null
    }

    private fun janelasAcesas(h: Float) = h >= estado.por - 0.3f
    private fun lampioesAcesos(h: Float) = h >= estado.por - 0.3f || h <= estado.nascer + 0.3f
    /** Lampejo de luz que desliza sobre o mar. */
    private class Brilho(var x: Float, val y: Float, val x0: Float, val x1: Float,
                         val w: Float, val vx: Float, var t: Float, val dur: Float,
                         val a: Float)

    /**
     * ÁGUA MEXENDO. A água é PINTADA (estática). Em vez de deformar pixel (caro
     * e borra o pixel art), passo lampejos claros por cima, DENTRO das faixas de
     * mar mapeadas — assim o brilho respeita a costa e o casco do barco.
     */
    /**
     * ÁGUA CORRENDO. O motor não deforma pixel (caro, e borra o pixel art):
     * a queda é feita de RISCOS CLAROS que descem dentro da faixa mapeada —
     * mesma ideia dos lampejos do mar da praia, virada na vertical. Como a
     * faixa vem linha a linha, o risco segue a curva da queda sozinho.
     */
    private fun updateCachoeiras(dt: Float) {
        val quedas = marca.cachoeiras
        if (quedas.isEmpty()) { if (riscosAgua.isNotEmpty()) riscosAgua.clear(); return }
        if (riscosAgua.isEmpty()) {
            quedas.forEachIndexed { q, queda ->
                val n = maxOf(3, Math.round(queda.faixas.size / 28f))
                repeat(n) {
                    riscosAgua.add(RiscoAgua(q, rnd.nextFloat() * queda.faixas.size,
                        190f + rnd.nextFloat() * 130f, 10f + rnd.nextFloat() * 16f,
                        0.30f + rnd.nextFloat() * 0.35f))
                }
            }
        }
        for (r in riscosAgua) {
            val f = quedas.getOrNull(r.q) ?: continue
            r.i += r.vel * dt
            if (r.i - r.comp > f.faixas.size) {
                r.i = -rnd.nextFloat() * 40f; r.vel = 190f + rnd.nextFloat() * 130f
                r.comp = 10f + rnd.nextFloat() * 16f; r.a = 0.30f + rnd.nextFloat() * 0.35f
            }
        }
    }

    private fun desenharCachoeiras(c: Canvas, tf: Tf) {
        if (riscosAgua.isEmpty()) return
        val quedas = marca.cachoeiras
        pFill.xfermode = ADD
        for (r in riscosAgua) {
            val f = quedas.getOrNull(r.q) ?: continue
            val ini = maxOf(0, (r.i - r.comp).toInt())
            val fim = minOf(f.faixas.size - 1, r.i.toInt())
            if (fim < ini) continue
            for (k in ini..fim) {
                val fa = f.faixas[k]
                val t = (k - ini).toFloat() / maxOf(1, fim - ini)
                val al = r.a * t * t * (0.75f + 0.25f * kotlin.math.sin(k * 0.7f))
                if (al < 0.02f) continue
                pFill.color = Color.argb((al * 255).toInt().coerceIn(0, 255), 226, 244, 255)
                val larg = maxOf(1f, (fa.x1 - fa.x0 + 1f) * 0.55f)
                val cx = fa.x0 + (fa.x1 - fa.x0 + 1f - larg) * 0.5f
                c.drawRect(tf.ox + cx * tf.s, tf.oy + fa.y * tf.s,
                           tf.ox + (cx + larg) * tf.s, tf.oy + (fa.y + 1f) * tf.s, pFill)
            }
        }
        pFill.xfermode = null
    }

    private fun updateBrilhos(dt: Float) {
        val mar = marca.mar
        if (mar.isEmpty()) { if (brilhos.isNotEmpty()) brilhos.clear(); return }
        val alvo = minOf(26, 8 + mar.size / 9)
        while (brilhos.size < alvo) {
            val f = mar[rnd.nextInt(mar.size)]
            val larg = f.x1 - f.x0
            if (larg < 24f) break
            val w = 10f + rnd.nextFloat() * minOf(70f, larg * 0.35f)
            val dur = 2.2f + rnd.nextFloat() * 2.6f
            brilhos.add(Brilho(
                f.x0 + rnd.nextFloat() * (larg - w), f.y, f.x0, f.x1, w,
                (if (rnd.nextBoolean()) -1f else 1f) * (3f + rnd.nextFloat() * 7f),
                rnd.nextFloat() * dur, dur,                 // entra espalhado no tempo
                0.10f + rnd.nextFloat() * 0.16f))
        }
        val it = brilhos.iterator()
        while (it.hasNext()) {
            val b = it.next()
            b.t += dt; b.x += b.vx * dt
            if (b.t > b.dur || b.x + b.w < b.x0 || b.x > b.x1) it.remove()
        }
    }

    private fun desenharBrilhos(c: Canvas, tf: Tf) {
        if (brilhos.isEmpty()) return
        pGlow.shader = null
        for (b in brilhos) {
            val k = sin(b.t / b.dur * Math.PI).toFloat()
            if (k <= 0.01f) continue
            val px = tf.ox + b.x * tf.s
            val py = tf.oy + b.y * tf.s
            val pw = b.w * tf.s
            val ph = maxOf(1f, 1.6f * tf.s)
            pGlow.shader = android.graphics.LinearGradient(
                px, 0f, px + pw, 0f,
                intArrayOf(Color.argb(0, 226, 248, 255),
                           Color.argb(((b.a * k) * 255).toInt(), 226, 248, 255),
                           Color.argb(0, 226, 248, 255)),
                floatArrayOf(0f, 0.5f, 1f), android.graphics.Shader.TileMode.CLAMP)
            c.drawRect(px, py, px + pw, py + ph, pGlow)
        }
        pGlow.shader = null
    }

    /**
     * FUMAÇA DO VULCÃO: sempre ligada (não depende de temperatura como a
     * chaminé), baforada pequena e lenta — o vulcão está longe no horizonte.
     */
    private fun updateVulcao(dt: Float) {
        val v = marca.vulcao
        if (v == null) { if (puffsVulcao.isNotEmpty()) puffsVulcao.clear(); return }
        puffVulcAcc += 1.5f * dt
        while (puffVulcAcc >= 1f) {
            puffVulcAcc -= 1f
            puffsVulcao.add(Puff(
                v.x + (rnd.nextFloat() - 0.5f) * v.w,
                v.y,
                2f + rnd.nextFloat() * 3f + max(0f, estado.vento) * 0.10f,
                -(7f + rnd.nextFloat() * 5f),
                0f, 5.5f + rnd.nextFloat() * 3f,
                Atlas.fumacaSprites[rnd.nextInt(3)],
                0.20f, 0.95f, 0.42f, (rnd.nextFloat() - 0.5f) * 0.5f))
        }
        val it = puffsVulcao.iterator()
        while (it.hasNext()) {
            val p = it.next()
            p.t += dt; p.x += p.vx * dt; p.y += p.vy * dt
            if (p.t > p.dur) it.remove()
        }
    }

    private fun desenharVulcao(c: Canvas, tf: Tf) {
        if (puffsVulcao.isEmpty()) return
        pSmooth.xfermode = null
        for (p in puffsVulcao) {
            val k = p.t / p.dur
            val esc = (p.esc0 + (p.esc1 - p.esc0) * k) * tf.s
            val a = sin(k * Math.PI).toFloat() * p.aMax
            if (a <= 0.01f) continue
            val sp = Atlas[p.sp]          // p.sp é a CHAVE; o retângulo vem do Atlas
            setA(pSmooth, a)
            c.save()
            c.translate(tf.ox + p.x * tf.s, tf.oy + p.y * tf.s)
            c.rotate(Math.toDegrees((p.giro * k).toDouble()).toFloat())
            src.set(sp.x * RES, sp.y * RES, (sp.x + sp.w) * RES, (sp.y + sp.h) * RES)
            dst.set(-sp.w * esc / 2, -sp.h * esc / 2, sp.w * esc / 2, sp.h * esc / 2)
            c.drawBitmap(sprites, src, dst, pSmooth)
            c.restore()
        }
        setA(pSmooth, 1f)
    }

    /**
     * GOTEIRA: pingo lento de um ponto fixo (na praia, o bico do tucano).
     * Só em chuva FORTE e TEMPORAL — é água que escorreu e se juntou, não faz
     * sentido garoando. Engorda na ponta, cai acelerando e estoura no chão que
     * a marcação/medição definiu (na praia, uma brómelia 70 px abaixo).
     */
    private class Gota(var x: Float, var y: Float, var vy: Float, val ychao: Float)
    private val gotas = ArrayList<Gota>()
    private val gotAcc = HashMap<Int, Float>()

    /** -1 sem chuva · 0 fraca · 1 forte · 2 temporal */
    private fun nivelChuva(): Int {
        if (estado.clima != "chuva" || estado.nevando()) return -1
        return if (estado.dropCount >= 180) 2 else if (estado.dropCount >= 120) 1 else 0
    }

    private fun updateGoteiras(dt: Float) {
        if (marca.goteiras.isEmpty()) { if (gotas.isNotEmpty()) gotas.clear(); return }
        val nv = nivelChuva()
        val taxa = if (nv == 2) 1.7f else if (nv == 1) 0.8f else 0f
        for ((i, g) in marca.goteiras.withIndex()) {
            if (taxa <= 0f) { gotAcc[i] = 0f; continue }
            var acc = (gotAcc[i] ?: rnd.nextFloat()) + taxa * dt
            while (acc >= 1f) { acc -= 1f; gotas.add(Gota(g.x, g.y, 0f, g.ychao)) }
            gotAcc[i] = acc
        }
        val it = gotas.iterator()
        while (it.hasNext()) {
            val d = it.next()
            d.vy += GOT_G * dt; d.y += d.vy * dt
            if (d.y >= d.ychao) {
                it.remove()
                impacts.add(Impact(d.x, d.ychao, Atlas.seqTelhado, 0f,
                                   escalaProf(profEm(d.x, d.ychao)) * 0.85f))
            }
        }
    }

    private fun desenharGoteiras(c: Canvas, tf: Tf) {
        if (marca.goteiras.isEmpty()) return
        val sp = Atlas.get("pingo")
        val esc = tf.s * estado.scaleMult
        pSprite.xfermode = null
        fun põe(x: Float, y: Float, k: Float, a: Float, ky: Float, topo: Boolean) {
            val dw = sp.w * esc * k; val dh = sp.h * esc * k * ky
            setA(pSprite, a)
            val px = tf.ox + x * tf.s - dw / 2f
            val py = tf.oy + y * tf.s - (if (topo) 0f else dh / 2f)
            blit(c, sp, px, py, dw, dh, pSprite)
        }
        // gota engordando: ACHATADA e pendurada pelo TOPO (o sprite do pingo é
        // um risco vertical; sem achatar parecia um pingo parado no ar)
        if (nivelChuva() >= 1) {
            for ((i, g) in marca.goteiras.withIndex()) {
                val acc = gotAcc[i] ?: 0f
                põe(g.x, g.y, 0.42f + 0.36f * acc, 0.5f + 0.5f * acc, 0.5f, true)
            }
        }
        for (d in gotas) põe(d.x, d.y, 0.85f, 1f, 1f, false)
        setA(pSprite, 1f)
    }

    // Frames da folha de bandeira (3 caída · 4 pouco vento · 4 muito vento).
    // A linha vem do VENTO; dentro dela os frames rodam no tempo.
    private val BAND_FRAMES = 11
    // largura do pano esticado em cada folha — é a régua da escala (sai no log
    // do tools/recorta_bandeira.py)
    private val BAND_LARG = mapOf("pixel" to 302f, "clay" to 315f)
    private val BAND_CAIDA = intArrayOf(0, 1, 2)
    private val BAND_POUCO = intArrayOf(3, 4, 5, 6)
    private val BAND_FORTE = intArrayOf(7, 8, 9, 10)

    /**
     * BANDEIRA no mastro. Parada ela cai ao longo do mastro; ventando estica e
     * ondula. O pano é SPRITE (renderizado, com dobra e sombra) — a primeira
     * versão desenhava por código e não combinava com a arte.
     */
    private fun desenharBandeira(c: Canvas, tf: Tf, ts: Long) {
        val B = cenaCfg.bandeiraDe(arteId) ?: return
        val tira = (if (B.folha == "clay") bandClay else bandPixel) ?: return
        val v = estado.vento
        val seq = if (v < 8f) BAND_CAIDA else if (v < 22f) BAND_POUCO else BAND_FORTE
        val fps = if (v < 8f) 2.2f else if (v < 22f) 6f else 9f
        val i = seq[((ts / 1000f * fps).toInt()).mod(seq.size)]
        val fw = tira.width / BAND_FRAMES
        val fh = tira.height
        val esc = B.comp / (BAND_LARG[B.folha] ?: 302f) * tf.s
        src.set(i * fw, 0, (i + 1) * fw, fh)
        dst.set(tf.ox + B.x * tf.s, tf.oy + B.y * tf.s,
                tf.ox + B.x * tf.s + fw * esc, tf.oy + B.y * tf.s + fh * esc)
        c.drawBitmap(tira, src, dst, pSmooth)
    }

    /**
     * FEIXE DO FAROL. A lanterna gira de verdade; projeto essa rotação (que é de
     * TOPO) na cena 2D: apontando pro lado vira uma cunha comprida no céu;
     * apontando PRA CÁ a cunha some e a lanterna dá um flash; pra trás, quase
     * nada. Desenhado ANTES da frente → o penhasco occlui o facho.
     */
    private fun desenharFeixe(c: Canvas, tf: Tf, escuro: Float, ts: Long) {
        val f = cenaCfg.feixe ?: return
        if (escuro < 0.15f || !estado.premium) return
        val th = (ts / 1000.0) * (2 * Math.PI) / f.periodo
        val dx = sin(th).toFloat(); val dz = cos(th).toFloat()
        val perfil = abs(dx); val paraCa = max(0f, dz)
        val ox = tf.ox + f.x * tf.s; val oy = tf.oy + f.y * tf.s
        if (perfil > 0.05f) {
            val dir = if (dx >= 0) 1 else -1
            val L = f.alcance * cenaW * (0.3f + 0.7f * perfil) * tf.s
            val meia = 0.055f
            val base = if (dir > 0) 0.06f else (Math.PI.toFloat() - 0.06f)
            pGlow.shader = android.graphics.RadialGradient(
                ox, oy, L,
                intArrayOf(
                    Color.argb((0.30f * perfil * 255).toInt(), 255, 248, 226),
                    Color.argb((0.13f * perfil * 255).toInt(), 255, 244, 210),
                    Color.argb(0, 255, 240, 200),
                ),
                floatArrayOf(0f, 0.35f, 1f),
                android.graphics.Shader.TileMode.CLAMP,
            )
            path.reset()
            path.moveTo(ox, oy)
            path.lineTo(ox + cos(base - meia) * L, oy + sin(base - meia) * L)
            path.lineTo(ox + cos(base + meia) * L, oy + sin(base + meia) * L)
            path.close()
            c.drawPath(path, pGlow)
            pGlow.shader = null
        }
        val fl = paraCa * paraCa
        glowQuente(c, ox, oy, 22f * tf.s * (0.6f + 1.5f * fl),
            min(1f, escuro * (0.4f + 0.6f * fl)))
    }

    /** Luzes MAPEADAS na cena: halo quente com o horário do seu tipo. */
    /**
     * LOTE DA NOITE: nem toda janela acende. Uma vila com 88 janelas todas
     * acesas parece prédio comercial; o que dá vida é cada noite ter um lote
     * diferente. Sorteio determinístico pelo DIA — estável a noite inteira, muda
     * à meia-noite. Só vale pra janela ('parcial'); lampião e farol não sorteiam.
     */
    private fun luzDoLote(l: LuzCena, i: Int): Boolean {
        if (l.modo == "fogueira") return i == fogoNoite
        if (l.tipo == "completa") return true
        val dia = (System.currentTimeMillis() / 86_400_000L).toInt()
        var h = (dia + 1) * -1640531527 xor ((i + 1) * 40503)
        h = (h xor (h ushr 15)) * -2048144789
        return ((h ushr 8) % 1000) / 1000f < LUZ_PROB
    }

    // ─────────────────────────────────────────────────────────────────
    //  ACENDER, e não pôr brilho por cima (2026-08-27)
    // ─────────────────────────────────────────────────────────────────
    // Porte do index.js — lá está o registro completo do que foi medido na arte
    // de NOITE de referência (`contato/fundos/beco/artes/pixel noite.png`). Em
    // três linhas: o AZUL CAI perto da luz (o modo aditivo só sabe somar, então
    // nunca chegava lá — a luz tem de entrar no MULTIPLY, como mapa); o alcance
    // é ~88 px numa arte de 1086 de largura, contra os 627 px do halo antigo; e
    // JANELA não é LAMPIÃO — o vidro de janela quase não derrama, porque a luz
    // está atrás do papel.
    /**
     * FOGUEIRA (cidade tomada): não é janela de prédio habitado, é sobrevivente
     * acampado. Pedido dele: "não devem ser ao mesmo tempo — uma noite uma,
     * outra noite uma terceira sozinha, como se fossem sobreviventes migrando
     * entre apartamentos". Das 15 marcadas, UMA acende por noite, sorteada pelo
     * dia (estável a noite inteira, troca à meia-noite) e nunca a mesma duas
     * noites seguidas. Porte do index.js — os dois têm de sortear igual.
     */
    private var fogoNoite = -1
    private var fogoDia = Int.MIN_VALUE

    private fun atualizarFogueira(luzes: List<LuzCena>) {
        val dia = (System.currentTimeMillis() / 86_400_000L).toInt()
        if (dia == fogoDia) return
        fogoDia = dia
        val idx = luzes.indices.filter { luzes[it].modo == "fogueira" }
        if (idx.isEmpty()) { fogoNoite = -1; return }
        fun escolhe(d: Int): Int {
            var h = (d + 1) * -1640531527
            h = (h xor (h ushr 15)) * -2048144789
            return idx[((h ushr 8) % idx.size)]
        }
        var a = escolhe(dia)
        if (idx.size > 1 && a == escolhe(dia - 1)) a = idx[(idx.indexOf(a) + 1) % idx.size]
        fogoNoite = a
    }

    private fun luzAcesa(l: LuzCena, i: Int): Boolean {
        // a fogueira do acampamento queima até o amanhecer, mesmo marcada de
        // amarelo: é o único fogo da cidade, apagar à meia-noite deixa a cena cega.
        val h = if (l.tipo == "completa" || l.modo == "fogueira")
                    lampioesAcesos(estado.hora)
                else janelasAcesas(estado.hora)
        return h && luzDoLote(l, i)
    }

    /** O lampião tremula (chama); a janela só pulsa de leve. Modo manda mais
     *  que tipo — ver [LuzCena.modo]. Porte do index.js. */
    private fun luzOsc(l: LuzCena, ts: Long): Float {
        val t = ts / 1000f
        // FOGUEIRA: chama, não lâmpada. Três senoides incomensuráveis somadas
        // nunca repetem o desenho — é o que separa fogo de pulso eletrônico.
        if (l.modo == "fogueira") {
            val f = sin(t * 7.3f + l.vx) * 0.5f +
                    sin(t * 11.7f + l.vy * 0.7f) * 0.3f +
                    sin(t * 2.9f + l.vx * 0.3f) * 0.2f
            return (0.78f + 0.32f * f).coerceIn(0.42f, 1f)
        }
        // AGONIZANDO: lâmpada elétrica no fim. Acesa, mas a cada ~2,4 s sorteia
        // um apagão CURTO (0,18 s) — mais longo que isso vira pisca-pisca. A
        // semente leva a posição do vidro, então dois postes não piscam juntos.
        if (l.modo == "agonizando") {
            val jan = (t / 2.4f).toInt()
            var h = (jan + 1) * -1640531527 xor ((l.vx.toInt() + 7) * 40503)
            h = (h xor (h ushr 15)) * -2048144789
            val r = ((h ushr 8) % 1000) / 1000f
            val fase = t / 2.4f - jan
            val base = 0.80f + 0.20f * sin(t * 17 + l.vx) * sin(t * 5.1f + l.vy)
            return if (r < 0.45f && fase > 0.5f && fase < 0.575f) base * 0.12f else base
        }
        return if (l.tipo == "completa")
            0.72f + 0.28f * sin(t * 7 + l.vx) * sin(t * 3.3f + l.vy)
        else 0.85f + 0.15f * sin(t * 1.3f + l.vx)
    }

    /** LUZ LONGE BRILHA MENOS E ALCANÇA MENOS — o mesmo mapa de profundidade
     *  que pesa no respingo. Sem isso, cena com muita luz pequena no ponto de
     *  fuga (o beco tem 10 placas ali) empilha halo e o fundo estoura. */
    private fun luzPeso(l: LuzCena): Float {
        val pf = profEm(l.cx, l.cy)
        return if (pf < 0f) 1f else 0.40f + 0.60f * pf
    }

    /** Alcance do derrame em px de CENA (medido numa arte de 1086 de largura). */
    private fun luzAlcance(l: LuzCena, peso: Float) =
        (when {
            l.modo == "fogueira" -> LUZ_ALCANCE_FOGO
            l.tipo == "completa" -> LUZ_ALCANCE
            else -> LUZ_ALCANCE_JANELA
        }) * (cenaW / 1086f) * peso

    private fun desenharMapaLuz(c: Canvas, tf: Tf, escuro: Float, ts: Long,
                                cw: Float, ch: Float, tc: IntArray) {
        val w = maxOf(1, (cw / LM_ESC).toInt())
        val h = maxOf(1, (ch / LM_ESC).toInt())
        var bm = lmBmp
        if (bm == null || bm.width != w || bm.height != h) {
            bm?.recycle()
            bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            lmBmp = bm; lmCv = Canvas(bm)
        }
        val lc = lmCv ?: return
        // AMBIENTE. Com marcação: `abrigo` do tint em tudo, e o resto só onde a
        // superfície vê o céu (a marca de pingo). Sem marcação: tint chapado.
        val mask = aoMask
        val abrigo = if (mask != null) 1f - (1f - AO_ABRIGO) * min(1f, escuro / 0.8f) else 1f
        lc.drawColor(Color.rgb((tc[0] * abrigo).toInt(), (tc[1] * abrigo).toInt(),
            (tc[2] * abrigo).toInt()), android.graphics.PorterDuff.Mode.SRC)
        if (mask != null && abrigo < 0.999f) {
            val k = 1f - abrigo
            pSmooth.xfermode = ADD
            pSmooth.colorFilter = android.graphics.PorterDuffColorFilter(
                Color.rgb((tc[0] * k).toInt(), (tc[1] * k).toInt(), (tc[2] * k).toInt()),
                android.graphics.PorterDuff.Mode.SRC_IN)
            lc.drawBitmap(mask,
                android.graphics.Rect(0, 0, mask.width, mask.height),
                android.graphics.RectF(tf.ox / LM_ESC, tf.oy / LM_ESC,
                    (tf.ox + cenaW * tf.s) / LM_ESC, (tf.oy + cenaH * tf.s) / LM_ESC),
                pSmooth)
            pSmooth.xfermode = null; pSmooth.colorFilter = null
        }
        // o derrame de cada lampião, somado ao ambiente
        pGlow.xfermode = ADD
        atualizarFogueira(marca.luzes)
        for ((i, l) in marca.luzes.withIndex()) {
            if (!luzAcesa(l, i)) continue
            val peso = luzPeso(l)
            val gh = max(l.vw, l.vh) / 2f
            val alc = luzAlcance(l, peso)
            val r = (gh + alc) * tf.s / LM_ESC
            if (r < 0.7f) continue
            val px = (tf.ox + l.cx * tf.s) / LM_ESC
            val py = (tf.oy + l.cy * tf.s) / LM_ESC
            val a = min(1f, escuro * luzOsc(l, ts)) * (0.45f + 0.55f * peso)
            val cores = IntArray(LUZ_QUEDA.size)
            val paradas = FloatArray(LUZ_QUEDA.size)
            for (j in LUZ_QUEDA.indices) {
                val f = LUZ_QUEDA[j][0]; val kq = LUZ_QUEDA[j][1]
                paradas[j] = min(1f, (gh + f * alc) / (gh + alc))
                cores[j] = Color.argb((kq * a * 255).toInt().coerceIn(0, 255),
                    Color.red(l.cor), Color.green(l.cor), Color.blue(l.cor))
            }
            pGlow.shader = android.graphics.RadialGradient(px, py, r, cores, paradas,
                android.graphics.Shader.TileMode.CLAMP)
            lc.drawCircle(px, py, r, pGlow)
            pGlow.shader = null
        }
        pGlow.xfermode = null
        // o mapa MULTIPLICA a cena (o borrão do upscale É o degradê)
        pSmooth.xfermode = MULT
        c.drawBitmap(bm, android.graphics.Rect(0, 0, w, h),
            android.graphics.RectF(0f, 0f, cw, ch), pSmooth)
        pSmooth.xfermode = null
    }

    /** O VIDRO aceso: a única parte que continua ADITIVA, porque acesa ela fica
     *  mais clara que a cor de dia e nenhum multiply chega lá. Do tamanho do
     *  vidro (mais um estouro curto), não do tamanho da luminária. */
    private fun desenharLuzesCena(c: Canvas, tf: Tf, escuro: Float, ts: Long) {
        pGlow.xfermode = ADD
        atualizarFogueira(marca.luzes)
        for ((i, l) in marca.luzes.withIndex()) {
            if (!luzAcesa(l, i)) continue
            val peso = luzPeso(l)
            val a = min(1f, escuro * luzOsc(l, ts)) * (0.45f + 0.55f * peso)
            val px = tf.ox + l.cx * tf.s; val py = tf.oy + l.cy * tf.s
            // 1,06× a caixa do vidro: a elipse inscrita deixaria os cantos da
            // placa retangular apagados, e o vidro já vem apertado.
            val rx = max(1f, l.vw / 2f * 1.06f * tf.s)
            val ry = max(1f, l.vh / 2f * 1.06f * tf.s)
            val cr = Color.red(l.cor); val cg = Color.green(l.cor); val cb = Color.blue(l.cor)
            // UM gradiente só: o vidro e o estouro curto em volta dele. Eram
            // dois (elipse do vidro + halo) e no fiordes, com 88 janelas, o
            // frame de noite ia de 2,2 ms pra 5,4 — o caro não é a área
            // pintada, é criar o gradiente. O estouro é curto de propósito: na
            // arte de noite o AR em volta do lampião é escuro, quem brilha é a
            // superfície que recebe a luz, e disso cuida o mapa de luz.
            val rb = ry * 1.9f
            c.save(); c.translate(px, py); c.scale(rx / ry, 1f)
            pGlow.shader = android.graphics.RadialGradient(0f, 0f, rb,
                intArrayOf(Color.argb((0.78f * a * 255).toInt().coerceIn(0, 255), cr, cg, cb),
                           Color.argb((0.66f * a * 255).toInt().coerceIn(0, 255), cr, cg, cb),
                           Color.argb((0.20f * a * 255).toInt().coerceIn(0, 255), cr, cg, cb),
                           Color.argb(0, cr, cg, cb)),
                floatArrayOf(0f, 0.368f, 0.526f, 1f),   // 0,70 do vidro · borda do vidro
                android.graphics.Shader.TileMode.CLAMP)
            c.drawCircle(0f, 0f, rb, pGlow)
            pGlow.shader = null
            c.restore()
        }
        pGlow.xfermode = null
    }

    private fun desenharLuzes(c: Canvas, tf: Tf, escuro: Float, ts: Long) {
        if (!temLuzes() || escuro < 0.12f) return
        if (marca.luzes.isNotEmpty()) { desenharLuzesCena(c, tf, escuro, ts); return }
        pSprite.xfermode = ADD
        if (janelasAcesas(estado.hora)) {
            val sp = Atlas.get("glow_janela")
            for (j in Atlas.janelas) {
                val pulso = 0.85f + 0.15f * sin(ts / 1000f * 1.3f + j.x)
                val dw = j.w * 0.5f * tf.s * Atlas.SPILL_VIDRO; val dh = j.h * 0.5f * tf.s * Atlas.SPILL_VIDRO
                setA(pSprite, min(1f, escuro * 0.62f * pulso))
                for (sx in intArrayOf(-1, 1)) for (sy in intArrayOf(-1, 1)) {
                    val cx = j.x + sx * j.w * 0.24f; val cy = j.y + sy * j.h * 0.24f
                    blit(c, sp, tf.ox + cx * tf.s - dw / 2, tf.oy + cy * tf.s - dh / 2, dw, dh, pSprite)
                }
            }
        }
        // A ARTE da cena já traz o lampião desenhado; o motor só o ACENDE. Só o
        // pack pixel tem glow de verdade no slot — nos packs de estilo (que têm
        // uma LANTERNA ali) o lampião acende por HALO radial quente.
        if (estado.premium && lampioesAcesos(estado.hora)) {
            val sp = Atlas.get("glow_lampiao")
            for (l in Atlas.lampioes) {
                val flick = 0.72f + 0.28f * sin(ts / 1000f * 7 + l.x) * sin(ts / 1000f * 3.3f + l.y)
                val dw = l.w * tf.s * Atlas.SPILL_LAMPIAO; val dh = l.h * tf.s * Atlas.SPILL_LAMPIAO
                val px = tf.ox + l.x * tf.s; val py = tf.oy + l.y * tf.s
                if (estiloCfg.lampiaoSprite) {
                    setA(pSprite, min(1f, escuro * 0.9f * flick))
                    blit(c, sp, px - dw / 2, py - dh / 2, dw, dh, pSprite)
                } else {
                    glowQuente(c, px, py, max(dw, dh), min(1f, escuro * flick))
                }
            }
        }
        pSprite.xfermode = null; setA(pSprite, 1f)
    }

    // ─────────────────────────────────────────────────────────────────
    //  Fumaça da chaminé
    // ─────────────────────────────────────────────────────────────────
    private fun estadoChamine(temp: Float, h: Float): String {
        if (!estado.premium || temp >= Atlas.TEMP_FOGO) return "apagada"
        val refeicao = (h >= 6 && h <= 8) || (h >= 18 && h <= 21)
        return if (refeicao || temp < Atlas.TEMP_INTENSO) "densa" else "fina"
    }
    private fun updateFumaca(dt: Float) {
        if (!temFumaca()) { if (puffs.isNotEmpty()) puffs.clear(); return }
        val est = estadoChamine(estado.temp, estado.hora)
        if (est != "apagada") {
            val densa = est == "densa"; val taxa = if (densa) 5.5f else 3f
            // deriva lateral guiada pelo vento (calmo = sobe reto; ventando =
            // inclina junto com as folhas). Puff(x, y, vx, vy, t, dur, ...)
            val ventoLean = 1.2f + max(0f, estado.vento) * 0.30f
            puffAcc += taxa * dt
            while (puffAcc >= 1) {
                puffAcc -= 1
                // boca: a marcada na cena (sorteia entre as marcadas) ou a
                // chaminé da cabana
                val boca = if (marca.fumaca.isEmpty())
                    BocaFumaca(Atlas.Chamine.x, Atlas.Chamine.y, Atlas.Chamine.w)
                else marca.fumaca[rnd.nextInt(marca.fumaca.size)]
                puffs.add(Puff(
                    boca.x + (rnd.nextFloat() - 0.5f) * boca.w,                     // x
                    boca.y + (rnd.nextFloat() - 0.5f) * 3,                          // y
                    ventoLean * (0.6f + rnd.nextFloat() * 0.7f),                    // vx (deriva)
                    -(if (densa) 30f else 22f) - rnd.nextFloat() * 8,               // vy (sobe)
                    0f,                                                             // t
                    (if (densa) 4.2f else 3.6f) + rnd.nextFloat() * 1.2f,           // dur
                    Atlas.fumacaSprites[rnd.nextInt(3)],                            // sp
                    if (densa) 0.7f else 0.55f, if (densa) 2.4f else 1.7f,          // esc0, esc1
                    if (densa) 0.85f else 0.62f, (rnd.nextFloat() - 0.5f) * 0.6f))  // aMax, giro
            }
        }
        val it = puffs.iterator()
        while (it.hasNext()) {
            val p = it.next()
            p.t += dt; p.x += p.vx * dt; p.y += p.vy * dt; p.vy += 2 * dt
            if (p.t >= p.dur) it.remove()
        }
    }
    private fun desenharFumaca(c: Canvas, tf: Tf) {
        if (puffs.isEmpty()) return
        pSmooth.xfermode = null
        for (p in puffs) {
            val k = p.t / p.dur; val esc = p.esc0 + (p.esc1 - p.esc0) * k
            val a = sin(k * Math.PI).toFloat() * p.aMax
            if (a < 0.02f) continue
            val sp = Atlas[p.sp]; val dw = sp.w * tf.s * esc; val dh = sp.h * tf.s * esc
            setA(pSmooth, a)
            c.save(); c.translate(tf.ox + p.x * tf.s, tf.oy + p.y * tf.s)
            c.rotate(Math.toDegrees((p.giro * k).toDouble()).toFloat())
            src.set(sp.x * RES, sp.y * RES, (sp.x + sp.w) * RES, (sp.y + sp.h) * RES)
            dst.set(-dw / 2, -dh / 2, dw / 2, dh / 2)
            c.drawBitmap(sprites, src, dst, pSmooth); c.restore()
        }
        setA(pSmooth, 1f)
    }

    // ─────────────────────────────────────────────────────────────────
    //  Vento (folhas + riscos rodopiando)
    // ─────────────────────────────────────────────────────────────────
    private fun ventoIntensidade() = ((estado.vento - Atlas.VENTO_MIN) / 42f).coerceIn(0f, 1f)
    private fun makeLeaf(intens: Float): Leaf = Leaf(
        -30f - rnd.nextFloat() * 120, 40f + rnd.nextFloat() * 1300,
        60f + intens * 190 + rnd.nextFloat() * 60, -8f + rnd.nextFloat() * 18,
        8f + rnd.nextFloat() * 22, rnd.nextFloat() * 6.283f, 1.4f + rnd.nextFloat() * 2.6f + intens * 2,
        rnd.nextFloat() * 6.283f, (rnd.nextFloat() - 0.5f) * (5 + intens * 8),
        Atlas.folhasSprites[rnd.nextInt(3)], 1.3f + rnd.nextFloat() * 0.9f)
    // Rajada fluida que surge em qualquer ponto, deriva suave p/ a direita,
    // ondula, rodopia e some (ciclo de vida). Espelha makeWisp do index.js.
    private fun makeWisp(intens: Float): Wisp = Wisp(
        rnd.nextFloat() * 688f,                          // x: surge de qualquer parte
        30f + rnd.nextFloat() * 1350f,                   // y
        30f + intens * 90f + rnd.nextFloat() * 30f,      // vx: deriva suave
        -10f + rnd.nextFloat() * 16f,                    // vy: leve subida/queda
        100f + rnd.nextFloat() * 140f,                   // len
        10f + rnd.nextFloat() * 16f,                     // amp
        0.6f + rnd.nextFloat() * 0.7f,                   // waves (< 1 → S suave)
        rnd.nextFloat() * 6.283f,                        // phase
        9f + rnd.nextFloat() * 9f,                       // curlR
        if (rnd.nextBoolean()) 1f else -1f,              // curlDir
        0f,                                              // t (vida)
        1.8f + rnd.nextFloat() * 1.6f)                   // dur
    private fun updateVento(dt: Float) {
        val intens = ventoIntensidade()
        if (intens <= 0f) { leaves.clear(); wisps.clear(); return }
        val alvoF = (3 + intens * 20).toInt(); val alvoW = (2 + intens * 10).toInt()
        while (leaves.size < alvoF) leaves.add(makeLeaf(intens))
        while (leaves.size > alvoF) leaves.removeAt(leaves.size - 1)
        while (wisps.size < alvoW) wisps.add(makeWisp(intens))
        while (wisps.size > alvoW) wisps.removeAt(wisps.size - 1)
        for (l in leaves) {
            l.x += l.vx * dt; l.baseY += l.vy * dt; l.wavePhase += l.waveSpeed * dt; l.rot += l.spin * dt
            if (l.x > 720) { val nl = makeLeaf(intens); l.copyFrom(nl) }
        }
        for (w in wisps) {
            w.t += dt; w.x += w.vx * dt; w.y += w.vy * dt; w.phase += 0.5f * dt
            if (w.t >= w.dur) w.copyFrom(makeWisp(intens))   // some e renasce noutro lugar
        }
    }
    private fun desenharVento(c: Canvas, tf: Tf) {
        val intens = ventoIntensidade()
        if (intens <= 0f) return
        pStroke.xfermode = null
        for (w in wisps) {
            val p = w.t / w.dur                          // fade pelo ciclo de vida
            val fin = min(1f, p / 0.30f)
            val fout = min(1f, (1f - p) / 0.40f)
            val lifeA = max(0f, min(fin, fout))
            desenharWisp(c, w, tf, lifeA * (0.22f + intens * 0.40f))
        }
        pSprite.xfermode = null; setA(pSprite, 1f)
        for (l in leaves) {
            val sp = Atlas[l.sp]; val y = l.baseY + sin(l.wavePhase) * l.waveAmp
            val dw = sp.w * tf.s * l.esc; val dh = sp.h * tf.s * l.esc
            c.save(); c.translate(tf.ox + l.x * tf.s, tf.oy + y * tf.s)
            c.rotate(Math.toDegrees(l.rot.toDouble()).toFloat())
            blit(c, sp, -dw / 2, -dh / 2, dw, dh, pSprite); c.restore()
        }
    }
    /**
     * A curva da rajada em pontos (x, y, peso da largura): corpo ondulado +
     * rodopio na ponta. Sai daqui separado do desenho porque o Van Gogh passa o
     * pincel VÁRIAS vezes sobre a MESMA curva (ver [FitaVento]).
     */
    private fun caminhoWisp(w: Wisp, tf: Tf): FloatArray {
        val pi = Math.PI.toFloat()
        val N = 26; val S = 14
        val out = FloatArray((N + S + 1) * 3)
        val x0 = tf.ox + w.x * tf.s; val y0 = tf.oy + w.y * tf.s; val len = w.len * tf.s
        var k = 0
        out[k++] = x0; out[k++] = y0; out[k++] = 0.35f
        for (i in 1..N) {
            val t = i / N.toFloat()
            val env = sin(pi * t)
            out[k++] = x0 + t * len
            out[k++] = y0 + sin(t * w.waves * 6.283f + w.phase) * w.amp * tf.s * env
            out[k++] = 0.35f + 0.65f * env
        }
        val ex = out[k - 3]; val ey = out[k - 2]
        val cx = ex; val cy = ey - w.curlDir * w.curlR * tf.s
        var ang = if (w.curlDir > 0f) pi / 2f else -pi / 2f
        var r = w.curlR * tf.s
        for (s in 1..S) {
            ang += 0.45f * w.curlDir; r *= 0.86f
            out[k++] = cx + cos(ang) * r
            out[k++] = cy + sin(ang) * r
            out[k++] = 0.5f * (1f - s / S.toFloat()) + 0.15f
        }
        return out
    }

    private fun desenharWisp(c: Canvas, w: Wisp, tf: Tf, alpha: Float) {
        if (alpha <= 0.01f) return
        val pts = caminhoWisp(w, tf)
        val n = pts.size / 3
        val baseW = max(1f, tf.s * 1.7f)
        val fitas = estiloCfg.wisp ?: FITA_PADRAO
        val dab = estiloCfg.wispDab
        for (f in fitas) {
            pStroke.color = f.cor
            for (i in 1 until n) {
                val ax = pts[(i - 1) * 3]; val ay = pts[(i - 1) * 3 + 1]
                val bx = pts[i * 3]; val by = pts[i * 3 + 1]
                var a = alpha * f.aMul * pts[i * 3 + 2]
                if (dab > 0f) {
                    val d = abs(sin((i * 2.2f) + w.phase))
                    a *= 1f - dab + dab * d
                }
                if (a <= 0.012f) continue
                // deslocamento PERPENDICULAR: é o que põe as fitas de tinta lado
                // a lado em vez de uma sombra diagonal.
                var dx = 0f; var dy = 0f
                if (f.off != 0f) {
                    val vx = bx - ax; val vy = by - ay
                    val m = max(0.001f, kotlin.math.hypot(vx, vy))
                    dx = -vy / m * f.off * tf.s; dy = vx / m * f.off * tf.s
                }
                pStroke.strokeWidth = baseW * pts[i * 3 + 2] * f.wMul
                pStroke.alpha = (a.coerceIn(0f, 1f) * 255f).toInt()
                c.drawLine(ax + dx, ay + dy, bx + dx, by + dy, pStroke)
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────
    //  Tint de luz do dia (multiply)
    // ─────────────────────────────────────────────────────────────────
    private fun tintColor(hora: Float): IntArray {
        for (i in 0 until TINT_KEYS.size - 1) {
            val a = TINT_KEYS[i]; val b = TINT_KEYS[i + 1]
            if (hora >= a.h && hora <= b.h) {
                val k = (hora - a.h) / (b.h - a.h)
                return intArrayOf(
                    (a.c[0] + (b.c[0] - a.c[0]) * k).toInt(),
                    (a.c[1] + (b.c[1] - a.c[1]) * k).toInt(),
                    (a.c[2] + (b.c[2] - a.c[2]) * k).toInt())
            }
        }
        return intArrayOf(255, 255, 255)
    }

    private class TintKey(val h: Float, val c: IntArray)
    companion object {
        /** Fração das janelas que acende em cada noite (ver luzDoLote). */
        private const val LUZ_PROB = 0.55f
        // ── Luz de verdade (ver desenharMapaLuz) ────────────────────
        /** Alcance do derrame do lampião, em px de arte (base 1086 de largura). */
        private const val LUZ_ALCANCE = 88f
        /** Janela/placa: a luz está ATRÁS do papel, quase não derrama. */
        private const val LUZ_ALCANCE_JANELA = 22f
        /** Fogo dentro do cômodo: derrama mais que o papel da janela, menos
         *  que o lampião na rua aberta (modo "fogueira"). */
        private const val LUZ_ALCANCE_FOGO = 44f
        /** Queda medida: [fração do alcance, quanto da luz sobra]. */
        private val LUZ_QUEDA = arrayOf(
            floatArrayOf(0f, 1f), floatArrayOf(0.09f, 0.60f), floatArrayOf(0.25f, 0.36f),
            floatArrayOf(0.41f, 0.21f), floatArrayOf(0.61f, 0.13f),
            floatArrayOf(0.86f, 0.04f), floatArrayOf(1f, 0f))
        /** Mapa de luz em 1/N da resolução: luz é sinal de baixa frequência. */
        private const val LM_ESC = 3f
        /** Aurora: bitmap auxiliar em 1/N da tela (luz é sinal de baixa
         *  frequência, igual ao mapa de luz), cortinas e colunas por cortina. */
        private const val AU_ESC = 3f
        private const val AU_FITAS = 3
        private const val AU_COLS = 56
        /** Acima disso a zona é DERIVADA (cobre a tela toda) e não vira oclusão. */
        private const val AO_LIMIAR = 0.35f
        /** Quanto do ambiente sobra onde a superfície não vê o céu. */
        private const val AO_ABRIGO = 0.62f
        /** Gravidade da goteira, em px de CENA por s². */
        private const val GOT_G = 900f
        /** Rajada sem receita de estilo: um risco claro só. */
        private val FITA_PADRAO = listOf(FitaVento(Color.rgb(236, 240, 246), 1f, 0f, 1f))
        private val TINT_KEYS = listOf(
            // NOITE MEDIDA (2026-08-27): comparando a arte de DIA e a de NOITE
            // do beco na MESMA calçada, longe de lampião, o multiply que leva
            // uma na outra é (63, 68, 101) — R e G batiam, o B estava 20% acima.
            // Era isso que deixava a pedra lavanda em vez de cinza-escuro.
            TintKey(0f, intArrayOf(55, 63, 101)),
            TintKey(5.0f, intArrayOf(72, 76, 108)),
            TintKey(6.0f, intArrayOf(200, 140, 130)),
            TintKey(7.0f, intArrayOf(255, 200, 175)),
            TintKey(9.0f, intArrayOf(255, 245, 232)),
            TintKey(11.0f, intArrayOf(255, 255, 255)),
            TintKey(15.0f, intArrayOf(255, 250, 240)),
            TintKey(17.0f, intArrayOf(255, 208, 165)),
            TintKey(18.0f, intArrayOf(255, 158, 110)),
            TintKey(18.75f, intArrayOf(225, 120, 115)),
            TintKey(19.75f, intArrayOf(120, 92, 145)),
            TintKey(20.75f, intArrayOf(70, 71, 105)),
            TintKey(24f, intArrayOf(55, 63, 101)),
        )
    }
}
