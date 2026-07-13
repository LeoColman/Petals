package br.com.colman.petals.use.io.output.auto

import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.ExistingWorkPolicy.REPLACE
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit.DAYS

private const val periodicWorkName = "petals-auto-export-daily"
private const val oneTimeWorkName = "petals-auto-export-now"

class AutoExportScheduler(
  private val workManager: WorkManager,
) {

  fun schedule() {
    val request = PeriodicWorkRequestBuilder<AutoExportWorker>(1, DAYS)
      // Constraints.NONE is required here, not optional: the fdroid flavor's
      // merged manifest has no ACCESS_NETWORK_STATE (removed via
      // tools:node="remove" in app/src/main/AndroidManifest.xml; only the
      // playstore flavor's ads SDK adds it back). Calling
      // setRequiredNetworkType(...) would throw SecurityException at runtime
      // on fdroid only, past every CI check. Do not add a network constraint.
      .setConstraints(Constraints.NONE)
      .build()

    workManager.enqueueUniquePeriodicWork(periodicWorkName, KEEP, request)
  }

  fun exportNow() {
    val request = OneTimeWorkRequestBuilder<AutoExportWorker>().build()
    workManager.enqueueUniqueWork(oneTimeWorkName, REPLACE, request)
  }

  fun cancel() {
    workManager.cancelUniqueWork(periodicWorkName)
    workManager.cancelUniqueWork(oneTimeWorkName)
  }
}
