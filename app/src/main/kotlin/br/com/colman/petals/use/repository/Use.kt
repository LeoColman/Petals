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

  val id: String = UUID.randomUUID().toString(),

  val description: String = "",

  val consumptionMethod: ConsumptionMethod? = null,

  val strainId: String? = null,

  /** Stars from half a star to five, in halves, see [Rating]; null when the use wasn't rated. */
  val rating: Double? = null
) {
  @Transient
  val localDate: LocalDate = date.toLocalDate()

  /**
   * This use's own CSV columns. The strain's columns and the rating follow them under fixed labels, written by the
   * serializer, which is why [strainId] and [rating] are not here.
   */
  fun columns(): List<String> = listOf(
    date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    amountGrams.toPlainString(),
    costPerGram.toPlainString(),
    id,
    description,
    consumptionMethod?.key.orEmpty()
  )

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Use

    if (date != other.date) return false
    if (amountGrams != other.amountGrams) return false
    if (costPerGram != other.costPerGram) return false
    if (description != other.description) return false
    if (consumptionMethod != other.consumptionMethod) return false
    if (strainId != other.strainId) return false
    if (rating != other.rating) return false

    return true
  }

  override fun hashCode(): Int {
    var result = date.hashCode()
    result = 31 * result + amountGrams.hashCode()
    result = 31 * result + costPerGram.hashCode()
    result = 31 * result + description.hashCode()
    result = 31 * result + (consumptionMethod?.hashCode() ?: 0)
    result = 31 * result + (strainId?.hashCode() ?: 0)
    result = 31 * result + (rating?.hashCode() ?: 0)
    return result
  }
}

val List<Use>.totalGrams: BigDecimal
  get() = map { it.amountGrams }.fold(BigDecimal.ZERO, BigDecimal::add)

val List<Use>.totalCost: BigDecimal
  get() = map { it.costPerGram * it.amountGrams }.fold(BigDecimal.ZERO, BigDecimal::add)
