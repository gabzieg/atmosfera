package com.atmosfera.wallpaper.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

private data class NavDestino(val rota: String, val titulo: String, val icone: ImageVector)

private val destinos = listOf(
    NavDestino("home", "Início", Icons.Default.Home),
    NavDestino("store", "Loja", Icons.Default.ShoppingCart),
    NavDestino("settings", "Ajustes", Icons.Default.Settings),
)

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        // Coluna de conteúdo com largura máxima, centralizada.
        //
        // Em celular (≈360–430dp) o teto nunca é atingido: layout idêntico ao de
        // antes. Em tela grande é o que evita o app parecer quebrado — a partir
        // do targetSdk 36 o Android 16 IGNORA a trava `screenOrientation`
        // declarada no manifesto quando a tela tem ≥600dp, então tablet e
        // dobrável passam a ver estas telas queiramos ou não.
        //
        // Sem o teto, o conteúdo esticava na largura toda e o card de clima
        // (altura fixa de 300dp) virava uma faixa achatada com a arte cortada.
        // Isto não torna o app "otimizado pra tablet" — é o mínimo pra leitura
        // ficar confortável e o resultado parecer intencional. Layout de duas
        // colunas, se um dia valer a pena, entra aqui.
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.widthIn(max = 600.dp).fillMaxSize(),
            ) {
                composable("home") { HomeTab(viewModel) }
                composable("store") { StoreTab(viewModel) }
                composable("settings") { SettingsTab(viewModel) }
            }
        }
    }
}

@Composable
private fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        destinos.forEach { destino ->
            NavigationBarItem(
                icon = { Icon(destino.icone, contentDescription = destino.titulo) },
                label = { Text(destino.titulo) },
                selected = currentRoute == destino.rota,
                colors = NavigationBarItemDefaults.colors(
                    // Selecionado = ícone claro (primary) sobre uma pílula escura sutil
                    // (surfaceVariant). No mono, primaryContainer é CLARO e colidiria
                    // com o ícone claro — daí o indicador usar surfaceVariant.
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                onClick = {
                    navController.navigate(destino.rota) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
