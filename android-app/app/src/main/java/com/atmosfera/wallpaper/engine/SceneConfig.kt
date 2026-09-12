package com.atmosfera.wallpaper.engine

import com.atmosfera.wallpaper.weather.DayPeriod
import com.atmosfera.wallpaper.weather.WeatherCondition
import com.atmosfera.wallpaper.weather.WeatherState
import java.util.Calendar

/** Tint de céu (rgba, a em 0..1). a=0 → sem tint. */
data class SkyTint(val r: Int, val g: Int, val b: Int, val a: Float)

/**
 * Estado que dirige o motor de efeitos — equivalente ao `cfg` do protótipo web.
 * É preenchido a partir do clima real ([aplicarClima]) e depois clampado pelo
 * plano do usuário ([aplicarPlano]).
 */
class SceneState {
    // Ambiente
    var hora = 12f                 // hora do dia (0..24), do relógio real
    var luaFase = 0.5f             // 0=nova, 0.5=cheia, 1=nova
    var temp = 12f                 // °C
    var vento = 0f                 // km/h
    var nevoa = 0f                 // névoa 0..1 (FOGGY da API)
    var nascer = Atlas.SolCfg.nascer
    var por = Atlas.SolCfg.por

    var clima = "chuva"            // "chuva" | "nublado" | "seco"
    var forcarNeve = false         // condição SNOW da API (neva mesmo se temp>1)

    // Precipitação (presets de intensidade)
    var dropCount = 120
    var speed = 600f
    var scaleMult = 2.5f
    var roofRate = 4f
    var lakeRate = 10f
    var frameMs = 120f
    var raios = false

    // Nuvens
    var cloudSet = "leves"
    var cloudMin = 1.2f
    var cloudMax = 1.7f
    var cloudN = 4

    // Tint do céu na chuva/nublado (pinta só o céu)
    var skyTint = SkyTint(0, 0, 0, 0f)

    // Plano
    var premium = false

    /**
     * Neve NO APP = a API disse que é neve (código WMO), não é chuva fria.
     * (forcarNeve vem da condição SNOW; assim uma chuva a 0°C segue chuva.)
     */
    fun nevando(): Boolean = clima == "chuva" && forcarNeve

    // ── Presets de chuva ────────────────────────────────────────────
    fun presetFraca() {
        dropCount = 60; speed = 480f; scaleMult = 2.5f; roofRate = 3f; lakeRate = 5f; frameMs = 120f
        cloudSet = "leves"; skyTint = SkyTint(150, 160, 176, 0.45f); raios = false
        cloudMin = 1.2f; cloudMax = 1.7f; cloudN = 4
    }
    fun presetForte() {
        dropCount = 120; speed = 600f; scaleMult = 2.5f; roofRate = 4f; lakeRate = 10f; frameMs = 120f
        cloudSet = "medias"; skyTint = SkyTint(120, 128, 140, 0.85f); raios = false
        cloudMin = 1.6f; cloudMax = 2.2f; cloudN = 5
    }
    fun presetTemporal() {
        dropCount = 180; speed = 700f; scaleMult = 3f; roofRate = 8f; lakeRate = 20f; frameMs = 120f
        cloudSet = "escuras"; skyTint = SkyTint(72, 78, 92, 0.92f); raios = true
        cloudMin = 2.6f; cloudMax = 3.6f; cloudN = 6
    }
    fun setSeco() {
        clima = "seco"; cloudSet = "leves"; skyTint = SkyTint(0, 0, 0, 0f)
        cloudMin = 0.9f; cloudMax = 1.3f; cloudN = 3; raios = false
    }
    /**
     * PARCIALMENTE NUBLADO (código WMO 2) — sol entre nuvens.
     *
     * Isto era `setSeco()`, e era a queixa mais fácil de reproduzir: o app de
     * clima dizia "parcialmente nublado" e o wallpaper mostrava céu limpo, sem
     * uma nuvem. Não é chuva nem encoberto: a chuva continua desligada e o céu
     * não ganha o véu cinza do nublado — o que muda é a QUANTIDADE de nuvem
     * (3 → 6) e um tint mínimo, só o bastante pra tirar o azul de dia perfeito.
     */
    fun setParcialNublado() {
        clima = "seco"; cloudSet = "leves"; skyTint = SkyTint(150, 160, 176, 0.15f)
        cloudMin = 1.4f; cloudMax = 2.0f; cloudN = 6; raios = false
    }
    fun setNublado() {
        clima = "nublado"; cloudSet = "medias"; skyTint = SkyTint(130, 137, 150, 0.90f)
        cloudMin = 2.6f; cloudMax = 3.6f; cloudN = 9; raios = false
    }

