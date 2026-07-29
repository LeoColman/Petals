package br.com.colman.petals.hittimer.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver.Companion.IN_MEMORY
import br.com.colman.petals.Database
import br.com.colman.petals.hittimer.HitTimer
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDateTime

class HitRepositoryTest : FunSpec({

  val database = JdbcSqliteDriver(IN_MEMORY).let {
    Database.Schema.create(it)
    Database(it)
  }

  val target = HitRepository(database.hitQueries)
  val now: LocalDateTime = LocalDateTime.of(2026, 1, 31, 12, 0)
  val hit = Hit(now.minusHours(1), Duration.ofMillis(12_345), "a-hit")

  test("Stores and reads a hit back whole") {
    target.upsert(hit)

    target.all().first().single() shouldBe hit
  }

  test("Upserting the same id updates instead of duplicating") {
    target.upsert(hit)
    target.upsert(hit.copy(duration = Duration.ofMillis(20_000)))

    val stored = target.all().first().single()
    stored.id shouldBe "a-hit"
    stored.duration shouldBe Duration.ofMillis(20_000)
  }

  test("Reads oldest first") {
    val older = hit.copy(date = now.minusDays(3), id = "older")
    val newer = hit.copy(date = now.minusDays(1), id = "newer")
    target.upsert(newer)
    target.upsert(older)

    target.all().first().map { it.id } shouldBe listOf("older", "newer")
  }

  test("Future dated hits are not read back") {
    target.upsert(hit.copy(date = LocalDateTime.now().plusDays(1), id = "future"))

    target.all().first().shouldBeEmpty()
  }

  test("Delete all empties the table") {
    target.upsert(hit)
    target.deleteAll()

    target.all().first().shouldBeEmpty()
  }

  context("Recording a run") {
    test("Writes the elapsed hold under the run's id") {
      val timer = HitTimer(100L)
      timer.start()
      delay(50L)
      timer.pause()

      target.record(timer, now)

      val stored = target.all().first().single()
      stored.id shouldBe timer.runId
      (stored.duration.toMillis() >= 50L) shouldBe true
    }

    test("Recording twice in one run keeps a single hit, which is what pause then reset does") {
      val timer = HitTimer(100L)
      timer.start()
      delay(50L)
      timer.pause()

      target.record(timer, now)
      target.record(timer, now)

      target.all().first().size shouldBe 1
    }

    test("A fresh run writes a separate hit") {
      val timer = HitTimer(100L)
      timer.start()
      delay(30L)
      timer.pause()
      target.record(timer, now)

      timer.start()
      delay(30L)
      timer.pause()
      target.record(timer, now)

      target.all().first().size shouldBe 2
    }

    test("A run that never started records nothing") {
      target.record(HitTimer(100L), now)

      target.all().first().shouldBeEmpty()
    }
  }

  isolationMode = IsolationMode.InstancePerTest
})
