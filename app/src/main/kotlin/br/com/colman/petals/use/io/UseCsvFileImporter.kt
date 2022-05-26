package br.com.colman.petals.use.io

import android.content.ContentResolver
import android.net.Uri
import timber.log.Timber

class UseCsvFileImporter(
  private val useImporter: UseImporter,
  private val contentResolver: ContentResolver
) {

  fun importCsvFile(uri: Uri) {
    Timber.d("Estou aqui $uri ${uri.path}")
    val lines = uri.readLines()
    useImporter.import(lines)
  }

  private fun Uri?.readLines() = this?.run {
    contentResolver.openInputStream(this)?.bufferedReader()?.readLines()
  }.orEmpty()
}
