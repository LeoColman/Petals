package br.com.colman.petals.settings

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import br.com.colman.petals.settings.SettingsRepository.Companion.CurrencyIcon
import br.com.colman.petals.settings.SettingsRepository.Companion.DateFormat
import br.com.colman.petals.settings.SettingsRepository.Companion.ExtendedDayEnabled
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

  test("Defaults currency icon to $") {
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

  test("Default extended day to disabled") {
    target.extendedDay.first() shouldBe "disabled"
  }

  test("Changes extend day to enable") {
    target.setExtendedDay("enabled")
    target.extendedDay.first() shouldBe true
  }

  test("Persists extended day to enabled") {
    target.setExtendedDay("enable")
    datastore.data.first()[ExtendedDayEnabled] shouldBe "enabled"
  }
})
