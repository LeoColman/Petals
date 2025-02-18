package br.com.colman.petals.hittimer

import com.jjoe64.graphview.series.DataPoint
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly

class WhyTenSecondsTest : FunSpec({

  test("getSubjectiveHighWeakSeries should return the correct data points") {
    val expected = listOf(
      DataPoint(0.0, 30.0),
      DataPoint(10.0, 40.0),
      DataPoint(20.0, 35.0)
    ).map { it.toString() }
    val actual = getSubjectiveHighWeakSeries().map { it.toString() }
    actual shouldContainExactly expected
  }

  test("getSubjectiveHighStrongSeries should return the correct data points") {
    val expected = listOf(
      DataPoint(0.0, 37.0),
      DataPoint(10.0, 47.0),
      DataPoint(20.0, 43.0)
    ).map { it.toString() }
    val actual = getSubjectiveHighStrongSeries().map { it.toString() }
    actual shouldContainExactly expected
  }
})
