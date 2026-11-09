package com.terra.wallpaper.debug

import com.google.android.play.agesignals.model.AgeRangeSource
import com.google.android.play.agesignals.model.AgeSignalsErrorCode
import com.google.android.play.agesignals.model.AgeSignalsStatus

/** Fronteira do SDK: somente os três campos necessários saem da resposta da Play. */
internal interface FonteSinaisIdade {
    fun solicitar(resposta: (Int?) -> Unit, falha: (Int?) -> Unit)
    fun consultar(resposta: (FaixaIdade) -> Unit, falha: (Int?) -> Unit)
}

internal data class FaixaIdade(val inferior: Int?, val superior: Int?, val origem: Int?) {
    fun descricao(): String {
        val faixa = when {
            inferior == null && superior == null -> "Faixa não informada; idade desconhecida."
            inferior == null || inferior !in 0..18 ||
                (superior != null && (superior !in 2..18 || superior < inferior)) ->
                "Faixa incompleta ou inconsistente; idade desconhecida."
            superior == null -> "Faixa: $inferior anos ou mais (limite superior aberto)."
            else -> "Faixa: $inferior a $superior anos (limites inclusivos)."
        }
        val fonte = when (origem) {
            AgeRangeSource.TIER_A -> "autodeclarada (TIER_A)"
            AgeRangeSource.TIER_B -> "gerida por responsável (TIER_B)"
            AgeRangeSource.TIER_C -> "avaliada (TIER_C)"
            AgeRangeSource.TIER_D -> "verificada (TIER_D)"
            else -> "não informada ou desconhecida"
        }
        return "SHARED — compartilhamento permitido.\n$faixa\nOrigem: $fonte.\n" +
            "Este diagnóstico não autoriza compras nem comprova maioridade por si só."
    }
}

/** Uma ação manual; sem cache, repetição automática ou autorização do produto. */
internal class ConsultaIdade(
    private val fonte: FonteSinaisIdade,
    private val mostrar: (String, Boolean) -> Unit,
) {
    private var ativa = false
    private var encerrada = false
    private var geracao = 0
    private var emCurso = false
    private var consultaPendente = false

    fun ativar() {
        if (encerrada) return
        ativa = true
        if (consultaPendente) {
            consultaPendente = false
            consultar(geracao)
        } else mostrar(if (emCurso) "Aguardando resposta da Play…" else INICIAL, emCurso)
    }

    fun suspender() {
        ativa = false
        mostrar(INICIAL, emCurso) // apaga qualquer faixa antes da tela ir ao fundo
    }

    fun encerrar() {
        suspender()
        encerrada = true
        consultaPendente = false
        geracao++
    }

    fun iniciar() {
        if (!ativa || encerrada || emCurso) return
        emCurso = true
        val tentativa = ++geracao
        mostrar("Solicitando compartilhamento à Play…", true)
        fonte.solicitar({ status ->
            if (vigente(tentativa)) when (status) {
                AgeSignalsStatus.SHARED -> if (ativa) consultar(tentativa) else consultaPendente = true
                AgeSignalsStatus.NOT_SHARED -> terminar(tentativa, "NOT_SHARED — faixa não compartilhada. Idade desconhecida.")
                AgeSignalsStatus.VERIFICATION_REQUIRED -> terminar(tentativa, "VERIFICATION_REQUIRED — resolva a verificação na Play Store e tente novamente.")
                else -> terminar(tentativa, "Estado de compartilhamento desconhecido. Nenhuma faixa foi consultada.")
            }
        }, { codigo -> erro(tentativa, "solicitar compartilhamento", codigo) })
    }

    private fun consultar(tentativa: Int) {
        mostrar("Compartilhamento permitido. Consultando faixa…", true)
        fonte.consultar({ faixa ->
            // Não formatar nem conservar resultado recebido com a tela ao fundo.
            terminar(tentativa, if (ativa && vigente(tentativa)) faixa.descricao() else INICIAL)
        }, { codigo -> erro(tentativa, "consultar faixa", codigo) })
    }

    private fun vigente(tentativa: Int) = !encerrada && emCurso && geracao == tentativa

    private fun terminar(tentativa: Int, texto: String) {
        if (!vigente(tentativa)) return
        emCurso = false
        consultaPendente = false
        if (ativa) mostrar(texto, false)
    }

    private fun erro(tentativa: Int, etapa: String, codigo: Int?) {
        val ajuda = when (codigo) {
            AgeSignalsErrorCode.APP_NOT_OWNED -> "Confira o pacote e a conta ativa como testadora de licença (APP_NOT_OWNED)."
            AgeSignalsErrorCode.API_NOT_AVAILABLE -> "API indisponível para este ambiente (API_NOT_AVAILABLE)."
            AgeSignalsErrorCode.PLAY_STORE_NOT_FOUND,
            AgeSignalsErrorCode.PLAY_SERVICES_NOT_FOUND,
            AgeSignalsErrorCode.CANNOT_BIND_TO_SERVICE,
            AgeSignalsErrorCode.PLAY_STORE_VERSION_OUTDATED,
            AgeSignalsErrorCode.PLAY_SERVICES_VERSION_OUTDATED -> "Confira a instalação e atualização da Play Store e dos serviços Google."
            AgeSignalsErrorCode.NETWORK_ERROR -> "Confira a conexão de rede."
            else -> "Confira o ambiente e tente novamente por este botão."
        }
        terminar(tentativa, "Falha ao $etapa. Código: ${codigo ?: "não informado"}.\n$ajuda\nIdade desconhecida.")
    }

    companion object {
        const val INICIAL = "Nenhuma faixa guardada. Toque em Consultar para executar uma nova consulta."
    }
}
