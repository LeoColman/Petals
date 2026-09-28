package br.com.colman.petals.use

import br.com.colman.petals.use.UseMilestone.RequestReview
import br.com.colman.petals.use.UseMilestone.SupportDeveloper
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class AddUseButtonTest : FunSpec({
  context("the milestone shown after a use depends on how many uses came before it") {
    withData(
      nameFn = { (count, milestone) -> "$count uses before: ${milestone ?: "nothing"}" },
      0 to null,
      1 to null,
      41 to null,
      42 to SupportDeveloper,
      43 to null,
      84 to SupportDeveloper,
      99 to null,
      100 to RequestReview,
      101 to null,
      200 to RequestReview,
      2100 to SupportDeveloper,
    ) { (count, milestone) ->
      useMilestone(count) shouldBe milestone
    }
  }
})
