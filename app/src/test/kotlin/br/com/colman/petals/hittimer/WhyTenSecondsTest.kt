package br.com.colman.petals.hittimer

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly

class WhyTenSecondsTest : FunSpec({

  // Compared by value rather than by toString: GraphView's DataPoint had no equals, which is why
  // this used to render both sides to strings first.
  test("getSubjectiveHighWeakSeries should return the correct data points") {
    getSubjectiveHighWeakSeries() shouldContainExactly listOf(
      SubjectiveHighPoint(0.0, 30.0),
      SubjectiveHighPoint(10.0, 40.0),
      SubjectiveHighPoint(20.0, 35.0)
    )
  }

  test("getSubjectiveHighStrongSeries should return the correct data points") {
    getSubjectiveHighStrongSeries() shouldContainExactly listOf(
      SubjectiveHighPoint(0.0, 37.0),
      SubjectiveHighPoint(10.0, 47.0),
      SubjectiveHighPoint(20.0, 43.0)
    )
  }
})
