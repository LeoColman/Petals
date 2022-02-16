package br.com.colman.petals.use

import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.add_use
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import java.math.BigDecimal.ZERO
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun AddUseButton(repository: UseRepository) {
  var openDialog by remember { mutableStateOf(false) }
  val lastUse by repository.getLastUse().collectAsState(null)

  if (openDialog) {
    AddUseDialog(lastUse, {repository.insert(it)}) { openDialog = false }
  }

  Button(onClick = { openDialog = true }) {
    Text(stringResource(add_use))
  }
}

@Suppress("NAME_SHADOWING")
@Composable
@Preview
private fun AddUseDialog(
  previousUse: Use? = null,
  onAddUse: (Use) -> Unit = {},
  onDismiss: () -> Unit = {}
) {
  val amount = remember { mutableStateOf(previousUse?.amountGrams?.toString().orEmpty()) }
  val costPerGram = remember { mutableStateOf(previousUse?.costPerGram?.toString().orEmpty()) }
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
    Text(stringResource(ok))
  }
}

