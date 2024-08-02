package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.enable_or_disable_milliseconds_on_hit_timer_page
import br.com.colman.petals.R.string.hit_timer_milliseconds_enabled
import br.com.colman.petals.settings.view.dialog.SelectFromListDialog
import compose.icons.TablerIcons
import compose.icons.tablericons.ToggleLeft

@Preview
@Composable
fun HitTimerMillisecondsEnabledListItem(
  hitTimerMillisecondsEnabled: String = "",
  hitTimerMillisecondsEnabledList: List<String> = listOf(),
  setHitTimerMillisecondsEnabled: (String) -> Unit = {}
) {
  DialogListItem(
    icon = TablerIcons.ToggleLeft,
    textId = hit_timer_milliseconds_enabled,
    descriptionId = enable_or_disable_milliseconds_on_hit_timer_page,
    dialog = { hideDialog ->
      HitTimerMillisecondsEnabledDialog(
        hitTimerMillisecondsEnabled,
        hitTimerMillisecondsEnabledList,
        setHitTimerMillisecondsEnabled,
        hideDialog
      )
    }
  )
}

@Preview
@Composable
private fun HitTimerMillisecondsEnabledDialog(
  initialHitTimerMillisecondsEnabled: String = "",
  hitTimerMillisecondsEnabledList: List<String> = listOf(),
  setHitTimerMillisecondsEnabled: (String) -> Unit = {},
  onDismiss: () -> Unit = {},
) {
  SelectFromListDialog(
    initialValue = initialHitTimerMillisecondsEnabled,
    possibleValues = hitTimerMillisecondsEnabledList,
    setValue = setHitTimerMillisecondsEnabled,
    onDismiss = onDismiss,
    label = hit_timer_milliseconds_enabled
  )
}
