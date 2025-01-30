/*
 * Petals APP
 * Copyright (C) 2021 Leonardo Colman Lopes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package br.com.colman.petals.use.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver.Companion.IN_MEMORY
import br.com.colman.petals.Database
import br.com.colman.petals.use.UseArb
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.take
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import java.util.UUID
import kotlin.system.measureTimeMillis

class UseRepositoryTest : FunSpec({

  val database = JdbcSqliteDriver(IN_MEMORY).let {
    Database.Schema.create(it)
    Database(it)
  }

  val target = UseRepository(database.useQueries)

  val use = Use(amountGrams = BigDecimal("1234.567"), costPerGram = BigDecimal("123.01"))

  test("Plain insert") {
    target.upsert(use)
    database.useQueries.selectAll().executeAsOne() shouldBe use.toEntity()
  }

  test("Plain Insert multiple") {
    val uses = List(10) {
      use.copy(id = "$it")
    }
    target.upsertAll(uses)
    target.all().first() shouldBe uses
  }

  test("Upsert") {
    val otherUse = use.copy(amountGrams = 1.0.toBigDecimal())
    target.upsert(use)
    target.upsert(otherUse)

    target.all().first().single() shouldBe otherUse
  }

  test("Get all") {
    database.useQueries.upsert(use.toEntity())
    target.all().first().single() shouldBe use
  }

  test("Count All should return 0 when empty") {
    target.countAll().first() shouldBe 0
  }

  test("Count All should return number of elements") {
    val otherUse = use.copy(amountGrams = 1.0.toBigDecimal(), id = UUID.randomUUID().toString())
    target.upsert(use)
    target.upsert(otherUse)
    target.countAll().first() shouldBe 2L
  }

  test("Delete") {
    database.useQueries.upsert(use.toEntity())
    target.delete(use)
    database.useQueries.selectAll().executeAsList().shouldBeEmpty()
  }

  test("Last use") {
    val useBefore = use.copy(date = use.date.minusYears(1), id = "2")
    target.upsert(use)
    target.upsert(useBefore)

    target.getLastUse().first() shouldBe use
    target.getLastUseDate().first() shouldBe use.date
  }

  test("Last use should disconsider uses in the future") {
    val useInTheFuture = use.copy(date = use.date.plusHours(3), id = "2")
    target.upsert(use)
    target.upsert(useInTheFuture)

    target.getLastUse().first() shouldBe use
    target.getLastUseDate().first() shouldBe use.date
  }

  test("Last use date should return null when no nulls are added") {
    target.getLastUseDate().first() shouldBe null
  }

  test("Last use performance") {
    UseArb.take(10_000).map(Use::toEntity).forEach(database.useQueries::upsert)

    measureTimeMillis {
      target.getLastUseDate().first()
    } shouldBeLessThan measureTimeMillis {
      target.all().first().maxByOrNull { it.date }
    }
  }

  isolationMode = IsolationMode.InstancePerTest
})
