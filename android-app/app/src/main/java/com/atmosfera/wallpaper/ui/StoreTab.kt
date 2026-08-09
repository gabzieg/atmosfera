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

/** Nome de exibição de um estilo de efeito (sem emoji — identidade monocromática). */
internal fun estiloNome(id: String): String = when (id) {
    "pixel" -> "Pixel Art"
    "clay" -> "Clay"
    "bizantino" -> "Bizantino"
    "aqua" -> "Aquarela"
    "ukiyoe" -> "Ukiyo-e"
    // Fallback genérico em vez de uma lista fixa: os ids de arte/estilo vêm de
    // `engine/` (Rafael) e crescem a cada snapshot — travar um nome por id aqui
    // quebraria a cada estilo novo até alguém lembrar de atualizar esta lista.
    // "paper_cutout_2" -> "Paper Cutout 2"; "rupestre_og" -> "Rupestre Og". Não
    // fica perfeito pra toda sigla (ex.: "point_gpt" -> "Point Gpt"), mas nunca
    // pior que o id cru com underscore.
    else -> id.replace('_', ' ').split(' ').joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
}

@Composable
fun StoreTab(viewModel: MainViewModel) {
    // Substituir a Loja pelo browser: navegação interna lista ⇄ detalhe,
    // sem tocar no NavHost de topo (a aba continua sendo "Loja").
    var cenarioAberto by rememberSaveable { mutableStateOf<String?>(null) }
    var premiumAberto by rememberSaveable { mutableStateOf(false) }

    when {
        premiumAberto -> PremiumScreen(
            viewModel = viewModel,
            onVoltar = { premiumAberto = false },
        )
        cenarioAberto != null -> {
            BackHandler { cenarioAberto = null }
            SceneDetailScreen(
                sceneId = cenarioAberto!!,
                viewModel = viewModel,
                onBack = { cenarioAberto = null },
            )
        }
        else -> StoreBrowser(
            viewModel = viewModel,
            onAbrir = { cenarioAberto = it },
            onVerPremium = { premiumAberto = true },
        )
    }
}

@Composable
private fun StoreBrowser(viewModel: MainViewModel, onAbrir: (String) -> Unit, onVerPremium: () -> Unit) {
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
                    onVerPremium = onVerPremium,
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
internal fun PremiumBanner(isPremium: Boolean, priceText: String?, onVerPremium: () -> Unit) {
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
            // Leva pra tela de Premium em vez de disparar a compra daqui. O
            // argumento de venda é ver os efeitos na cena — texto não compete
            // com isso. E, ao contrário do botão de compra, este funciona mesmo
            // sem o Play responder: dá pra conhecer o produto offline.
            Button(onClick = onVerPremium, shape = RoundedCornerShape(Radius.pill)) {
                Text("Ver o que muda")
            }
            Spacer(Modifier.height(Spacing.sm))
            Text(
                priceText?.let { "Compra única de $it. Ou compre só o cenário que quiser, dentro dele." }
                    ?: "Ou compre só o cenário que quiser, dentro dele.",
                style = MaterialTheme.typography.bodySmall,
                color = onContainer.copy(alpha = 0.7f),
            )
        }
    }
}
