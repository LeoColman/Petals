package br.com.colman.petals.use.io

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.koin
import br.com.colman.petals.strain.repository.StrainRepository
import br.com.colman.petals.use.io.input.UseCsvFileImporter
import br.com.colman.petals.use.io.output.UseExporter
import br.com.colman.petals.use.repository.UseRepository
import io.kotest.matchers.file.shouldHaveSameStructureAndContentAs
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import java.io.File
import java.time.LocalDate

class UseIOModuleTest : FunSpec({

  val useCsvFileImporter = koin.get<UseCsvFileImporter>()
  val useExporter = koin.get<UseExporter>()
  val useRepository = koin.get<UseRepository>()
  val strainRepository = koin.get<StrainRepository>()

  // The importer writes to the app's own database, so the uses and the strain this test adds are removed after it.
  afterTest {
    useRepository.all().first().filter { it.id in FixtureUseIds }.forEach(useRepository::delete)
    strainRepository.allNow().filter { it.id == FixtureStrainId }.forEach(strainRepository::delete)
  }

  test("should import and export data maintaining integrity") {
    val inputFile = File(ApplicationProvider.getApplicationContext<Context>().filesDir, "test_input.csv")

    // Every column the exporter writes, in its order. The fixture used to stop at `id`, from before
    // description and consumption method existed, so the round trip compared four columns against
    // six and could never match. The second line names a strain, so the round trip also proves the
    // import creates it and the export writes it back. Its name is the test's own, so a strain made
    // by hand on the same device can't be matched by name instead. The third line's notes hold a line break, which
    // the export quotes, so its use spans two lines of the file.
    inputFile.writeText(
      """
        date,amount,cost_per_gram,id,description,consumption_method,strain_id,strain_name,strain_thc_percent,strain_cbd_percent,strain_cost_per_gram,strain_archived
        2024-03-21T19:01:47.163,0.08,22.2,80204597-00eb-4412-b7ee-223388806fe2,,,,,,,,
        2024-03-22T21:30:00,0.25,12.5,5d2f8a3e-1c4b-4f6e-9a7d-2b8c0e1f3a45,,vaporized,0b7e9c1a-6d2f-4e8b-a3c5-9f1d2e4b6a78,UseIOModuleTest strain,27,1,12.5,false
        2024-03-23T08:15:00,0.1,10,9c1e2f3a-4b5d-4e6f-8a7b-1c2d3e4f5a6b,"Bedrocan
        felt sleepy",smoked,,,,,,
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

private val FixtureUseIds = setOf(
  "80204597-00eb-4412-b7ee-223388806fe2",
  "5d2f8a3e-1c4b-4f6e-9a7d-2b8c0e1f3a45",
  "9c1e2f3a-4b5d-4e6f-8a7b-1c2d3e4f5a6b"
)
private const val FixtureStrainId = "0b7e9c1a-6d2f-4e8b-a3c5-9f1d2e4b6a78"
