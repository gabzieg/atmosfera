package com.atmosfera.wallpaper.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
