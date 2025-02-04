package br.com.colman.petals.use.io.output

import android.net.Uri
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.File
import java.time.LocalDate

class FileWriterTest : FunSpec({

  val exportDir = tempdir()
  val fileToUri = mockk<(File) -> Uri>()

  val target = FileWriter(exportDir, fileToUri)

  test("should create export directory if it does not exist") {
    exportDir.deleteRecursively()

    FileWriter(exportDir, fileToUri)

    exportDir.shouldExist()
  }

  test("should generate correct file name and write to file") {
    val expectedFileName = "PetalsExport-${LocalDate.now()}.csv"
    val expectedFile = File(exportDir, expectedFileName)
    val expectedUri = mockk<Uri>()
    every { fileToUri(expectedFile) } returns expectedUri

    target.write("Test Content")

    expectedFile.shouldExist()
    expectedFile.name shouldBe expectedFileName
  }

  test("should write content to file and return its URI") {
    val content = "Sample data"
    val expectedFile = File(exportDir, "PetalsExport-${LocalDate.now()}.csv")
    val expectedUri = mockk<Uri>()

    every { fileToUri(expectedFile) } returns expectedUri

    val resultUri = target.write(content)

    expectedFile.shouldExist()
    expectedFile.readText() shouldBe content
    resultUri shouldBe expectedUri

    verify { fileToUri(expectedFile) }
  }
})
