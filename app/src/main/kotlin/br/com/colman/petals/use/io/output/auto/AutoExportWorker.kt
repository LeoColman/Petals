package br.com.colman.petals.use.io.output.auto

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val maxAttempts = 3

/**
 * Thin CoroutineWorker: WorkManager's default WorkerFactory reflects the
 * (Context, WorkerParameters) constructor, so no custom WorkerFactory or
 * Configuration.Provider is needed. Koin starts in PetalsApplication.onCreate,
 * always before any worker can run in this process (the Glance widget already
 * relies on the same ordering).
 */
class AutoExportWorker(
  context: Context,
  workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters), KoinComponent {

  private val autoExporter: AutoExporter by inject()

  override suspend fun doWork(): Result {
    return when (autoExporter.export()) {
      AutoExportResult.Success -> Result.success()
      AutoExportResult.PermissionLost -> Result.failure()
      AutoExportResult.Transient -> if (runAttemptCount < maxAttempts) Result.retry() else Result.failure()
    }
  }
}
