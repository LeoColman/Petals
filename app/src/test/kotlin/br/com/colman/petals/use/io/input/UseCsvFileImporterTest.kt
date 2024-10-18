package br.com.colman.petals.use.io.input

import android.content.ContentResolver
import android.net.Uri
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.ByteArrayInputStream
import java.io.IOException

class UseCsvFileImporterTest : FunSpec({
  val useImporter = mockk<UseImporter>(relaxed = true)
  val contentResolver = mockk<ContentResolver>()
  val target = UseCsvFileImporter(useImporter, contentResolver)

  val uri = mockk<Uri>()

  test("importCsvFile should read lines from the URI and call useImporter.import with the lines") {
    val csvContent = "line1\nline2\nline3"

    every { contentResolver.openInputStream(uri) } returns ByteArrayInputStream(csvContent.toByteArray())

    target.importCsvFile(uri)

    shouldNotThrowAny {
      verify { useImporter.import(listOf("line1", "line2", "line3")) }
    }
  }

  test("importCsvFile should handle empty content and call useImporter.import with an empty list") {
    val csvContent = ""

    every { contentResolver.openInputStream(uri) } returns ByteArrayInputStream(csvContent.toByteArray())

    target.importCsvFile(uri)

    shouldNotThrowAny {
      verify { useImporter.import(emptyList()) }
    }
  }

  test("importCsvFile should handle null InputStream and call useImporter.import with an empty list") {
    every { contentResolver.openInputStream(uri) } returns null

    target.importCsvFile(uri)

    shouldNotThrowAny {
      verify { useImporter.import(emptyList()) }
    }
  }

  test("importCsvFile should propagate exceptions during file reading") {
    every { contentResolver.openInputStream(uri) } throws IOException("File not found")

    shouldThrow<IOException> {
      target.importCsvFile(uri)
    }
  }
})
