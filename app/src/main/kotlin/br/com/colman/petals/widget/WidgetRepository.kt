package br.com.colman.petals.widget

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import br.com.colman.petals.R
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class WidgetRepository(
  private val datastore: DataStore<Preferences>,
) {

  val iconKey = datastore.data.map { it[IconKey] ?: R.drawable.ic_repeat }

  fun setIconValue(value: Int): Unit = runBlocking {
    datastore.edit {it[IconKey] = value}
  }

  companion object {
    val IconKey = intPreferencesKey("icon")
  }
}
