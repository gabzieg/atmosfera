package com.atmosfera.wallpaper.ui

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.atmosfera.wallpaper.service.AtmosferaWallpaperService
import com.atmosfera.wallpaper.ui.components.EngineLivePreview
import com.atmosfera.wallpaper.ui.components.SceneThumbnail
import com.atmosfera.wallpaper.ui.theme.Radius
import com.atmosfera.wallpaper.ui.theme.Spacing
import com.atmosfera.wallpaper.weather.WeatherCondition
import kotlinx.coroutines.launch

/**
 * Onboarding de primeira abertura — três passos, mostrados uma única vez.
 *
 * Existe para resolver a maior causa de desinstalação em live wallpaper: o
 * usuário instala, não entende que precisa APLICAR o papel de parede, conclui
 * que o app não faz nada e remove. Por isso a ordem é: mostrar a cena rodando →
 * aplicar → só então pedir a localização. Antes, o primeiro contato do app era
 * um pedido de permissão, ou seja, cobrança antes de entrega.
 *
 * Sem padrão escuro por decisão de design: "Pular" aparece desde o passo 1, e
 * recusar a permissão tem o mesmo peso visual de aceitar.
 */
@Composable
fun OnboardingScreen(viewModel: MainViewModel, onConcluir: () -> Unit) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 3 })
    val escopo = rememberCoroutineScope()

    val sceneId by viewModel.currentSceneId.collectAsState()
    val arte by viewModel.currentArt.collectAsState()
    val estilo by viewModel.currentEffectStyle.collectAsState()
    val weather by viewModel.weatherState.collectAsState()

    fun concluir() {
        Onboarding.marcarConcluido(context)
        onConcluir()
    }

    fun irPara(pagina: Int) = escopo.launch { pagerState.animateScrollToPage(pagina) }

    // Volta do seletor do Android: se o wallpaper foi mesmo aplicado, avança
    // sozinho. Se voltou sem aplicar, fica onde está — sem erro, sem bloqueio.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_RESUME &&
                pagerState.currentPage == 1 &&
                wallpaperAtivo(context)
            ) {
                irPara(2)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter,
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.widthIn(max = 600.dp).fillMaxSize(),
        ) { pagina ->
            when (pagina) {
                0 -> PassoCena(
                    sceneId = sceneId,
                    arte = arte,
                    estilo = estilo,
                    condicao = weather?.condition,
                    onAvancar = { irPara(1) },
                    onPular = { concluir() },
                )
                1 -> PassoAplicar(
                    sceneId = sceneId,
                    arte = arte,
                    estilo = estilo,
                    onAplicar = { abrirSeletorWallpaper(context) },
                    onDepois = { irPara(2) },
                    onPular = { concluir() },
                )
                else -> PassoLocalizacao(
                    sceneId = sceneId,
                    arte = arte,
                    viewModel = viewModel,
                    onConcluir = { concluir() },
                )
            }
        }
    }
}

// ── Passo 1 — a cena ────────────────────────────────────────────────────────

@Composable
private fun PassoCena(
    sceneId: String,
    arte: String,
    estilo: String,
    condicao: WeatherCondition?,
    onAvancar: () -> Unit,
    onPular: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        SceneThumbnail(sceneId = sceneId, arte = arte, modifier = Modifier.fillMaxSize())
        EngineLivePreview(sceneId, arte, estilo, modifier = Modifier.fillMaxSize())

        // Gradiente só para garantir leitura do texto sobre a cena.
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.55f),
                    0.26f to Color.Transparent,
                    0.42f to Color.Transparent,
                    0.78f to Color.Black.copy(alpha = 0.88f),
                    1f to MaterialTheme.colorScheme.background,
                )
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.lg, vertical = Spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PilulaAoVivo()
            TextButton(onClick = onPular) {
                Text("Pular", color = Color.White.copy(alpha = 0.75f), fontSize = 14.sp)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Kicker("ATMOSFERA")
            Text(
                tituloPara(condicao),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "Isto não é uma imagem: a cena é desenhada quadro a quadro e segue o " +
                    "clima, a hora e o vento da sua região.",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
            BotaoPrimario("Quero isso na minha tela", onClick = onAvancar)
            Pontos(atual = 0)
        }
    }
}

/**
 * O título afirma o clima que está na tela. Se o clima real for outro, afirmar
 * "está chovendo" com sol na cena destruiria a credibilidade logo na primeira
 * frase — que é justamente o que esta tela precisa construir.
 */
private fun tituloPara(condicao: WeatherCondition?): String = when (condicao) {
    null -> "A cena que muda com o tempo lá fora."
    WeatherCondition.SNOW -> "Está nevando aí fora. Está nevando aqui dentro."
    WeatherCondition.SUNNY -> "O sol está batendo aí fora. E aqui dentro também."
    WeatherCondition.CLEAR_NIGHT -> "A noite está limpa aí fora. E aqui dentro também."
    WeatherCondition.FOGGY -> "Tem névoa aí fora. Tem névoa aqui dentro."
    WeatherCondition.CLOUDY, WeatherCondition.PARTLY_CLOUDY ->
        "Está nublado aí fora. Está nublado aqui dentro."
    else -> "Está chovendo aí fora. Está chovendo aqui dentro."
}

// ── Passo 2 — aplicar ───────────────────────────────────────────────────────

