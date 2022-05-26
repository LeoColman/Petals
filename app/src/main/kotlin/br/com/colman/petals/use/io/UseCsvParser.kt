package br.com.colman.petals.use.io

import br.com.colman.petals.use.repository.Use
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object UseCsvParser {
  private val csvReader = csvReader()

  fun parse(line: String): Result<Use> = runCatching {
    val (date, amount, cost) = csvReader.readAll(line).single()

    val dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    Use(dateTime, amount.toBigDecimal(), cost.toBigDecimal())
  }
}
