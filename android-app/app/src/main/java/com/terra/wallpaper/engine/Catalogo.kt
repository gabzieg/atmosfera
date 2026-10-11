package com.terra.wallpaper.engine

import android.content.Context
import org.json.JSONArray

/**
 * Catálogo embarcado da primeira versão do Terra.
 *
 * Cada cenário oferece uma arte gratuita para experimentação. Premium libera
 * apenas efeitos e estilos. A compra individual das demais artes e a entrega
 * do catálogo remoto ainda serão integradas; os arquivos remotos não fazem
 * parte deste catálogo embarcado.
 *
 * [id] corresponde à pasta `assets/atmosfera/cenas/<id>/`.
 * [productId] foi mantido no contrato para compatibilidade, mas não há compras
 * avulsas de cenário nesta versão.
 */
data class Cenario(
    val id: String,
    val nome: String,
    val descricao: String,
    val gratis: Boolean,
    val productId: String?,
    val artes: List<String>,
    val artesGratis: Set<String> = emptySet(),
    val artesRemotas: Set<String> = emptySet(),
    val miniaturas: Map<String, String> = emptyMap(),
)

object Catalogo {
    @Volatile private var extras: List<Cenario> = emptyList()
    val cenarios: List<Cenario> get() = embarcados + extras
    val embarcados: List<Cenario> = listOf(
        Cenario(
            id = "cabana",
            nome = "Cabana na floresta",
            descricao = "Refúgio de madeira à beira do lago, sob a mata.",
            gratis = false,
            productId = null,
            artes = listOf("pixel"),
            artesGratis = setOf("pixel"),
        ),
        Cenario(
            id = "bruxa",
            nome = "Casa da bruxa",
            descricao = "Casario torto de bruxa à beira do lago.",
            gratis = false,
            productId = null,
            artes = listOf("pixel", "clay"),
            artesGratis = setOf("clay"),
        ),
        Cenario(
            id = "lavanda",
            nome = "Campo de lavanda",
            descricao = "Fileiras de lavanda subindo até a casa de pedra.",
            gratis = false,
            productId = null,
            artes = listOf("pixel", "aqua"),
            artesGratis = setOf("aqua"),
        ),
        Cenario(
            id = "esfinge",
            nome = "Esfinge",
            descricao = "Esfinge e pirâmides ao fim do dia.",
            gratis = false,
            productId = null,
            artes = listOf("pixel", "vangogh"),
            artesGratis = setOf("vangogh"),
        ),
        Cenario(
            id = "jardim",
            nome = "Jardim japonês",
            descricao = "Cerejeiras, ponte vermelha e lago ao pé do monte.",
            gratis = false,
            productId = null,
            artes = listOf("pixel", "ukiyoe"),
            artesGratis = setOf("ukiyoe"),
        ),
    )

    private val excluidos = setOf("simpsons", "budokai", "konoha", "timessquare", "burj", "ogro", "eiffel")

    /** Metadados/miniaturas locais; nao baixa HD nem concede direito. */
    fun carregar(c: Context) {
        extras = try {
            val linhas = JSONArray(c.assets.open("atmosfera/catalogo/artes.json").bufferedReader().use { it.readText() })
            val grupos = (0 until linhas.length()).map { linhas.getJSONObject(it) }
                .filter { !it.getBoolean("embarcada") && it.getString("cena") !in excluidos }
                .groupBy { it.getString("cena") }
            grupos.mapNotNull { (id, artes) ->
                if (!id.matches(Regex("[a-z0-9_]+")) || Cenas.por(id).id != id) return@mapNotNull null
                val ids = artes.map { it.getString("arte") }.distinct()
                if (ids.any { !it.matches(Regex("[a-z0-9_]+")) }) return@mapNotNull null
                Cenario(id, id.replaceFirstChar { it.uppercase() }, "Arte do acervo Terra.",
                    false, null, ids, artesRemotas = ids.toSet(),
                    miniaturas = ids.associateWith { "atmosfera/miniaturas/${id}__${it}.jpg" })
            }.sortedBy { it.id }
        } catch (_: Exception) { emptyList() }
    }

    fun produtoArte(cenario: Cenario, arte: String): String? =
        if (arte !in cenario.artes || cenario.gratis || arte in cenario.artesGratis) null
        else "terra_arte_${cenario.id}_$arte"

    fun produtosArtes(): Set<String> = cenarios.flatMap { c -> c.artes.mapNotNull { produtoArte(c, it) } }.toSet()
    fun por(id: String): Cenario? = cenarios.firstOrNull { it.id == id }
    val padrao: Cenario get() = cenarios.first()
}
