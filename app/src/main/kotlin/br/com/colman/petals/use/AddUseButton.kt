package br.com.colman.petals.use

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.add_use
import java.math.BigDecimal.ZERO
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Preview
@Composable
fun AddUseButton(onAddUse: (Use) -> Unit = {}) {
  var openDialog by remember { mutableStateOf(false) }

  if (openDialog) {
    AddUseDialog(onAddUse) { openDialog = false }
  }

  Button(onClick = { openDialog = true }) {
    Text(stringResource(add_use))
  }
}

@Suppress("NAME_SHADOWING")
@Composable
@Preview
private fun AddUseDialog(
  onAddUse: (Use) -> Unit = {},
  onDismiss: () -> Unit = {}
) {
  val amount = remember { mutableStateOf("") }
  val costPerGram = remember { mutableStateOf("") }
  val date = remember { mutableStateOf(LocalDate.now()) }
  val time = remember { mutableStateOf(LocalTime.now()) }

  val use = Use(
    amount.value.toBigDecimalOrNull() ?: ZERO,
    costPerGram.value.toBigDecimalOrNull() ?: ZERO,
    LocalDateTime.of(date.value, time.value)
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    text = { AddUseForm(amount, costPerGram, date, time) },
    confirmButton = { ConfirmNewUseButton(onAddUse, use, onDismiss) }
  )
}

@Composable
private fun ConfirmNewUseButton(
  onAddUse: (Use) -> Unit = {},
  use: Use = Use(),
  onDismiss: () -> Unit = {}
) {
  TextButton({ onAddUse(use); onDismiss() }) {
    Text("Ok")
  }
}

