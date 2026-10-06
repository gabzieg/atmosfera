package com.terra.wallpaper.service

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.PowerManager
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import androidx.core.content.ContextCompat
import com.terra.wallpaper.AtmosferaApp
import com.terra.wallpaper.BuildConfig
import com.terra.wallpaper.billing.Plano
import com.terra.wallpaper.debug.DebugOverride
import com.terra.wallpaper.engine.ArteFundo
import com.terra.wallpaper.engine.Cena
import com.terra.wallpaper.engine.EffectEngine
import com.terra.wallpaper.engine.EstiloEfeito
import com.terra.wallpaper.engine.PersonalizacaoPref
import com.terra.wallpaper.engine.SceneState
import com.terra.wallpaper.weather.IntervaloClima
import com.terra.wallpaper.weather.ClimaNaHome
import com.terra.wallpaper.weather.FreioMet
import com.terra.wallpaper.weather.LocationHelper
import com.terra.wallpaper.weather.WeatherCache
import com.terra.wallpaper.weather.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * Live wallpaper Atmosfera: desenha o cenário reativo ao clima usando o
 * [EffectEngine]. Sem imagens pré-renderizadas — tudo é o motor em Canvas.
 */
class AtmosferaWallpaperService : WallpaperService() {

    // Engines podem se sobrepor durante troca/recriação do wallpaper.
    private val climaMutex = Mutex()

    override fun onCreateEngine(): Engine = AtmosferaEngine()

