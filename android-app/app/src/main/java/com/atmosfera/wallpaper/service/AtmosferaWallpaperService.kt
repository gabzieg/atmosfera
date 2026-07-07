package com.atmosfera.wallpaper.service

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.atmosfera.wallpaper.billing.Plano
import com.atmosfera.wallpaper.engine.EffectEngine
import com.atmosfera.wallpaper.engine.SceneState
import com.atmosfera.wallpaper.weather.LocationHelper
import com.atmosfera.wallpaper.weather.WeatherCache
import com.atmosfera.wallpaper.weather.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Live wallpaper Atmosfera: desenha o cenário reativo ao clima usando o
 * [EffectEngine]. Sem imagens pré-renderizadas — tudo é o motor em Canvas.
 */
class AtmosferaWallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = AtmosferaEngine()

    inner class AtmosferaEngine : Engine() {

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        private val handler = Handler(Looper.getMainLooper())
        private val estado = SceneState()
        private val motor = EffectEngine(estado)
        private var visivel = false
        private val frameMs = 33L // ~30 fps (equilíbrio fluidez × bateria)

        private val frame = object : Runnable {
            override fun run() {
                desenhar()
                if (visivel) handler.postDelayed(this, frameMs)
            }
        }

        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)
            scope.launch(Dispatchers.IO) {
                motor.carregar(assets)
                carregarClima()
                withContext(Dispatchers.Main) { if (visivel) handler.post(frame) }
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            visivel = visible
            if (visible) {
                estado.hora = SceneState.horaAtual()
                scope.launch(Dispatchers.IO) { carregarClima() }
                if (motor.pronto) handler.post(frame)
            } else {
                handler.removeCallbacks(frame)
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            handler.removeCallbacks(frame)
            scope.cancel()
            motor.liberar()
        }

        /** Busca o clima (cache 30 min) e aplica ao estado do motor. */
        private suspend fun carregarClima() {
            try {
                val cache = WeatherCache(applicationContext)
                val loc = LocationHelper(applicationContext)
                val repo = WeatherRepository()
                val (lat, lon) = loc.getLocation()
                val state = if (!cache.isStale(lat, lon)) cache.get()
                else repo.fetchWeather(lat, lon).getOrNull()?.also { cache.save(it, lat, lon) } ?: cache.get()

                state ?: return
                val premium = Plano.isPremium(applicationContext)
                withContext(Dispatchers.Main) {
                    SceneState.aplicarClima(estado, state, premium)
                    motor.aoMudarClima()
                }
            } catch (_: Exception) {
                // Sem clima e sem cache: o motor desenha o cenário base mesmo assim.
            }
        }

        private fun desenhar() {
            if (!motor.pronto) return
            estado.hora = SceneState.horaAtual()
            val holder = surfaceHolder
            var canvas: Canvas? = null
            try {
                canvas = holder.lockHardwareCanvas()
                if (canvas != null) {
                    motor.draw(canvas, canvas.width.toFloat(), canvas.height.toFloat(), SystemClock.uptimeMillis())
                }
            } finally {
                if (canvas != null) {
                    try { holder.unlockCanvasAndPost(canvas) } catch (_: Exception) {}
                }
            }
        }
    }
}
