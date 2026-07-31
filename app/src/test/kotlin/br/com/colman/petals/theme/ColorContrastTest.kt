package br.com.colman.petals.theme

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThan
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

private const val TextMinimum = 4.5
private const val ComponentMinimum = 3.0

private data class ContrastCase(
  val name: String,
  val foreground: Color,
  val background: Color,
  val minimum: Double
)

private fun textCases(theme: String, colors: Colors) = listOf(
  ContrastCase("$theme onBackground/background", colors.onBackground, colors.background, TextMinimum),
  ContrastCase("$theme onSurface/surface", colors.onSurface, colors.surface, TextMinimum),
  ContrastCase("$theme onPrimary/primary", colors.onPrimary, colors.primary, TextMinimum),
  ContrastCase("$theme onError/error", colors.onError, colors.error, TextMinimum),
  ContrastCase("$theme primary/background", colors.primary, colors.background, TextMinimum),
  ContrastCase("$theme error/background", colors.error, colors.background, TextMinimum)
)

private fun componentCases(theme: String, colors: Colors) = listOf(
  ContrastCase("$theme primaryVariant/background", colors.primaryVariant, colors.background, ComponentMinimum),
  ContrastCase("$theme secondary/background", colors.secondary, colors.background, ComponentMinimum),
  ContrastCase("$theme secondaryVariant/background", colors.secondaryVariant, colors.background, ComponentMinimum),
  // Dark theme's pink sits at 3.89:1, above the component floor but below the text one. It backs
  // FABs and toggles rather than body text, so the component bar is the applicable one.
  ContrastCase("$theme onSecondary/secondary", colors.onSecondary, colors.secondary, ComponentMinimum),
  // The hit timer fades `smoke` in over the background until it is fully opaque, so whatever it
  // draws on top has to stay legible against the haze, not just against the background.
  ContrastCase("$theme error/smoke", colors.error, colors.smoke, ComponentMinimum),
  ContrastCase("$theme primary/smoke", colors.primary, colors.smoke, ComponentMinimum),
  ContrastCase("$theme onBackground/smoke", colors.onBackground, colors.smoke, TextMinimum)
)

/**
 * Guards the palette against contrast regressions (issue #903). Ratios follow WCAG 2.1: 4.5:1 for
 * anything that renders text, 3:1 for plain UI components such as chart strokes and toggles.
 */
class ColorContrastTest : FunSpec({

  val cases = textCases("dark", darkColors) + componentCases("dark", darkColors) +
    textCases("light", lightColors) + componentCases("light", lightColors)

  withData(
    nameFn = { "${it.name} is at least ${it.minimum}:1" },
    ts = cases
  ) { (_, foreground, background, minimum) ->
    contrastRatio(foreground, background) shouldBeGreaterThanOrEqualTo minimum
  }

  test("light primaryVariant is darker than primary, as Material 2 expects") {
    relativeLuminance(lightColors.primaryVariant) shouldBeLessThan relativeLuminance(lightColors.primary)
  }
})

private fun contrastRatio(a: Color, b: Color): Double {
  val first = relativeLuminance(a)
  val second = relativeLuminance(b)
  return (max(first, second) + 0.05) / (min(first, second) + 0.05)
}

private fun relativeLuminance(color: Color) =
  0.2126 * linearize(color.red) + 0.7152 * linearize(color.green) + 0.0722 * linearize(color.blue)

private fun linearize(channel: Float): Double {
  val value = channel.toDouble()
  return if (value <= 0.03928) value / 12.92 else ((value + 0.055) / 1.055).pow(2.4)
}
