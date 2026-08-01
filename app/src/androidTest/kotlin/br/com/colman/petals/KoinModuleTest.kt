package br.com.colman.petals

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import br.com.colman.kotest.FunSpec
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.koinApplication
import org.koin.test.check.checkModules

/**
 * checkModules closes the container it verifies. Pointed at the application's own Koin, as this
 * spec used to be, it left the root scope closed for the rest of the run, and every later test died
 * resolving anything with "Scope '_root_' is closed" - a crash that took the whole suite with it.
 *
 * Verifying a throwaway container over the same modules gives the same signal and leaves the
 * running application untouched.
 */
class KoinModuleTest : FunSpec({
  test("Koin can resolve all modules") {
    val application = koinApplication {
      androidContext(ApplicationProvider.getApplicationContext<Context>())
      modules(KoinModule)
    }

    try {
      application.checkModules()
    } finally {
      application.close()
    }
  }
})
