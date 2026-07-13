package br.com.colman.petals.use.repository

import androidx.annotation.StringRes
import br.com.colman.petals.R.string.method_edible
import br.com.colman.petals.R.string.method_other
import br.com.colman.petals.R.string.method_smoked
import br.com.colman.petals.R.string.method_vaporized

enum class ConsumptionMethod(val key: String, @StringRes val label: Int) {
  SMOKED("smoked", method_smoked),
  VAPORIZED("vaporized", method_vaporized),
  EDIBLE("edible", method_edible),
  OTHER("other", method_other);

  companion object {
    fun fromKey(key: String): ConsumptionMethod? = entries.firstOrNull { it.key == key }
  }
}
