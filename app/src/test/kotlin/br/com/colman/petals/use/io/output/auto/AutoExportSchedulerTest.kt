package br.com.colman.petals.use.io.output.auto

import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.ExistingWorkPolicy.REPLACE
import androidx.work.NetworkType.NOT_REQUIRED
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.concurrent.TimeUnit.DAYS

class AutoExportSchedulerTest : FunSpec({

  test("schedule() enqueues a 24h periodic request with KEEP and no network constraint") {
    val workManager = mockk<WorkManager>()
    val requestSlot = slot<PeriodicWorkRequest>()
    every {
      workManager.enqueueUniquePeriodicWork("petals-auto-export-daily", KEEP, capture(requestSlot))
    } returns mockk()

    AutoExportScheduler(workManager).schedule()

    requestSlot.captured.workSpec.constraints.requiredNetworkType shouldBe NOT_REQUIRED
    requestSlot.captured.workSpec.intervalDuration shouldBe DAYS.toMillis(1)
  }

  test("exportNow() enqueues a one-time request replacing any pending one") {
    val workManager = mockk<WorkManager>()
    every {
      workManager.enqueueUniqueWork("petals-auto-export-now", REPLACE, any<OneTimeWorkRequest>())
    } returns mockk()

    AutoExportScheduler(workManager).exportNow()

    shouldNotThrowAny {
      verify { workManager.enqueueUniqueWork("petals-auto-export-now", REPLACE, any<OneTimeWorkRequest>()) }
    }
  }

  test("cancel() cancels both the periodic and one-time unique work names") {
    val workManager = mockk<WorkManager>()
    every { workManager.cancelUniqueWork(any<String>()) } returns mockk()

    AutoExportScheduler(workManager).cancel()

    shouldNotThrowAny {
      verify {
        workManager.cancelUniqueWork("petals-auto-export-daily")
        workManager.cancelUniqueWork("petals-auto-export-now")
      }
    }
  }
})
