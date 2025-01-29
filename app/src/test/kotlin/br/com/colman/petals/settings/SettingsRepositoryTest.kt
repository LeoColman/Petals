package br.com.colman.petals.settings

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import br.com.colman.petals.settings.SettingsRepository.Companion.CurrencyIcon
import br.com.colman.petals.settings.SettingsRepository.Companion.DateFormat
import br.com.colman.petals.settings.SettingsRepository.Companion.DecimalPrecision
import br.com.colman.petals.settings.SettingsRepository.Companion.Is24HoursFormat
import br.com.colman.petals.settings.SettingsRepository.Companion.IsDarkModeOn
import br.com.colman.petals.settings.SettingsRepository.Companion.IsDayExtended
import br.com.colman.petals.settings.SettingsRepository.Companion.IsHitTimerMillisecondsEnabled
import br.com.colman.petals.settings.SettingsRepository.Companion.IsHourOfDayLineInStatsEnabled
import br.com.colman.petals.settings.SettingsRepository.Companion.Pin
import br.com.colman.petals.settings.SettingsRepository.Companion.TimeFormat
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import java.time.format.DateTimeFormatter

class SettingsRepositoryTest : FunSpec({
  val datastore = PreferenceDataStoreFactory.create { tempfile(suffix = ".preferences_pb") }
  val target = SettingsRepository(datastore)

  test("Defaults currency icon to \$") {
    target.currencyIcon.first() shouldBe "$"
  }

  test("Changes the currency to the specified one") {
    target.setCurrencyIcon("R$")
    target.currencyIcon.first() shouldBe "R$"
  }

  test("Persists specified currency to permanent storage") {
    target.setCurrencyIcon("R$")
    datastore.data.first()[CurrencyIcon] shouldBe "R$"
  }

  test("Validate date formats") {
    val dateFormatList = target.dateFormatList
    shouldNotThrowAny {
      dateFormatList.forEach { DateTimeFormatter.ofPattern(it) }
    }
  }

  test("Validate time formats") {
    val timeFormatList = target.timeFormatList
    shouldNotThrowAny {
      timeFormatList.forEach { DateTimeFormatter.ofPattern(it) }
    }
  }

  test("Defaults date format to yyyy-MM-dd") {
    target.dateFormat.first() shouldBe "yyyy-MM-dd"
  }

  test("Changes date format to the specified one") {
    target.setDateFormat("yyyy/MM/dd")
    target.dateFormat.first() shouldBe "yyyy/MM/dd"
  }

  test("Persists specified date format to permanent storage") {
    target.setDateFormat("yyyy/MM/dd")
    datastore.data.first()[DateFormat] shouldBe "yyyy/MM/dd"
  }

  test("Defaults time format to HH:mm") {
    target.timeFormat.first() shouldBe "HH:mm"
  }

  test("Changes time format to the specified one") {
    target.setTimeFormat("HH:mm")
    target.timeFormat.first() shouldBe "HH:mm"
  }

  test("Persists specified time format to permanent storage") {
    target.setTimeFormat("HH:mm")
    datastore.data.first()[TimeFormat] shouldBe "HH:mm"
  }

  test("Default extended day to false") {
    target.isDayExtended.first() shouldBe false
  }

  test("Changes extend day to false") {
    target.setDayExtended(false)
    target.isDayExtended.first() shouldBe false
  }

  test("Persists extended day to true") {
    target.setDayExtended(true)
    target.isDayExtended.first() shouldBe true
  }

  test("Changes dark mode to true") {
    target.setDarkMode(true)
    target.isDarkModeEnabled.first() shouldBe true
  }

  test("Changes dark mode to false") {
    target.setDarkMode(false)
    target.isDarkModeEnabled.first() shouldBe false
  }

  test("Defaults 24 hours format to false") {
    target.is24HoursFormat.first() shouldBe false
  }

  test("Changes 24 hours format to true") {
    target.setIs24HoursFormat(true)
    target.is24HoursFormat.first() shouldBe true
  }

  test("Persists 24 hours format to permanent storage") {
    target.setIs24HoursFormat(true)
    datastore.data.first()[Is24HoursFormat] shouldBe true
  }

  test("Defaults decimal precision to 2") {
    target.decimalPrecision.first() shouldBe 2
  }

  test("Changes decimal precision to 0") {
    target.setDecimalPrecision(0)
    target.decimalPrecision.first() shouldBe 0
  }

  test("Persists decimal precision to storage") {
    target.setDecimalPrecision(3)
    datastore.data.first()[DecimalPrecision] shouldBe 3
  }

  test("decimalPrecisionList should be [0,1,2,3]") {
    target.decimalPrecisionList shouldBe listOf(0, 1, 2, 3)
  }

  test("Defaults hit timer milliseconds to false") {
    target.isHitTimerMillisecondsEnabled.first() shouldBe false
  }

  test("Changes hit timer milliseconds to true") {
    target.setIsHitTimerMillisecondsEnabled(true)
    target.isHitTimerMillisecondsEnabled.first() shouldBe true
  }

  test("Persists hit timer milliseconds setting") {
    target.setIsHitTimerMillisecondsEnabled(true)
    datastore.data.first()[IsHitTimerMillisecondsEnabled] shouldBe true
  }

  test("Defaults hour of day line in stats to false") {
    target.isHourOfDayLineInStatsEnabled.first() shouldBe false
  }

  test("Changes hour of day line in stats to true") {
    target.setIsHourOfDayLineInStatsEnabled(true)
    target.isHourOfDayLineInStatsEnabled.first() shouldBe true
  }

  test("Persists hour of day line setting") {
    target.setIsHourOfDayLineInStatsEnabled(true)
    datastore.data.first()[IsHourOfDayLineInStatsEnabled] shouldBe true
  }

  test("Pin defaults to null") {
    target.pin.first() shouldBe null
  }

  test("Set pin to a value") {
    target.setPin("1234")
    target.pin.first() shouldBe "1234"
  }

  test("Clear pin by setting to null") {
    target.setPin(null)
    target.pin.first() shouldBe null
  }

  test("Persists pin to storage") {
    target.setPin("5678")
    datastore.data.first()[Pin] shouldBe "5678"
  }

  test("Removing pin deletes it from storage") {
    target.setPin("1234")
    target.setPin(null)
    datastore.data.first()[Pin] shouldBe null
  }

  test("Clock format list has two elements") {
    target.clockFormatList.size shouldBe 2
  }

  test("appLanguages is not empty") {
    target.appLanguages.isNotEmpty() shouldBe true
  }

  test("Persists dark mode setting") {
    target.setDarkMode(false)
    datastore.data.first()[IsDarkModeOn] shouldBe false
  }

  test("Persists extended day setting to storage") {
    target.setDayExtended(true)
    datastore.data.first()[IsDayExtended] shouldBe true
  }
})
