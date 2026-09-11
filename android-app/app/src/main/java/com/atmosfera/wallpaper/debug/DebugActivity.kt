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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.atmosfera.wallpaper.BuildConfig
import com.atmosfera.wallpaper.billing.Plano
import com.atmosfera.wallpaper.engine.Acervo
import com.atmosfera.wallpaper.engine.ArteFundo
import com.atmosfera.wallpaper.engine.Catalogo
import com.atmosfera.wallpaper.engine.Cena
import com.atmosfera.wallpaper.engine.EstiloEfeito
import com.atmosfera.wallpaper.engine.Estilos
import com.atmosfera.wallpaper.weather.WeatherCondition
import kotlinx.coroutines.launch

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
        if (!BuildConfig.DEBUG) { finish(); return } // defesa extra além do exported=false
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

        // Com targetSdk 35+ o edge-to-edge é imposto pelo sistema: sem tratar os
        // insets, a prévia entra por baixo da barra de status e o botão "Fechar"
        // some atrás da barra de navegação. As telas Compose já estão cobertas
        // pelo Scaffold; esta aqui é View crua, então precisa aplicar na mão.
        ViewCompat.setOnApplyWindowInsetsListener(raiz) { v, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }

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
        // "pixel" = arte BASE da cena (na cabana é pixel art, no tanque é o
        // diorama clay). As demais só existem em algumas cenas — escolher uma
        // que a cena não tem cai no fundo base (fundoPrefixo faz o fallback).
        val artes = listOf(
            "pixel" to "🟦 Arte base",
            "aqua" to "🎨 Aquarela",
            "clay" to "🧱 Clay",
            "doodle" to "✏️ Doodle",
            "needle" to "🧶 Needle Felting",
            "pixelart" to "🟦 Pixel Art",
            "pixel2" to "🟦 Pixel Art 2",
            "doodleinf" to "🖍️ Doodle Infantil",
            "point" to "🖌️ Pontilhismo",
            "point2" to "🖌️ Pontilhismo 2",
            "vangogh" to "🌌 Van Gogh",
            "doodle2" to "✏️ Doodle 2", "papel" to "📰 Papier-mâché",
            "noite" to "🌙 Pixel Noite", "chibi" to "🎎 Anime Chibi",
            "kodomo" to "🎈 Anime Kodomo", "seinen" to "🗡️ Anime Seinen",
            "impress" to "🖼️ Impressionismo", "cozy" to "🛋️ Cozy Fantasy",
            "lowpoly" to "🔷 Low Poly", "vivid" to "🌈 Vivid",
            "cartoon" to "💫 Cartoon", "cutout" to "✂️ Paper Cutout",
            "cera" to "🕯️ Cera", "sfumato" to "🌫️ Sfumato", "iso" to "📐 Isométrico",
            "rupestre" to "🪨 Rupestre", "xilo" to "🪵 Xilogravura", "clay2" to "🧱 Clay 2",
            "giz" to "🖍️ Giz", "clau" to "🎨 Clau", "impamer" to "🖼️ Imp. Americano", "simpsons" to "📺 Suburbano",
            "dark" to "🌑 Dark", "puppet" to "🎭 Puppet", "anime" to "🎌 Anime",
            "ukiyoe" to "🎴 Ukiyo-e",
        )
        col.addView(rotulo("↳ Arte do cenário"))
        col.addView(dropdown(artes, ArteFundo.atual(this)) { id ->
            ArteFundo.definir(this, id); preview.trocarCenaEstilo()
        })
        val estilos = listOf(
            "aqua" to "🎨 Aquarela",
            "bizantino" to "🏛️ Bizantino",
            "clay" to "🧱 Clay",
            "lowpoly" to "🔷 Low Poly",
            "needle_felting" to "🧶 Needle Felting",
            "papel_mache" to "📰 Papel-maché", "papel_mache_2" to "📰 Papel-maché 2",
            "paper_cutout" to "✂️ Paper Cutout", "paper_cutout_2" to "✂️ Paper Cutout 2",
            "paper_cutout_3" to "✂️ Paper Cutout 3",
            "pixel" to "🟦 Pixel Art", "pixel_art2" to "🟩 Pixel Art 2",
            "pixel_retro" to "👾 Pixel Retrô", "pixel_retro_2" to "👾 Pixel Retrô 2",
            "pointilismo" to "🖌️ Pontilhismo",
            "point_gpt" to "🖌️ Pontilhismo GPT", "point_gpt_2" to "🖌️ Pontilhismo GPT 2",
            "rpg" to "⚔️ RPG",
            "rupestre_og" to "🪨 Rupestre", "rupestre_1" to "🪨 Rupestre 2",
            "rupestre_2" to "🪨 Rupestre 3", "rupestre_gemini" to "🪨 Rupestre Gemini",
            "simplao" to "✏️ Simplão",
            "talhe_doce_og" to "🪵 Talhe Doce", "talhe_doce" to "🪵 Talhe Doce Rico",
            "ukiyoe" to "🎴 Ukiyo-e",
            "doodle_kinder" to "🧸 Doodle Kinder", "doodle_rabisco" to "🖋️ Doodle Rabisco",
            "van_gogh" to "🌌 Van Gogh"
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

        // Destrave de cenários pagos — sem isto o `tanque` é intestável, já que
        // não há produto no Play Console nem Play Store no emulador.
        col.addView(switch("Destravar cenários pagos (teste)", DebugOverride.destravarPagos(this)) { on ->
            DebugOverride.setDestravarPagos(this, on)
            Toast.makeText(
                this,
                if (on) "Cenários pagos liberados. Abra a Loja para escolher."
                else "Cenários pagos voltaram a exigir compra.",
                Toast.LENGTH_SHORT
            ).show()
        })
        col.addView(TextView(this).apply {
            text = "Só vale em build debug: no APK de release este destrave não existe."
            setTextColor(Color.parseColor("#8A94A6")); textSize = 12f
            setPadding(0, dp(2), 0, 0)
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

        secaoAcervo(col)

        col.addView(Button(this).apply {
            text = "Fechar"
            setOnClickListener { finish() }
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply { topMargin = dp(16) }
        })
    }

    /**
     * ACERVO — baixar a arte da cena escolhida de um servidor, em vez de tirar
     * dos assets. É a única forma de exercitar o download antes de a Loja ter
     * botão pra isso (ver docs/dev/ENTREGA-DE-ARTE.md). No emulador, o servidor
     * do tester responde em `http://10.0.2.2:8123/dist/`.
     */
    private fun secaoAcervo(col: LinearLayout) {
        col.addView(rotulo("—— Acervo (download da arte) ——"))
        val estado = TextView(this).apply {
            setTextColor(Color.parseColor("#8A94A6")); textSize = 12f
        }
        fun atualizar() {
            val cena = Cena.atual(this); val arte = ArteFundo.atual(this)
            estado.text = buildString {
                append(if (Acervo.temArte(this@DebugActivity, cena, arte))
                    "$cena/$arte: baixado" else "$cena/$arte: usando o asset embutido")
                append("  ·  disco ")
                append("%.1f MB".format(Acervo.bytesEmDisco(this@DebugActivity) / 1e6))
                val b = Acervo.base(this@DebugActivity)
                append("\nservidor: ").append(if (b.isEmpty()) "(nenhum)" else b)
            }
        }
        val campo = android.widget.EditText(this).apply {
            hint = "http://10.0.2.2:8123/dist/"
            setText(Acervo.base(this@DebugActivity))
            setTextColor(Color.WHITE); textSize = 13f
        }
        col.addView(campo)
        col.addView(Button(this).apply {
            text = "Salvar servidor"
            setOnClickListener {
                Acervo.definirBase(this@DebugActivity, campo.text.toString().trim())
                atualizar()
            }
        })
        col.addView(Button(this).apply {
            text = "Baixar arte desta cena"
            setOnClickListener {
                val cena = Cena.atual(this@DebugActivity)
                val arte = ArteFundo.atual(this@DebugActivity)
                estado.text = "baixando $cena/$arte…"
                lifecycleScope.launch {
                    Acervo.baixarArte(this@DebugActivity, cena, arte).collect { p ->
                        when (p) {
                            is Acervo.Progresso.Baixando ->
                                estado.text = "baixando $cena/$arte… %.0f%%".format(p.fracao * 100)
                            is Acervo.Progresso.Erro -> estado.text = "erro: ${p.motivo}"
                            Acervo.Progresso.Ok -> { atualizar(); preview.trocarCenaEstilo() }
                        }
                    }
                }
            }
        })
        col.addView(Button(this).apply {
            text = "Apagar o que foi baixado desta cena"
            setOnClickListener {
                Acervo.apagarCena(this@DebugActivity, Cena.atual(this@DebugActivity))
                atualizar(); preview.recarregar()
            }
        })
        col.addView(estado)
        atualizar()
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
