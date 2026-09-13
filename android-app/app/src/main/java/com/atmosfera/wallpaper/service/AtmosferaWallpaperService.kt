package com.atmosfera.wallpaper.service

import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import com.atmosfera.wallpaper.BuildConfig
import com.atmosfera.wallpaper.billing.Plano
import com.atmosfera.wallpaper.debug.DebugOverride
import com.atmosfera.wallpaper.engine.ArteFundo
import com.atmosfera.wallpaper.engine.Cena
import com.atmosfera.wallpaper.engine.EffectEngine
import com.atmosfera.wallpaper.engine.EstiloEfeito
import com.atmosfera.wallpaper.engine.SceneState
import com.atmosfera.wallpaper.weather.IntervaloClima
import com.atmosfera.wallpaper.weather.LocationHelper
import com.atmosfera.wallpaper.weather.WeatherCache
import com.atmosfera.wallpaper.weather.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
        private val cargaMutex = Mutex()   // serializa carregar (troca cena/estilo)

        // REVALIDAR ENQUANTO VISÍVEL. O clima só era relido ao voltar pra tela
        // inicial; quem fica com a home aberta via o tempo congelar no que foi
        // lido quando a tela acendeu. A cada 10 min o serviço chama de novo o
        // `carregarClima`, que decide sozinho entre o cache e a rede pelo TTL
        // do intervalo escolhido — ou seja, isto não gera requisição extra
        // nenhuma, só deixa de esperar o usuário sair e voltar. Também cobre o
        // caso do WeatherWorker apanhar do Doze e não rodar no horário.
        private val revalidaClimaMs = 10 * 60 * 1000L
        private var ultimoClimaMs = 0L
        private val climaMutex = Mutex()

        private val frame = object : Runnable {
            override fun run() {
                desenhar()
                talvezRevalidarClima()
                if (visivel) handler.postDelayed(this, frameMs)
            }
        }

        /** Dispara a revalidação do clima quando vence o passo de 10 min. */
        private fun talvezRevalidarClima() {
            if (!visivel) return
            val agora = SystemClock.elapsedRealtime()
            if (agora - ultimoClimaMs < revalidaClimaMs) return
            ultimoClimaMs = agora
            scope.launch(Dispatchers.IO) { carregarClima() }
        }

        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)
            scope.launch(Dispatchers.IO) {
                carregarComSelecao()
                carregarClima()
                withContext(Dispatchers.Main) { if (visivel) handler.post(frame) }
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            visivel = visible
            if (visible) {
                // recarrega assets se o usuário trocou cenário/arte/estilo,
                // DEPOIS repõe o frame (o loop estava parado enquanto invisível,
                // então não há corrida de bitmaps com o carregar).
                scope.launch(Dispatchers.IO) {
                    carregarComSelecao()
                    carregarClima()
                    withContext(Dispatchers.Main) {
                        if (visivel) { estado.hora = horaEfetiva(); handler.post(frame) }
                    }
                }
            } else {
                handler.removeCallbacks(frame)
            }
        }

        /** Recarrega os assets se a seleção (cenário/arte/estilo) mudou. */
        private suspend fun carregarComSelecao() = cargaMutex.withLock {
            val cena = Cena.atual(applicationContext)
            val arte = ArteFundo.atual(applicationContext)
            val estilo = EstiloEfeito.atual(applicationContext)
            if (!motor.pronto || cena != motor.cenaId || arte != motor.arteId || estilo != motor.estiloId) {
                try {
                    motor.carregar(applicationContext, cena, arte, estilo)
                } catch (_: Throwable) {
                    // falha de asset/memória: não derruba o app; tenta de novo
                    // na próxima visibilidade (o motor fica pronto=false até lá).
                }
            }
        }

        /** Hora do cenário: respeita o override de teste; senão o relógio real. */
        private fun horaEfetiva(): Float {
            val real = SceneState.horaAtual()
            return if (BuildConfig.DEBUG && DebugOverride.ativo(applicationContext))
                DebugOverride.horaEfetiva(applicationContext, real) else real
        }

        override fun onDestroy() {
            super.onDestroy()
            handler.removeCallbacks(frame)
            scope.cancel()
            motor.liberar()
        }

        /**
         * Busca o clima e aplica ao estado do motor.
         *
         * O cache decide se sai requisição: dentro do TTL (90% do intervalo
         * escolhido em Ajustes) ele devolve o que já tem; vencido, busca. O
         * mutex existe porque agora há dois disparos possíveis ao mesmo tempo —
         * o de ficar visível e o passo de 10 min.
         */
        private suspend fun carregarClima() = climaMutex.withLock {
            ultimoClimaMs = SystemClock.elapsedRealtime()
            // Modo TESTE: força o clima escolhido no painel de debug.
            if (BuildConfig.DEBUG && DebugOverride.ativo(applicationContext)) {
                withContext(Dispatchers.Main) {
                    DebugOverride.aplicar(applicationContext, estado)
                    motor.aoMudarClima()
                }
                return
            }
            try {
                val cache = WeatherCache(applicationContext)
                val loc = LocationHelper(applicationContext)
                val repo = WeatherRepository()
                val onde = loc.getLocalizacao()
                val lat = onde.lat
                val lon = onde.lon
                val ttl = IntervaloClima.ttlMs(applicationContext)
                val state = if (!cache.isStale(lat, lon, ttl)) cache.get()
                else repo.fetchWeather(lat, lon).getOrNull()
                    ?.also { cache.save(it, lat, lon, localPadrao = onde.padrao) }
                    ?: cache.get()

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
            estado.hora = horaEfetiva()
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
