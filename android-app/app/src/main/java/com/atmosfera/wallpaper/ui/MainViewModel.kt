package com.atmosfera.wallpaper.ui

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.atmosfera.wallpaper.billing.BillingManager
import com.atmosfera.wallpaper.billing.Plano
import com.atmosfera.wallpaper.engine.Catalogo
import com.atmosfera.wallpaper.engine.Cena
import com.atmosfera.wallpaper.weather.LocationHelper
import com.atmosfera.wallpaper.weather.WeatherCache
import com.atmosfera.wallpaper.weather.WeatherRepository
import com.atmosfera.wallpaper.weather.WeatherState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application), SharedPreferences.OnSharedPreferenceChangeListener {

    private val context: Context get() = getApplication()
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val cenaPrefs = context.getSharedPreferences("atmosfera_cena", Context.MODE_PRIVATE)
    private val locationHelper = LocationHelper(context)
    private val weatherRepo = WeatherRepository()
    private val weatherCache = WeatherCache(context)
    
    val billingManager = BillingManager(context) { onPremiumMudou(it) }

    private val _hasLocationPermission = MutableStateFlow(checkPermission())
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission

    private val _weatherState = MutableStateFlow<WeatherState?>(weatherCache.get())
    val weatherState: StateFlow<WeatherState?> = _weatherState

    private val _currentSceneId = MutableStateFlow(Cena.atual(context))
    val currentSceneId: StateFlow<String> = _currentSceneId

    private val _isPremium = MutableStateFlow(Plano.isPremium(context))
    val isPremium: StateFlow<Boolean> = _isPremium

    init {
        prefs.registerOnSharedPreferenceChangeListener(this)
        cenaPrefs.registerOnSharedPreferenceChangeListener(this)
        billingManager.conectar()
        if (_hasLocationPermission.value) {
            refreshWeather()
        }
    }

    override fun onCleared() {
        super.onCleared()
        prefs.unregisterOnSharedPreferenceChangeListener(this)
        cenaPrefs.unregisterOnSharedPreferenceChangeListener(this)
        billingManager.encerrar()
    }

    fun checkPermission(): Boolean {
        val hasFine = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return hasFine || hasCoarse
    }

    fun onPermissionGranted() {
        _hasLocationPermission.value = true
        refreshWeather()
    }

    fun onPermissionDenied() {
        _hasLocationPermission.value = false
    }

    fun refreshWeather() {
        viewModelScope.launch {
            try {
                val (lat, lon) = locationHelper.getLocation()
                weatherRepo.fetchWeather(lat, lon)
                    .onSuccess { state -> 
                        weatherCache.save(state, lat, lon)
                        _weatherState.value = state
                        // Notifica o motor que o clima atualizou para que reaja caso esteja visivel
                        prefs.edit().putLong("KEY_WEATHER_UPDATE", System.currentTimeMillis()).apply()
                    }
                    .onFailure { 
                        _weatherState.value = weatherCache.get()
                    }
            } catch (e: Exception) {
                 _weatherState.value = weatherCache.get()
            }
        }
    }

    private fun onPremiumMudou(premium: Boolean) {
        _isPremium.value = premium
        prefs.edit().putLong("KEY_PREMIUM_STATUS", System.currentTimeMillis()).apply()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences == cenaPrefs && key == "atual") {
            _currentSceneId.value = Cena.atual(context)
            prefs.edit().putLong("KEY_CENA_ATUAL", System.currentTimeMillis()).apply()
        }
    }

    fun buyPremium(activity: android.app.Activity) {
        billingManager.comprar(activity, BillingManager.PRODUTO_PREMIUM)
    }

    fun buyScene(activity: android.app.Activity, productId: String) {
        billingManager.comprar(activity, productId)
    }

    fun setScene(sceneId: String) {
        Cena.definir(context, sceneId)
        _currentSceneId.value = sceneId
    }

    fun isSceneUnlocked(cenario: com.atmosfera.wallpaper.engine.Cenario): Boolean {
        if (cenario.gratis) return true
        return billingManager.isAvulsoDesbloqueado(cenario.id)
    }
}
