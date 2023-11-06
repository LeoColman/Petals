package br.com.colman.petals.information

import ExpandableComponent
import android.content.Context
import android.content.res.XmlResourceParser
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.R
import org.xmlpull.v1.XmlPullParser
import java.util.Locale

@Composable
fun InformationView() {
  val context = LocalContext.current
  val generalKnowledgeList = parseXmlResource(context)
  var expanded by remember { mutableStateOf(true) }
  var selectedCountry by remember { mutableStateOf<String?>(null) }
  val countries = listOf("USA", "Canada", "UK", "Germany", "France", "Japan")

  Column {
    LazyColumn {
      item {
        SectionHeader(text = "General Knowledge")
        generalKnowledgeList.forEach{generalKnowledge ->
          ExpandableComponent(title = generalKnowledge.title, content = { ParseGenContent(generalKnowledge.content) })
        }
        SectionHeader(text = "Legislation and Rights")
      }
      item{
        CountryPicker()
      }
    }
  }
}

@Composable
fun ParseGenContent(text: String) {
  val content = text.split("\n")
  Column{
    content.forEach{ item ->
      Text(
        text = "•" + item.trim(),
        modifier = Modifier
          .padding(4.dp)
      )
    }
  }
}

@Composable
fun SectionHeader(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.h5,
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentWidth(Alignment.CenterHorizontally)
      .padding(top = 16.dp, bottom = 16.dp)
  )
}

fun parseXmlResource(context: Context): List<InfoItem> {
  val infoItems = mutableListOf<InfoItem>()
  val parser: XmlResourceParser = context.resources.getXml(R.xml.general_knowledge)

  var eventType = parser.eventType
  var currentTag: String? = null
  var currentTitle: String? = null
  var currentContent: String? = null

  while (eventType != XmlPullParser.END_DOCUMENT) {
    when (eventType) {
      XmlPullParser.START_TAG -> {
        currentTag = parser.name
      }
      XmlPullParser.TEXT -> {
        when (currentTag) {
          "title" -> currentTitle = parser.text
          "content" -> currentContent = parser.text
        }
      }
      XmlPullParser.END_TAG -> {
        if (parser.name == "item") {
          if (currentTitle != null && currentContent != null) {
            infoItems.add(InfoItem(currentTitle, currentContent))
          }
          currentTitle = null
          currentContent = null
        }
        currentTag = null
      }
    }
    eventType = parser.next()
  }

  return infoItems
}

@Composable
fun CountryPicker() {
  var expanded by remember { mutableStateOf(false) }
  val items = listOf("Brazil", "Canada", "Slovakia")
  var selectedItem by remember { mutableStateOf("Select Country") }

  Column(modifier = Modifier.padding(16.dp)) {
    OutlinedButton(
      onClick = { expanded = true },
      modifier = Modifier
        .fillMaxWidth()
    ) {
      Text(
        text = selectedItem,
        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
        modifier = Modifier
          .padding(4.dp)
      )
    }

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      modifier = Modifier
        .fillMaxWidth(0.9f)
        .padding(8.dp)
    ) {
      items.forEachIndexed { index, s ->
        DropdownMenuItem(
          onClick = {
            selectedItem = s
            expanded = false
          },
        ) {
          Text(
            text = s
          )
        }
      }
    }
    Card(
      elevation = 4.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)
    ){
      if(selectedItem == "Select Country"){
        Text(
          text = "No country selected",
          modifier = Modifier
            .padding(8.dp)
        )
      }else{
        Text(
          text = "Legislation and rights for: $selectedItem",
          modifier = Modifier
            .padding(8.dp)
        )
      }
    }
  }
}
@Composable
fun getAllCountries(): List<String> {
  // This will sort the countries by their display name in the default locale
  return Locale.getISOCountries().map { countryCode ->
    Locale("", countryCode).displayCountry
  }.sorted()
}
