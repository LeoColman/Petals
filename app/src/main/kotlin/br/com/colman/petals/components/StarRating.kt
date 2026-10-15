package br.com.colman.petals.components

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import br.com.colman.petals.R.string.add_half_star
import br.com.colman.petals.R.string.clear_rating
import br.com.colman.petals.R.string.not_rated
import br.com.colman.petals.R.string.rating_out_of_five
import br.com.colman.petals.use.repository.Rating
import br.com.colman.petals.utils.currentLocale
import java.math.RoundingMode.HALF_UP
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.floor

/**
 * Five stars showing [rating], in halves. Given [onRatingChange], they work like a slider: tapping the first half of a
 * star rates half a star short of it and the second half rates the whole star, dragging along them rates the half star
 * under the finger, and tapping the rating it already has clears it. Each star then takes at least a 48dp touch target.
 * Screen readers hear [label] and [description] and adjust it half a star at a time, or add half a star with a double
 * tap; when it can't be changed they hear [description] alone.
 */
@Suppress("LongParameterList")
@Composable
fun StarRating(
  rating: Double?,
  modifier: Modifier = Modifier,
  starSize: Dp = 24.dp,
  description: String = ratingDescription(rating),
  label: String? = null,
  onRatingChange: ((Double?) -> Unit)? = null
) {
  val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
  val currentRating by rememberUpdatedState(rating)
  val onChange by rememberUpdatedState(onRatingChange)
  val clickLabel = stringResource(if (rating == Rating.Max) clear_rating else add_half_star)
  val semantics = Modifier.semantics(mergeDescendants = true) {
    if (onRatingChange == null) {
      contentDescription = description
    } else {
      label?.let { contentDescription = it }
      stateDescription = description
      progressBarRangeInfo = ProgressBarRangeInfo((rating ?: 0.0).toFloat(), 0f..Rating.Max.toFloat(), HalfSteps - 1)
      // Out of range is clamped, as a slider does, so "set to maximum" gives five stars. Zero, the bottom of the
      // range, is unrated.
      setProgress { stars ->
        val changed = Rating.ofOrNull(stars.coerceIn(0f, Rating.Max.toFloat()).toDouble())
        if (changed == currentRating) return@setProgress false
        onChange?.invoke(changed)
        true
      }
      // A screen reader's double tap, or a Switch Access or Voice Access tap, adds half a star and clears five, so the
      // rating can be set without swiping. Without a click of its own, a double tap would land as a tap in the middle
      // of the stars, rating or clearing the third one.
      onClick(clickLabel) {
        onChange?.invoke(ratingAfterClick(currentRating))
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
      // Each star reads its own touches, so a tap always rates the star drawn under it, whatever size the stars end up.
      // A drag stays with the star it started on, which works out the star it reached from how far it went.
      val touches = if (onRatingChange == null) {
        Modifier
      } else {
        Modifier
          .pointerInput(star, isRtl) {
            detectTapGestures { offset ->
              onChange?.invoke(ratingAfterTap(ratingAt(star, offset.x, size.width, isRtl), currentRating))
            }
          }
          .pointerInput(star, isRtl) {
            detectHorizontalDragGestures { change, _ ->
              onChange?.invoke(ratingAt(star, change.position.x, size.width, isRtl))
            }
          }
          .minimumInteractiveComponentSize()
      }
      Icon(icon, null, touches.size(starSize), tint = tint)
    }
  }
}

/** "3.5 of 5 stars", or "Not rated". */
@Composable
fun ratingDescription(rating: Double?): String =
  rating?.let { stringResource(rating_out_of_five, ratingNumber(it, currentLocale())) } ?: stringResource(not_rated)

/**
 * [stars] to one decimal at most, as [locale] writes numbers: "4", "3.5", or "3,5" in German. Screen readers read it
 * out, so it follows the app's language where the CSV's [Rating.format] doesn't.
 */
fun ratingNumber(stars: Double, locale: Locale): String =
  NumberFormat.getNumberInstance(locale).apply {
    maximumFractionDigits = 1
    roundingMode = HALF_UP
  }.format(stars)

private val Stars = Rating.Max.toInt()
private val HalfSteps = Stars * 2

/**
 * The rating a touch at [x] on [star], [width] wide, gives: half a star short of it in the star's first half in reading
 * order, which is its right half in a right-to-left language, and the whole star in its second half. A drag that left
 * the star goes on counting half stars of the same width, and stops at half a star and at five.
 */
internal fun ratingAt(star: Int, x: Float, width: Int, isRtl: Boolean): Double {
  val fromStart = if (isRtl) width - x else x
  val halves = floor(fromStart / (width / 2f)).toInt() + 1
  return (star - 1 + halves / 2.0).coerceIn(0.5, Rating.Max)
}

/** The rating after tapping [tapped] when it is [current]: [tapped], or none when it already was that. */
internal fun ratingAfterTap(tapped: Double, current: Double?): Double? = tapped.takeIf { it != current }

/** The rating after an accessibility click when it is [current]: half a star more, or none after five. */
internal fun ratingAfterClick(current: Double?): Double? = ((current ?: 0.0) + 0.5).takeIf { it <= Rating.Max }