    inner class AtmosferaEngine : Engine() {

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        private val handler = Handler(Looper.getMainLooper())
        private val estado = SceneState()
        private val motor = EffectEngine(estado)
        private var visivel = false
        private val frameMs = 33L // ~30 fps (equilíbrio fluidez × bateria)
        private val cargaMutex = Mutex()   // serializa carregar (troca cena/estilo)

        private val app get() = application as AtmosferaApp
        private val power get() = getSystemService(PowerManager::class.java)
        private val keyguard get() = getSystemService(KeyguardManager::class.java)
        private fun podeConsultar(): Boolean = ClimaNaHome.podeAtualizar(
            visivel, power.isInteractive, keyguard.isKeyguardLocked, isPreview, app.appAberto)
        private val climaNaHome = ClimaNaHome(scope, ::podeConsultar, ::carregarClima) {
            IntervaloClima.atual(applicationContext) * 60_000L
        }
        private val aoMudarApp: () -> Unit = { climaNaHome.reavaliar() }
        private var ultimaElegibilidadeMs = 0L
        private val telaReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Intent.ACTION_SCREEN_OFF) climaNaHome.parar()
                else climaNaHome.reavaliar()
            }
        }

        private val frame = object : Runnable {
            override fun run() {
                // Cobre mudança de keyguard sem transição de visibilidade;
                // os broadcasts cancelam imediatamente nas transições de tela.
                val agora = SystemClock.elapsedRealtime()
                if (agora - ultimaElegibilidadeMs >= 1_000L) {
                    ultimaElegibilidadeMs = agora
                    climaNaHome.reavaliar()
                }
                desenhar()
                if (visivel) handler.postDelayed(this, frameMs)
            }
        }

        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)
            app.observadoresVisibilidade.add(aoMudarApp)
            ContextCompat.registerReceiver(applicationContext, telaReceiver,
                IntentFilter().apply {
                    addAction(Intent.ACTION_SCREEN_OFF)
                    addAction(Intent.ACTION_SCREEN_ON)
                    addAction(Intent.ACTION_USER_PRESENT)
                }, ContextCompat.RECEIVER_NOT_EXPORTED)
            aplicarPersonalizacao()
            scope.launch(Dispatchers.IO) {
                carregarComSelecao()
                withContext(Dispatchers.Main) {
                    aplicarClimaEmCache()
                    if (visivel) {
                        handler.removeCallbacks(frame)
                        handler.post(frame)
                    }
                }
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            visivel = visible
            climaNaHome.reavaliar()
            if (visible) {
                aplicarPersonalizacao()
                // recarrega assets se o usuário trocou cenário/arte/estilo,
                // DEPOIS repõe o frame (o loop estava parado enquanto invisível,
                // então não há corrida de bitmaps com o carregar).
                scope.launch(Dispatchers.IO) {
                    carregarComSelecao()
                    withContext(Dispatchers.Main) {
                        aplicarClimaEmCache()
                        if (visivel) {
                            estado.hora = horaEfetiva()
                            handler.removeCallbacks(frame)
                            handler.post(frame)
                        }
                    }
                }
            } else {
                handler.removeCallbacks(frame)
            }
        }

        /** Brilho e rolagem lateral (Ajustes → Personalização) — relidos toda
         * vez que o wallpaper fica visível, igual cenário/arte/estilo: mudar
         * no app e voltar pra home já é o "voltar a ficar visível". */
        private fun aplicarPersonalizacao() {
            motor.brilho = PersonalizacaoPref.brilho(applicationContext) / 100f
            motor.parallaxAtivo = PersonalizacaoPref.parallaxAtivo(applicationContext)
        }

        /** Rolagem horizontal entre páginas da home — só usada quando o usuário
         * liga "Rolagem lateral"; ver [com.terra.wallpaper.engine.EffectEngine.offsetX]. */
        override fun onOffsetsChanged(
            xOffset: Float, yOffset: Float,
            xOffsetStep: Float, yOffsetStep: Float,
            xPixelOffset: Int, yPixelOffset: Int,
        ) {
            super.onOffsetsChanged(
                xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset,
            )
            motor.offsetX = xOffset
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
            app.observadoresVisibilidade.remove(aoMudarApp)
            applicationContext.unregisterReceiver(telaReceiver)
            climaNaHome.parar()
            handler.removeCallbacks(frame)
            scope.cancel()
            motor.liberar()
        }

        /**
         * Busca o clima e aplica ao estado do motor.
         *
         * Somente na home desbloqueada. Cancelamento alcança localização e
         * Retrofit; a elegibilidade é relida após cada suspensão e antes da rede.
         */
        private suspend fun carregarClima() = climaMutex.withLock {
            conferirUso()
            // Modo TESTE: força o clima escolhido no painel de debug.
            if (BuildConfig.DEBUG && DebugOverride.ativo(applicationContext)) {
                withContext(Dispatchers.Main) {
                    DebugOverride.aplicar(applicationContext, estado)
                    motor.aoMudarClima()
                }
                return@withLock
            }
            try {
                val cache = WeatherCache(applicationContext)
                aplicarClimaEmCache()
                val ultima = cache.cachedLocation()
                if (ultima != null && !cache.isStale(ultima.first, ultima.second,
                        IntervaloClima.ttlMs(applicationContext))) return@withLock
                if (FreioMet.doContexto(applicationContext).bloqueado()) return@withLock
                // Distribui consultas de várias instalações; espera cancelável,
                // somente durante uso, sem worker ou espera fora da home.
                delay(Random.nextLong(0L, 15_000L))
                conferirUso()
                val loc = LocationHelper(applicationContext)
                val repo = WeatherRepository(applicationContext)
                val onde = loc.getLocalizacao()
                conferirUso()
                val lat = onde.lat
                val lon = onde.lon
                val ttl = IntervaloClima.ttlMs(applicationContext)
                if (cache.isStale(lat, lon, ttl)) {
                    val novo = repo.fetchWeather(lat, lon).getOrNull()
                    conferirUso()
                    if (novo != null) cache.save(novo, lat, lon, localPadrao = onde.padrao)
                }
                aplicarClimaEmCache()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                // Sem clima e sem cache: o motor desenha o cenário base mesmo assim.
            }
        }

        private suspend fun conferirUso() {
            currentCoroutineContext().ensureActive()
            if (!podeConsultar()) throw CancellationException("Wallpaper fora da home ativa")
        }

        private fun aplicarClimaEmCache() {
            val salvo = WeatherCache(applicationContext).get() ?: return
            SceneState.aplicarClima(estado, salvo, Plano.isPremium(applicationContext))
            motor.aoMudarClima()
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
