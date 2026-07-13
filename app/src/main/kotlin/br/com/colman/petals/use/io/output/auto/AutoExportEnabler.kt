package br.com.colman.petals.use.io.output.auto

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import br.com.colman.petals.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class AutoExportEnabler(
  private val contentResolver: ContentResolver,
  private val settingsRepository: SettingsRepository,
  private val scheduler: AutoExportScheduler,
  private val uriParser: (String) -> Uri = Uri::parse,
) {

  fun enable(treeUri: Uri, folderName: String) {
    releasePreviousGrant()

    contentResolver.takePersistableUriPermission(
      treeUri,
      Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
    )

    settingsRepository.setAutoExportTreeUri(treeUri.toString())
    settingsRepository.setAutoExportFolderName(folderName)
    settingsRepository.setAutoExportLastError(null)

    scheduler.schedule()
    scheduler.exportNow()
  }

  fun disable() {
    releasePreviousGrant()
    settingsRepository.clearAutoExport()
    scheduler.cancel()
  }

  private fun releasePreviousGrant() {
    val previousUriString = runBlocking { settingsRepository.autoExportTreeUri.first() } ?: return

    try {
      contentResolver.releasePersistableUriPermission(
        uriParser(previousUriString),
        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
      )
    } catch (e: SecurityException) {
      Timber.w(e, "Could not release previous auto-export grant, permission likely already gone")
    }
  }
}
