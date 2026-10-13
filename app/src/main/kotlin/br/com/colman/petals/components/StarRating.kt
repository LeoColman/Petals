package br.com.colman.petals.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.StarHalf
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string.not_rated
import br.com.colman.petals.R.string.rating_out_of_five
import br.com.colman.petals.use.repository.Rating
import java.math.RoundingMode.HALF_UP
import java.text.NumberFormat
import java.util.Locale

/**
 * Five stars showing [rating], in halves. Given [onRatingChange], tapping the first half of a star rates half a star
 * short of it and the second half rates the whole star, while tapping the rating it already has clears it. Screen
 * readers hear [description] and, when it can be changed, adjust it half a star at a time like a slider.
 */
@Composable
fun StarRating(
  rating: Double?,
  modifier: Modifier = Modifier,
  starSize: Dp = 24.dp,
  description: String = ratingDescription(rating),
  onRatingChange: ((Double?) -> Unit)? = null
) {
  val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
  val onChange by rememberUpdatedState(onRatingChange)
  val semantics = Modifier.semantics(mergeDescendants = true) {
    if (onRatingChange == null) {
      contentDescription = description
    } else {
      stateDescription = description
      progressBarRangeInfo = ProgressBarRangeInfo((rating ?: 0.0).toFloat(), 0f..Rating.Max.toFloat(), HalfSteps - 1)
      setProgress { stars ->
        onChange?.invoke(Rating.ofOrNull(stars.toDouble()))
        true
      }
    }
  }

  Row(modifier.then(semantics)) {
    for (star in 1..Stars) {
      val icon = when {
        rating == null || rating <= star - 1 -> Icons.Rounded.StarBorder
        rating >= star -> Icons.Rounded.Star
        else -> Icons.AutoMirrored.Rounded.StarHalf
      }
      val tint = if (icon == Icons.Rounded.StarBorder) {
        LocalContentColor.current.copy(alpha = ContentAlpha.medium)
      } else {
        MaterialTheme.colors.primary
      }
      // Each star reads its own taps, so a tap always rates the star drawn under it, whatever size the stars end up.
      val taps = if (onRatingChange == null) {
        Modifier
      } else {
        Modifier.pointerInput(star, rating, isRtl) {
          detectTapGestures { offset ->
            onChange?.invoke(ratingAfterTap(ratingAt(star, offset.x, size.width, isRtl), rating))
          }
        }
      }
      Icon(icon, null, Modifier.size(starSize).then(taps), tint = tint)
    }
  }
}

/** "3.5 of 5 stars", or "Not rated". */
@Composable
fun ratingDescription(rating: Double?): String =
  rating?.let { stringResource(rating_out_of_five, ratingNumber(it)) } ?: stringResource(not_rated)

/**
 * [stars] to one decimal at most, as the user's language writes numbers: "4", "3.5", or "3,5" in German. Screen
 * readers read it out, so it follows the locale where the CSV's [Rating.format] doesn't.
 */
fun ratingNumber(stars: Double, locale: Locale = Locale.getDefault()): String =
  NumberFormat.getNumberInstance(locale).apply {
    maximumFractionDigits = 1
    roundingMode = HALF_UP
  }.format(stars)

private val Stars = Rating.Max.toInt()
private val HalfSteps = Stars * 2

/**
 * The rating a tap at [x] on [star], [width] wide, gives: half a star short of it in the star's first half in reading
 * order, which is its right half in a right-to-left language, and the whole star in its second half.
 */
internal fun ratingAt(star: Int, x: Float, width: Int, isRtl: Boolean): Double {
  val isFirstHalf = (x < width / 2f) != isRtl
  return if (isFirstHalf) star - 0.5 else star.toDouble()
}

/** The rating after tapping [tapped] when it is [current]: [tapped], or none when it already was that. */
internal fun ratingAfterTap(tapped: Double, current: Double?): Double? = tapped.takeIf { it != current }
