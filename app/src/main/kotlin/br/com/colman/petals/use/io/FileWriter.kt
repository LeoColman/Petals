package br.com.colman.petals.use.io

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider.getUriForFile
import br.com.colman.petals.BuildConfig.APPLICATION_ID
import java.io.File

class FileWriter(private val context: Context) {

  private val fileName = "PetalsExport.csv"

  private val exportsDir by lazy {
    File(context.filesDir, "exports").apply {
      if (!exists()) mkdirs()
    }
  }

  fun write(content: String): Uri {
    val exportedFile = createFile(content)

    return getUriForFile(context, APPLICATION_ID, exportedFile)
  }

  private fun createFile(content: String) = File(exportsDir, fileName).apply { writeText(content) }
}
