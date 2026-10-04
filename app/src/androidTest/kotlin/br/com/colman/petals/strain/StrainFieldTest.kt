package br.com.colman.petals.strain

import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runAndroidComposeUiTest
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.MainActivity
import br.com.colman.petals.R.string.new_strain
import br.com.colman.petals.R.string.no_strain
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.strain_archived
import br.com.colman.petals.R.string.strain_name
import br.com.colman.petals.strain.repository.Strain
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

@OptIn(ExperimentalTestApi::class)
class StrainFieldTest : FunSpec({

  val flm = Strain("420 Evo FLM", BigDecimal("27"), BigDecimal("1"), BigDecimal("12.50"), id = "flm")
  val bedrocan = Strain("Bedrocan", id = "bedrocan")
  val oldBatch = Strain("Old batch", isArchived = true, id = "old")
  val catalog = listOf(flm, bedrocan, oldBatch)

  test("Picking a strain selects it and tells the form which one") {
    runAndroidComposeUiTest<MainActivity> {
      val strainId = mutableStateOf<String?>(null)
      var picked: Strain? = null
      activity!!.setContent { StrainField(strainId, catalog, onPick = { picked = it }) }

      onNodeWithText(activity!!.getString(no_strain)).performClick()
      onNodeWithText(flm.name).performClick()

      strainId.value shouldBe flm.id
      picked shouldBe flm
    }
  }

  test("Picking no strain clears the selection") {
    runAndroidComposeUiTest<MainActivity> {
      val strainId = mutableStateOf<String?>(flm.id)
      var picked: Strain? = flm
      activity!!.setContent { StrainField(strainId, catalog, onPick = { picked = it }) }

      onNodeWithText(flm.name).performClick()
      onNodeWithText(activity!!.getString(no_strain)).performClick()

      strainId.value shouldBe null
      picked shouldBe null
    }
  }

  test("Archived strains aren't offered, but one the use already has still shows") {
    runAndroidComposeUiTest<MainActivity> {
      val strainId = mutableStateOf<String?>(oldBatch.id)
      activity!!.setContent { StrainField(strainId, catalog) }

      val shown = activity!!.getString(strain_archived, oldBatch.name)
      onNodeWithText(shown).performClick()

      onAllNodesWithText(oldBatch.name).fetchSemanticsNodes().shouldBeEmpty()
      onNodeWithText(bedrocan.name).performClick()
      strainId.value shouldBe bedrocan.id
    }
  }

  test("New strain makes a strain, hands it over and selects it") {
    runAndroidComposeUiTest<MainActivity> {
      val strainId = mutableStateOf<String?>(null)
      var created: Strain? = null
      activity!!.setContent { StrainField(strainId, catalog, onCreate = { created = it }) }

      onNodeWithText(activity!!.getString(no_strain)).performClick()
      onNodeWithText(activity!!.getString(new_strain)).performClick()
      onNode(hasSetTextAction() and hasText(activity!!.getString(strain_name))).performTextInput("Aurora 20/1")
      onNodeWithText(activity!!.getString(ok)).performClick()

      created!!.name shouldBe "Aurora 20/1"
      strainId.value shouldBe created!!.id
    }
  }
})
