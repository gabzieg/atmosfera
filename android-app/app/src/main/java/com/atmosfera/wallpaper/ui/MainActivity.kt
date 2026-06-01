package com.atmosfera.wallpaper.ui

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.atmosfera.wallpaper.R
import com.atmosfera.wallpaper.databinding.ActivityMainBinding
import com.atmosfera.wallpaper.service.AtmosferaWallpaperService
import com.atmosfera.wallpaper.weather.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val locationHelper by lazy { LocationHelper(this) }
    private val weatherRepo by lazy { WeatherRepository() }
    private val weatherCache by lazy { WeatherCache(this) }

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) refreshWeather()
        else showToast("Sem localização: usando Guarapuava, PR como padrão.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        checkPermissionsAndLoad()
        WeatherWorker.schedule(this)
    }

    private fun setupUI() {
        binding.btnSetWallpaper.setOnClickListener { openLiveWallpaperPicker() }
        binding.btnRefresh.setOnClickListener { refreshWeather() }
        binding.swipeRefresh.setOnRefreshListener { refreshWeather() }
    }

    private fun checkPermissionsAndLoad() {
        val hasFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            refreshWeather()
        } else {
            locationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    private fun refreshWeather() {
        binding.progressBar.visibility = View.VISIBLE
        binding.swipeRefresh.isRefreshing = true

        lifecycleScope.launch {
            try {
                val (lat, lon) = locationHelper.getLocation()
                val result = weatherRepo.fetchWeather(lat, lon)

                result.onSuccess { state ->
                    weatherCache.save(state, lat, lon)
                    updateUI(state)
                }.onFailure {
                    // Tenta usar cache
                    weatherCache.get()?.let { updateUI(it) }
                        ?: showToast("Sem conexão e sem dados em cache.")
                }
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun updateUI(state: WeatherState) {
        binding.tvTemperature.text = "${state.temperatureCelsius.toInt()}°C"
        binding.tvFeelsLike.text = "Sensação ${state.feelsLikeCelsius.toInt()}°C"
        binding.tvDescription.text = state.description
        binding.tvConditionEmoji.text = conditionEmoji(state.condition)
        binding.tvHumidity.text = "Umidade: ${state.humidity}%"
        binding.tvWind.text = "Vento: ${state.windspeedKmh.toInt()} km/h"

        val assetName = state.assetFileName()
        binding.tvAssetFile.text = "Wallpaper: $assetName"

        // Preview do wallpaper
        try {
            val stream = assets.open(assetName)
            val bmp = android.graphics.BitmapFactory.decodeStream(stream)
            stream.close()
            binding.ivPreview.setImageBitmap(bmp)
        } catch (e: Exception) {
            binding.ivPreview.setImageResource(R.drawable.ic_placeholder)
        }
    }

    private fun conditionEmoji(condition: WeatherCondition) = when (condition) {
        WeatherCondition.SUNNY        -> "☀️"
        WeatherCondition.PARTLY_CLOUDY -> "⛅"
        WeatherCondition.CLOUDY       -> "☁️"
        WeatherCondition.LIGHT_RAIN   -> "🌦"
        WeatherCondition.HEAVY_RAIN   -> "🌧"
        WeatherCondition.STORM        -> "⛈"
        WeatherCondition.FOGGY        -> "🌫"
        WeatherCondition.SNOW         -> "❄️"
        WeatherCondition.CLEAR_NIGHT  -> "🌙"
    }

    private fun openLiveWallpaperPicker() {
        try {
            val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                putExtra(
                    WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    ComponentName(this@MainActivity, AtmosferaWallpaperService::class.java)
                )
            }
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback para seletor geral
            startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER))
        }
    }

    private fun showToast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}
