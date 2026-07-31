package br.com.colman.petals.settings.view.dialog

import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runAndroidComposeUiTest
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.MainActivity
import compose.icons.TablerIcons
import compose.icons.tablericons.ToggleLeft
import io.kotest.matchers.shouldBe

/**
 * Guards the screen reader contract from #931. The row has to be a single toggleable node carrying
 * the label, otherwise a screen reader stops twice: once on text with no state, once on a state with
 * no name. Merely making the row toggleable does not achieve that, because Material's ListItem
 * merges its own descendants and keeps the label on a node of its own.
 */
@OptIn(ExperimentalTestApi::class)
class SwitchListItemTest : FunSpec({

  test("the row is one toggleable node that names what it toggles") {
    runAndroidComposeUiTest<MainActivity> {
      activity!!.setContent {
        SwitchListItem(TablerIcons.ToggleLeft, Label, Description, false) { }
      }

      onAllNodes(isToggleable()).fetchSemanticsNodes().size shouldBe 1
      onAllNodes(hasContentDescription("$Label. $Description") and isToggleable())[0].assertIsOff()
    }
  }

  test("tapping the row toggles, not only the switch itself") {
    runAndroidComposeUiTest<MainActivity> {
      var state by mutableStateOf(false)
      activity!!.setContent {
        SwitchListItem(TablerIcons.ToggleLeft, Label, Description, state) { state = it }
      }

      onAllNodes(isToggleable())[0].performClick()

      state shouldBe true
      onAllNodes(isToggleable())[0].assertIsOn()
    }
  }
})

private const val Label = "Milliseconds on hit timer"
private const val Description = "Enable or disable milliseconds on the hit timer page"
