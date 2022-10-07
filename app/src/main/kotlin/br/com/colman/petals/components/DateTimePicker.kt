package br.com.colman.petals.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogScope
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime

@Preview
@Composable
fun dateDialogState(
  onDateChange: (LocalDate) -> Unit = {}
) = createMaterialDialog {
  datepicker(title = stringResource(R.string.select_date)) { date ->
    onDateChange(date)
  }
}

@Preview
@Composable
fun timeDialogState(
  onTimeChange: (LocalTime) -> Unit = { }
) = createMaterialDialog {
  timepicker { onTimeChange(it) }
}

@Composable
private fun createMaterialDialog(
  content: @Composable MaterialDialogScope.() -> Unit
) = rememberMaterialDialogState().also {
  MaterialDialog(
    dialogState = it,
    buttons = {
      positiveButton(stringResource(R.string.ok))
      negativeButton(stringResource(R.string.cancel))
    },
    content = content
  )
}
