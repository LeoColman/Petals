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
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.Database
import br.com.colman.petals.MainActivity
import br.com.colman.petals.R.plurals.amount_uses
import br.com.colman.petals.R.string.add_strain
import br.com.colman.petals.R.string.amount_grams
import br.com.colman.petals.R.string.archive_strain
import br.com.colman.petals.R.string.average_rating_out_of_five
import br.com.colman.petals.R.string.censored
import br.com.colman.petals.R.string.delete_strain
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.settings
import br.com.colman.petals.R.string.strain_archived
import br.com.colman.petals.R.string.strain_name
import br.com.colman.petals.R.string.total_spent
import br.com.colman.petals.R.string.yes
import br.com.colman.petals.components.ratingNumber
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.strain.repository.StrainRepository
import br.com.colman.petals.use.repository.BlockType.AllTime
import br.com.colman.petals.use.repository.CensorshipRepository
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.utils.decimalText
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.io.File
import java.math.BigDecimal
import java.util.UUID

@OptIn(ExperimentalTestApi::class)
class StrainsPageTest : FunSpec({

  val flm = Strain("420 Evo FLM", id = "flm")
  val bedrocan = Strain("Bedrocan", id = "bedrocan")

  // Settings and censorship start from their defaults for each test, whatever other tests left in the app's own.
  class Catalog(context: Context) {
    private val database = Database(AndroidSqliteDriver(Database.Schema, context, null))
    private val preferences = PreferenceDataStoreFactory.create {
      File(context.cacheDir, "StrainsPageTest-${UUID.randomUUID()}.preferences_pb")
    }
    val strains = StrainRepository(database.strainQueries)
    val uses = UseRepository(database.useQueries)
    val censorship = CensorshipRepository(preferences)
    private val settings = SettingsRepository(preferences)

    fun show(activity: MainActivity) = activity.setContent { StrainsPage(strains, settings, censorship) }
  }

  fun AndroidComposeUiTest<MainActivity>.catalogWithUsedFlmAndUnusedBedrocan(): Catalog = Catalog(activity!!).apply {
    strains.upsertAll(listOf(flm, bedrocan))
    uses.upsert(Use(strainId = flm.id))
    show(activity!!)
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

  test("A used strain's card counts its uses and averages their ratings") {
    runAndroidComposeUiTest<MainActivity> {
      Catalog(activity!!).apply {
        strains.upsert(flm)
        uses.upsertAll(listOf(4.0, 3.0, null).map { Use(strainId = flm.id, rating = it) })
        show(activity!!)
      }
      val threeUses = activity!!.resources.getQuantityString(amount_uses, 3, "3")
      val locale = activity!!.resources.configuration.locales[0]
      val averageOfThreeAndAHalf = activity!!.getString(average_rating_out_of_five, ratingNumber(3.5, locale))

      waitUntil(timeoutMillis = 5_000) { exists(hasText(threeUses, substring = true) and inCard(flm)) }
      exists(hasContentDescription(averageOfThreeAndAHalf) and inCard(flm)) shouldBe true
    }
  }

  fun AndroidComposeUiTest<MainActivity>.catalogWithTwoUsesOfFlm(): Catalog = Catalog(activity!!).apply {
    strains.upsert(flm)
    uses.upsertAll(List(2) { Use(amountGrams = BigDecimal("0.25"), costPerGram = BigDecimal("10"), strainId = flm.id) })
  }

  test("A used strain's card adds up its grams and spending, as the app's language writes numbers") {
    runAndroidComposeUiTest<MainActivity> {
      catalogWithTwoUsesOfFlm().show(activity!!)
      val locale = activity!!.resources.configuration.locales[0]
      val totals = listOf(
        activity!!.resources.getQuantityString(amount_uses, 2, "2"),
        activity!!.getString(amount_grams, decimalText(BigDecimal("0.50"), 2, locale)),
        "$ " + activity!!.getString(total_spent, decimalText(BigDecimal("5.00"), 2, locale))
      ).joinToString(" · ")

      waitUntil(timeoutMillis = 5_000) { exists(hasText(totals) and inCard(flm)) }
    }
  }

  test("A used strain's totals stay hidden while the all-time totals are censored") {
    runAndroidComposeUiTest<MainActivity> {
      catalogWithTwoUsesOfFlm().apply {
        censorship.setBlockCensure(AllTime, true)
        show(activity!!)
      }
      val twoUses = activity!!.resources.getQuantityString(amount_uses, 2, "2")
      val hidden = "$twoUses · ${activity!!.getString(censored)}"

      waitUntil(timeoutMillis = 5_000) { exists(hasText(hidden) and inCard(flm)) }
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
