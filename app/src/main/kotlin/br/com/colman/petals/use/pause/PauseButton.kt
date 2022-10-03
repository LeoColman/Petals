package br.com.colman.petals.use.pause

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.Alarm

@Preview
@Composable
fun PauseButton() {
  Row(Modifier.padding(8.dp).clickable { }, spacedBy(8.dp), CenterVertically) {
    Icon(TablerIcons.Alarm, null)
    Text("Pause Time")
  }
}
