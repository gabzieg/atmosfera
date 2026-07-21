package com.atmosfera.wallpaper.debug

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.atmosfera.wallpaper.billing.Plano
import com.atmosfera.wallpaper.engine.ArteFundo
import com.atmosfera.wallpaper.engine.Catalogo
import com.atmosfera.wallpaper.engine.Cena
import com.atmosfera.wallpaper.engine.EstiloEfeito
import com.atmosfera.wallpaper.engine.Estilos
import com.atmosfera.wallpaper.weather.WeatherCondition

/**
 * Painel de TESTE (só em builds debug): força clima/hora/vento/névoa e mostra
 * uma prévia ao vivo do motor. O que estiver aqui também dirige o live wallpaper
 * na tela inicial quando "Forçar clima" estiver ligado.
 */
class DebugActivity : AppCompatActivity() {

    private lateinit var preview: EnginePreviewView

    private val condicoes = listOf(
        "☀️ Ensolarado" to WeatherCondition.SUNNY,
        "⛅ Parcialmente nublado" to WeatherCondition.PARTLY_CLOUDY,
        "☁️ Nublado" to WeatherCondition.CLOUDY,
        "🌫 Névoa" to WeatherCondition.FOGGY,
        "🌦 Chuva fraca" to WeatherCondition.LIGHT_RAIN,
        "🌧 Chuva forte" to WeatherCondition.HEAVY_RAIN,
        "⛈ Temporal" to WeatherCondition.STORM,
        "❄️ Neve" to WeatherCondition.SNOW,
        "🌙 Noite limpa" to WeatherCondition.CLEAR_NIGHT,
    )

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Atmosfera — Teste"

