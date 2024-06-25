package br.com.colman.petals.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun ExpandableComponent() {
  ExpandableComponent(0, "Expand this CoMpOnEnT") {
    Column {
      Text("Expanded!")
      Text("Isn't it beautiful?")
    }
  }
}

@Composable
fun ExpandableComponent(index: Int, title: String, content: @Composable () -> Unit) {
  var expanded by remember { mutableStateOf(false) }
  val flipExpanded = { expanded = !expanded }

  Card(Modifier.padding(8.dp), elevation = 4.dp) {
    Column {
      ExpandCollapseLine(index, flipExpanded, expanded, title)

      if (expanded) {
        Box(Modifier.fillMaxWidth().padding(8.dp).animateContentSize()) {
          content()
        }
      }
    }
  }
}

@Composable
private fun ExpandCollapseLine(index: Int, flipExpanded: () -> Unit, expanded: Boolean, title: String) {
  Row(
    Modifier.fillMaxWidth().clickable(flipExpanded).padding(8.dp).testTag("ExpandCollapseLine $index"),
    verticalAlignment = CenterVertically
  ) {
    IconButton(flipExpanded) {
      Icon(if (expanded) Filled.ExpandLess else Filled.ExpandMore, null)
    }
    Text(title, Modifier.weight(1f), style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp))
  }
}

private fun Modifier.clickable(onClick: () -> Unit) = this.clickable(onClick = onClick)
