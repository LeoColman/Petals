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

import br.com.colman.petals.Database
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver.Companion.IN_MEMORY
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

class UseRepositoryTest : FunSpec({

  val database = JdbcSqliteDriver(IN_MEMORY).let {
    Database.Schema.create(it)
    Database(it)
  }

  val target = UseRepository(database.useQueries)

  val use = Use(amountGrams = BigDecimal("1234.567"), costPerGram = BigDecimal("123.01"))

  test("Insert") {
    target.insert(use)
    database.useQueries.selectAll().executeAsOne() shouldBe use.toEntity()
  }

  test("Insert multiple") {
    val uses = List(10) {
      use.copy(id = "$it")
    }
    target.insertAll(uses)
    target.all().first() shouldBe uses
  }

  test("Get all") {
    database.useQueries.insert(use.toEntity())
    target.all().first().single() shouldBe use
  }

  test("Delete") {
    database.useQueries.insert(use.toEntity())
    target.delete(use)
    database.useQueries.selectAll().executeAsList().shouldBeEmpty()
  }

  test("Last use") {
    val useBefore = use.copy(date = use.date.minusYears(1), id = "2")
    database.useQueries.insert(use.toEntity())
    database.useQueries.insert(useBefore.toEntity())

    target.getLastUse().first() shouldBe use
    target.getLastUseDate().first() shouldBe use.date
  }

  isolationMode = IsolationMode.InstancePerTest
})
