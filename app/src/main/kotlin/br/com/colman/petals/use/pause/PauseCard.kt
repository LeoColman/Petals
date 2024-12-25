package br.com.colman.petals.use.pause

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R
import br.com.colman.petals.use.pause.repository.Pause
import br.com.colman.petals.utils.truncatedToMinute
import java.time.format.DateTimeFormatter.ISO_TIME

@Composable
fun PauseCard(
  modifier: Modifier = Modifier,
  pause: Pause,
  onPauseEditClick: () -> Unit,
  onPauseDeleteClick: () -> Unit,
  onPauseDisabledStatusChange: (Boolean) -> Unit,
) {
  Card(modifier = modifier) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = CenterVertically,
    ) {
      Column {
        Text(stringResource(id = R.string.pause_time))
        Text(
          "${pause.startTime.truncatedToMinute().format(ISO_TIME)} - ${pause.endTime.truncatedToMinute().format(
            ISO_TIME
          )}"
        )
      }
      Row(horizontalArrangement = spacedBy(4.dp)) {
        DisablePauseView(
          isEnabled = pause.isEnabled,
          onClick = onPauseDisabledStatusChange
        )
        IconButton(onClick = onPauseEditClick) {
          Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = stringResource(id = R.string.edit_pause)
          )
        }
        IconButton(onClick = onPauseDeleteClick) {
          Icon(
            imageVector = Icons.Rounded.DeleteForever,
            contentDescription = stringResource(id = R.string.delete_pause)
          )
        }
      }
    }
  }
}

@Preview
@Composable
private fun PauseCard_Preview() {
  PauseCard(pause = Pause(), onPauseEditClick = { }, onPauseDeleteClick = { }) {}
}
