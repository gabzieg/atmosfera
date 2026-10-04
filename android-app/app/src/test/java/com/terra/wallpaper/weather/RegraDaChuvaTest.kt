package com.terra.wallpaper.weather

import com.terra.wallpaper.engine.SceneState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * A regra "a água manda na chuva" (03/09).
 *
 * Nasceu da queixa de que o wallpaper não correspondia ao clima real: o código
 * do tempo é uma categoria e arredonda, a lâmina em mm é o dado bruto. A regra
 * sobreviveu à troca da Open-Meteo pela MET Norway (2026-10-03) — o que mudou
 * foi só de onde vem a mm (hora/4 em vez do bloco de 15 min). Este arquivo se
 * chamava `BlocoQuartoDeHoraTest` e testava também a escolha do bloco de 15 min,
 * que deixou de existir; a escolha do passo da MET está em `MetNorwayTest`.
 */
class RegraDaChuvaTest {

    @Test
    fun `intensidade sai da lamina em mm`() {
        assertEquals(61, codigoPorChuva(0.1))    // garoa
        assertEquals(61, codigoPorChuva(0.39))
        assertEquals(63, codigoPorChuva(0.4))    // moderada
        assertEquals(63, codigoPorChuva(1.49))
        assertEquals(65, codigoPorChuva(1.5))    // forte (6 mm/h)
        assertEquals(65, codigoPorChuva(9.0))
    }

    @Test
    fun `temPrecipitacao separa ceu de agua caindo`() {
        assertFalse(0.temPrecipitacao())   // limpo
        assertFalse(3.temPrecipitacao())   // encoberto
        assertFalse(48.temPrecipitacao())  // névoa
        assertTrue(51.temPrecipitacao())   // garoa
        assertTrue(61.temPrecipitacao())
        assertTrue(75.temPrecipitacao())   // neve
        assertTrue(95.temPrecipitacao())   // trovoada
    }

    @Test
    fun `agua caindo liga a chuva mesmo com codigo de ceu`() {
        // O caso que motivou tudo: garoa fina que o código arredonda pra
        // "encoberto" — o wallpaper ficava seco com chuva na rua.
        assertEquals(61, codigoEfetivo(3, 0.1))
        assertEquals(63, codigoEfetivo(2, 0.5))
        assertEquals(65, codigoEfetivo(0, 3.0))
    }

    @Test
    fun `sem agua o codigo manda e a chuva desliga`() {
        assertEquals(3, codigoEfetivo(3, 0.0))
        assertEquals(0, codigoEfetivo(0, 0.02))   // resíduo abaixo do piso
        assertEquals(61, codigoEfetivo(61, 0.0))  // código de chuva sem mm: respeitado
    }

    @Test
    fun `codigo de chuva com agua nao e reescrito pela lamina`() {
        // 95 é trovoada: a mm não pode rebaixar isso a "chuva fraca".
        assertEquals(95, codigoEfetivo(95, 0.2))
        assertEquals(75, codigoEfetivo(75, 1.0))  // neve continua neve
    }

    @Test
    fun `mm por hora da MET dividida por 4 cai na regua do quarto de hora`() {
        // 0,3 mm/h (pancada fraca real de São Paulo, 04/10) → 0,075 no quarto
        // de hora: acima do piso de 0,05, liga chuva fraca mesmo sob "cloudy".
        assertEquals(61, codigoEfetivo(3, 0.3 / 4))
        // 0,1 mm/h → 0,025: resíduo, a cena fica seca.
        assertEquals(3, codigoEfetivo(3, 0.1 / 4))
        // 6 mm/h → 1,5: forte, igual à régua antiga.
        assertEquals(65, codigoEfetivo(3, 6.0 / 4))
    }

    @Test
    fun `parcialmente nublado poe nuvem sem ligar chuva`() {
        val s = SceneState()
        s.setParcialNublado()
        assertEquals("seco", s.clima)
        assertEquals(6, s.cloudN)                 // era 3 no seco
        assertTrue(s.skyTint.a > 0f)              // véu mínimo
        assertTrue(s.skyTint.a < 0.5f)            // mas não o do nublado (0.90)
        assertFalse(s.raios)
    }
}
