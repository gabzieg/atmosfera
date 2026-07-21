package com.atmosfera.wallpaper.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.atmosfera.wallpaper.billing.BillingManager
import com.atmosfera.wallpaper.engine.Catalogo
import com.atmosfera.wallpaper.engine.Cenario
import com.atmosfera.wallpaper.ui.components.SceneThumbnail
import com.atmosfera.wallpaper.ui.components.StatusPill

@Composable
fun StoreTab(viewModel: MainViewModel) {
    val isPremium by viewModel.isPremium.collectAsState()
    val currentSceneId by viewModel.currentSceneId.collectAsState()
    val context = LocalContext.current
    val activity = context as? android.app.Activity

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                "Loja",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        item {
            PremiumBanner(
                isPremium = isPremium,
                priceText = viewModel.billingManager.precoFormatado(BillingManager.PRODUTO_PREMIUM),
                onBuy = { activity?.let { viewModel.buyPremium(it) } },
            )
        }

        item {
            Text(
                "Cenários",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        items(Catalogo.cenarios) { cenario ->
            val isUnlocked = viewModel.isSceneUnlocked(cenario)
            val isActive = currentSceneId == cenario.id
            SceneCard(
                cenario = cenario,
                isUnlocked = isUnlocked,
                isActive = isActive,
                priceText = cenario.productId?.let { viewModel.billingManager.precoFormatado(it) },
                onApply = { viewModel.setScene(cenario.id) },
                onBuy = { cenario.productId?.let { pid -> activity?.let { a -> viewModel.buyScene(a, pid) } } },
            )
        }
    }
}

@Composable
private fun PremiumBanner(isPremium: Boolean, priceText: String?, onBuy: () -> Unit) {
    val container = if (isPremium) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer
    val onContainer = if (isPremium) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = null, tint = onContainer, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isPremium) "Você é Premium" else "Desbloquear efeitos vivos",
                    style = MaterialTheme.typography.titleMedium,
                    color = onContainer,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                if (isPremium) "Raios, vento, vagalumes, fumaça, fases da lua e acúmulo de neve ligados em todos os cenários."
                else "Compra única que liga os efeitos climáticos vivos em TODOS os cenários.",
                style = MaterialTheme.typography.bodyMedium,
                color = onContainer.copy(alpha = 0.9f),
            )
            if (!isPremium) {
                Spacer(Modifier.height(14.dp))
                Button(onClick = onBuy, shape = RoundedCornerShape(14.dp)) {
                    Text("Comprar Premium${priceText?.let { " · $it" } ?: ""}")
                }
            }
        }
    }
}

@Composable
private fun SceneCard(
    cenario: Cenario,
    isUnlocked: Boolean,
    isActive: Boolean,
    priceText: String?,
    onApply: () -> Unit,
    onBuy: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            SceneThumbnail(
                sceneId = cenario.id,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp)),
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(cenario.nome, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(2.dp))
                Text(
                    cenario.descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                )
                Spacer(Modifier.height(8.dp))
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
                }
            }
            Spacer(Modifier.width(8.dp))
            if (!isActive) {
                Button(
                    onClick = if (isUnlocked) onApply else onBuy,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(if (isUnlocked) "Aplicar" else (priceText ?: "Comprar"))
                }
            }
        }
    }
}
