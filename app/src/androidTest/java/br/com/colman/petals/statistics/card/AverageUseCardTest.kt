package br.com.colman.petals.statistics.card

import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import br.com.colman.petals.MainActivity
import me.moallemi.tools.daterange.localdate.LocalDateRange
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate.now

class AverageUseCardTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  val localDateRange = LocalDateRange(now().minusDays(1L), now())

  @Test
  fun doesntShowOnEmptyList() {
    composeTestRule.setContent {
      AverageUseCard(emptyList(), localDateRange)
    }

    composeTestRule.onRoot().assertIsNotDisplayed()
  }


}

