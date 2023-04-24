package br.com.colman.petals.withdrawal.interpolator

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.collections.shouldBeMonotonicallyDecreasing
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.duration
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.negativeLong
import io.kotest.property.arbitrary.take
import io.kotest.property.checkAll
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit.DAYS
import kotlin.time.DurationUnit.SECONDS

class ThcConcentrationInterpolatorTest : FunSpec({

  val target = ThcConcentrationInterpolator()

  test("Must be a descending function") {
    val sortedDurations = Arb.duration(0.days..20.days, DAYS)
      .map { it.toDouble(SECONDS) }.take(1_000).toList().sorted()

    sortedDurations.map(target::value).shouldBeMonotonicallyDecreasing()
  }

  context("Percentage calculations") {
    withData(
      0.days to 1.00,
      2.days to 0.50,
      7.days to 0.20,
      14.days to 0.10,
      20.days to 0.00
    ) { (day, expectedPercent) ->
      target.calculatePercentage(day.inWholeSeconds) shouldBe expectedPercent
    }
  }

  test("Reject negative values for percentage calculation") {
    Arb.negativeLong().checkAll {
      shouldThrowAny { target.calculatePercentage(it) }
    }
  }

  test("Coerces inputs greater than 20 days to 0%") {
    target.calculatePercentage((20.days + 1.seconds).inWholeSeconds) shouldBe 0.0
  }
})
