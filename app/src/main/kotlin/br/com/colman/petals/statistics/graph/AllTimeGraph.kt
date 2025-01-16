package br.com.colman.petals.statistics.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.string.grams_distribution_over_days_since_first_use
import br.com.colman.petals.statistics.graph.component.LineChart
import br.com.colman.petals.statistics.graph.data.createAllTimeDistributionWithMovingAverage
import br.com.colman.petals.statistics.graph.formatter.DaysSinceFirstUseFormatter
import br.com.colman.petals.use.repository.Use
import com.github.mikephil.charting.components.LimitLine
import java.time.YearMonth

@Composable
fun AllTimeGraph(uses: List<Use>, dateFormat: String) {
  val description = stringResource(grams_distribution_over_days_since_first_use)
  val gramsData = createAllTimeDistributionWithMovingAverage(uses)
  val gramsDataList = listOf(gramsData)

  LineChart(gramsDataList.flatten(), description, 5f) {
    axisMinimum = 1f
    labelCount = 5
    granularity = 1f
    valueFormatter = DaysSinceFirstUseFormatter(uses, dateFormat).formatDate

  }
}
