package br.com.colman.petals.use

import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runAndroidComposeUiTest
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import br.com.colman.kotest.FunSpec
import br.com.colman.petals.Database
import br.com.colman.petals.MainActivity
import br.com.colman.petals.R.string.add_use_during_pause_alert
import br.com.colman.petals.R.string.amount_grams_title
import br.com.colman.petals.R.string.no
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.yes
import br.com.colman.petals.review.ReviewAppRequester
import br.com.colman.petals.use.repository.ConsumptionMethod.VAPORIZED
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDateTime

@OptIn(ExperimentalTestApi::class)
class AddUseFlowTest : FunSpec({

  val template = Use(
    LocalDateTime.of(2024, 3, 1, 21, 30),
    "0.3".toBigDecimal(),
    "12.5".toBigDecimal(),
    description = "420 Evo FLM 27/1",
    consumptionMethod = VAPORIZED,
    strainId = "420-evo-flm"
  )

  val noReview = object : ReviewAppRequester {}

  fun inMemoryRepository(context: Context) =
    UseRepository(Database(AndroidSqliteDriver(Database.Schema, context, null)).useQueries)

  test("a duplicate is saved as a new use, now, with everything else copied") {
    lateinit var repository: UseRepository
    var finished = false

    runAndroidComposeUiTest<MainActivity> {
      repository = inMemoryRepository(activity!!)
      activity!!.setContent {
        AddUseFlow(AddUseRequest(template), false, noReview, repository) { finished = true }
      }

      onNodeWithText(activity!!.getString(ok)).performClick()
    }

    val saved = repository.all().first().single()
    saved.copy(date = template.date) shouldBe template
    saved.id shouldNotBe template.id
    Duration.between(saved.date, LocalDateTime.now()).abs() shouldBeLessThan Duration.ofMinutes(1)
    finished shouldBe true
  }

  test("during a pause the form only opens once the pause is confirmed") {
    lateinit var repository: UseRepository

    runAndroidComposeUiTest<MainActivity> {
      repository = inMemoryRepository(activity!!)
      activity!!.setContent {
        AddUseFlow(AddUseRequest(template), true, noReview, repository) { }
      }

      onNodeWithText(activity!!.getString(add_use_during_pause_alert)).assertIsDisplayed()
      onAllNodesWithText(activity!!.getString(amount_grams_title)).fetchSemanticsNodes().shouldBeEmpty()

      val yes = activity!!.getString(yes)
      waitUntil(timeoutMillis = 15_000) { onAllNodesWithText(yes).fetchSemanticsNodes().isNotEmpty() }
      onNodeWithText(yes).performClick()
      onNodeWithText(activity!!.getString(ok)).performClick()
    }

    repository.all().first() shouldHaveSize 1
  }

  test("declining the pause confirmation finishes without saving") {
    lateinit var repository: UseRepository
    var finished = false

    runAndroidComposeUiTest<MainActivity> {
      repository = inMemoryRepository(activity!!)
      activity!!.setContent {
        AddUseFlow(AddUseRequest(template), true, noReview, repository) { finished = true }
      }

      onNodeWithText(activity!!.getString(no)).performClick()
    }

    repository.all().first().shouldBeEmpty()
    finished shouldBe true
  }
})
