package br.com.colman.petals.use.io

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.GET_META_DATA
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import timber.log.Timber

class UseExporter(
  private val useCsvSerializer: UseCsvSerializer,
  private val fileWriter: FileWriter,
  private val context: Context
) {

  suspend fun exportUses(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val useCsv = useCsvSerializer.computeUseCsv()
    val contentUri = fileWriter.write(useCsv)

    launcher.launchShareFile(contentUri)
  }

  private fun ManagedActivityResultLauncher<Intent, ActivityResult>.launchShareFile(uri: Uri) {
    val intent = Intent().apply {
      data = uri
      flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_STREAM, uri)
    }
    val activityInfo = intent.resolveActivityInfo(context.packageManager, GET_META_DATA)
    if(activityInfo?.exported == true) {
      launch(intent)
    } else {
      Timber.e("No application for this context exists")
    }
  }
}
