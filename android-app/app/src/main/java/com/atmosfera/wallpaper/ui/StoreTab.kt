package com.atmosfera.wallpaper.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.atmosfera.wallpaper.billing.BillingManager
import com.atmosfera.wallpaper.engine.Catalogo
import com.atmosfera.wallpaper.engine.Cenario
import com.atmosfera.wallpaper.engine.Cenas
import com.atmosfera.wallpaper.engine.Estilos
import androidx.compose.ui.platform.LocalContext
import com.atmosfera.wallpaper.ui.components.MasonryGrid
import com.atmosfera.wallpaper.ui.components.MosaicCard
import com.atmosfera.wallpaper.ui.components.PillSearchBar
import com.atmosfera.wallpaper.ui.components.SectionCarousel
import com.atmosfera.wallpaper.ui.components.StackedThumbnail
import com.atmosfera.wallpaper.ui.components.StatusPill
import com.atmosfera.wallpaper.ui.components.cenarioTemAsset
import com.atmosfera.wallpaper.ui.theme.Radius
import com.atmosfera.wallpaper.ui.theme.Spacing

// ── Helpers de domínio compartilhados entre a Loja e a tela de detalhe ────────

/** Artes de fundo de um cenário: base "pixel" + variantes (clay/aqua) do motor. */
internal fun artesDoCenario(id: String): List<String> =
    listOf("pixel") + Cenas.por(id).variantes.keys.toList()

// Cenários que existem no Catalogo (motor) mas ainda não têm `fundo.png` nos
// assets são escondidos da Loja — filtro só de EXIBIÇÃO, não edita
// `engine/Catalogo.kt` (congelado).
//
// Era uma lista fixa (`setOf("fiordes")`) que só ficava correta enquanto alguém
// lembrasse de editá-la a cada snapshot do motor: cenário novo sem arte voltaria
// a aparecer como card quebrado. Agora a pergunta é feita aos assets de verdade,
// via `cenarioTemAsset` — some sozinho quando entra, aparece sozinho quando a
// arte chega.

/**
 * Nome de exibição de um estilo — serve tanto pro estilo de EFEITO quanto pra
 * ARTE do cenário (o seletor da tela de detalhe chama esta mesma função).
 *
 * A tabela existe porque o fallback (capitalizar o slug) mostrava coisa como
 * "Needlefelting", "Papelmache" e "Gizcera" pro usuário. Os slugs foram
 * unificados em 09/09 (`needle`+`needlefelting`, `xilo`+`xilogravura`,
 * `impress`+`impressionista`, `papel`+`mache`+`papelmache`, `cutout`+
 * `papercutout`, `giz`+`cera`+`gizcera`), então o mesmo estilo em cenas
 * diferentes agora cai na MESMA linha daqui — que é o que permite vender pack
 * por estilo. Numeração romana = segunda arte no mesmo estilo, na mesma cena.
 */
internal fun estiloNome(id: String): String = NOMES_ESTILO[id]
    ?: id.replaceFirstChar { it.uppercase() }

