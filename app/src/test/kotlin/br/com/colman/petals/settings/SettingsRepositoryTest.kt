package br.com.colman.petals.settings

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import br.com.colman.petals.settings.SettingsRepository.Companion.CurrencyIcon
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
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
    datastore.data.first().get(CurrencyIcon) shouldBe "R$"
  }

  test("Validate date formats") {
    val flow = target.dateFormatList
    flow.forEach {
      DateTimeFormatter.ofPattern(it)
    }
    assert(true)
  }
})
