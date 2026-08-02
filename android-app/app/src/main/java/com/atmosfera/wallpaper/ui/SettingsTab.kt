package com.atmosfera.wallpaper.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import android.widget.Toast
import com.atmosfera.wallpaper.weather.IntervaloClima

/**
 * Padrão reproduzido: **lista de linhas agrupadas** (ícone + rótulo + trailing),
 * sob um rótulo pequeno em maiúsculas acima de cada grupo — sem parágrafo
 * explicativo nem botão de largura total dentro do card. Ícones sempre em
 * `onSurfaceVariant` sobre `surfaceVariant` (mono, sem cor de marca), pra
 * respeitar a identidade monocromática do app.
 *
 * Navegação interna própria (como a Loja faz com a tela de detalhe): a aba
 * continua sendo "Ajustes", sem mexer no NavHost de topo.
 */
private enum class TelaAjustes { LISTA, TUTORIAIS, PRIVACIDADE, TERMOS, CONTATO }

@Composable
fun SettingsTab(viewModel: MainViewModel) {
    var tela by rememberSaveable { mutableStateOf(TelaAjustes.LISTA) }

    if (tela != TelaAjustes.LISTA) {
        BackHandler { tela = TelaAjustes.LISTA }
    }

    when (tela) {
        TelaAjustes.LISTA -> ListaAjustes(viewModel = viewModel, onNavegar = { tela = it })
        TelaAjustes.TUTORIAIS -> TutoriaisScreen(onBack = { tela = TelaAjustes.LISTA })
        TelaAjustes.PRIVACIDADE -> LegalWebViewScreen(PaginaLegal.PRIVACIDADE) { tela = TelaAjustes.LISTA }
        TelaAjustes.TERMOS -> LegalWebViewScreen(PaginaLegal.TERMOS) { tela = TelaAjustes.LISTA }
        TelaAjustes.CONTATO -> LegalWebViewScreen(PaginaLegal.CONTATO) { tela = TelaAjustes.LISTA }
    }
}

@Composable
private fun ListaAjustes(viewModel: MainViewModel, onNavegar: (TelaAjustes) -> Unit) {
    val context = LocalContext.current
    val intervaloClimaMinutos by viewModel.intervaloClimaMinutos.collectAsState()
    var mostrarSeletorIntervalo by remember { mutableStateOf(false) }
    // Recalculado ao entrar na tela e após limpar, pra não mentir o tamanho.
    var tamanhoCache by remember { mutableStateOf(viewModel.tamanhoCache()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            "Ajustes",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        GrupoAjustes(titulo = "Cenário") {
            LinhaAjuste(
                icone = Icons.Default.Schedule,
                rotulo = "Atualizar clima",
                onClick = { mostrarSeletorIntervalo = true },
            ) {
                Text(
                    "${intervaloClimaMinutos}min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Chevron()
            }
        }

        GrupoAjustes(titulo = "Geral") {
            LinhaAjuste(
                icone = Icons.Default.CleaningServices,
                rotulo = "Limpar cache",
                onClick = {
                    val bytes = viewModel.limparCache()
                    tamanhoCache = viewModel.tamanhoCache()
                    Toast.makeText(context, "Cache limpo (${formatarBytes(bytes)})", Toast.LENGTH_SHORT).show()
                },
            ) {
                Text(
                    formatarBytes(tamanhoCache),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            LinhaAjuste(
                icone = Icons.Default.Restore,
                rotulo = "Restaurar compras",
                onClick = {
                    viewModel.billingManager.restaurar()
                    Toast.makeText(context, "Buscando compras…", Toast.LENGTH_SHORT).show()
                },
            )
        }

        GrupoAjustes(titulo = "Ajuda") {
            LinhaAjuste(
                icone = Icons.AutoMirrored.Filled.HelpOutline,
                rotulo = "Como funciona",
                onClick = { onNavegar(TelaAjustes.TUTORIAIS) },
            ) { Chevron() }
            LinhaAjuste(
                icone = Icons.Default.MailOutline,
                rotulo = "Contato",
                onClick = { onNavegar(TelaAjustes.CONTATO) },
            ) { Chevron() }
        }

        GrupoAjustes(titulo = "Informações") {
            LinhaAjuste(
                icone = Icons.Default.Description,
                rotulo = "Termos de Uso",
                onClick = { onNavegar(TelaAjustes.TERMOS) },
            ) { Chevron() }
            LinhaAjuste(
                icone = Icons.Default.PrivacyTip,
                rotulo = "Política de Privacidade",
                onClick = { onNavegar(TelaAjustes.PRIVACIDADE) },
            ) { Chevron() }
        }

        GrupoAjustes(titulo = "Sobre") {
            LinhaAjuste(
                icone = Icons.Default.Info,
                rotulo = "Atmosfera Live Wallpaper",
            ) {
                Text(
                    "Versão 1.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    if (mostrarSeletorIntervalo) {
        SeletorIntervaloDialog(
            atual = intervaloClimaMinutos,
            onSelecionar = {
                viewModel.setIntervaloClima(it)
                mostrarSeletorIntervalo = false
            },
            onDismiss = { mostrarSeletorIntervalo = false },
        )
    }
}

private fun formatarBytes(bytes: Long): String = when {
    bytes >= 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    bytes >= 1024 -> "${bytes / 1024} KB"
    else -> "$bytes B"
}

@Composable
private fun Chevron() {
    Icon(
        Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/** Rótulo pequeno em maiúsculas + cluster arredondado contendo as linhas. */
@Composable
private fun GrupoAjustes(titulo: String, content: @Composable () -> Unit) {
    Column {
        Text(
            titulo.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column { content() }
        }
    }
}

/** Uma linha do cluster: ícone num box arredondado + rótulo + trailing. */
@Composable
private fun LinhaAjuste(
    icone: ImageVector,
    rotulo: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(
            rotulo,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            trailing()
        }
    }
}

@Composable
private fun SeletorIntervaloDialog(atual: Int, onSelecionar: (Int) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                Text(
                    "Atualizar clima a cada",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
                IntervaloClima.OPCOES_MIN.forEach { minutos ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelecionar(minutos) }
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            "${minutos}min",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        if (minutos == atual) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selecionado",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
