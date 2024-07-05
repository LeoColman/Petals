package br.com.colman.petals.widget

import androidx.annotation.DrawableRes
import br.com.colman.petals.R

enum class WidgetState(@DrawableRes val iconResId: Int, val isButtonEnabled: Boolean) {
  ENABLED(R.drawable.ic_repeat, true),
  DISABLED(R.drawable.ic_padlock, false);

  val changeState: WidgetState
    get() = when (this) {
      ENABLED -> DISABLED
      DISABLED -> ENABLED
    }
}
