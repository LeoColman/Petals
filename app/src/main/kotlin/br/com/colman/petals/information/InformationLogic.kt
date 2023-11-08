package br.com.colman.petals.information

import android.content.Context
import android.content.res.XmlResourceParser
import br.com.colman.petals.R
import org.xmlpull.v1.XmlPullParser
import java.util.Locale

// Retrieving the content of the general_knowledge xml
data class InfoItem(val title: String, val content: String)

fun getXmlGKResId(context: Context): Int {
  val locale = context.resources.configuration.locale ?: Locale.getDefault()
  val localizeXmlResId = when (locale.language) {
    Locale.ENGLISH.language -> R.xml.general_knowledge_en
    else -> R.xml.general_knowledge_en
  }
  return localizeXmlResId
}

fun parseXmlGenKnowledge(context: Context): List<InfoItem> {
  val infoItems = mutableListOf<InfoItem>()
  val locale = context.resources.configuration.locale ?: Locale.getDefault()
  val localizeXmlResId = getXmlGKResId(context)
  val parser: XmlResourceParser = context.resources.getXml(localizeXmlResId)
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

// Retrieving all the countries that are in the legislation_and_rights xml
data class CountryItem(val name: String)

fun getCountriesList(context: Context): List<CountryItem> {
  val countries = mutableListOf<CountryItem>()
  val locale = context.resources.configuration.locale ?: Locale.getDefault()
  val localizeXmlResId = when (locale.language) {
    Locale.ENGLISH.language -> R.xml.legislation_and_rights_en
    else -> R.xml.legislation_and_rights_en
  }

  val parser: XmlResourceParser = context.resources.getXml(localizeXmlResId)

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
          countries.add(CountryItem(name = countryName))
          countryName = null
        }
        currentTag = null
      }
    }
    eventType = parser.next()
  }
  return countries
}

// Searching in the legislation_and_rights xml
// Returning the information for the specific country

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

fun getXmlCIResId(context: Context): Int {
  val locale = context.resources.configuration.locale ?: Locale.getDefault()
  val localizeXmlResId = when (locale.language) {
    Locale.ENGLISH.language -> R.xml.legislation_and_rights_en
    else -> R.xml.legislation_and_rights_en
  }
  return localizeXmlResId
}

fun getCountryInformation(context: Context, countryNameToFind: String): CountryInformation? {
  val localizeXmlResId = getXmlCIResId(context)
  val parser: XmlResourceParser = context.resources.getXml(localizeXmlResId)
  var eventType = parser.eventType
  val countryInformation = CountryInformation("", "", "", "", "", "", "", "", "")
  var countryName = ""
  while (eventType != XmlPullParser.END_DOCUMENT) {
    if (eventType == XmlPullParser.START_TAG && parser.name == "country") {
      countryName = parser.nextText()
      if (countryName.equals(countryNameToFind, ignoreCase = true)) {
        countryInformation.countryName = countryName
        break
      }
    }
    eventType = parser.next()
  }
  return buildCountryInformation(parser, countryInformation)
}

fun buildCountryInformation(
  parser: XmlResourceParser,
  countryInformation: CountryInformation
): CountryInformation {
  var eventType = parser.next()
  while (eventType != XmlPullParser.END_DOCUMENT && parser.name != "item") {
    if (eventType == XmlPullParser.START_TAG) {
      when (parser.name) {
        "legalstatus" -> countryInformation.legalStatus = parser.nextText()
        "possession" -> countryInformation.possession = parser.nextText()
        "consumption" -> countryInformation.consumption = parser.nextText()
        "medicaluse" -> countryInformation.medicalUse = parser.nextText()
        "cultivation" -> countryInformation.cultivation = parser.nextText()
        "purchaseandsale" -> countryInformation.purchaseAndSale = parser.nextText()
        "enforcement" -> countryInformation.enforcement = parser.nextText()
        "lastupdate" -> countryInformation.lastUpdate = parser.nextText()
      }
    }
    eventType = parser.next()
  }
  return countryInformation
}
