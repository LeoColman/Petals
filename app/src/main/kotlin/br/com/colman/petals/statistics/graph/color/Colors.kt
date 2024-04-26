package br.com.colman.petals.statistics.graph.color

import androidx.compose.ui.graphics.Color

val colors = mapOf(
  0 to Color.Green,
  7 to Color.Blue,
  14 to Color.Yellow,
  28 to Color.Red,
  30 to Color.Red,
  56 to Color.Gray,
  60 to Color.Gray,
  84 to Color.DarkGray,
  90 to Color.DarkGray
).withDefault { Color.Red }

fun createColor(days: Int): Color {
  return colors.getValue(days)
}
