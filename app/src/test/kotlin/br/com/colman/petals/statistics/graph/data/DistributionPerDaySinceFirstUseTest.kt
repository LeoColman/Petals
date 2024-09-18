package br.com.colman.petals.statistics.graph.data

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.Row2
import io.kotest.matchers.shouldBe
import io.kotest.data.forAll
import java.util.*

class DistributionPerDaySinceFirstUseTest: FunSpec({
  val entryList:List<Entry> = listOf(
    Entry(1f,0.8f),
    Entry(2f, 0.5f),
    Entry(3f, 0.2f )
  )


  test("Create LineDataSet Unit Test") {
    Locale.setDefault(Locale.US)
    forAll(
      Row2(entryList, LineDataSet(entryList, "Grams distribution over days since first use")),
    ) { dataSet: List<Entry>, expected:LineDataSet ->
      val actual = createLineDataSet(dataSet, "Grams distribution over days since first use")

      actual shouldBe expected
    }
  }
})
