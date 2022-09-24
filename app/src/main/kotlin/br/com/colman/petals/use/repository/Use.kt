package br.com.colman.petals.use.repository

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Use(
  val date: LocalDateTime = LocalDateTime.now(),

  val amountGrams: BigDecimal = BigDecimal.ZERO,

  val costPerGram: BigDecimal = BigDecimal.ZERO,

  var id: Long = 0
) {

  @Transient
  val localDate = date.toLocalDate()

  fun columns(): List<String> = listOf(
    date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    amountGrams.toPlainString(),
    costPerGram.toPlainString(),
    id.toString() // FIXME ThinkMe: should this be included?
  )
}

val List<Use>.totalGrams
  get() = map { it.amountGrams }.fold(BigDecimal.ZERO, BigDecimal::add)

val List<Use>.totalCost
  get() = map { it.costPerGram * it.amountGrams }.fold(BigDecimal.ZERO, BigDecimal::add)

val List<Use>.minGrams
  get() = minOfOrNull { it.amountGrams }?.toDouble() ?: 0.0

val List<Use>.maxGrams
  get() = maxOfOrNull { it.amountGrams }?.toDouble() ?: 0.0

val List<Use>.averageGrams
  get() = map { it.amountGrams.toDouble() }.average()
