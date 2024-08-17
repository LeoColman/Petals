package br.com.colman.petals.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@Composable
fun ExpandableComponentPreview() {
  Column {
    ExpandableComponent("Expandable Component") {
      Column {
        Text("Expanded!")
        Text("Isn't it beautiful?")
      }
    }

    ExpandableComponent("Collapsable Component", true) {
      Column {
        Text("Expanded!")
        Text("Isn't it beautiful?")
      }
    }
  }
}

@Composable
fun ExpandableComponent(title: String, isExpanded: Boolean = false, content: @Composable () -> Unit) {
  var isExpanded by remember { mutableStateOf(isExpanded) }
  val onClick = { isExpanded = !isExpanded }

  Card(Modifier.padding(8.dp)) {
    Column {
      ExpandCollapseLine(title, isExpanded, onClick)

      if (isExpanded) {
        Box(Modifier.fillMaxWidth().padding(8.dp).animateContentSize()) {
          content()
        }
      }
    }
  }
}

@Composable
private fun ExpandCollapseLine(
  text: String,
  isExpanded: Boolean,
  onClick: () -> Unit,
) {
  val icon = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore

  Row(
    Modifier.fillMaxWidth().clickable(onClick = onClick).padding(8.dp).testTag("ExpandCollapseLine"),
    verticalAlignment = CenterVertically
  ) {
    IconButton(onClick) {
      Icon(icon, null)
    }
    Text(text, Modifier.weight(1f), style = TextStyle(fontWeight = Bold, fontSize = 16.sp))
  }
}
