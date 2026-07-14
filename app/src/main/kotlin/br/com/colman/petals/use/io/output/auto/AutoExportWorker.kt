package br.com.colman.petals.use.io.output.auto

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// runAttemptCount is 0 on the first run, so this is the number of RETRIES after that
// first attempt: retry while 0..1, give up on the third run. Three attempts in total.
private const val maxRetries = 2

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
      AutoExportResult.Transient -> if (runAttemptCount < maxRetries) Result.retry() else Result.failure()
    }
  }
}
