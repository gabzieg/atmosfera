package com.atmosfera.wallpaper

import android.content.res.AssetManager
import com.atmosfera.wallpaper.engine.FonteDeAssets

/**
 * Adapta o conteúdo embarcado no APK para a [FonteDeAssets] que o motor pede.
 *
 * **Mora no front, e não no `engine/`, de propósito.** O motor não deve conhecer
 * `AssetManager` — é justamente essa dependência que impediria ele de ler
 * conteúdo baixado sob demanda. `ContratoFonteDeAssetsTest` trava isso: qualquer
 * menção a `AssetManager` dentro de `engine/` quebra o gate.
 *
 * Quando a Fase 4 do `ROADMAP.md` entrar, o par desta função nasce aqui do lado
 * — algo como `AssetPackLocation.comoFonte()` — e o motor não muda uma linha.
 */
fun AssetManager.comoFonte(): FonteDeAssets = FonteDeAssets { caminho -> open(caminho) }
