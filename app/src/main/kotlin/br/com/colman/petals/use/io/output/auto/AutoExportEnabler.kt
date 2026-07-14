package br.com.colman.petals.use.io.output.auto

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import br.com.colman.petals.settings.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber

private const val readWriteFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

/**
 * [enable] and [disable] are suspend and hop to [dispatcher] because everything they
 * touch is disk IO: reading the previous tree Uri out of DataStore, querying the
 * provider for the folder's display name, and SettingsRepository's blocking setters.
 * They are driven straight from a Compose switch, so doing any of that on the main
 * thread janks the UI and can ANR on a slow device.
 */
class AutoExportEnabler(
  private val contentResolver: ContentResolver,
  private val settingsRepository: SettingsRepository,
  private val scheduler: AutoExportScheduler,
  private val folderNameResolver: (Uri) -> String?,
  private val uriParser: (String) -> Uri = Uri::parse,
  private val dispatcher: CoroutineDispatcher = IO,
) {

  constructor(
    context: Context,
    settingsRepository: SettingsRepository,
    scheduler: AutoExportScheduler,
  ) : this(
    context.contentResolver,
    settingsRepository,
    scheduler,
    { DocumentFile.fromTreeUri(context, it)?.name },
  )

  suspend fun enable(treeUri: Uri): Unit = withContext(dispatcher) {
    releasePreviousGrant()

    contentResolver.takePersistableUriPermission(treeUri, readWriteFlags)

    settingsRepository.setAutoExportTreeUri(treeUri.toString())
    settingsRepository.setAutoExportFolderName(folderNameResolver(treeUri) ?: treeUri.toString())
    settingsRepository.setAutoExportLastError(null)

    scheduler.schedule()
    scheduler.exportNow()
  }

  suspend fun disable(): Unit = withContext(dispatcher) {
    releasePreviousGrant()
    settingsRepository.clearAutoExport()
    scheduler.cancel()
  }

  private suspend fun releasePreviousGrant() {
    val previousUriString = settingsRepository.autoExportTreeUri.first() ?: return

    try {
      contentResolver.releasePersistableUriPermission(uriParser(previousUriString), readWriteFlags)
    } catch (e: SecurityException) {
      Timber.w(e, "Could not release previous auto-export grant, permission likely already gone")
    }
  }
}
