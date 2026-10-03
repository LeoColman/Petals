package br.com.colman.petals.strain.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver.Companion.IN_MEMORY
import br.com.colman.petals.Database
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import java.util.Locale

class StrainRepositoryTest : FunSpec({

  val database = JdbcSqliteDriver(IN_MEMORY).let {
    Database.Schema.create(it)
    Database(it)
  }

  val target = StrainRepository(database.strainQueries)
  val uses = UseRepository(database.useQueries)

  val flm = Strain("420 Evo FLM", BigDecimal("27.5"), BigDecimal("1"), BigDecimal("12.50"))

  test("Starts empty") {
    target.all().first().shouldBeEmpty()
  }

  test("Reads back every field it saved") {
    val archived = Strain("Old batch", BigDecimal("18"), BigDecimal("0.5"), BigDecimal("9.80"), isArchived = true)
    target.upsertAll(listOf(flm, archived))

    target.all().first() shouldContainExactly listOf(flm, archived)
  }

  test("Reads back a strain with no potency or cost") {
    val bare = Strain("Bedrocan")
    target.upsert(bare)

    target.all().first().single() shouldBe bare
  }

  test("Upsert updates the strain with the same id") {
    target.upsert(flm)
    val edited = flm.copy(name = "420 Evo FLM 27/1", costPerGram = null, isArchived = true)
    target.upsert(edited)

    target.all().first().single() shouldBe edited
  }

  test("Orders strains by name, ignoring case") {
    val names = listOf("bedrocan", "Aurora", "420 Evo FLM", "Cannatrek")
    target.upsertAll(names.map { Strain(it) })

    target.all().first().map { it.name } shouldBe listOf("420 Evo FLM", "Aurora", "bedrocan", "Cannatrek")
  }

  test("Orders accented names where people expect them, not after z") {
    target.upsertAll(listOf("Zkittlez", "\u00c9clair", "apple").map { Strain(it) })

    target.all().first().map { it.name } shouldBe listOf("apple", "\u00c9clair", "Zkittlez")
  }

  // The JVM's collator only knows Cyrillic under a Cyrillic locale, while Android's, backed by ICU, sorts every
  // script under any locale. Pinning the locale keeps this a test of the ordering, not of the machine running it.
  test("Orders Cyrillic names ignoring case, as a Russian reader expects") {
    val default = Locale.getDefault()
    Locale.setDefault(Locale.forLanguageTag("ru"))
    try {
      target.upsertAll(listOf("Яблоко", "арбуз", "Ёлка").map { Strain(it) })

      target.all().first().map { it.name } shouldBe listOf("арбуз", "Ёлка", "Яблоко")
    } finally {
      Locale.setDefault(default)
    }
  }

  test("allNow reads the same strains as all") {
    target.upsertAll(listOf(flm, Strain("Bedrocan")))

    target.allNow() shouldBe target.all().first()
  }

  context("Uses") {
    test("Counts the uses logged with a strain") {
      target.upsert(flm)
      uses.upsertAll(List(3) { Use(strainId = flm.id) } + Use(strainId = "other") + Use())

      target.countUses(flm) shouldBe 3
    }

    test("A strain nobody used has no uses") {
      target.countUses(flm) shouldBe 0
    }
  }

  context("Delete") {
    test("Deletes a strain no use refers to") {
      target.upsert(flm)

      target.delete(flm).shouldBeTrue()
      target.all().first().shouldBeEmpty()
    }

    test("Keeps a strain a use refers to") {
      target.upsert(flm)
      uses.upsert(Use(strainId = flm.id))

      target.delete(flm).shouldBeFalse()
      target.all().first().single() shouldBe flm
    }

    test("Leaves the other strains alone") {
      val bedrocan = Strain("Bedrocan")
      target.upsertAll(listOf(flm, bedrocan))

      target.delete(flm)

      target.all().first().single() shouldBe bedrocan
    }
  }
})
