package com.atmosfera.wallpaper.ui

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.atmosfera.wallpaper.R
import com.atmosfera.wallpaper.billing.BillingManager
import com.atmosfera.wallpaper.billing.Plano
import com.atmosfera.wallpaper.databinding.ActivityMainBinding
import com.atmosfera.wallpaper.service.AtmosferaWallpaperService
import com.atmosfera.wallpaper.weather.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val locationHelper by lazy { LocationHelper(this) }
    private val weatherRepo by lazy { WeatherRepository() }
    private val weatherCache by lazy { WeatherCache(this) }
    private val billing by lazy { BillingManager(applicationContext) { onPremiumMudou(it) } }

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

        carregarPreview()
        setupUI()
        checkPermissionsAndLoad()
        WeatherWorker.schedule(this)
        billing.conectar()
        atualizarPremium()
    }

    override fun onResume() {
        super.onResume()
        atualizarPremium()
    }

    override fun onDestroy() {
        super.onDestroy()
        billing.encerrar()
    }

    private fun setupUI() {
        binding.btnSetWallpaper.setOnClickListener { openLiveWallpaperPicker() }
        binding.btnRefresh.setOnClickListener { refreshWeather() }
        binding.swipeRefresh.setOnRefreshListener { refreshWeather() }
        binding.btnPremium.setOnClickListener {
            if (Plano.isPremium(this)) return@setOnClickListener
            if (billing.temProduto()) billing.comprar(this)
            else showToast("Loja indisponível. Tente novamente em instantes.")
        }
    }

    /** Mostra o cenário real (fundo.png) como prévia estática. */
    private fun carregarPreview() {
        try {
            val opts = BitmapFactory.Options().apply { inSampleSize = 2 }
            assets.open("atmosfera/fundo.png").use {
                binding.ivPreview.setImageBitmap(BitmapFactory.decodeStream(it, null, opts))
            }
        } catch (e: Exception) {
            binding.ivPreview.setImageResource(R.drawable.ic_placeholder)
        }
    }

    private fun checkPermissionsAndLoad() {
        val hasFine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) refreshWeather()
        else locationPermissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    private fun refreshWeather() {
        binding.progressBar.visibility = View.VISIBLE
        binding.swipeRefresh.isRefreshing = true
        lifecycleScope.launch {
            try {
                val (lat, lon) = locationHelper.getLocation()
                weatherRepo.fetchWeather(lat, lon)
                    .onSuccess { state -> weatherCache.save(state, lat, lon); updateUI(state) }
                    .onFailure { weatherCache.get()?.let { updateUI(it) } ?: showToast("Sem conexão e sem dados em cache.") }
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
    }

    private fun conditionEmoji(condition: WeatherCondition) = when (condition) {
        WeatherCondition.SUNNY -> "☀️"
        WeatherCondition.PARTLY_CLOUDY -> "⛅"
        WeatherCondition.CLOUDY -> "☁️"
        WeatherCondition.LIGHT_RAIN -> "🌦"
        WeatherCondition.HEAVY_RAIN -> "🌧"
        WeatherCondition.STORM -> "⛈"
        WeatherCondition.FOGGY -> "🌫"
        WeatherCondition.SNOW -> "❄️"
        WeatherCondition.CLEAR_NIGHT -> "🌙"
    }

    // ── Premium ──────────────────────────────────────────────────────
    private fun onPremiumMudou(premium: Boolean) = runOnUiThread {
        atualizarPremium()
        if (premium) showToast("Premium desbloqueado! Aproveite. ✨")
    }

    private fun atualizarPremium() {
        val premium = Plano.isPremium(this)
        if (premium) {
            binding.tvPremiumStatus.text = "Plano: Premium ✓"
            binding.btnPremium.text = "Premium ativo"
            binding.btnPremium.isEnabled = false
        } else {
            binding.tvPremiumStatus.text = "Plano: Grátis"
            val preco = billing.precoFormatado()
            binding.btnPremium.text = if (preco != null) "Desbloquear Premium — $preco" else "Desbloquear Premium"
            binding.btnPremium.isEnabled = true
        }
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
            startActivity(Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER))
        }
    }

    private fun showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}
