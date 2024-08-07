package br.com.colman.petals.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import br.com.colman.petals.settings.view.listitem.CurrencyListItem
import br.com.colman.petals.settings.view.listitem.DateListItem
import br.com.colman.petals.settings.view.listitem.ExtendDayListItem
import br.com.colman.petals.settings.view.listitem.HitTimerMillisecondsEnabledListItem
import br.com.colman.petals.settings.view.listitem.PinListItem
import br.com.colman.petals.settings.view.listitem.PrecisionListItem
import br.com.colman.petals.settings.view.listitem.RepositoryListItem
import br.com.colman.petals.settings.view.listitem.ShareApp
import br.com.colman.petals.settings.view.listitem.TimeListItem

@Composable
fun SettingsView(settingsRepository: SettingsRepository) {
  val currentCurrency by settingsRepository.currencyIcon.collectAsState("$")
  val currentDateFormat by settingsRepository.dateFormat.collectAsState(settingsRepository.dateFormatList[0])
  val currentTimeFormat by settingsRepository.timeFormat.collectAsState(settingsRepository.timeFormatList[0])
  val currentHitTimerMillisecondsEnabled by settingsRepository.hitTimerMillisecondsEnabled.collectAsState(
    settingsRepository.hitTimerMillisecondsEnabledList[0]
  )
  val currentDecimalPrecision by settingsRepository.decimalPrecision.collectAsState(
    settingsRepository.decimalPrecisionList[2]
  )
  val currentExtendDay: String by settingsRepository.extendedDay.collectAsState(settingsRepository.extendedDayList[1])

  Column(Modifier.verticalScroll(rememberScrollState())) {
    CurrencyListItem(currentCurrency, settingsRepository::setCurrencyIcon)
    PinListItem(settingsRepository::setPin)
    RepositoryListItem()
    DateListItem(currentDateFormat, settingsRepository.dateFormatList, settingsRepository::setDateFormat)
    TimeListItem(currentTimeFormat, settingsRepository.timeFormatList, settingsRepository::setTimeFormat)
    HitTimerMillisecondsEnabledListItem(
      currentHitTimerMillisecondsEnabled,
      settingsRepository.hitTimerMillisecondsEnabledList,
      settingsRepository::setHitTimerMillisecondsEnabled
    )
    PrecisionListItem(
      currentDecimalPrecision,
      settingsRepository.decimalPrecisionList,
      settingsRepository::setDecimalPrecision
    )
    ExtendDayListItem(
      currentExtendDay,
      settingsRepository.extendedDayList,
      settingsRepository::setExtendedDay
    )
    ShareApp()
  }
}
