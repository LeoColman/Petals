package br.com.colman.petals.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.text.NumberFormat
import java.util.Locale

/**
 * The language the app is showing, which string resources follow. It can differ from [Locale.getDefault] after the
 * language is changed in the settings, so numbers formatted into a string resource should follow this one.
 */
@Composable
@ReadOnlyComposable
fun currentLocale(): Locale = LocalConfiguration.current.locales[0]

/** [value] to [decimals] places, rounded half up, as [locale] writes numbers: "1,234.50", or "1.234,50" in German. */
fun decimalText(value: BigDecimal, decimals: Int, locale: Locale): String =
  NumberFormat.getNumberInstance(locale).apply {
    minimumFractionDigits = decimals
    maximumFractionDigits = decimals
    roundingMode = HALF_UP
  }.format(value)
