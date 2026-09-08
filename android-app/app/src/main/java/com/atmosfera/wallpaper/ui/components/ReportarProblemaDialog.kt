package com.atmosfera.wallpaper.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.atmosfera.wallpaper.ui.theme.Radius
import com.atmosfera.wallpaper.ui.theme.Spacing

/**
 * "Reportar problema" numa cena — o caminho de volta do recorte mal revisado.
 *
 * POR QUE ISTO EXISTE: metade do acervo roda com zona/distância DERIVADAS da
 * arte, não marcadas à mão. Quando o derivado erra, quem vê primeiro é o
 * usuário, não nós — e hoje ele não tem como avisar. Cada opção da lista abaixo
 * é uma MÁSCARA nossa (céu, zona de pingo, luz, profundidade, enquadramento):
 * o relato já chega dizendo qual arquivo refazer.
 *
 * COMO O DADO SAI DAQUI: pelo app de e-mail DELE, com o texto pronto — o app
 * não envia nada em segundo plano e não fica sabendo o endereço de ninguém.
 * Isso é obrigatório: `docs/contato/index.html` afirma que "o app não tem
 * formulário, não envia mensagem em segundo plano e não sabe o seu e-mail".
 * Um POST daqui tornaria aquele texto falso e mudaria a base legal.
 */
object Suporte {
    /**
     * Endereço público de suporte. Enquanto estiver vazio, o botão abre o
     * seletor de compartilhamento (o usuário escolhe por onde manda) em vez de
     * um e-mail endereçado — é o mesmo texto, sem destinatário fixo.
     *
     * PENDENTE: preencher aqui e no `docs/contato/index.html`, que também está
     * com `[PREENCHER: e-mail de contato]`. Não chutei um endereço pessoal de
     * propósito: o que entrar aqui vira endereço público do app.
     */
    const val EMAIL = ""
}

/** Um problema que o usuário pode marcar. `chave` é o que eu leio do lado de cá. */
private data class Sintoma(val chave: String, val rotulo: String)

private val SINTOMAS = listOf(
    Sintoma("ceu", "O céu está por cima do cenário, ou comeu um pedaço dele"),
    Sintoma("pingo_demais", "Respinga onde não devia (no céu, na parede, na copa da árvore)"),
    Sintoma("pingo_de_menos", "Não respinga onde devia (no chão, no telhado, na água)"),
    Sintoma("luz", "Luz acesa de dia, ou apagada de noite"),
    Sintoma("profundidade", "Sol, lua ou nuvem passa na FRENTE do cenário"),
    Sintoma("enquadramento", "A arte está cortada, esticada ou fora de lugar"),
    Sintoma("outro", "Outra coisa"),
)

@Composable
fun ReportarProblemaDialog(
    sceneId: String,
    sceneNome: String,
    arte: String,
    estilo: String,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val marcados = remember { mutableStateMapOf<String, Boolean>() }
    var enviou by remember { mutableStateOf(false) }
    val algumMarcado = marcados.any { it.value }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(Radius.card), color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier
                    .padding(Spacing.lg)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    "Algo errado neste cenário?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    "Marque o que você está vendo. Cada item aponta pra uma parte " +
                        "diferente do desenho, então isso encurta muito a correção.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(Spacing.md))

                SINTOMAS.forEach { s ->
                    val ligado = marcados[s.chave] == true
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { marcados[s.chave] = !ligado }
                            .padding(vertical = Spacing.xs),
                    ) {
                        Checkbox(checked = ligado, onCheckedChange = { marcados[s.chave] = it })
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            s.rotulo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                Spacer(Modifier.height(Spacing.md))
                Text(
                    if (enviou)
                        "Não achei um app de e-mail neste aparelho. Se quiser, escreva pra gente pela tela de Contato, em Ajustes."
                    else
                        "Vai abrir o seu app de mensagem com o texto pronto — nada é " +
                            "enviado sozinho. Um print da tela ajuda muito; dá pra anexar lá.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(Spacing.lg))
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(
                        onClick = {
                            val ok = enviarRelato(
                                context = context,
                                sceneId = sceneId,
                                sceneNome = sceneNome,
                                arte = arte,
                                estilo = estilo,
                                chaves = SINTOMAS.filter { marcados[it.chave] == true },
                            )
                            if (ok) onDismiss() else enviou = true
                        },
                        enabled = algumMarcado,
                        modifier = Modifier.weight(1f),
                    ) { Text("Reportar") }
                }
            }
        }
    }
}

/**
 * Monta o texto e entrega pro app de e-mail/mensagem. Devolve false quando não
 * há nenhum app capaz de receber (aparelho sem cliente de e-mail).
 *
 * O bloco técnico vai NO FIM e separado: é o que eu leio, e é o que diz qual
 * arquivo refazer sem precisar de uma segunda troca de mensagem.
 */
private fun enviarRelato(
    context: Context,
    sceneId: String,
    sceneNome: String,
    arte: String,
    estilo: String,
    chaves: List<Sintoma>,
): Boolean {
    val versao = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "?"
    } catch (_: Exception) {
        "?"
    }

    val assunto = "Atmosfera — problema em $sceneNome ($sceneId/$arte)"
    val corpo = buildString {
        appendLine("O que está errado:")
        chaves.forEach { appendLine("  - ${it.rotulo}") }
        appendLine()
        appendLine("(Se puder, descreva com as suas palavras e anexe um print:)")
        appendLine()
        appendLine()
        appendLine("--- não precisa mexer daqui pra baixo ---")
        appendLine("cena: $sceneId")
        appendLine("arte: $arte")
        appendLine("efeito: $estilo")
        appendLine("sintomas: ${chaves.joinToString(",") { it.chave }}")
        appendLine("app: $versao")
        appendLine("aparelho: ${Build.MANUFACTURER} ${Build.MODEL} · Android ${Build.VERSION.RELEASE}")
    }

    val intent = if (Suporte.EMAIL.isNotBlank()) {
        Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(Suporte.EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, assunto)
            putExtra(Intent.EXTRA_TEXT, corpo)
        }
    } else {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, assunto)
            putExtra(Intent.EXTRA_TEXT, corpo)
        }
    }

    return try {
        context.startActivity(Intent.createChooser(intent, "Reportar problema"))
        true
    } catch (_: Exception) {
        false
    }
}
