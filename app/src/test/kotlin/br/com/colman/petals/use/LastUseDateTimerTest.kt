package br.com.colman.petals.use

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class LastUseDateTimerTest : FunSpec({
  context("4:20 is the whole 16:20 minute") {
    withData(
      nameFn = { (time, expected) -> "$time is ${if (expected) "" else "not "}4:20" },
      LocalDateTime.of(2026, 4, 20, 16, 20, 0) to true,
      LocalDateTime.of(2026, 4, 20, 16, 20, 59) to true,
      LocalDateTime.of(2026, 4, 20, 16, 21, 0) to false,
      LocalDateTime.of(2026, 4, 20, 16, 19, 59) to false,
      LocalDateTime.of(2026, 4, 20, 4, 20, 0) to false,
    ) { (time, expected) ->
      time.is420() shouldBe expected
    }
  }
})
