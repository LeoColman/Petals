package br.com.colman.petals.clock

import androidx.test.core.app.ApplicationProvider
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.extensions.robolectric.RobolectricTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import org.joda.time.LocalDateTime

@RobolectricTest
class StopTimerTest : ShouldSpec({

   val target = QuitTimer(ApplicationProvider.getApplicationContext())

   should("Persist and return the start date") {
      val now = LocalDateTime.now()

      target.setQuitDate(now)

      target.quitDate.first() shouldBe now
   }

})
