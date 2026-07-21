package com.atmosfera.wallpaper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AtmosferaColorScheme = darkColorScheme(
    background = AtmBackground,
    onBackground = AtmTextPrimary,
    surface = AtmSurface,
    onSurface = AtmTextPrimary,
    surfaceVariant = AtmSurfaceElevated,
    onSurfaceVariant = AtmTextSecondary,
    surfaceContainerHigh = AtmSurfaceElevated,
    primary = AtmAccent,
    onPrimary = Color.White,
    primaryContainer = AtmAccentContainer,
    onPrimaryContainer = AtmOnAccentContainer,
    secondary = AtmAccent,
    onSecondary = Color.White,
    tertiaryContainer = AtmSuccessContainer,
    onTertiaryContainer = AtmOnSuccessContainer,
    outline = AtmOutline,
    outlineVariant = AtmOutline,
)

@Composable
fun AtmosferaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AtmosferaColorScheme,
        typography = AtmosferaTypography,
        content = content,
    )
}
