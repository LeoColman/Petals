package br.com.colman.petals.use.io.output.auto

import android.content.Context
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class AutoExportWorkerTest : FunSpec({

  val autoExporter = mockk<AutoExporter>()

  beforeTest { startKoin { modules(module { single { autoExporter } }) } }
  afterTest { stopKoin() }

  fun worker(runAttemptCount: Int = 0): AutoExportWorker {
    val context = mockk<Context>(relaxed = true) { every { applicationContext } returns this }
    val parameters = mockk<WorkerParameters>(relaxed = true) {
      every { this@mockk.runAttemptCount } returns runAttemptCount
    }
    return AutoExportWorker(context, parameters)
  }

  test("A successful export succeeds") {
    coEvery { autoExporter.export() } returns AutoExportResult.Success

    worker().doWork() shouldBe Result.success()
  }

  test("A lost folder fails without retrying, since retrying cannot bring the permission back") {
    coEvery { autoExporter.export() } returns AutoExportResult.PermissionLost

    worker().doWork() shouldBe Result.failure()
  }

  // A transient error is retried twice, then given up on.
  withData(
    nameFn = { (attempt, result) -> "A transient error on attempt $attempt gives ${result.javaClass.simpleName}" },
    0 to Result.retry(),
    1 to Result.retry(),
    2 to Result.failure(),
  ) { (attempt, expected) ->
    coEvery { autoExporter.export() } returns AutoExportResult.Transient

    worker(attempt).doWork() shouldBe expected
  }
})
