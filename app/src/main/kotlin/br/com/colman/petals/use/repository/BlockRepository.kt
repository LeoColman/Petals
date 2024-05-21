package br.com.colman.petals.use.repository

import androidx.annotation.StringRes
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import br.com.colman.petals.R
import br.com.colman.petals.use.repository.BlockType.AllTime
import br.com.colman.petals.use.repository.BlockType.ThisMonth
import br.com.colman.petals.use.repository.BlockType.ThisWeek
import br.com.colman.petals.use.repository.BlockType.ThisYear
import br.com.colman.petals.use.repository.BlockType.Today
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class BlockRepository(
  private val datastore: DataStore<Preferences>
) {
  val isTodayCensored = datastore.data.map { it[Today.preferencesKey] ?: false }
  val isThisWeekCensored = datastore.data.map { it[ThisWeek.preferencesKey] ?: false }
  val isThisMonthCensored = datastore.data.map { it[ThisMonth.preferencesKey] ?: false }
  val isThisYearCensored = datastore.data.map { it[ThisYear.preferencesKey] ?: false }
  val isAllTimeCensored = datastore.data.map { it[AllTime.preferencesKey] ?: false }

  fun setBlockCensure(blockType: BlockType, isCensored: Boolean): Unit = runBlocking {
    datastore.edit { it[blockType.preferencesKey] = isCensored }
  }
}

enum class BlockType(@StringRes val resourceId: Int, val preferencesKey: Preferences.Key<Boolean>) {
  Today(R.string.today, booleanPreferencesKey("IsTodayCensored")),
  ThisWeek(R.string.this_week, booleanPreferencesKey("IsThisWeekCensored")),
  ThisMonth(R.string.this_month, booleanPreferencesKey("IsThisMonthCensored")),
  ThisYear(R.string.this_year, booleanPreferencesKey("IsThisYearCensored")),
  AllTime(R.string.all_time, booleanPreferencesKey("IsAllTimeCensored"))
}
