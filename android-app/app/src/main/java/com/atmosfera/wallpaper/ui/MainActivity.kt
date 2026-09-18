package com.atmosfera.wallpaper.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.atmosfera.wallpaper.ui.theme.AtmosferaTheme
import com.atmosfera.wallpaper.weather.IntervaloClima
import com.atmosfera.wallpaper.weather.WeatherWorker

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WeatherWorker.schedule(this, IntervaloClima.atual(this).toLong())

        setContent {
            AtmosferaTheme {
                // Onboarding só na primeira abertura. Enquanto a flag for falsa
                // ele substitui o app inteiro — é ele que leva o usuário a
                // APLICAR o wallpaper, que é o passo sem o qual o app parece não
                // fazer nada (maior causa de desinstalação em live wallpaper).
                var precisaOnboarding by rememberSaveable {
                    mutableStateOf(!Onboarding.concluido(this@MainActivity))
                }
                if (precisaOnboarding) {
                    OnboardingScreen(
                        viewModel = viewModel,
                        onConcluir = { precisaOnboarding = false },
                    )
                } else {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }

    /**
     * Compra concluída FORA do app (ex.: fluxo do Play que devolve o foco
     * depois, ou compra feita em outro device na mesma conta) não chega aqui
     * sozinha — o BillingManager só reconsulta no connect inicial. Recomendação
     * oficial da Play: reconsultar em onResume.
     * https://developer.android.com/google/play/billing/integrate
     */
    override fun onResume() {
        super.onResume()
        viewModel.billingManager.restaurar()
    }
}
