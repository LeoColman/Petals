@file:OptIn(ExperimentalLayoutApi::class, ExperimentalLayoutApi::class)

package br.com.colman.petals.statistics.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun MultiPeriodSelectPreview() {
  var selectedPeriods by remember { mutableStateOf(emptyList<Period>()) }
  MultiPeriodSelect(selectedPeriods) { selectedPeriods = it }
}

@Composable
fun MultiPeriodSelect(selectedPeriods: List<Period>, setSelectedDays: (List<Period>) -> Unit) {
  fun addPeriod(period: Period) { setSelectedDays(selectedPeriods + period) }
  fun removePeriod(period: Period) { setSelectedDays(selectedPeriods - period) }
  fun putPeriod(period: Period, condition: Boolean) { if (condition) addPeriod(period) else removePeriod(period) }

  FlowRow(Modifier, Arrangement.Center, Arrangement.Center) {
    Period.entries().forEach { period ->
      DaysCheckbox(period in selectedPeriods, { putPeriod(period, it) }, period)
    }
  }
}

@Preview
@Composable
fun CheckboxPreviews() {
  Row {
    Box(Modifier.weight(1f)) { DaysCheckbox(true, {}, Period.Zero) }
    Box(Modifier.weight(1f)) { DaysCheckbox(false, {}, Period.Week) }
    Box(Modifier.weight(1f)) { DaysCheckbox(true, {}, Period.TwoWeek) }
    Box(Modifier.weight(1f)) { DaysCheckbox(false, {}, Period.Month) }
  }
}

@Composable
fun DaysCheckbox(selected: Boolean, setSelected: (Boolean) -> Unit, period: Period) {
  // The tag moves to the row along with the click handling, so it still marks the thing you tap.
  // The label used to carry its own bare clickable, which gave a screen reader a second stop with
  // no role and no checked state; folding both into the row leaves exactly one target.
  Row(
    Modifier
      .testTag("Days ${period.days}")
      .toggleable(value = selected, role = Role.Checkbox, onValueChange = setSelected),
    Arrangement.Start,
    Alignment.CenterVertically
  ) {
    // Material sizes a control to the 48dp touch target only while it handles its own changes.
    // Handing the toggle to the row drops that, and with it the breathing room around the box, so
    // ask for the size back explicitly.
    Checkbox(selected, null, Modifier.minimumInteractiveComponentSize())
    Text(period.label(), fontSize = 10.sp)
  }
}
