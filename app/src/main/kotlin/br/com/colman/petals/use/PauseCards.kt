package br.com.colman.petals.use

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.colman.petals.use.pause.DeletePauseDialog
import br.com.colman.petals.use.pause.PauseCard
import br.com.colman.petals.use.pause.PauseDialog
import br.com.colman.petals.use.pause.repository.Pause
import br.com.colman.petals.use.pause.repository.PauseRepository

@Composable
fun PauseCards(
  pauseRepository: PauseRepository
) {
  val pauses by pauseRepository.getAll().collectAsState(listOf())
  var editingPause: Pause? by remember {
    mutableStateOf(null)
  }
  var deletingPause: Pause? by remember {
    mutableStateOf(null)
  }

  Column {
    pauses.forEach { pause ->
      PauseCard(
        modifier = Modifier.padding(8.dp),
        pause = pause,
        onPauseEditClick = {
          editingPause = pause
        },
        onPauseDeleteClick = {
          deletingPause = pause
        },
        onPauseDisabledStatusChange = { isEnabled ->
          pauseRepository.update(pause.copy(isEnabled = isEnabled))
        }
      )
    }
  }

  editingPause?.let {
    PauseDialog(
      pause = it,
      setPause = { updatedPause ->
        if (updatedPause != null) {
          pauseRepository.update(updatedPause)
        } else {
          pauseRepository.delete(it)
        }
      },
      onDismiss = { editingPause = null }
    )
  }
  deletingPause?.let { pause ->
    DeletePauseDialog(
      pause,
      onDelete = {
        pauseRepository.delete(pause)
      },
      onDismiss = {
        deletingPause = null
      }
    )
  }
}
