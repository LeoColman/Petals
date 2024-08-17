package br.com.colman.petals.information

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.R.string.consumption
import br.com.colman.petals.R.string.cultivation
import br.com.colman.petals.R.string.enforcement
import br.com.colman.petals.R.string.general_knowledge
import br.com.colman.petals.R.string.keep_in_mind_that_laws_can_change
import br.com.colman.petals.R.string.last_updated
import br.com.colman.petals.R.string.legal_status
import br.com.colman.petals.R.string.legislation_and_rights
import br.com.colman.petals.R.string.medical_use
import br.com.colman.petals.R.string.no_country_selected
import br.com.colman.petals.R.string.possession
import br.com.colman.petals.R.string.purchase_and_sale
import br.com.colman.petals.R.string.select_country
import br.com.colman.petals.components.ExpandableComponent

@Composable
fun InformationView() {
  val context = LocalContext.current
  val generalKnowledgeList = parseXmlGenKnowledge(context)
  Column(Modifier.verticalScroll(rememberScrollState()).testTag("InformationViewMainColumn")) {
    SectionHeader(stringResource(general_knowledge))

    generalKnowledgeList.forEach { (title, text) ->
      ExpandableComponent(title) {
        KnowledgeContent(text)
      }
    }

    SectionHeader(stringResource(legislation_and_rights))

    CountryPicker(context)
  }
}

@Composable
fun KnowledgeContent(text: String) {
  val content = text.split("\n")
  Column {
    content.forEach { item ->
      Text(bullettedItem(item), Modifier.padding(4.dp))
    }
  }
}

private fun bullettedItem(item: String) = "•" + item.trim()

@Composable
fun SectionHeader(text: String) {
  Text(
    text,
    Modifier.fillMaxWidth().wrapContentWidth(CenterHorizontally).padding(vertical = 32.dp),
    style = MaterialTheme.typography.headlineSmall
  )
}

@Composable
fun CountryPicker(context: Context) {
  var expanded by remember { mutableStateOf(false) }
  val expand = { expanded = true }
  val collapse = { expanded = false }

  var selectedCountry by remember { mutableStateOf("") }
  val countries = getCountriesList(context)
  val selectCountry = { country: String -> selectedCountry = country }

  Column {
    OutlinedButton(expand, Modifier.fillMaxWidth().padding(horizontal = 16.dp).testTag("SelectCountry")) {
      Text(
        selectedCountry.ifEmpty { stringResource(select_country) },
        Modifier.padding(4.dp),
        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
      )
    }

    DropdownMenu(expanded, collapse, Modifier.fillMaxWidth(0.9f).padding(8.dp)) {
      countries.forEach { country ->
        DropdownMenuItem(onClick = {
          selectCountry(country.name)
          collapse()
        }, modifier = Modifier.testTag("DropdownMenuItem"), text = {
          Text(country.name)
        })
      }
    }

    if (selectedCountry == "") {
      Card(Modifier.fillMaxWidth().padding(top = 8.dp).padding(horizontal = 16.dp)) {
        Text(stringResource(no_country_selected), Modifier.padding(8.dp))
      }
    } else {
      CountryLegislationAndRights(context = context, country = selectedCountry)
    }
  }
}

@Composable
fun CountryLegislationAndRights(context: Context, country: String) {
  val countryInformation = getCountryInformation(context, country)

  ExpandableComponent(stringResource(legal_status)) { KnowledgeContent(countryInformation.legalStatus) }

  ExpandableComponent(stringResource(possession)) { KnowledgeContent(countryInformation.possession) }

  ExpandableComponent(stringResource(consumption)) { KnowledgeContent(countryInformation.consumption) }

  ExpandableComponent(stringResource(medical_use)) { KnowledgeContent(countryInformation.medicalUse) }

  ExpandableComponent(stringResource(cultivation)) { KnowledgeContent(countryInformation.cultivation) }

  ExpandableComponent(stringResource(purchase_and_sale)) { KnowledgeContent(countryInformation.purchaseAndSale) }

  ExpandableComponent(stringResource(enforcement)) { KnowledgeContent(countryInformation.enforcement) }

  Text(
    stringResource(keep_in_mind_that_laws_can_change),
    Modifier.padding(8.dp),
    style = TextStyle(fontStyle = FontStyle.Italic)
  )

  Text(
    stringResource(last_updated, countryInformation.lastUpdate),
    Modifier.padding(8.dp),
    style = TextStyle(fontStyle = FontStyle.Italic)
  )
}
