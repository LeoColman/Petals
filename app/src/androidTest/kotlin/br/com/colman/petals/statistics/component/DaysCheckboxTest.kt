package br.com.colman.petals.statistics.component

import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runAndroidComposeUiTest
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.MainActivity
import io.kotest.matchers.shouldBe

/**
 * The period label used to carry a bare clickable of its own, which gave a screen reader a second
 * stop with no role and no checked state. Row and label are one target now, and the test tag rides
 * along with the click handling so it still marks the thing you tap.
 */
@OptIn(ExperimentalTestApi::class)
class DaysCheckboxTest : FunSpec({

  test("the label and the checkbox are one toggleable node") {
    runAndroidComposeUiTest<MainActivity> {
      activity!!.setContent {
        DaysCheckbox(false, { }, Period.Week)
      }

      onAllNodes(isToggleable()).fetchSemanticsNodes().size shouldBe 1
      onNodeWithTag("Days 7").assertIsOff()
    }
  }

  test("tapping the row toggles through the tagged node") {
    runAndroidComposeUiTest<MainActivity> {
      var selected by mutableStateOf(false)
      activity!!.setContent {
        DaysCheckbox(selected, { selected = it }, Period.Week)
      }

      onNodeWithTag("Days 7").performClick()

      selected shouldBe true
      onNodeWithTag("Days 7").assertIsOn()
    }
  }
})