@Composable
private fun PassoAplicar(
    sceneId: String,
    arte: String,
    estilo: String,
    onAplicar: () -> Unit,
    onDepois: () -> Unit,
    onPular: () -> Unit,
) {
    // Rolável: hero de 300dp + texto + cartão + dois botões estoura a altura de
    // um celular comum. Sem isto o último botão e o indicador de pontos ficavam
    // cortados pela borda de baixo — confirmado no emulador.
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.md),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onPular) {
                Text(
                    "Pular",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                )
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = Spacing.lg)
                .clip(RoundedCornerShape(Radius.tile)),
        ) {
            SceneThumbnail(sceneId = sceneId, arte = arte, modifier = Modifier.fillMaxSize())
            EngineLivePreview(sceneId, arte, estilo, modifier = Modifier.fillMaxSize())
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Kicker("PASSO 2 DE 3")
            Text(
                "Agora é só aplicar",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "O Android assume daqui. Vai abrir uma tela que não é do Atmosfera — " +
                    "é assim mesmo, e são dois toques:",
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(Radius.card)),
            ) {
                LinhaNumerada(
                    numero = "1",
                    titulo = "Toque em ",
                    destaque = "Definir papel de parede",
                    subtitulo = "A prévia que aparecer já é a cena rodando.",
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                )
                LinhaNumerada(
                    numero = "2",
                    titulo = "Escolha ",
                    destaque = "Tela inicial e de bloqueio",
                    subtitulo = "Assim a cena aparece nos dois lugares. Dá para mudar depois.",
                )
            }

            // Sem Spacer(weight) aqui: dentro de coluna rolável a altura é
            // infinita e o peso empurraria os botões pra fora da tela.
            BotaoPrimario("Abrir seletor do Android", onClick = onAplicar)
            BotaoTexto("Depois, na tela Início", onClick = onDepois)
            Pontos(atual = 1)
            Spacer(Modifier.height(Spacing.sm))
        }
    }
}

@Composable
private fun LinhaNumerada(numero: String, titulo: String, destaque: String, subtitulo: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                numero,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                buildAnnotatedString {
                    append(titulo)
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(destaque) }
                },
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                subtitulo,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// ── Passo 3 — localização ───────────────────────────────────────────────────

@Composable
private fun PassoLocalizacao(
    sceneId: String,
    arte: String,
    viewModel: MainViewModel,
    onConcluir: () -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedida ->
        if (concedida) viewModel.onPermissionGranted() else viewModel.onPermissionDenied()
        onConcluir()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(Radius.card)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(26.dp),
            )
        }
        Kicker("ÚLTIMO PASSO")
        Text(
            "Deixar a cena seguir o seu clima",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            "O Atmosfera usa a localização aproximada só para consultar o clima. " +
                "Nada sai do aparelho além disso.",
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // O par de cartões é o argumento da tela: mostra que o app funciona sem
        // a permissão. É o oposto de coagir — e é o que torna o "sim" confiável.
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            CartaoPermissao(
                sceneId = sceneId,
                arte = arte,
                titulo = "Com permissão",
                corpo = "Chuva na cena quando chove na sua rua.",
                destaque = true,
                modifier = Modifier.weight(1f),
            )
            CartaoPermissao(
                sceneId = sceneId,
                arte = arte,
                titulo = "Sem permissão",
                corpo = "Funciona igual, com o clima de Guarapuava, PR.",
                destaque = false,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(Modifier.height(Spacing.xs))

        BotaoPrimario("Permitir localização") {
            launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        BotaoSecundario("Continuar com a cidade padrão", onClick = onConcluir)
        Pontos(atual = 2)
    }
}

@Composable
private fun CartaoPermissao(
    sceneId: String,
    arte: String,
    titulo: String,
    corpo: String,
    destaque: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(Radius.card))
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SceneThumbnail(
            sceneId = sceneId,
            arte = arte,
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(Radius.card)),
        )
        Text(
            titulo,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (destaque) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onTertiaryContainer
            },
        )
        Text(
            corpo,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── Peças compartilhadas ────────────────────────────────────────────────────

@Composable
private fun PilulaAoVivo() {
    Row(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(Radius.pill))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).background(Color.White.copy(alpha = 0.9f), CircleShape))
        Text(
            "AO VIVO",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.3.sp,
            color = Color.White,
        )
    }
}

@Composable
private fun Kicker(texto: String) {
    Text(
        texto,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun BotaoPrimario(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(Radius.card),
    ) {
        Text(texto, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun BotaoSecundario(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(Radius.card)),
        shape = RoundedCornerShape(Radius.card),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        ),
    ) {
        Text(texto, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BotaoTexto(texto: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
    ) {
        Text(
            texto,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Indicador de 3 páginas: o ativo cresce em largura, sem cor de acento. */
@Composable
private fun Pontos(atual: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(3) { indice ->
            val ativo = indice == atual
            val largura by animateDpAsState(if (ativo) 18.dp else 6.dp, label = "ponto")
            Box(
                Modifier
                    .padding(horizontal = 3.dp)
                    .width(largura)
                    .height(6.dp)
                    .background(
                        if (ativo) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        RoundedCornerShape(3.dp),
                    )
            )
        }
    }
}

// ── Utilidades ──────────────────────────────────────────────────────────────

internal fun wallpaperAtivo(context: Context): Boolean {
    val info = WallpaperManager.getInstance(context).wallpaperInfo
    return info != null && info.packageName == context.packageName
}

internal fun abrirSeletorWallpaper(context: Context) {
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
