package com.atmosfera.wallpaper.weather

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Boot detectado, reagendando WeatherWorker.")
            WeatherWorker.schedule(context, IntervaloClima.atual(context).toLong())
        }
    }
}

class WeatherWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val locationHelper = LocationHelper(applicationContext)
            val onde = locationHelper.getLocalizacao()

            val repo = WeatherRepository()
            val cache = WeatherCache(applicationContext)

            if (cache.isStale(onde.lat, onde.lon, IntervaloClima.ttlMs(applicationContext))) {
                repo.fetchWeather(onde.lat, onde.lon).onSuccess { state ->
                    // sem nome de lugar: o worker roda em background, onde o
                    // Geocoder costuma não responder. O cache mantém o nome
                    // anterior enquanto o aparelho não se mover.
                    cache.save(state, onde.lat, onde.lon, localPadrao = onde.padrao)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "atmosfera_weather_sync"

        fun schedule(context: Context, minutos: Long = 30) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<WeatherWorker>(minutos, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 5, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }

        fun scheduleOnce(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<WeatherWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