private val NOMES_ESTILO: Map<String, String> = mapOf(
    // ── pixel ──
    "pixel" to "Pixel Art", "pixel2" to "Pixel Art II", "pixelart" to "Pixel Art",
    "pixelv0" to "Pixel Art (v0)", "pixel16" to "Pixel 16 bits",
    "16bits" to "16 bits", "16bits2" to "16 bits II",
    "8bits" to "8 bits", "8bits2" to "8 bits II",
    // ── artesanato ──
    "clay" to "Clay", "clay2" to "Clay II",
    "papelmache" to "Papel machê",
    "papercutout" to "Paper cutout",
    "needlefelting" to "Needle felting",
    "bordado" to "Bordado", "bordado1" to "Bordado I", "bordado2" to "Bordado II",
    "tapecaria" to "Tapeçaria",
    "ceramica" to "Cerâmica", "ceramica2" to "Cerâmica II",
    "puppet" to "Puppet",
    // ── pintura ──
    "aqua" to "Aquarela",
    "vangogh" to "Van Gogh", "vangoghnoite" to "Van Gogh (noite)",
    "impressionista" to "Impressionismo",
    "impamer" to "Impressionismo americano", "impalemao" to "Impressionismo alemão",
    "point" to "Pontilhismo", "point2" to "Pontilhismo II",
    "fauvismo" to "Fauvismo", "sfumato" to "Sfumato",
    "gizcera" to "Giz de cera",
    // ── gravura e mosaico ──
    "xilogravura" to "Xilogravura",
    "ukiyoe" to "Ukiyo-e", "ukiyoe2" to "Ukiyo-e II", "ukiyogpt" to "Ukiyo-e III",
    "bizantino" to "Bizantino", "mosaicobizantino" to "Mosaico bizantino",
    "rupestre" to "Rupestre",
    // ── desenho e animação ──
    "doodle" to "Doodle", "doodle2" to "Doodle II", "doodleinf" to "Doodle infantil",
    "cartoon" to "Cartoon", "anime" to "Anime", "anime1" to "Anime II",
    "chibi" to "Chibi", "kodomo" to "Kodomo", "seinen" to "Seinen",
    "suburbano" to "Suburbano",
    // ── 3D e outros ──
    "lowpoly" to "Low poly", "poly2" to "Low poly II", "iso" to "Isométrico",
    "cozy" to "Cozy", "cozynoite" to "Cozy (noite)",
    "dark" to "Dark", "vivid" to "Vivid", "noite" to "Noite",
    "terraco" to "Terraço", "longe" to "Plano aberto",
    "dragao" to "Dragão", "dragao2" to "Dragão II", "dragao3" to "Dragão III",
)

@Composable
fun StoreTab(viewModel: MainViewModel) {
    // Substituir a Loja pelo browser: navegação interna lista ⇄ detalhe,
    // sem tocar no NavHost de topo (a aba continua sendo "Loja").
    var cenarioAberto by rememberSaveable { mutableStateOf<String?>(null) }

    if (cenarioAberto != null) {
        BackHandler { cenarioAberto = null }
        SceneDetailScreen(
            sceneId = cenarioAberto!!,
            viewModel = viewModel,
            onBack = { cenarioAberto = null },
        )
    } else {
        StoreBrowser(viewModel = viewModel, onAbrir = { cenarioAberto = it })
    }
}

@Composable
private fun StoreBrowser(viewModel: MainViewModel, onAbrir: (String) -> Unit) {
    val isPremium by viewModel.isPremium.collectAsState()
    val currentSceneId by viewModel.currentSceneId.collectAsState()
    val currentEffectStyle by viewModel.currentEffectStyle.collectAsState()
    val precos by viewModel.billingManager.precos.collectAsState()

    val assets = LocalContext.current.assets
    // Checado uma vez por sessão (abre e fecha um handle por cenário), não a
    // cada tecla digitada na busca.
    val publicados = remember(assets) {
        Catalogo.cenarios.filter { cenarioTemAsset(assets, it.id) }
    }

    var query by remember { mutableStateOf("") }
    val cenarios = remember(publicados, query) {
        publicados.filter { it.nome.contains(query.trim(), ignoreCase = true) }
    }
    // Alturas variadas → efeito escalonado do mosaico.
    val aspectos = listOf(0.72f, 0.95f, 0.78f, 0.68f, 0.88f)

    MasonryGrid(
        items = cenarios,
        modifier = Modifier.fillMaxSize(),
        columns = 2,
        key = { it.id },
        header = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
                Text("Loja", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
                PillSearchBar(query = query, onQueryChange = { query = it })
                PremiumBanner(
                    isPremium = isPremium,
                    priceText = precos[BillingManager.PRODUTO_PREMIUM],
                    onBuy = { viewModel.buyPremium(it) },
                )
                // Carrossel de seção: categoria pequena + título grande + fileira rolável.
                SectionCarousel(
                    categoria = "Coleção",
                    titulo = "Estilos de efeito",
                    items = Estilos.ids,
                    modifier = Modifier.padding(bottom = Spacing.xs),
                    // alinhado ao conteúdo do grid (sem padding lateral extra do carrossel)
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 0.dp),
                ) { estiloId ->
                    EstiloChip(
                        estiloId = estiloId,
                        selecionado = estiloId == currentEffectStyle,
                        onClick = { viewModel.setEffectStyle(estiloId) },
                    )
                }
                Text("Cenários", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
            }
        },
    ) { cenario ->
        val idx = Catalogo.cenarios.indexOfFirst { it.id == cenario.id }.coerceAtLeast(0)
        CenarioTile(
            cenario = cenario,
            isUnlocked = viewModel.isSceneUnlocked(cenario),
            isActive = currentSceneId == cenario.id,
            aspect = aspectos[idx % aspectos.size],
            onClick = { onAbrir(cenario.id) },
        )
    }
}

