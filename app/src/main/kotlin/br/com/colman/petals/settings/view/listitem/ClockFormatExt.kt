package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.colman.petals.R
import br.com.colman.petals.utils.datetime.ClockFormatEnum

@Composable
fun ClockFormatEnum.name(): String {
  val id = when (this) {
    ClockFormatEnum.HOURS_12 -> R.string.hours_12
    else -> R.string.hours_24
  }

  return stringResource(id)
}
