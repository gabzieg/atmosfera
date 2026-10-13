package com.terra.wallpaper.engine

/** Nomes de apresentação; IDs de arquivos, cenas e produtos permanecem estáveis. */
internal object NomesCatalogo {
    val temporariamenteFora = setOf("estacionamento", "natal")
    fun familia(id: String): String = when (id) {
        "cabana2" -> "cabana"
        "cyberseco" -> "cyberpunk"
        else -> id
    }
    fun ambiente(id: String): String = when (id) {
        "cabana" -> "Lago"
        "cabana2" -> "Selva"
        "cyberpunk" -> "Canal"
        "cyberseco" -> "Rua"
        else -> nome(id)
    }
    fun nome(id: String): String = nomes[id] ?: id.replaceFirstChar { it.uppercase() }
    private val nomes = mapOf(
        "aldeiajapao" to "Aldeia japonesa", "apefino" to "Sala com vista",
        "beco" to "Beco japonês", "cabana" to "Cabana na floresta", "cabana2" to "Cabana na selva",
        "cafeinterno" to "Café na esquina", "cafeparis" to "Café de Paris",
        "camboja" to "Templo cambojano", "castelo" to "Castelo nas montanhas",
        "cidadeproibida" to "Cidade proibida", "cofre" to "Câmara do ouro",
        "cyberpunk" to "Cyberpunk", "cyberseco" to "Cyberpunk — Rua",
        "elfico" to "Refúgio élfico", "estrada" to "Estrada desolada",
        "heroi" to "Campina dos moinhos", "himalaia" to "Templo no Himalaia",
        "mureta" to "Muro do campo", "natal" to "Feira de Natal",
        "natalchale" to "Chalé de Natal", "natalsala" to "Sala de Natal",
        "navio" to "Navio viking", "notredame" to "Notre-Dame",
        "nortada" to "Cabana da aurora", "orbita" to "Estação orbital",
        "pizzaria" to "Mesa no beco italiano", "porto" to "Porto medieval",
        "postapoc" to "Cidade esquecida", "praia" to "Baía dos navegantes",
        "praiaeuropa" to "Praia mediterrânea", "quarto" to "Quarto acolhedor",
        "ruinas" to "Ruínas do tesouro", "tanque" to "Campo de batalha",
        "terrao" to "Campinho de várzea", "templo" to "Templo grego",
        "trem" to "Vagão de trem", "vale" to "Vale alpino",
        "velhooeste" to "Velho Oeste", "vilaviking" to "Vila Viking",
        "vigia" to "Torre de vigia", "vulcao" to "Ilha vulcânica",
    )
}
