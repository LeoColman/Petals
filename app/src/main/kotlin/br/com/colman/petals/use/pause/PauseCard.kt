package br.com.colman.petals.use.pause

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R
import br.com.colman.petals.use.pause.repository.Pause
import java.time.format.DateTimeFormatter

@Composable
fun PauseCard(
  modifier: Modifier = Modifier,
  pause: Pause,
  onPauseEditClick: () -> Unit,
  onPauseDeleteClick: () -> Unit,
  onPauseDisabledStatusChange: (Boolean) -> Unit,
) {
  Card(modifier = modifier) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = CenterVertically,
    ) {
      Column {
        Text(stringResource(id = R.string.pause_time))
        Text("${pause.startTime.format(formatter)} - ${pause.endTime.format(formatter)}")
      }
      Row(horizontalArrangement = spacedBy(4.dp)) {
        DisablePauseView(isEnabled = pause.isEnabled, onClick = onPauseDisabledStatusChange)
        IconButton(onClick = onPauseEditClick) {
          Icon(imageVector = Icons.Rounded.Edit, contentDescription = "Edit pause")
        }
        IconButton(onClick = onPauseDeleteClick) {
          Icon(imageVector = Icons.Rounded.DeleteForever, contentDescription = "Delete pause")
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
