package br.com.colman.petals.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager

/**
 * Create and [remember] a [DialogState].
 *
 * @param initialValue the initial showing state of the dialog
 */
class DialogState(initialValue: Boolean = false) {
  var showing by mutableStateOf(initialValue)

  /**
   *  Shows the dialog
   */
  fun show() {
    showing = true
  }

  /**
   * Clears focus with a given [FocusManager] and then hides the dialog
   *
   * @param focusManager the focus manager of the dialog view
   */
  fun hide(focusManager: FocusManager? = null) {
    focusManager?.clearFocus()
    showing = false
  }

  companion object {
    /**
     * The default [Saver] implementation for [DialogState].
     */
    fun Saver(): Saver<DialogState, *> = Saver(
      save = { it.showing },
      restore = { DialogState(it) }
    )
  }
}

/**
 * Create and [remember] a [DialogState].
 *
 * @param initialValue the initial showing state of the dialog
 */
@Composable
fun rememberDialogState(initialValue: Boolean = false): DialogState {
  return rememberSaveable(saver = DialogState.Saver()) {
    DialogState(initialValue)
  }
}
