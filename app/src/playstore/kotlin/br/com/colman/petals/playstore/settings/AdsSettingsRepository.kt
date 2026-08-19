package br.com.colman.petals.playstore.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import br.com.colman.petals.ads.RewardedAdFreeDuration
import br.com.colman.petals.ads.adFreeTimeRemaining
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.Instant

class AdsSettingsRepository(
  private val datastore: DataStore<Preferences>,
  private val clock: () -> Instant = Instant::now
) {

  /** Whether ads were bought away for good. */
  val isAdsFree: Flow<Boolean> = datastore.data.map { it[isAdFree] ?: false }

  private val adFreeUntil: Flow<Instant?> = datastore.data.map { preferences ->
    preferences[adFreeUntilKey]?.let(Instant::ofEpochMilli)
  }

  /** How much of the rewarded ad-free time is left, or null when no reward is running. */
  val rewardedTimeRemaining: Flow<Duration?> = combine(adFreeUntil, ticks()) { until, now ->
    adFreeTimeRemaining(until, now)
  }.distinctUntilChanged()

  /** Whether ads must be hidden right now, be it because they were bought away or watched away. */
  val isCurrentlyAdFree: Flow<Boolean> = combine(isAdsFree, rewardedTimeRemaining) { purchased, remaining ->
    purchased || remaining != null
  }.distinctUntilChanged()

  fun setAdFree(value: Boolean): Unit = runBlocking {
    datastore.edit {
      it[isAdFree] = value
    }
  }

  /** Hides the ads for [RewardedAdFreeDuration], starting now. */
  fun grantRewardedAdFreeTime(): Unit = runBlocking {
    datastore.edit {
      it[adFreeUntilKey] = clock().plus(RewardedAdFreeDuration).toEpochMilli()
    }
  }

  private fun ticks(): Flow<Instant> = flow {
    while (true) {
      emit(clock())
      delay(TickInterval)
    }
  }

  companion object {
    val isAdFree = booleanPreferencesKey("is_adfree")
    val adFreeUntilKey = longPreferencesKey("ad_free_until")

    private val TickInterval = Duration.ofSeconds(10).toMillis()
  }
}
