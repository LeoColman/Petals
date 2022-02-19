@file:OptIn(ExperimentalCoroutinesApi::class)

package br.com.colman.petals.use.repository

import io.objectbox.Box
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.converter.PropertyConverter
import io.objectbox.kotlin.toFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.math.BigDecimal
import java.math.BigDecimal.ZERO
import java.time.LocalDateTime
import java.time.LocalDateTime.parse
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

class UseRepository(private val box: Box<Use>) {

  fun insert(use: Use) {
    Timber.d("Adding use: $use")
    box.put(use)
  }

  fun getLastUse() = all().map { it.maxByOrNull { it.date } }

  fun getLastUseDate() = getLastUse().map { it?.date }

  fun all() = box.query().build().subscribe().toFlow().map { it.toList() }

  fun delete(use: Use) {
    Timber.d("Deleting use: $use")
    box.remove(use)
  }
}

@Entity
data class Use(
  @Convert(converter = BigDecimalConverter::class, dbType = String::class)
  val amountGrams: BigDecimal = ZERO,

  @Convert(converter = BigDecimalConverter::class, dbType = String::class)
  val costPerGram: BigDecimal = ZERO,

  @Convert(converter = LocalDateTimeConverter::class, dbType = String::class)
  val date: LocalDateTime = LocalDateTime.now(),

  @Id var id: Long = 0
)

class BigDecimalConverter : PropertyConverter<BigDecimal, String> {
  override fun convertToEntityProperty(str: String): BigDecimal {
    return BigDecimal(str)
  }

  override fun convertToDatabaseValue(bigDecimal: BigDecimal): String {
    return bigDecimal.toString()
  }
}

class LocalDateTimeConverter : PropertyConverter<LocalDateTime, String> {
  override fun convertToEntityProperty(str: String): LocalDateTime = parse(str, ISO_LOCAL_DATE_TIME)

  override fun convertToDatabaseValue(ldt: LocalDateTime): String = ldt.format(ISO_LOCAL_DATE_TIME)
}
