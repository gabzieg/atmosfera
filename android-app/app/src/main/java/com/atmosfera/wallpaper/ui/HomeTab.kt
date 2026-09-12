package com.atmosfera.wallpaper.ui

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.atmosfera.wallpaper.service.AtmosferaWallpaperService
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextAlign
import com.atmosfera.wallpaper.engine.Catalogo
import com.atmosfera.wallpaper.engine.Cenario
import com.atmosfera.wallpaper.ui.components.ConfirmarWallpaperDialog
import com.atmosfera.wallpaper.ui.components.cenarioTemAsset
import com.atmosfera.wallpaper.ui.components.SceneThumbnail
import com.atmosfera.wallpaper.ui.components.StatChip
import com.atmosfera.wallpaper.weather.WeatherState

@Composable
fun HomeTab(viewModel: MainViewModel) {
    val hasLocationPermission by viewModel.hasLocationPermission.collectAsState()
    val weatherState by viewModel.weatherState.collectAsState()
    val currentSceneId by viewModel.currentSceneId.collectAsState()
    val currentArt by viewModel.currentArt.collectAsState()
    val currentEffectStyle by viewModel.currentEffectStyle.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    var mostrarConfirmacao by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Os cenários que o usuário PODE usar agora: grátis + comprados, e só os que
    // têm arte publicada. É o que responde "quais wallpapers eu tenho?" sem
    // mandar ninguém pra Loja — comprar um cenário faz ele aparecer aqui.
    //
    // Recalculado quando `isPremium` ou o cenário atual mudam: são os dois
    // eventos que seguem uma compra ou um destrave, e evitam refazer a checagem
    // de asset (que abre arquivo) a cada recomposição.
    val assets = context.assets
    val meusCenarios = remember(isPremium, currentSceneId) {
        Catalogo.cenarios.filter { cenarioTemAsset(assets, it.id) && viewModel.isSceneUnlocked(it) }
    }

    // Só a permissão aproximada: é a única que o app usa de fato (ver o
    // comentário no AndroidManifest). Pedir a precisa junto mostrava ao usuário
    // um pedido maior do que o necessário.
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedida ->
        if (concedida) viewModel.onPermissionGranted() else viewModel.onPermissionDenied()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            "Atmosfera",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        // Este card deixou de ser o primeiro contato do app — quem pede a
        // permissão agora é o passo 3 do onboarding, depois de já ter mostrado
        // valor. Aqui ele sobrou como FALLBACK: para quem recusou no onboarding,
        // escolheu "continuar com a cidade padrão", ou revogou depois nos
        // ajustes do sistema. Sem ele, essas pessoas não teriam como voltar
        // atrás sem procurar nas configurações do Android.
        if (!hasLocationPermission) {
            PermissionOnboardingCard(
                onClick = {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                }
            )
        } else {
            WeatherHeroCard(
                sceneId = currentSceneId,
                arte = currentArt,
                weatherState = weatherState,
                onRefresh = { viewModel.refreshWeather() },
            )

            if (weatherState != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    StatChip(
                        value = "${weatherState!!.feelsLikeCelsius.toInt()}°C",
                        label = "Sensação",
                        modifier = Modifier.weight(1f),
                    )
                    StatChip(
                        value = "${weatherState!!.windspeedKmh.toInt()} km/h",
                        label = "Vento",
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            MeusCenarios(
                cenarios = meusCenarios,
                atual = currentSceneId,
                arte = currentArt,
                onEscolher = { viewModel.setScene(it) },
            )

            SetWallpaperCta(onDefinir = { mostrarConfirmacao = true })
        }
    }

    if (mostrarConfirmacao) {
        ConfirmarWallpaperDialog(
            sceneId = currentSceneId,
            arte = currentArt,
            estilo = currentEffectStyle,
            onConfirm = {
                mostrarConfirmacao = false
                aplicarWallpaper(context)
            },
            onDismiss = { mostrarConfirmacao = false },
        )
    }
}

private fun aplicarWallpaper(context: Context) {
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

@Composable
private fun PermissionOnboardingCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.Start) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Permissão de localização",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Precisamos da sua localização aproximada para o papel de parede reagir ao clima real da sua região.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text("Conceder permissão")
            }
        }
    }
}

