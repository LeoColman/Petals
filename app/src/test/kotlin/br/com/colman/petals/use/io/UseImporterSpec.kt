package br.com.colman.petals.use.io

import br.com.colman.petals.use.repository.UseRepository
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.property.arbitrary.take
import io.mockk.Called
import io.mockk.mockk
import io.mockk.verify

class UseImporterSpec : FunSpec({
  val useRepository = mockk<UseRepository>(relaxed = true)
  val target = UseImporter(useRepository)

  context("Parse file") {
    test("Returns success if all lines are parseable") {
      val usesCsv = useCsvArb.take(1000).toList()
      target.import(usesCsv).shouldBeSuccess()
    }

    test("Returns success if the only unparseable line is the header") {
      val header = "my,header,line\n"
      val usesCsv = useCsvArb.take(1000)
      val csvLines = listOf(header) + usesCsv

      target.import(csvLines).shouldBeSuccess()
    }

    test("Returns failure when any line other than the header is unparseable") {
      val header = "my,header,line\n"
      val usesCsv = useCsvArb.take(1000).toList()
      val invalidUseCsvs = invalidUseCsvArb.take(1000).toList()

      val firstLine = listOf(usesCsv.first())
      val otherLines = (listOf(header) + usesCsv + invalidUseCsvs).shuffled()

      target.import(firstLine + otherLines).shouldBeFailure()
    }

    test("Returns success when file is empty") {
      target.import(emptyList()).shouldBeSuccess()
    }
  }

  context("Data ingestion") {
    test("Doesn't call database when a line is wrong") {
      val wrongLine = "invalid,csv,line"

      target.import(List(2) { wrongLine })

      shouldNotThrowAny {
        verify { useRepository wasNot Called }
      }
    }

    test("Ingests the data if the only wrong line is the header") {
      val header = "my,beautiful,header"
      val uses = useArb.take(1000).toList()

      val csvs = listOf(header) + uses.map { it.columns().joinToString(",") }

      target.import(csvs)

      shouldNotThrowAny {
        verify {
          useRepository.insertAll(uses)
        }
      }
    }

    test("Ingests the data if all lines are parseable") {
      val uses = useArb.take(1000).toList()

      val csvs = uses.map { it.columns().joinToString(",") }
      target.import(csvs)

      shouldNotThrowAny {
        verify {
          useRepository.insertAll(uses)
        }
      }
    }

    test("Doesn't do anything if the list is empty") {
      val empty = emptyList<String>()

      target.import(empty)

      shouldNotThrowAny {
        verify {
          useRepository.insertAll(emptyList())
        }
      }
    }
  }
})
