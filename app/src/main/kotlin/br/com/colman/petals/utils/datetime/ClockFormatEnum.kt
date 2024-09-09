package br.com.colman.petals.utils.datetime

import androidx.annotation.StringRes
import br.com.colman.petals.R

enum class ClockFormatEnum(@StringRes val nameRes: Int) {
  Hours12(R.string.hours_12),
  Hours24(R.string.hours_24)
}
