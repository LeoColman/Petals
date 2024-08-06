package br.com.colman.petals.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R.string.cancel
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.isDarkModeEnabled
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset.UTC
import androidx.compose.material3.DatePicker as MaterialDatePicker
import androidx.compose.material3.DatePickerDialog as MaterialDatePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
  dialogState: DialogState,
  onDateChange: (selectedDate: LocalDate) -> Unit
) {
  val datePickerState = rememberDatePickerState(Instant.now().toEpochMilli())

  MaterialTheme(if (isDarkModeEnabled()) darkColorScheme() else lightColorScheme()) {
    MaterialDatePickerDialog(onDismissRequest = dialogState::hide, confirmButton = {
      TextButton(onClick = {
        val selectedDateMillis = datePickerState.selectedDateMillis
        if (selectedDateMillis != null) {
          onDateChange(Instant.ofEpochMilli(selectedDateMillis).atOffset(UTC).toLocalDate())
          dialogState.hide()
        }
      }) { Text(stringResource(ok)) }
    }, dismissButton = {
      TextButton(dialogState::hide) {
        Text(stringResource(cancel))
      }
    }) {
      MaterialDatePicker(datePickerState)
    }
  }
}
