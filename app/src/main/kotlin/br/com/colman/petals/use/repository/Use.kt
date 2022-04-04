package br.com.colman.petals.use.repository

import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Transient
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
data class Use(
  @Convert(converter = LocalDateTimeConverter::class, dbType = String::class)
  val date: LocalDateTime = LocalDateTime.now(),

  @Convert(converter = BigDecimalConverter::class, dbType = String::class)
  val amountGrams: BigDecimal = BigDecimal.ZERO,

  @Convert(converter = BigDecimalConverter::class, dbType = String::class)
  val costPerGram: BigDecimal = BigDecimal.ZERO,

  @Id var id: Long = 0
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

val List<Use>.minGrams
  get() = minOfOrNull { it.amountGrams }?.toDouble() ?: 0.0

val List<Use>.maxGrams
  get() = maxOfOrNull { it.amountGrams }?.toDouble() ?: 0.0

val List<Use>.averageGrams
  get() = map { it.amountGrams.toDouble() }.average()
