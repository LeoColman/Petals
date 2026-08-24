package br.com.colman.petals.statistics.graph

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.statistics.component.Period
import br.com.colman.petals.statistics.graph.component.PeriodLineChart
import br.com.colman.petals.statistics.graph.data.createDistributionPerDayOfWeekSeries
import br.com.colman.petals.statistics.graph.formatter.DayOfWeekFormatter
import br.com.colman.petals.statistics.graph.formatter.GramsLabel
import br.com.colman.petals.statistics.graph.formatter.gramsAxisFormatter
import br.com.colman.petals.use.repository.Use
import io.grafima.charts.line.LineValueLabelConfig
import io.grafima.charts.line.ReferenceLine
import io.grafima.charts.line.ReferenceLineAxis
import org.koin.compose.koinInject
import java.time.LocalDate
import kotlin.random.Random

@Composable
@Preview
fun UsePerDayOfWeekGraphPreview() {
  val uses = List(293) {
    Use(
      LocalDate.now().minusDays(Random.nextLong(0, 7)).atStartOfDay(),
      "3.37".toBigDecimal(),
      (it % 4).toBigDecimal()
    )
  }

  UsePerDayOfWeekGraph(mapOf(Period.TwoWeek to uses))
}

@Composable
@Preview
fun UsePerDayOfWeekGraphPreview2() {
  val uses = List(293) {
    Use(
      LocalDate.now().minusDays(Random.nextLong(0, 7)).atStartOfDay(),
      "3.37".toBigDecimal(),
      (it % 4).toBigDecimal()
    )
  }
  val uses2 = List(29) {
    Use(
      LocalDate.now().minusDays(Random.nextLong(0, 7)).atStartOfDay(),
      "3.37".toBigDecimal(),
      (it % 4).toBigDecimal()
    )
  }

  val uses3 = List(100) {
    Use(
      LocalDate.now().minusDays(Random.nextLong(0, 10)).atStartOfDay(),
      "3.37".toBigDecimal(),
      (it % 4).toBigDecimal()
    )
  }

  UsePerDayOfWeekGraph(
    mapOf(
      Period.Week to uses,
      Period.TwoWeek to uses2,
      Period.Month to uses + uses2,
      Period.TwoMonth to uses + uses2 + uses3
    )
  )
}

@Composable
@Preview
fun UsePerDayOfWeekGraphPreview3() {
  val uses = List(2930) {
    Use(
      LocalDate.now().minusDays(Random.nextLong(0, 30)).atStartOfDay(),
      "3.37".toBigDecimal(),
      1.toBigDecimal()
    )
  }

  UsePerDayOfWeekGraph(mapOf(Period.Month to uses))
}

@Composable
@Preview
fun UsePerDayOfWeekGraphPreview4() {
  val uses = List(2930) {
    Use(
      LocalDate.now().minusDays(0).atStartOfDay(),
      "3.37".toBigDecimal(),
      1.toBigDecimal()
    )
  }

  UsePerDayOfWeekGraph(mapOf(Period.Zero to uses))
}

@Composable
fun UsePerDayOfWeekGraph(useGroups: Map<Period, List<Use>>) {
  val settingsRepository = koinInject<SettingsRepository>()
  val currentHourOfDayLineInStatsEnabled by settingsRepository.isHourOfDayLineInStatsEnabled.collectAsState(false)

  val description = stringResource(string.grams_distribution_per_day_of_week)
  val colors = MaterialTheme.colors
  val gramsData = useGroups.map { (period, uses) ->
    val daysExceedingWeek = period.days % 7
    val weekPeriod = period.minusDays(daysExceedingWeek)
    val weekUses = if (weekPeriod == Period.Zero) {
      uses
    } else {
      uses.filter {
        it.localDate > LocalDate.now().minusDays(weekPeriod.days.toLong())
      }
    }

    val label = weekPeriod.label()
    createDistributionPerDayOfWeekSeries(weekPeriod.days, weekUses, label)
  }

  val yMax = gramsData.flatMap { it.points }.maxOfOrNull { it.y } ?: 0f

  // Today's weekday, read per composition rather than once per process, so the rule still points at
  // today after the app has been left open overnight.
  val referenceLines = if (currentHourOfDayLineInStatsEnabled) {
    listOf(
      ReferenceLine(
        value = LocalDate.now().dayOfWeek.value.toFloat(),
        axis = ReferenceLineAxis.X,
        color = colors.primary,
        strokeWidth = 2.dp
      )
    )
  } else {
    emptyList()
  }

  PeriodLineChart(
    series = gramsData,
    contentDescription = description,
    xMin = 1f,
    xMax = 7f,
    maxXLabels = 7,
    xLabelFormatter = DayOfWeekFormatter,
    yLabelFormatter = gramsAxisFormatter(yMax),
    referenceLines = referenceLines,
    // Each period's own colour, rather than one colour for every label as before: with four periods
    // overlapping, a number told you what but not whose.
    valueLabels = LineValueLabelConfig(
      enabled = true,
      formatter = { _, point -> GramsLabel(point.y) },
      useSeriesColor = true
    )
  )
}
