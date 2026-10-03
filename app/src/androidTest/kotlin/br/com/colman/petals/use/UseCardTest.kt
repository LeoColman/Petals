package br.com.colman.petals.use

import androidx.activity.compose.setContent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runAndroidComposeUiTest
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.MainActivity
import br.com.colman.petals.R.string.duplicate_use
import br.com.colman.petals.use.repository.Use
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.time.LocalDateTime

@OptIn(ExperimentalTestApi::class)
class UseCardTest : FunSpec({

  test("tapping duplicate hands over the use on that card") {
    runAndroidComposeUiTest<MainActivity> {
      val use = Use(LocalDateTime.of(2024, 3, 1, 21, 30), "0.3".toBigDecimal(), "12".toBigDecimal())
      var duplicated: Use? = null
      activity!!.setContent {
        UseCard(use, onDuplicateUse = { duplicated = it })
      }

      onNodeWithContentDescription(activity!!.getString(duplicate_use)).performClick()

      duplicated shouldBeSameInstanceAs use
    }
  }
})
