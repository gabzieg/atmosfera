package com.terra.wallpaper.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class ItemAjuda(val pergunta: String, val resposta: String)

/**
 * Conteúdo escrito a partir do que o app realmente faz (motor reagindo ao clima
 * da MET Norway, compra única sem assinatura, fallback de localização em
 * São Paulo). Se o comportamento mudar, este texto precisa mudar junto — é a
 * primeira coisa que o usuário lê quando algo não funciona como ele esperava.
 */
private val ITENS_AJUDA = listOf(
    ItemAjuda(
        "Como ativo o papel de parede?",
        "Na aba Início, toque em \"Definir papel de parede\". O app mostra uma prévia " +
            "ao vivo do cenário; ao confirmar, o seletor do Android abre para você aplicar. " +
            "Depois de aplicado, a aba Início passa a mostrar \"Papel de parede ativado\".",
    ),
    ItemAjuda(
        "Como o wallpaper reage ao clima?",
        "O app consulta a previsão do instituto meteorológico da Noruega (MET Norway) usando " +
            "a localização aproximada do aparelho e traduz a condição atual na cena: chuva, neve, " +
            "nuvens, névoa, vento, sol e lua, além do ciclo de dia e noite. A cena muda sozinha " +
            "conforme o tempo real da sua região muda. Dados meteorológicos: MET Norway, " +
            "licença CC BY 4.0.",
    ),
    ItemAjuda(
        "Preciso dar permissão de localização?",
        "Não é obrigatório. Sem a permissão, o app usa São Paulo (SP) como local padrão e " +
            "continua funcionando normalmente — só o clima deixa de ser o da sua região. " +
            "Você pode conceder ou revogar a qualquer momento nos ajustes do Android.",
    ),
    ItemAjuda(
        "De quanto em quanto tempo o clima atualiza?",
        "O clima atualiza somente com o wallpaper visível na tela inicial, celular acordado " +
            "e desbloqueado. O intervalo padrão é 30 minutos; você pode escolher 15 ou 60 " +
            "em Ajustes → Cenário → Clima na tela inicial. O cache do fornecedor pode adiar " +
            "uma consulta. Com outro app aberto, tela apagada ou bloqueada, não há novas " +
            "consultas. A aba Início apenas mostra o último clima salvo.",
    ),
    ItemAjuda(
        "Qual a diferença entre cenário, arte e estilo?",
        "Cenário é o lugar da cena (a cabana na floresta, o campo de batalha). Arte é a " +
            "variante visual do fundo daquele cenário — a cabana tem pixel art, clay e " +
            "aquarela. Estilo é como os efeitos climáticos são desenhados por cima, e vale " +
            "para todos os cenários de uma vez.",
    ),
    ItemAjuda(
        "Como troco de cenário ou de estilo?",
        "Na aba Loja, toque num cenário para abrir a tela de detalhe. Lá dentro você " +
            "escolhe a arte do fundo, o estilo dos efeitos e aplica o cenário. O estilo " +
            "escolhido vale para todos os cenários; a arte é específica do cenário aberto.",
    ),
    ItemAjuda(
        "O que o Premium desbloqueia?",
        "Premium é uma compra única que libera todas as artes dos cinco cenários, todos " +
            "os estilos de efeito e os oito efeitos vivos. Não existe assinatura nem " +
            "cobrança recorrente.",
    ),
    ItemAjuda(
        "Comprei em outro aparelho. Como recupero?",
        "Use Ajustes → Compras → Restaurar compras, com a mesma conta Google usada na " +
            "compra. As compras ficam vinculadas à conta, não ao aparelho, então não é " +
            "preciso comprar de novo.",
    ),
    ItemAjuda(
        "O papel de parede parou de animar. E agora?",
        "Alguns fabricantes restringem apps em segundo plano para economizar bateria. " +
            "Verifique se o Terra está liberado da otimização de bateria nos ajustes do " +
            "Android. Se o clima estiver desatualizado, volte à tela inicial com o celular " +
            "desbloqueado e confira a conexão. A atualização respeita o intervalo e o cache.",
    ),
    ItemAjuda(
        "O app gasta muita bateria?",
        "As animações usam sprites: pequenas imagens estáticas que o motor movimenta, " +
            "em vez de vídeos ou animações pré-renderizadas. Isso reduz o processamento " +
            "e o consumo. A cena só é desenhada quando o wallpaper está visível; com a " +
            "tela apagada ou outro app aberto, o desenho é pausado. A consulta de clima " +
            "também só acontece na tela inicial desbloqueada e reaproveita os dados recentes.",
    ),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutoriaisScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Como funciona", style = MaterialTheme.typography.titleMedium) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
            ),
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
        ) {
            items(ITENS_AJUDA) { item -> ItemAjudaExpansivel(item) }
        }
    }
}

@Composable
private fun ItemAjudaExpansivel(item: ItemAjuda) {
    var aberto by rememberSaveable(item.pergunta) { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .clickable { aberto = !aberto }
                .padding(horizontal = 18.dp, vertical = 16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.pergunta,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    if (aberto) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
            if (aberto) {
                Spacer(Modifier.height(10.dp))
                Text(
                    item.resposta,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
