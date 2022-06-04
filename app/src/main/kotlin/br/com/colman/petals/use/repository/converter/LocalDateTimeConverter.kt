package br.com.colman.petals.use.repository.converter

import io.objectbox.converter.PropertyConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

class LocalDateTimeConverter : PropertyConverter<LocalDateTime, String> {
  override fun convertToEntityProperty(str: String?): LocalDateTime? {
    if (str == null) return null
    return LocalDateTime.parse(str, ISO_LOCAL_DATE_TIME)
  }

  override fun convertToDatabaseValue(ldt: LocalDateTime?): String? {
    if (ldt == null) return null
    return ldt.format(ISO_LOCAL_DATE_TIME)
  }
}
