package com.terra.wallpaper.weather

import android.content.Context
import androidx.core.content.edit
import kotlin.math.min

/**
 * Freio de tráfego da MET Norway.
 *
 * Os termos de uso (https://api.met.no/doc/TermsOfService) mandam o app parar
 * assim que a API devolve 429 ("Always check response Status headers and limit
 * traffic immediately if this should happen") e avisam que quem for bloqueado
 * recebe 403. Sem isto, qualquer erro virava "falha" e a próxima abertura do
 * app ou do wallpaper batia na API de novo — exatamente o comportamento que
 * leva ao bloqueio.
 *
 * O estado vale para o APARELHO inteiro, não para uma instância: engines do
 * wallpaper respeitam o mesmo prazo persistido, até depois de o processo ser
 * morto. Companion e worker legado não fazem consultas.
 *
 * Espera: 30 min na primeira falha, dobrando a cada falha seguida, no máximo
 * 6 h. Um sucesso zera. Se a resposta trouxer `Retry-After`, vale o maior
 * entre ele e a espera calculada (a MET não documenta o cabeçalho, mas é
 * inofensivo respeitar).
 */
class FreioMet(
    private val armazem: Armazem,
    private val agora: () -> Long = System::currentTimeMillis,
) {
    /** Onde o estado fica entre execuções. `SharedPreferences` em produção. */
    interface Armazem {
        var ateMs: Long
        var falhasSeguidas: Int
    }

    /** Instante (epoch ms) até o qual não se deve consultar; 0 se livre. */
    fun bloqueadoAteMs(): Long = if (agora() < armazem.ateMs) armazem.ateMs else 0L

    fun bloqueado(): Boolean = bloqueadoAteMs() != 0L

    /** A MET respondeu 429 ou 403: para de consultar por um tempo. */
    fun aoSerBarrado(retryAfterSeg: Long? = null) {
        val falhas = armazem.falhasSeguidas + 1
        armazem.falhasSeguidas = falhas
        armazem.ateMs = agora() + esperaMs(falhas, retryAfterSeg)
    }

    /** Uma consulta deu certo: esquece o histórico de bloqueios. */
    fun aoSucesso() {
        if (armazem.falhasSeguidas != 0 || armazem.ateMs != 0L) {
            armazem.falhasSeguidas = 0
            armazem.ateMs = 0L
        }
    }

    companion object {
        const val BASE_MS = 30 * 60_000L
        const val TETO_MS = 6 * 60 * 60_000L

        /** Espera depois da n-ésima falha seguida (n ≥ 1). */
        fun esperaMs(falhasSeguidas: Int, retryAfterSeg: Long?): Long {
            val expoente = (falhasSeguidas - 1).coerceIn(0, 10)
            val calculada = min(BASE_MS shl expoente, TETO_MS)
            val pedida = (retryAfterSeg ?: 0L).coerceAtLeast(0L) * 1000L
            return min(maxOf(calculada, pedida), TETO_MS)
        }

        /** Armazém em `SharedPreferences` — o estado sobrevive ao processo. */
        fun doContexto(context: Context): FreioMet {
            val prefs = context.applicationContext
                .getSharedPreferences("atmosfera_met_freio", Context.MODE_PRIVATE)
            return FreioMet(object : Armazem {
                override var ateMs: Long
                    get() = prefs.getLong("ate_ms", 0L)
                    set(v) = prefs.edit { putLong("ate_ms", v) }
                override var falhasSeguidas: Int
                    get() = prefs.getInt("falhas", 0)
                    set(v) = prefs.edit { putInt("falhas", v) }
            })
        }
    }
}

/** A MET mandou reduzir o tráfego; nenhuma requisição foi feita. */
class MetBloqueadaException(val ateMs: Long) :
    Exception("MET Norway pediu para reduzir o tráfego (429/403); sem consultas até $ateMs")
