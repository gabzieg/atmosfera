package com.atmosfera.wallpaper.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Tokens de espaçamento e raio — fonte única de "respiro" do app.
 * Use `Spacing.*` no lugar de `dp` solto pelas telas (padrão consistente,
 * fácil de ajustar em um lugar só). Escala 4/8/12/16/24/32.
 */
object Spacing {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
}

/** Raios de canto — "bordas bem arredondadas" da referência. */
object Radius {
    val card = 20.dp   // cards de conteúdo
    val tile = 24.dp   // tiles do mosaico (mais arredondado)
    val pill = 50.dp   // pílulas (search bar, botões-cápsula, chips)
}
