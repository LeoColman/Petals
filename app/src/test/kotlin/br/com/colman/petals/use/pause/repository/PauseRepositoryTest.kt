package br.com.colman.petals.use.pause.repository

import br.com.colman.petals.Database
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.localTime
import io.kotest.property.arbitrary.next

class PauseRepositoryTest : FunSpec({

  val database = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).let {
    Database.Schema.create(it)
    Database(it)
  }
  val target = PauseRepository(database.pauseQueries)

  val pause = Pause(Arb.localTime().next(), Arb.localTime().next())
  val otherPause = Pause(Arb.localTime().next(), Arb.localTime().next())

  test("Set") {
    target.set(pause)
    database.pauseQueries.selectFirst().executeAsOne().toPause() shouldBe pause
  }

  test("Replaces existing value after set") {
    target.set(pause)
    target.set(otherPause)
    database.pauseQueries.selectFirst().executeAsOne().toPause() shouldBe otherPause
  }

  test("Get") {
    database.pauseQueries.insert(pause.toEntity())
    target.get() shouldBe pause
  }

  test("Delete") {
    database.pauseQueries.insert(pause.toEntity())
    target.delete()
    database.pauseQueries.selectFirst().executeAsList().shouldBeEmpty()
  }

  isolationMode = IsolationMode.InstancePerTest
})