/**
 * Os cenários que o usuário já possui, pra trocar o wallpaper sem sair da Home.
 *
 * Existe porque antes a única forma de trocar de cenário era Loja → card →
 * tela de detalhe → "Aplicar": três níveis, dentro de uma aba chamada "Loja",
 * que o usuário associa a comprar e não a configurar. Quem chegava na Home só
 * conseguia aplicar o cenário que já estivesse selecionado.
 *
 * Mostra só o que é utilizável (grátis + comprado, com arte publicada) — o que
 * ainda não foi comprado continua sendo assunto da Loja. Com um cenário só, a
 * fileira vira uma vitrine da coleção; passa a ser seletor de verdade quando
 * chega o segundo.
 */
@Composable
private fun MeusCenarios(
    cenarios: List<Cenario>,
    atual: String,
    arte: String,
    onEscolher: (String) -> Unit,
) {
    if (cenarios.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "Meus cenários",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            cenarios.forEach { cenario ->
                CenarioOption(
                    cenario = cenario,
                    arte = arte,
                    selecionado = cenario.id == atual,
                    onClick = { onEscolher(cenario.id) },
                )
            }
        }
    }
}

/** Miniatura selecionável de um cenário possuído. */
@Composable
private fun CenarioOption(
    cenario: Cenario,
    arte: String,
    selecionado: Boolean,
    onClick: () -> Unit,
) {
    // Selecionado = borda clara (primary). No tema mono não há cor de acento,
    // então o estado vem de contraste, igual ao resto do design system.
    val borda = if (selecionado) MaterialTheme.colorScheme.primary else Color.Transparent
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.width(96.dp),
    ) {
        SceneThumbnail(
            sceneId = cenario.id,
            arte = arte,
            modifier = Modifier
                .size(width = 96.dp, height = 128.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(2.dp, borda, RoundedCornerShape(14.dp))
                .clickable(onClick = onClick),
        )
        Text(
            cenario.nome,
            style = MaterialTheme.typography.labelSmall,
            color = if (selecionado) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            maxLines = 2,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun WeatherHeroCard(
    sceneId: String,
    arte: String,
    weatherState: WeatherState?,
    onRefresh: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(300.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
            // Miniatura ESTÁTICA de propósito, não por limitação.
            //
            // Já teve um `EngineLivePreview` aqui por cima. Medido com
            // `dumpsys gfxinfo` (Pixel 8): com o motor rodando, a Início
            // desenhava ~300 quadros a cada 12 s com mediana de 26 ms e 86% de
            // jank; a tela de Ajustes, sem prévia, desenha ZERO quadro no mesmo
            // intervalo. Como o motor roda na thread de UI, isso deixava a
            // rolagem e os toques pastosos na tela em que o usuário mais fica —
            // e a Início não precisa vender a cena: quem vende é o Onboarding,
            // o detalhe da Loja e o comparador do Premium, onde a prévia ao
            // vivo continua.
            //
            // Passar `arte` aqui não é detalhe: sem isso o SceneThumbnail caía no
            // default "pixel" e a Home mostrava a arte ERRADA — quem escolhesse
            // Clay ou Aquarela via a mudança na Loja e no diálogo, mas a tela
            // principal seguia exibindo pixel art.
            SceneThumbnail(sceneId = sceneId, arte = arte, modifier = Modifier.fillMaxSize())

            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.35f), CircleShape),
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Atualizar clima", tint = Color.White)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f)),
                        )
                    )
                    .padding(20.dp),
            ) {
                if (weatherState == null) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                        Text("Carregando clima…", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    Column {
                        // Sem emoji de clima: a condição já vem no texto abaixo
                        // ("Nublado"), e emoji colorido furaria a identidade mono.
                        Text(
                            "${weatherState.temperatureCelsius.toInt()}°C",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            weatherState.description,
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SetWallpaperCta(onDefinir: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isWallpaperActive by remember { mutableStateOf(isAtmosferaWallpaperActive(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isWallpaperActive = isAtmosferaWallpaperActive(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (isWallpaperActive) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(16.dp)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Papel de parede ativado",
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    } else {
        Button(
            onClick = onDefinir,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text("Definir papel de parede")
        }
    }
}

private fun isAtmosferaWallpaperActive(context: Context): Boolean {
    val info = WallpaperManager.getInstance(context).wallpaperInfo
    return info != null && info.packageName == context.packageName
}
