package com.terra.wallpaper

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import androidx.compose.runtime.mutableStateOf
import com.terra.wallpaper.ui.HomeWeatherSection
import com.terra.wallpaper.ui.ClimaInfo
import com.terra.wallpaper.weather.WeatherState
import com.terra.wallpaper.weather.WeatherCondition
import com.terra.wallpaper.weather.DayPeriod
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = android.app.Application::class)
class HomePermissionTest {
    @get:Rule val compose = createComposeRule()

    @Test fun `negativa nao esconde estado sem cache nem opcao de conceder`() {
        var requests = 0
        compose.setContent {
            MaterialTheme {
                HomeWeatherSection(false, "cabana", "pixel", null, ClimaInfo(null, 0L, false), {}, { requests++ })
            }
        }
        compose.onNodeWithText("Local padrão: São Paulo, SP").assertIsDisplayed()
        compose.onNodeWithText("Ainda não há clima salvo.", substring = true).assertIsDisplayed()
        compose.onNodeWithText("Conceder permissão").performClick()
        assertEquals(1, requests)
    }

    @Test fun `cache refresh sincroniza permissao revogada fora do app`() {
        val application = org.robolectric.RuntimeEnvironment.getApplication()
        val shadow = org.robolectric.Shadows.shadowOf(application)
        shadow.grantPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        val model = com.terra.wallpaper.ui.MainViewModel(application)
        try {
            org.junit.Assert.assertTrue(model.hasLocationPermission.value)
            shadow.denyPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            model.refreshWeather() // Mesma entrada chamada por MainActivity.onResume.
            org.junit.Assert.assertFalse(model.hasLocationPermission.value)
            shadow.grantPermissions(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            model.refreshWeather()
            org.junit.Assert.assertTrue(model.hasLocationPermission.value)
        } finally {
            androidx.lifecycle.ViewModelStore().apply { put("model", model); clear() }
        }
    }

    @Test fun `retorno e revogacao preservam clima salvo e procedencia`() {
        val permission = mutableStateOf(true)
        compose.setContent {
            MaterialTheme {
                HomeWeatherSection(permission.value, "cabana", "pixel",
                    WeatherState(WeatherCondition.SUNNY, DayPeriod.MORNING, 23.0, 24.0, "Ensolarado"),
                    ClimaInfo("Outra cidade", 0L, false), {}, {})
            }
        }
        compose.onNodeWithText("Local padrão: São Paulo, SP").assertDoesNotExist()
        compose.runOnIdle { permission.value = false }
        compose.onNodeWithText("Local padrão: São Paulo, SP").assertIsDisplayed()
        compose.onNodeWithText("Conceder permissão").assertIsDisplayed()
        compose.onNodeWithText("23°C").assertIsDisplayed()
        compose.onNodeWithText("Outra cidade").assertIsDisplayed()
        compose.onNodeWithText("São Paulo, SP · local padrão").assertDoesNotExist()
        compose.runOnIdle { permission.value = true }
        compose.onNodeWithText("Local padrão: São Paulo, SP").assertDoesNotExist()
        compose.onNodeWithText("23°C").assertIsDisplayed()
        compose.onNodeWithText("Outra cidade").assertIsDisplayed()
    }
}
