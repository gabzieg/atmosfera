package com.atmosfera.wallpaper.weather

import android.content.Context
import androidx.preference.PreferenceManager

/**
 * Intervalo de atualização do clima escolhido pelo usuário (Ajustes → Cenário).
 * `object` sobre `SharedPreferences`, mesmo padrão de [com.atmosfera.wallpaper.billing.Plano]
 * e `Cena` — o projeto não tem injeção de dependência.
 *
 * Existe pra que a chave da preferência não fique repetida solta em
 * `MainViewModel`/`MainActivity`/`BootReceiver`, e pra derivar o TTL do cache
 * do valor escolhido: sem isso, escolher 15min não tinha efeito nenhum, porque
 * o [WeatherCache] considerava o dado fresco por 30min fixos e o worker pulava
 * a busca.
 */
object IntervaloClima {

    private const val KEY = "intervalo_clima_min"
    const val PADRAO_MIN = 30
    val OPCOES_MIN = listOf(15, 30, 60)

    fun atual(context: Context): Int =
        PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY, PADRAO_MIN)

    fun definir(context: Context, minutos: Int) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putInt(KEY, minutos).apply()
    }

    /**
     * Por quanto tempo o clima em cache conta como fresco.
     *
     * 90% do intervalo, de propósito: se o TTL fosse igual ao período do worker,
     * a checagem cairia exatamente na fronteira e um atraso de milissegundos
     * faria `isStale` devolver false, pulando a busca daquele ciclo. A margem
     * garante que o dado já esteja vencido quando o worker acorda. (O
     * WorkManager pode disparar depois do período, nunca antes.)
     */
    fun ttlMs(context: Context): Long = ttlMs(atual(context))

    fun ttlMs(minutos: Int): Long = minutos * 60_000L * 9 / 10
}
