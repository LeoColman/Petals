package br.com.colman.petals

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver.Companion.IN_MEMORY
import io.kotest.core.TestConfiguration

/** A fresh in-memory database with the current schema, closed when the spec is done with it. */
fun TestConfiguration.inMemoryDatabase(): Database {
  val driver = autoClose(JdbcSqliteDriver(IN_MEMORY))
  Database.Schema.create(driver)
  return Database(driver)
}
