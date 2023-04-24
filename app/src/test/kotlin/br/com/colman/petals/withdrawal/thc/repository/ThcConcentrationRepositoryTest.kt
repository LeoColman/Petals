package br.com.colman.petals.withdrawal.thc.repository

import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.withdrawal.interpolator.ThcConcentrationInterpolator
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.time.ConstantNowTestListener
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import java.time.LocalDateTime.now
import kotlin.time.Duration.Companion.days

class ThcConcentrationRepositoryTest : FunSpec({

  val foreverNow = now()
  listener(ConstantNowTestListener(foreverNow))

  val useRepository = mockk<UseRepository> {
    every { getLastUseDate() } returns flowOf(foreverNow.minusDays(3))
  }
  val interpolator = mockk<ThcConcentrationInterpolator>(relaxed = true)

  val target = ThcConcentrationRepository(useRepository, interpolator)

  test("Emits the concentration value constantly") {
    target.concentration.take(10).toList() shouldHaveSize 10
  }

  test("Does not emit concentration if no uses") {
    every { useRepository.getLastUseDate() } returns emptyFlow()
    target.concentration.firstOrNull() shouldBe 0.0
  }

  test("Calculate concentration using last use and interpolator") {
    val returnedValue = target.concentration.first()

    returnedValue shouldBe interpolator.calculatePercentage(3.days.inWholeSeconds)
  }
})
