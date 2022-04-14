package br.com.colman.petals.use.repository.converter

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldNotBe
import io.objectbox.converter.PropertyConverter

fun propertyConverterTests(converter: PropertyConverter<*, *>) = funSpec {
  context("PropertyConverter interface validations") {
    test("Converters are created by the default constructor") {
      converter::class.constructors.first().call() shouldNotBe null
    }

    test("Converters must handle null values") {
      shouldNotThrowAny {
        converter.convertToDatabaseValue(null)
        converter.convertToEntityProperty(null)
      }
    }
  }
}
