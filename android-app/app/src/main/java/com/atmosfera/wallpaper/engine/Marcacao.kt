package com.atmosfera.wallpaper.engine

import android.content.res.AssetManager
import org.json.JSONObject

/**
 * Dados que vêm da DEMARCAÇÃO da cena (`zonas.json`, gerado por
 * `tools/cena_marcada.py` a partir do que o usuário pinta à mão).
 *
 * Aqui só entra o que o motor não consegue derivar da imagem: onde ficam as
 * LUZES (e de que tipo) e de onde sai a FUMAÇA. As zonas de impacto continuam
 * vindo do `zonas.png`, que é mais preciso e não precisa de parser.
 *
 * Cena sem `zonas.json` (ou sem esses campos) devolve listas vazias — aí o
 * motor cai no comportamento antigo (as constantes da cabana no [Atlas]).
 */
data class LuzCena(
    val x: Float, val y: Float, val w: Float, val h: Float,
    /** "parcial" = apaga à meia-noite (janela) · "completa" = até o amanhecer. */
    val tipo: String,
    /**
     * Caixa APERTADA no vidro que emite (`tools/luz_vidro.py`). A caixa marcada
     * é a luminária INTEIRA — lampião com haste, placa com moldura — e usá-la
     * como fonte punha o halo no meio do poste, do tamanho do poste. Cena cujo
     * `zonas.json` ainda não passou pela ferramenta cai na caixa marcada.
     */
    val vx: Float = x, val vy: Float = y, val vw: Float = w, val vh: Float = h,
    /** Cor ACESA desta luz: a lanterna vermelha não acende laranja. */
    val cor: Int = android.graphics.Color.rgb(255, 186, 104),
) {
    val cx get() = vx + vw / 2f
    val cy get() = vy + vh / 2f
}

data class BocaFumaca(val x: Float, val y: Float, val w: Float)

/** Faixa de ÁGUA numa linha: [y] com água de [x0] a [x1]. O brilho d'água usa
 *  isto p/ respeitar a costa em vez de vazar num retângulo. */
data class FaixaMar(val y: Float, val x0: Float, val x1: Float)

/**
 * CACHOEIRA: a queda d'água mapeada LINHA A LINHA (tools/cachoeira.py). Uma
 * lista por queda, porque o risco que desce precisa seguir a curva de UMA
 * cachoeira — misturadas, ele saltaria de uma pra outra no meio do caminho.
 */
data class Queda(val faixas: List<FaixaMar>)

/** GOTEIRA: pinga de [x],[y] e estoura em [ychao] (só chuva forte/temporal). */
data class Goteira(val x: Float, val y: Float, val ychao: Float)

class DadosMarcacao(
    val luzes: List<LuzCena> = emptyList(),
    val fumaca: List<BocaFumaca> = emptyList(),
    val mar: List<FaixaMar> = emptyList(),
    val cachoeiras: List<Queda> = emptyList(),
    val vulcao: BocaFumaca? = null,
    val goteiras: List<Goteira> = emptyList(),
) {
    companion object {
        val VAZIO = DadosMarcacao()

        /** Lê `<prefixo>zonas.json` dos assets. Ausência/erro = VAZIO. */
        fun ler(assets: AssetManager, prefixo: String): DadosMarcacao = try {
            ler(assets.open("atmosfera/${prefixo}zonas.json")
                .bufferedReader().use { it.readText() })
        } catch (e: Exception) {
            VAZIO
        }

        /** Mesmo parser, a partir do TEXTO — é assim que entra o `zonas.json`
         *  do acervo baixado, que não mora nos assets. Ver Acervo.kt. */
        fun ler(txt: String): DadosMarcacao = try {
            val o = JSONObject(txt)
            val luzes = ArrayList<LuzCena>()
            o.optJSONArray("luzes")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val l = arr.getJSONObject(i)
                    // só a luz que veio da MARCAÇÃO tem tipo; as antigas (farol,
                    // pântano) não têm e seguem apenas pintadas na arte.
                    val tipo = l.optString("tipo", "")
                    if (tipo.isEmpty()) continue
                    val bx = l.optDouble("x", 0.0).toFloat()
                    val by = l.optDouble("y", 0.0).toFloat()
                    val bw = l.optDouble("w", 1.0).toFloat()
                    val bh = l.optDouble("h", 1.0).toFloat()
                    val v = l.optJSONArray("vidro")
                    val c = l.optJSONArray("cor")
                    luzes.add(LuzCena(
                        bx, by, bw, bh, tipo,
                        if (v != null) v.getDouble(0).toFloat() else bx,
                        if (v != null) v.getDouble(1).toFloat() else by,
                        if (v != null) v.getDouble(2).toFloat() else bw,
                        if (v != null) v.getDouble(3).toFloat() else bh,
                        if (c != null) android.graphics.Color.rgb(
                            c.getInt(0), c.getInt(1), c.getInt(2))
                        else android.graphics.Color.rgb(255, 186, 104)))
                }
            }
            val fum = ArrayList<BocaFumaca>()
            o.optJSONArray("fumaca")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val f = arr.getJSONObject(i)
                    val w = f.optDouble("w", 6.0).toFloat()
                    fum.add(BocaFumaca(
                        f.optDouble("x", 0.0).toFloat() + w / 2f,
                        f.optDouble("y", 0.0).toFloat() + f.optDouble("h", 0.0).toFloat() / 2f,
                        maxOf(6f, w)))
                }
            }
            val mar = ArrayList<FaixaMar>()
            o.optJSONArray("mar")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val f = arr.getJSONArray(i)
                    mar.add(FaixaMar(f.getDouble(0).toFloat(),
                                     f.getDouble(1).toFloat(), f.getDouble(2).toFloat()))
                }
            }
            val vul = o.optJSONObject("vulcao")?.let {
                BocaFumaca(it.optDouble("x", 0.0).toFloat(),
                           it.optDouble("y", 0.0).toFloat(),
                           maxOf(8.0, it.optDouble("w", 8.0)).toFloat())
            }
            val got = ArrayList<Goteira>()
            o.optJSONArray("goteira")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val g = arr.getJSONObject(i)
                    got.add(Goteira(g.optDouble("x", 0.0).toFloat(),
                                    g.optDouble("y", 0.0).toFloat(),
                                    g.optDouble("ychao", 0.0).toFloat()))
                }
            }
            val quedas = ArrayList<Queda>()
            o.optJSONArray("cachoeira")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val fa = arr.getJSONArray(i)
                    val lst = ArrayList<FaixaMar>(fa.length())
                    for (j in 0 until fa.length()) {
                        val f = fa.getJSONArray(j)
                        lst.add(FaixaMar(f.getDouble(0).toFloat(),
                                         f.getDouble(1).toFloat(),
                                         f.getDouble(2).toFloat()))
                    }
                    if (lst.size >= 20) quedas.add(Queda(lst))
                }
            }
            DadosMarcacao(luzes, fum, mar, quedas, vul, got)
        } catch (e: Exception) {
            VAZIO
        }
    }
}
