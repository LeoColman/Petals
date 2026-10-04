package br.com.colman.petals.use.io.input

import android.content.ContentResolver
import android.net.Uri

class UseCsvFileImporter(
  private val useImporter: UseImporter,
  private val contentResolver: ContentResolver
) {

  fun importCsvFile(uri: Uri) {
    useImporter.import(uri.readText())
  }

  private fun Uri.readText() = contentResolver.openInputStream(this)?.bufferedReader()?.use { it.readText() }.orEmpty()
}
