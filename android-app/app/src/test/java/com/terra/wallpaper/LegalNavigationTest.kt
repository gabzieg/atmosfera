package com.terra.wallpaper

import android.content.ActivityNotFoundException
import android.content.ContextWrapper
import android.content.Intent
import com.terra.wallpaper.ui.LegalNavigationPolicy
import com.terra.wallpaper.ui.LegalNavigationAction
import com.terra.wallpaper.ui.components.Suporte
import org.robolectric.shadows.ShadowToast
import android.app.Activity
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import com.terra.wallpaper.ui.LegalWebViewClient
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = android.app.Application::class)
class LegalNavigationTest {
    private val local = "file:///android_asset/legal/termos/index.html"
    private val license = "https://creativecommons.org/licenses/by/4.0/"
    private fun request(url: String, frame: Boolean = true, gesture: Boolean = true, redirect: Boolean = false) = object : WebResourceRequest {
        override fun getUrl() = Uri.parse(url)
        override fun isForMainFrame() = frame
        override fun hasGesture() = gesture
        override fun isRedirect() = redirect
        override fun getMethod() = "GET"
        override fun getRequestHeaders() = emptyMap<String, String>()
    }

    @Test fun `clique explicito da licenca abre navegador externo e consome navegacao`() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val view = object : WebView(activity) { override fun getUrl() = local }
        assertTrue(LegalWebViewClient(activity).shouldOverrideUrlLoading(view, request(license)))
        val intent = shadowOf(activity).nextStartedActivity
        assertNotNull("Clique da licença deve abrir app externo", intent)
        assertEquals(android.content.Intent.ACTION_VIEW, intent.action)
        assertEquals(license, intent.dataString)
        assertTrue(intent.hasCategory(android.content.Intent.CATEGORY_BROWSABLE))
        view.destroy()
    }

    @Test fun `destinos externos exatos exigem gesto frame principal e ausencia de redirect`() {
        for (url in LegalNavigationPolicy.externalLinks + "mailto:${Suporte.EMAIL}") {
            assertTrue(LegalNavigationPolicy.decide(url, local, true, true, false) in
                setOf(LegalNavigationAction.EXTERNAL_WEB, LegalNavigationAction.EXTERNAL_MAIL))
            for (flags in listOf(Triple(false, true, false), Triple(true, false, false), Triple(true, true, true))) {
                assertEquals(url, LegalNavigationAction.BLOCK,
                    LegalNavigationPolicy.decide(url, local, flags.first, flags.second, flags.third))
            }
        }
    }

    @Test fun `recusa esquemas caminhos hosts e consultas nao canonicos`() {
        for (url in listOf("javascript:alert(1)", "data:text/html,x", "intent://x", "content://x",
            "http://creativecommons.org/licenses/by/4.0/", "$license?extra=x", "$license#x",
            "https://creativecommons.org.evil.invalid/licenses/by/4.0/",
            "https://user@creativecommons.org/licenses/by/4.0/", "https://creativecommons.org:443/licenses/by/4.0/",
            "file:///android_asset/elsewhere.html", "file:///data/data/private", "../../../secret",
            "file:///android_asset/legal/%2e%2e/secret", "file:///android_asset/legal/termos/index.html?x=1",
            "mailto:fake@example.invalid", "mailto:${Suporte.EMAIL}?body=extra")) {
            assertEquals(url, LegalNavigationAction.BLOCK, LegalNavigationPolicy.decide(url, local, true, true, false))
        }
        assertEquals(LegalNavigationAction.BLOCK, LegalNavigationPolicy.decide(license, null, true, true, false))
        assertEquals(LegalNavigationAction.BLOCK, LegalNavigationPolicy.decide(license, "https://evil.invalid", true, true, false))
    }

    @Test fun `normaliza somente tres documentos relativos e preserva ancoras`() {
        for (page in listOf("privacidade", "termos", "contato")) {
            val relative = "../$page/#secao"
            assertEquals(LegalNavigationAction.LOCAL, LegalNavigationPolicy.decide(relative, local, true, true, false))
            assertEquals("file:///android_asset/legal/$page/index.html#secao", LegalNavigationPolicy.localDestination(relative, local))
        }
        assertEquals(LegalNavigationAction.LOCAL, LegalNavigationPolicy.decide("#secao", local, true, true, false))
        assertEquals("$local#secao", LegalNavigationPolicy.localDestination("#secao", local))
    }

    @Test fun `mailto usa sendto e nao e carregado na webview`() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val view = object : WebView(activity) { override fun getUrl() = local }
        assertTrue(LegalWebViewClient(activity).shouldOverrideUrlLoading(view, request("mailto:${Suporte.EMAIL}")))
        assertEquals(Intent.ACTION_SENDTO, shadowOf(activity).nextStartedActivity.action)
        view.destroy()
    }

    @Test fun `sem app ou sem permissao falha tratada sem navegacao remota`() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val view = object : WebView(activity) { override fun getUrl() = local }
        for (failure in listOf(ActivityNotFoundException(), SecurityException())) {
            val context = object : ContextWrapper(activity) {
                override fun startActivity(intent: Intent) { throw failure }
            }
            assertTrue(LegalWebViewClient(context).shouldOverrideUrlLoading(view, request(license)))
            assertEquals("Não foi possível abrir o aplicativo externo.", ShadowToast.getTextOfLatestToast())
        }
        view.destroy()
    }

    @Test fun `cliente bloqueia autoload redirect e subframe sem iniciar app`() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val view = object : WebView(activity) { override fun getUrl() = local }
        for (r in listOf(request(license, gesture=false), request(license, frame=false), request(license, redirect=true))) {
            assertTrue(LegalWebViewClient(activity).shouldOverrideUrlLoading(view, r))
            assertNull(shadowOf(activity).nextStartedActivity)
        }
        view.destroy()
    }

    @Test fun `relativo entre documentos carrega index canonico sem abrir app`() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val loaded = mutableListOf<String>()
        val view = object : WebView(activity) {
            override fun getUrl() = local
            override fun loadUrl(url: String) { loaded += url }
        }
        assertTrue(LegalWebViewClient(activity).shouldOverrideUrlLoading(view, request("file:///android_asset/legal/contato/")))
        assertEquals(listOf("file:///android_asset/legal/contato/index.html"), loaded)
        assertNull(shadowOf(activity).nextStartedActivity)
        assertFalse(LegalWebViewClient(activity).shouldOverrideUrlLoading(view, request("$local#secao")))
        view.destroy()
    }

    @Test fun `webview nao busca conteudo remoto nem outros arquivos`() {
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        val client = LegalWebViewClient(activity)
        assertNull(client.shouldInterceptRequest(null, request(local)))
        for (url in listOf(license, "https://evil.invalid/image.png", "file:///data/private", "file:///android_asset/elsewhere")) {
            assertNotNull(client.shouldInterceptRequest(null, request(url)))
        }
    }

    @Test fun `todos os links dos tres assets seguem politica sem acesso a rede`() {
        val directory = java.io.File("src/main/assets/legal")
        for (page in listOf("privacidade", "termos", "contato")) {
            val html = java.io.File(directory, "$page/index.html").readText()
            val origin = "file:///android_asset/legal/$page/index.html"
            val links = Regex("href=\"([^\"]+)\"").findAll(html).map { it.groupValues[1] }
            assertTrue(links.any())
            for (url in links) {
                assertNotEquals("Link do documento precisa de politica explicita", LegalNavigationAction.BLOCK,
                    LegalNavigationPolicy.decide(url, origin, true, true, false))
            }
        }
    }
}
