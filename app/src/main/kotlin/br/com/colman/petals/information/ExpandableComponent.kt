
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExpandableComponent(title: String, content: @Composable () -> Unit) {
  var expanded by remember { mutableStateOf(false) }

  Card(
    elevation = 4.dp,
    modifier = Modifier
      .padding(8.dp)
  ) {
    Column {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .clickable { expanded = !expanded }
          .padding(8.dp)
      ) {
        IconButton(
          onClick = { expanded = !expanded }
        ) {
          Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = if (expanded) "Collapse" else "Expand"
          )
        }
        Text(
          text = title,
          style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp), //MaterialTheme.typography.subtitle1,
          modifier = Modifier.weight(1f) // This makes the Text occupy the remaining space
        )
      }
      // Content that appears when you click the title or icon
      if (expanded) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize()
        ) {
          content() // Here we invoke the passed composable function
        }
      }
    }
  }
}
