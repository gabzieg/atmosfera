package com.atmosfera.wallpaper.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta ÚNICA do app — monocromática (preto / cinzas / branco).
 *
 * Sem matiz: a hierarquia vem de **contraste, peso e opacidade**, não de cor.
 * O "destaque"/estado ativo é resolvido por INVERSÃO (preenchimento claro sobre
 * fundo escuro), não por uma cor de acento. Para trocar a identidade depois,
 * edite só estes valores (e o mapeamento em [Theme.kt]) — nenhuma cor fica
 * espalhada pelas telas.
 *
 * Neutro escolhido, não default: um viés levíssimo de azul-frio nos cinzas
 * (canal B +2/3) para casar com o "clima/noite" do produto sem virar colorido.
 */

// ── Fundos ──────────────────────────────────────────────────────────
val AtmBackground = Color(0xFF0D0E10)     // fundo (quase preto)
val AtmSurface = Color(0xFF17181B)        // cards / painéis
val AtmSurfaceVariant = Color(0xFF202226) // chips, stat tiles, busca, indicador de nav
val AtmOutline = Color(0xFF32353A)        // bordas hairline

// ── Texto ───────────────────────────────────────────────────────────
val AtmTextPrimary = Color(0xFFF4F5F7)    // texto primário (quase branco)
val AtmTextSecondary = Color(0xFF9A9EA5)  // texto secundário / labels (cinza médio)

// ── Ênfase (substitui o antigo azul) ────────────────────────────────
// Claro sobre escuro = maior destaque. Usado em CTAs preenchidos e no estado
// "ativo/selecionado" (pílula/superfície invertida).
val AtmEmphasis = Color(0xFFF4F5F7)          // primary   — preenchimento claro / tinta de destaque
val AtmOnEmphasis = Color(0xFF101113)        // onPrimary — texto escuro sobre o claro
val AtmEmphasisContainer = Color(0xFFE7E8EB) // primaryContainer   — superfície invertida (pílula "ativa")
val AtmOnEmphasisContainer = Color(0xFF141518) // onPrimaryContainer

// ── Estado neutro/calmo (ex.: "ativado", "grátis") ──────────────────
// Diferencia sem gritar: um degrau acima do fundo, texto sereno.
val AtmStateContainer = Color(0xFF24262A)    // tertiaryContainer
val AtmOnStateContainer = Color(0xFFC9CCD2)  // onTertiaryContainer