    companion object {
        /** Mapeia o clima real (Open-Meteo) para o estado do motor. */
        fun aplicarClima(s: SceneState, w: WeatherState, premium: Boolean) {
            s.premium = premium
            s.temp = w.temperatureCelsius.toFloat()
            s.vento = w.windspeedKmh.toFloat()
            s.hora = horaAtual()
            s.nascer = w.sunriseHour
            s.por = w.sunsetHour
            s.forcarNeve = w.condition == WeatherCondition.SNOW
            s.luaFase = if (premium) faseLua() else 0.5f
            s.nevoa = 0f

            when (w.condition) {
                WeatherCondition.SUNNY, WeatherCondition.CLEAR_NIGHT -> s.setSeco()
                WeatherCondition.PARTLY_CLOUDY -> s.setParcialNublado()
                WeatherCondition.CLOUDY -> s.setNublado()
                WeatherCondition.FOGGY -> { s.setSeco(); s.nevoa = 0.9f } // névoa densa sobre céu neutro
                WeatherCondition.LIGHT_RAIN -> { s.clima = "chuva"; s.presetFraca() }
                WeatherCondition.HEAVY_RAIN -> { s.clima = "chuva"; s.presetForte() }
                WeatherCondition.STORM -> { s.clima = "chuva"; s.presetTemporal() }
                WeatherCondition.SNOW -> {
                    s.clima = "chuva" // vira neve via nevando(); intensidade por código WMO
                    when (w.weatherCode) {
                        71, 77, 85 -> s.presetFraca()      // neve fraca / grãos / pancada leve
                        75, 86 -> s.presetTemporal()       // neve forte / pancada forte
                        else -> s.presetForte()            // 73 moderada e demais
                    }
                }
            }
            aplicarPlano(s)
        }

        /**
         * Gate do plano: no FREE, corta a "vida" (efeitos premium) mas mantém o
         * clima real e o aconchego (dia/noite, estrelas, janelas acesas).
         * A neve continua caindo no free (o motor apenas não acumula).
         */
        fun aplicarPlano(s: SceneState) {
            if (s.premium) return
            s.raios = false
            s.vento = 0f
            s.luaFase = 0.5f
        }

        /** Hora do relógio como fração (ex.: 14h30 → 14.5). */
        fun horaAtual(): Float {
            val c = Calendar.getInstance()
            return c.get(Calendar.HOUR_OF_DAY) + c.get(Calendar.MINUTE) / 60f
        }

        /**
         * Fase da lua por fórmula (sem API). 0=nova, 0.5=cheia, 1=nova.
         * Baseado no mês sinódico a partir de uma lua nova conhecida.
         */
        fun faseLua(nowMs: Long = System.currentTimeMillis()): Float {
            val sinodico = 29.53058867
            val luaNovaConhecida = 947182440000L // 2000-01-06 18:14 UTC
            val dias = (nowMs - luaNovaConhecida) / 86_400_000.0
            var frac = (dias % sinodico) / sinodico
            if (frac < 0) frac += 1.0
            return frac.toFloat()
        }
    }
}
