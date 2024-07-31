package br.com.colman.petals.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.string.cancel
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.select_time
import com.vanpra.composematerialdialogs.MaterialDialogState
import java.time.LocalTime
import java.util.Calendar

/**
 * Time Picker Dialog
 *
 * [Material3TimePicker] is a date picker dialog  which let's user choose
 * time in 12 hour or 24 hour format.It remembers time picker state for both 12
 * and 24 state to let user pick time in any format.
 *
 * @param dialogState holds the state logic to open or close the dialog.
 * @param onTimeChange is a lambda function which accepts LocalTime as parameter to set
 * the time chosen by the user.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material3TimePicker(
  dialogState: MaterialDialogState,
  onTimeChange: (LocalTime) -> Unit
) {
  val timePickerState24Hour = getTimePickerState(is24Hour = true)
  val timePickerState12Hour = getTimePickerState(is24Hour = false)

  var timePickerState by remember { mutableStateOf(timePickerState24Hour) }

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
      TimePickerDialogContent(timePickerState = timePickerState) {
        timePickerState = if (it) {
          timePickerState24Hour
        } else {
          timePickerState12Hour
        }
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogContent(timePickerState: TimePickerState, set24HourMode: (Boolean) -> Unit) {
  Column {
    Text(text = stringResource(id = select_time))
    var tabIndex by remember { mutableIntStateOf(if (timePickerState.is24hour) 0 else 1) }
    val tabs = listOf("24H Mode", "12H Mode")

    TabRow(selectedTabIndex = tabIndex) {
      tabs.forEachIndexed { index, title ->
        Tab(
          text = { Text(title) },
          selected = tabIndex == index,
          onClick = {
            if (index != tabIndex) {
              tabIndex = index
              set24HourMode(index == 0)
            }
          }
        )
      }
    }
    TimePicker(state = timePickerState)
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun getTimePickerState(is24Hour: Boolean): TimePickerState {
  val currentTime = Calendar.getInstance()
  return rememberTimePickerState(
    initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
    initialMinute = currentTime.get(Calendar.MINUTE),
    is24Hour = is24Hour,
  )
}
