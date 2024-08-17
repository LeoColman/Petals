package br.com.colman.petals.components

import androidx.compose.runtime.Composable
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun dateDialogState(onDateChange: (newDate: LocalDate) -> Unit) = createMaterialDialog { dialogState ->
  if (dialogState.showing) {
    DatePickerDialog(dialogState = dialogState, onDateChange = onDateChange)
  }
}

@Composable
fun timeDialogState(is24Hour: Boolean, onTimeChange: (newTime: LocalTime) -> Unit) = createMaterialDialog { dialogState ->
  if (dialogState.showing) {
    TimePickerDialog(dialogState = dialogState, onTimeChange = onTimeChange, is24Hour = is24Hour)
  }
}

@Composable
private fun createMaterialDialog(content: @Composable (dialogState: DialogState) -> Unit) =
  rememberDialogState().also { dialogState ->
    content(dialogState)
  }
