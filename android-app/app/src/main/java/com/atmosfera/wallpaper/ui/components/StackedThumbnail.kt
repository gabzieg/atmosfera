package com.atmosfera.wallpaper.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.atmosfera.wallpaper.ui.theme.Radius

/**
 * Padrão reproduzido: **efeito de "pilha"/colagem de coleção** — várias imagens
 * levemente sobrepostas/deslocadas formando a prévia de uma pasta/coleção.
 *
 * Adaptação ao Atmosfera: um cenário com **variantes de arte** (a cabana tem
 * pixel/clay/aqua) é a "coleção"; cada arte é uma camada da pilha. Cenário com
 * uma arte só cai no caminho de imagem única (sem pilha).
 */
@Composable
fun StackedThumbnail(
    sceneId: String,
    artes: List<String>,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 0.72f,
) {
    // Uma arte só → imagem única, sem sobreposição (o card em volta recorta os cantos).
    if (artes.size <= 1) {
        SceneThumbnail(
            sceneId = sceneId,
            arte = artes.firstOrNull() ?: "pixel",
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio),
        )
        return
    }

    // Até 3 camadas, alinhadas embaixo. As de trás são mais ESTREITAS e mais
    // ALTAS que a da frente — é isso que faz o topo delas aparecer por cima da
    // borda superior da camada frontal, criando a leitura de "pilha".
    // Desenhadas de trás pra frente; a arte base (artes[0]) fica na frente.
    val camadas = artes.take(3)
    Box(modifier = modifier.fillMaxWidth().aspectRatio(aspectRatio)) {
        for (prof in camadas.indices.reversed()) {       // prof 0 = frente (desenhada por último)
            SceneThumbnail(
                sceneId = sceneId,
                arte = camadas[prof],
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(1f - 0.10f * prof)
                    .fillMaxHeight(0.86f + 0.07f * prof)
                    .shadow(if (prof == 0) 6.dp else 2.dp, RoundedCornerShape(Radius.tile))
                    .clip(RoundedCornerShape(Radius.tile)),
            )
        }
    }
}
