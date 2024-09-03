package br.com.colman.petals.utils.datetime

import androidx.annotation.StringRes
import br.com.colman.petals.R

enum class ClockFormatEnum(@StringRes val nameRes: Int) {
  HOURS_12(R.string.hours_12),
  HOURS_24(R.string.hours_24)
}
