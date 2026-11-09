package com.terra.wallpaper.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.terra.wallpaper.engine.Acervo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext

/** Thumbnail imediata; apenas a variante selecionada recebe a prévia intermediária. */
@Composable
fun ScenePreview(sceneId: String, arte: String, remota: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext
    val lifecycleOwner = LocalLifecycleOwner.current
    var imagem by remember(sceneId, arte) { mutableStateOf<Bitmap?>(null) }
    var carregando by remember(sceneId, arte) { mutableStateOf(remota) }
    LaunchedEffect(sceneId, arte, remota, lifecycleOwner) {
        carregando = remota
        if (remota) {
            lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                Acervo.reterPrevia(context, sceneId, arte)
                try {
                    carregando = true
                    val arquivo = Acervo.preview(context, sceneId, arte)
                    imagem = withContext(Dispatchers.IO) { arquivo?.let { BitmapFactory.decodeFile(it.path) } }
                    carregando = false
                    awaitCancellation()
                } finally {
                    Acervo.soltarPrevia(context, sceneId, arte)
                    imagem = null
                    carregando = false
                }
            }
        }
        carregando = false
    }
    Box(modifier) {
        SceneThumbnail(sceneId = sceneId, arte = arte, modifier = Modifier.fillMaxSize())
        imagem?.let { Image(it.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
        if (carregando) Text("Carregando prévia…", modifier = Modifier.align(Alignment.BottomCenter))
    }
}
