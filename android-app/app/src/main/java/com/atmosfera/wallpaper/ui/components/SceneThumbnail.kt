package com.atmosfera.wallpaper.ui.components

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.atmosfera.wallpaper.engine.Cenas

/**
 * Carrega e mostra o fundo (fundo.png) de um cenário a partir dos assets,
 * na ARTE escolhida ([arte]: pixel/clay/aqua). Usa o mesmo cálculo de caminho
 * do motor ([Cenas.fundoPrefixo]) pra a miniatura casar com o que é renderizado.
 *
 * Reaproveitado no preview grande da Home, nas miniaturas da Loja e na pilha de
 * coleção. Se o cenário ainda não tem asset, mostra um placeholder honesto —
 * não a imagem de outro cenário.
 *
 * **Decodifica no tamanho em que vai aparecer**, não no tamanho do arquivo. As
 * artes do snapshot de 2026-08 têm ~841×1870 (≈6 MB já descomprimidos, e a
 * pasta inteira passou de 14,7 MB pra ~140 MB); a Loja mostra até 3 delas por
 * card via [StackedThumbnail], em 8 cards. Decodificar tudo em resolução cheia
 * enchia a heap de bitmap que só ia ser desenhado num quinto do tamanho, e a
 * rolagem redecodificava tudo de novo — daí o cache abaixo.
 */
@Composable
fun SceneThumbnail(
    sceneId: String,
    modifier: Modifier = Modifier,
    arte: String = "pixel",
    contentScale: ContentScale = ContentScale.Crop,
) {
    val context = LocalContext.current
    BoxWithConstraints(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        // Largura real do slot, em pixels. Sem limite (dentro de um pai rolável,
        // por exemplo) cai na largura de uma tela grande, que é o pior caso
        // razoável — melhor errar pra cima do que borrar a arte.
        val larguraAlvo = if (constraints.hasBoundedWidth) constraints.maxWidth else 1080

        var bitmap by remember(sceneId, arte, larguraAlvo) { mutableStateOf<Bitmap?>(null) }
        var carregando by remember(sceneId, arte, larguraAlvo) { mutableStateOf(true) }

        LaunchedEffect(sceneId, arte, larguraAlvo) {
            carregando = true
            bitmap = withContext(Dispatchers.IO) {
                carregarFundo(context.assets, sceneId, arte, larguraAlvo)
            }
            carregando = false
        }

        val bmp = bitmap
        when {
            bmp != null -> Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
            )
            // Cenário ainda sem arte nos assets — diz a verdade em vez de
            // mostrar a imagem de outro cenário.
            !carregando -> Text(
                "Em breve",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * O cenário tem arte publicada nos assets? Usa exatamente o mesmo caminho que
 * [carregarFundo], então não há como as duas respostas divergirem.
 *
 * Serve pra Loja esconder cenário que existe no [com.atmosfera.wallpaper.engine.Catalogo]
 * mas ainda não tem asset — antes isso era uma lista fixa no código, que só
 * ficava correta enquanto alguém lembrasse de editá-la a cada snapshot do motor.
 *
 * A comparação de `id` no começo trata o fallback de `Cenas.por()`, que devolve
 * a config da cabana para id desconhecido: sem ela, um cenário novo apontaria
 * pro `fundo.png` da cabana e passaria como "tem asset".
 */
internal fun cenarioTemAsset(assets: AssetManager, sceneId: String): Boolean {
    val cfg = Cenas.por(sceneId)
    if (cfg.id != sceneId) return false
    return try {
        assets.open("atmosfera/${cfg.fundoPrefixo("pixel")}fundo.png").close()
        true
    } catch (e: Exception) {
        false
    }
}

// ── Decodificação e cache ───────────────────────────────────────────────────

/**
 * Teto do cache: um oitavo da heap disponível, no máximo 24 MB. O limite existe
 * porque estas miniaturas convivem com os bitmaps do próprio motor (ver
 * [EngineLivePreview]), que são bem maiores e não passam por aqui.
 */
private val LIMITE_CACHE_BYTES: Int =
    (Runtime.getRuntime().maxMemory() / 8).coerceAtMost(24L * 1024 * 1024).toInt()

/**
 * Miniaturas já decodificadas, por (arte, tamanho pedido).
 *
 * Nada aqui é reciclado: o mesmo [Bitmap] pode estar sendo desenhado por vários
 * composables ao mesmo tempo (a Loja mostra a mesma arte na pilha e no card),
 * e o LruCache só solta a referência — quem recicla é o GC, quando ninguém mais
 * estiver apontando pro bitmap.
 */
private val cacheMiniaturas = object : LruCache<String, Bitmap>(LIMITE_CACHE_BYTES) {
    override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
}

private fun carregarFundo(
    assets: AssetManager,
    sceneId: String,
    arte: String,
    larguraAlvoPx: Int,
): Bitmap? {
    val prefixo = Cenas.por(sceneId).fundoPrefixo(arte)   // ex.: "", "cenas/tanque/", "cenas/cabana_clay/"

    // Arredonda pra múltiplo de 64 pra que larguras quase iguais (as duas colunas
    // da Loja, por exemplo) compartilhem a mesma entrada em vez de decodificar
    // duas vezes quase a mesma coisa.
    val alvo = ((larguraAlvoPx + 63) / 64) * 64
    val chave = "$prefixo@$alvo"
    cacheMiniaturas.get(chave)?.let { return it }

    val caminho = "atmosfera/${prefixo}fundo.png"

    // 1ª passada: só as dimensões do arquivo — não aloca os pixels.
    val medida = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    try {
        assets.open(caminho).use { BitmapFactory.decodeStream(it, null, medida) }
    } catch (e: Exception) {
        return null   // cenário sem asset ainda → caller mostra placeholder
    }
    if (medida.outWidth <= 0) return null

    val opcoes = BitmapFactory.Options().apply {
        inSampleSize = calcularAmostra(medida.outWidth, alvo)
    }
    return try {
        assets.open(caminho).use { BitmapFactory.decodeStream(it, null, opcoes) }
            ?.also { cacheMiniaturas.put(chave, it) }
    } catch (e: Exception) {
        null
    }
}

/**
 * Maior fator de redução que ainda deixa a imagem com pelo menos [larguraAlvo]
 * de largura. Nunca reduz abaixo do alvo — miniatura borrada é pior que memória
 * gasta, e o `inSampleSize` só aceita potência de 2 mesmo.
 */
private fun calcularAmostra(larguraOriginal: Int, larguraAlvo: Int): Int {
    if (larguraAlvo <= 0) return 1
    var amostra = 1
    while (larguraOriginal / (amostra * 2) >= larguraAlvo) amostra *= 2
    return amostra
}
