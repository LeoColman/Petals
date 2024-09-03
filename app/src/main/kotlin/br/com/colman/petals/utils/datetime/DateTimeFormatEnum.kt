package br.com.colman.petals.utils.datetime

enum class DateTimeFormatEnum(val format: String) {
  YYYY_MM_DD_LINE("yyyy-MM-dd"),
  YYYY_MM_DD_SLASH("yyyy/MM/dd"),
  DD_MM_YYYY_LINE("dd-MM-yyyy"),
  DD_MM_YYYY_DOT("dd.MM.yyyy"),
  MM_DD_YYYY_SLASH("MM/dd/yyyy"),
  MM_DD_YYYY_LINE("MM-dd-yyyy")
}
