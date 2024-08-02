package br.com.colman.petals.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.string.cancel
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.select_time
import java.time.LocalTime
import java.util.Calendar

/**
 * Time Picker Dialog
 *
 * [Material3TimePicker] is a date picker dialog  which let's user choose
 * time in 24 hour format.
 *
 * @param dialogState holds the state logic to open or close the dialog.
 * @param onTimeChange is a lambda function which accepts LocalTime as parameter to set
 * the time chosen by the user.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material3TimePicker(
  dialogState: DialogState,
  onTimeChange: (LocalTime) -> Unit,
  is24Hour: Boolean = true
) {
  val currentTime = Calendar.getInstance()
  val timePickerState = rememberTimePickerState(
    initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
    initialMinute = currentTime.get(Calendar.MINUTE),
    is24Hour = is24Hour
  )

  AlertDialog(
    onDismissRequest = { dialogState.hide() },
    dismissButton = {
      TextButton(onClick = { dialogState.hide() }) {
        Text(stringResource(id = cancel))
      }
    },
    confirmButton = {
      TextButton(onClick = {
        onTimeChange(LocalTime.of(timePickerState.hour, timePickerState.minute))
        dialogState.hide()
      }) {
        Text(stringResource(id = ok))
      }
    },
    text = {
      Column {
        Text(text = stringResource(id = select_time))
        TimePicker(state = timePickerState)
      }
    }
  )
}
