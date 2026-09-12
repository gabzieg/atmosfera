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
        Cenario(
            id = "fiordes",
            nome = "Fiordes — vila norueguesa",
            descricao = "Vila à beira de um fiorde, com barcos e farol.",
            gratis = false,
            productId = "cenario_fiordes",
        ),
        Cenario(
            id = "cabana2",
            nome = "Cabana no templo",
            descricao = "Ruína de pedra tomada pela selva, à beira de um lago.",
            gratis = false,
            productId = "cenario_cabana2",
        ),
        Cenario(
            id = "velhooeste",
            nome = "Velho Oeste",
            descricao = "Rua de terra entre mesas de arenito, ao pôr do sol.",
            gratis = false,
            productId = "cenario_velhooeste",
        ),
        Cenario(
            id = "heroi",
            nome = "Descanso do Herói",
            descricao = "Túmulo sob a árvore, com o moinho e a vila ao longe.",
            gratis = false,
            productId = "cenario_heroi",
        ),
        Cenario(
            id = "jardim",
            nome = "Jardim japonês",
            descricao = "Cerejeiras, ponte vermelha e lago ao pé do monte.",
            gratis = false,
            productId = "cenario_jardim",
        ),
        Cenario(
            id = "pantano",
            nome = "Pântano",
            descricao = "Água parada, cipós e névoa entre as árvores.",
            gratis = false,
            productId = "cenario_pantano",
        ),
        Cenario(
            id = "farol",
            nome = "Farol",
            descricao = "Farol no penhasco, de frente para o mar aberto.",
            gratis = false,
            productId = "cenario_farol",
        ),
        Cenario(
            id = "praia",
            nome = "Praia tropical",
            descricao = "Enseada de areia clara, coqueiros e um galeão no horizonte.",
            gratis = false,
            productId = "cenario_praia",
        ),
    )

    fun por(id: String): Cenario? = cenarios.firstOrNull { it.id == id }
    val padrao: Cenario get() = cenarios.first()
}
