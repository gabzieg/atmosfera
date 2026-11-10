package com.terra.wallpaper.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.io.ByteArrayInputStream

internal class LegalWebViewClient(private val context: Context) : WebViewClient() {
    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? =
        if (request != null && LegalNavigationPolicy.isLocalPage(request.url.toString())) null
        else WebResourceResponse("text/plain", "UTF-8", ByteArrayInputStream(byteArrayOf()))

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (request == null || request.method != "GET") return true
        val currentUrl = view?.url
        val action = LegalNavigationPolicy.decide(
            request.url.toString(), currentUrl, request.isForMainFrame, request.hasGesture(), request.isRedirect,
        )
        if (action == LegalNavigationAction.LOCAL) {
            if (view == null || currentUrl == null) return true
            val destination = LegalNavigationPolicy.localDestination(request.url.toString(), currentUrl)
            if (destination == request.url.toString()) return false
            view.loadUrl(destination)
            return true
        }
        if (action == LegalNavigationAction.BLOCK) return true
        val intent = if (action == LegalNavigationAction.EXTERNAL_MAIL) {
            Intent(Intent.ACTION_SENDTO, request.url)
        } else {
            Intent(Intent.ACTION_VIEW, request.url).addCategory(Intent.CATEGORY_BROWSABLE)
        }
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            unavailable()
        } catch (_: SecurityException) {
            unavailable()
        }
        return true
    }

    private fun unavailable() {
        Toast.makeText(context, "Não foi possível abrir o aplicativo externo.", Toast.LENGTH_SHORT).show()
    }
}
