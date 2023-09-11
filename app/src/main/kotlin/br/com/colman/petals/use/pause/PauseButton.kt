package br.com.colman.petals.use.pause

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.colman.petals.use.pause.repository.Pause
import br.com.colman.petals.use.pause.repository.PauseRepository
import compose.icons.TablerIcons
import compose.icons.tablericons.Alarm

@Composable
fun PauseButton(
  pauseRepository: PauseRepository
) {
  var openPauseDialog by remember { mutableStateOf(false) }
  val pause by pauseRepository.get().collectAsState(null)

  PauseButtonView { openPauseDialog = true }

  if (openPauseDialog) {
    PauseDialog(
      pause = pause ?: Pause(),
      setPause = {
        if (it == null) {
          pauseRepository.delete()
        } else {
          pauseRepository.set(it)
        }
      },
      onDismiss = { openPauseDialog = false }
    )
  }
}

@Preview
@Composable
private fun PauseButtonView(
  onClick: () -> Unit = { }
) {
  Row(Modifier.padding(8.dp).clickable { onClick() }, spacedBy(8.dp), CenterVertically) {
    Icon(TablerIcons.Alarm, null)
    Text("Pause Time")
  }
}
