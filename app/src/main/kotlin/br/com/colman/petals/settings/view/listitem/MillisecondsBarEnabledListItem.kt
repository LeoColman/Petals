package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.enable_or_disable_milliseconds_bar_on_home_page
import br.com.colman.petals.R.string.milliseconds_enabled
import br.com.colman.petals.settings.view.dialog.SelectFromListDialog
import compose.icons.TablerIcons
import compose.icons.tablericons.CircleOff

@Preview
@Composable
fun MillisecondsBarEnabledListItem(
  millisEnabled: String = "",
  millisOptions: List<String> = listOf(),
  setMillisEnabled: (String) -> Unit = {}
) {
  DialogListItem(
    icon = TablerIcons.CircleOff,
    textId = milliseconds_enabled,
    descriptionId = enable_or_disable_milliseconds_bar_on_home_page,
    dialog = { hideDialog -> MillisecondsEnabledDialog(millisEnabled, millisOptions, setMillisEnabled, hideDialog) }
  )
}

@Preview
@Composable
private fun MillisecondsEnabledDialog(
  initialMillisecondsEnabled: String = "",
  millisecondsEnabledList: List<String> = listOf(),
  setMillisecondsEnabled: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  SelectFromListDialog(
    initialValue = initialMillisecondsEnabled,
    possibleValues = millisecondsEnabledList,
    setValue = setMillisecondsEnabled,
    onDismiss = onDismiss,
    label = milliseconds_enabled
  )
}
