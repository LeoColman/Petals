package br.com.colman.petals.information

import android.content.Context
import android.content.res.XmlResourceParser
import br.com.colman.petals.R.xml.general_knowledge
import br.com.colman.petals.R.xml.legislation_and_rights
import org.xmlpull.v1.XmlPullParser

data class InfoItem(val title: String, val content: String)

fun parseXmlGenKnowledge(context: Context): List<InfoItem> {
  val infoItems = mutableListOf<InfoItem>()
  val parser: XmlResourceParser = context.resources.getXml(general_knowledge)
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
          "title" -> currentTitle = getResourceString(context, parser.text)
          "content" -> currentContent = getResourceString(context, parser.text)
        }
      }

      XmlPullParser.END_TAG -> {
        if (parser.name == "item" && currentTitle != null && currentContent != null) {
          infoItems.add(InfoItem(currentTitle, currentContent))
        }
        if (parser.name == "item") {
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

data class CountryItem(val name: String)

fun getCountriesList(context: Context): List<CountryItem> {
  val countries = mutableListOf<CountryItem>()

  val parser: XmlResourceParser = context.resources.getXml(legislation_and_rights)

  var eventType = parser.eventType
  var currentTag: String? = null
  var countryName: String? = null

  while (eventType != XmlPullParser.END_DOCUMENT) {
    when (eventType) {
      XmlPullParser.START_TAG -> {
        currentTag = parser.name
      }

      XmlPullParser.TEXT -> {
        if (currentTag == "country") {
          countryName = parser.text.trim()
        }
      }

      XmlPullParser.END_TAG -> {
        if (parser.name == "country" && countryName != null) {
          countries.add(CountryItem(name = getResourceString(context, countryName)))
          countryName = null
        }
        currentTag = null
      }
    }
    eventType = parser.next()
  }
  return countries
}

data class CountryInformation(
  var countryName: String,
  var legalStatus: String,
  var possession: String,
  var consumption: String,
  var medicalUse: String,
  var cultivation: String,
  var purchaseAndSale: String,
  var enforcement: String,
  var lastUpdate: String
)

fun getCountryInformation(context: Context, countryNameToFind: String): CountryInformation {
  val parser: XmlResourceParser = context.resources.getXml(legislation_and_rights)
  var eventType = parser.eventType
  val countryInformation = CountryInformation("", "", "", "", "", "", "", "", "")
  var countryName: String
  while (eventType != XmlPullParser.END_DOCUMENT) {
    if (eventType == XmlPullParser.START_TAG && parser.name == "country") {
      countryName = getResourceString(context, parser.nextText())
      if (countryName.equals(countryNameToFind, ignoreCase = true)) {
        countryInformation.countryName = getResourceString(context, countryName)
        break
      }
    }
    eventType = parser.next()
  }
  return buildCountryInformation(context, parser, countryInformation)
}

private fun buildCountryInformation(
  context: Context,
  parser: XmlResourceParser,
  countryInformation: CountryInformation
): CountryInformation {
  var eventType = parser.next()
  while (eventType != XmlPullParser.END_DOCUMENT && parser.name != "item") {
    if (eventType == XmlPullParser.START_TAG) {
      when (parser.name) {
        "legalstatus" -> countryInformation.legalStatus = getResourceString(context, parser.nextText())
        "possession" -> countryInformation.possession = getResourceString(context, parser.nextText())
        "consumption" -> countryInformation.consumption = getResourceString(context, parser.nextText())
        "medicaluse" -> countryInformation.medicalUse = getResourceString(context, parser.nextText())
        "cultivation" -> countryInformation.cultivation = getResourceString(context, parser.nextText())
        "purchaseandsale" -> countryInformation.purchaseAndSale = getResourceString(context, parser.nextText())
        "enforcement" -> countryInformation.enforcement = getResourceString(context, parser.nextText())
        "lastupdate" -> countryInformation.lastUpdate = getResourceString(context, parser.nextText())
      }
    }
    eventType = parser.next()
  }
  return countryInformation
}

fun getResourceString(context: Context, resourceId: String): String {
  return if (resourceId.startsWith("@string/")) {
    val resName = resourceId.substring(8)
    val resId = context.resources.getIdentifier(resName, "string", context.packageName)
    context.getString(resId)
  } else {
    resourceId
  }
}
