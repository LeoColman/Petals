package br.com.colman.petals.use.pause.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import br.com.colman.petals.Database
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.localTime
import io.kotest.property.arbitrary.next
import kotlinx.coroutines.flow.first
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import br.com.colman.petals.Pause as PauseEntity

class PauseRepositoryTest : FunSpec({

  val database = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).let {
    Database.Schema.create(it)
    Database(it)
  }
  val target = PauseRepository(database.pauseQueries)

  val pause = Pause(Arb.localTime().next(), Arb.localTime().next())
  val otherPause = Pause(Arb.localTime().next(), Arb.localTime().next())

  context("Get All") {
    test("returns empty list when database is empty") {
      target.getAll().first() shouldBe emptyList()
    }

    test("returns all inserted pauses") {
      target.insert(pause)
      target.insert(otherPause)
      target.getAll().first() shouldContainAll listOf(pause, otherPause)
    }

    test("sorts by start time then end time") {
      val earlyStart = pause.copy(startTime = LocalTime.NOON.minusHours(1))
      val lateStart = otherPause.copy(startTime = LocalTime.NOON)

      target.insert(lateStart)
      target.insert(earlyStart)
      target.getAll().first() shouldBe listOf(earlyStart, lateStart)
    }

    test("sorts by end time when start times are equal") {
      val earlyEnd = pause.copy(
        startTime = LocalTime.NOON,
        endTime = LocalTime.NOON.plusHours(1)
      )
      val lateEnd = otherPause.copy(
        startTime = LocalTime.NOON,
        endTime = LocalTime.NOON.plusHours(2)
      )

      target.insert(lateEnd)
      target.insert(earlyEnd)
      target.getAll().first() shouldBe listOf(earlyEnd, lateEnd)
    }
  }

  context("Update Operations") {
    test("does nothing for non-existent pause") {
      val nonExistentPause = Pause(LocalTime.MIN, LocalTime.MAX, id = "999")
      target.update(nonExistentPause)
      target.getAll().first() shouldBe emptyList()
    }

    test("modifies existing pause") {
      target.insert(pause)
      val updated = pause.copy(isEnabled = false)
      target.update(updated)
      target.getAll().first() shouldContain updated
    }

    test("persists time changes") {
      target.insert(pause)
      val updated = pause.copy(
        startTime = LocalTime.NOON,
        endTime = LocalTime.MIDNIGHT
      )
      target.update(updated)
      target.getAll().first().single() shouldBe updated
    }
  }

  context("Insert Operations") {
    test("stores new pause") {
      target.insert(pause)
      target.getAll().first() shouldContain pause
    }

    test("stores disabled state correctly") {
      val disabledPause = pause.copy(isEnabled = false)
      target.insert(disabledPause)
      target.getAll().first()
        .find { it.id == disabledPause.id }
        ?.isEnabled shouldBe false
    }

    test("throws constraint exception for duplicate IDs") {
      val pause1 = pause.copy(id = "1")
      val pause2 = otherPause.copy(id = "1")
      target.insert(pause1)
      shouldThrowAny {
        target.insert(pause2)
      }
    }

    test("handles boundary time values") {
      val minTime = LocalTime.MIN
      val maxTime = LocalTime.MAX.truncatedTo(ChronoUnit.SECONDS)
      val boundaryPause = Pause(minTime, maxTime)

      target.insert(boundaryPause)
      val retrieved = target.getAll().first().single()

      retrieved.startTime shouldBe minTime
      retrieved.endTime shouldBe maxTime
    }
  }

  context("Delete Operations") {
    test("removes pause by ID") {
      target.insert(pause)
      target.delete(pause)
      target.getAll().first() shouldNotContain pause
    }

    test("ignores non-existent pauses") {
      target.insert(pause)
      target.delete(Pause(LocalTime.MIN, LocalTime.MAX, id = "999"))
      target.getAll().first() shouldContain pause
    }
  }

  context("Conversion Logic") {
    test("Pause to Entity converts times to ISO strings") {
      val normalPause = Pause(
        startTime = LocalTime.of(14, 30, 45),
        endTime = LocalTime.of(15, 0),
        isEnabled = true
      )

      val entity = normalPause.toEntity()
      entity.start_time shouldBe "14:30:45"
      entity.end_time shouldBe "15:00:00"
      entity.is_enabled shouldBe 1L
    }

    test("Entity to Pause parses ISO time strings") {
      val entity = PauseEntity(
        start_time = "23:59:59",
        end_time = "00:00:00",
        id = "123",
        is_enabled = 0L
      )

      val convertedPause = entity.toPause()
      convertedPause.startTime shouldBe LocalTime.of(23, 59, 59)
      convertedPause.endTime shouldBe LocalTime.MIDNIGHT
      convertedPause.isEnabled shouldBe false
    }

    test("Disabled state round-trip conversion") {
      val original = Pause(
        startTime = LocalTime.NOON,
        endTime = LocalTime.MIDNIGHT,
        isEnabled = false
      )

      val roundTripped = original.toEntity().toPause()
      roundTripped shouldBe original
    }

    test("Boundary time round-trip conversion") {
      val minTime = LocalTime.MIN
      val maxTime = LocalTime.MAX.truncatedTo(ChronoUnit.SECONDS)

      val original = Pause(minTime, maxTime)
      val roundTripped = original.toEntity().toPause()

      roundTripped.startTime shouldBe minTime
      roundTripped.endTime shouldBe maxTime
    }

    test("ID preservation in conversions") {
      val normalPause = Pause(
        startTime = LocalTime.NOON,
        endTime = LocalTime.NOON.plusHours(1),
        id = "unique-123"
      )

      val entity = normalPause.toEntity()
      entity.id shouldBe "unique-123"

      val convertedBack = entity.toPause()
      convertedBack.id shouldBe "unique-123"
    }
  }

  isolationMode = IsolationMode.InstancePerTest
})
