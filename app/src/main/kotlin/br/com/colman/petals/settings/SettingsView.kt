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
import br.com.colman.petals.settings.view.listitem.MillisecondsBarEnabledListItem
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
  val currentMillisecondsEnabled by settingsRepository.millisecondsEnabled.collectAsState(true)
  val currentHitTimerMillisecondsEnabled by settingsRepository.hitTimerMillisecondsEnabled.collectAsState(true)
  val currentDecimalPrecision by settingsRepository.decimalPrecision.collectAsState(
    settingsRepository.decimalPrecisionList[2]
  )
  val currentExtendDay by settingsRepository.extendedDay.collectAsState(false)

  Column(Modifier.verticalScroll(rememberScrollState())) {
    CurrencyListItem(currentCurrency, settingsRepository::setCurrencyIcon)
    PinListItem(settingsRepository::setPin)
    RepositoryListItem()
    DateListItem(currentDateFormat, settingsRepository.dateFormatList, settingsRepository::setDateFormat)
    TimeListItem(currentTimeFormat, settingsRepository.timeFormatList, settingsRepository::setTimeFormat)
    PrecisionListItem(
      currentDecimalPrecision,
      settingsRepository.decimalPrecisionList,
      settingsRepository::setDecimalPrecision
    )
    MillisecondsBarEnabledListItem(
      currentMillisecondsEnabled,
      settingsRepository::setMillisecondsEnabled
    )
    HitTimerMillisecondsEnabledListItem(
      currentHitTimerMillisecondsEnabled,
      settingsRepository::setHitTimerMillisecondsEnabled
    )
    ExtendDayListItem(
      currentExtendDay,
      settingsRepository::setExtendedDay
    )
    ShareApp()
  }
}
