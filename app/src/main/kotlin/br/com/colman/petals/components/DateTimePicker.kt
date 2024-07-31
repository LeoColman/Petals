package br.com.colman.petals.components

import androidx.compose.runtime.Composable
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun dateDialogState(onDateChange: (LocalDate) -> Unit) = createMaterialDialog { dialogState ->
  if (dialogState.showing) {
    Material3DatePicker(dialogState = dialogState, onDateChange = onDateChange)
  }
}

@Composable
fun timeDialogState(onTimeChange: (LocalTime) -> Unit) = createMaterialDialog { dialogState ->
  if (dialogState.showing) {
    Material3TimePicker(dialogState = dialogState, onTimeChange = onTimeChange)
  }
}

@Composable
private fun createMaterialDialog(content: @Composable (dialogState: MaterialDialogState) -> Unit) =
  rememberMaterialDialogState().also { dialogState ->
    content(dialogState)
  }
