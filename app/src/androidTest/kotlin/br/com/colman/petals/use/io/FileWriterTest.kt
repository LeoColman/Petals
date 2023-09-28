package br.com.colman.petals.use.io

import androidx.test.platform.app.InstrumentationRegistry
import br.com.colman.kotest.FunSpec
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldNotExist
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import java.io.File
import java.time.LocalDate

class FileWriterTest : FunSpec({

  val context = InstrumentationRegistry.getInstrumentation().targetContext
  val exportsDirectory = File(context.filesDir, "exports")
  val target = FileWriter(context)

  beforeEach { exportsDirectory.deleteRecursively() }
  isolationMode = InstancePerTest

  test("Creates exports directory when it doesn't exist") {
    exportsDirectory.shouldNotExist()

    target.write("abc")

    exportsDirectory.shouldExist()
  }

  test("Replaces an existing file") {
    val uri1 = target.write("abc")
    val uri2 = target.write("def")

    uri1 shouldBe uri2

    val content = context.contentResolver.openInputStream(uri2)!!
    content.bufferedReader().readText() shouldBe "def"
  }

  test("Uses PetalsExport.csv as the default file name") {
    val uri = target.write("xxx")

    uri.path shouldEndWith "PetalsExport-${LocalDate.now()}.csv"
  }
})
