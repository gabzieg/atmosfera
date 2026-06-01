package com.atmosfera.wallpaper.service

import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.SurfaceHolder
import com.atmosfera.wallpaper.weather.*
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.math.min

/**
 * AtmosferaWallpaperService
 *
 * Live Wallpaper que:
 *  1. Busca o clima atual via Open-Meteo (com cache de 30 min)
 *  2. Seleciona a imagem de asset correspondente
 *  3. Desenha a imagem + overlay de relógio e temperatura na tela
 *  4. Atualiza o relógio a cada minuto
 *  5. Anima partículas de chuva/neve quando aplicável
 */
class AtmosferaWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = AtmosferaEngine()

    inner class AtmosferaEngine : Engine() {

        private val TAG = "AtmosferaEngine"
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        private val handler = Handler(Looper.getMainLooper())

        // ── Estado ─────────────────────────────────────────────────────────
        private var wallpaperBitmap: Bitmap? = null
        private var currentState: WeatherState? = null
        private var isVisible = false
        private var surfaceWidth = 0
        private var surfaceHeight = 0

        // ── Partículas ─────────────────────────────────────────────────────
        private val particles = mutableListOf<Particle>()
        private var lastParticleTime = 0L

        // ── Paints ─────────────────────────────────────────────────────────
        private val clockPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 160f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.LIGHT)
            setShadowLayer(12f, 0f, 2f, Color.argb(100, 0, 0, 0))
        }
        private val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 38f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            alpha = 210
            setShadowLayer(8f, 0f, 2f, Color.argb(80, 0, 0, 0))
        }
        private val tempPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 130f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.THIN)
            setShadowLayer(12f, 0f, 2f, Color.argb(100, 0, 0, 0))
        }
        private val descPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 36f
            alpha = 200
            setShadowLayer(6f, 0f, 1f, Color.argb(80, 0, 0, 0))
        }
        private val rainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(140, 180, 210, 255)
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
        private val snowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(200, 255, 255, 255)
            style = Paint.Style.FILL
        }
        private val overlayPaint = Paint().apply {
            color = Color.argb(60, 0, 0, 0)
        }

        // ── Runnable de tick do relógio ────────────────────────────────────
        private val tickRunnable = object : Runnable {
            override fun run() {
                if (isVisible) {
                    draw()
                    handler.postDelayed(this, 60_000L) // atualiza a cada minuto
                }
            }
        }

        // ── Runnable de animação de partículas ─────────────────────────────
        private val animRunnable = object : Runnable {
            override fun run() {
                if (isVisible && hasParticles()) {
                    updateParticles()
                    draw()
                    handler.postDelayed(this, 50L) // ~20 fps para partículas
                }
            }
        }

        // ──────────────────────────────────────────────────────────────────
        //  Ciclo de vida
        // ──────────────────────────────────────────────────────────────────

        override fun onSurfaceCreated(holder: SurfaceHolder) {
            super.onSurfaceCreated(holder)
            loadWeatherAndDraw()
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            surfaceWidth = width
            surfaceHeight = height
            loadBitmapForCurrentState()
            draw()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            isVisible = visible
            if (visible) {
                loadWeatherAndDraw()
                handler.post(tickRunnable)
                if (hasParticles()) handler.post(animRunnable)
            } else {
                handler.removeCallbacks(tickRunnable)
                handler.removeCallbacks(animRunnable)
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            handler.removeCallbacksAndMessages(null)
            scope.cancel()
            wallpaperBitmap?.recycle()
            wallpaperBitmap = null
        }

        // ──────────────────────────────────────────────────────────────────
        //  Lógica de clima
        // ──────────────────────────────────────────────────────────────────

        private fun loadWeatherAndDraw() {
            scope.launch {
                val cache = com.atmosfera.wallpaper.weather.WeatherCache(applicationContext)
                val locationHelper = LocationHelper(applicationContext)
                val repo = WeatherRepository()

                val (lat, lon) = locationHelper.getLocation()

                val state = if (!cache.isStale(lat, lon)) {
                    cache.get()
                } else {
                    repo.fetchWeather(lat, lon).getOrNull()?.also { cache.save(it, lat, lon) }
                        ?: cache.get()
                }

                state?.let {
                    currentState = it
                    loadBitmapForCurrentState()
                    setupParticles(it.condition)
                    draw()
                    if (hasParticles()) {
                        handler.removeCallbacks(animRunnable)
                        handler.post(animRunnable)
                    }
                } ?: run {
                    // Sem clima e sem cache: desenha fundo sólido
                    draw()
                }
            }
        }

        private fun loadBitmapForCurrentState() {
            val state = currentState ?: return
            val assetFile = state.assetFileName()
            try {
                val opts = BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.RGB_565 // menor uso de RAM
                }
                val stream = assets.open(assetFile)
                val raw = BitmapFactory.decodeStream(stream, null, opts)
                stream.close()

                raw?.let {
                    wallpaperBitmap?.recycle()
                    wallpaperBitmap = scaleBitmapToFill(it, surfaceWidth, surfaceHeight)
                    if (wallpaperBitmap !== it) it.recycle()
                }
            } catch (e: IOException) {
                Log.e(TAG, "Asset não encontrado: $assetFile — usando fallback.")
                wallpaperBitmap = null
            }
        }

        private fun scaleBitmapToFill(src: Bitmap, targetW: Int, targetH: Int): Bitmap {
            if (targetW == 0 || targetH == 0) return src
            val scale = maxOf(targetW.toFloat() / src.width, targetH.toFloat() / src.height)
            val newW = (src.width * scale).toInt()
            val newH = (src.height * scale).toInt()
            val scaled = Bitmap.createScaledBitmap(src, newW, newH, true)
            val x = (newW - targetW) / 2
            val y = (newH - targetH) / 2
            return Bitmap.createBitmap(scaled, x, y, targetW, targetH).also {
                if (it !== scaled) scaled.recycle()
            }
        }

        // ──────────────────────────────────────────────────────────────────
        //  Partículas (chuva / neve)
        // ──────────────────────────────────────────────────────────────────

        private fun setupParticles(condition: WeatherCondition) {
            particles.clear()
            when (condition) {
                WeatherCondition.LIGHT_RAIN -> spawnRainParticles(60)
                WeatherCondition.HEAVY_RAIN -> spawnRainParticles(150)
                WeatherCondition.STORM      -> spawnRainParticles(220)
                WeatherCondition.SNOW       -> spawnSnowParticles(80)
                else -> { /* sem partículas */ }
            }
        }

        private fun spawnRainParticles(count: Int) {
            repeat(count) {
                particles.add(Particle(
                    x = (Math.random() * (surfaceWidth + 200) - 100).toFloat(),
                    y = (Math.random() * surfaceHeight).toFloat(),
                    vx = -3f + (-1f * (Math.random() * 2).toFloat()),
                    vy = 20f + (Math.random() * 15).toFloat(),
                    length = 20f + (Math.random() * 20).toFloat(),
                    type = ParticleType.RAIN
                ))
            }
        }

        private fun spawnSnowParticles(count: Int) {
            repeat(count) {
                particles.add(Particle(
                    x = (Math.random() * surfaceWidth).toFloat(),
                    y = (Math.random() * surfaceHeight).toFloat(),
                    vx = (-1f + Math.random() * 2).toFloat(),
                    vy = (1f + Math.random() * 3).toFloat(),
                    length = (3f + Math.random() * 5).toFloat(),
                    type = ParticleType.SNOW
                ))
            }
        }

        private fun hasParticles(): Boolean =
            currentState?.condition in listOf(
                WeatherCondition.LIGHT_RAIN,
                WeatherCondition.HEAVY_RAIN,
                WeatherCondition.STORM,
                WeatherCondition.SNOW
            )

        private fun updateParticles() {
            particles.forEach { p ->
                p.x += p.vx
                p.y += p.vy
                // Wrap around
                if (p.y > surfaceHeight + 50) p.y = -50f
                if (p.x < -100) p.x = surfaceWidth + 50f
            }
        }

        // ──────────────────────────────────────────────────────────────────
        //  Desenho
        // ──────────────────────────────────────────────────────────────────

        private fun draw() {
            val holder = surfaceHolder ?: return
            var canvas: Canvas? = null
            try {
                canvas = holder.lockCanvas() ?: return
                drawFrame(canvas)
            } finally {
                canvas?.let { holder.unlockCanvasAndPost(it) }
            }
        }

        private fun drawFrame(canvas: Canvas) {
            val w = canvas.width.toFloat()
            val h = canvas.height.toFloat()

            // 1. Fundo (bitmap ou cor sólida de fallback)
            val bmp = wallpaperBitmap
            if (bmp != null && !bmp.isRecycled) {
                canvas.drawBitmap(bmp, 0f, 0f, null)
            } else {
                canvas.drawColor(Color.rgb(15, 20, 35))
            }

            // 2. Overlay escuro leve para legibilidade do texto
            canvas.drawRect(0f, 0f, w, h, overlayPaint)

            // 3. Partículas de chuva/neve
            drawParticles(canvas)

            // 4. Relógio (topo)
            val now = java.util.Calendar.getInstance()
            val hour = now.get(java.util.Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
            val minute = now.get(java.util.Calendar.MINUTE).toString().padStart(2, '0')
            val timeStr = "$hour:$minute"

            val clockX = w / 2 - clockPaint.measureText(timeStr) / 2
            canvas.drawText(timeStr, clockX, h * 0.18f, clockPaint)

            // 5. Data
            val dayNames = arrayOf("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")
            val monthNames = arrayOf("jan","fev","mar","abr","mai","jun","jul","ago","set","out","nov","dez")
            val dayName = dayNames[now.get(java.util.Calendar.DAY_OF_WEEK) - 1]
            val day = now.get(java.util.Calendar.DAY_OF_MONTH)
            val month = monthNames[now.get(java.util.Calendar.MONTH)]
            val dateStr = "$dayName, $day de $month"
            val dateX = w / 2 - datePaint.measureText(dateStr) / 2
            canvas.drawText(dateStr, dateX, h * 0.18f + 52f, datePaint)

            // 6. Temperatura (rodapé esquerdo)
            val state = currentState
            val tempStr = if (state != null) "${state.temperatureCelsius.toInt()}°" else "--°"
            canvas.drawText(tempStr, w * 0.08f, h * 0.88f, tempPaint)

            // 7. Descrição + sensação térmica (rodapé direito)
            if (state != null) {
                val feelsStr = "Sensação ${state.feelsLikeCelsius.toInt()}°"
                val descStr = state.description
                val descX = w - descPaint.measureText(descStr) - w * 0.08f
                val feelsX = w - descPaint.measureText(feelsStr) - w * 0.08f
                canvas.drawText(descStr, descX, h * 0.85f, descPaint)
                canvas.drawText(feelsStr, feelsX, h * 0.85f + 46f, descPaint)

                // Linha fina separadora
                val linePaint = Paint().apply {
                    color = Color.argb(80, 255, 255, 255)
                    strokeWidth = 1f
                }
                canvas.drawLine(w * 0.08f, h * 0.90f, w * 0.92f, h * 0.90f, linePaint)

                // Cidade (rodapé central)
                val cityPaint = Paint(descPaint).apply { textSize = 28f; alpha = 140 }
                val city = "Guarapuava, PR" // TODO: obter do LocationHelper
                val cityX = w / 2 - cityPaint.measureText(city) / 2
                canvas.drawText(city, cityX, h * 0.94f, cityPaint)
            }
        }

        private fun drawParticles(canvas: Canvas) {
            particles.forEach { p ->
                when (p.type) {
                    ParticleType.RAIN -> {
                        canvas.drawLine(
                            p.x, p.y,
                            p.x - p.vx * 3, p.y - p.length,
                            rainPaint
                        )
                    }
                    ParticleType.SNOW -> {
                        canvas.drawCircle(p.x, p.y, p.length / 2, snowPaint)
                    }
                }
            }
        }
    }
}

// ── Modelos de partícula ──────────────────────────────────────────────────────

enum class ParticleType { RAIN, SNOW }

data class Particle(
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    val length: Float,
    val type: ParticleType,
)
