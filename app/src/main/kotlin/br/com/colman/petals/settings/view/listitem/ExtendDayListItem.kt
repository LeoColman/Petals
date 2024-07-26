package br.com.colman.petals.settings.view.listitem

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.extend_day_a_few_hours
import br.com.colman.petals.R.string.wait_until_3am_to_show_a_new_day
import br.com.colman.petals.settings.view.dialog.SwitchListItem
import compose.icons.TablerIcons
import compose.icons.tablericons.CalendarTime

@Preview
@Composable
fun ExtendDayListItem(
  extendedDay: Boolean,
  setExtendDayOption: (Boolean) -> Unit = {}
) {
  SwitchListItem(
    icon = TablerIcons.CalendarTime,
    textId = extend_day_a_few_hours,
    descriptionId = wait_until_3am_to_show_a_new_day,
    extendedDay,
    setExtendDayOption
  )
}
