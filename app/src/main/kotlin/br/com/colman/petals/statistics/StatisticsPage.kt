package br.com.colman.petals.statistics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R
import br.com.colman.petals.R.string.*
import br.com.colman.petals.settings.SettingsRepository
import br.com.colman.petals.statistics.card.AverageUseCard
import br.com.colman.petals.statistics.graph.UsePerDayOfWeekGraph
import br.com.colman.petals.statistics.graph.UsePerHourGraph
import br.com.colman.petals.use.repository.UseRepository
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogScope
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import compose.icons.TablerIcons
import compose.icons.tablericons.Calendar
import me.moallemi.tools.daterange.localdate.LocalDateRange
import org.koin.androidx.compose.get
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.format.DateTimeFormatter

@Composable
fun MyDropdown(text: String, days: Int, onClick: (LocalDateRange) -> Unit) {
  val period = LocalDateRange(now().minusDays(days.toLong()), now())
  DropdownMenuItem(onClick = { onClick(period) }) {
    Text(text)
  }
}

@Composable
fun StatisticsPage(
  useRepository: UseRepository,
  settingsRepository: SettingsRepository
) {
  val dateFormat by settingsRepository.dateFormat.collectAsState(settingsRepository.dateFormatList[0])

  Column(Modifier.verticalScroll(rememberScrollState())) {
    val uses by useRepository.all().collectAsState(emptyList())

    var period by remember { mutableStateOf(LocalDateRange(now(), now())) }
    val usesInPeriod by remember { derivedStateOf { uses.filter { it.localDate in period } } }

    val startDateDialog = dateDialog {
      period = LocalDateRange(it, period.endInclusive)
    }

    val endDateDialog = dateDialog {
      period = LocalDateRange(period.start, it)
    }

    var expanded by remember { mutableStateOf(false) }

    Column {
      Button(onClick = { expanded = true }, Modifier.fillMaxWidth()) {
        Text(stringResource(change_period))
      }

      val setPeriod: (LocalDateRange) -> Unit = {
        period = it
        expanded = false
      }

      DropdownMenu(expanded, { expanded = false }) {
        MyDropdown(stringResource(today), 0, setPeriod)
        MyDropdown(stringResource(last_seven_days), 7, setPeriod)
        MyDropdown(stringResource(last_fourteen_days), 14, setPeriod)
        MyDropdown(stringResource(last_thirty_days), 30, setPeriod)
        MyDropdown(stringResource(last_sixty_days), 60, setPeriod)
        MyDropdown(stringResource(last_ninety_days), 90, setPeriod)
      }
    }

    Row(Modifier.padding(8.dp), spacedBy(8.dp)) {
      OutlinedTextField(
        modifier = Modifier
          .weight(0.5f)
          .clickable { startDateDialog.show() },
        value = period.start.format(DateTimeFormatter.ofPattern(dateFormat)),
        onValueChange = {},
        leadingIcon = { Icon(TablerIcons.Calendar, null) },
        enabled = false,
        label = { Text(stringResource(start_date)) }
      )

      OutlinedTextField(
        modifier = Modifier
          .weight(0.5f)
          .clickable { endDateDialog.show() },
        value = period.endInclusive.format(DateTimeFormatter.ofPattern(dateFormat)),
        onValueChange = {},
        leadingIcon = { Icon(TablerIcons.Calendar, null) },
        enabled = false,
        label = { Text(stringResource(R.string.end_date)) }
      )
    }

    UsePerHourGraph(usesInPeriod)
    UsePerDayOfWeekGraph(usesInPeriod)
    AverageUseCard(usesInPeriod, period)
  }
}

@Composable
private fun dateDialog(onDateChange: (LocalDate) -> Unit) = myMaterialDialog {
  datepicker(title = stringResource(R.string.select_date)) { date ->
    onDateChange(date)
  }
}

@Composable
private fun myMaterialDialog(content: @Composable MaterialDialogScope.() -> Unit) =
  rememberMaterialDialogState().also {
    MaterialDialog(
      dialogState = it,
      buttons = {
        positiveButton(stringResource(R.string.ok))
        negativeButton(stringResource(R.string.cancel))
      },
      content = content
    )
  }
