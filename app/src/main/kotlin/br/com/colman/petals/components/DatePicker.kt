package br.com.colman.petals.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.string.cancel
import br.com.colman.petals.R.string.ok
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 *  Date picker Dialog
 *
 * [Material3DatePicker] is a date picker dialog  which let's user pick
 * date. The default year range can be picked from 1900 to 2100.
 *
 * @param dialogState holds the state logic to open or close the dialog.
 *
 * @param onDateChange is a lambda function which accepts LocalDate as parameter to set the date
 * picked by the user.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Material3DatePicker(
  dialogState: DialogState,
  onDateChange: (LocalDate) -> Unit
) {
  val datePickerState = rememberDatePickerState(
    yearRange = 1900..2100
  )
  val confirmEnabled by remember {
    derivedStateOf { datePickerState.selectedDateMillis != null }
  }
  DatePickerDialog(
    onDismissRequest = { dialogState.hide() },
    confirmButton = {
      TextButton(
        enabled = confirmEnabled,
        onClick = {
          val selectedDateMillis = datePickerState.selectedDateMillis
          if (selectedDateMillis != null) {
            /**
             * The LocalDate is calculated by converting selected date (milliseconds) to
             * LocalDate using Instant.
             */
            onDateChange(
              Instant
                .ofEpochMilli(selectedDateMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            )
            dialogState.hide()
          }
        }
      ) { Text(stringResource(id = ok)) }
    },
    dismissButton = {
      TextButton(onClick = { dialogState.hide() }) {
        Text(stringResource(id = cancel))
      }
    }
  ) {
    DatePicker(state = datePickerState)
  }
}
