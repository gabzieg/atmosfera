package com.atmosfera.wallpaper.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.atmosfera.wallpaper.ui.theme.Spacing

/**
 * Padrão reproduzido: **cabeçalho de seção** — categoria em texto pequeno
 * (sobretítulo) acima de um título grande em destaque.
 */
@Composable
fun SectionHeader(
    categoria: String,
    titulo: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Eyebrow discreto: rótulo pequeno e MUTED sobre o título grande.
        // (Sem cor de acento, o contraste do par é que cria a hierarquia.)
        Text(
            categoria.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            titulo,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

/**
 * Padrão reproduzido: **carrossel horizontal com cabeçalho de seção** — o
 * cabeçalho (categoria + título) acima de uma fileira rolável de cards.
 * Genérico: recebe a lista e desenha cada item.
 */
@Composable
fun <T> SectionCarousel(
    categoria: String,
    titulo: String,
    items: List<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = Spacing.lg),
    itemContent: @Composable (T) -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        SectionHeader(
            categoria = categoria,
            titulo = titulo,
            modifier = Modifier.padding(horizontal = Spacing.lg),
        )
        LazyRow(
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            items(items = items) { itemContent(it) }
        }
    }
}
