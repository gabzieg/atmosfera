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
            id = "versalhes",
            nome = "Versalhes",
            descricao = "Espelho d'água e o palácio ao fundo.",
            gratis = false,
            productId = "cenario_versalhes",
        ),
        Cenario(
            id = "babilonia",
            nome = "Jardins da Babilônia",
            descricao = "Zigurate em terraços sobre o rio.",
            gratis = false,
            productId = "cenario_babilonia",
        ),
        Cenario(
            id = "telhados",
            nome = "Telhados",
            descricao = "Terraço sobre os telhados da cidade velha.",
            gratis = false,
            productId = "cenario_telhados",
        ),
        Cenario(
            id = "forte",
            nome = "Forte",
            descricao = "Fortaleza cercada de água.",
            gratis = false,
            productId = "cenario_forte",
        ),
        Cenario(
            id = "camboja",
            nome = "Templo tomado",
            descricao = "Ruína khmer engolida pelas raízes.",
            gratis = false,
            productId = "cenario_camboja",
        ),
        Cenario(
            id = "caverna",
            nome = "Caverna",
            descricao = "Boca de caverna sobre o açude.",
            gratis = false,
            productId = "cenario_caverna",
        ),
        Cenario(
            id = "estacionamento",
            nome = "Estacionamento",
            descricao = "Mirante de asfalto sobre a baía.",
            gratis = false,
            productId = "cenario_estacionamento",
        ),
        Cenario(
            id = "mureta",
            nome = "Mureta",
            descricao = "Muro de pedra e o vale aberto.",
            gratis = false,
            productId = "cenario_mureta",
        ),
        Cenario(
            id = "porto",
            nome = "Porto",
            descricao = "Cais medieval com o navio atracado.",
            gratis = false,
            productId = "cenario_porto",
        ),
        Cenario(
            id = "terrao",
            nome = "Terrão",
            descricao = "Campinho de terra na beira da vila.",
            gratis = false,
            productId = "cenario_terrao",
        ),
        Cenario(
            id = "bruxa",
            nome = "Casa da bruxa",
            descricao = "Casario torto de bruxa à beira do lago.",
            gratis = false,
            productId = "cenario_bruxa",
        ),
        Cenario(
            id = "eiffel",
            nome = "Torre Eiffel",
            descricao = "Campo de Marte com a torre ao fundo.",
            gratis = false,
            productId = "cenario_eiffel",
        ),
        Cenario(
            id = "simpsons",
            nome = "Casa amarela",
            descricao = "Rua de subúrbio com a casa amarela.",
            gratis = false,
            productId = "cenario_simpsons",
        ),
        Cenario(
            id = "budokai",
            nome = "Arena de torneio",
            descricao = "Ringue de artes marciais entre montanhas.",
            gratis = false,
            productId = "cenario_budokai",
        ),
        Cenario(
            id = "konoha",
            nome = "Vila ninja",
            descricao = "Rua da vila sob os rostos esculpidos na rocha.",
            gratis = false,
            productId = "cenario_konoha",
        ),
        Cenario(
            id = "trincheira",
            nome = "Trincheira",
            descricao = "Terra de ninguém vista da trincheira.",
            gratis = false,
            productId = "cenario_trincheira",
        ),
        Cenario(
            id = "cofre",
            nome = "Cofre",
            descricao = "Câmara-forte transbordando de ouro.",
            gratis = false,
            productId = "cenario_cofre",
        ),
        Cenario(
            id = "ruinas",
            nome = "Ruínas do tesouro",
            descricao = "Ruínas tomadas por um mar de moedas.",
            gratis = false,
            productId = "cenario_ruinas",
        ),
        Cenario(
            id = "cyberpunk",
            nome = "Cyberpunk — canal",
            descricao = "Passarelas de neon sobre o canal cheio.",
            gratis = false,
            productId = "cenario_cyberpunk",
        ),
        Cenario(
            id = "cyberseco",
            nome = "Cyberpunk — rua",
            descricao = "A mesma cidade pela rua seca, entre letreiros.",
            gratis = false,
            productId = "cenario_cyberseco",
        ),
        Cenario(
            id = "esfinge",
            nome = "Esfinge",
            descricao = "Esfinge e pirâmides ao fim do dia.",
            gratis = false,
            productId = "cenario_esfinge",
        ),
        Cenario(
            id = "sitio",
            nome = "Sítio",
            descricao = "Celeiro vermelho à beira do lago.",
            gratis = false,
            productId = "cenario_sitio",
        ),
        Cenario(
            id = "savana",
            nome = "Savana",
            descricao = "Acácia e poço d'água na planície.",
            gratis = false,
            productId = "cenario_savana",
        ),
        Cenario(
            id = "oasis",
            nome = "Oásis",
            descricao = "Cidade de pedra ao redor do lago no deserto.",
            gratis = false,
            productId = "cenario_oasis",
        ),
        Cenario(
            id = "elfico",
            nome = "Refúgio élfico",
            descricao = "Casa nas raízes da árvore antiga.",
            gratis = false,
            productId = "cenario_elfico",
        ),
        Cenario(
            id = "postapoc",
            nome = "Cidade tomada",
            descricao = "Metrópole em ruínas, engolida pela mata.",
            gratis = false,
            productId = "cenario_postapoc",
        ),
        Cenario(
            id = "castelo",
            nome = "Castelo na Montanha",
            descricao = "Castelo no pico, acima de um mar de nuvens.",
            gratis = false,
            productId = "cenario_castelo",
        ),
        Cenario(
            id = "beco",
            nome = "Beco japonês",
            descricao = "Ruela de izakayas, lanternas e cerejeira.",
            gratis = false,
            productId = "cenario_beco",
        ),
        Cenario(
            id = "templo",
            nome = "Templo grego",
            descricao = "Templo no penhasco, de frente para o Egeu.",
            gratis = false,
            productId = "cenario_templo",
        ),
        Cenario(
            id = "vale",
            nome = "Vale alpino",
            descricao = "Rio entre montanhas, vila e roda d'água.",
            gratis = false,
            productId = "cenario_vale",
        ),
        Cenario(
            id = "muralha",
            nome = "Muralha da China",
            descricao = "A muralha serpenteando entre os picos, com o rio ao fundo.",
            gratis = false,
            productId = "cenario_muralha",
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
