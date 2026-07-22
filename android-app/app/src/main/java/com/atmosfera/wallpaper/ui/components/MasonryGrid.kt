package com.atmosfera.wallpaper.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.atmosfera.wallpaper.ui.theme.Spacing

/**
 * Padrão reproduzido: **grade em mosaico (masonry/staggered grid)** — cards de
 * alturas variadas organizados em colunas, sem alinhamento rígido de altura
 * entre eles.
 *
 * Reutilizável e genérico: recebe uma lista de qualquer tipo e desenha cada
 * item; o próprio conteúdo define a altura (é isso que cria o efeito escalonado).
 * [header] ocupa a linha inteira (útil pra barra de busca / carrossel de seção
 * rolarem junto com a grade).
 */
@Composable
fun <T> MasonryGrid(
    items: List<T>,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    contentPadding: PaddingValues = PaddingValues(Spacing.lg),
    horizontalSpacing: Dp = Spacing.md,
    verticalSpacing: Dp = Spacing.md,
    key: ((T) -> Any)? = null,
    header: (@Composable () -> Unit)? = null,
    itemContent: @Composable (T) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(horizontalSpacing),
        verticalItemSpacing = verticalSpacing,
    ) {
        if (header != null) {
            item(span = StaggeredGridItemSpan.FullLine) { header() }
        }
        items(items = items, key = key) { itemContent(it) }
    }
}
