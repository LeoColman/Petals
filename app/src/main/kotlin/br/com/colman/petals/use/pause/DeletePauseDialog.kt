package br.com.colman.petals.use.pause

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R
import br.com.colman.petals.use.pause.repository.Pause
import java.time.format.DateTimeFormatter

@Preview
@Composable
fun DeletePauseDialog(
  pause: Pause = Pause(),
  onDelete: () -> Unit = { },
  onDismiss: () -> Unit = { }
) {
  val formatter = remember {
    DateTimeFormatter.ofPattern("HH:mm")
  }
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        stringResource(R.string.confirm_deletion),
        style = MaterialTheme.typography.headlineMedium,
      )
    },
    text = {
      Text(
        stringResource(
          R.string.are_you_sure_delete_pause,
          pause.startTime.format(formatter),
          pause.endTime.format(formatter)
        ),
      )
    },
    confirmButton = { ConfirmPauseButton(onDelete, onDismiss) },
    dismissButton = { DeletePauseButton(onDismiss) }
  )
}

@Composable
private fun ConfirmPauseButton(
  deletePause: () -> Unit = {},
  onDismiss: () -> Unit = {}
) {
  TextButton({
    deletePause()
    onDismiss()
  }) {
    Text(stringResource(R.string.delete))
  }
}

@Composable
private fun DeletePauseButton(
  onDismiss: () -> Unit = {}
) {
  TextButton(onDismiss) {
    Text(stringResource(R.string.cancel))
  }
}
