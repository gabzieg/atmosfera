package com.terra.wallpaper.engine

/**
 * Catálogo embarcado da primeira versão do Terra.
 *
 * Cada cenário oferece uma arte gratuita para experimentação. A compra única
 * do Premium libera todas as demais artes dos cinco cenários, além dos efeitos
 * e estilos Premium. Os cenários reservados para packs futuros ficam fora do
 * módulo Android e, portanto, não aumentam o tamanho do aplicativo.
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
)

object Catalogo {
    val cenarios: List<Cenario> = listOf(
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

    fun por(id: String): Cenario? = cenarios.firstOrNull { it.id == id }
    val padrao: Cenario get() = cenarios.first()
}
