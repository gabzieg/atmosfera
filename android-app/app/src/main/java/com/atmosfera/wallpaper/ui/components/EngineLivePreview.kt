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

    fun garantirCarregado(sceneId: String, arte: String, estilo: String) {
        val alvo = Triple(sceneId, arte, estilo)
        if (carregadoPara == alvo) return
        carregadoPara = alvo
        Thread {
            try {
                motor.carregar(context, sceneId, arte, estilo)
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

@Composable
fun EngineLivePreview(sceneId: String, arte: String, estilo: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context -> LivePreviewView(context) },
        update = { view -> view.garantirCarregado(sceneId, arte, estilo) },
    )
}
