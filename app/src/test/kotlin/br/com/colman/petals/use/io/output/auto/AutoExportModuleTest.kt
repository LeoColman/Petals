package br.com.colman.petals.use.io.output.auto

import android.content.ContentResolver
import android.content.Context
import androidx.work.WorkManager
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.io.output.UseCsvSerializer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class AutoExportModuleTest : FunSpec({
  val koin = koinApplication {
    modules(
      AutoExportModule,
      module {
        single { mockk<WorkManager>() }
        single { mockk<ContentResolver>() }
        single {
          mockk<Context> {
            every { contentResolver } returns get()
          }
        }
        single { mockk<SettingsRepository>() }
        single { mockk<UseCsvSerializer>() }
      }
    )
  }.koin

  test("Should resolve AutoExportDocumentWriter") {
    koin.get<AutoExportDocumentWriter>() shouldNotBe null
  }

  test("Should resolve AutoExportScheduler") {
    koin.get<AutoExportScheduler>() shouldNotBe null
  }

  test("Should resolve AutoExporter") {
    koin.get<AutoExporter>() shouldNotBe null
  }

  test("Should resolve AutoExportEnabler") {
    koin.get<AutoExportEnabler>() shouldNotBe null
  }
})
