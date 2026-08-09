package com.atmosfera.wallpaper.ui.components

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
 * coleção. Se o cenário ainda não tem asset (ex.: fiordes), mostra um
 * placeholder honesto — não a imagem de outro cenário.
 */
@Composable
fun SceneThumbnail(
    sceneId: String,
    modifier: Modifier = Modifier,
    arte: String = "pixel",
    contentScale: ContentScale = ContentScale.Crop,
) {
    val context = LocalContext.current
    var bitmap by remember(sceneId, arte) { mutableStateOf<Bitmap?>(null) }
    var carregando by remember(sceneId, arte) { mutableStateOf(true) }

    LaunchedEffect(sceneId, arte) {
        carregando = true
        bitmap = withContext(Dispatchers.IO) { loadSceneBitmap(context.assets, sceneId, arte) }
        carregando = false
    }

    Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
        val bmp = bitmap
        when {
            bmp != null -> Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
            )
            // Cenário ainda sem arte nos assets (ex.: fiordes) — diz a verdade
            // em vez de mostrar a imagem de outro cenário.
            !carregando -> Text(
                "Em breve",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun loadSceneBitmap(assets: AssetManager, sceneId: String, arte: String): Bitmap? {
    val prefixo = Cenas.por(sceneId).fundoPrefixo(arte)   // ex.: "", "cenas/tanque/", "cenas/cabana_clay/"
    return try {
        assets.open("atmosfera/${prefixo}fundo.png").use { BitmapFactory.decodeStream(it) }
    } catch (e: Exception) {
        null   // cenário sem asset ainda → caller mostra placeholder
    }
}
