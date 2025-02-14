package br.com.colman.petals.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import br.com.colman.petals.settings.view.listitem.ClockListItem
import br.com.colman.petals.settings.view.listitem.CurrencyListItem
import br.com.colman.petals.settings.view.listitem.DateListItem
import br.com.colman.petals.settings.view.listitem.ExtendDayListItem
import br.com.colman.petals.settings.view.listitem.HitTimerMillisecondsEnabledListItem
import br.com.colman.petals.settings.view.listitem.LanguageListItem
import br.com.colman.petals.settings.view.listitem.PinListItem
import br.com.colman.petals.settings.view.listitem.PrecisionListItem
import br.com.colman.petals.settings.view.listitem.RepositoryListItem
import br.com.colman.petals.settings.view.listitem.ShareApp
import br.com.colman.petals.settings.view.listitem.TimeListItem
import br.com.colman.petals.statistics.view.listitem.HourOfDayLineInStatsEnabledListItem

@Composable
fun SettingsView(settingsRepository: SettingsRepository) {
  val currentCurrency by settingsRepository.currencyIcon.collectAsState("$")
  val currentDateFormat by settingsRepository.dateFormat.collectAsState(settingsRepository.dateFormatList[0])
  val currentTimeFormat by settingsRepository.timeFormat.collectAsState(settingsRepository.timeFormatList[0])
  val is24HoursFormat by settingsRepository.is24HoursFormat.collectAsState(false)
  val currentHitTimerMillisecondsEnabled by settingsRepository.isHitTimerMillisecondsEnabled.collectAsState(true)
  val currentDecimalPrecision by settingsRepository.decimalPrecision.collectAsState(
    settingsRepository.decimalPrecisionList[2]
  )
  val currentExtendDay by settingsRepository.isDayExtended.collectAsState(false)

  val setAppLanguage = { language: String ->
    val languageCode = AppLanguage.getAppLanguageCode(language)
    if (languageCode.isNotEmpty()) {
      val appLocale = LocaleListCompat.forLanguageTags(languageCode)
      AppCompatDelegate.setApplicationLocales(appLocale)
    }
  }

  val currentHourOfDayInLineStatsEnabled by settingsRepository.isHourOfDayLineInStatsEnabled.collectAsState(true)

  Column(Modifier.verticalScroll(rememberScrollState())) {
    CurrencyListItem(currentCurrency, settingsRepository::setCurrencyIcon)
    PinListItem(settingsRepository::setPin)
    RepositoryListItem()
    DateListItem(currentDateFormat, settingsRepository.dateFormatList, settingsRepository::setDateFormat)
    TimeListItem(currentTimeFormat, settingsRepository.timeFormatList, settingsRepository::setTimeFormat)
    ClockListItem(
      is24HoursFormat,
      settingsRepository.clockFormatList.map {
        stringResource(it)
      },
      settingsRepository::setIs24HoursFormat
    )
    PrecisionListItem(
      currentDecimalPrecision,
      settingsRepository.decimalPrecisionList,
      settingsRepository::setDecimalPrecision
    )
    LanguageListItem(
      AppLanguage.getAppLanguageName(AppCompatDelegate.getApplicationLocales().toLanguageTags()),
      settingsRepository.appLanguages,
      setAppLanguage
    )
    HitTimerMillisecondsEnabledListItem(
      currentHitTimerMillisecondsEnabled,
      settingsRepository::setIsHitTimerMillisecondsEnabled
    )
    ExtendDayListItem(
      currentExtendDay,
      settingsRepository::setDayExtended
    )
    HourOfDayLineInStatsEnabledListItem(
      currentHourOfDayInLineStatsEnabled,
      settingsRepository::setIsHourOfDayLineInStatsEnabled
    )
    ShareApp()
  }
}
