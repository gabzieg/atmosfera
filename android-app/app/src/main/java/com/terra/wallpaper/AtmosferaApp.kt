package com.terra.wallpaper

import android.app.Application
import android.app.Activity
import android.os.Bundle
import com.terra.wallpaper.weather.WeatherWorker
import androidx.work.Configuration

class AtmosferaApp : Application(), Configuration.Provider {

    private var atividadesVisiveis = 0
    val appAberto: Boolean get() = atividadesVisiveis > 0

    // O serviço acompanha também telas transparentes do próprio Terra.
    val observadoresVisibilidade = mutableSetOf<() -> Unit>()

    override fun onCreate() {
        super.onCreate()
        WeatherWorker.cancelarLegado(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityStarted(activity: Activity) {
                atividadesVisiveis++
                observadoresVisibilidade.toList().forEach { it() }
            }
            override fun onActivityStopped(activity: Activity) {
                atividadesVisiveis = (atividadesVisiveis - 1).coerceAtLeast(0)
                observadoresVisibilidade.toList().forEach { it() }
            }
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityResumed(activity: Activity) = Unit
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
