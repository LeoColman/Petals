package br.com.colman.petals.use.pause

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.R
import br.com.colman.petals.R.string.end_time
import br.com.colman.petals.R.string.start_time
import br.com.colman.petals.components.ClickableTextField
import br.com.colman.petals.components.timeDialogState
import br.com.colman.petals.use.pause.repository.Pause
import br.com.colman.petals.utils.truncatedToMinute
import compose.icons.TablerIcons
import compose.icons.tablericons.Clock
import java.time.LocalTime
import java.time.LocalTime.now


@Preview
@Composable
fun PauseDialog(
  pause: Pause = Pause(),
  setPause: (Pause?) -> Unit = { },
  onDismiss: () -> Unit = { }
) {
  var pauseState by remember { mutableStateOf(pause) }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = { PauseDialogContent(pauseState) { pauseState = it } },
    confirmButton = { ConfirmPauseButton(pauseState, setPause, onDismiss) },
    dismissButton = { DeletePauseButton(setPause, onDismiss) }
  )
}

@Composable
@Preview
fun PauseDialogContent(
  pause: Pause = Pause(),
  setPause: (Pause) -> Unit = {  }
) {

  var pauseStart by remember { mutableStateOf(pause.startTime) }
  var pauseEnd by remember { mutableStateOf(pause.endTime) }

  Column(Modifier.padding(8.dp), spacedBy(8.dp)) {
    Text("During this time period, the app will remind you of your break")

    TimeRow(start_time, pauseStart) { pauseStart = it }
    TimeRow(end_time, pauseEnd) { pauseEnd = it }
  }
  setPause(Pause(pauseStart, pauseEnd))
}

@Preview
@Composable
private fun TimeRow(
  @StringRes label: Int = start_time,
  value: LocalTime = now(),
  changeValue: (LocalTime) -> Unit = {}
) {
  val dialog = timeDialogState(changeValue)

  Row(Modifier.height(IntrinsicSize.Min), spacedBy(2.dp)) {
    ClickableTextField(
      label,
      TablerIcons.Clock,
      value.truncatedToMinute().toString(),
      Modifier.weight(2f),
      dialog::show,
    )
    AddTimeButton(-5, value, changeValue, Modifier.fillMaxHeight().weight(1f))
    AddTimeButton(5, value, changeValue, Modifier.fillMaxHeight().weight(1f))
  }
}

@Composable
private fun AddTimeButton(
  minutes: Int,
  value: LocalTime,
  changeValue: (LocalTime) -> Unit,
  modifier: Modifier = Modifier
) {
  TextButton({ changeValue(value.plusMinutes(minutes.toLong())) }, modifier) {
    Text(stringResource(R.string.pause_change_minutes, minutes), fontSize = 10.sp)
  }
}


@Composable
private fun ConfirmPauseButton(
  pause: Pause = Pause(),
  setPause: (Pause) -> Unit = {},
  onDismiss: () -> Unit = {}
) {
  TextButton({ setPause(pause); onDismiss() }) {
    Text(stringResource(R.string.ok))
  }
}

@Composable
private fun DeletePauseButton(
  setPause: (Pause?) -> Unit = {},
  onDismiss: () -> Unit = {}
) {
  TextButton({ setPause(null); onDismiss() }) {
    Text(stringResource(R.string.delete))
  }
}