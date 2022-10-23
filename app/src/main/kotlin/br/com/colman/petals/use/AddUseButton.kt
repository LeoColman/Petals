package br.com.colman.petals.use

import androidx.compose.foundation.layout.Row
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.add_use
import br.com.colman.petals.R.string.add_use_during_pause_alert
import br.com.colman.petals.R.string.no
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.yes
import br.com.colman.petals.R.string.yes_timer
import br.com.colman.petals.use.pause.repository.Pause
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import compose.icons.TablerIcons
import compose.icons.tablericons.Lock
import kotlinx.coroutines.delay
import java.math.BigDecimal.ZERO
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun AddUseButton(
  repository: UseRepository,
  pause: Pause? = null
) {
  var openAddUseDialog by remember { mutableStateOf(false) }
  var openConfirmAddUseDialog by remember { mutableStateOf(false) }
  val lastUse by repository.getLastUse().collectAsState(null)

  if (openAddUseDialog) {
    AddUseDialog(lastUse, { repository.upsert(it) }) { openAddUseDialog = false }
  }

  if (openConfirmAddUseDialog) {
    ConfirmAddUseDuringPauseDialog({ openConfirmAddUseDialog = false }) {
      openConfirmAddUseDialog = false
      openAddUseDialog = true
    }
  }

  if (pause == null || !pause.isActive()) {
    UseButton { openAddUseDialog = true }
  } else {
    PauseUseButton { openConfirmAddUseDialog = true }
  }
}

@Preview
@Composable
private fun UseButton(onClick: () -> Unit = { }) {
  Button(onClick) {
    Text(stringResource(add_use))
  }
}

@Preview
@Composable
private fun PauseUseButton(onClick: () -> Unit = { }) {
  Button(onClick) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(stringResource(add_use))
      Icon(TablerIcons.Lock, contentDescription = null)
    }
  }
}

@Composable
@Preview
private fun ConfirmAddUseDuringPauseDialog(
  onDismiss: () -> Unit = {},
  onConfirm: () -> Unit = {}
) {

  var yesTimer by remember { mutableStateOf(10) }
  var yesEnabled by remember { mutableStateOf(yesTimer == 0) }

  LaunchedEffect(yesTimer) {
    while (yesTimer > 0) {
      delay(1000)
      yesTimer--
      yesEnabled = yesTimer == 0
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = { Text(stringResource(add_use_during_pause_alert)) },
    confirmButton = {
      TextButton(onConfirm, enabled = yesEnabled) {
        if (yesEnabled) {
          Text(stringResource(yes))
        } else {
          Text(stringResource(yes_timer, yesTimer))
        }
      }
    },
    dismissButton = { TextButton(onDismiss) { Text(stringResource(no)) } }
  )
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
    LocalDateTime.of(date.value, time.value),
    amount.value.toBigDecimalOrNull() ?: ZERO,
    costPerGram.value.toBigDecimalOrNull() ?: ZERO
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
