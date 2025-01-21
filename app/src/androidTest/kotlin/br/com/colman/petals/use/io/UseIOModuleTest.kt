package br.com.colman.petals.use.io

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.koin
import br.com.colman.petals.use.io.input.UseCsvFileImporter
import br.com.colman.petals.use.io.output.UseExporter
import io.kotest.matchers.file.shouldHaveSameStructureAndContentAs
import io.mockk.mockk
import io.mockk.verify
import java.io.File
import java.time.LocalDate

class UseIOModuleTest : FunSpec({

  val useCsvFileImporter = koin.get<UseCsvFileImporter>()
  val useExporter = koin.get<UseExporter>()

  test("should import and export data maintaining integrity") {
    val inputFile = File(ApplicationProvider.getApplicationContext<Context>().filesDir, "test_input.csv")

    inputFile.writeText(
      """
        date,amount,cost_per_gram,id
        2024-03-21T19:01:47.163,0.08,22.2,80204597-00eb-4412-b7ee-223388806fe2
      """.trimIndent()
    )

    useCsvFileImporter.importCsvFile(inputFile.toUri())

    val mockLauncher = mockk<ManagedActivityResultLauncher<Intent, ActivityResult>>(relaxed = true)

    useExporter.exportUses(mockLauncher)

    verify {
      mockLauncher.launch(
        match { intent ->
          intent.action == Intent.ACTION_SEND &&
            intent.type == "text/plain" &&
            intent.flags == Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
      )
    }

    val exportsDir = File(ApplicationProvider.getApplicationContext<Context>().filesDir, "exports")
    val expectedFileName = "PetalsExport-${LocalDate.now()}.csv"
    val exportedFile = File(exportsDir, expectedFileName)

    inputFile shouldHaveSameStructureAndContentAs exportedFile
  }
})
