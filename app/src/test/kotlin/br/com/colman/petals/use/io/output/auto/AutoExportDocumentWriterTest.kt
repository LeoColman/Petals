package br.com.colman.petals.use.io.output.auto

import android.content.ContentResolver
import android.content.UriPermission
import android.net.Uri
import android.provider.DocumentsContract
import androidx.documentfile.provider.DocumentFile
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verifyOrder
import java.io.ByteArrayOutputStream
import java.io.IOException

private const val fileName = "PetalsExport.csv"
private const val tmpFileName = "PetalsExport-tmp.csv"
private const val baseName = "PetalsExport"
private const val tmpBaseName = "PetalsExport-tmp"

class AutoExportDocumentWriterTest : FunSpec({

  fun treeUri() = mockk<Uri>(relaxed = true) { every { scheme } returns "content" }

  fun grantedPermission(uri: Uri) = mockk<UriPermission> {
    every { this@mockk.uri } returns uri
    every { isWritePermission } returns true
  }

  test("throws SecurityException when write permission is missing") {
    val contentResolver = mockk<ContentResolver> { every { persistedUriPermissions } returns emptyList() }
    val target = AutoExportDocumentWriter(contentResolver, { mockk() }, { mockk() })

    shouldThrow<SecurityException> { target.write(treeUri(), null, "content") }
  }

  test("uses the cached document Uri when it still exists and is writable") {
    val tree = treeUri()
    val contentResolver = mockk<ContentResolver> {
      every { persistedUriPermissions } returns listOf(grantedPermission(tree))
    }
    val cachedUri = mockk<Uri>()
    val cachedDocument = mockk<DocumentFile> {
      every { exists() } returns true
      every { canWrite() } returns true
      every { uri } returns cachedUri
      every { length() } returns 7L
    }
    every { contentResolver.openOutputStream(cachedUri, "wt") } returns ByteArrayOutputStream()

    val target = AutoExportDocumentWriter(contentResolver, { mockk() }, { cachedDocument })

    target.write(tree, cachedUri, "content") shouldBe cachedUri
  }

  test("falls back to findFile then createFile when the cached Uri is not usable") {
    val tree = treeUri()
    val contentResolver = mockk<ContentResolver> {
      every { persistedUriPermissions } returns listOf(grantedPermission(tree))
    }

    val existingUri = mockk<Uri>()
    val existingDocument = mockk<DocumentFile> {
      every { uri } returns existingUri
      every { length() } returns 7L
    }
    val treeDocument = mockk<DocumentFile> { every { findFile(fileName) } returns existingDocument }
    every { contentResolver.openOutputStream(existingUri, "wt") } returns ByteArrayOutputStream()

    val target = AutoExportDocumentWriter(contentResolver, { treeDocument }, { null })

    target.write(tree, null, "content") shouldBe existingUri
  }

  test("creates a new file when none exists, and throws IOException if createFile returns null") {
    val tree = treeUri()
    val contentResolver = mockk<ContentResolver> {
      every { persistedUriPermissions } returns listOf(grantedPermission(tree))
    }

    val treeDocumentNoCreate = mockk<DocumentFile> {
      every { findFile(fileName) } returns null
      every { findFile(baseName) } returns null
      every { createFile("text/csv", baseName) } returns null
    }
    val target = AutoExportDocumentWriter(contentResolver, { treeDocumentNoCreate }, { null })

    shouldThrow<IOException> { target.write(tree, null, "content") }
  }

  // Regression: passing the name WITH the extension makes providers that append the
  // extension themselves produce "PetalsExport.csv.csv", after which findFile misses,
  // createFile refuses to overwrite, and every subsequent export throws.
  test("creates the file with the base name, letting the provider append the extension") {
    val tree = treeUri()
    val contentResolver = mockk<ContentResolver> {
      every { persistedUriPermissions } returns listOf(grantedPermission(tree))
    }

    val createdUri = mockk<Uri>()
    val createdDocument = mockk<DocumentFile> {
      every { uri } returns createdUri
      every { length() } returns 7L
    }
    val treeDocument = mockk<DocumentFile> {
      every { findFile(fileName) } returns null
      every { findFile(baseName) } returns null
      every { createFile("text/csv", baseName) } returns createdDocument
    }
    every { contentResolver.openOutputStream(createdUri, "wt") } returns ByteArrayOutputStream()

    val target = AutoExportDocumentWriter(contentResolver, { treeDocument }, { null })

    target.write(tree, null, "content") shouldBe createdUri
  }

  // A provider that took the display name verbatim leaves a file with no extension.
  // Finding it is what stops the next run from creating a duplicate beside it.
  test("finds an existing file stored under the base name, without an extension") {
    val tree = treeUri()
    val contentResolver = mockk<ContentResolver> {
      every { persistedUriPermissions } returns listOf(grantedPermission(tree))
    }

    val existingUri = mockk<Uri>()
    val existingDocument = mockk<DocumentFile> {
      every { uri } returns existingUri
      every { length() } returns 7L
    }
    val treeDocument = mockk<DocumentFile> {
      every { findFile(fileName) } returns null
      every { findFile(baseName) } returns existingDocument
    }
    every { contentResolver.openOutputStream(existingUri, "wt") } returns ByteArrayOutputStream()

    val target = AutoExportDocumentWriter(contentResolver, { treeDocument }, { null })

    target.write(tree, null, "content") shouldBe existingUri
  }

  test("throws IOException when openOutputStream returns null") {
    val tree = treeUri()
    val contentResolver = mockk<ContentResolver> {
      every { persistedUriPermissions } returns listOf(grantedPermission(tree))
    }

    val documentUri = mockk<Uri>()
    val document = mockk<DocumentFile> { every { uri } returns documentUri }
    val treeDocument = mockk<DocumentFile> { every { findFile(fileName) } returns document }
    every { contentResolver.openOutputStream(documentUri, "wt") } returns null

    val target = AutoExportDocumentWriter(contentResolver, { treeDocument }, { null })

    shouldThrow<IOException> { target.write(tree, null, "content") }
  }

  test("recreates via a tmp file when truncate is ignored, writing the tmp file before deleting the stale one") {
    mockkStatic(DocumentsContract::class)
    try {
      val tree = treeUri()
      val contentResolver = mockk<ContentResolver> {
        every { persistedUriPermissions } returns listOf(grantedPermission(tree))
      }

      val staleUri = mockk<Uri>()
      val staleDocument = mockk<DocumentFile>(relaxed = true) {
        every { uri } returns staleUri
        every { length() } returns 999L // longer than what we're about to write: truncate ignored
        every { delete() } returns true
      }
      val tmpUri = mockk<Uri>()
      val tmpDocument = mockk<DocumentFile>(relaxed = true) { every { uri } returns tmpUri }
      val renamedUri = mockk<Uri>()

      val treeDocument = mockk<DocumentFile> {
        every { findFile(fileName) } returns staleDocument
        every { findFile(tmpFileName) } returns null
        every { findFile(tmpBaseName) } returns null
        every { createFile("text/csv", tmpBaseName) } returns tmpDocument
      }

      every { contentResolver.openOutputStream(staleUri, "wt") } returns ByteArrayOutputStream()
      every { contentResolver.openOutputStream(tmpUri, "wt") } returns ByteArrayOutputStream()
      every { DocumentsContract.renameDocument(contentResolver, tmpUri, fileName) } returns renamedUri

      val target = AutoExportDocumentWriter(contentResolver, { treeDocument }, { null })

      target.write(tree, null, "content") shouldBe renamedUri

      verifyOrder {
        contentResolver.openOutputStream(tmpUri, "wt")
        staleDocument.delete()
        DocumentsContract.renameDocument(contentResolver, tmpUri, fileName)
      }
    } finally {
      unmockkStatic(DocumentsContract::class)
    }
  }

  // Without this, the rename below collides with a name that still exists and the user
  // is shown an error about the rename rather than the delete that actually failed.
  test("throws IOException when the provider refuses to delete the stale file") {
    mockkStatic(DocumentsContract::class)
    try {
      val tree = treeUri()
      val contentResolver = mockk<ContentResolver> {
        every { persistedUriPermissions } returns listOf(grantedPermission(tree))
      }

      val staleUri = mockk<Uri>()
      val staleDocument = mockk<DocumentFile>(relaxed = true) {
        every { uri } returns staleUri
        every { length() } returns 999L
        every { delete() } returns false
      }
      val tmpDocument = mockk<DocumentFile>(relaxed = true) { every { uri } returns mockk() }

      val treeDocument = mockk<DocumentFile> {
        every { findFile(fileName) } returns staleDocument
        every { findFile(tmpFileName) } returns null
        every { findFile(tmpBaseName) } returns null
        every { createFile("text/csv", tmpBaseName) } returns tmpDocument
      }
      every { contentResolver.openOutputStream(any(), "wt") } returns ByteArrayOutputStream()

      val target = AutoExportDocumentWriter(contentResolver, { treeDocument }, { null })

      shouldThrow<IOException> { target.write(tree, null, "content") }
    } finally {
      unmockkStatic(DocumentsContract::class)
    }
  }

  test("throws IOException when a leftover tmp file cannot be deleted") {
    val tree = treeUri()
    val contentResolver = mockk<ContentResolver> {
      every { persistedUriPermissions } returns listOf(grantedPermission(tree))
    }

    val staleUri = mockk<Uri>()
    val staleDocument = mockk<DocumentFile>(relaxed = true) {
      every { uri } returns staleUri
      every { length() } returns 999L
    }
    val leftoverTmp = mockk<DocumentFile>(relaxed = true) { every { delete() } returns false }

    val treeDocument = mockk<DocumentFile> {
      every { findFile(fileName) } returns staleDocument
      every { findFile(tmpFileName) } returns leftoverTmp
    }
    every { contentResolver.openOutputStream(staleUri, "wt") } returns ByteArrayOutputStream()

    val target = AutoExportDocumentWriter(contentResolver, { treeDocument }, { null })

    shouldThrow<IOException> { target.write(tree, null, "content") }
  }

  test("does not recreate when length() reports the provider sentinel values 0 or -1") {
    listOf(0L, -1L).forEach { sentinel ->
      val tree = treeUri()
      val contentResolver = mockk<ContentResolver> {
        every { persistedUriPermissions } returns listOf(grantedPermission(tree))
      }

      val documentUri = mockk<Uri>()
      val document = mockk<DocumentFile> {
        every { uri } returns documentUri
        every { length() } returns sentinel
      }
      val treeDocument = mockk<DocumentFile> { every { findFile(fileName) } returns document }
      every { contentResolver.openOutputStream(documentUri, "wt") } returns ByteArrayOutputStream()

      val target = AutoExportDocumentWriter(contentResolver, { treeDocument }, { null })

      target.write(tree, null, "content") shouldBe documentUri
    }
  }
})
