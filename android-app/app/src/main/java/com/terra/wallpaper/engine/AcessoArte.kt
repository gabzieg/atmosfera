package com.terra.wallpaper.engine

/** Direito à arte, compartilhado pela UI e pela leitura do wallpaper.
 * Premium pertence aos efeitos e não participa desta autorização.
 * A compra individual será integrada aqui quando houver produtos por arte.
 */
object AcessoArte {
    fun permitida(cenario: Cenario, arte: String, destraveTeste: Boolean = false): Boolean =
        arte in cenario.artes && (cenario.gratis || arte in cenario.artesGratis || destraveTeste)

    /** Preferências antigas não podem manter uma arte paga aplicada. */
    fun selecionada(cenario: Cenario, salva: String, destraveTeste: Boolean = false): String =
        if (permitida(cenario, salva, destraveTeste)) salva
        else cenario.artes.first { permitida(cenario, it, destraveTeste) }
}
