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
import com.atmosfera.wallpaper.comoFonte
import com.atmosfera.wallpaper.engine.EffectEngine
import com.atmosfera.wallpaper.engine.SceneState

/**
 * Intervalo entre quadros da prévia — ~20 fps, contra os ~30 fps do wallpaper
 * de verdade em [com.atmosfera.wallpaper.service.AtmosferaWallpaperService].
 *
 * Não é chute: medido com `dumpsys gfxinfo` num Pixel 8 (emulador), a tela
 * Início com uma prévia a 30 fps dava 86% de quadros com jank e mediana de
 * 26 ms — a thread de UI ficava ocupada ~79% do tempo só desenhando cena, e
 * era isso que deixava a rolagem e os toques pastosos. A tela de Ajustes, sem
 * prévia, desenha ZERO quadro no mesmo intervalo.
 *
 * O motor calcula o passo a partir do timestamp que recebe, então mudar o
 * intervalo muda a taxa de quadros, não a velocidade da animação.
 */
private const val INTERVALO_MS = 50L

/**
 * Já foi tentado desenhar a cena num bitmap reduzido e ampliar (pra tocar menos
 * pixels por quadro). **Piorou**: a mediana subiu de 25 ms pra 38 ms, porque
 * `Canvas(Bitmap)` é rasterização por software — a CPU perde mais do que a
 * economia de área devolve. O motor precisa do canvas acelerado da View, igual
 * ao `lockHardwareCanvas()` que o serviço do wallpaper usa. Não repetir.
 */

/**
 * Prévia do motor rodando AO VIVO (não é screenshot nem simulação) — reaproveita
 * o mesmo [EffectEngine] que desenha o wallpaper de verdade. Usada na tela de
 * detalhe da Loja pra reduzir a incerteza de compra: o usuário vê o cenário se
 * mover antes de aplicar, não só uma imagem estática.
 *
 * Desenha nada (fica transparente) até [EffectEngine.pronto] — cabe ao caller
 * empilhar isto sobre um [SceneThumbnail] estático como fallback honesto
 * enquanto carrega ou se o cenário não tiver assets ainda.
 *
 * **Cada instância carrega o conjunto inteiro de bitmaps do cenário** (fundo,
 * frente, sprites, neve, névoa…) — dezenas de MB com as artes do snapshot de
 * 2026-08. Como o app chega a ter duas prévias vivas ao mesmo tempo (o
 * comparador da tela de Premium) e várias ao longo de uma navegação, a devolução
 * dessa memória é obrigatória, não otimização: ver `descartar()`.
 */
private class LivePreviewView(context: Context) : View(context) {
    val estado = SceneState()
    val motor = EffectEngine(estado)

    /** O que o Compose pediu pra mostrar. */
    private var alvo: Triple<String, String, String>? = null
    /** O que está de fato decodificado no motor agora. */
    private var carregado: Triple<String, String, String>? = null
    private var carregando = false
    /** Composable já saiu de cena: a memória tem que voltar assim que der. */
    private var descartado = false

    private val handler = Handler(Looper.getMainLooper())
    private var rodando = false
    private val tick = object : Runnable {
        override fun run() {
            invalidate()
            if (rodando) handler.postDelayed(this, INTERVALO_MS)
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
        alvo = Triple(sceneId, arte, estilo)
        dispararCarga()
    }

    /**
     * Decodifica fora da thread de UI. Só uma carga por vez: se o alvo mudar no
     * meio (usuário varrendo as variantes de arte na tela de detalhe), a troca é
     * aplicada quando a atual terminar, em vez de disparar uma thread por toque.
     */
    private fun dispararCarga() {
        val alvoAtual = alvo ?: return
        if (carregando || descartado || carregado == alvoAtual) return
        carregando = true
        Thread {
            val ok = try {
                motor.carregar(context.assets.comoFonte(), alvoAtual.first, alvoAtual.second, alvoAtual.third)
                true
            } catch (_: Throwable) {
                // Cenário sem asset plugado ainda: motor.pronto fica false,
                // onDraw não desenha nada — o SceneThumbnail por baixo mostra.
                false
            }
            // handler, não View.post: uma View destacada engaveta o post até
            // reanexar, e aqui justamente o caso a tratar é o que nunca reanexa.
            handler.post {
                carregando = false
                carregado = if (ok) alvoAtual else null
                if (descartado) liberarMotor() else dispararCarga()
            }
        }.start()
    }

    /**
     * Devolve os bitmaps. Chamado quando o composable sai da composição — não no
     * `onDetachedFromWindow`, que também dispara em troca de aba e faria a prévia
     * recarregar dezenas de MB toda vez que o usuário fosse e voltasse.
     */
    fun descartar() {
        descartado = true
        pararLoop()
        // Se há decodificação em curso, `liberar()` ficaria bloqueado no mesmo
        // lock esperando ela acabar — na thread de UI. Quem libera, nesse caso,
        // é o fim da própria carga.
        if (!carregando) liberarMotor()
    }

    private fun liberarMotor() {
        motor.liberar()
        carregado = null
    }

    private fun pararLoop() {
        rodando = false
        handler.removeCallbacks(tick)
    }

    private fun ajustarLoop() {
        val deveRodar = !descartado && isAttachedToWindow && windowVisibility == VISIBLE
        if (deveRodar == rodando) return
        if (deveRodar) {
            rodando = true
            handler.post(tick)
        } else {
            pararLoop()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        ajustarLoop()
    }

    override fun onDetachedFromWindow() {
        pararLoop()
        super.onDetachedFromWindow()
    }

    /** App foi pro segundo plano: parar de invalidar 30×/s enquanto ninguém vê. */
    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        ajustarLoop()
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
        onRelease = { view -> view.descartar() },
    )
}
