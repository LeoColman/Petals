package br.com.colman.petals.use.io.output

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider.getUriForFile
import br.com.colman.petals.BuildConfig.APPLICATION_ID
import java.io.File
import java.time.LocalDate

class FileWriter(
  private val exportDirectory: File,
  private val fileToUriConverter: (File) -> Uri,
) {

  init {
    if (!exportDirectory.exists()) exportDirectory.mkdirs()
  }

  constructor(context: Context) : this(
    File(context.filesDir, "exports"),
    { getUriForFile(context, APPLICATION_ID, it) }
  )

  fun write(content: String): Uri {
    val exportedFile = createAndWriteFile(content)
    return fileToUriConverter(exportedFile)
  }

  private fun createAndWriteFile(content: String): File {
    val fileName = generateFileName()
    return File(exportDirectory, fileName).apply { writeText(content) }
  }

  private fun generateFileName(): String {
    val date = LocalDate.now()
    return "PetalsExport-$date.csv"
  }
}
