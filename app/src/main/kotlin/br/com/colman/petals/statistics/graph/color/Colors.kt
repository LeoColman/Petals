package br.com.colman.petals.statistics.graph.color

import androidx.compose.ui.graphics.Color

val colors = mapOf(
  0 to Color.Green,
  7 to Color.Blue,
  14 to Color.Yellow,
  30 to Color.Red,
  60 to Color.Gray,
  90 to Color.DarkGray
).withDefault { Color.Red }

fun createColor(days: Int): Color {
  return colors.getValue(days)
}
