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
import kotlin.math.ceil

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
  val semantics = Modifier.semantics(mergeDescendants = true) {
    if (onRatingChange == null) {
      contentDescription = description
    } else {
      stateDescription = description
      progressBarRangeInfo = ProgressBarRangeInfo((rating ?: 0.0).toFloat(), 0f..Rating.Max.toFloat(), steps = 9)
      setProgress { stars ->
        onRatingChange(Rating.ofOrNull(stars.toDouble()))
        true
      }
    }
  }
  val taps = if (onRatingChange == null) {
    Modifier
  } else {
    Modifier.pointerInput(rating, isRtl) {
      detectTapGestures { offset ->
        val fraction = offset.x / size.width
        onRatingChange(ratingAfterTap(ratingAt(if (isRtl) 1 - fraction else fraction), rating))
      }
    }
  }

  Row(modifier.then(semantics).then(taps)) {
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
      Icon(icon, null, Modifier.size(starSize), tint = tint)
    }
  }
}

/** "3.5 of 5 stars", or "Not rated". */
@Composable
fun ratingDescription(rating: Double?): String =
  rating?.let { stringResource(rating_out_of_five, Rating.format(it)) } ?: stringResource(not_rated)

private const val Stars = 5

/**
 * The rating a tap gives at [fraction] of the way along the stars, in reading order: each star's first half is half a
 * star short of it, its second half the whole star.
 */
internal fun ratingAt(fraction: Float): Double = ceil(fraction.coerceIn(0f, 1f) * Stars * 2).coerceIn(1f, 10f) / 2.0

/** The rating after tapping [tapped] when it is [current]: [tapped], or none when it already was that. */
internal fun ratingAfterTap(tapped: Double, current: Double?): Double? = tapped.takeIf { it != current }
