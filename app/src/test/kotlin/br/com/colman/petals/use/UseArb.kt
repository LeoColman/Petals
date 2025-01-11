package br.com.colman.petals.use

import br.com.colman.petals.use.repository.Use
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAtMost
import io.kotest.matchers.bigdecimal.shouldBeInRange
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.sequences.shouldNotContainDuplicates
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeUUID
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.bigDecimal
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import io.kotest.property.checkAll

val UseArb = arbitrary {
  val bigDecimals = Arb.bigDecimal((-100.0).toBigDecimal(), 100.0.toBigDecimal())
  Use(
    Arb.localDateTime().bind(),
    bigDecimals.bind(),
    bigDecimals.bind(),
    description = Arb.string(codepoints = Codepoint.az()).bind()
  )
}

class UseArbitraryTest : FunSpec({
  test("Generated Use instances should have valid UUIDs") {
    checkAll(UseArb) { use ->
      use.id.shouldBeUUID()
    }
  }

  test("Generated Use instances should have valid LocalDateTime") {
    checkAll(UseArb) { use ->
      use.date.year shouldBeInRange 1900..2100
    }
  }

  test("Generated amount and cost should be within the range [-100.0, 100.0]") {
    val min = (-100.0).toBigDecimal()
    val max = 100.0.toBigDecimal()
    checkAll(UseArb) { use ->
      use.amountGrams shouldBeInRange (min..max)
      use.costPerGram shouldBeInRange (min..max)
    }
  }

  test("Generated amount and cost should not always be equal") {
    UseArb.take(1_000).toList().forAtMost(100) { it.amountGrams shouldBe it.costPerGram }
  }

  test("Generated Use instances should have diverse values") {
    UseArb.take(10_000).shouldNotContainDuplicates()
  }
})
