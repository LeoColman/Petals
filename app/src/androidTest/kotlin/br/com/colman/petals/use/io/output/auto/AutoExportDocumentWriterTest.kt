package br.com.colman.petals.use.io.output.auto

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import androidx.test.core.app.ApplicationProvider
import br.com.colman.kotest.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe

/**
 * The test that actually matters: proves openOutputStream(uri, "wt") really
 * truncates for a real ContentResolver against a local file:// tree (which it
 * reliably does — unlike some cloud DocumentsProviders, which is exactly why
 * AutoExportDocumentWriter has the verify+recreate fallback at all). The
 * fallback path itself can only be proven against a real flaky provider like
 * Nextcloud — see the manual end-to-end steps in the original plan.
 */
class AutoExportDocumentWriterTest : FunSpec({
  val context: Context = ApplicationProvider.getApplicationContext()
  val treeDir = tempdir()
  val tree = DocumentFile.fromFile(treeDir)

  // documentFileFromSingleUri returns null on purpose: forces every write to
  // re-resolve via findFile-by-name on the tree, exactly like a fresh install
  // with no cached document Uri yet.
  val target = AutoExportDocumentWriter(context.contentResolver, { tree }, { null })

  test("overwrites PetalsExport.csv in place with no trailing bytes when the new content is shorter") {
    val longContent = "date,amount,cost_per_gram,id,description,consumption_method\n".repeat(50)
    val shortContent = "date,amount,cost_per_gram,id,description,consumption_method\n"

    target.write(tree.uri, null, longContent)
    val secondUri = target.write(tree.uri, null, shortContent)

    context.contentResolver.openInputStream(secondUri)!!.bufferedReader().use { it.readText() } shouldBe shortContent
    treeDir.listFiles()!!.size shouldBe 1
  }

  test("still only one file after several writes of varying length") {
    listOf("a\n", "b".repeat(200) + "\n", "c\n", "d".repeat(80) + "\n").forEach {
      target.write(tree.uri, null, it)
    }

    treeDir.listFiles()!!.size shouldBe 1
  }
})
