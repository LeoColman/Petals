package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.clock_format_label
import br.com.colman.petals.R.string.what_clock_format_should_be_used
import br.com.colman.petals.settings.view.dialog.SelectFromListDialog
import compose.icons.TablerIcons
import compose.icons.tablericons.Clock

@Preview
@Composable
fun ClockListItem(
  clockFormat: String = "",
  clockFormatList: List<String> = listOf(),
  setClockFormat: (String) -> Unit = {}
) {
  DialogListItem(
    icon = TablerIcons.Clock,
    textId = clock_format_label,
    descriptionId = what_clock_format_should_be_used,
    dialog = { hideDialog -> ClockDialog(clockFormat, clockFormatList, setClockFormat, hideDialog) }
  )
}

@Preview
@Composable
private fun ClockDialog(
  initialClockFormat: String = "",
  clockFormatList: List<String> = listOf(),
  setClockFormat: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  SelectFromListDialog(
    initialValue = initialClockFormat,
    possibleValues = clockFormatList,
    setValue = setClockFormat,
    onDismiss = onDismiss,
    label = clock_format_label
  )
}
