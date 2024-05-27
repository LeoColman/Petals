package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.date_format_label
import br.com.colman.petals.R.string.what_date_format_should_be_used
import br.com.colman.petals.settings.view.dialog.SelectFromListDialog
import compose.icons.TablerIcons
import compose.icons.tablericons.Calendar

@Preview
@Composable
fun DateListItem(
  dateFormat: String = "",
  dateFormatList: List<String> = listOf(),
  setDateFormat: (String) -> Unit = {}
) {
  DialogListItem(
    icon = TablerIcons.Calendar,
    textId = date_format_label,
    descriptionId = what_date_format_should_be_used,
    dialog = { hideDialog -> DateDialog(dateFormat, dateFormatList, setDateFormat, hideDialog) }
  )
}

@Preview
@Composable
private fun DateDialog(
  initialDateFormat: String = "",
  dateFormatList: List<String> = listOf(),
  setDateFormat: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  SelectFromListDialog(
    initialValue = initialDateFormat,
    possibleValues = dateFormatList,
    setValue = setDateFormat,
    onDismiss = onDismiss,
    label = date_format_label
  )
}
