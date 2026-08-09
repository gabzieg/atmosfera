package com.atmosfera.wallpaper.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atmosfera.wallpaper.billing.BillingManager
import com.atmosfera.wallpaper.ui.components.EngineLivePreview
import com.atmosfera.wallpaper.ui.components.SceneThumbnail
import com.atmosfera.wallpaper.ui.theme.Radius
import com.atmosfera.wallpaper.ui.theme.Spacing

/** Os oito efeitos que o Premium liga, na ordem do handoff de design. */
private val EFEITOS_PREMIUM = listOf(
    "Raios", "Rajadas de vento", "Vagalumes", "Fumaça de chaminé",
    "Fases da lua", "Acúmulo de neve", "Estrela cadente", "Lampiões acendendo",
)

/**
 * Tela de venda do Premium: mostra o que o plano liga, em vez de descrever.
 *
 * Antes isto era um parágrafo de texto no banner da Loja listando os efeitos —
 * o momento de conversão inteiro resolvido em prosa, tendo um motor capaz de
 * desenhar cada um deles em movimento.
 *
 * ## Duas diferenças em relação ao handoff de design, e por quê
 *
 * 1. **As duas metades do comparador não desenham partículas idênticas.** O
 *    desenho pedia "a mesma cena, no mesmo instante, com a mesma seed", sendo o
 *    recorte a única diferença. Não é alcançável hoje: cada [EngineLivePreview]
 *    instancia seu próprio `EffectEngine`, e o sorteio das partículas usa
 *    `Random.Default` (global, sem semente) — ver `EffectEngine.rnd`. Duas
 *    instâncias sorteiam chuvas diferentes. Sincronizar exige mexer em
 *    `engine/`, que é área congelada do Rafael.
 *    O que sobrou continua honesto — mesma cena, mesma arte, mesmo clima, e a
 *    diferença real dos efeitos pagos aparece — mas as gotas não casam na
 *    emenda do divisor.
 *
 * 2. **Os oito efeitos não são clicáveis para isolar um de cada vez.** O
 *    handoff chamava isso de "coração da tela". `SceneState` só expõe
 *    `premium: Boolean` global — não há liga/desliga por efeito, então isolar
 *    exigiria também mexer no motor. Preferi uma lista que não promete
 *    interação a chips que não fazem nada ao toque.
 *
 * As duas coisas viram um pedido ao Rafael, não um remendo no front.
 */
@Composable
fun PremiumScreen(viewModel: MainViewModel, onVoltar: () -> Unit) {
    BackHandler(onBack = onVoltar)

    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val sceneId by viewModel.currentSceneId.collectAsState()
    val arte by viewModel.currentArt.collectAsState()
    val estilo by viewModel.currentEffectStyle.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val precos by viewModel.billingManager.precos.collectAsState()

    val preco = precos[BillingManager.PRODUTO_PREMIUM]

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        Box {
            ComparadorPremium(sceneId = sceneId, arte = arte, estilo = estilo)
            IconButton(
                onClick = onVoltar,
                modifier = Modifier
                    .padding(Spacing.md)
                    .size(40.dp)
                    .background(Color.Black.copy(alpha = 0.45f), CircleShape),
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White,
                )
            }
        }

        Text(
            "Arraste o divisor para comparar os dois lados da mesma cena.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.md, start = Spacing.xl, end = Spacing.xl),
        )

        Column(
            modifier = Modifier.padding(Spacing.xl),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Text(
                    "PREMIUM",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "A mesma cena, viva",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 31.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    "Oito efeitos que só existem com o Premium ligado, em todos os cenários.",
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            GradeEfeitos()

            CartaoPreco(preco = preco)

            BotaoCompra(
                isPremium = isPremium,
                preco = preco,
                onComprar = { activity?.let { viewModel.buyPremium(it) } },
            )

            Text(
                "Cenários avulsos são comprados à parte — o Premium liga os efeitos " +
                    "vivos em todos eles.",
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ── Comparador ──────────────────────────────────────────────────────────────

/**
 * Mesma cena lado a lado: à esquerda sem os efeitos pagos, à direita com eles.
 * O divisor é arrastável e o toque em qualquer ponto o move até ali.
 */
@Composable
private fun ComparadorPremium(sceneId: String, arte: String, estilo: String) {
    // Começa em 52% — levemente à direita, pro lado premium aparecer primeiro
    // sem esconder o grátis.
    var divisor by remember { mutableFloatStateOf(0.52f) }
    var largura by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .clip(RoundedCornerShape(0.dp))
            .pointerInput(Unit) {
                largura = size.width.toFloat()
                detectHorizontalDragGestures { mudanca, _ ->
                    divisor = (mudanca.position.x / largura).coerceIn(0.06f, 0.94f)
                }
            },
    ) {
        SceneThumbnail(sceneId = sceneId, arte = arte, modifier = Modifier.fillMaxSize())

        // Camada de baixo: sem os efeitos pagos.
        EngineLivePreview(sceneId, arte, estilo, modifier = Modifier.fillMaxSize(), premium = false)

        // Camada de cima: com os efeitos pagos, recortada a partir do divisor.
        // `drawWithContent` + clipRect é o que faz o recorte seguir o dedo sem
        // recompor a View do motor — recompor derrubaria a animação a cada pixel.
        EngineLivePreview(
            sceneId, arte, estilo,
            premium = true,
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    clipRect(left = size.width * divisor) { this@drawWithContent.drawContent() }
                },
        )

        LinhaDivisora(divisor)

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            RotuloComparador("GRÁTIS", ativo = false)
            RotuloComparador("PREMIUM", ativo = true)
        }
    }
}

