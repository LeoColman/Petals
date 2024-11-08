package br.com.colman.petals.statistics.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.enable_or_disable_hour_of_day_line_in_stats
import br.com.colman.petals.R.string.hour_of_day_line_in_stats_enabled
import br.com.colman.petals.settings.view.dialog.SwitchListItem
import compose.icons.TablerIcons
import compose.icons.tablericons.ToggleLeft

@Preview
@Composable
fun HourOfDayLineInStatsEnabledListItem(
  hourOfDayLineInStatsEnabled: Boolean,
  setHourOfDayLineInStatsEnabled: (Boolean) -> Unit = {}
) {
  SwitchListItem(
    icon = TablerIcons.ToggleLeft,
    textId = hour_of_day_line_in_stats_enabled,
    descriptionId = enable_or_disable_hour_of_day_line_in_stats,
    initialState = hourOfDayLineInStatsEnabled,
    onChangeState = setHourOfDayLineInStatsEnabled
  )
}
