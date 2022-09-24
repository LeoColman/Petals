package br.com.colman.petals.use.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class UseRepository {

  // TODO Test
  fun insertAll(uses: Iterable<Use>) {
    uses.forEach(::insert)
  }

  fun insert(use: Use) {
    Timber.d("Adding use: $use")
    TODO()
  }

  fun getLastUse() = all().map { it.maxByOrNull { it.date } }

  fun getLastUseDate() = getLastUse().map { it?.date }

  fun all(): Flow<List<Use>> = emptyFlow()

  fun delete(use: Use) {
    Timber.d("Deleting use: $use")
    TODO()
  }
}
