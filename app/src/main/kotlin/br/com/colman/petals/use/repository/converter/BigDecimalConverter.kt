package br.com.colman.petals.use.repository.converter

import io.objectbox.converter.PropertyConverter
import java.math.BigDecimal

class BigDecimalConverter : PropertyConverter<BigDecimal, String> {
  override fun convertToEntityProperty(str: String?) = str?.let {
    BigDecimal(it)
  }
  override fun convertToDatabaseValue(bigDecimal: BigDecimal?) = bigDecimal?.let {
    if(it.scale() == 0)
      it.setScale(1).toPlainString()
    else
      it.toPlainString()
  }
}
