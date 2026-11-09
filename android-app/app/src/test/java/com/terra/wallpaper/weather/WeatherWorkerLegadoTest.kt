package com.terra.wallpaper.weather

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

/** Migra uma fila real do WorkManager; o trabalho de outro recurso deve sobreviver. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = Application::class)
class WeatherWorkerLegadoTest {
    private lateinit var context: Context
    private lateinit var manager: WorkManager

    @Before fun preparar() {
        context = RuntimeEnvironment.getApplication()
        WorkManagerTestInitHelper.initializeTestWorkManager(context,
            Configuration.Builder().setExecutor(SynchronousExecutor())
                .setTaskExecutor(SynchronousExecutor()).build())
        manager = WorkManager.getInstance(context)
    }

    @Test fun `worker persistido termina sem consultar clima nem localizacao`() = runBlocking {
        val worker = TestListenableWorkerBuilder<WeatherWorker>(context).build()
        assertEquals(androidx.work.ListenableWorker.Result.success(), worker.doWork())
    }

    @Test fun `cancela periodico e pontual antigos sem cancelar outros recursos`() {
        val periodico = PeriodicWorkRequestBuilder<WeatherWorker>(30, TimeUnit.MINUTES)
            .setInitialDelay(1, TimeUnit.DAYS).build()
        val pontual = OneTimeWorkRequestBuilder<WeatherWorker>()
            .setInitialDelay(1, TimeUnit.DAYS).build()
        val outro = OneTimeWorkRequestBuilder<OutroWorker>()
            .setInitialDelay(1, TimeUnit.DAYS).build()
        manager.enqueueUniquePeriodicWork("atmosfera_weather_sync",
            ExistingPeriodicWorkPolicy.KEEP, periodico).result.get()
        manager.enqueue(listOf(pontual, outro)).result.get()

        WeatherWorker.cancelarLegado(context)

        assertEquals(WorkInfo.State.CANCELLED, manager.getWorkInfoById(periodico.id).get()!!.state)
        assertEquals(WorkInfo.State.CANCELLED, manager.getWorkInfoById(pontual.id).get()!!.state)
        assertEquals(WorkInfo.State.ENQUEUED, manager.getWorkInfoById(outro.id).get()!!.state)
    }

    class OutroWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
        override fun doWork(): Result = Result.success()
    }
}
