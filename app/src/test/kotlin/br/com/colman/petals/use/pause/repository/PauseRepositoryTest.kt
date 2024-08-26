package br.com.colman.petals.use.pause.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import br.com.colman.petals.Database
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.localTime
import io.kotest.property.arbitrary.next
import kotlinx.coroutines.flow.first

class PauseRepositoryTest : FunSpec({

  val database = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).let {
    Database.Schema.create(it)
    Database(it)
  }
  val target = PauseRepository(database.pauseQueries)

  val pause = Pause(Arb.localTime().next(), Arb.localTime().next())
  val otherPause = Pause(Arb.localTime().next(), Arb.localTime().next())

  test("Insert") {
    target.insert(pause)
    database.pauseQueries.selectFirst().executeAsOne().toPause() shouldBe pause
  }

  test("Updating existing value after insert") {
    target.insert(pause)
    val updatedPause = pause.copy(isEnabled = false)
    target.update(updatedPause)
    database.pauseQueries.selectFirst().executeAsOne().toPause() shouldBe updatedPause
  }

  test("Get all") {
    target.insert(pause)
    target.insert(otherPause)
    database.pauseQueries.selectAll().executeAsList().map { it.toPause() } shouldContain pause
    database.pauseQueries.selectAll().executeAsList().map { it.toPause() } shouldContain otherPause
  }

  test("Get all should be sorted") {
    target.insert(pause)
    target.insert(otherPause)

    target.getAll().first().shouldBeSortedWith(compareBy({ it.startTime }, { it.endTime }))
  }

  test("Delete by id") {
    target.insert(pause)
    target.insert(otherPause)
    database.pauseQueries.selectAll().executeAsList().map {
      it.toPause()
    } shouldContainExactlyInAnyOrder listOf(pause, otherPause)

    target.delete(pause)
    database.pauseQueries.selectAll().executeAsList().map { it.toPause() } shouldNotContain pause

    target.delete(otherPause)
    database.pauseQueries.selectAll().executeAsList().map { it.toPause() } shouldNotContain otherPause
  }

  test("Get") {
    database.pauseQueries.insert(pause.toEntity())
    target.get().first() shouldBe pause
  }

  isolationMode = IsolationMode.InstancePerTest
})
