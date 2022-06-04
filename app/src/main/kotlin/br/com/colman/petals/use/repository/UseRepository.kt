@file:OptIn(ExperimentalCoroutinesApi::class)

package br.com.colman.petals.use.repository

import io.objectbox.Box
import io.objectbox.kotlin.toFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class UseRepository(private val box: Box<Use>) {

  // TODO Test
  fun insertAll(uses: Iterable<Use>) {
    uses.forEach(::insert)
  }

  fun insert(use: Use) {
    Timber.d("Adding use: $use")
    box.put(use)
  }

  fun getLastUse() = all().map { it.maxByOrNull { it.date } }

  fun getLastUseDate() = getLastUse().map { it?.date }

  fun all(): Flow<List<Use>> = box.query().build().subscribe().toFlow().map { it.toList() }

  fun delete(use: Use) {
    Timber.d("Deleting use: $use")
    box.remove(use)
  }
}
