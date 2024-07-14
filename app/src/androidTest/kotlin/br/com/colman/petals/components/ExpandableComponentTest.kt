package br.com.colman.petals.components

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runAndroidComposeUiTest
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.MainActivity

@OptIn(ExperimentalTestApi::class)
class ExpandableComponentTest : FunSpec({

  test("Initial state of expandable and collapsible components") {
    runAndroidComposeUiTest<MainActivity> {
      activity!!.setContent {
        ExpandableComponentTestContent()
      }

      onNodeWithText("Expandable Component").assertExists().assertIsDisplayed()

      onNodeWithText("Expanded!").assertIsNotDisplayed()
    }
  }

  test("Expand and collapse functionality") {
    runAndroidComposeUiTest<MainActivity> {
      activity!!.setContent {
        ExpandableComponentTestContent()
      }

      val expandCollapseLineTag = "ExpandCollapseLine"

      onNodeWithTag(expandCollapseLineTag).assertExists().assertIsDisplayed()

      onNodeWithTag(expandCollapseLineTag).performClick()

      onNodeWithText("Expanded!").assertExists().assertIsDisplayed()

      onNodeWithTag(expandCollapseLineTag).performClick()

      onNodeWithText("Expandable Component").assertExists().assertIsDisplayed()
      onNodeWithText("Expanded!").assertIsNotDisplayed()

    }
  }


})


@Composable
private fun ExpandableComponentTestContent() {
  ExpandableComponent("Expandable Component") {
    Column {
      Text("Expanded!")
      Text("Isn't it beautiful?")
    }
  }
}
