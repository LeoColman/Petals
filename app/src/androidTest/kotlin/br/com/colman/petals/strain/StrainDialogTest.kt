package br.com.colman.petals.strain

import androidx.activity.compose.setContent
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runAndroidComposeUiTest
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.MainActivity
import br.com.colman.petals.R.string.cost_per_gram_title
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.percent_out_of_range
import br.com.colman.petals.R.string.strain_name
import br.com.colman.petals.R.string.strain_name_taken
import br.com.colman.petals.R.string.thc_percent
import br.com.colman.petals.strain.repository.Strain
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

@OptIn(ExperimentalTestApi::class)
class StrainDialogTest : FunSpec({

  val catalog = listOf(Strain("420 Evo FLM", id = "flm"))

  fun AndroidComposeUiTest<MainActivity>.type(label: Int, text: String) =
    onNode(hasSetTextAction() and hasText(activity!!.getString(label))).performTextInput(text)

  test("A name another strain has keeps OK disabled and says why") {
    runAndroidComposeUiTest<MainActivity> {
      activity!!.setContent { StrainDialog(catalog) }

      type(strain_name, "420 evo flm")

      onNodeWithText(activity!!.getString(strain_name_taken)).assertIsDisplayed()
      onNodeWithText(activity!!.getString(ok)).assertIsNotEnabled()
    }
  }

  test("A missing name keeps OK disabled without an error") {
    runAndroidComposeUiTest<MainActivity> {
      activity!!.setContent { StrainDialog(catalog) }

      onNodeWithText(activity!!.getString(ok)).assertIsNotEnabled()
      onAllNodesWithText(activity!!.getString(strain_name_taken)).fetchSemanticsNodes().shouldBeEmpty()
    }
  }

  test("A potency outside 0 to 100 keeps OK disabled and says why") {
    runAndroidComposeUiTest<MainActivity> {
      activity!!.setContent { StrainDialog(catalog) }

      type(strain_name, "Bedrocan")
      type(thc_percent, "150")

      onNodeWithText(activity!!.getString(percent_out_of_range)).assertIsDisplayed()
      onNodeWithText(activity!!.getString(ok)).assertIsNotEnabled()
    }
  }

  test("A valid strain saves, reading decimal commas") {
    runAndroidComposeUiTest<MainActivity> {
      var saved: Strain? = null
      activity!!.setContent { StrainDialog(catalog, onSave = { saved = it }) }

      type(strain_name, "Bedrocan")
      type(thc_percent, "22,5")
      type(cost_per_gram_title, "9,80")
      onNodeWithText(activity!!.getString(ok)).assertIsEnabled().performClick()

      saved!!.name shouldBe "Bedrocan"
      saved!!.thcPercent shouldBe BigDecimal("22.5")
      saved!!.costPerGram shouldBe BigDecimal("9.80")
    }
  }

  test("Editing keeps the strain's id") {
    runAndroidComposeUiTest<MainActivity> {
      var saved: Strain? = null
      activity!!.setContent { StrainDialog(catalog, catalog.single(), onSave = { saved = it }) }

      type(thc_percent, "27")
      onNodeWithText(activity!!.getString(ok)).performClick()

      saved shouldBe catalog.single().copy(thcPercent = BigDecimal("27"))
    }
  }
})
