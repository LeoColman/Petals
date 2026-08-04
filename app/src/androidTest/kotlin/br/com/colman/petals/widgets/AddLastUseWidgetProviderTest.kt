package br.com.colman.petals.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import br.com.colman.kotest.FunSpec
import io.kotest.matchers.shouldNotBe

/**
 * The launcher inflates [android.appwidget.AppWidgetProviderInfo.initialLayout] before Glance ever
 * runs, so a provider without one asks it to inflate resource 0. That surfaces to the user as
 * "Can't load widget", with `Resources$NotFoundException: Resource ID #0x0` in the log, and the
 * widget can never be placed. The attribute had been missing since the widget was written.
 */
class AddLastUseWidgetProviderTest : FunSpec({

  test("the widget provider declares an initial layout") {
    val context = ApplicationProvider.getApplicationContext<Context>()

    val provider = AppWidgetManager.getInstance(context)
      .getInstalledProvidersForPackage(context.packageName, null)
      .single { it.provider.className == AddLastUseWidgetReciever::class.java.name }

    provider.initialLayout shouldNotBe 0
  }
})
