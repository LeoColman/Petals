package br.com.colman.petals.statistics.graph

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string.break_period
import br.com.colman.petals.R.string.grams_distribution_per_hour_of_day
import br.com.colman.petals.R.string.time_of_day_resolution_hour
import br.com.colman.petals.R.string.time_of_day_resolution_quarter_hour
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.statistics.component.Period
import br.com.colman.petals.statistics.graph.component.LineChart
import br.com.colman.petals.statistics.graph.data.TimeOfDayResolution
import br.com.colman.petals.statistics.graph.data.breakPeriodLimitLines
import br.com.colman.petals.statistics.graph.data.createBreakPeriodBands
import br.com.colman.petals.statistics.graph.data.createDistributionPerHourDataset
import br.com.colman.petals.statistics.graph.formatter.TwelveHourFormatter
import br.com.colman.petals.use.pause.repository.PauseRepository
import br.com.colman.petals.use.repository.Use
import com.github.mikephil.charting.components.LimitLine
import org.koin.compose.koinInject
import java.time.LocalDate
import java.time.LocalTime

@Composable
@Preview
fun UsePerHourGraphPreview() {
  val hoursInDay = (0..23).toList()
  val minutesInHour = (0..59).toList()
  val uses = List(293) {
    Use(
      LocalDate.now().atTime(hoursInDay.random(), minutesInHour.random()),
      "3.37".toBigDecimal(),
      (it % 4).toBigDecimal()
    )
  }

  UsePerHourGraph(mapOf(Period.TwoWeek to uses))
}

@Composable
@Preview
fun UsePerHourGraphPreview2() {
  val hoursInDay = (0..23).toList()
  val minutesInHour = (0..59).toList()
  val uses = List(293) {
    Use(
      LocalDate.now().minusDays(hoursInDay.random().toLong()).atTime(hoursInDay.random(), minutesInHour.random()),
      "3.37".toBigDecimal(),
      (it % 4).toBigDecimal()
    )
  }
  val uses2 = List(29) {
    Use(
      LocalDate.now().atTime(hoursInDay.random(), minutesInHour.random()),
      "3.37".toBigDecimal(),
      (it % 4).toBigDecimal()
    )
  }

  UsePerHourGraph(mapOf(Period.Week to uses, Period.TwoWeek to uses2, Period.Month to uses + uses2))
}

@Composable
fun UsePerHourGraph(useGroups: Map<Period, List<Use>>) {
  val settingsRepository = koinInject<SettingsRepository>()
  val pauseRepository = koinInject<PauseRepository>()
  val currentHourOfDayLineInStatsEnabled by settingsRepository.isHourOfDayLineInStatsEnabled.collectAsState(false)
  val breakPeriodInStatsEnabled by settingsRepository.isBreakPeriodInStatsEnabled.collectAsState(true)
  val pauses by pauseRepository.getAll().collectAsState(emptyList())
  // Nothing until the saved choice is in. Drawn by the hour in the meantime, the chart would redraw and
  // the switch slide across on every visit from someone who keeps it at 15 minutes.
  val savedQuarterHour: Boolean? by settingsRepository.isQuarterHourInStatsEnabled.collectAsState(null)
  val isQuarterHour = savedQuarterHour ?: return
  val resolution = TimeOfDayResolution.of(isQuarterHour)

  val description = stringResource(grams_distribution_per_hour_of_day)
  val breakLabel = stringResource(break_period)
  val gramsData = useGroups.map { (period, uses) ->
    val label = period.label()
    createDistributionPerHourDataset(period.days, uses, label, resolution)
  }

  val yMax = gramsData.maxOfOrNull { it.yMax } ?: 0f
  val showBreaks = breakPeriodInStatsEnabled && yMax > 0f
  val axisEnd = resolution.lastHour
  val breakBands = if (showBreaks) createBreakPeriodBands(pauses, yMax, breakLabel, axisEnd) else emptyList()
  val datasets = breakBands + gramsData

  Column(Modifier.fillMaxWidth()) {
    ResolutionSwitch(isQuarterHour, settingsRepository::setIsQuarterHourInStatsEnabled)

    LineChart(datasets, description) {
      axisMinimum = 0f
      // Where the last bucket starts: 23 by the hour, 23.75 by the quarter. The labels and grid lines
      // stay on the whole hours either way, as the axis spaces them by granularity, not by the entries.
      axisMaximum = axisEnd
      labelCount = 24
      granularity = 1f
      valueFormatter = TwelveHourFormatter
      removeAllLimitLines()
      if (currentHourOfDayLineInStatsEnabled) {
        addLimitLine(hourLimitLine)
      }
      if (showBreaks) {
        breakPeriodLimitLines(pauses).forEach { addLimitLine(it) }
      }
    }
  }
}

private val hourLimitLine = LimitLine(LocalTime.now().hour.toFloat()).apply { lineWidth = 2f }

/**
 * Named on both sides, so it reads as a choice between one point per hour and one every fifteen
 * minutes rather than as fifteen minutes being on or off. For the same reason both positions share
 * one set of colours, and the side in use is the one in bold and in the primary colour.
 *
 * A screen reader gets the two sides as a pair of radio buttons, which say which one is in use. The
 * switch between them is left out, as it would only repeat them with a less telling "on" or "off".
 */
@Composable
private fun ResolutionSwitch(isQuarterHour: Boolean, onChange: (isQuarterHour: Boolean) -> Unit) {
  val switchColor = MaterialTheme.colors.secondaryVariant

  Row(
    Modifier.fillMaxWidth().selectableGroup(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    ResolutionLabel(stringResource(time_of_day_resolution_hour), selected = !isQuarterHour) { onChange(false) }
    Switch(
      checked = isQuarterHour,
      onCheckedChange = onChange,
      modifier = Modifier.clearAndSetSemantics { },
      colors = SwitchDefaults.colors(
        checkedThumbColor = switchColor,
        checkedTrackColor = switchColor,
        uncheckedThumbColor = switchColor,
        uncheckedTrackColor = switchColor,
        uncheckedTrackAlpha = SwitchTrackAlpha
      )
    )
    ResolutionLabel(stringResource(time_of_day_resolution_quarter_hour), selected = isQuarterHour) { onChange(true) }
  }
}

// What the switch gives a checked track by default, now given to both positions.
private const val SwitchTrackAlpha = 0.54f

@Composable
private fun ResolutionLabel(text: String, selected: Boolean, onClick: () -> Unit) {
  val colors = MaterialTheme.colors
  Text(
    text,
    Modifier
      .selectable(selected, onClick = onClick, role = Role.RadioButton)
      .minimumInteractiveComponentSize()
      .padding(horizontal = 8.dp),
    color = if (selected) colors.primary else colors.onSurface.copy(alpha = ContentAlpha.medium),
    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
    style = MaterialTheme.typography.body2
  )
}
