package com.atmosfera.wallpaper.ui.components

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Carrega e mostra o fundo (fundo.png) de um cenário a partir dos assets.
 * Reaproveitado no preview grande da Home e nas miniaturas da Loja — antes
 * essa lógica de carregamento existia duplicada só dentro da Home.
 */
@Composable
fun SceneThumbnail(
    sceneId: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val context = LocalContext.current
    var bitmap by remember(sceneId) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(sceneId) {
        bitmap = withContext(Dispatchers.IO) { loadSceneBitmap(context.assets, sceneId) }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val bmp = bitmap
        if (bmp != null) {
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp,
            )
        }
    }
}

private fun loadSceneBitmap(assets: AssetManager, sceneId: String): Bitmap? {
    val path = if (sceneId == "cabana") "atmosfera/fundo.png" else "atmosfera/cenas/$sceneId/fundo.png"
    return try {
        assets.open(path).use { BitmapFactory.decodeStream(it) }
    } catch (e: Exception) {
        try {
            assets.open("atmosfera/fundo.png").use { BitmapFactory.decodeStream(it) }
        } catch (e2: Exception) {
            null
        }
    }
}
