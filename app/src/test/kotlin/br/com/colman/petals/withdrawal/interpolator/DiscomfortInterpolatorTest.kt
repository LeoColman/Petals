package br.com.colman.petals.withdrawal.interpolator

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeMonotonicallyDecreasing
import io.kotest.matchers.collections.shouldBeMonotonicallyIncreasing
import io.kotest.matchers.doubles.percent
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.duration
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.take
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit.DAYS
import kotlin.time.DurationUnit.SECONDS

class DiscomfortInterpolatorTest : FunSpec({

  val target = DiscomfortInterpolator()

  test("Must be an increasing function until day 1") {
    val sortedDurations =
      Arb.duration((-2).days..0.days, DAYS).map { it.toDouble(SECONDS) }.take(1_000).toList().sorted()

    sortedDurations.map(target::value).shouldBeMonotonicallyIncreasing()
  }

  test("Must be a descending function after day 1") {
    val sortedDurations = Arb.duration(1.days..25.days, DAYS)
      .map { it.toDouble(SECONDS) }.take(1_000).toList().sorted()

    sortedDurations.map(target::value).shouldBeMonotonicallyDecreasing()
  }

  test("Coerces inputs greater than 25 days to 3.0") {
    target.calculateDiscomfort((25.days + 1.seconds).inWholeSeconds) shouldBe (3.0 plusOrMinus 1.percent)
  }
})
