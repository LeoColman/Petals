package br.com.colman.petals.statistics.graph.formatter

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import java.util.Locale

class DayOfWeekFormatterTest : FunSpec({
  context("getFormattedValue returns correctly formatted day of the week (US Locale)") {
    Locale.setDefault(Locale.US)
    withData(
      Pair(1f, "Mon"),
      Pair(2f, "Tue"),
      Pair(3f, "Wed"),
      Pair(4f, "Thu"),
      Pair(5f, "Fri"),
      Pair(6f, "Sat"),
      Pair(7f, "Sun")
    ) { (dayAsFloat, expectedDay) ->

      val actual = DayOfWeekFormatter.getFormattedValue(dayAsFloat, null)
      actual shouldBe expectedDay
    }
  }

  context("getFormattedValue returns correctly formatted day of the week (BR Locale)") {
    Locale.setDefault(Locale("pt", "BR"))
    withData(
      Pair(1f, "seg"),
      Pair(2f, "ter"),
      Pair(3f, "qua"),
      Pair(4f, "qui"),
      Pair(5f, "sex"),
      Pair(6f, "sáb"),
      Pair(7f, "dom")
    ) { (dayAsFloat, expectedDay) ->

      val actual = DayOfWeekFormatter.getFormattedValue(dayAsFloat, null)
      actual shouldBe expectedDay
    }
  }
})
