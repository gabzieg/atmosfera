package com.terra.wallpaper.ui

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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.terra.wallpaper.engine.Catalogo
import com.terra.wallpaper.engine.Estilos
import com.terra.wallpaper.engine.Acervo
import android.app.Activity
import androidx.compose.material3.LinearProgressIndicator
import com.terra.wallpaper.service.AtmosferaWallpaperService
import androidx.compose.material3.TextButton
import com.terra.wallpaper.ui.components.ConfirmarWallpaperDialog
import com.terra.wallpaper.ui.components.ReportarProblemaDialog
import com.terra.wallpaper.ui.components.EngineLivePreview
import com.terra.wallpaper.ui.components.SceneThumbnail
import com.terra.wallpaper.ui.components.ScenePreview
import com.terra.wallpaper.ui.theme.Radius
import com.terra.wallpaper.ui.theme.Spacing

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
fun SceneDetailScreen(sceneId: String, viewModel: MainViewModel, onBack: () -> Unit, onVerPremium: (String?) -> Unit,
    onAbrirCenario: (String) -> Unit) {
    val cenario = Catalogo.por(sceneId) ?: return
    val currentSceneId by viewModel.currentSceneId.collectAsState()
    val currentArt by viewModel.currentArt.collectAsState()
    val currentEffectStyle by viewModel.currentEffectStyle.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val compras by viewModel.artesCompradas.collectAsState()
    val revisaoAcesso by viewModel.revisaoAcesso.collectAsState()
    val precos by viewModel.billingManager.precos.collectAsState()
    val mensagemCompra by viewModel.billingManager.mensagemPremium.collectAsState()
    val download by viewModel.downloadArte.collectAsState()
    val context = LocalContext.current
    val activity = generateSequence(context) { (it as? android.content.ContextWrapper)?.baseContext }
        .filterIsInstance<Activity>().firstOrNull()

    val isActiveScene = currentSceneId == sceneId
    val artes = artesDoCenario(sceneId)
    // A arte escolhida aqui é PRÉVIA até "Aplicar": a preferência de arte é
    // global, e mexer nela olhando outro cenário trocava a arte do wallpaper que
    // está no ar (e, com arte grátis avulsa, podia pôr no ar uma arte paga).
    var arteExibida by remember(sceneId) { mutableStateOf(viewModel.arteInicial(cenario)) }
    val isUnlocked = remember(cenario, arteExibida, compras, revisaoAcesso) {
        viewModel.isArtUnlocked(cenario, arteExibida)
    }
    val instalada = viewModel.arteInstalada(cenario, arteExibida)
    val produto = Catalogo.produtoArte(cenario, arteExibida)
    val preco = precos[produto]
    val baixando = download.id == "$sceneId/$arteExibida" && download.progresso != null
    val isActive = isUnlocked && isActiveScene && currentArt == arteExibida
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
                // o serviço desenha o par salvo: sem isto ele abria o cenário
                // de antes, não o que está na tela
                if (isUnlocked) {
                    viewModel.aplicar(sceneId, arteExibida)
                    aplicarWallpaper()
                }
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
        // clipToBounds: o Compose hospeda AndroidView com clipChildren=false, e o
        // motor pinta além da altura da View — sem isso a chuva vaza por cima do
        // nome, dos botões e da seção de estilos abaixo.
        Box(modifier = Modifier.fillMaxWidth().height(340.dp).clipToBounds()) {
            ScenePreview(sceneId = sceneId, arte = arteExibida, remota = !instalada, modifier = Modifier.fillMaxSize())
            if (instalada) EngineLivePreview(
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
            if (!isUnlocked) Row(
                modifier = Modifier.align(Alignment.BottomEnd).padding(Spacing.md)
                    .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(Radius.pill)).padding(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Arte não adquirida", tint = Color.White)
                Text("Arte não adquirida", color = Color.White)
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
                    Text(Catalogo.por(Catalogo.familia(sceneId))?.nome ?: cenario.nome,
                        style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(Modifier.width(Spacing.md))
                if (isUnlocked) AcaoPrincipal(
                    isActive = isActive,
                    isUnlocked = isUnlocked,
                    onAplicar = { viewModel.baixarEAplicar(cenario, arteExibida) },
                    textoAplicar = if (instalada) "Aplicar" else "Baixar e aplicar",
                    enabled = !baixando && viewModel.entregaDisponivel(cenario, arteExibida),
                )
                else Button(
                    enabled = produto != null && preco != null && viewModel.entregaDisponivel(cenario, arteExibida) && activity != null,
                    onClick = { if (produto != null && activity != null) viewModel.buyScene(activity, produto) },
                    shape = RoundedCornerShape(Radius.pill),
                ) { Text(if (preco != null) "Comprar $preco" else "Compra indisponível") }
            }

            if (!instalada) {
                val bytes = Acervo.bytesParaBaixar(context, sceneId, arteExibida)
                Text(String.format(java.util.Locale.forLanguageTag("pt-BR"), "Download: %.1f MB", bytes / (1024f * 1024f)))
            }

            // ── Ações secundárias em pílula ─────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                PilulaAcao(
                    texto = "Definir wallpaper",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (isUnlocked) mostrarConfirmacao = true
                    },
                    enabled = isUnlocked && instalada,
                )
                PilulaAcao(
                    icone = Icons.Default.Share,
                    texto = "Compartilhar",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Olha esse cenário do Terra: ${cenario.nome}")
                        }
                        context.startActivity(Intent.createChooser(share, "Compartilhar"))
                    },
                )
            }

            if (!isUnlocked) {
                Text(
                    "Esta arte é uma compra individual e não está incluída no Premium." +
                        if (preco == null || !viewModel.entregaDisponivel(cenario, arteExibida)) " A compra está indisponível no momento." else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (baixando) {
                LinearProgressIndicator(progress = { download.progresso ?: 0f }, modifier = Modifier.fillMaxWidth())
                TextButton(onClick = { viewModel.cancelarDownload() }) { Text("Cancelar download") }
            }
            if (!instalada && !viewModel.entregaDisponivel(cenario, arteExibida)) {
                Text("Download indisponível no momento. As artes já instaladas continuam disponíveis.", style = MaterialTheme.typography.bodySmall)
            }
            download.mensagem?.takeIf { download.id == "$sceneId/$arteExibida" || download.id.isEmpty() }?.let {
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
            mensagemCompra?.let { Text(it, style = MaterialTheme.typography.bodySmall) }

            val ambientes = Catalogo.ambientes(sceneId)
            if (ambientes.size > 1) SecaoDetalhe(titulo = "Ambiente") {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    ambientes.forEach { ambiente ->
                        OutlinedButton(onClick = { onAbrirCenario(ambiente.id) }, enabled = ambiente.id != sceneId) {
                            Text(Catalogo.nomeAmbiente(ambiente.id))
                        }
                    }
                }
            }
            // ── Seção: arte do cenário (só quem tem variantes) ──────────
            if (artes.size > 1) {
                SecaoDetalhe(titulo = "Arte do cenário") {
                    // Rolável: a cabana tinha só 3 artes e cabia na tela sem
                    // rolar, mas cenários novos (ex.: jardim) chegam com até 6 —
                    // sem isto a fileira estourava a largura e o texto do último
                    // item quebrava letra por letra na lateral da tela.
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                    ) {
                        artes.forEach { arte ->
                            ArteOption(
                                sceneId = sceneId,
                                arte = arte,
                                selecionada = arte == arteExibida,
                                bloqueada = !viewModel.isArtUnlocked(cenario, arte),
                                gratis = arte in cenario.artesGratis,
                                onClick = {
                                    arteExibida = arte
                                    // Seleção é apenas prévia; o wallpaper só muda em Aplicar.
                                },
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
                            onClick = {
                                if (estiloId != "pixel" && !isPremium) onVerPremium(estiloId)
                                else viewModel.setEffectStyle(estiloId)
                            },
                            bloqueado = estiloId != "pixel" && !isPremium,
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
    onAplicar: () -> Unit,
    textoAplicar: String = "Aplicar",
    enabled: Boolean = true,
) {
    when {
        isActive && isUnlocked -> Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(Spacing.xs))
            Text("Atual", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
        }
        isUnlocked -> Button(onClick = onAplicar, enabled = enabled, shape = RoundedCornerShape(Radius.pill)) { Text(textoAplicar) }
        else -> Button(
            onClick = {},
            enabled = false,
            shape = RoundedCornerShape(Radius.pill),
        ) {
            Text("Arte indisponível")
        }
    }
}

/** Ação secundária em cápsula (contorno leve). */
@Composable
private fun PilulaAcao(
    texto: String,
    modifier: Modifier = Modifier,
    icone: androidx.compose.ui.graphics.vector.ImageVector? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    OutlinedButton(onClick = onClick, enabled = enabled, shape = RoundedCornerShape(Radius.pill), modifier = modifier) {
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
private fun ArteOption(
    sceneId: String,
    arte: String,
    selecionada: Boolean,
    bloqueada: Boolean,
    gratis: Boolean,
    onClick: () -> Unit,
) {
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (bloqueada) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Bloqueada",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(Modifier.width(2.dp))
            }
            Text(
                if (gratis) "${estiloNome(arte)} · grátis" else estiloNome(arte),
                style = MaterialTheme.typography.labelSmall,
                color = if (selecionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
