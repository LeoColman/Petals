package br.com.colman.petals.information

import ExpandableComponent
import android.content.Context
import android.content.res.XmlResourceParser
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R
import org.xmlpull.v1.XmlPullParser

@Composable
fun InformationView() {
  val context = LocalContext.current
  val generalKnowledgeList = parseXmlResource(context)

  Column {
    LazyColumn {
      item(){
        SectionHeader(text = "General Knowledge")
        generalKnowledgeList.forEach{generalKnowledge ->
          ExpandableComponent(title = generalKnowledge.title, content = { ParseGenContent(generalKnowledge.content) })
        }
        SectionHeader(text = "Legislation and Rights")
      }
//      item() {
//        ExpandableComponent(title = "Carrying marijuana", content = { GeneralInformation() })
//        ExpandableComponent(title = "Use of marijuana", content = { GeneralInformation() })
//        ExpandableComponent(title = "Interacting with police", content = { GeneralInformation() })

//      }
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

