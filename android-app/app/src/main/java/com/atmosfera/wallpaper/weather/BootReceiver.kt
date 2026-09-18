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
            val cache = WeatherCache(applicationContext)
            val repo = WeatherRepository()

            val loc = cache.cachedLocation()
            if (loc != null) {
                val (lat, lon) = loc
                if (cache.isStale(lat, lon, IntervaloClima.ttlMs(applicationContext))) {
                    repo.fetchWeather(lat, lon).onSuccess { state ->
                        cache.save(state, lat, lon, localPadrao = cache.localPadrao())
                    }
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
