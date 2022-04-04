package br.com.colman.petals.use.repository

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode.InstancePerTest
import io.kotest.core.test.AssertionMode.Error

object ProjectConfig : AbstractProjectConfig() {
  override val isolationMode = InstancePerTest
  override val assertionMode = Error
}
