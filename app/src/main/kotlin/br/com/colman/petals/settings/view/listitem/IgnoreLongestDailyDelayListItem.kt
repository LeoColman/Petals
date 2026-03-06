package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.ignore_longest_daily_delay
import br.com.colman.petals.R.string.ignore_longest_daily_delay_description
import br.com.colman.petals.settings.view.dialog.SwitchListItem
import compose.icons.TablerIcons
import compose.icons.tablericons.Moon

@Preview
@Composable
fun IgnoreLongestDailyDelayListItem(
  isIgnoringLongestDailyDelay: Boolean,
  setIgnoreLongestDailyDelay: (Boolean) -> Unit = {}
) {
  SwitchListItem(
    icon = TablerIcons.Moon,
    textId = ignore_longest_daily_delay,
    descriptionId = ignore_longest_daily_delay_description,
    initialState = isIgnoringLongestDailyDelay,
    onChangeState = setIgnoreLongestDailyDelay
  )
}
