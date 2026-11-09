package com.terra.wallpaper.debug

import org.junit.Assert.*
import org.junit.Test

class ConsultaIdadeTest {
    private class Fonte : FonteSinaisIdade {
        var solicitacoes = 0
        var consultas = 0
        lateinit var acesso: (Int?) -> Unit
        lateinit var falhaAcesso: (Int?) -> Unit
        lateinit var faixa: (FaixaIdade) -> Unit
        lateinit var falhaFaixa: (Int?) -> Unit
        override fun solicitar(resposta: (Int?) -> Unit, falha: (Int?) -> Unit) {
            solicitacoes++; acesso = resposta; falhaAcesso = falha
        }
        override fun consultar(resposta: (FaixaIdade) -> Unit, falha: (Int?) -> Unit) {
            consultas++; faixa = resposta; falhaFaixa = falha
        }
    }
    private class Cenario {
        val fonte = Fonte()
        var texto = ""
        var ocupada = false
        val consulta = ConsultaIdade(fonte) { mensagem, curso -> texto = mensagem; ocupada = curso }
        init { consulta.ativar() }
        fun compartilhar() { consulta.iniciar(); fonte.acesso(1) }
    }

    @Test fun `so acao manual consulta e cliques repetidos nao sobrepoem`() {
        val c = Cenario()
        assertEquals(0, c.fonte.solicitacoes)
        c.consulta.iniciar(); c.consulta.iniciar()
        assertEquals(1, c.fonte.solicitacoes)
        assertEquals(0, c.fonte.consultas)
        assertTrue(c.ocupada)
        c.fonte.acesso(1)
        c.consulta.iniciar()
        assertEquals(1, c.fonte.consultas)
        c.fonte.faixa(FaixaIdade(13, 15, 2))
        assertTrue(c.texto.contains("13 a 15"))
        assertTrue(c.texto.contains("TIER_B"))
        assertFalse(c.ocupada)
        c.consulta.iniciar()
        assertEquals(2, c.fonte.solicitacoes)
    }

    @Test fun `recusa pendencia e estado desconhecido nunca consultam faixa`() {
        for (status in listOf(2, 3, 0, null, 99)) {
            val c = Cenario()
            c.consulta.iniciar(); c.fonte.acesso(status)
            assertEquals(0, c.fonte.consultas)
            assertFalse(c.ocupada)
            assertFalse(c.texto.contains("anos ou mais"))
            assertTrue(c.texto.isNotBlank())
        }
    }

    @Test fun `erro na solicitacao mantem idade desconhecida e permite tentar manualmente`() {
        val c = Cenario()
        c.consulta.iniciar(); c.fonte.falhaAcesso(-9)
        assertEquals(0, c.fonte.consultas)
        assertTrue(c.texto.contains("APP_NOT_OWNED"))
        assertTrue(c.texto.contains("Idade desconhecida"))
        assertFalse(c.ocupada)
        c.consulta.iniciar()
        assertEquals(2, c.fonte.solicitacoes)
    }

    @Test fun `erro na consulta nao exibe faixa anterior nem repete sozinho`() {
        val c = Cenario()
        c.compartilhar(); c.fonte.faixa(FaixaIdade(18, null, 4))
        c.compartilhar(); c.fonte.falhaFaixa(-3)
        assertFalse(c.texto.contains("18 anos"))
        assertTrue(c.texto.contains("conexão"))
        assertEquals(2, c.fonte.consultas)
        assertFalse(c.ocupada)
    }

    @Test fun `limite superior aberto requer limite inferior e nao vira faixa ausente`() {
        assertTrue(FaixaIdade(18, null, 4).descricao().contains("18 anos ou mais"))
        assertTrue(FaixaIdade(16, null, 3).descricao().contains("16 anos ou mais"))
        assertTrue(FaixaIdade(null, null, null).descricao().contains("idade desconhecida"))
        assertTrue(FaixaIdade(null, 18, 4).descricao().contains("incompleta"))
    }

    @Test fun `limites invalidos nao produzem faixa utilizavel`() {
        for (faixa in listOf(FaixaIdade(18, 13, 4), FaixaIdade(-1, 12, 1), FaixaIdade(19, null, 4))) {
            assertTrue(faixa.descricao().contains("inconsistente"))
        }
        assertTrue(FaixaIdade(0, 12, 99).descricao().contains("Origem: não informada"))
    }

    @Test fun `ir ao fundo apaga resultado e voltar nao consulta automaticamente`() {
        val c = Cenario()
        c.compartilhar(); c.fonte.faixa(FaixaIdade(13, 15, 2))
        c.consulta.suspender()
        assertEquals(ConsultaIdade.INICIAL, c.texto)
        c.consulta.ativar()
        assertEquals(ConsultaIdade.INICIAL, c.texto)
        assertEquals(1, c.fonte.solicitacoes)
        assertEquals(1, c.fonte.consultas)
    }

    @Test fun `prompt Play pode ir ao fundo e compartilhamento continua so ao voltar`() {
        val c = Cenario()
        c.consulta.iniciar(); c.consulta.suspender()
        c.fonte.acesso(1)
        assertEquals(0, c.fonte.consultas)
        c.consulta.ativar()
        assertEquals(1, c.fonte.consultas)
        assertEquals(1, c.fonte.solicitacoes) // não abre segundo prompt
        c.fonte.faixa(FaixaIdade(16, 17, 2))
        assertTrue(c.texto.contains("16 a 17"))
    }

    @Test fun `resposta etaria no fundo e descartada sem reaparecer`() {
        val c = Cenario()
        c.compartilhar(); c.consulta.suspender()
        c.fonte.faixa(FaixaIdade(18, null, 4))
        c.consulta.ativar()
        assertEquals(ConsultaIdade.INICIAL, c.texto)
        assertFalse(c.ocupada)
    }

    @Test fun `encerrar ignora acesso e resposta de UI antiga`() {
        val c = Cenario()
        c.consulta.iniciar(); c.consulta.encerrar(); c.fonte.acesso(1)
        assertEquals(0, c.fonte.consultas)
        c.consulta.ativar(); c.consulta.iniciar()
        assertEquals(1, c.fonte.solicitacoes)
        val outro = Cenario()
        outro.compartilhar(); outro.consulta.encerrar()
        outro.fonte.faixa(FaixaIdade(13, 15, 2))
        assertEquals(ConsultaIdade.INICIAL, outro.texto)
    }
}
