package br.com.colman.petals.use.io.output

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult

class UseExporter(
  private val useCsvSerializer: UseCsvSerializer,
  private val fileWriter: FileWriter
) {

  suspend fun exportUses(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val useCsv = useCsvSerializer.computeUseCsv()
    val contentUri = fileWriter.write(useCsv)

    launcher.launchShareFile(contentUri)
  }

  private fun ManagedActivityResultLauncher<Intent, ActivityResult>.launchShareFile(uri: Uri) {
    val intent = Intent().apply {
      type = "text/plain"
      flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_STREAM, uri)
    }
    launch(intent)
  }
}
