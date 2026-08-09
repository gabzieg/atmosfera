package com.atmosfera.wallpaper.ui.components

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.atmosfera.wallpaper.engine.EffectEngine
import com.atmosfera.wallpaper.engine.SceneState

/**
 * Prévia do motor rodando AO VIVO (não é screenshot nem simulação) — reaproveita
 * o mesmo [EffectEngine] que desenha o wallpaper de verdade. Usada na tela de
 * detalhe da Loja pra reduzir a incerteza de compra: o usuário vê o cenário se
 * mover antes de aplicar, não só uma imagem estática.
 *
 * Desenha nada (fica transparente) até [EffectEngine.pronto] — cabe ao caller
 * empilhar isto sobre um [SceneThumbnail] estático como fallback honesto
 * enquanto carrega ou se o cenário não tiver assets ainda.
 */
private class LivePreviewView(context: Context) : View(context) {
    val estado = SceneState()
    val motor = EffectEngine(estado)
    private var carregadoPara: Triple<String, String, String>? = null

    private val handler = Handler(Looper.getMainLooper())
    private var rodando = false
    private val tick = object : Runnable {
        override fun run() {
            invalidate()
            if (rodando) handler.postDelayed(this, 33L)
        }
    }

    /**
     * Liga/desliga os efeitos pagos nesta prévia. O motor lê `estado.premium` em
     * cinco pontos (acúmulo de neve, vagalumes, estrela cadente, lampiões e
     * fumaça de chaminé), então isto muda o que é desenhado sem recarregar nada.
     *
     * Serve pro comparador da tela de Premium mostrar os dois lados ao mesmo
     * tempo, e não é a mesma coisa que `Plano.isPremium()`: aqui é só a prévia,
     * o plano de verdade continua vindo do billing.
     */
    fun definirPremium(valor: Boolean) {
        estado.premium = valor
    }

    fun garantirCarregado(sceneId: String, arte: String, estilo: String) {
        val alvo = Triple(sceneId, arte, estilo)
        if (carregadoPara == alvo) return
        carregadoPara = alvo
        Thread {
            try {
                motor.carregar(context.assets, sceneId, arte, estilo)
            } catch (_: Throwable) {
                // Cenário sem asset plugado ainda: motor.pronto fica false,
                // onDraw não desenha nada — o SceneThumbnail por baixo mostra.
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
        estado.hora = SceneState.horaAtual()
        motor.draw(canvas, width.toFloat(), height.toFloat(), SystemClock.uptimeMillis())
    }
}

/**
 * @param premium liga os efeitos pagos NESTA prévia. Só afeta o desenho local —
 *   não confere nem altera o plano real (isso é do `BillingManager`/`Plano`).
 *   O padrão segue o plano do aparelho, que é o comportamento das telas comuns.
 */
@Composable
fun EngineLivePreview(
    sceneId: String,
    arte: String,
    estilo: String,
    modifier: Modifier = Modifier,
    premium: Boolean? = null,
) {
    AndroidView(
        modifier = modifier,
        factory = { context -> LivePreviewView(context) },
        update = { view ->
            premium?.let { view.definirPremium(it) }
            view.garantirCarregado(sceneId, arte, estilo)
        },
    )
}
