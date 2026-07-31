package br.com.colman.petals.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

// Contrast ratios in the comments are WCAG 2.1 against this theme's background.
// Text-bearing pairs target 4.5:1, plain UI components 3:1. See COLOR_GUIDE.md.
val darkColors = darkColors(
  primary = Color(0xFF879017), // 5.05:1
  primaryVariant = Color(0xFF717817), // 3.68:1
  secondary = Color(0xFFC3488F), // 3.89:1
  secondaryVariant = Color(0xFFC3488F), // Material 2 wants these equal in dark theme
  background = Color(0xFF191919),
  surface = Color(0xFF191919),
  error = Color(0xFFCF72A8), // 5.55:1
  onPrimary = Color(0xFF191919),
  onSecondary = Color(0xFF191919),
  onBackground = Color(0xFFF2F2F2),
  onSurface = Color(0xFFF2F2F2),
  onError = Color(0xFF191919)
)

val lightColors = lightColors(
  primary = Color(0xFF5B6018), // 5.99:1
  primaryVariant = Color(0xFF454818), // 8.55:1
  secondary = Color(0xFFB71D76), // 5.44:1
  secondaryVariant = Color(0xFF821C57), // 8.33:1
  background = Color(0xFFF2F2F2),
  surface = Color(0xFFF2F2F2),
  error = Color(0xFF9D1C66), // 6.72:1
  onPrimary = Color(0xFFF2F2F2),
  onSecondary = Color(0xFFF2F2F2),
  onBackground = Color(0xFF191919),
  onSurface = Color(0xFF191919),
  onError = Color(0xFFF2F2F2)
)

/**
 * Haze that fills the hit timer as the hold runs on. It has to follow the theme: a single fixed gray
 * is a dark scrim under the light palette, which sinks the countdown text to 1.16:1. Each variant is
 * one step away from its own background, so the effect still reads as smoke rolling in.
 */
val Colors.smoke: Color get() = if (isLight) Color(0xFFD0D0D0) else Color(0xFF303030)
