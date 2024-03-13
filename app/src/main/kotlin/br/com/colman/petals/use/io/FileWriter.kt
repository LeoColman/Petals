package br.com.colman.petals.use.io

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider.getUriForFile
import br.com.colman.petals.BuildConfig.APPLICATION_ID
import java.io.File
import java.time.LocalDate

class FileWriter(private val context: Context) {

  private val exportsDir by lazy { getExportDirectory() }

  fun write(content: String): Uri {
    val exportedFile = createAndWriteFile(content)
    return getUriForFile(context, APPLICATION_ID, exportedFile)
  }

  private fun createAndWriteFile(content: String): File {
    val fileName = generateFileName()
    return File(exportsDir, fileName).apply { writeText(content) }
  }

  private fun generateFileName(): String {
    val date = LocalDate.now()
    return "PetalsExport-$date.csv"
  }

  private fun getExportDirectory() = File(context.filesDir, "exports").apply {
    if (!exists()) mkdirs()
  }
}
