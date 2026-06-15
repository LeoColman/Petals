package br.com.colman.petals.statistics.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.break_period_in_stats_enabled
import br.com.colman.petals.R.string.enable_or_disable_break_period_in_stats
import br.com.colman.petals.settings.view.dialog.SwitchListItem
import compose.icons.TablerIcons
import compose.icons.tablericons.PlayerPause

@Preview
@Composable
fun BreakPeriodInStatsEnabledListItem(
  breakPeriodInStatsEnabled: Boolean = true,
  setBreakPeriodInStatsEnabled: (Boolean) -> Unit = {}
) {
  SwitchListItem(
    icon = TablerIcons.PlayerPause,
    textId = break_period_in_stats_enabled,
    descriptionId = enable_or_disable_break_period_in_stats,
    initialState = breakPeriodInStatsEnabled,
    onChangeState = setBreakPeriodInStatsEnabled
  )
}
