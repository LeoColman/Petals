package br.com.colman.petals.statistics.graph

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string.break_period
import br.com.colman.petals.R.string.grams_distribution_per_hour_of_day
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.statistics.component.Period
import br.com.colman.petals.statistics.graph.component.PeriodLineChart
import br.com.colman.petals.statistics.graph.data.breakPeriodReferenceLines
import br.com.colman.petals.statistics.graph.data.createBreakPeriodBands
import br.com.colman.petals.statistics.graph.data.createDistributionPerHourSeries
import br.com.colman.petals.statistics.graph.formatter.TwelveHourFormatter
import br.com.colman.petals.statistics.graph.formatter.gramsAxisFormatter
import br.com.colman.petals.use.pause.repository.PauseRepository
import br.com.colman.petals.use.repository.Use
import io.grafima.charts.line.LineCurveType
import io.grafima.charts.line.ReferenceLine
import io.grafima.charts.line.ReferenceLineAxis
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

  val description = stringResource(grams_distribution_per_hour_of_day)
  val breakLabel = stringResource(break_period)
  val colors = MaterialTheme.colors
  val gramsData = useGroups.map { (period, uses) ->
    val label = period.label()
    createDistributionPerHourSeries(period.days, uses, label)
  }

  val yMax = gramsData.flatMap { it.points }.maxOfOrNull { it.y } ?: 0f
  val showBreaks = breakPeriodInStatsEnabled && yMax > 0f
  val breakBands = if (showBreaks) createBreakPeriodBands(pauses, yMax, breakLabel) else emptyList()

  // Read on every composition rather than once per process: the hour rule is "now", and the old
  // top-level version kept pointing at whatever hour the app happened to start in.
  val referenceLines = buildList {
    if (currentHourOfDayLineInStatsEnabled) {
      add(
        ReferenceLine(
          value = LocalTime.now().hour.toFloat(),
          axis = ReferenceLineAxis.X,
          color = colors.primary,
          strokeWidth = 2.dp
        )
      )
    }
    if (showBreaks) addAll(breakPeriodReferenceLines(pauses))
  }

  PeriodLineChart(
    // Readings first: the chart takes its x labels from the first series, and a break band only has
    // a point at each of its own edges. The bands are translucent, so drawing them last is fine.
    series = gramsData + breakBands,
    contentDescription = description,
    xMin = 0f,
    xMax = 23f,
    maxXLabels = 24,
    xLabelFormatter = TwelveHourFormatter,
    yLabelFormatter = gramsAxisFormatter(yMax),
    referenceLines = referenceLines,
    // The old chart used HORIZONTAL_BEZIER here. Hours are a cycle, and the smoothing says so.
    curveType = LineCurveType.MonotoneCubic
  )
}
