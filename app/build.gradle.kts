/*
 * Petals APP
 * Copyright (C) 2021 Leonardo Colman Lopes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import br.com.colman.petals.Libs
import org.gradle.api.JavaVersion.VERSION_1_8
import java.lang.System.getenv
import java.util.Properties

plugins {
  id("com.android.application")
  id("kotlin-android")
  kotlin("kapt")
  id("io.gitlab.arturbosch.detekt") version "1.21.0"
  id("org.jetbrains.kotlinx.kover") version "0.5.1"
  id("io.objectbox") // Apply last.
}

repositories {
  mavenCentral()
  google()
  maven("https://jitpack.io/")
  maven("https://s01.oss.sonatype.org/content/repositories/snapshots")

}

android {
  compileSdk = 31

  defaultConfig {
    applicationId = "br.com.colman.petals"
    minSdk = 21
    targetSdk = 30
    versionCode = 261
    versionName = "v2.6.1"

    testApplicationId = "$applicationId.test"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    testFunctionalTest = true
  }

  signingConfigs {
    val keystore = rootProject.file("keystore")
    val keystoreSecret = rootProject.file("keystore.secret")

    if(!keystore.exists() && keystoreSecret.exists()) {
      logger.warn("Impossible to create signing configuration with files encrypted")
      return@signingConfigs
    }
    val keystoreProperties = Properties().apply {
      load(rootProject.file("keystore.properties").inputStream())
    }

    create("self-sign") {
      storeFile = keystore
      storePassword = keystoreProperties.getProperty("KEYSTORE_PASSWORD")
      keyAlias = keystoreProperties.getProperty("SIGNING_KEY_ALIAS")
      keyPassword = keystoreProperties.getProperty("SIGNING_KEY_PASSWORD")
    }
  }

  flavorDimensions += "distribution"
  productFlavors {
    create("fdroid") {
      dimension = "distribution"
    }

    if(signingConfigs.findByName("self-sign") == null) return@productFlavors

    create("github") {
      dimension = "distribution"
      signingConfig = signingConfigs.getByName("self-sign")
    }
  }


  buildTypes {
    named("release") {
      isMinifyEnabled = true
    }

    named("debug") {
      applicationIdSuffix = ".debug"
      isPseudoLocalesEnabled = true
      isDebuggable = true
    }
  }

  compileOptions {
    sourceCompatibility(VERSION_1_8)
    targetCompatibility(VERSION_1_8)
    isCoreLibraryDesugaringEnabled = true
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }

  buildFeatures {
    compose = true

  }

  composeOptions {
    kotlinCompilerExtensionVersion = Libs.AndroidX.Compose.version
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      all { it.useJUnitPlatform() }
    }
  }

  packagingOptions {
    resources.excludes.add("META-INF/*")
  }

}

dependencies {
  // Kotlin
  implementation(Libs.Kotlin.reflect)
  testImplementation(Libs.KotlinX.Test.coroutines)

  // AndroidX
  implementation(Libs.AndroidX.Compose.material)
  implementation(Libs.AndroidX.Compose.materialIcons)
  compileOnly(Libs.AndroidX.Compose.tooling)
  androidTestImplementation(Libs.AndroidX.Compose.Test.uiTest)
  androidTestImplementation(Libs.AndroidX.Compose.Test.uiTestJunit4)

  implementation(Libs.AndroidX.activityCompose)
  implementation(Libs.AndroidX.navigationCompose)
  implementation(Libs.AndroidX.datastorePreferences)

  androidTestImplementation(Libs.AndroidX.Test.rules)
  androidTestImplementation(Libs.AndroidX.Test.runner)

  // GraphView
  implementation(Libs.GraphView.graphView) {
    because("We use graphs for statistics and other UI components")
  }

  // Mp Android Chart
  implementation(Libs.MPAndroidChart.mpAndroidChart) {
    because("Sometimes the other GraphView isn't enough")
  }

  // Apache Math
  implementation(Libs.Apache.math)

  // Apache Commons
  implementation(Libs.Apache.commons)
  
  // Joda Time
  implementation(Libs.JodaTime.jodaTime)

  // Koin
  implementation(Libs.Koin.android)
  implementation(Libs.Koin.compose)
  androidTestImplementation(Libs.Koin.test)

  // Kotest
  testImplementation(Libs.Kotest.junitRunner)
  testImplementation(Libs.Kotest.property)
  androidTestImplementation(Libs.Kotest.property)
  androidTestImplementation(Libs.Kotest.assertions)

  // Mockk
  testImplementation(Libs.Mockk.mockk)

  // JUnit
  androidTestImplementation(Libs.JUnit.junit4)

  // UI Tests
  androidTestImplementation(Libs.AndroidX.Compose.Test.uiTestJunit4)

  // Date range
  implementation(Libs.KotlinDateRange.kotlinDateRange)

  // Detekt
  detektPlugins(Libs.Detekt.formatting)

  coreLibraryDesugaring(Libs.Android.desugarJdk) {
    because("We want to use features from Java 8+, and this is the way to do it in Android")
  }

  // Material Compose dialogs
  implementation(Libs.ComposeMaterialDialogs.core)
  implementation(Libs.ComposeMaterialDialogs.dateTime)

  // Timber
  implementation(Libs.Timber.timber) {
    because("Logging library, easy to use")
  }

  // Icons
  implementation(Libs.TablerIcons.tablerIcons)

  // KotlinCSV
  implementation(Libs.KotlinCsv.jvm)

  // Snodge
  testImplementation(Libs.Snodge.snodge) {
    because("It's useful for fuzzy testing (mutating strings, jsons, etc)")
  }

}

tasks.withType<Test> {
  useJUnitPlatform()
}

detekt {
  buildUponDefaultConfig = true
  autoCorrect = true
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}
