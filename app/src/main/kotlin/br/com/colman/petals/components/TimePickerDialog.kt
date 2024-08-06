package br.com.colman.petals.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.string.cancel
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.select_time
import br.com.colman.petals.isDarkModeEnabled
import java.time.LocalTime
import androidx.compose.material3.TimePicker as MaterialTimePicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
  dialogState: DialogState,
  onTimeChange: (LocalTime) -> Unit,
) {
  val timePickerState = rememberTimePickerState(LocalTime.now().hour, LocalTime.now().minute)

  MaterialTheme(if (isDarkModeEnabled()) darkColorScheme() else lightColorScheme()) {
    AlertDialog(onDismissRequest = dialogState::hide, dismissButton = {
      TextButton(onClick = dialogState::hide) {
        Text(stringResource(cancel))
      }
    }, confirmButton = {
      TextButton(onClick = {
        onTimeChange(LocalTime.of(timePickerState.hour, timePickerState.minute))
        dialogState.hide()
      }) {
        Text(stringResource(ok))
      }
    }, text = {
      Column {
        Text(stringResource(select_time))
        MaterialTimePicker(timePickerState)
      }
    })
  }
}
