package com.terra.wallpaper.weather

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.random.Random

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
            val repo = WeatherRepository(applicationContext)

            val loc = cache.cachedLocation()
            if (loc != null) {
                val (lat, lon) = loc
                if (cache.isStale(lat, lon, IntervaloClima.ttlMs(applicationContext))) {
                    // A MET pede tráfego "numa curva plana, não em dente de serra":
                    // o Doze junta as execuções de muitos aparelhos nas mesmas
                    // janelas, então cada um espera um pedaço aleatório antes de
                    // consultar. Só vale pra rotina em segundo plano — consulta
                    // pedida pelo usuário (abrir o app) não espera.
                    delay(Random.nextLong(0L, JITTER_MAX_MS))
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
        internal const val JITTER_MAX_MS = 2 * 60_000L

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
