package com.atmosfera.wallpaper.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.atmosfera.wallpaper.ui.theme.Radius
import com.atmosfera.wallpaper.ui.theme.Spacing

/**
 * Confirma antes de sair pro seletor do sistema — mostra o mesmo
 * [EngineLivePreview] da tela de detalhe pra fechar o loop de confiança:
 * o usuário viu o cenário se mover na Loja, e vê de novo aqui antes de
 * aplicar de verdade, em vez de pular pro seletor do Android às cegas.
 */
@Composable
fun ConfirmarWallpaperDialog(
    sceneId: String,
    arte: String,
    estilo: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(Radius.card), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                Text(
                    "Aplicar este papel de parede?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(Spacing.md))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(Radius.card)),
                ) {
                    SceneThumbnail(sceneId = sceneId, arte = arte, modifier = Modifier.fillMaxSize())
                    EngineLivePreview(sceneId = sceneId, arte = arte, estilo = estilo, modifier = Modifier.fillMaxSize())
                }
                Spacer(Modifier.height(Spacing.md))
                Text(
                    "É isto que vai aparecer na sua tela.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(Spacing.lg))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(onClick = onConfirm, modifier = Modifier.weight(1f)) { Text("Aplicar") }
                }
            }
        }
    }
}
