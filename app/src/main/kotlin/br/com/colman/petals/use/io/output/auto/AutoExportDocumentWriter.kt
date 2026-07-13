package br.com.colman.petals.use.io.output.auto

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.charset.StandardCharsets.UTF_8

private const val fileName = "PetalsExport.csv"
private const val tmpFileName = "PetalsExport-tmp.csv"
private const val mimeType = "text/csv"

// createFile() is given the name WITHOUT the extension on purpose. Providers derive
// the extension from the MIME type and append it themselves: DocumentFile's raw
// (file://) backing appends unconditionally, so passing "PetalsExport.csv" there
// yields "PetalsExport.csv.csv" — after which findFile("PetalsExport.csv") misses,
// createFile refuses to clobber the existing file, and every run throws. Passing the
// base name lands on "PetalsExport.csv" for both raw and content:// providers.
private const val baseName = "PetalsExport"
private const val tmpBaseName = "PetalsExport-tmp"

/**
 * Writes the auto-export CSV into a SAF-picked folder, overwriting the same
 * fixed [fileName] every time.
 *
 * `openOutputStream(uri, "wt")` is only guaranteed to open for writing; the
 * SAF contract does not guarantee the `t` (truncate) is honored by every
 * DocumentsProvider. [write] verifies the result and, if truncation was
 * ignored (or the provider throws [UnsupportedOperationException] outright),
 * falls back to writing a `.tmp` sibling, deleting the stale file, then
 * renaming.
 *
 * That order is deliberate but it is NOT crash-atomic: a crash between the
 * delete and the rename leaves only the tmp file, and no PetalsExport.csv at
 * all until the next run recreates it. What the order does buy is that the
 * export is never left truncated or half-written in place — the stale file is
 * only removed once the replacement is durably on disk. Losing the file for a
 * day is acceptable because it is derived data: the SQLDelight database is the
 * source of truth and the next run rebuilds it from scratch.
 */
class AutoExportDocumentWriter(
  private val contentResolver: ContentResolver,
  private val documentFileFromTreeUri: (Uri) -> DocumentFile?,
  private val documentFileFromSingleUri: (Uri) -> DocumentFile?,
) {

  constructor(context: Context) : this(
    context.contentResolver,
    { DocumentFile.fromTreeUri(context, it) },
    { DocumentFile.fromSingleUri(context, it) },
  )

  /**
   * SAF persisted-permission grants only exist for `content:` URIs obtained
   * through `ACTION_OPEN_DOCUMENT_TREE`. A `file:` URI (used by the
   * instrumented test against a local temp directory) never has one and
   * never needs one — plain filesystem permissions apply instead. In
   * production [treeUri] is always the `content://.../tree/...` URI handed
   * back by the picker, so this early-return branch never triggers there.
   */
  fun hasWritePermission(treeUri: Uri): Boolean {
    if (treeUri.scheme == "file") return true
    return contentResolver.persistedUriPermissions.any { it.uri == treeUri && it.isWritePermission }
  }

  fun write(treeUri: Uri, cachedDocumentUri: Uri?, content: String): Uri {
    if (!hasWritePermission(treeUri)) throw SecurityException("Lost write permission for $treeUri")

    val bytes = content.toByteArray(UTF_8)
    val targetDocument = resolveTargetDocument(treeUri, cachedDocumentUri)

    return if (tryWriteWithTruncate(targetDocument, bytes)) {
      targetDocument.uri
    } else {
      recreate(treeUri, targetDocument, bytes)
    }
  }

  private fun resolveTargetDocument(treeUri: Uri, cachedDocumentUri: Uri?): DocumentFile {
    val cached = cachedDocumentUri?.let(documentFileFromSingleUri)
    if (cached != null && cached.exists() && cached.canWrite()) return cached

    val tree = documentFileFromTreeUri(treeUri) ?: throw FileNotFoundException("Tree not found: $treeUri")

    return tree.findExisting(fileName, baseName)
      ?: tree.createFile(mimeType, baseName)
      ?: throw IOException("Could not create $fileName in $treeUri")
  }

  /**
   * A provider that appends the extension lands on [withExtension]; one that takes the
   * display name verbatim lands on [base]. Look for both, so a run never fails to find
   * the file it wrote last time and create a duplicate beside it.
   */
  private fun DocumentFile.findExisting(withExtension: String, base: String) =
    findFile(withExtension) ?: findFile(base)

  private fun tryWriteWithTruncate(document: DocumentFile, bytes: ByteArray): Boolean {
    return try {
      writeBytes(document.uri, bytes)
      !wasTruncationIgnored(document, bytes.size)
    } catch (e: UnsupportedOperationException) {
      Timber.w(e, "Provider does not honor \"wt\" truncate for ${document.uri}")
      false
    }
  }

  private fun wasTruncationIgnored(document: DocumentFile, expectedSize: Int): Boolean {
    // Some providers report 0 or -1 (unknown) for length() right after a write.
    // Only a length strictly greater than what we wrote proves leftover bytes.
    val actual = document.length()
    return actual > expectedSize
  }

  private fun writeBytes(uri: Uri, bytes: ByteArray) {
    val stream = contentResolver.openOutputStream(uri, "wt")
      ?: throw IOException("openOutputStream returned null for $uri")
    stream.use { it.write(bytes) }
  }

  private fun recreate(treeUri: Uri, staleDocument: DocumentFile, bytes: ByteArray): Uri {
    val tmpDocument = resolveTmpDocument(treeUri)

    writeBytes(tmpDocument.uri, bytes)

    // Fail here rather than at the rename: if the provider refuses to delete, the
    // rename below collides with a name that still exists, and the error the user
    // is shown would point at the rename instead of the actual cause.
    if (!staleDocument.delete()) {
      throw IOException("Could not delete stale ${staleDocument.uri} to replace it with $fileName")
    }

    return renameTmpToFinal(tmpDocument.uri)
  }

  private fun resolveTmpDocument(treeUri: Uri): DocumentFile {
    val tree = documentFileFromTreeUri(treeUri) ?: throw FileNotFoundException("Tree not found: $treeUri")

    deleteLeftoverTmp(tree)

    return tree.createFile(mimeType, tmpBaseName)
      ?: throw IOException("Could not create $tmpFileName in $treeUri")
  }

  /**
   * A tmp file left behind by an earlier crash. If it cannot be removed, [DocumentFile.createFile]
   * either returns null or silently makes a duplicate, depending on the provider — so fail loudly
   * here instead of guessing which of those happened later.
   */
  private fun deleteLeftoverTmp(tree: DocumentFile) {
    val leftoverTmp = tree.findExisting(tmpFileName, tmpBaseName) ?: return
    if (!leftoverTmp.delete()) throw IOException("Could not delete leftover ${leftoverTmp.uri}")
  }

  private fun renameTmpToFinal(tmpUri: Uri): Uri {
    return try {
      DocumentsContract.renameDocument(contentResolver, tmpUri, fileName)
        ?: throw IOException("renameDocument returned null for $tmpUri")
    } catch (e: UnsupportedOperationException) {
      Timber.w(e, "Provider does not support renameDocument for $tmpUri")
      throw IOException("Rename not supported by provider", e)
    }
  }
}
