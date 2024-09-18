package br.com.colman.petals.statistics.graph.formatter

import br.com.colman.petals.koin
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val DaysSinceFirstUseFormatter = object : IAxisValueFormatter {
  val settingsRepository: SettingsRepository = koin.get<SettingsRepository>()
  val useRepository: UseRepository = koin.get<UseRepository>()

  private var dateFormat: String = runBlocking { settingsRepository.dateFormat.first() }
  private val formatter = DateTimeFormatter.ofPattern(dateFormat)

  private var uses: List<Use> = runBlocking { useRepository.all().first() }
  private val dayBeforeFirstUseDateToEpochDay: Long = uses.minBy { it.date }.localDate.toEpochDay().dec()

  override fun getFormattedValue(value: Float, axis: AxisBase?): String {
    val epochDay = (value + dayBeforeFirstUseDateToEpochDay).toLong()
    val localDate = LocalDate.ofEpochDay(epochDay)

    return formatter.format(localDate)
  }
}
