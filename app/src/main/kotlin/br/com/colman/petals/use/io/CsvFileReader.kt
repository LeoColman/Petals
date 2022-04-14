package br.com.colman.petals.use.io

import android.content.ContentResolver
import android.net.Uri
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.OpenDocument
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.talhafaki.composablesweettoast.util.SweetToastUtil.SweetError
import com.talhafaki.composablesweettoast.util.SweetToastUtil.SweetSuccess

class CsvFileReader(
  private val useImporter: UseImporter,
  private val contentResolver: ContentResolver
) {

  @Composable
  fun ReadCsvFile() {
    val importResult by importLauncher()

    importResult?.onSuccess {
      SweetSuccess("Success", LENGTH_SHORT, PaddingValues(8.dp))
    }?.onFailure {
      SweetError("Error", LENGTH_LONG, PaddingValues(8.dp))
    }
  }

  @Composable
  private fun importLauncher(): State<Result<*>?> {
    val importResult = remember { mutableStateOf<Result<Any>?>(null) }

    rememberLauncherForActivityResult(OpenDocument()) { contentUri ->
      val lines = contentUri.readLines()
      importResult.value = useImporter.import(lines)
    }.launch(arrayOf("text/*"))

    return importResult
  }

  private fun Uri?.readLines() = this?.run {
    contentResolver.openInputStream(this)?.bufferedReader()?.readLines()
  }.orEmpty()
}
