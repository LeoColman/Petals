package br.com.colman.petals.withdrawal.tolerance

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.ln

private val Now: LocalDateTime = LocalDateTime.of(2026, 1, 1, 12, 0)

private fun Duration.inDays() = seconds.toDouble() / SecondsPerDay

/** [days] of [perDay] doses of [each] grams, ending [endingDaysAgo] days before [Now]. */
private fun history(days: Int, perDay: Int, each: Double, endingDaysAgo: Double = 0.0): List<Dose> {
  val total = days * perDay
  return (0 until total).map { index ->
    val daysAgo = endingDaysAgo + (total - 1 - index).toDouble() / perDay
    Dose(Now.minusSeconds((daysAgo * SecondsPerDay).toLong()), each)
  }
}

private fun daysQuit(doses: List<Dose>) = effectiveAbstinence(doses, Now)!!.inDays()

class ThcBurdenTest : FunSpec({

  test("A clean quitter reads exactly the time since their last use") {
    listOf(0.0, 1.0, 5.0, 12.5, 20.0, 26.0).forEach { quitDaysAgo ->
      daysQuit(history(days = 90, perDay = 4, each = 7.0, endingDaysAgo = quitDaysAgo)) shouldBe
        (quitDaysAgo plusOrMinus 1e-4)
    }
  }

  test("Dosing cadence does not shift a clean quitter") {
    val once = daysQuit(history(days = 90, perDay = 1, each = 28.0, endingDaysAgo = 6.0))
    val eight = daysQuit(history(days = 90, perDay = 8, each = 3.5, endingDaysAgo = 6.0))

    once shouldBe (6.0 plusOrMinus 1e-4)
    eight shouldBe (6.0 plusOrMinus 1e-4)
  }

  test("Lambda does not affect a clean quitter") {
    val doses = history(days = 90, perDay = 2, each = 14.0, endingDaysAgo = 9.0)

    listOf(0.10, 0.15, 0.26).forEach { lambda ->
      effectiveAbstinence(doses, Now, lambda)!!.inDays() shouldBe (9.0 plusOrMinus 1e-4)
    }
  }

  test("An ounce a day cut to an eighth reads as mid withdrawal, not day zero") {
    val heavy = history(days = 90, perDay = 1, each = 28.0, endingDaysAgo = 14.0)
    val reduced = history(days = 14, perDay = 1, each = 3.5)

    // The app used to read the time since the last use, which here is under a day.
    daysQuit(heavy + reduced) shouldBe (6.9 plusOrMinus 0.5)
  }

  test("A sustained reduction fades as tolerance readjusts") {
    val heavy = history(days = 90, perDay = 1, each = 28.0, endingDaysAgo = 60.0)
    val reduced = history(days = 60, perDay = 1, each = 3.5)

    daysQuit(heavy + reduced) shouldBeLessThan 1.0
  }

  test("An old binge does not flag a stable user") {
    val steady = history(days = 150, perDay = 1, each = 2.0)
    val binge = history(days = 7, perDay = 1, each = 20.0, endingDaysAgo = 80.0)

    daysQuit(steady + binge) shouldBe (0.0 plusOrMinus 0.01)
  }

  test("Holding a k-fold reduction settles at ln(k)/lambda before tolerance releases it") {
    val heavy = history(days = 90, perDay = 1, each = 28.0, endingDaysAgo = 30.0)
    val reduced = history(days = 30, perDay = 1, each = 3.5)

    val withoutRelease = effectiveAbstinence(heavy + reduced, Now, tolerance = 0.0)!!.inDays()

    withoutRelease shouldBe (ln(8.0) / ThcEliminationPerDay plusOrMinus 0.6)
  }

  test("Using at the all time peak reads as no withdrawal at all") {
    val ramping = history(days = 30, perDay = 1, each = 2.0) + history(days = 1, perDay = 1, each = 50.0)

    daysQuit(ramping) shouldBe (0.0 plusOrMinus 1e-6)
  }

  test("Future dated doses are ignored rather than blowing the burden up") {
    val past = history(days = 30, perDay = 1, each = 3.0, endingDaysAgo = 4.0)
    val future = listOf(Dose(Now.plusDays(30), 3.0))

    daysQuit(past + future) shouldBe (daysQuit(past) plusOrMinus 1e-6)
  }

  test("A single lifetime use reads as the time since it happened") {
    daysQuit(listOf(Dose(Now.minusDays(3), 1.0))) shouldBe (3.0 plusOrMinus 1e-4)
  }

  test("The reading is never negative and never exceeds the cap") {
    val ancient = listOf(Dose(Now.minusDays(365), 40.0), Dose(Now.minusDays(364), 40.0))

    val days = daysQuit(ancient)
    days shouldBeGreaterThan 0.0
    days shouldBe (MaxEffectiveAbstinenceDays plusOrMinus 1e-6)
  }

  test("No usable dose means no estimate") {
    effectiveAbstinence(emptyList(), Now).shouldBeNull()
    effectiveAbstinence(listOf(Dose(Now.minusDays(1), 0.0)), Now).shouldBeNull()
    effectiveAbstinence(listOf(Dose(Now.minusDays(1), -5.0)), Now).shouldBeNull()
    effectiveAbstinence(listOf(Dose(Now.plusDays(1), 5.0)), Now).shouldBeNull()
  }

  test("Burden decays between doses and jumps on each one") {
    val doses = listOf(Dose(Now.minusDays(2), 10.0))

    burdenAt(doses, Now.minusDays(3)) shouldBe 0.0
    burdenAt(doses, Now.minusDays(2)) shouldBe (10.0 plusOrMinus 1e-9)
    burdenAt(doses, Now.minusDays(1)) shouldBeLessThan 10.0
    burdenAt(doses, Now) shouldBeLessThan burdenAt(doses, Now.minusDays(1))
  }

  test("The running recurrence used to find the peak agrees with the direct sum") {
    val doses = history(days = 20, perDay = 3, each = 1.5)
    val direct = doses.maxOf { burdenAt(doses, it.at) }

    referenceBurden(doses, tolerance = 0.0) shouldBe (direct plusOrMinus 1e-9)
  }

  test("Adding a dose can only lower the reading") {
    val doses = history(days = 40, perDay = 2, each = 3.0, endingDaysAgo = 5.0)
    val extra = Dose(Now.minusDays(1), 3.0)

    daysQuit(doses + extra) shouldBeLessThan daysQuit(doses)
  }

  test("With a frozen history the reading only grows as time passes") {
    val doses = history(days = 40, perDay = 2, each = 3.0, endingDaysAgo = 2.0)

    val today = effectiveAbstinence(doses, Now)!!
    val tomorrow = effectiveAbstinence(doses, Now.plusDays(1))!!

    (tomorrow > today) shouldBe true
    tomorrow shouldNotBe today
  }
})
