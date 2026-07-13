package br.com.colman.petals.use.io.output.auto

import android.net.Uri
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.io.output.UseCsvSerializer
import kotlinx.coroutines.flow.first
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException
import java.time.Instant

sealed interface AutoExportResult {
  data object Success : AutoExportResult
  data object PermissionLost : AutoExportResult
  data object Transient : AutoExportResult
}

class AutoExporter(
  private val settingsRepository: SettingsRepository,
  private val useCsvSerializer: UseCsvSerializer,
  private val documentWriter: AutoExportDocumentWriter,
  private val uriParser: (String) -> Uri = Uri::parse,
) {

  suspend fun export(): AutoExportResult {
    val treeUriString = settingsRepository.autoExportTreeUri.first() ?: return AutoExportResult.Success

    return try {
      writeExport(treeUriString)
      AutoExportResult.Success
    } catch (e: SecurityException) {
      Timber.w(e, "Lost permission to auto-export folder")
      settingsRepository.setAutoExportLastError("permission")
      AutoExportResult.PermissionLost
    } catch (e: FileNotFoundException) {
      Timber.w(e, "Auto-export folder no longer exists")
      settingsRepository.setAutoExportLastError("permission")
      AutoExportResult.PermissionLost
    } catch (e: IOException) {
      Timber.w(e, "Transient error during auto-export")
      settingsRepository.setAutoExportLastError("io")
      AutoExportResult.Transient
    }
  }

  private suspend fun writeExport(treeUriString: String) {
    val treeUri = uriParser(treeUriString)
    val cachedDocumentUri = settingsRepository.autoExportDocumentUri.first()?.let(uriParser)

    val content = useCsvSerializer.computeUseCsv()
    val writtenUri = documentWriter.write(treeUri, cachedDocumentUri, content)

    settingsRepository.setAutoExportDocumentUri(writtenUri.toString())
    settingsRepository.setAutoExportLastSuccessAt(Instant.now().toEpochMilli())
    settingsRepository.setAutoExportLastError(null)
  }
}
