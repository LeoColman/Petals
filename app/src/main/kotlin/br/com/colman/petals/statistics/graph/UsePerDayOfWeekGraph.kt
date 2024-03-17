package br.com.colman.petals.statistics.graph

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R
import br.com.colman.petals.R.string
import br.com.colman.petals.statistics.component.Period
import br.com.colman.petals.statistics.graph.component.LineChart
import br.com.colman.petals.statistics.graph.data.createDistributionPerDayOfWeekDataset
import br.com.colman.petals.statistics.graph.formatter.DayOfWeekFormatter
import br.com.colman.petals.use.repository.Use
import com.github.mikephil.charting.components.LimitLine
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

  UsePerDayOfWeekGraph(mapOf(Period.Week to uses, Period.TwoWeek to uses2, Period.Month to uses + uses2))
}

@Composable
fun UsePerDayOfWeekGraph(useGroups: Map<Period, List<Use>>) {
  val description = stringResource(string.grams_distribution_per_day_of_week)
  val colors = MaterialTheme.colors
  val gramsData = useGroups.map { (period, uses) ->
    val label = pluralStringResource(R.plurals.last_x_days, period.days, period.days)
    createDistributionPerDayOfWeekDataset(period.days, uses, label, colors)
  }

  LineChart(gramsData, description) {
    axisMinimum = 1f
    axisMaximum = 7f
    labelCount = 7
    granularity = 1f
    valueFormatter = DayOfWeekFormatter
    addLimitLine(LimitLine(LocalDate.now().dayOfWeek.value.toFloat()).apply { lineWidth = 2f })
  }
}
