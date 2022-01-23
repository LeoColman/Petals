package br.com.colman.petals.use

import io.realm.RealmObject
import io.realm.annotations.Ignore
import java.math.BigDecimal

class UseRepository {
}

@Suppress("CanBeParameter")
open class Use(
  private var amountGramsDb: Long = 0,
  private var costPerGramDb: Long = 0,
) : RealmObject() {

  @Ignore val amountGrams = BigDecimal(amountGramsDb).divide(precision).setScale(3)
  @Ignore val costPerGram = BigDecimal(costPerGramDb).divide(precision).setScale(3)

  companion object {
    private val precision = BigDecimal(100)
  }
}
