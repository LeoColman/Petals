package br.com.colman.petals.statistics.graph.formatter

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.Row2
import io.kotest.data.forAll
import io.kotest.matchers.shouldBe
import java.util.Locale

class DayOfWeekFormatterTest : FunSpec({
  test("getFormattedValue returns correctly formatted day of the week (US Locale)") {
    Locale.setDefault(Locale.US)
    forAll(
      Row2(1f, "Mon"),
      Row2(2f, "Tue"),
      Row2(3f, "Wed"),
      Row2(4f, "Thu"),
      Row2(5f, "Fri"),
      Row2(6f, "Sat"),
      Row2(7f, "Sun")
    ) { dayAsFloat, expectedDay ->

      val actual = DayOfWeekFormatter.getFormattedValue(dayAsFloat, null)
      actual shouldBe expectedDay
    }
  }

  test("getFormattedValue returns correctly formatted day of the week (BR Locale)") {
    Locale.setDefault(Locale("pt", "BR"))
    forAll(
      Row2(1f, "seg"),
      Row2(2f, "ter"),
      Row2(3f, "qua"),
      Row2(4f, "qui"),
      Row2(5f, "sex"),
      Row2(6f, "sáb"),
      Row2(7f, "dom")
    ) { dayAsFloat, expectedDay ->

      val actual = DayOfWeekFormatter.getFormattedValue(dayAsFloat, null)
      actual shouldBe expectedDay
    }
  }
})
