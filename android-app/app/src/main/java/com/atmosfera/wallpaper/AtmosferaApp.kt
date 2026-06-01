package com.atmosfera.wallpaper

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager

class AtmosferaApp : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        // WorkManager é inicializado automaticamente via Configuration.Provider
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
