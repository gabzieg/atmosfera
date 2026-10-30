package com.terra.wallpaper.debug

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/** Existe somente no source set debug; não participa da navegação do companion. */
class ConsultaIdadeActivity : Activity() {
    private lateinit var consulta: ConsultaIdade
    private lateinit var resultado: TextView
    private lateinit var botao: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Terra — Consulta de idade"
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        val coluna = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.BLACK)
            isSaveEnabled = false
        }
        fun texto(conteudo: String, tamanho: Float) = TextView(this).apply {
            text = conteudo
            textSize = tamanho
            setTextColor(Color.WHITE)
            isSaveEnabled = false
            setTextIsSelectable(false)
        }
        coluna.addView(texto("Consulta de idade — teste", 24f))
        coluna.addView(texto(
            "A Play pode pedir que você ou o responsável compartilhe a faixa etária. " +
                "A ação é opcional e não altera compras ou acesso às artes. " +
                "O resultado fica só nesta tela e é apagado ao sair. " +
                "Não envie faixa, conta ou capturas para o suporte.", 16f,
        ))
        botao = Button(this).apply {
            text = "Consultar faixa na Play"
            isSaveEnabled = false
        }
        coluna.addView(botao)
        resultado = texto(ConsultaIdade.INICIAL, 18f)
        coluna.addView(resultado)
        coluna.addView(Button(this).apply {
            text = "Fechar diagnóstico"
            setOnClickListener { finish() }
        })
        setContentView(ScrollView(this).apply {
            isSaveEnabled = false
            addView(coluna)
        })
        ViewCompat.setOnApplyWindowInsetsListener(coluna) { view, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val margem = (16 * resources.displayMetrics.density).toInt()
            view.setPadding(barras.left + margem, barras.top + margem, barras.right + margem, barras.bottom + margem)
            insets
        }
        consulta = ConsultaIdade(FontePlaySinaisIdade(this)) { mensagem, ocupada ->
            resultado.text = mensagem
            botao.isEnabled = !ocupada
        }
        botao.setOnClickListener { consulta.iniciar() }
    }

    override fun onStart() {
        super.onStart()
        consulta.ativar()
    }

    override fun onStop() {
        consulta.suspender()
        super.onStop()
    }

    override fun onDestroy() {
        consulta.encerrar()
        super.onDestroy()
    }
}
