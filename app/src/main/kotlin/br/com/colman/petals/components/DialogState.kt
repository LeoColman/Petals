package br.com.colman.petals.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

class DialogState(isShowing: Boolean = false) {
  var showing by mutableStateOf(isShowing)

  fun show() {
    showing = true
  }

  fun hide() {
    showing = false
  }

  companion object {
    fun Saver(): Saver<DialogState, Boolean> = Saver({ it.showing }, { DialogState(it) })
  }
}

@Composable
fun rememberDialogState(isShowing: Boolean = false): DialogState {
  return rememberSaveable(saver = DialogState.Saver()) {
    DialogState(isShowing)
  }
}
