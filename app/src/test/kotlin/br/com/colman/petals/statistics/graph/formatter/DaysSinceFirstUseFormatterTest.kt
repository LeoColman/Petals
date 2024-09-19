package br.com.colman.petals.statistics.graph.formatter

import br.com.colman.petals.use.repository.Use
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.Row2
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.Month

class DaysSinceFirstUseFormatterTest : FunSpec({

  val uses = listOf(
    Use(LocalDateTime.of(2021, Month.FEBRUARY, 7, 15, 30), 0.6.toBigDecimal(), 18.toBigDecimal()),
    Use(LocalDateTime.of(2021, Month.FEBRUARY, 8, 21, 57), 1.2.toBigDecimal(), 18.toBigDecimal()),
    Use(LocalDateTime.of(2021, Month.FEBRUARY, 9, 9, 30), 0.6.toBigDecimal(), 18.toBigDecimal())
  )

  test("getFormattedValue returns correctly formatted days since first use (yyyy-MM-dd)") {
    forAll(
      Row2(0f, "2021-02-06"),
      Row2(1f, "2021-02-07"),
      Row2(2f, "2021-02-08"),
      Row2(3f, "2021-02-09")
    ) { value, expectedDay ->
      val actual = DaysSinceFirstUseFormatter(uses, "yyyy-MM-dd").formatDate.getFormattedValue(value, null)
      actual shouldBe expectedDay
    }
  }
})
