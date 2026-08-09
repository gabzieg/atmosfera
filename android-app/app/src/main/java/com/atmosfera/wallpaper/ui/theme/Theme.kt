package com.atmosfera.wallpaper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * ColorScheme monocromático. Todo o app lê estes tokens — trocar a paleta é
 * só reescrever [Color.kt] + este mapeamento.
 *
 * Papéis-chave sem matiz:
 *  - primary            → ênfase clara (CTA preenchido, tinta de "ativo")
 *  - primaryContainer   → superfície INVERTIDA (pílula "ativa"/"selecionada")
 *  - tertiaryContainer  → estado neutro/calmo ("ativado", "grátis")
 *  - error*             → também neutralizado (sem vermelho), p/ não furar o mono
 */
private val AtmosferaColorScheme = darkColorScheme(
    background = AtmBackground,
    onBackground = AtmTextPrimary,
    surface = AtmSurface,
    onSurface = AtmTextPrimary,
    surfaceVariant = AtmSurfaceVariant,
    onSurfaceVariant = AtmTextSecondary,
    surfaceContainerHigh = AtmSurfaceVariant,
    primary = AtmEmphasis,
    onPrimary = AtmOnEmphasis,
    primaryContainer = AtmEmphasisContainer,
    onPrimaryContainer = AtmOnEmphasisContainer,
    secondary = AtmEmphasis,
    onSecondary = AtmOnEmphasis,
    tertiaryContainer = AtmStateContainer,
    onTertiaryContainer = AtmOnStateContainer,
    outline = AtmOutline,
    outlineVariant = AtmOutline,
    inverseSurface = AtmEmphasisContainer,
    inverseOnSurface = AtmOnEmphasisContainer,
    error = AtmTextSecondary,
    onError = AtmOnEmphasis,
)

@Composable
fun AtmosferaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AtmosferaColorScheme,
        typography = AtmosferaTypography,
        content = content,
    )
}
