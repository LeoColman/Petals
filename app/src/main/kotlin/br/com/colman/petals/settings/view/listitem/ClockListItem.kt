package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R
import br.com.colman.petals.R.string.clock_format_label
import br.com.colman.petals.R.string.what_clock_format_should_be_used
import br.com.colman.petals.settings.view.dialog.SelectFromListDialog
import br.com.colman.petals.utils.datetime.ClockFormatEnum
import compose.icons.TablerIcons
import compose.icons.tablericons.Clock

@Preview
@Composable
fun ClockListItem(
  is24HoursFormat: Boolean = false,
  clockFormatList: List<ClockFormatEnum> = listOf(),
  setIs24HoursFormat: (Boolean) -> Unit = {}
) {
  DialogListItem(
    icon = TablerIcons.Clock,
    textId = clock_format_label,
    descriptionId = what_clock_format_should_be_used,
    dialog = { hideDialog ->
      ClockDialog(
        is24HoursFormat,
        clockFormatList,
        setIs24HoursFormat,
        hideDialog
      )
    }
  )
}

@Preview
@Composable
private fun ClockDialog(
  is24HoursFormat: Boolean = false,
  clockFormatList: List<ClockFormatEnum> = listOf(),
  setIs24HoursFormat: (Boolean) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  val context = LocalContext.current
  val initialValue = if (is24HoursFormat) {
    stringResource(id = R.string.hours_24)
  } else {
    stringResource(id = R.string.hours_12)
  }

  SelectFromListDialog(
    initialValue = initialValue,
    possibleValues = clockFormatList.map { stringResource(it.nameRes) },
    setValue = { value -> setIs24HoursFormat(value == context.getString(R.string.hours_24)) },
    onDismiss = onDismiss,
    label = clock_format_label
  )
}
