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

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.objectbox.kotlin.boxFor
import kotlinx.coroutines.flow.first
import java.math.BigDecimal

class UseRepositoryTest : FunSpec({

  val box = MyObjectBox.builder().build().boxFor<Use>()
  beforeEach { box.removeAll() }

  val target = UseRepository(box)

  val use = Use(amountGrams = BigDecimal("1234.567"), costPerGram = BigDecimal("123.01"))

  test("Insert") {
    target.insert(use)
    box.all.single() shouldBe use
  }

  test("Get all") {
    box.put(use)
    target.all().first().single() shouldBe use
  }

  test("Delete") {
    box.put(use)
    box.all.shouldNotBeEmpty()
    target.delete(use)
    box.all.shouldBeEmpty()
  }

  test("Last use") {
    val useBefore = use.copy(date = use.date.minusYears(1), id = 0)
    box.put(use)
    box.put(useBefore)

    target.getLastUse().first() shouldBe use
    target.getLastUseDate().first() shouldBe use.date
  }

  isolationMode = IsolationMode.SingleInstance
})
