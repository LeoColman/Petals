package br.com.colman.petals.statistics.graph.formatter

import br.com.colman.petals.use.repository.Use
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.Month

class DaysSinceFirstUseFormatterTest : FunSpec({

  val uses = listOf(
    Use(LocalDateTime.of(2021, Month.FEBRUARY, 7, 15, 30), 0.6.toBigDecimal(), 18.toBigDecimal()),
    Use(LocalDateTime.of(2021, Month.FEBRUARY, 8, 21, 57), 1.2.toBigDecimal(), 18.toBigDecimal()),
    Use(LocalDateTime.of(2021, Month.FEBRUARY, 9, 9, 30), 0.6.toBigDecimal(), 18.toBigDecimal())
  )

  context("getFormattedValue returns correctly formatted days since first use (yyyy-MM-dd)") {
    withData(
      Pair(0f, "2021-02-06"),
      Pair(1f, "2021-02-07"),
      Pair(2f, "2021-02-08"),
      Pair(3f, "2021-02-09")
    ) { (value, expectedDay) ->
      val actual = DaysSinceFirstUseFormatter(uses, "yyyy-MM-dd").formatDate.getFormattedValue(value, null)
      actual shouldBe expectedDay
    }
  }
})
