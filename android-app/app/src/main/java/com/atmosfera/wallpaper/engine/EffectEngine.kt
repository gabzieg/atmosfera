package com.atmosfera.wallpaper.engine

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
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
    private var bandPixel: Bitmap? = null     // tira de frames do pano (arte pixel)
    private var bandClay: Bitmap? = null      // idem, arte clay
    private lateinit var frente: Bitmap
    private lateinit var sprites: Bitmap
    private lateinit var neve: Bitmap
    private lateinit var neveForte: Bitmap
    private lateinit var nevoa: Bitmap
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

    private class Tf(val s: Float, val ox: Float, val oy: Float)

    // ─────────────────────────────────────────────────────────────────
    //  Carregamento
    // ─────────────────────────────────────────────────────────────────
    /** Carrega os assets do cenário [cenaId] (arte de fundo [arte]) + o pack de
     *  sprites do estilo de efeito [estilo]. Pode ser chamado de novo p/ trocar. */
    fun carregar(assets: AssetManager, cenaId: String = "cabana",
                 arte: String = "pixel", estilo: String = "pixel") = synchronized(lock) {
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
        fundo = bmp(fp + "fundo.png")
        frente = bmp(fp + "frente.png")
        // opcional por ARTE (nem toda cena tem luz pintada) — ver tools/luzes_off.py
        luzesOff = try { bmp(fp + "luzes_off.png") } catch (e: Exception) { null }
        sprites = bmp(estiloCfg.arquivo)
        neve = bmp("neve_acumulo.png")
        neveForte = bmp("neve_acumulo_forte.png")
        nevoa = bmp("nevoa.png")
        bandPixel = try { bmp("bandeira_pixel.png") } catch (e: Exception) { null }
        bandClay = try { bmp("bandeira_clay.png") } catch (e: Exception) { null }
        roofPts.clear(); lakePts.clear()
        extrairZonas(bmp(cenaCfg.prefixo + "zonas.png"))
        carregarProf(try { bmp(cenaCfg.prefixo + "profundidade.png") } catch (e: Exception) { null })
        // luzes e saída de fumaça da MARCAÇÃO da cena (cena sem isso cai nas
        // constantes da cabana no Atlas — ver Marcacao.kt)
        marca = DadosMarcacao.ler(assets, cenaCfg.prefixo)
        // estado dependente da cena/dimensões
        clouds.clear(); drops.clear(); flakes.clear(); impacts.clear()
        brilhos.clear(); puffsVulcao.clear(); puffVulcAcc = 0f
        fogBanks.clear(); leaves.clear(); wisps.clear(); puffs.clear()
        bolt = null; snowAccum = 0f; roofAcc = 0f; lakeAcc = 0f; lastTs = 0L
        initStars()
        if (cenaCfg.vagalumes) initFireflies() else fireflies.clear()
        pronto = true
    }

    private fun liberarBitmaps() {
        if (::fundo.isInitialized) fundo.recycle()
        luzesOff?.recycle(); luzesOff = null
        bandPixel?.recycle(); bandPixel = null
        bandClay?.recycle(); bandClay = null
        if (::frente.isInitialized) frente.recycle()
        if (::sprites.isInitialized) sprites.recycle()
        if (::neve.isInitialized) neve.recycle()
        if (::neveForte.isInitialized) neveForte.recycle()
        if (::nevoa.isInitialized) nevoa.recycle()
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
        blitFull(canvas, fundo, tf, pSmooth)
        // Luzes APAGADAS enquanto é dia: janelas/lampiões vêm PINTADOS acesos na
        // arte. O overlay some ao anoitecer, quando o glow entra por cima.
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

        // updates
        atualizar(dt, cw, ch, escuro)
        updateVulcao(dt)
        updateBrilhos(dt)
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
        // bandeira por cima da frente: é o objeto mais à frente naquele ponto
        desenharBandeira(canvas, tf, ts)
        // brilho d'água por cima da frente: ali o mar É a camada de cima
        desenharBrilhos(canvas, tf)
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

        // day tint (multiply) sobre tudo
        val tc = tintColor(estado.hora)
        if (tc[0] != 255 || tc[1] != 255 || tc[2] != 255) {
            pFill.xfermode = MULT
            pFill.color = Color.rgb(tc[0], tc[1], tc[2])
            canvas.drawRect(0f, 0f, cw, ch, pFill)
            pFill.xfermode = null
        }

        // noite (aditivo, por cima da escuridão)
        if (st.a < 0.3f) {
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
        updateNevoa(dt)

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
    // ─────────────────────────────────────────────────────────────────
    private fun desenharSol(c: Canvas, tf: Tf) {
        if (estado.clima != "seco") return
        val A = cenaCfg.astros; val S = cenaCfg.solDe(arteId)
        val t = (estado.hora - estado.nascer) / (estado.por - estado.nascer)
        if (t < -0.02f || t > 1.02f) return
        val ix = A.x1 + (A.x0 - A.x1) * t              // nasce à direita
        val iy = S.yBase - (S.yBase - S.yPico) * 4f * t * (1 - t)
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
        val iy = L.yBase - (L.yBase - L.yPico) * 4f * t * (1 - t)
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
    private fun desenharPingos(c: Canvas, tf: Tf) {
        val sp = Atlas.get("pingo"); val sc = tf.s * estado.scaleMult
        val dw = sp.w * sc; val dh = sp.h * sc
        pSprite.xfermode = null; setA(pSprite, 1f)
        for (d in drops) blit(c, sp, d.x - dw / 2, d.y - dh / 2, dw, dh, pSprite)
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
            val dw = sp.w * sc * im.esc; val dh = sp.h * sc * im.esc
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
    private fun desenharLuzesCena(c: Canvas, tf: Tf, escuro: Float, ts: Long) {
        for (l in marca.luzes) {
            val aceso = if (l.tipo == "completa") lampioesAcesos(estado.hora)
                        else janelasAcesas(estado.hora)
            if (!aceso) continue
            val osc = if (l.tipo == "completa")
                0.72f + 0.28f * sin(ts / 1000f * 7 + l.x) * sin(ts / 1000f * 3.3f + l.y)
            else 0.85f + 0.15f * sin(ts / 1000f * 1.3f + l.x)
            val raio = max(l.w, l.h) * tf.s * Atlas.SPILL_LAMPIAO * 0.6f
            glowQuente(c, tf.ox + l.cx * tf.s, tf.oy + l.cy * tf.s, raio,
                min(1f, escuro * osc))
        }
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
        /** Gravidade da goteira, em px de CENA por s². */
        private const val GOT_G = 900f
        /** Rajada sem receita de estilo: um risco claro só. */
        private val FITA_PADRAO = listOf(FitaVento(Color.rgb(236, 240, 246), 1f, 0f, 1f))
        private val TINT_KEYS = listOf(
            TintKey(0f, intArrayOf(55, 65, 120)),
            TintKey(5.0f, intArrayOf(72, 78, 125)),
            TintKey(6.0f, intArrayOf(200, 140, 130)),
            TintKey(7.0f, intArrayOf(255, 200, 175)),
            TintKey(9.0f, intArrayOf(255, 245, 232)),
            TintKey(11.0f, intArrayOf(255, 255, 255)),
            TintKey(15.0f, intArrayOf(255, 250, 240)),
            TintKey(17.0f, intArrayOf(255, 208, 165)),
            TintKey(18.0f, intArrayOf(255, 158, 110)),
            TintKey(18.75f, intArrayOf(225, 120, 115)),
            TintKey(19.75f, intArrayOf(120, 92, 145)),
            TintKey(20.75f, intArrayOf(70, 72, 122)),
            TintKey(24f, intArrayOf(55, 65, 120)),
        )
    }
}
