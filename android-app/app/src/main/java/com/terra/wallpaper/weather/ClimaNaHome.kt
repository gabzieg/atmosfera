package com.terra.wallpaper.weather

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Uma sessão cancelável por engine. Chamadas de controle ocorrem na main thread. */
internal class ClimaNaHome(
    private val scope: CoroutineScope,
    private val elegivel: () -> Boolean,
    private val atualizar: suspend () -> Unit,
    private val intervaloMs: () -> Long,
) {
    private var tarefa: Job? = null

    fun reavaliar() {
        if (!elegivel()) {
            parar()
            return
        }
        if (tarefa?.isActive == true) return
        val anterior = tarefa
        tarefa = scope.launch {
            // Não sobrepor a consulta cancelada com a nova sessão.
            anterior?.join()
            while (isActive && elegivel()) {
                atualizar()
                delay(intervaloMs().coerceAtLeast(1L))
            }
        }
    }

    fun parar() {
        tarefa?.cancel()
    }

    companion object {
        fun podeAtualizar(visivel: Boolean, interativa: Boolean, bloqueado: Boolean,
                         previa: Boolean, appAberto: Boolean): Boolean =
            visivel && interativa && !bloqueado && !previa && !appAberto
    }
}
