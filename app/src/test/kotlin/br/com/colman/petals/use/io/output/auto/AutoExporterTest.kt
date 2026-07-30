package br.com.colman.petals.use.io.output.auto

import android.net.Uri
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.io.output.UseCsvSerializer
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.IOException

class AutoExporterTest : FunSpec({

  fun newSettingsRepository() = SettingsRepository(
    PreferenceDataStoreFactory.create { tempfile(suffix = ".preferences_pb") }
  )

  val fakeUri = mockk<Uri>()
  val uriParser: (String) -> Uri = { fakeUri }

  test("returns Success and never touches the writer when no tree Uri is configured") {
    val settingsRepository = newSettingsRepository()
    val target = AutoExporter(settingsRepository, mockk(), mockk(), uriParser)

    runBlocking { target.export() } shouldBe AutoExportResult.Success
  }

  test("records the document Uri and success timestamp, and clears the error, on success") {
    val settingsRepository = newSettingsRepository()
    settingsRepository.setAutoExportTreeUri("content://tree")
    settingsRepository.setAutoExportLastError("io")

    val serializer = mockk<UseCsvSerializer>()
    val writer = mockk<AutoExportDocumentWriter>()
    val resultUri = mockk<Uri>()
    every { resultUri.toString() } returns "content://tree/PetalsExport.csv"

    coEvery { serializer.computeUseCsv() } returns "csv"
    every { writer.write(any(), any(), "csv") } returns resultUri

    val target = AutoExporter(settingsRepository, serializer, writer, uriParser)

    runBlocking { target.export() } shouldBe AutoExportResult.Success
    runBlocking { settingsRepository.autoExportDocumentUri.first() } shouldBe "content://tree/PetalsExport.csv"
    runBlocking { settingsRepository.autoExportLastError.first() } shouldBe null
  }

  test("maps SecurityException to PermissionLost and records the permission error") {
    val settingsRepository = newSettingsRepository()
    settingsRepository.setAutoExportTreeUri("content://tree")

    val serializer = mockk<UseCsvSerializer>()
    val writer = mockk<AutoExportDocumentWriter>()
    coEvery { serializer.computeUseCsv() } returns "csv"
    every { writer.write(any(), any(), any()) } throws SecurityException("gone")

    val target = AutoExporter(settingsRepository, serializer, writer, uriParser)

    runBlocking { target.export() } shouldBe AutoExportResult.PermissionLost
    runBlocking { settingsRepository.autoExportLastError.first() } shouldBe "permission"
  }

  test("maps IOException to Transient and records the io error") {
    val settingsRepository = newSettingsRepository()
    settingsRepository.setAutoExportTreeUri("content://tree")

    val serializer = mockk<UseCsvSerializer>()
    val writer = mockk<AutoExportDocumentWriter>()
    coEvery { serializer.computeUseCsv() } returns "csv"
    every { writer.write(any(), any(), any()) } throws IOException("offline")

    val target = AutoExporter(settingsRepository, serializer, writer, uriParser)

    runBlocking { target.export() } shouldBe AutoExportResult.Transient
    runBlocking { settingsRepository.autoExportLastError.first() } shouldBe "io"
  }
})
