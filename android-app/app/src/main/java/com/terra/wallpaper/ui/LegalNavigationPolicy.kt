package com.terra.wallpaper.ui

import com.terra.wallpaper.ui.components.Suporte
import java.net.URI

internal enum class LegalNavigationAction { LOCAL, EXTERNAL_WEB, EXTERNAL_MAIL, BLOCK }

/** #86, 10/11/2026: destinos exatos; novas fontes legais exigem rever esta fronteira. */
internal object LegalNavigationPolicy {
    private val pages = setOf("privacidade", "termos", "contato")
    internal val externalLinks = setOf(
        "https://terrawallpaper.com.br/privacidade",
        "https://terrawallpaper.com.br/termos",
        "https://terrawallpaper.com.br/contato",
        "https://creativecommons.org/licenses/by/4.0/",
        "https://api.met.no/doc/TermsOfService",
        "https://policies.google.com/privacy?hl=pt-BR",
        "https://support.google.com/googleplay/answer/2479637?hl=pt-BR",
        "https://www.cloudflare.com/cloudflare-customer-dpa/",
        "https://www.cloudflare.com/privacypolicy/",
        "https://www.cloudflare.com/service-specific-terms-developer-platform/",
        "https://www.gov.br/anpd",
        "https://www.gov.br/anpd/pt-br/acesso-a-informacao/institucional/atos-normativos/regulamentacoes_anpd/resolucao-cd-anpd-no-2-de-27-de-janeiro-de-2022",
    )

    private fun isLocal(uri: URI): Boolean {
        if (uri.scheme != "file" || uri.rawAuthority != null || uri.rawQuery != null ||
            uri.rawPath == null || '%' in uri.rawPath || '\\' in uri.rawPath) return false
        val path = uri.normalize().rawPath
        return pages.any { path == "/android_asset/legal/$it/" || path == "/android_asset/legal/$it/index.html" }
    }

    fun isLocalPage(url: String): Boolean = try {
        isLocal(URI(url))
    } catch (_: java.net.URISyntaxException) { false }

    fun localDestination(url: String, currentUrl: String): String {
        val target = URI(currentUrl).resolve(URI(url)).normalize()
        val path = if (target.rawPath.endsWith('/')) target.rawPath + "index.html" else target.rawPath
        return "file://$path" + (target.rawFragment?.let { "#$it" } ?: "")
    }

    fun decide(url: String, currentUrl: String?, mainFrame: Boolean, gesture: Boolean, redirect: Boolean): LegalNavigationAction {
        if (!mainFrame || redirect) return LegalNavigationAction.BLOCK
        val current = try { currentUrl?.let(::URI) } catch (_: java.net.URISyntaxException) { null }
        if (current == null || !isLocal(current)) return LegalNavigationAction.BLOCK
        val target = try { current.resolve(URI(url)) } catch (_: IllegalArgumentException) {
            return LegalNavigationAction.BLOCK
        } catch (_: java.net.URISyntaxException) { return LegalNavigationAction.BLOCK }
        if (isLocal(target)) return LegalNavigationAction.LOCAL
        if (!gesture) return LegalNavigationAction.BLOCK
        return when (target.toString()) {
            in externalLinks -> LegalNavigationAction.EXTERNAL_WEB
            "mailto:${Suporte.EMAIL}" -> LegalNavigationAction.EXTERNAL_MAIL
            else -> LegalNavigationAction.BLOCK
        }
    }
}
