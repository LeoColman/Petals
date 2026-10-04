package br.com.colman.petals.strain

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runAndroidComposeUiTest
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.Database
import br.com.colman.petals.MainActivity
import br.com.colman.petals.R.string.add_strain
import br.com.colman.petals.R.string.archive_strain
import br.com.colman.petals.R.string.delete_strain
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.settings
import br.com.colman.petals.R.string.strain_archived
import br.com.colman.petals.R.string.strain_name
import br.com.colman.petals.R.string.yes
import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.strain.repository.StrainRepository
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

@OptIn(ExperimentalTestApi::class)
class StrainsPageTest : FunSpec({

  val flm = Strain("420 Evo FLM", id = "flm")
  val bedrocan = Strain("Bedrocan", id = "bedrocan")

  class Catalog(context: Context) {
    private val database = Database(AndroidSqliteDriver(Database.Schema, context, null))
    val strains = StrainRepository(database.strainQueries)
    val uses = UseRepository(database.useQueries)
  }

  fun AndroidComposeUiTest<MainActivity>.catalogWithUsedFlmAndUnusedBedrocan(): Catalog = Catalog(activity!!).apply {
    strains.upsertAll(listOf(flm, bedrocan))
    uses.upsert(Use(strainId = flm.id))
    activity!!.setContent { StrainsPage(strains) }
  }

  fun inCard(strain: Strain) = hasAnyAncestor(hasTestTag("StrainCard ${strain.name}"))

  fun AndroidComposeUiTest<MainActivity>.exists(matcher: SemanticsMatcher) =
    onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()

  test("Only a strain no use was logged with can be deleted") {
    runAndroidComposeUiTest<MainActivity> {
      catalogWithUsedFlmAndUnusedBedrocan()
      val delete = hasContentDescription(activity!!.getString(delete_strain))

      waitUntil(timeoutMillis = 5_000) { exists(delete and inCard(bedrocan)) }
      exists(delete and inCard(flm)) shouldBe false
    }
  }

  test("Deleting asks first, then removes the strain") {
    runAndroidComposeUiTest<MainActivity> {
      val catalog = catalogWithUsedFlmAndUnusedBedrocan()
      val delete = hasContentDescription(activity!!.getString(delete_strain)) and inCard(bedrocan)

      waitUntil(timeoutMillis = 5_000) { exists(delete) }
      onNode(delete).performClick()
      onNodeWithText(activity!!.getString(yes)).performClick()

      waitUntil(timeoutMillis = 5_000) { catalog.strains.allNow() == listOf(flm) }
    }
  }

  test("Archiving hides a strain until archived ones are shown") {
    runAndroidComposeUiTest<MainActivity> {
      val catalog = catalogWithUsedFlmAndUnusedBedrocan()
      val archive = hasContentDescription(activity!!.getString(archive_strain)) and inCard(flm)

      waitUntil(timeoutMillis = 5_000) { exists(archive) }
      onNode(archive).performClick()
      waitUntil(timeoutMillis = 5_000) { !exists(hasTestTag("StrainCard ${flm.name}")) }

      onNodeWithTag("ShowArchivedStrains").performClick()
      waitUntil(timeoutMillis = 5_000) { exists(hasText(activity!!.getString(strain_archived, flm.name))) }
      catalog.strains.allNow().single { it.id == flm.id }.isArchived shouldBe true
    }
  }

  test("Add strain saves a new strain") {
    runAndroidComposeUiTest<MainActivity> {
      val catalog = catalogWithUsedFlmAndUnusedBedrocan()

      onNodeWithText(activity!!.getString(add_strain)).performClick()
      onNode(hasSetTextAction() and hasText(activity!!.getString(strain_name))).performTextInput("Aurora 20/1")
      onNodeWithText(activity!!.getString(ok)).performClick()

      waitUntil(timeoutMillis = 5_000) { catalog.strains.allNow().any { it.name == "Aurora 20/1" } }
      catalog.strains.allNow().map { it.name }.sorted() shouldContainExactly
        listOf("420 Evo FLM", "Aurora 20/1", "Bedrocan")
    }
  }

  test("Settings opens the strains screen") {
    runAndroidComposeUiTest<MainActivity> {
      onNodeWithContentDescription(activity!!.getString(settings)).performClick()
      onNodeWithTag("StrainsListItem").performClick()

      waitUntil(timeoutMillis = 5_000) { exists(hasText(activity!!.getString(add_strain))) }
    }
  }
})
