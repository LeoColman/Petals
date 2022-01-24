package br.com.colman.petals.use

import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.icons.TablerIcons
import compose.icons.tablericons.CurrencyDollar
import compose.icons.tablericons.ReportMoney
import compose.icons.tablericons.Scale
import compose.icons.tablericons.Smoking
import java.time.format.DateTimeFormatter.ofPattern


@Preview
@Composable
fun UseCards(uses: List<Use> = List(5) { Use() }) {
  Column(Modifier.fillMaxWidth(), spacedBy(8.dp)) {
    uses.sortedByDescending { it.date }.forEach {
      UseCard(it)
    }
  }
}

@Preview
@Composable
fun UseCard(use: Use = Use()) {
  val (amountGrams, costPerGram, date) = use
  val dateString = date.format(ofPattern("yyyy/MM/dd"))
  val timeString = date.format(ofPattern("HH:mm:ss"))

  Card(Modifier.padding(8.dp).fillMaxWidth(), elevation = 3.dp) {
    Column(Modifier.padding(24.dp), spacedBy(16.dp)) {
      Row(Modifier, spacedBy(8.dp), CenterVertically) {
        Icon(TablerIcons.Smoking, null)
        Text("$dateString at $timeString")
      }

      Row(Modifier, spacedBy(8.dp), CenterVertically) {
        Icon(TablerIcons.CurrencyDollar, null)
        val costPerGramString = costPerGram.setScale(3).toString()
        Text("$costPerGramString per gram")
      }

      Row(Modifier, spacedBy(8.dp), CenterVertically) {
        Icon(TablerIcons.Scale, null)
        val amountGramsString = amountGrams.setScale(3).toString()
        Text("$amountGramsString grams")
      }

      Row(Modifier, spacedBy(8.dp), CenterVertically) {
        Icon(TablerIcons.ReportMoney, null)
        val total = (amountGrams * costPerGram).setScale(3)
        Text("$total total")
      }
    }
  }
}