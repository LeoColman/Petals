package br.com.colman.petals.withdrawal.tolerance

import br.com.colman.petals.use.UseArb
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.withdrawal.tolerance.EstimateMode.Disabled
import br.com.colman.petals.withdrawal.tolerance.EstimateMode.Grams
import br.com.colman.petals.withdrawal.tolerance.EstimateMode.LastUseOnly
import br.com.colman.petals.withdrawal.tolerance.EstimateMode.Sessions
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThanOrEqual
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime

private val Now: LocalDateTime = LocalDateTime.of(2026, 1, 1, 12, 0)

private fun Duration.inDays() = seconds.toDouble() / 86_400.0

private fun use(daysAgo: Double, grams: Double) =
  Use(Now.minusSeconds((daysAgo * 86_400).toLong()), BigDecimal(grams.toString()))

private fun uses(days: Int, perDay: Int, grams: Double, endingDaysAgo: Double = 0.0): List<Use> {
  val total = days * perDay
  return (0 until total).map { use(endingDaysAgo + (total - 1 - it).toDouble() / perDay, grams) }
}

private fun estimate(uses: List<Use>, modelEnabled: Boolean = true) =
  estimateAbstinence(uses, uses.maxByOrNull { it.date }?.date, Now, modelEnabled)

class DoseSeriesTest : FunSpec({

  test("No use at all means no estimate") {
    estimateAbstinence(emptyList(), null, Now, true).shouldBeNull()
  }

  test("With the model switched off the reading is the time since the last use") {
    val history = uses(days = 90, perDay = 1, grams = 28.0, endingDaysAgo = 14.0) + uses(14, 1, 3.5)

    val result = estimate(history, modelEnabled = false)!!

    // Not LastUseOnly: the user has plenty of history, they just turned the adjustment off, and the
    // screen tells them so rather than claiming there is not enough data.
    result.mode shouldBe Disabled
    result.effective shouldBe result.actual
  }

  test("Fewer uses than the minimum falls back to the time since the last use") {
    val result = estimate(listOf(use(5.0, 3.0), use(4.0, 3.0)))!!

    result.mode shouldBe LastUseOnly
    result.effective.inDays() shouldBe (4.0 plusOrMinus 1e-4)
  }

  test("An ounce a day cut to an eighth reads as mid withdrawal while the last use was minutes ago") {
    val history = uses(days = 90, perDay = 1, grams = 28.0, endingDaysAgo = 14.0) + uses(14, 1, 3.5)

    val result = estimate(history)!!

    result.mode shouldBe Grams
    result.actual.inDays() shouldBe (0.0 plusOrMinus 1e-4)
    result.effective.inDays() shouldBeGreaterThan 5.0
  }

  test("Amounts are trusted at exactly the coverage threshold and not below it") {
    // A hundred uses spread over fifty days, so the whole set sits inside the lookback window.
    fun window(positives: Int) = (0 until 100).map { index ->
      use(daysAgo = 0.5 + index * 0.5, grams = if (index < positives) 2.0 else 0.0)
    }

    estimate(window(80))!!.mode shouldBe Grams
    estimate(window(79))!!.mode shouldBe Sessions
  }

  test("A history with no amounts at all still detects a reduction, counting sessions") {
    val heavy = uses(days = 60, perDay = 8, grams = 0.0, endingDaysAgo = 14.0)
    val reduced = uses(days = 14, perDay = 1, grams = 0.0)

    val result = estimate(heavy + reduced)!!

    result.mode shouldBe Sessions
    result.effective.inDays() shouldBeGreaterThan 4.0
  }

  test("Absurd amounts are capped so a single typo cannot pin the reading") {
    val typo = uses(days = 30, perDay = 1, grams = 2.0, endingDaysAgo = 3.0) + listOf(use(31.0, 280.0))
    val capped = uses(days = 30, perDay = 1, grams = 2.0, endingDaysAgo = 3.0) + listOf(use(31.0, 100.0))

    estimate(typo)!!.effective shouldBe estimate(capped)!!.effective
  }

  test("Negative amounts do not count as doses") {
    val history = uses(days = 30, perDay = 1, grams = 2.0, endingDaysAgo = 2.0)

    estimate(history + listOf(use(1.0, -50.0)))!!.effective shouldBe estimate(history)!!.effective
  }

  test("Uses older than the lookback window are not read") {
    val recent = uses(days = 10, perDay = 1, grams = 2.0, endingDaysAgo = 1.0)
    val ancient = uses(days = 10, perDay = 1, grams = 40.0, endingDaysAgo = 200.0)

    estimate(recent + ancient)!!.effective shouldBe estimate(recent)!!.effective
  }

  test("Future dated uses never shorten or lengthen the reading") {
    val history = uses(days = 30, perDay = 1, grams = 3.0, endingDaysAgo = 2.0)
    val future = listOf(Use(Now.plusDays(5), BigDecimal("3.0")))

    val withFuture = estimateAbstinence(history + future, history.last().date, Now, true)!!

    withFuture.effective shouldBe estimate(history)!!.effective
  }

  test("Any history at all produces a sane reading") {
    checkAll(Arb.list(UseArb, 0..60)) { generated ->
      val result = estimateAbstinence(generated, generated.maxByOrNull { it.date }?.date, Now, true)

      if (result != null) {
        result.effective.isNegative shouldBe false
        result.actual.isNegative shouldBe false
        // The cap belongs to the model. The fallback is the literal elapsed time, which has no ceiling.
        if (result.mode != LastUseOnly) {
          result.effective.inDays() shouldBeLessThanOrEqual MaxEffectiveAbstinenceDays
        } else {
          result.effective shouldBe result.actual
        }
      }
    }
  }
})
