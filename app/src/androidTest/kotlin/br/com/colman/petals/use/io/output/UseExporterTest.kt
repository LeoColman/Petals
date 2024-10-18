package br.com.colman.petals.use.io.output

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import br.com.colman.kotest.FunSpec
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot

class UseExporterTest : FunSpec({

  test("exportUses should compute CSV, write to file, and launch share intent") {
    val useCsvSerializer = mockk<UseCsvSerializer>()
    val fileWriter = mockk<FileWriter>()
    val launcher = mockk<ManagedActivityResultLauncher<Intent, ActivityResult>>()
    val contentUri = mockk<Uri>()
    val csvContent = "csv content"

    coEvery { useCsvSerializer.computeUseCsv() } returns csvContent
    every { fileWriter.write(csvContent) } returns contentUri

    val intentSlot = slot<Intent>()
    every { launcher.launch(capture(intentSlot)) } just Runs

    val target = UseExporter(useCsvSerializer, fileWriter)

    target.exportUses(launcher)

    coVerifySequence {
      useCsvSerializer.computeUseCsv()
      fileWriter.write(csvContent)
      launcher.launch(any())
    }

    assertSoftly(intentSlot.captured) {
      action shouldBe Intent.ACTION_SEND
      type shouldBe "text/plain"
      (flags and Intent.FLAG_GRANT_READ_URI_PERMISSION) shouldNotBe 0
      extras?.getParcelable(Intent.EXTRA_STREAM, Uri::class.java) shouldBe contentUri
    }
  }
})
