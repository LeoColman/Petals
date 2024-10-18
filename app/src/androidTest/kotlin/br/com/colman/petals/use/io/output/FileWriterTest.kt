package br.com.colman.petals.use.io.output

import androidx.test.platform.app.InstrumentationRegistry
import br.com.colman.kotest.FunSpec
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldNotExist
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldEndWith
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.io.File
import java.time.LocalDate

class FileWriterTest : FunSpec({

  val context = InstrumentationRegistry.getInstrumentation().targetContext
  val exportsDirectory = File(context.filesDir, "exports")
  val target = FileWriter(context)

  beforeEach { exportsDirectory.deleteRecursively() }

  test("Creates exports directory when it doesn't exist") {
    exportsDirectory.shouldNotExist()

    target.write(FakeContent1)

    exportsDirectory.shouldExist()
  }

  test("Replaces an existing file") {
    val uri1 = target.write(FakeContent1)
    val uri2 = target.write(FakeContent2)

    uri1 shouldBe uri2

    val content = context.contentResolver.openInputStream(uri2)!!
    content.bufferedReader().readText() shouldBe FakeContent2
  }

  test("Uses PetalsExport-date.csv as the default file name") {
    val uri = target.write(FakeContent1)

    uri.path shouldEndWith "PetalsExport-${LocalDate.now()}.csv"
  }

  test("Generates different file names on different dates") {
    val fixedDate = LocalDate.of(2024, 2, 9)
    mockkStatic(LocalDate::class)
    every { LocalDate.now() } returns fixedDate

    val uri1 = target.write(FakeContent1)

    val newDate = fixedDate.plusDays(1)
    every { LocalDate.now() } returns newDate

    val uri2 = target.write(FakeContent2)

    uri1 shouldNotBe uri2

    uri1.path shouldEndWith "PetalsExport-$fixedDate.csv"
    uri2.path shouldEndWith "PetalsExport-$newDate.csv"

    unmockkStatic(LocalDate::class)
  }

  isolationMode = InstancePerTest
})

private const val FakeContent1 = "abc"
private const val FakeContent2 = "def"
