package br.com.colman.petals.withdrawal.discomfort.repository

import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.withdrawal.interpolator.DiscomfortDataPoints
import br.com.colman.petals.withdrawal.interpolator.Interpolator
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
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.days

class DiscomfortRepositoryTest : FunSpec({

  val foreverNow = LocalDateTime.now()
  listener(ConstantNowTestListener(foreverNow))

  val useRepository = mockk<UseRepository> {
    every { getLastUseDate() } returns flowOf(foreverNow.minusDays(3))
  }

  val target = DiscomfortRepository(useRepository)
  val interpolator = Interpolator(DiscomfortDataPoints)

  test("Emits the concentration value constantly") {
    target.discomfort.take(10).toList() shouldHaveSize 10
  }

  test("Does not emit discomfort if no uses") {
    every { useRepository.getLastUseDate() } returns emptyFlow()
    target.discomfort.firstOrNull() shouldBe 0.0
  }

  test("Calculate discomfort using last use and interpolator") {
    val returnedValue = target.discomfort.first()

    returnedValue shouldBe interpolator.value(3.days.inWholeSeconds.toDouble())
  }
})
