package com.terra.wallpaper.billing

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.StandardCharsets
import java.security.KeyPairGenerator
import java.security.Signature
import java.util.Base64

class AssinaturaCompraTest {
    @Test
    fun `so o recibo original assinado pela chave correta concede acesso`() {
        val chaves = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        val outraChave = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        val recibo = """{"productId":"terra_premium","description":"céu"}"""
        val assinatura = Signature.getInstance("SHA1withRSA").run {
            initSign(chaves.private)
            update(recibo.toByteArray(StandardCharsets.UTF_8))
            Base64.getEncoder().encodeToString(sign())
        }
        val publica = Base64.getEncoder().encodeToString(chaves.public.encoded)
        val publicaErrada = Base64.getEncoder().encodeToString(outraChave.public.encoded)

        assertTrue(AssinaturaCompra.valida(publica, recibo, assinatura))
        assertFalse(AssinaturaCompra.valida(publica, recibo.replace("premium", "gratis"), assinatura))
        assertFalse(AssinaturaCompra.valida(publicaErrada, recibo, assinatura))
        assertFalse(AssinaturaCompra.valida("", recibo, assinatura))
        assertFalse(AssinaturaCompra.valida("chave invalida", recibo, assinatura))
        assertFalse(AssinaturaCompra.valida(publica, recibo, "assinatura invalida"))
    }
}