        val raiz = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#0E1116"))
        }

        preview = EnginePreviewView(this)
        raiz.addView(preview, LinearLayout.LayoutParams(MATCH_PARENT, 0, 3f))

        val scroll = ScrollView(this)
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(24))
        }
        scroll.addView(col)
        raiz.addView(scroll, LinearLayout.LayoutParams(MATCH_PARENT, 0, 2f))
        setContentView(raiz)

        montarControles(col)
    }

    private fun montarControles(col: LinearLayout) {
        // ── Seletores de CENÁRIO / ARTE DO FUNDO / ESTILO DOS EFEITOS ──
        // (gravam a preferência lida pelo wallpaper de verdade + recarregam a prévia)
        val cenarios = Catalogo.cenarios.map { it.id to it.nome }
        col.addView(rotulo("Cenário (wallpaper)"))
        col.addView(dropdown(cenarios, Cena.atual(this)) { id ->
            Cena.definir(this, id); preview.trocarCenaEstilo()
        })
        val artes = listOf("pixel" to "🟦 Pixel Art", "clay" to "🧱 Clay", "aqua" to "🎨 Aquarela")
        col.addView(rotulo("↳ Arte do cenário"))
        col.addView(dropdown(artes, ArteFundo.atual(this)) { id ->
            ArteFundo.definir(this, id); preview.trocarCenaEstilo()
        })
        val estilos = listOf(
            "pixel" to "🟦 Pixel Art", "clay" to "🧱 Clay",
            "bizantino" to "🏛️ Bizantino", "aqua" to "🎨 Aquarela"
        ).filter { it.first in Estilos.ids }
        col.addView(rotulo("Estilo dos efeitos"))
        col.addView(dropdown(estilos, EstiloEfeito.atual(this)) { id ->
            EstiloEfeito.definir(this, id); preview.trocarCenaEstilo()
        })
        col.addView(rotulo("——"))

        // Condição
        col.addView(rotulo("Condição"))
        val spinner = Spinner(this)
        spinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, condicoes.map { it.first }
        )
        spinner.setSelection(condicoes.indexOfFirst { it.second == DebugOverride.condicao(this) }.coerceAtLeast(0))
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                DebugOverride.setCondicao(this@DebugActivity, condicoes[pos].second)
                preview.recarregar()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
        col.addView(spinner)

        // Nível de neve 1..3 (só afeta a condição ❄️)
        col.addView(sliderInt(
            "Nível de neve (só ❄️)", 1, 3, DebugOverride.nivelNeve(this),
            persist = { DebugOverride.setNivelNeve(this, it) }, fmt = { "Nível $it" }
        ))

        // Temperatura
        col.addView(sliderFloat(
            "Temperatura", -15f, 35f, DebugOverride.temp(this),
            persist = { DebugOverride.setTemp(this, it) }, fmt = { "${it.toInt()}°C" }
        ))

        // Vento
        col.addView(sliderFloat(
            "Vento", 0f, 60f, DebugOverride.vento(this),
            persist = { DebugOverride.setVento(this, it) }, fmt = { "${it.toInt()} km/h" }
        ))

        // Hora (com switch "usar hora real")
        val horaAtual = DebugOverride.hora(this)
        val horaSlider = sliderFloat(
            "Hora do dia", 0f, 24f, if (horaAtual < 0f) 12f else horaAtual,
            persist = { DebugOverride.setHora(this, it) },
            fmt = { h -> val hh = h.toInt(); val mm = ((h - hh) * 60).toInt(); String.format("%02d:%02d", hh, mm) }
        )
        horaSlider.isEnabled = horaAtual >= 0f
        col.addView(switch("Usar hora real (relógio)", horaAtual < 0f) { on ->
            DebugOverride.setHora(this, if (on) -1f else 12f)
            horaSlider.isEnabled = !on
            preview.recarregar()
        })
        col.addView(horaSlider)

        // Névoa (com switch manual)
        val nevoaAtual = DebugOverride.nevoa(this)
        val nevoaSlider = sliderFloat(
            "Névoa", 0f, 1f, if (nevoaAtual < 0f) 0.6f else nevoaAtual,
            persist = { DebugOverride.setNevoa(this, it) }, fmt = { "${(it * 100).toInt()}%" }
        )
        nevoaSlider.isEnabled = nevoaAtual >= 0f
        col.addView(switch("Névoa manual (senão segue a condição)", nevoaAtual >= 0f) { on ->
            DebugOverride.setNevoa(this, if (on) 0.6f else -1f)
            nevoaSlider.isEnabled = on
            preview.recarregar()
        })
        col.addView(nevoaSlider)

        // Premium
        col.addView(switch("Premium (efeitos vivos)", Plano.isPremium(this)) { on ->
            Plano.setPremium(this, on); preview.recarregar()
        })

        // Master: forçar clima no wallpaper real
        col.addView(rotulo("——"))
        col.addView(switch("Forçar este clima no wallpaper", DebugOverride.ativo(this)) { on ->
            DebugOverride.setAtivo(this, on)
            Toast.makeText(
                this,
                if (on) "Ligado. Volte à tela inicial para ver no wallpaper."
                else "Desligado. O wallpaper volta ao clima real.",
                Toast.LENGTH_SHORT
            ).show()
        })
        col.addView(TextView(this).apply {
            text = "A prévia acima sempre mostra a configuração escolhida. " +
                "O switch acima decide se o wallpaper de verdade também usa ela."
            setTextColor(Color.parseColor("#8A94A6")); textSize = 12f
            setPadding(0, dp(6), 0, 0)
        })

        col.addView(Button(this).apply {
            text = "Fechar"
            setOnClickListener { finish() }
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply { topMargin = dp(16) }
        })
    }

    // ── Helpers de UI ────────────────────────────────────────────────
    /** Spinner de (id, rótulo); [onSel] só dispara em troca real do usuário. */
    private fun dropdown(itens: List<Pair<String, String>>, atual: String, onSel: (String) -> Unit): Spinner {
        val sp = Spinner(this)
        sp.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, itens.map { it.second })
        sp.setSelection(itens.indexOfFirst { it.first == atual }.coerceAtLeast(0))
        var primeiro = true
        sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                if (primeiro) { primeiro = false; return }   // ignora o callback do init
                onSel(itens[pos].first)
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
        return sp
    }

    private fun rotulo(txt: String) = TextView(this).apply {
        text = txt
        setTextColor(Color.parseColor("#C7D0DE")); textSize = 13f
        setPadding(0, dp(14), 0, dp(2))
    }

    private fun switch(txt: String, ligado: Boolean, onChange: (Boolean) -> Unit) = Switch(this).apply {
        text = txt; isChecked = ligado
        setTextColor(Color.parseColor("#E4E9F1")); textSize = 14f
        setPadding(0, dp(10), 0, dp(4))
        setOnCheckedChangeListener { _, v -> onChange(v) }
    }

    /**
     * Slider float. [persist] grava o valor (só em interação do usuário);
     * [fmt] só formata o texto exibido — sem efeitos colaterais.
     * Retorna uma View cujo setEnabled também habilita/desabilita a barra.
     */
    private fun sliderFloat(
        label: String, min: Float, max: Float, valor: Float,
        persist: (Float) -> Unit, fmt: (Float) -> String,
    ): View {
        val tv = TextView(this).apply { setTextColor(Color.parseColor("#C7D0DE")); textSize = 13f }
        val bar = SeekBar(this).apply { this.max = 1000 }
        fun valDe(prog: Int): Float = min + (max - min) * prog / 1000f
        tv.text = "$label:  ${fmt(valor)}"
        bar.progress = (((valor - min) / (max - min)) * 1000f).toInt().coerceIn(0, 1000)
        bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, p: Int, fromUser: Boolean) {
                val v = valDe(p); tv.text = "$label:  ${fmt(v)}"
                if (fromUser) { persist(v); preview.recarregar() }
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
        return caixaSlider(tv, bar)
    }

    private fun sliderInt(
        label: String, min: Int, max: Int, valor: Int,
        persist: (Int) -> Unit, fmt: (Int) -> String,
    ): View {
        val tv = TextView(this).apply { setTextColor(Color.parseColor("#C7D0DE")); textSize = 13f }
        val bar = SeekBar(this).apply { this.max = max - min }
        tv.text = "$label:  ${fmt(valor)}"
        bar.progress = (valor - min).coerceIn(0, max - min)
        bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, p: Int, fromUser: Boolean) {
                val v = min + p; tv.text = "$label:  ${fmt(v)}"
                if (fromUser) { persist(v); preview.recarregar() }
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
        return caixaSlider(tv, bar)
    }

    /** Empacota rótulo + barra; setEnabled propaga para a barra e esmaece o rótulo. */
    private fun caixaSlider(tv: TextView, bar: SeekBar): View =
        object : LinearLayout(this) {
            init {
                orientation = LinearLayout.VERTICAL
                setPadding(0, dp(8), 0, 0)
                addView(tv); addView(bar)
            }
            override fun setEnabled(enabled: Boolean) {
                super.setEnabled(enabled)
                bar.isEnabled = enabled
                tv.alpha = if (enabled) 1f else 0.4f
            }
        }
}
