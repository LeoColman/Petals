package br.com.colman.petals.navigation

import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.use.repository.Use
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue

class UsageFilterTest : FunSpec({

  val flm = Strain("420 Evo FLM", id = "flm")
  val use = Use(description = "With a friend", strainId = flm.id)

  test("An empty filter shows every use") {
    use.matchesFilter("", flm).shouldBeTrue()
    Use().matchesFilter("", null).shouldBeTrue()
  }

  test("Shows a use whose notes contain the filter, ignoring case") {
    use.matchesFilter("FRIEND", null).shouldBeTrue()
  }

  test("Shows a use whose strain's name contains the filter, ignoring case") {
    use.matchesFilter("evo", flm).shouldBeTrue()
  }

  test("Hides a use whose notes and strain both miss the filter") {
    use.matchesFilter("Bedrocan", flm).shouldBeFalse()
    Use().matchesFilter("Bedrocan", null).shouldBeFalse()
  }
})
