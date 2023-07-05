package br.com.colman.petals.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.com.colman.petals.statistics.card.AverageUseCard
import br.com.colman.petals.statistics.component.MultiPeriodSelect
import br.com.colman.petals.statistics.component.Period
import br.com.colman.petals.statistics.component.Period.Zero
import br.com.colman.petals.statistics.graph.UsePerHourGraph
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import java.time.LocalDate.now

@Composable
fun StatisticsPage(useRepository: UseRepository) {
  var selectedPeriods by remember { mutableStateOf(listOf<Period>(Zero)) }
  val uses by useRepository.all().collectAsState(emptyList())
  val usesInPeriod = selectedPeriods.associateWith(uses)

  Column(Modifier.verticalScroll(rememberScrollState())) {
    MultiPeriodSelect(selectedPeriods) { selectedPeriods = it }
    if (selectedPeriods.isEmpty()) return

    UsePerHourGraph(usesInPeriod)
    usesInPeriod.forEach { (period, uses) ->
      AverageUseCard(uses, period.toDateRange())
    }
  }
}

private fun List<Period>.associateWith(uses: List<Use>) = associateWith { period ->
  uses.filter { it.localDate >= now().minusDays(period.days?.toLong() ?: 0) }
}.toSortedMap()
