package br.com.colman.petals.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.string.cancel
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.select_date
import br.com.colman.petals.R.string.select_time
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogScope
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun dateDialogState(onDateChange: (LocalDate) -> Unit) = createMaterialDialog {
  datepicker(title = stringResource(select_date)) { date ->
    onDateChange(date)
  }
}

@Composable
fun timeDialogState(is24Hour: Boolean, onTimeChange: (newTime: LocalTime) -> Unit) = createMaterialDialog {
  timepicker(title = stringResource(select_time), is24HourClock = is24Hour) { time ->
    onTimeChange(time)
  }
}

@Composable
private fun createMaterialDialog(content: @Composable MaterialDialogScope.() -> Unit) =
  rememberMaterialDialogState().also {
    MaterialDialog(
      dialogState = it,
      buttons = {
        positiveButton(res = ok)
        negativeButton(res = cancel)
      },
      content = content
    )
  }
