package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.enable_or_disable_milliseconds_bar_on_home_page
import br.com.colman.petals.R.string.milliseconds_enabled
import br.com.colman.petals.settings.view.dialog.SelectFromListDialog
import br.com.colman.petals.settings.view.dialog.SwitchListItem
import compose.icons.TablerIcons
import compose.icons.tablericons.CircleOff

@Preview
@Composable
fun MillisecondsBarEnabledListItem(
  millisEnabled: Boolean,
  setMillisEnabled: (Boolean) -> Unit = {}
) {
  SwitchListItem(
    icon = TablerIcons.CircleOff,
    textId = milliseconds_enabled,
    descriptionId = enable_or_disable_milliseconds_bar_on_home_page,
    millisEnabled,
    setMillisEnabled
  )
}
