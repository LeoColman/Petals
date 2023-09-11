package br.com.colman.petals.use.repository

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class Use(
  val date: LocalDateTime = LocalDateTime.now(),

  val amountGrams: BigDecimal = BigDecimal.ZERO,

  val costPerGram: BigDecimal = BigDecimal.ZERO,

  val id: String = UUID.randomUUID().toString()
) {

  @Transient
  val localDate: LocalDate = date.toLocalDate()

  fun columns(): List<String> = listOf(
    date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    amountGrams.toPlainString(),
    costPerGram.toPlainString(),
    id // FIXME ThinkMe: should this be included?
  )

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Use

    if (date != other.date) return false
    if (amountGrams != other.amountGrams) return false
    if (costPerGram != other.costPerGram) return false

    return true
  }

  override fun hashCode(): Int {
    var result = date.hashCode()
    result = 31 * result + amountGrams.hashCode()
    result = 31 * result + costPerGram.hashCode()
    return result
  }
}

val List<Use>.totalGrams: BigDecimal
  get() = map { it.amountGrams }.fold(BigDecimal.ZERO, BigDecimal::add)

val List<Use>.totalCost: BigDecimal
  get() = map { it.costPerGram * it.amountGrams }.fold(BigDecimal.ZERO, BigDecimal::add)

