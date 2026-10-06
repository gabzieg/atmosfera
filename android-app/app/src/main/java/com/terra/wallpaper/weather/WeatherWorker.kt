package com.terra.wallpaper.weather

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters

/**
 * Compatibilidade com trabalhos persistidos antes da atualização.
 * Nunca consulta clima/localização, mesmo se iniciar antes de terminar o cancelamento.
 * Preservar o nome da classe enquanto houver instalações antigas para migrar.
 */
class WeatherWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = Result.success()

    companion object {
        fun cancelarLegado(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork("atmosfera_weather_sync")
            // WorkRequest inclui automaticamente a classe do worker nas tags,
            // inclusive os trabalhos pontuais antigos sem nome único.
            workManager.cancelAllWorkByTag(WeatherWorker::class.java.name)
        }
    }
}
