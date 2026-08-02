package com.atmosfera.wallpaper.ui

import android.graphics.Color as AndroidColor
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Páginas legais (privacidade, termos, contato) renderizadas de um asset local
 * — o mesmo HTML publicado em `docs/`, copiado pros assets pela task
 * `copiarPaginasLegais` (ver `app/build.gradle`). Local, não remoto: funciona
 * offline e não depende da URL pública existir ainda.
 *
 * JavaScript fica DESLIGADO de propósito: as páginas são texto estático, não
 * precisam, e ligar aumentaria a superfície de ataque à toa. A navegação é
 * presa aos assets — link externo é ignorado em vez de abrir dentro da WebView.
 */
enum class PaginaLegal(val arquivo: String, val titulo: String) {
    PRIVACIDADE("privacidade/index.html", "Política de Privacidade"),
    TERMOS("termos/index.html", "Termos de Uso"),
    CONTATO("contato/index.html", "Contato"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalWebViewScreen(pagina: PaginaLegal, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(pagina.titulo, style = MaterialTheme.typography.titleMedium) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            ),
        )
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    setBackgroundColor(AndroidColor.TRANSPARENT)
                    // allowFileAccess precisa ficar LIGADO: apesar da doc dizer que
                    // file:///android_asset segue acessível com ele desligado, na
                    // prática o WebView atual devolve página em branco. O risco é
                    // contido — JS desligado, navegação presa aos assets abaixo.
                    settings.javaScriptEnabled = false
                    settings.allowFileAccess = true
                    settings.allowContentAccess = false
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: android.webkit.WebResourceRequest?,
                        ): Boolean {
                            // Só navega dentro dos assets; link externo não abre aqui.
                            val url = request?.url?.toString().orEmpty()
                            return !url.startsWith("file:///android_asset/")
                        }
                    }
                }
            },
            update = { view ->
                view.loadUrl("file:///android_asset/legal/${pagina.arquivo}")
            },
        )
    }
}
