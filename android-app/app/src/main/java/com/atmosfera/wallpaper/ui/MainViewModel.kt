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
import com.atmosfera.wallpaper.engine.ArteFundo
import com.atmosfera.wallpaper.engine.Catalogo
import com.atmosfera.wallpaper.engine.Cena
import com.atmosfera.wallpaper.engine.EstiloEfeito
import com.atmosfera.wallpaper.weather.IntervaloClima
import com.atmosfera.wallpaper.weather.LocationHelper
import com.atmosfera.wallpaper.weather.WeatherCache
import com.atmosfera.wallpaper.weather.WeatherRepository
import com.atmosfera.wallpaper.weather.WeatherState
import com.atmosfera.wallpaper.weather.WeatherWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Procedência do clima que está na tela: onde foi medido, quando foi buscado e
 * se caiu no local padrão. (De qual passo do modelo veio o dado fica no
 * `WeatherState.fonte`, que vai pro log — na tela seria ruído.)
 */
data class ClimaInfo(
    val lugar: String?,
    val atualizadoEmMs: Long,
    val localPadrao: Boolean,
) {
    /** "Guarapuava, PR · 14:32" — ou só o horário, quando não há nome. */
    fun resumo(): String? {
        val hora = if (atualizadoEmMs > 0L) {
            val c = java.util.Calendar.getInstance().apply { timeInMillis = atualizadoEmMs }
            String.format(java.util.Locale.US, "%02d:%02d",
                c.get(java.util.Calendar.HOUR_OF_DAY), c.get(java.util.Calendar.MINUTE))
        } else null
        val onde = when {
            localPadrao -> "local padrão"
            lugar != null -> lugar
            else -> null
        }
        return when {
            onde != null && hora != null -> "$onde · atualizado $hora"
            hora != null -> "atualizado $hora"
            onde != null -> onde
            else -> null
        }
    }
}

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

    // DE ONDE e DE QUANDO é o clima que está na tela. Nasceu de uma pergunta
    // dele: "o wallpaper não corresponde ao clima real" — sem isto não dá pra
    // saber se a previsão errou, se o dado está velho ou se o app está olhando
    // outra cidade (sem permissão de localização ele cai em Guarapuava calado).
    private val _climaInfo = MutableStateFlow(
        ClimaInfo(weatherCache.lugar(), weatherCache.ultimaBuscaMs(),
                  weatherCache.localPadrao())
    )
    val climaInfo: StateFlow<ClimaInfo> = _climaInfo

    private val _currentSceneId = MutableStateFlow(Cena.atual(context))
    val currentSceneId: StateFlow<String> = _currentSceneId

    private val _isPremium = MutableStateFlow(Plano.isPremium(context))
    val isPremium: StateFlow<Boolean> = _isPremium

    // Arte do fundo (pixel/clay/aqua) e estilo dos efeitos (pixel/clay/bizantino/aqua).
    // O serviço do wallpaper relê essas prefs ao voltar à tela inicial.
    private val _currentArt = MutableStateFlow(ArteFundo.atual(context))
    val currentArt: StateFlow<String> = _currentArt

    private val _currentEffectStyle = MutableStateFlow(EstiloEfeito.atual(context))
    val currentEffectStyle: StateFlow<String> = _currentEffectStyle

    private val _intervaloClimaMinutos = MutableStateFlow(IntervaloClima.atual(context))
    val intervaloClimaMinutos: StateFlow<Int> = _intervaloClimaMinutos

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
                val onde = locationHelper.getLocalizacao()
                // O nome do lugar é resolvido aqui, no app em primeiro plano —
                // é onde o Geocoder tem chance de responder. O serviço do
                // wallpaper e o worker salvam sem nome e herdam este.
                val lugar = locationHelper.nomeDoLugar(onde.lat, onde.lon)
                weatherRepo.fetchWeather(onde.lat, onde.lon)
                    .onSuccess { state ->
                        weatherCache.save(state, onde.lat, onde.lon, lugar, onde.padrao)
                        _weatherState.value = state
                        _climaInfo.value = ClimaInfo(
                            weatherCache.lugar(), weatherCache.ultimaBuscaMs(), onde.padrao)
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

    fun setArt(arteId: String) {
        ArteFundo.definir(context, arteId)
        _currentArt.value = arteId
    }

    fun setEffectStyle(styleId: String) {
        EstiloEfeito.definir(context, styleId)
        _currentEffectStyle.value = styleId
    }

    fun setIntervaloClima(minutos: Int) {
        IntervaloClima.definir(context, minutos)
        _intervaloClimaMinutos.value = minutos
        WeatherWorker.schedule(context, minutos.toLong())
    }

    /** Tamanho atual do cache descartável, em bytes (exibido na tela de Ajustes). */
    fun tamanhoCache(): Long =
        context.cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }

    /**
     * Limpa só o `cacheDir` (cache do WebView das páginas legais, temporários) —
     * conteúdo genuinamente descartável, recriado sozinho.
     *
     * NÃO limpa o [WeatherCache] de propósito: apesar do nome, ele é estado
     * funcional, não descarte. Apagá-lo deixaria quem está offline sem clima
     * nenhum até a próxima conexão — "limpar cache" não deve piorar o app.
     * Retorna quantos bytes foram liberados.
     */
    fun limparCache(): Long {
        val bytes = tamanhoCache()
        context.cacheDir.listFiles()?.forEach { it.deleteRecursively() }
        return bytes
    }

    fun isSceneUnlocked(cenario: com.atmosfera.wallpaper.engine.Cenario): Boolean {
        if (cenario.gratis) return true
        return billingManager.isAvulsoDesbloqueado(cenario.id)
    }
}
