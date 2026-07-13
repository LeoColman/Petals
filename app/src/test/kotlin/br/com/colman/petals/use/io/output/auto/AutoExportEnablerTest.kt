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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher

private val grantFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

@OptIn(ExperimentalCoroutinesApi::class)
class AutoExportEnablerTest : FunSpec({

  fun newSettingsRepository() = SettingsRepository(
    PreferenceDataStoreFactory.create { tempfile(suffix = ".preferences_pb") }
  )

  fun enabler(
    contentResolver: ContentResolver,
    settingsRepository: SettingsRepository,
    scheduler: AutoExportScheduler,
    previousUri: Uri,
    folderName: String? = "My Folder",
  ) = AutoExportEnabler(
    contentResolver = contentResolver,
    settingsRepository = settingsRepository,
    scheduler = scheduler,
    folderNameResolver = { folderName },
    uriParser = { previousUri },
    dispatcher = UnconfinedTestDispatcher(),
  )

  test("enable() releases any previous grant, takes a new grant, persists it and schedules both jobs") {
    val settingsRepository = newSettingsRepository()
    settingsRepository.setAutoExportTreeUri("content://old-tree")

    val contentResolver = mockk<ContentResolver>(relaxed = true)
    val scheduler = mockk<AutoExportScheduler>(relaxed = true)
    val oldUri = mockk<Uri>()
    val newUri = mockk<Uri>()
    every { newUri.toString() } returns "content://new-tree"

    enabler(contentResolver, settingsRepository, scheduler, oldUri).enable(newUri)

    shouldNotThrowAny {
      verifyOrder {
        contentResolver.releasePersistableUriPermission(oldUri, grantFlags)
        contentResolver.takePersistableUriPermission(newUri, grantFlags)
        scheduler.schedule()
        scheduler.exportNow()
      }
    }

    runBlocking { settingsRepository.autoExportTreeUri.first() } shouldBe "content://new-tree"
    runBlocking { settingsRepository.autoExportFolderName.first() } shouldBe "My Folder"
  }

  test("enable() falls back to the Uri when the provider does not report a folder name") {
    val settingsRepository = newSettingsRepository()

    val scheduler = mockk<AutoExportScheduler>(relaxed = true)
    val newUri = mockk<Uri>()
    every { newUri.toString() } returns "content://new-tree"

    enabler(mockk(relaxed = true), settingsRepository, scheduler, mockk(), folderName = null).enable(newUri)

    runBlocking { settingsRepository.autoExportFolderName.first() } shouldBe "content://new-tree"
  }

  test("disable() releases the grant, clears settings and cancels both jobs") {
    val settingsRepository = newSettingsRepository()
    settingsRepository.setAutoExportTreeUri("content://old-tree")

    val contentResolver = mockk<ContentResolver>(relaxed = true)
    val scheduler = mockk<AutoExportScheduler>(relaxed = true)
    val oldUri = mockk<Uri>()

    enabler(contentResolver, settingsRepository, scheduler, oldUri).disable()

    shouldNotThrowAny {
      verify { contentResolver.releasePersistableUriPermission(oldUri, grantFlags) }
      verify { scheduler.cancel() }
    }
    runBlocking { settingsRepository.autoExportTreeUri.first() } shouldBe null
  }

  test("a SecurityException releasing the previous grant does not propagate") {
    val settingsRepository = newSettingsRepository()
    settingsRepository.setAutoExportTreeUri("content://old-tree")

    val contentResolver = mockk<ContentResolver>()
    val scheduler = mockk<AutoExportScheduler>(relaxed = true)
    every { contentResolver.releasePersistableUriPermission(any(), any()) } throws SecurityException("gone")

    val target = enabler(contentResolver, settingsRepository, scheduler, mockk())

    shouldNotThrowAny { target.disable() }
  }
})
