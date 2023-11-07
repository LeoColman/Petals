package br.com.colman.petals.information

import ExpandableComponent
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.colman.petals.R
import br.com.colman.petals.R.string.consumption
import br.com.colman.petals.R.string.cultivation
import br.com.colman.petals.R.string.enforcement
import br.com.colman.petals.R.string.general_knowledge
import br.com.colman.petals.R.string.last_updated
import br.com.colman.petals.R.string.legal_status
import br.com.colman.petals.R.string.legislation_and_rights
import br.com.colman.petals.R.string.medical_use
import br.com.colman.petals.R.string.no_country_selected
import br.com.colman.petals.R.string.possession
import br.com.colman.petals.R.string.purchase_and_sale
import br.com.colman.petals.R.string.select_country

@Composable
fun InformationView() {
  val context = LocalContext.current
  val generalKnowledgeList = parseXmlGenKnowledge(context)
  Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
    SectionHeader(text = stringResource(general_knowledge))
    generalKnowledgeList.forEach { generalKnowledge ->
      ExpandableComponent(
        title = generalKnowledge.title,
        content = { ParseGenContent(generalKnowledge.content) }
      )
    }
    SectionHeader(text = stringResource(legislation_and_rights))
    CountryPicker(context)
  }
}

@Composable
fun ParseGenContent(text: String) {
  val content = text.split("\n")
  Column {
    content.forEach { item ->
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

@Composable
fun CountryPicker(context: Context) {
  var expanded by remember { mutableStateOf(false) }
  var selectedCountry by remember { mutableStateOf("") }
  val countries = getCountriesList(context)

  Column {
    OutlinedButton(
      onClick = { expanded = true },
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
    ) {
      Text(
        text = if (selectedCountry == "") {
          stringResource(select_country)
        } else {
          selectedCountry
        },
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
      countries.forEach { s ->
        DropdownMenuItem(
          onClick = {
            selectedCountry = s.name
            expanded = false
          },
        ) {
          Text(
            text = s.name
          )
        }
      }
    }

    if (selectedCountry == "") {
      Card(
        elevation = 4.dp,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).padding(horizontal = 16.dp)
      ) {
        Text(
          text = stringResource(no_country_selected),
          modifier = Modifier
            .padding(8.dp)
        )
      }
    } else {
      CountryLegislationAndRights(context = context, country = selectedCountry)
    }
  }
}

@Composable
fun CountryLegislationAndRights(context: Context, country: String) {
  val countryInformation = getCountryInformation(context, country)

  ExpandableComponent(
    title = stringResource(legal_status),
    { ParseGenContent(countryInformation!!.legalStatus) }
  )
  ExpandableComponent(
    title = stringResource(possession),
    { ParseGenContent(countryInformation!!.possession) }
  )
  ExpandableComponent(
    title = stringResource(consumption),
    { ParseGenContent(countryInformation!!.consumption) }
  )
  ExpandableComponent(
    title = stringResource(medical_use),
    { ParseGenContent(countryInformation!!.medicalUse) }
  )
  ExpandableComponent(
    title = stringResource(cultivation),
    { ParseGenContent(countryInformation!!.cultivation) }
  )
  ExpandableComponent(
    title = stringResource(purchase_and_sale),
    { ParseGenContent(countryInformation!!.purchaseAndSale) }
  )
  ExpandableComponent(
    title = stringResource(enforcement),
    { ParseGenContent(countryInformation!!.enforcement) }
  )

  Text(
    text = stringResource(R.string.keep_in_mind_that_laws_can_change),
    style = TextStyle(fontStyle = FontStyle.Italic),
    modifier = Modifier
      .padding(8.dp)
  )

  Text(
    text = stringResource(last_updated) + ": " + countryInformation!!.lastUpdate,
    style = TextStyle(fontStyle = FontStyle.Italic),
    modifier = Modifier
      .padding(8.dp)
  )
}
