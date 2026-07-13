package br.com.colman.petals.use.io.output.auto

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import br.com.colman.petals.settings.SettingsRepository
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val grantFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

class AutoExportEnablerTest : FunSpec({

  fun newSettingsRepository() = SettingsRepository(
    PreferenceDataStoreFactory.create { tempfile(suffix = ".preferences_pb") }
  )

  test("enable() releases any previous grant, takes a new grant, persists it and schedules both jobs") {
    val settingsRepository = newSettingsRepository()
    settingsRepository.setAutoExportTreeUri("content://old-tree")

    val contentResolver = mockk<ContentResolver>(relaxed = true)
    val scheduler = mockk<AutoExportScheduler>(relaxed = true)
    val oldUri = mockk<Uri>()
    val newUri = mockk<Uri>()
    every { newUri.toString() } returns "content://new-tree"

    val target = AutoExportEnabler(contentResolver, settingsRepository, scheduler) { oldUri }

    target.enable(newUri, "My Folder")

    verifyOrder {
      contentResolver.releasePersistableUriPermission(oldUri, grantFlags)
      contentResolver.takePersistableUriPermission(newUri, grantFlags)
      scheduler.schedule()
      scheduler.exportNow()
    }

    runBlocking { settingsRepository.autoExportTreeUri.first() } shouldBe "content://new-tree"
    runBlocking { settingsRepository.autoExportFolderName.first() } shouldBe "My Folder"
  }

  test("disable() releases the grant, clears settings and cancels both jobs") {
    val settingsRepository = newSettingsRepository()
    settingsRepository.setAutoExportTreeUri("content://old-tree")

    val contentResolver = mockk<ContentResolver>(relaxed = true)
    val scheduler = mockk<AutoExportScheduler>(relaxed = true)
    val oldUri = mockk<Uri>()

    val target = AutoExportEnabler(contentResolver, settingsRepository, scheduler) { oldUri }

    target.disable()

    verify { contentResolver.releasePersistableUriPermission(oldUri, grantFlags) }
    verify { scheduler.cancel() }
    runBlocking { settingsRepository.autoExportTreeUri.first() } shouldBe null
  }

  test("a SecurityException releasing the previous grant does not propagate") {
    val settingsRepository = newSettingsRepository()
    settingsRepository.setAutoExportTreeUri("content://old-tree")

    val contentResolver = mockk<ContentResolver>()
    val scheduler = mockk<AutoExportScheduler>(relaxed = true)
    val oldUri = mockk<Uri>()
    every { contentResolver.releasePersistableUriPermission(any(), any()) } throws SecurityException("gone")

    val target = AutoExportEnabler(contentResolver, settingsRepository, scheduler) { oldUri }

    shouldNotThrowAny { target.disable() }
  }
})