/** Padrão reproduzido: **card do mosaico** — imagem (ou pilha de coleção) no topo,
 *  cantos arredondados, título + estado embaixo; altura definida pelo conteúdo. */
@Composable
private fun CenarioTile(
    cenario: Cenario,
    isUnlocked: Boolean,
    isActive: Boolean,
    aspect: Float,
    onClick: () -> Unit,
) {
    MosaicCard(onClick = onClick) {
        StackedThumbnail(
            sceneId = cenario.id,
            artes = artesDoCenario(cenario.id),
            aspectRatio = aspect,
        )
        Column(Modifier.padding(Spacing.md)) {
            Text(
                cenario.nome,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
            )
            Spacer(Modifier.height(Spacing.sm))
            when {
                isActive -> StatusPill(
                    "Atual",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                cenario.gratis -> StatusPill(
                    "Grátis",
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                isUnlocked -> StatusPill("Comprado")
                // uma arte de vitrine grátis (ex.: o ukiyo-e do jardim); o resto é pago
                cenario.artesGratis.isNotEmpty() -> StatusPill(
                    "Arte grátis",
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                else -> StatusPill(
                    "Bloqueado",
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
internal fun EstiloChip(estiloId: String, selecionado: Boolean, onClick: () -> Unit) {
    val nome = estiloNome(estiloId)
    val bg = if (selecionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val fg = if (selecionado) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
    val borda = if (selecionado) bg else MaterialTheme.colorScheme.outline
    Row(
        modifier = Modifier
            .background(bg, RoundedCornerShape(Radius.pill))
            .border(1.dp, borda, RoundedCornerShape(Radius.pill))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(nome, style = MaterialTheme.typography.labelLarge, color = fg, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun PremiumBanner(isPremium: Boolean, priceText: String?, onBuy: (android.app.Activity) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity
    // Banner sempre em superfície ESCURA: no estado não-premium o CTA é um botão
    // claro (primary) — sobre um container claro ele sumiria. Ênfase vem do botão,
    // não do fundo. (tertiaryContainer/surface são só um degrau de tom.)
    val container = if (isPremium) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surface
    val onContainer = if (isPremium) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(container, RoundedCornerShape(Radius.card))
            .padding(Spacing.xl),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = onContainer, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(Spacing.sm))
            Text(
                if (isPremium) "Você é Premium" else "Desbloquear efeitos vivos",
                style = MaterialTheme.typography.titleMedium,
                color = onContainer,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(Spacing.sm))
        Text(
            if (isPremium) "Raios, vento, vagalumes, fumaça, fases da lua e acúmulo de neve ligados em todos os cenários."
            else "Compra única que liga os efeitos climáticos vivos em TODOS os cenários.",
            style = MaterialTheme.typography.bodyMedium,
            color = onContainer.copy(alpha = 0.9f),
        )
        if (!isPremium) {
            Spacer(Modifier.height(Spacing.md))
            // priceText nulo = o Google Play não devolveu o produto (offline, sem
            // Play Store, ou produto ainda não publicado). Botão desabilitado em
            // vez de mudo: clicar sem efeito e sem explicação parece app quebrado.
            val disponivel = priceText != null
            Button(
                onClick = { activity?.let(onBuy) },
                enabled = disponivel,
                shape = RoundedCornerShape(Radius.pill),
            ) {
                Text("Comprar Premium${priceText?.let { " · $it" } ?: ""}")
            }
            Spacer(Modifier.height(Spacing.sm))
            Text(
                if (disponivel) "Ou compre só o cenário que quiser, dentro dele."
                else "Compras indisponíveis agora. Verifique a conexão e se o Google Play está atualizado.",
                style = MaterialTheme.typography.bodySmall,
                color = onContainer.copy(alpha = 0.7f),
            )
        }
    }
}
