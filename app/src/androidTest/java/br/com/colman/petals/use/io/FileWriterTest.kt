package br.com.colman.petals.use.io

import androidx.test.platform.app.InstrumentationRegistry
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldNotExist
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import org.junit.Before
import org.junit.Test
import java.io.File

class FileWriterTest {
  val context = InstrumentationRegistry.getInstrumentation().targetContext
  val exportsDirectory = File(context.filesDir, "exports")
  val target = FileWriter(context)

  @Before
  fun deleteExportsDirectory() {
    exportsDirectory.deleteRecursively()
  }

  @Test
  fun createsExportsDirectory() {
    exportsDirectory.shouldNotExist()

    target.write("abc")

    exportsDirectory.shouldExist()
  }

  @Test
  fun replacesExistingFile() {
    val uri1 = target.write("abc")
    val uri2 = target.write("def")

    uri1 shouldBe uri2

    val content = context.contentResolver.openInputStream(uri2)!!
    content.bufferedReader().readText() shouldBe "def"
  }

  @Test
  fun defaultFIleName() {
    val uri = target.write("xxx")

    uri.path shouldEndWith "PetalsExport.csv"
  }
}
