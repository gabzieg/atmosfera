package com.atmosfera.wallpaper.debug

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import com.atmosfera.wallpaper.engine.ArteFundo
import com.atmosfera.wallpaper.engine.Cena
import com.atmosfera.wallpaper.engine.EffectEngine
import com.atmosfera.wallpaper.engine.EstiloEfeito
import com.atmosfera.wallpaper.engine.SceneState

/**
 * Prévia ao vivo do motor dentro do app (só usada no painel de debug).
 * Roda seu próprio [EffectEngine] a ~30fps e reflete o [DebugOverride]
 * atual, para iterar rápido sem precisar voltar à tela inicial.
 */
class EnginePreviewView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : View(context, attrs) {

    private val estado = SceneState()
    private val motor = EffectEngine(estado)
    private val handler = Handler(Looper.getMainLooper())
    private var rodando = false

    private val tick = object : Runnable {
        override fun run() {
            invalidate()
            if (rodando) handler.postDelayed(this, 33L)
        }
    }

    init {
        Thread {
            motor.carregar(context.assets, Cena.atual(context), ArteFundo.atual(context), EstiloEfeito.atual(context))
            post { recarregar() }
        }.start()
    }

    /** Reaplica o override ao estado do motor (chamar quando um slider muda). */
    fun recarregar() {
        DebugOverride.aplicar(context, estado)
        if (motor.pronto) motor.aoMudarClima()
    }

    /** Recarrega o cenário/arte/estilo escolhidos (para o tick durante a troca). */
    fun trocarCenaEstilo() {
        rodando = false
        handler.removeCallbacks(tick)
        Thread {
            motor.carregar(context.assets, Cena.atual(context), ArteFundo.atual(context), EstiloEfeito.atual(context))
            post {
                recarregar()
                if (isAttachedToWindow) { rodando = true; handler.post(tick) }
            }
        }.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        rodando = true
        handler.post(tick)
    }

    override fun onDetachedFromWindow() {
        rodando = false
        handler.removeCallbacks(tick)
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        if (!motor.pronto) return
        estado.hora = DebugOverride.horaEfetiva(context, SceneState.horaAtual())
        motor.draw(canvas, width.toFloat(), height.toFloat(), SystemClock.uptimeMillis())
    }
}
