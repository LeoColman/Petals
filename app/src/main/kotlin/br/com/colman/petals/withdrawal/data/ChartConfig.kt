package br.com.colman.petals.withdrawal.data

import androidx.annotation.StringRes
import br.com.colman.petals.R
import br.com.colman.petals.R.string.current_decreased_appetite
import br.com.colman.petals.R.string.current_irritability
import br.com.colman.petals.R.string.current_nervousness_anxiety
import br.com.colman.petals.R.string.current_restlessness
import br.com.colman.petals.R.string.current_sleep_difficulty
import br.com.colman.petals.R.string.current_thc_concentration
import br.com.colman.petals.R.string.current_withdrawal_discomfort
import br.com.colman.petals.R.string.days
import br.com.colman.petals.R.string.decreased_appetite
import br.com.colman.petals.R.string.discomfort_strength
import br.com.colman.petals.R.string.irritability
import br.com.colman.petals.R.string.nervousness_anxiety
import br.com.colman.petals.R.string.restlessness
import br.com.colman.petals.R.string.sleep_difficulty
import br.com.colman.petals.R.string.thc_concentration
import java.time.Duration

sealed class ChartConfig(
  val data: Map<Duration, Double>,
  @StringRes val title: Int,
  @StringRes val verticalAxisTitle: Int,
  @StringRes val horizontalAxisTitle: Int,
  val maxX: Double = 25.0,
  val maxY: Double = 10.0
) {

  private data object ThcConcentration : ChartConfig(
    ThcConcentrationDataPoints,
    current_thc_concentration,
    thc_concentration,
    days,
    20.0,
    100.0
  )
  private data object Discomfort : ChartConfig(
    DiscomfortDataPoints,
    current_withdrawal_discomfort,
    discomfort_strength,
    days
  )
  private data object DecreasedAppetite : ChartConfig(
    DecreasedAppetiteDataPoints,
    current_decreased_appetite,
    decreased_appetite,
    days
  )
  private data object Irritability : ChartConfig(IrritabilityDataPoints, current_irritability, irritability, days)
  private data object NervousnessAnxiety : ChartConfig(
    NervousnessAnxietyDataPoints,
    current_nervousness_anxiety,
    nervousness_anxiety,
    days
  )
  private data object Anger : ChartConfig(AngerDataPoints, R.string.current_anger, R.string.anger, days)
  private data object Restlessness : ChartConfig(RestlessnessDataPoints, current_restlessness, restlessness, days)
  private data object SleepDifficulty : ChartConfig(
    SleepDifficultyDataPoints,
    current_sleep_difficulty,
    sleep_difficulty,
    days
  )

  companion object {
    fun entries(): List<ChartConfig> =
      listOf(
        ThcConcentration,
        Discomfort,
        DecreasedAppetite,
        Irritability,
        NervousnessAnxiety,
        Anger,
        Restlessness,
        SleepDifficulty
      )
  }
}
