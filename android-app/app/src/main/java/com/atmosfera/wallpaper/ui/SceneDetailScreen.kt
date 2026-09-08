package com.atmosfera.wallpaper.ui

import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.atmosfera.wallpaper.engine.Catalogo
import com.atmosfera.wallpaper.engine.Estilos
import com.atmosfera.wallpaper.service.AtmosferaWallpaperService
import androidx.compose.material3.TextButton
import com.atmosfera.wallpaper.ui.components.ConfirmarWallpaperDialog
import com.atmosfera.wallpaper.ui.components.ReportarProblemaDialog
import com.atmosfera.wallpaper.ui.components.EngineLivePreview
import com.atmosfera.wallpaper.ui.components.SceneThumbnail
import com.atmosfera.wallpaper.ui.theme.Radius
import com.atmosfera.wallpaper.ui.theme.Spacing

/**
 * Padrão reproduzido: **tela de detalhe de item** — imagem grande no topo; nome
 * + botão de ação principal ao lado; ações secundárias em pílula abaixo; e o
 * conteúdo detalhado organizado em seções.
 *
 * Adaptação honesta: não há "autor" num app de wallpaper, então o lugar do nome
 * do autor é o **nome do cenário**; e curtir/salvar/compartilhar viram
 * aplicar / definir papel de parede / compartilhar (ações reais).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SceneDetailScreen(sceneId: String, viewModel: MainViewModel, onBack: () -> Unit) {
    val cenario = Catalogo.por(sceneId) ?: return
    val currentSceneId by viewModel.currentSceneId.collectAsState()
    val currentArt by viewModel.currentArt.collectAsState()
    val currentEffectStyle by viewModel.currentEffectStyle.collectAsState()
    val context = LocalContext.current
    val activity = context as? android.app.Activity

    val isActive = currentSceneId == sceneId
    val isUnlocked = viewModel.isSceneUnlocked(cenario)
    val artes = artesDoCenario(sceneId)
    val arteExibida = if (artes.contains(currentArt)) currentArt else "pixel"
    var mostrarConfirmacao by remember { mutableStateOf(false) }
    var mostrarReporte by remember { mutableStateOf(false) }

    fun aplicarWallpaper() {
        try {
            context.startActivity(
                Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(context, AtmosferaWallpaperService::class.java),
                )
            )
        } catch (e: Exception) {
            context.startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER))
        }
    }

    if (mostrarReporte) {
        ReportarProblemaDialog(
            sceneId = sceneId,
            sceneNome = cenario.nome,
            arte = arteExibida,
            estilo = currentEffectStyle,
            onDismiss = { mostrarReporte = false },
        )
    }

    if (mostrarConfirmacao) {
        ConfirmarWallpaperDialog(
            sceneId = sceneId,
            arte = arteExibida,
            estilo = currentEffectStyle,
            onConfirm = {
                mostrarConfirmacao = false
                aplicarWallpaper()
            },
            onDismiss = { mostrarConfirmacao = false },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // ── Prévia grande no topo + voltar ───────────────────────────────
        // SceneThumbnail é o fallback estático (mostra na hora, ou "Em breve"
        // se o cenário não tem asset); EngineLivePreview desenha por cima
        // assim que o motor carrega — o usuário vê o cenário se mover de
        // verdade antes de aplicar, não só uma imagem parada.
        Box(modifier = Modifier.fillMaxWidth().height(340.dp)) {
            SceneThumbnail(sceneId = sceneId, arte = arteExibida, modifier = Modifier.fillMaxSize())
            EngineLivePreview(
                sceneId = sceneId,
                arte = arteExibida,
                estilo = currentEffectStyle,
                modifier = Modifier.fillMaxSize(),
            )
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(Spacing.md)
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
        }

        Column(
            modifier = Modifier.padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        ) {
            // ── Nome + ação principal ao lado ───────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("CENÁRIO", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                    Text(cenario.nome, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(Modifier.width(Spacing.md))
                AcaoPrincipal(
                    isActive = isActive,
                    isUnlocked = isUnlocked,
                    priceText = cenario.productId?.let { viewModel.billingManager.precoFormatado(it) },
                    onAplicar = { viewModel.setScene(sceneId) },
                    onComprar = { cenario.productId?.let { pid -> activity?.let { viewModel.buyScene(it, pid) } } },
                )
            }

            // ── Ações secundárias em pílula ─────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                PilulaAcao(
                    texto = "Definir wallpaper",
                    modifier = Modifier.weight(1f),
                    onClick = { mostrarConfirmacao = true },
                )
                PilulaAcao(
                    icone = Icons.Default.Share,
                    texto = "Compartilhar",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Olha esse cenário do Atmosfera: ${cenario.nome}")
                        }
                        context.startActivity(Intent.createChooser(share, "Compartilhar"))
                    },
                )
            }

            // ── Seção: arte do cenário (só quem tem variantes) ──────────
            if (artes.size > 1) {
                SecaoDetalhe(titulo = "Arte do cenário") {
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                        artes.forEach { arte ->
                            ArteOption(
                                sceneId = sceneId,
                                arte = arte,
                                selecionada = arte == arteExibida,
                                onClick = { viewModel.setArt(arte) },
                            )
                        }
                    }
                }
            }

            // ── Seção: estilo dos efeitos (global) ──────────────────────
            // FlowRow: os 4 estilos não cabem numa linha só — quebra em duas
            // em vez de empurrar opções pra fora da tela.
            SecaoDetalhe(titulo = "Estilo dos efeitos") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    Estilos.ids.forEach { estiloId ->
                        EstiloChip(
                            estiloId = estiloId,
                            selecionado = estiloId == currentEffectStyle,
                            onClick = { viewModel.setEffectStyle(estiloId) },
                        )
                    }
                }
            }

            // ── Seção: sobre ────────────────────────────────────────────
            SecaoDetalhe(titulo = "Sobre") {
                Text(
                    cenario.descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    "Reage ao clima real: sol, chuva, neve, névoa, vento, além do ciclo de dia e noite.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                // Discreto de propósito: é a válvula de escape de quem viu um
                // recorte errado, não uma ação que a gente queira estimular.
                TextButton(
                    onClick = { mostrarReporte = true },
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(
                        "Algo errado neste cenário?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun AcaoPrincipal(
    isActive: Boolean,
    isUnlocked: Boolean,
    priceText: String?,
    onAplicar: () -> Unit,
    onComprar: () -> Unit,
) {
    when {
        isActive -> Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(Spacing.xs))
            Text("Atual", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
        }
        isUnlocked -> Button(onClick = onAplicar, shape = RoundedCornerShape(Radius.pill)) { Text("Aplicar") }
        else -> Button(onClick = onComprar, shape = RoundedCornerShape(Radius.pill)) {
            Text("Comprar${priceText?.let { " · $it" } ?: ""}")
        }
    }
}

/** Ação secundária em cápsula (contorno leve). */
@Composable
private fun PilulaAcao(
    texto: String,
    modifier: Modifier = Modifier,
    icone: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit,
) {
    OutlinedButton(onClick = onClick, shape = RoundedCornerShape(Radius.pill), modifier = modifier) {
        if (icone != null) {
            Icon(icone, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(Spacing.sm))
        }
        Text(texto, maxLines = 1)
    }
}

@Composable
private fun SecaoDetalhe(titulo: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        content()
    }
}

/** Miniatura selecionável de uma arte do cenário. */
@Composable
private fun ArteOption(sceneId: String, arte: String, selecionada: Boolean, onClick: () -> Unit) {
    val borda = if (selecionada) MaterialTheme.colorScheme.primary else Color.Transparent
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        SceneThumbnail(
            sceneId = sceneId,
            arte = arte,
            modifier = Modifier
                .size(width = 64.dp, height = 88.dp)
                .clip(RoundedCornerShape(Radius.card))
                .border(2.dp, borda, RoundedCornerShape(Radius.card))
                .clickable(onClick = onClick),
        )
        Text(
            estiloNome(arte),
            style = MaterialTheme.typography.labelSmall,
            color = if (selecionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
