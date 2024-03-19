package br.com.colman.petals.statistics.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.grams_distribution_per_hour_of_day
import br.com.colman.petals.statistics.component.Period
import br.com.colman.petals.statistics.graph.component.LineChart
import br.com.colman.petals.statistics.graph.data.createDistributionPerHourDataset
import br.com.colman.petals.statistics.graph.formatter.TwelveHourFormatter
import br.com.colman.petals.use.repository.Use
import com.github.mikephil.charting.components.LimitLine
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
  val description = stringResource(grams_distribution_per_hour_of_day)
  val gramsData = useGroups.map { (period, uses) ->
    val label = period.label()
    createDistributionPerHourDataset(period.days, uses, label)
  }

  LineChart(gramsData, description) {
    axisMinimum = 0f
    axisMaximum = 23f
    labelCount = 24
    granularity = 1f
    valueFormatter = TwelveHourFormatter
    addLimitLine(LimitLine(LocalTime.now().hour.toFloat()).apply { lineWidth = 2f })
  }
}