@Composable
private fun LinhaDivisora(fracao: Float) {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .drawWithContent {
                    val x = size.width * fracao
                    drawContent()
                    drawRect(
                        color = Color.White.copy(alpha = 0.9f),
                        topLeft = Offset(x - 1.dp.toPx(), 0f),
                        size = Size(2.dp.toPx(), size.height),
                    )
                }
        )
    }
}

/** Estado ativo por INVERSÃO (preenchimento claro), nunca por cor de acento. */
@Composable
private fun RotuloComparador(texto: String, ativo: Boolean) {
    Text(
        texto,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.3.sp,
        color = if (ativo) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        modifier = Modifier
            .background(
                if (ativo) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.Black.copy(alpha = 0.6f)
                },
                RoundedCornerShape(Radius.pill),
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

// ── Corpo ───────────────────────────────────────────────────────────────────

@Composable
private fun GradeEfeitos() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        EFEITOS_PREMIUM.chunked(2).forEach { par ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm), modifier = Modifier.fillMaxWidth()) {
                par.forEach { efeito ->
                    Text(
                        efeito,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                    )
                }
                if (par.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CartaoPreco(preco: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(Radius.card))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Preço vem do Play (ProductDetails), formatado pela locale — nunca fixo
        // no código. Sem resposta da loja não inventa valor E não mostra traço no
        // lugar: um "—" grande ao lado de "uma vez só" lê como defeito de
        // renderização, não como informação ausente. O motivo real da ausência já
        // está dito, uma vez só, embaixo do botão desabilitado.
        if (preco != null) {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Text(
                    preco,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    "uma vez só",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }

            Box(Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outline))
        }

        LinhaCheck("Não é assinatura.", " Sem mensalidade, sem renovação, sem cobrança futura.")
        LinhaCheck("Vale em todos os cenários,", " inclusive nos que ainda vão sair.")
        LinhaCheck("O app não tem anúncios", " — nem no plano grátis.")
    }
}

@Composable
private fun LinhaCheck(destaque: String, resto: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Icon(
            Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.size(18.dp),
        )
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)) {
                    append(destaque)
                }
                append(resto)
            },
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
        )
    }
}

@Composable
private fun BotaoCompra(isPremium: Boolean, preco: String?, onComprar: () -> Unit) {
    when {
        isPremium -> Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(Radius.card)),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(Spacing.sm))
            Text(
                "Premium ativo neste aparelho",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }

        // Sem preço = o Play não respondeu. Botão desabilitado COM o motivo à
        // vista; nunca um botão que parece clicável e não faz nada.
        preco == null -> Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(Radius.card),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            ) {
                Text("Comprar Premium", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    "Indisponível agora: o Google Play não respondeu. Sem conexão, ou o " +
                        "produto ainda não foi publicado. A compra volta sozinha quando a " +
                        "loja responder.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        else -> Button(
            onClick = onComprar,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(Radius.card),
        ) {
            Text("Comprar Premium · $preco", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
