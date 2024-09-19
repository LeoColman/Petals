package br.com.colman.petals.statistics.graph

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.string
import br.com.colman.petals.statistics.graph.component.LineChart
import br.com.colman.petals.statistics.graph.data.createAllTimeDistribution
import br.com.colman.petals.statistics.graph.formatter.DaysSinceFirstUseFormatter
import br.com.colman.petals.use.repository.Use
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.LineDataSet
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun AllTimeGraph(uses: List<Use>, dateFormat: String) {
  val description = stringResource(string.grams_distribution_over_days_since_first_use)
  val gramsData = createAllTimeDistribution(uses)
  val gramsDataList = mutableListOf<LineDataSet>()
  gramsDataList.add(gramsData)

  LineChart(gramsDataList, description, 5f) {
    axisMinimum = 1f
    labelCount = 5
    granularity = 1f
    valueFormatter = DaysSinceFirstUseFormatter(uses, dateFormat).formatDate
    addLimitLine(LimitLine(YearMonth.from(LocalDate.now()).monthValue.toFloat()).apply { lineWidth = 2f })
  }
}
