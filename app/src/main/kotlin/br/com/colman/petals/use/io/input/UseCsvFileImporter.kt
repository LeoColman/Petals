package br.com.colman.petals.use.io.input

import android.content.ContentResolver
import android.net.Uri

class UseCsvFileImporter(
  private val useImporter: UseImporter,
  private val contentResolver: ContentResolver
) {

  /** Imports the file at [uri], and returns how many uses it saved, or why it couldn't be read or imported. */
  fun importCsvFile(uri: Uri): Result<Int> = runCatching {
    useImporter.import(uri.readText()).getOrThrow()
  }

  private fun Uri.readText() = contentResolver.openInputStream(this)?.bufferedReader()?.use { it.readText() }.orEmpty()
}
