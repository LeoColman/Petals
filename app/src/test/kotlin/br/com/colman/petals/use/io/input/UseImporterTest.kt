package br.com.colman.petals.use.io.input

import br.com.colman.petals.Database
import br.com.colman.petals.inMemoryDatabase
import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.strain.repository.StrainRepository
import br.com.colman.petals.use.UseArb
import br.com.colman.petals.use.io.UseCsvArb
import br.com.colman.petals.use.repository.UseRepository
import com.natpryce.snodge.mutants
import com.natpryce.snodge.text.replaceWithPossiblyMeaningfulText
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.take
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import kotlin.random.Random

class UseImporterTest : FunSpec({
  val useRepository = mockk<UseRepository>(relaxed = true)
  val strainRepository = mockk<StrainRepository>(relaxed = true) { every { allNow() } returns emptyList() }
  val target = UseImporter(useRepository, strainRepository, inMemoryDatabase())

  context("Parse file") {
    test("Returns success if all lines are parseable") {
      val usesCsv = UseCsvArb.take(1000).toList()
      target.import(usesCsv).shouldBeSuccess()
    }

    test("Returns success if the only unparseable line is the header") {
      val header = "my,header,line\n"
      val usesCsv = UseCsvArb.take(1000)
      val csvLines = listOf(header) + usesCsv

      target.import(csvLines).shouldBeSuccess()
    }

    test("Returns failure when any line other than the header is unparseable") {
      val header = "my,header,line\n"
      val usesCsv = UseCsvArb.take(1000).toList()
      val invalidUseCsvs = invalidUseCsvArb.take(1000).toList()

      val firstLine = listOf(usesCsv.first())
      val otherLines = (listOf(header) + usesCsv + invalidUseCsvs).shuffled()

      target.import(firstLine + otherLines).shouldBeFailure()
    }

    test("Returns success when file is empty") {
      target.import(emptyList()).shouldBeSuccess()
    }
  }

  test("Saves every line with the upsert that never takes a strain away, in the file's order") {
    val uses = UseArb.take(3).toList()
    val lines = listOf(uses[0].columns(), uses[1].columns() + List(Strain.CsvColumnCount) { "" }, uses[2].columns())

    target.import(lines.map { it.joinToString(",") }).shouldBeSuccess()

    verify { useRepository.upsertAllKeepingStrains(uses) }
    verify(exactly = 0) { useRepository.upsertAll(any()) }
  }

  context("Data ingestion") {
    test("Doesn't call database when a line is wrong") {
      val wrongLine = "invalid,csv,line,is,invalid"

      target.import(List(2) { wrongLine })

      shouldNotThrowAny {
        verify { useRepository wasNot Called }
      }
    }

    test("Ingests the data if the only wrong line is the header") {
      val header = "my,beautiful,header"
      val uses = UseArb.take(1000).toList()

      val csvs = listOf(header) + uses.map { it.columns().joinToString(",") }

      target.import(csvs)

      shouldNotThrowAny {
        verify {
          useRepository.upsertAllKeepingStrains(uses)
        }
      }
    }

    test("Ingests the data if all lines are parseable") {
      val uses = UseArb.take(1000).toList()

      val csvs = uses.map { it.columns().joinToString(",") }
      target.import(csvs)

      shouldNotThrowAny {
        verify {
          useRepository.upsertAllKeepingStrains(uses)
        }
      }
    }

    test("Doesn't do anything if the list is empty") {
      val empty = emptyList<String>()

      target.import(empty)

      shouldNotThrowAny {
        verify {
          useRepository.upsertAllKeepingStrains(emptyList())
        }
      }
    }
  }

  context("Strains") {
    class Catalog(val database: Database = inMemoryDatabase()) {
      val uses = UseRepository(database.useQueries)
      val strains = StrainRepository(database.strainQueries)
      val importer = UseImporter(uses, strains, database)
    }

    val flm = Strain("420 Evo FLM", BigDecimal("27"), BigDecimal("1"))
    fun line(strain: Strain?) =
      (UseArb.take(1).single().columns() + (strain?.columns() ?: List(Strain.CsvColumnCount) { "" })).joinToString(",")

    test("Creates the strain a file names and links its uses to it") {
      with(Catalog()) {
        importer.import(listOf(line(flm))).shouldBeSuccess()

        strains.allNow() shouldContainExactly listOf(flm)
        uses.all().first().single().strainId shouldBe flm.id
      }
    }

    test("Creates a strain named on many lines only once") {
      with(Catalog()) {
        importer.import(List(3) { line(flm) }).shouldBeSuccess()

        strains.allNow().shouldBeSingleton()
        uses.all().first().map { it.strainId }.toSet() shouldBe setOf(flm.id)
      }
    }

    test("Links to a catalog strain with the same name, ignoring case, instead of creating another") {
      with(Catalog()) {
        val catalogued = Strain("420 evo flm")
        strains.upsert(catalogued)

        importer.import(listOf(line(flm))).shouldBeSuccess()

        strains.allNow() shouldContainExactly listOf(catalogued)
        uses.all().first().single().strainId shouldBe catalogued.id
      }
    }

    test("Links to the catalog strain with the same id even after it was renamed") {
      with(Catalog()) {
        val renamed = flm.copy(name = "FLM, new batch")
        strains.upsert(renamed)

        importer.import(listOf(line(flm))).shouldBeSuccess()

        strains.allNow() shouldContainExactly listOf(renamed)
        uses.all().first().single().strainId shouldBe renamed.id
      }
    }

    test("Imports lines without strain columns with no strain") {
      with(Catalog()) {
        importer.import(listOf(line(null))).shouldBeSuccess()

        strains.allNow() shouldHaveSize 0
        uses.all().first().single().strainId shouldBe null
      }
    }

    test("Makes one strain of id-less lines that name the same strain") {
      with(Catalog()) {
        val idLess = List(
          3
        ) { (UseArb.take(1).single().columns() + listOf("", "Bedrocan", "22", "1")).joinToString(",") }

        importer.import(idLess).shouldBeSuccess()

        strains.allNow().single().name shouldBe "Bedrocan"
        uses.all().first().map { it.strainId }.toSet() shouldBe setOf(strains.allNow().single().id)
      }
    }

    test("Makes one strain when an id-less line comes before the line giving the strain's id") {
      with(Catalog()) {
        val bedrocan = Strain("Bedrocan", BigDecimal("22"), id = "from-the-file")
        val idLess = (UseArb.take(1).single().columns() + listOf("", "Bedrocan", "22", "")).joinToString(",")

        importer.import(listOf(idLess, line(bedrocan))).shouldBeSuccess()

        strains.allNow().map { it.id } shouldBe listOf("from-the-file")
        uses.all().first().map { it.strainId }.toSet() shouldBe setOf("from-the-file")
      }
    }

    test("Joins an id-less line to a strain the file names by id, even though it was renamed here since") {
      with(Catalog()) {
        val renamed = flm.copy(name = "FLM, 2023 batch")
        strains.upsert(renamed)
        val idLess = (UseArb.take(1).single().columns() + listOf("", flm.name)).joinToString(",")

        importer.import(listOf(line(flm), idLess)).shouldBeSuccess()

        strains.allNow() shouldContainExactly listOf(renamed)
        uses.all().first().map { it.strainId }.toSet() shouldBe setOf(renamed.id)
      }
    }

    test("Restoring onto an empty catalog brings strains back as they were") {
      with(Catalog()) {
        val archived = Strain("Old batch", BigDecimal("22"), BigDecimal("1"), BigDecimal("9.80"), isArchived = true)

        importer.import(listOf(line(archived))).shouldBeSuccess()

        strains.allNow() shouldContainExactly listOf(archived)
      }
    }

    test("Restoring onto an empty catalog keeps apart strains that share a name") {
      with(Catalog()) {
        val oldBatch = Strain(flm.name, BigDecimal("22"), isArchived = true)
        val newBatch = Strain(flm.name, BigDecimal("27"))

        importer.import(listOf(line(oldBatch), line(newBatch))).shouldBeSuccess()

        strains.allNow().map { it.id }.toSet() shouldBe setOf(oldBatch.id, newBatch.id)
      }
    }

    context("An existing use") {
      val existing = UseArb.take(1).single().copy(strainId = flm.id)
      fun legacyLine() = existing.columns().joinToString(",")
      fun emptyStrainLine() = (existing.columns() + List(Strain.CsvColumnCount) { "" }).joinToString(",")

      test("keeps its strain when the line comes from before strains") {
        with(Catalog()) {
          strains.upsert(flm)
          uses.upsert(existing)

          importer.import(listOf(legacyLine())).shouldBeSuccess()

          uses.all().first().single().strainId shouldBe flm.id
        }
      }

      test("keeps its strain when the line has empty strain columns, as an import never unlinks") {
        with(Catalog()) {
          strains.upsert(flm)
          uses.upsert(existing)

          importer.import(listOf(emptyStrainLine())).shouldBeSuccess()

          uses.all().first().single().strainId shouldBe flm.id
        }
      }

      test("takes the strain the line names") {
        with(Catalog()) {
          val bedrocan = Strain("Bedrocan", id = "bedrocan")
          strains.upsertAll(listOf(flm, bedrocan))
          uses.upsert(existing)

          importer.import(listOf((existing.columns() + bedrocan.columns()).joinToString(","))).shouldBeSuccess()

          uses.all().first().single().strainId shouldBe bedrocan.id
        }
      }
    }

    test("Saves nothing when a line can't be parsed") {
      with(Catalog()) {
        importer.import(listOf(line(flm), line(flm), "not,a,use")).shouldBeFailure()

        strains.allNow() shouldHaveSize 0
        uses.all().first() shouldHaveSize 0
      }
    }

    test("Rolls back the strains it created when saving the uses fails") {
      val database = inMemoryDatabase()
      val strains = StrainRepository(database.strainQueries)
      val failingUses = spyk(UseRepository(database.useQueries)) {
        every { upsertAllKeepingStrains(any()) } throws IllegalStateException("disk full")
      }

      UseImporter(failingUses, strains, database).import(listOf(line(flm))).shouldBeFailure()

      strains.allNow() shouldHaveSize 0
    }
  }
})

val invalidUseCsvArb = UseCsvArb.map {
  Random.mutants(replaceWithPossiblyMeaningfulText(), 1, it)
}.map { it.single() }
