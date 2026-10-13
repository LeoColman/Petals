package br.com.colman.petals.strain

import br.com.colman.petals.use.repository.Use
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class StrainSummaryTest : FunSpec({

  fun use(strain: String?, grams: String, cost: String, rating: Double? = null) =
    Use(amountGrams = BigDecimal(grams), costPerGram = BigDecimal(cost), strainId = strain, rating = rating)

  test("Adds up each strain's uses, grams and spending") {
    val uses = listOf(use("flm", "0.25", "12.50"), use("flm", "0.30", "12.50"), use("bed", "0.20", "9.80"))

    val summaries = uses.summariesByStrain()

    summaries["flm"] shouldBe StrainSummary(2, BigDecimal("0.55"), BigDecimal("6.8750"), null)
    summaries["bed"] shouldBe StrainSummary(1, BigDecimal("0.20"), BigDecimal("1.9600"), null)
  }

  test("Averages only the rated uses") {
    val uses = listOf(use("flm", "0.25", "10", 4.0), use("flm", "0.25", "10", 3.0), use("flm", "0.25", "10"))

    uses.summariesByStrain().getValue("flm").averageRating shouldBe 3.5
  }

  test("Leaves out uses without a strain") {
    listOf(use(null, "1", "10", 5.0)).summariesByStrain().shouldBeEmpty()
  }
})
