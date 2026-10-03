package br.com.colman.petals.use

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import br.com.colman.petals.R.string.add_use
import br.com.colman.petals.R.string.add_use_during_pause_alert
import br.com.colman.petals.R.string.later
import br.com.colman.petals.R.string.no
import br.com.colman.petals.R.string.ok
import br.com.colman.petals.R.string.support_my_work
import br.com.colman.petals.R.string.support_now
import br.com.colman.petals.R.string.thank_your_for_using_message
import br.com.colman.petals.R.string.yes
import br.com.colman.petals.R.string.yes_timer
import br.com.colman.petals.review.ReviewAppRequester
import br.com.colman.petals.use.repository.Use
import br.com.colman.petals.use.repository.UseRepository
import br.com.colman.petals.widgets.updateWidget
import compose.icons.TablerIcons
import compose.icons.tablericons.Lock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal.ZERO
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Preview
@Composable
fun AddUseButton(isAnyPauseActive: Boolean = false, onClick: () -> Unit = { }) {
  Button(onClick) {
    Text(stringResource(add_use))
    if (isAnyPauseActive) {
      Icon(TablerIcons.Lock, contentDescription = null)
    }
  }
}

/**
 * A use about to be added, its form prefilled from [template], or left blank when there is none.
 */
data class AddUseRequest(val template: Use?)

/**
 * Takes [request] through to a saved use: the pause confirmation when a pause is active, the prefilled form, then the
 * widget refresh and the support or review milestone. Adding and duplicating both go through here, so neither skips
 * the pause.
 */
@Composable
fun AddUseFlow(
  request: AddUseRequest?,
  isAnyPauseActive: Boolean,
  reviewAppRequester: ReviewAppRequester,
  repository: UseRepository,
  onFinish: () -> Unit
) {
  var openSupportDialog by remember { mutableStateOf(false) }
  val context = LocalContext.current
  val activity = context.getActivity()
  val totalUseCount by repository.countAll().collectAsState(0)
  val scope = rememberCoroutineScope()

  if (request != null) {
    var isPauseConfirmed by remember(request) { mutableStateOf(!isAnyPauseActive) }

    if (!isPauseConfirmed) {
      ConfirmAddUseDuringPauseDialog(onFinish) { isPauseConfirmed = true }
    } else {
      AddUseDialog(request.template, {
        repository.upsert(it)

        scope.launch {
          context.updateWidget(it)
        }

        when (useMilestone(totalUseCount)) {
          UseMilestone.SupportDeveloper -> openSupportDialog = true
          UseMilestone.RequestReview -> activity?.let { activity -> reviewAppRequester.requestReview(activity) }
          null -> Unit
        }
      }, onFinish)
    }
  }

  if (openSupportDialog) {
    SupportDeveloperDialog({ openSupportDialog = false }) {
      openSupportDialog = false
      context.launchKofi()
    }
  }
}

internal enum class UseMilestone { SupportDeveloper, RequestReview }

/**
 * What to show after a use is added, given how many uses were logged before it.
 * Every 42nd asks for support; every 100th asks for a review, unless it is also a 42nd.
 */
internal fun useMilestone(totalUseCount: Int): UseMilestone? = when {
  totalUseCount <= 0 -> null
  totalUseCount % 42 == 0 -> UseMilestone.SupportDeveloper
  totalUseCount % 100 == 0 -> UseMilestone.RequestReview
  else -> null
}

@Composable
@Preview
private fun SupportDeveloperDialog(
  onDismiss: () -> Unit = {},
  onConfirm: () -> Unit = {}
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(support_my_work)) },
    text = { Text(stringResource(thank_your_for_using_message), fontWeight = FontWeight.Bold) },
    confirmButton = {
      TextButton(onConfirm) {
        Text(stringResource(support_now))
      }
    },
    dismissButton = {
      TextButton(onDismiss) {
        Text(
          stringResource(later),
          color = Color.LightGray
        )
      }
    }
  )
}

@Composable
@Preview
private fun ConfirmAddUseDuringPauseDialog(
  onDismiss: () -> Unit = {},
  onConfirm: () -> Unit = {}
) {
  var yesTimer by remember { mutableStateOf(10) }
  var yesEnabled by remember { mutableStateOf(yesTimer == 0) }

  LaunchedEffect(yesTimer) {
    while (yesTimer > 0) {
      delay(1000)
      yesTimer--
      yesEnabled = yesTimer == 0
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = { Text(stringResource(add_use_during_pause_alert)) },
    confirmButton = {
      TextButton(onConfirm, enabled = yesEnabled) {
        if (yesEnabled) {
          Text(stringResource(yes))
        } else {
          Text(stringResource(yes_timer, yesTimer))
        }
      }
    },
    dismissButton = { TextButton(onDismiss) { Text(stringResource(no)) } }
  )
}

@Composable
@Preview
private fun AddUseDialog(
  previousUse: Use? = null,
  onAddUse: (Use) -> Unit = {},
  onDismiss: () -> Unit = {}
) {
  val amount = remember { mutableStateOf(previousUse?.amountGrams?.toString().orEmpty()) }
  val costPerGram = remember { mutableStateOf(previousUse?.costPerGram?.toString().orEmpty()) }
  val date = remember { mutableStateOf(LocalDate.now()) }
  val time = remember { mutableStateOf(LocalTime.now()) }
  val description = remember { mutableStateOf(previousUse?.description.orEmpty()) }
  val consumptionMethod = remember { mutableStateOf(previousUse?.consumptionMethod) }

  val use = Use(
    LocalDateTime.of(date.value, time.value),
    amount.value.toBigDecimalOrNull() ?: ZERO,
    costPerGram.value.toBigDecimalOrNull() ?: ZERO,
    description = description.value,
    consumptionMethod = consumptionMethod.value
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    text = { AddUseForm(amount, costPerGram, date, time, description, consumptionMethod) },
    confirmButton = { ConfirmNewUseButton(onAddUse, use, onDismiss) }
  )
}

@Composable
private fun ConfirmNewUseButton(
  onAddUse: (Use) -> Unit = {},
  use: Use = Use(),
  onDismiss: () -> Unit = {}
) {
  TextButton({
    onAddUse(use)
    onDismiss()
  }) {
    Text(stringResource(ok))
  }
}

private const val KofiUrl = "https://ko-fi.com/leocolman"

private fun Context.launchKofi() {
  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(KofiUrl))
  intent.resolveActivity(packageManager)?.let {
    startActivity(intent)
  }
}

fun Context.getActivity(): Activity? {
  var currentContext = this
  while (currentContext is ContextWrapper) {
    if (currentContext is Activity) {
      return currentContext
    }
    currentContext = currentContext.baseContext
  }
  return null
}
