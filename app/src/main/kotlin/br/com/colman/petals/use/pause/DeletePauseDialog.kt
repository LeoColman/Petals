package br.com.colman.petals.use.pause

import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R
import br.com.colman.petals.use.pause.repository.Pause
import br.com.colman.petals.utils.truncatedToMinute
import java.time.format.DateTimeFormatter.ISO_TIME

@Preview
@Composable
fun DeletePauseDialog(
  pause: Pause = Pause(),
  onDelete: () -> Unit = { },
  onDismiss: () -> Unit = { }
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        stringResource(R.string.confirm_deletion),
        style = MaterialTheme.typography.h5,
      )
    },
    text = {
      Text(
        stringResource(
          R.string.are_you_sure_delete_pause,
          pause.startTime.truncatedToMinute().format(ISO_TIME),
          pause.endTime.truncatedToMinute().format(ISO_TIME)
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
