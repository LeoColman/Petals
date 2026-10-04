package br.com.colman.petals.use.io.input

import br.com.colman.petals.Database
import br.com.colman.petals.inMemoryDatabase
import br.com.colman.petals.strain.repository.Strain
import br.com.colman.petals.strain.repository.StrainRepository
import br.com.colman.petals.use.UseArb
import br.com.colman.petals.use.io.UseCsvArb
import br.com.colman.petals.use.io.output.UseCsvHeaders
import br.com.colman.petals.use.io.output.UseCsvSerializer
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import com.natpryce.snodge.mutants
import com.natpryce.snodge.text.replaceWithPossiblyMeaningfulText
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeSingleton
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
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

  class Catalog(val database: Database = inMemoryDatabase()) {
    val uses = UseRepository(database.useQueries)
    val strains = StrainRepository(database.strainQueries)
    val importer = UseImporter(uses, strains, database)
  }

  context("Parse file") {
    test("Returns how many uses it saved if all lines are parseable") {
      val usesCsv = UseCsvArb.take(1000).toList()
      target.import(usesCsv.joinToString("\n")) shouldBeSuccess 1000
    }

    test("Returns how many uses it saved, without the header, if the only unparseable line is the header") {
      val header = "my,header,line"
      val usesCsv = UseCsvArb.take(1000)
      val csvLines = listOf(header) + usesCsv

      target.import(csvLines.joinToString("\n")) shouldBeSuccess 1000
    }

    test("Returns failure when any line other than the header is unparseable") {
      val header = "my,header,line"
      val usesCsv = UseCsvArb.take(1000).toList()
      val invalidUseCsvs = invalidUseCsvArb.take(1000).toList()

      val firstLine = listOf(usesCsv.first())
      val otherLines = (listOf(header) + usesCsv + invalidUseCsvs).shuffled()

      target.import((firstLine + otherLines).joinToString("\n")).shouldBeFailure()
    }

    test("Returns failure when a quote is never closed") {
      target.import(UseCsvArb.take(3).joinToString("\n", postfix = ",\"unclosed")).shouldBeFailure()
    }

    test("Returns success with no use when file is empty") {
      target.import("") shouldBeSuccess 0
    }
  }

  test("Saves every line with the upsert that never takes a strain away, in the file's order") {
    val uses = UseArb.take(3).toList()
    val lines = listOf(uses[0].columns(), uses[1].columns() + List(Strain.CsvColumnCount) { "" }, uses[2].columns())

    target.import(lines.joinToString("\n") { it.joinToString(",") }).shouldBeSuccess()

    verify { useRepository.upsertAllKeepingStrains(uses) }
    verify(exactly = 0) { useRepository.upsertAll(any()) }
  }

  context("Data ingestion") {
    test("Doesn't call database when a line is wrong") {
      val wrongLine = "invalid,csv,line,is,invalid"

      target.import(List(2) { wrongLine }.joinToString("\n"))

      shouldNotThrowAny {
        verify { useRepository wasNot Called }
      }
    }

    test("Ingests the data if the only wrong line is the header") {
      val header = "my,beautiful,header"
      val uses = UseArb.take(1000).toList()

      val csvs = listOf(header) + uses.map { it.columns().joinToString(",") }

      target.import(csvs.joinToString("\n"))

      shouldNotThrowAny {
        verify {
          useRepository.upsertAllKeepingStrains(uses)
        }
      }
    }

    test("Ingests the data if all lines are parseable") {
      val uses = UseArb.take(1000).toList()

      val csvs = uses.map { it.columns().joinToString(",") }
      target.import(csvs.joinToString("\n"))

      shouldNotThrowAny {
        verify {
          useRepository.upsertAllKeepingStrains(uses)
        }
      }
    }

    test("Doesn't do anything if the file is empty") {
      target.import("")

      shouldNotThrowAny {
        verify {
          useRepository.upsertAllKeepingStrains(emptyList())
        }
      }
    }
  }

  context("Its own export") {
    val headers = UseCsvHeaders("date", "amount", "cost", "id", "description", "method")
    suspend fun exportOf(saved: List<Use>, strain: Strain) = with(Catalog()) {
      strains.upsert(strain)
      uses.upsertAll(saved)
      UseCsvSerializer(uses, headers).computeUseCsv()
    }

    test("Imports every use, even when one's notes hold a line break") {
      val bedrocan = Strain("Bedrocan", BigDecimal("22"))
      val saved = listOf(
        UseArb.next().copy(description = "Bedrocan\nfelt sleepy", strainId = bedrocan.id),
        UseArb.next().copy(description = "")
      )

      with(Catalog()) {
        importer.import(exportOf(saved, bedrocan)) shouldBeSuccess 2

        uses.all().first() shouldContainExactlyInAnyOrder saved
        strains.allNow() shouldContainExactly listOf(bedrocan)
      }
    }

    test("Imports back whatever the notes hold, line breaks, commas and quotes included") {
      val notes = Arb.string(0..12, Arb.element("ab \n\r,\"".map { Codepoint(it.code) }))
      val strain = Strain("Strain, \"with\"\nall of them", BigDecimal("22"))
      val saved = UseArb.map { it.copy(description = notes.next(), strainId = strain.id) }.take(500).toList()

      with(Catalog()) {
        importer.import(exportOf(saved, strain)) shouldBeSuccess saved.size

        uses.all().first() shouldContainExactlyInAnyOrder saved
        strains.allNow() shouldContainExactly listOf(strain)
      }
    }
  }

  context("Strains") {
    val flm = Strain("420 Evo FLM", BigDecimal("27"), BigDecimal("1"))
    val header = (List(6) { "column" } + Strain.CsvHeader).joinToString(",")
    fun withHeader(lines: List<String>) = (listOf(header) + lines).joinToString("\n")
    fun withHeader(vararg lines: String) = withHeader(lines.toList())
    fun line(strain: Strain?) =
      (UseArb.take(1).single().columns() + (strain?.columns() ?: List(Strain.CsvColumnCount) { "" })).joinToString(",")

    test("Creates the strain a file names and links its uses to it") {
      with(Catalog()) {
        importer.import(withHeader(line(flm))).shouldBeSuccess()

        strains.allNow() shouldContainExactly listOf(flm)
        uses.all().first().single().strainId shouldBe flm.id
      }
    }

    test("Creates a strain named on many lines only once") {
      with(Catalog()) {
        importer.import(withHeader(List(3) { line(flm) })).shouldBeSuccess()

        strains.allNow().shouldBeSingleton()
        uses.all().first().map { it.strainId }.toSet() shouldBe setOf(flm.id)
      }
    }

    test("Links to a catalog strain with the same name, ignoring case, instead of creating another") {
      with(Catalog()) {
        val catalogued = Strain("420 evo flm")
        strains.upsert(catalogued)

        importer.import(withHeader(line(flm))).shouldBeSuccess()

        strains.allNow() shouldContainExactly listOf(catalogued)
        uses.all().first().single().strainId shouldBe catalogued.id
      }
    }

    test("Links to the catalog strain with the same id even after it was renamed") {
      with(Catalog()) {
        val renamed = flm.copy(name = "FLM, new batch")
        strains.upsert(renamed)

        importer.import(withHeader(line(flm))).shouldBeSuccess()

        strains.allNow() shouldContainExactly listOf(renamed)
        uses.all().first().single().strainId shouldBe renamed.id
      }
    }

    test("Imports lines without strain columns with no strain") {
      with(Catalog()) {
        importer.import(withHeader(line(null))).shouldBeSuccess()

        strains.allNow() shouldHaveSize 0
        uses.all().first().single().strainId shouldBe null
      }
    }

    test("Makes one strain of id-less lines that name the same strain") {
      with(Catalog()) {
        val idLess = List(
          3
        ) { (UseArb.take(1).single().columns() + listOf("", "Bedrocan", "22", "1")).joinToString(",") }

        importer.import(withHeader(idLess)).shouldBeSuccess()

        strains.allNow().single().name shouldBe "Bedrocan"
        uses.all().first().map { it.strainId }.toSet() shouldBe setOf(strains.allNow().single().id)
      }
    }

    test("Makes one strain when an id-less line comes before the line giving the strain's id") {
      with(Catalog()) {
        val bedrocan = Strain("Bedrocan", BigDecimal("22"))
        val idLess = (UseArb.take(1).single().columns() + listOf("", "Bedrocan", "22", "")).joinToString(",")

        importer.import(withHeader(idLess, line(bedrocan))).shouldBeSuccess()

        strains.allNow().map { it.id } shouldBe listOf(bedrocan.id)
        uses.all().first().map { it.strainId }.toSet() shouldBe setOf(bedrocan.id)
      }
    }

    test("Joins an id-less line to a strain the file names by id, even though it was renamed here since") {
      with(Catalog()) {
        val renamed = flm.copy(name = "FLM, 2023 batch")
        strains.upsert(renamed)
        val idLess = (UseArb.take(1).single().columns() + listOf("", flm.name)).joinToString(",")

        importer.import(withHeader(line(flm), idLess)).shouldBeSuccess()

        strains.allNow() shouldContainExactly listOf(renamed)
        uses.all().first().map { it.strainId }.toSet() shouldBe setOf(renamed.id)
      }
    }

    test("Restoring onto an empty catalog brings strains back as they were") {
      with(Catalog()) {
        val archived = Strain("Old batch", BigDecimal("22"), BigDecimal("1"), BigDecimal("9.80"), isArchived = true)

        importer.import(withHeader(line(archived))).shouldBeSuccess()

        strains.allNow() shouldContainExactly listOf(archived)
      }
    }

    test("Restoring onto an empty catalog keeps apart strains that share a name") {
      with(Catalog()) {
        val oldBatch = Strain(flm.name, BigDecimal("22"), isArchived = true)
        val newBatch = Strain(flm.name, BigDecimal("27"))

        importer.import(withHeader(line(oldBatch), line(newBatch))).shouldBeSuccess()

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

          importer.import(withHeader(legacyLine())).shouldBeSuccess()

          uses.all().first().single().strainId shouldBe flm.id
        }
      }

      test("keeps its strain when the line has empty strain columns, as an import never unlinks") {
        with(Catalog()) {
          strains.upsert(flm)
          uses.upsert(existing)

          importer.import(withHeader(emptyStrainLine())).shouldBeSuccess()

          uses.all().first().single().strainId shouldBe flm.id
        }
      }

      test("takes the strain the line names") {
        with(Catalog()) {
          val bedrocan = Strain("Bedrocan", id = "bedrocan")
          strains.upsertAll(listOf(flm, bedrocan))
          uses.upsert(existing)

          importer.import(withHeader((existing.columns() + bedrocan.columns()).joinToString(","))).shouldBeSuccess()

          uses.all().first().single().strainId shouldBe bedrocan.id
        }
      }
    }

    test("Reads no strain from a file without the strain header, whatever its extra columns hold") {
      with(Catalog()) {
        val userColumns = (UseArb.take(1).single().columns() + listOf("my note", "my tag")).joinToString(",")
        val unlabelled = (UseArb.take(1).single().columns() + flm.columns()).joinToString(",")

        importer.import("date,amount,cost,id,description,method,notes,tags\n$userColumns").shouldBeSuccess()
        importer.import(unlabelled).shouldBeSuccess()

        strains.allNow() shouldHaveSize 0
        uses.all().first().map { it.strainId }.toSet() shouldBe setOf(null)
      }
    }

    test("Saves nothing when a line can't be parsed") {
      with(Catalog()) {
        importer.import(withHeader(line(flm), line(flm), "not,a,use")).shouldBeFailure()

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

      UseImporter(failingUses, strains, database).import(withHeader(line(flm))).shouldBeFailure()

      strains.allNow() shouldHaveSize 0
    }
  }
})

val invalidUseCsvArb = UseCsvArb.map {
  Random.mutants(replaceWithPossiblyMeaningfulText(), 1, it)
}.map { it.single() }
