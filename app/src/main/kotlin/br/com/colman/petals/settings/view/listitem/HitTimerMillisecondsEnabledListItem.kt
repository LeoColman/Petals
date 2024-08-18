package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.enable_or_disable_milliseconds_on_hit_timer_page
import br.com.colman.petals.R.string.hit_timer_milliseconds_enabled
import br.com.colman.petals.settings.view.dialog.SwitchListItem
import compose.icons.TablerIcons
import compose.icons.tablericons.ToggleLeft

@Preview
@Composable
fun HitTimerMillisecondsEnabledListItem(
  hitTimerMillisecondsEnabled: Boolean,
  setHitTimerMillisecondsEnabled: (Boolean) -> Unit = {}
) {
  SwitchListItem(
    icon = TablerIcons.ToggleLeft,
    textId = hit_timer_milliseconds_enabled,
    descriptionId = enable_or_disable_milliseconds_on_hit_timer_page,
    initialState = hitTimerMillisecondsEnabled,
    onChangeState = setHitTimerMillisecondsEnabled
  )
}
