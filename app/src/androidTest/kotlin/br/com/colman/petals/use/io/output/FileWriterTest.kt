package br.com.colman.petals.use.io.output

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import br.com.colman.kotest.FunSpec
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.shouldBe
import java.io.File
import java.time.LocalDate

class FileWriterTest : FunSpec({
  val context: Context = ApplicationProvider.getApplicationContext()
  val target = FileWriter(context)
  val exportDir = File(context.filesDir, "exports")

  test("should create export directory when using context constructor") {
    exportDir.deleteRecursively()
    FileWriter(context)
    exportDir.shouldExist()
  }

  test("should write content to file and generate correct file name") {
    val content = "Integration test data"
    val expectedFileName = "PetalsExport-${LocalDate.now()}.csv"
    val expectedFile = File(exportDir, expectedFileName)

    target.write(content)

    expectedFile.shouldExist()
    expectedFile.readText() shouldBe content
  }
})
