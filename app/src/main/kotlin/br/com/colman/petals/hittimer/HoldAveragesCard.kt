package br.com.colman.petals.hittimer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.R.plurals.amount_hits
import br.com.colman.petals.R.plurals.last_x_days
import br.com.colman.petals.R.string.all_time
import br.com.colman.petals.R.string.average_hold
import br.com.colman.petals.R.string.average_hold_explanation
import br.com.colman.petals.R.string.seconds_short
import br.com.colman.petals.hittimer.HoldWindow.AllTime
import java.time.Duration
import java.util.Locale

/** Nothing recorded yet means nothing to say, so the card stays away until there is a hit. */
@Composable
fun HoldAveragesCard(averages: List<HoldAverage>) {
  if (averages.none { it.count > 0 }) return

  Card(Modifier.fillMaxWidth().padding(16.dp)) {
    Column(Modifier.fillMaxWidth().padding(16.dp), Arrangement.spacedBy(12.dp)) {
      Text(stringResource(average_hold), fontSize = 24.sp)
      Text(stringResource(average_hold_explanation), fontSize = 14.sp)

      averages.forEach { HoldAverageRow(it) }
    }
  }
}

@Composable
private fun HoldAverageRow(average: HoldAverage) {
  Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
    Text(average.window.label(), fontSize = 16.sp)
    Text(
      average.average?.let { stringResource(seconds_short, it.asSecondsText()) } ?: "-",
      fontSize = 16.sp,
      color = MaterialTheme.colors.primary
    )
    Text(pluralStringResource(amount_hits, average.count, average.count), fontSize = 16.sp)
  }
}

@Composable
private fun HoldWindow.label(): String = when (val windowDays = days) {
  null -> stringResource(all_time)
  else -> pluralStringResource(last_x_days, windowDays.toInt(), windowDays)
}

private fun Duration.asSecondsText() = "%.1f".format(Locale.US, toMillis() / 1000.0)

@Preview
@Composable
private fun HoldAveragesCardPreview() {
  HoldAveragesCard(
    listOf(
      HoldAverage(AllTime, Duration.ofMillis(12_400), 37),
      HoldAverage(HoldWindow.Last30Days, Duration.ofMillis(11_100), 21),
      HoldAverage(HoldWindow.Last7Days, Duration.ofMillis(9_800), 6)
    )
  )
}
