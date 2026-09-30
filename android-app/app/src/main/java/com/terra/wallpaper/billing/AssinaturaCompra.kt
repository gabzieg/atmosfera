package com.terra.wallpaper.billing

import java.nio.charset.StandardCharsets
import java.security.GeneralSecurityException
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

/** Verifica o JSON assinado devolvido pelo Google Play antes de conceder acesso. */
internal object AssinaturaCompra {
    fun valida(chaveBase64: String, jsonOriginal: String, assinaturaBase64: String): Boolean {
        if (chaveBase64.isBlank() || jsonOriginal.isBlank() || assinaturaBase64.isBlank()) return false
        return try {
            val chave = KeyFactory.getInstance("RSA").generatePublic(
                X509EncodedKeySpec(Base64.getDecoder().decode(chaveBase64))
            )
            val verificador = Signature.getInstance("SHA1withRSA")
            verificador.initVerify(chave)
            verificador.update(jsonOriginal.toByteArray(StandardCharsets.UTF_8))
            verificador.verify(Base64.getDecoder().decode(assinaturaBase64))
        } catch (_: GeneralSecurityException) {
            false
        } catch (_: IllegalArgumentException) {
            false
        }
    }
}
