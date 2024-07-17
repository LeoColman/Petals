package br.com.colman.petals.statistics

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import br.com.colman.petals.statistics.card.AverageUseCard
import br.com.colman.petals.statistics.component.MultiPeriodSelect
import br.com.colman.petals.statistics.component.Period
import br.com.colman.petals.statistics.component.Period.Zero
import br.com.colman.petals.statistics.graph.UsePerDayOfWeekGraph
import br.com.colman.petals.statistics.graph.UsePerHourGraph
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository

@Composable
fun StatisticsPage(useRepository: UseRepository) {
  var selectedPeriods by remember { mutableStateOf(listOf<Period>(Zero)) }
  val uses by useRepository.all().collectAsState(emptyList())
  val usesInPeriod = selectedPeriods.associateWith(uses)

  Column(Modifier.verticalScroll(rememberScrollState()).testTag("StatisticsMainColumn")) {
    MultiPeriodSelect(selectedPeriods) { selectedPeriods = it }
    if (selectedPeriods.isEmpty()) return

    UsePerHourGraph(usesInPeriod)
    UsePerDayOfWeekGraph(usesInPeriod)
    Row(Modifier.horizontalScroll(rememberScrollState())) {
      usesInPeriod.forEach { (period, uses) ->
        AverageUseCard(uses, period.toDateRange())
      }
    }
  }
}

private fun List<Period>.associateWith(uses: List<Use>) = associateWith { period ->
  uses.filter { it.localDate in period.toDateRange() }
}.toSortedMap()
