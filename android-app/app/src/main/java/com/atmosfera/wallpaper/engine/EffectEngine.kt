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
    private lateinit var frente: Bitmap
    private lateinit var sprites: Bitmap
    private lateinit var neve: Bitmap
    private lateinit var neveForte: Bitmap
    private lateinit var nevoa: Bitmap
    var pronto = false; private set

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
    private val ADD = PorterDuffXfermode(PorterDuff.Mode.ADD)
    private val MULT = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)

    private class Tf(val s: Float, val ox: Float, val oy: Float)

    // ─────────────────────────────────────────────────────────────────
    //  Carregamento
    // ─────────────────────────────────────────────────────────────────
    fun carregar(assets: AssetManager) {
        fun bmp(nome: String) = assets.open("atmosfera/$nome").use { BitmapFactory.decodeStream(it) }
        fundo = bmp("fundo.png")
        frente = bmp("frente.png")
        sprites = bmp("sprites.png")
        neve = bmp("neve_acumulo.png")
        neveForte = bmp("neve_acumulo_forte.png")
        nevoa = bmp("nevoa.png")
        extrairZonas(bmp("zonas.png"))
        initStars(); initFireflies()
        pronto = true
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
                    if (r > 200 && g > 200 && b < 100) roofPts.add(intArrayOf(x, y))
                    else if (r > 200 && g < 100 && b < 100) lakePts.add(intArrayOf(x, y))
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
    fun draw(canvas: Canvas, cw: Float, ch: Float, tsMs: Long) {
        if (!pronto) return
        var dt = if (lastTs == 0L) 0f else (tsMs - lastTs) / 1000f
        lastTs = tsMs
        if (dt > 0.05f) dt = 0.05f
        val ts = tsMs

        val tf = cover(cw, ch)
        val escuro = nightFactor(estado.hora)

        // fundo
        canvas.drawColor(Color.BLACK)
        blitFull(canvas, fundo, tf, pSmooth)

        // updates
        atualizar(dt, cw, ch, escuro)

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
        blitFull(canvas, frente, tf, pSmooth)
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
            if (nv.ix > Atlas.CENA_W + 20) nv.ix = -w - 20
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
        clouds.clear(); drops.clear(); flakes.clear(); impacts.clear(); bolt = null
        roofAcc = 0f; lakeAcc = 0f; puffAcc = 0f
    }

    /** Libera os bitmaps (chamar no onDestroy do wallpaper). */
    fun liberar() {
        if (::fundo.isInitialized) fundo.recycle()
        if (::frente.isInitialized) frente.recycle()
        if (::sprites.isInitialized) sprites.recycle()
        if (::neve.isInitialized) neve.recycle()
        if (::neveForte.isInitialized) neveForte.recycle()
        if (::nevoa.isInitialized) nevoa.recycle()
        pronto = false
    }

    // ── Transform "cover" ───────────────────────────────────────────
    private fun cover(cw: Float, ch: Float): Tf {
        val s = max(cw / Atlas.CENA_W, ch / Atlas.CENA_H)
        return Tf(s, (cw - Atlas.CENA_W * s) / 2f, (ch - Atlas.CENA_H * s) / 2f)
    }

    private fun blitFull(c: Canvas, b: Bitmap, tf: Tf, p: Paint) {
        dst.set(tf.ox, tf.oy, tf.ox + b.width * tf.s, tf.oy + b.height * tf.s)
        c.drawBitmap(b, null, dst, p)
    }

    private fun blit(c: Canvas, sp: Sprite, dx: Float, dy: Float, dw: Float, dh: Float, p: Paint) {
        src.set(sp.x, sp.y, sp.x + sp.w, sp.y + sp.h)
        dst.set(dx, dy, dx + dw, dy + dh)
        c.drawBitmap(sprites, src, dst, p)
    }

    private fun setA(p: Paint, a: Float) { p.alpha = (a.coerceIn(0f, 1f) * 255f).toInt() }

    // ─────────────────────────────────────────────────────────────────
    //  Sol / Lua / céu diurno
    // ─────────────────────────────────────────────────────────────────
    private fun desenharSol(c: Canvas, tf: Tf) {
        if (estado.clima != "seco") return
        val S = Atlas.SolCfg
        val t = (estado.hora - estado.nascer) / (estado.por - estado.nascer)
        if (t < -0.02f || t > 1.02f) return
        val ix = S.x1 + (S.x0 - S.x1) * t              // nasce à direita
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
        val L = Atlas.LuaCfg
        val ix = 700f + (-10f - 700f) * t
        val iy = L.yBase - (L.yBase - L.yPico) * 4f * t * (1 - t)
        val fadeAlt = ((215f - iy) / 35f).coerceIn(0f, 1f)
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
        roofAcc += estado.roofRate * dt; lakeAcc += estado.lakeRate * dt
        while (roofAcc >= 1) { spawnImpact(roofPts, Atlas.seqTelhado); roofAcc -= 1 }
        while (lakeAcc >= 1) { spawnImpact(lakePts, Atlas.seqLago); lakeAcc -= 1 }
    }
    private fun spawnImpact(pts: List<IntArray>, seq: List<String>) {
        if (pts.isEmpty()) return
        val p = pts[rnd.nextInt(pts.size)]
        impacts.add(Impact(p[0].toFloat(), p[1].toFloat(), seq, 0f))
    }
    private fun desenharImpactos(c: Canvas, tf: Tf) {
        val sc = tf.s * estado.scaleMult
        pSprite.xfermode = null; setA(pSprite, 1f)
        for (im in impacts) {
            val fi = min((im.t / estado.frameMs).toInt(), im.seq.size - 1)
            val sp = Atlas[im.seq[fi]]
            val px = tf.ox + im.ix * tf.s; val py = tf.oy + im.iy * tf.s
            val dw = sp.w * sc; val dh = sp.h * sc
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
            if (f.x - w / 2 > Atlas.CENA_W + 40) f.x = -w / 2 - 40
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
        for (i in 0 until 90) {
            val twinkle = r.nextFloat() < 0.4f
            stars.add(Star(r.nextFloat() * 688, r.nextFloat() * 205 + 8,
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
        if (escuro < 0.2f || !estado.premium) return
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
    private fun janelasAcesas(h: Float) = h >= estado.por - 0.3f
    private fun lampioesAcesos(h: Float) = h >= estado.por - 0.3f || h <= estado.nascer + 0.3f
    private fun desenharLuzes(c: Canvas, tf: Tf, escuro: Float, ts: Long) {
        if (escuro < 0.12f) return
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
        if (estado.premium && lampioesAcesos(estado.hora)) {
            val sp = Atlas.get("glow_lampiao")
            for (l in Atlas.lampioes) {
                val flick = 0.72f + 0.28f * sin(ts / 1000f * 7 + l.x) * sin(ts / 1000f * 3.3f + l.y)
                val dw = l.w * tf.s * Atlas.SPILL_LAMPIAO; val dh = l.h * tf.s * Atlas.SPILL_LAMPIAO
                setA(pSprite, min(1f, escuro * 0.9f * flick))
                blit(c, sp, tf.ox + l.x * tf.s - dw / 2, tf.oy + l.y * tf.s - dh / 2, dw, dh, pSprite)
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
        val est = estadoChamine(estado.temp, estado.hora)
        if (est != "apagada") {
            val densa = est == "densa"; val taxa = if (densa) 5.5f else 3f
            // deriva lateral guiada pelo vento (calmo = sobe reto; ventando =
            // inclina junto com as folhas). Puff(x, y, vx, vy, t, dur, ...)
            val ventoLean = 1.2f + max(0f, estado.vento) * 0.30f
            puffAcc += taxa * dt
            while (puffAcc >= 1) {
                puffAcc -= 1
                puffs.add(Puff(
                    Atlas.Chamine.x + (rnd.nextFloat() - 0.5f) * Atlas.Chamine.w,   // x
                    Atlas.Chamine.y + (rnd.nextFloat() - 0.5f) * 3,                 // y
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
            src.set(sp.x, sp.y, sp.x + sp.w, sp.y + sp.h); dst.set(-dw / 2, -dh / 2, dw / 2, dh / 2)
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
    private fun desenharWisp(c: Canvas, w: Wisp, tf: Tf, alpha: Float) {
        if (alpha <= 0.01f) return
        val pi = Math.PI.toFloat()
        pStroke.color = Color.rgb(236, 240, 246)
        val x0 = tf.ox + w.x * tf.s; val y0 = tf.oy + w.y * tf.s; val len = w.len * tf.s
        val baseW = max(1f, tf.s * 1.7f)
        val N = 26
        // corpo: amplitude em envelope sin(pi t) → calmo nas pontas, ondula no meio.
        // Cada segmento afina/esmaece nas pontas = pincelada fluida (sem zigzag).
        var pxPrev = x0; var pyPrev = y0
        for (i in 1..N) {
            val t = i / N.toFloat()
            val env = sin(pi * t)
            val px = x0 + t * len
            val py = y0 + sin(t * w.waves * 6.283f + w.phase) * w.amp * tf.s * env
            pStroke.strokeWidth = baseW * (0.35f + 0.65f * env)
            pStroke.alpha = ((alpha * (0.45f + 0.55f * env)).coerceIn(0f, 1f) * 255f).toInt()
            c.drawLine(pxPrev, pyPrev, px, py, pStroke)
            pxPrev = px; pyPrev = py
        }
        // rodopio nascendo da ponta (centro deslocado perpendicular → sem "pulo")
        val ex = pxPrev; val ey = pyPrev
        val cx = ex; val cy = ey - w.curlDir * w.curlR * tf.s
        var ang = if (w.curlDir > 0f) pi / 2f else -pi / 2f
        var r = w.curlR * tf.s; var qx = ex; var qy = ey
        for (s in 1..14) {
            ang += 0.45f * w.curlDir; r *= 0.86f
            val nx = cx + cos(ang) * r; val ny = cy + sin(ang) * r
            val fade = 1f - s / 14f
            pStroke.strokeWidth = baseW * (0.5f * fade + 0.15f)
            pStroke.alpha = ((alpha * 0.5f * fade).coerceIn(0f, 1f) * 255f).toInt()
            c.drawLine(qx, qy, nx, ny, pStroke)
            qx = nx; qy = ny
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
