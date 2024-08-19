package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.time_format_label
import br.com.colman.petals.R.string.what_time_format_should_be_used
import br.com.colman.petals.settings.view.dialog.SelectFromListDialog
import compose.icons.TablerIcons
import compose.icons.tablericons.CalendarTime

@Preview
@Composable
fun TimeListItem(
  timeFormat: String = "",
  timeFormatList: List<String> = listOf(),
  setTimeFormat: (String) -> Unit = {}
) {
  DialogListItem(
    icon = TablerIcons.CalendarTime,
    textId = what_time_format_should_be_used,
    descriptionId = time_format_label,
    dialog = { hideDialog -> TimeDialog(timeFormat, timeFormatList, setTimeFormat, hideDialog) }
  )
}

@Preview
@Composable
private fun TimeDialog(
  initialTimeFormat: String = "",
  timeFormatList: List<String> = listOf(),
  setTimeFormat: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  SelectFromListDialog(
    initialValue = initialTimeFormat,
    possibleValues = timeFormatList,
    setValue = setTimeFormat,
    onDismiss = onDismiss,
    label = time_format_label
  )
}
