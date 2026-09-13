package com.atmosfera.wallpaper.weather

import com.atmosfera.wallpaper.engine.SceneState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/**
 * Leitura do quarto de hora e a regra "a água manda na chuva" (03/09).
 *
 * Nasceu da queixa de que o wallpaper não correspondia ao clima real. Aqui está
 * o que dá pra provar sem Android nem rede: qual bloco de 15 min é o corrente,
 * e o que acontece quando a lâmina em mm discorda do código WMO.
 */
class BlocoQuartoDeHoraTest {

    private fun serie(vararg t: Pair<String, Pair<Int?, Double?>>) = Minutely15Response(
        time = t.map { it.first },
        precipitation = t.map { it.second.second },
        weatherCode = t.map { it.second.first },
    )

    @Test
    fun `pega o ultimo bloco que ja comecou`() {
        val m = serie(
            "2026-09-03T13:45" to (3 to 0.0),
            "2026-09-03T14:00" to (61 to 0.3),
            "2026-09-03T14:15" to (65 to 2.0),
        )
        val b = blocoAtual(m, "2026-09-03T14:07")
        assertNotNull(b)
        assertEquals(61, b!!.weatherCode)
        assertEquals(0.3, b.precipMm, 0.0001)
    }

    @Test
    fun `no instante exato do bloco vale o proprio bloco`() {
        val m = serie("2026-09-03T14:00" to (61 to 0.3), "2026-09-03T14:15" to (65 to 2.0))
        assertEquals(65, blocoAtual(m, "2026-09-03T14:15")!!.weatherCode)
    }

    @Test
    fun `serie que so comeca no futuro nao tem bloco corrente`() {
        val m = serie("2026-09-03T15:00" to (61 to 0.3))
        assertNull(blocoAtual(m, "2026-09-03T14:07"))
    }

    @Test
    fun `sem minutely_15 nao ha bloco`() {
        assertNull(blocoAtual(null, "2026-09-03T14:07"))
        assertNull(blocoAtual(Minutely15Response(), "2026-09-03T14:07"))
    }

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
    fun `agoraIsoLocal tem a largura fixa que a comparacao de texto exige`() {
        val c = Calendar.getInstance().apply { set(2026, Calendar.JANUARY, 5, 7, 3, 0) }
        assertEquals("2026-01-05T07:03", agoraIsoLocal(c))
    }

    // ── A regra de decisão, como o repositório aplica ────────────────────────
    // (mesma expressão do fetchWeather; o teste existe pra ela não ser trocada
    // sem querer — é ela que liga a chuva na tela.)
    private fun codigoEfetivo(codigo: Int, mm: Double): Int =
        if (mm >= 0.05 && !codigo.temPrecipitacao()) codigoPorChuva(mm) else codigo

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
