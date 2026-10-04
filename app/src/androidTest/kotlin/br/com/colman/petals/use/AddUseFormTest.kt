package br.com.colman.petals.use

import androidx.activity.compose.setContent
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runAndroidComposeUiTest
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.MainActivity
import br.com.colman.petals.R.string.no_strain
import br.com.colman.petals.koin
import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.strain.repository.StrainRepository
import br.com.colman.petals.use.repository.ConsumptionMethod
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalTestApi::class)
class AddUseFormTest : FunSpec({

  // The form reads strains from the app's own catalog, so the strain lives there for each test.
  val strains = koin.get<StrainRepository>()
  val strain = Strain("AddUseFormTest strain", costPerGram = BigDecimal("12.50"), id = "add-use-form-test")
  beforeTest { strains.upsert(strain) }
  afterTest { strains.delete(strain) }

  fun AndroidComposeUiTest<MainActivity>.pickStrain(fillsStrainCost: Boolean): MutableState<String> {
    val cost = mutableStateOf("10.00")
    activity!!.setContent {
      AddUseForm(
        mutableStateOf(""),
        cost,
        mutableStateOf(LocalDate.now()),
        mutableStateOf(LocalTime.now()),
        mutableStateOf(""),
        mutableStateOf<ConsumptionMethod?>(null),
        mutableStateOf(null),
        fillsStrainCost
      )
    }
    onNodeWithText(activity!!.getString(no_strain)).performClick()
    waitUntil(timeoutMillis = 5_000) { onAllNodesWithText(strain.name).fetchSemanticsNodes().isNotEmpty() }
    onNodeWithText(strain.name).performClick()
    waitForIdle()
    return cost
  }

  test("Picking a strain fills in its cost for a new use") {
    runAndroidComposeUiTest<MainActivity> {
      pickStrain(fillsStrainCost = true).value shouldBe "12.50"
    }
  }

  test("Picking a strain leaves the cost alone for a use being edited") {
    runAndroidComposeUiTest<MainActivity> {
      pickStrain(fillsStrainCost = false).value shouldBe "10.00"
    }
  }
})
