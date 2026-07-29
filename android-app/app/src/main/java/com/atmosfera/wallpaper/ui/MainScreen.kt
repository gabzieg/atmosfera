package com.atmosfera.wallpaper.ui

import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
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
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding),
        ) {
            composable("home") { HomeTab(viewModel) }
            composable("store") { StoreTab(viewModel) }
            composable("settings") { SettingsTab(viewModel) }
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
