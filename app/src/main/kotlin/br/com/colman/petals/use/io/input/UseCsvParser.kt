package br.com.colman.petals.use.io.input

import br.com.colman.petals.use.repository.ConsumptionMethod
import br.com.colman.petals.use.repository.Use
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.util.UUID.randomUUID

object UseCsvParser {
  private val csvReader = csvReader()

  fun parse(line: String): Result<Use> = runCatching {
    val values = csvReader.readAll(line).single()

    val dateTime = parseDateTime(values[0])
    val amount = values[1].toBigDecimal()
    val cost = values[2].toBigDecimal()
    val id = parseOrGenerateUUID(values.getOrNull(3))
    val description = values.getOrElse(4) { "" }
    val consumptionMethod = ConsumptionMethod.fromKey(values.getOrElse(5) { "" })

    Use(dateTime, amount, cost, id, description, consumptionMethod)
  }

  private fun parseDateTime(date: String) = LocalDateTime.parse(date, ISO_LOCAL_DATE_TIME)

  private fun parseOrGenerateUUID(uuid: String?) = if (uuid.isNullOrBlank()) randomUUID().toString() else uuid
}
