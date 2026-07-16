package com.atmosfera.wallpaper.engine

/**
 * CONTRATO (congelado) — catálogo de cenários (wallpapers) do Atmosfera.
 *
 * Esta é a fonte da verdade dos wallpapers disponíveis. O FRONT lê isto para
 * montar a loja / o seletor de cenário. O MOTOR (nosso lado) atualiza a lista
 * conforme novos cenários ficam prontos — o front NÃO edita este arquivo.
 *
 * Convenções:
 *  - [id] = nome da pasta de assets em `assets/atmosfera/cenas/<id>/`
 *           (o cenário grátis "cabana" mora na raiz `assets/atmosfera/`).
 *  - [gratis] = já incluído (não precisa comprar).
 *  - [productId] = SKU de COMPRA AVULSA no Google Play (null se grátis).
 *                  Quem tem Premium destrava os efeitos "vivos" em todos.
 */
data class Cenario(
    val id: String,
    val nome: String,
    val descricao: String,
    val gratis: Boolean,
    val productId: String?,
)

object Catalogo {
    val cenarios: List<Cenario> = listOf(
        Cenario(
            id = "cabana",
            nome = "Cabana na floresta",
            descricao = "Refúgio de madeira à beira do lago, sob a mata.",
            gratis = true,
            productId = null,
        ),
        Cenario(
            id = "tanque",
            nome = "Tanque — campo de batalha",
            descricao = "Diorama em massinha de um Sherman no front de guerra.",
            gratis = false,
            productId = "cenario_tanque",
        ),
    )

    fun por(id: String): Cenario? = cenarios.firstOrNull { it.id == id }
    val padrao: Cenario get() = cenarios.first()
}
