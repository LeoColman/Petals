package br.com.colman.petals.use.io.input

import android.content.ContentResolver
import android.net.Uri
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
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

  test("importCsvFile should read the whole file from the URI and call useImporter.import with it as it is") {
    val csvContent = "line1\n\"line2\nstill line2\"\r\nline3\n"

    every { contentResolver.openInputStream(uri) } returns ByteArrayInputStream(csvContent.toByteArray())

    target.importCsvFile(uri)

    shouldNotThrowAny {
      verify { useImporter.import(csvContent, any()) }
    }
  }

  test("importCsvFile should handle empty content and call useImporter.import with an empty file") {
    every { contentResolver.openInputStream(uri) } returns ByteArrayInputStream(ByteArray(0))

    target.importCsvFile(uri)

    shouldNotThrowAny {
      verify { useImporter.import("", any()) }
    }
  }

  test("importCsvFile should handle null InputStream and call useImporter.import with an empty file") {
    every { contentResolver.openInputStream(uri) } returns null

    target.importCsvFile(uri)

    shouldNotThrowAny {
      verify { useImporter.import("", any()) }
    }
  }

  test("importCsvFile should close the file it read") {
    var closed = false
    every { contentResolver.openInputStream(uri) } returns object : ByteArrayInputStream("line1".toByteArray()) {
      override fun close() {
        closed = true
      }
    }

    target.importCsvFile(uri)

    closed shouldBe true
  }

  test("importCsvFile should propagate exceptions during file reading") {
    every { contentResolver.openInputStream(uri) } throws IOException("File not found")

    shouldThrow<IOException> {
      target.importCsvFile(uri)
    }
  }
})
