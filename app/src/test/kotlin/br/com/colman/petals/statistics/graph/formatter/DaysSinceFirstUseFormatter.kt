package br.com.colman.petals.statistics.graph.formatter

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.Row2
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime.now
import java.time.format.DateTimeFormatter

class DaysSinceFirstUseFormatter: FunSpec({

  test("getFormattedValue returns correctly formatted days since first use (yyyy-MM-dd)"){
    val dateFormat = "yyyy-MM-dd"
    val formatter = DateTimeFormatter.ofPattern(dateFormat)
    forAll(
      Row2(0f, now().minusDays(3).format(formatter).toString()),
      Row2(1f, now().minusDays(2).format(formatter).toString()),
      Row2(2f, now().minusDays(1).format(formatter).toString()),
      Row2(3f, now().format(formatter).toString())
    ){ value, expectedDay ->
      val actual = DaysSinceFirstUseFormatter.getFormattedValue((value), null)
      actual shouldBe expectedDay
    }
  }
})
