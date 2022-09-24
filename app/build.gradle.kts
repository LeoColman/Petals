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

import org.gradle.api.JavaVersion.VERSION_1_8
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") //KTIJ-19369
plugins {
  id("com.android.application")
  id("kotlin-android")
  kotlin("kapt")
  alias(libs.plugins.detekt)
  id("org.jetbrains.kotlinx.kover") version "0.6.0"
}

repositories {
  mavenCentral()
  google()
  maven("https://jitpack.io/")
  maven("https://s01.oss.sonatype.org/content/repositories/snapshots")

}

android {
  compileSdk = 33

  defaultConfig {
    applicationId = "br.com.colman.petals"
    minSdk = 21
    targetSdk = 30
    versionCode = 2100
    versionName = "2.10.0"

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
    kotlinCompilerExtensionVersion = libs.versions.compose.get()
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
  testRuntimeOnly(libs.kotlin.reflect)
  testImplementation(libs.kotlinx.coroutines.test)

  // Compose
  implementation(libs.bundles.compose)
  compileOnly(libs.compose.material.tooling)
  debugRuntimeOnly(libs.compose.material.tooling)
  androidTestImplementation(libs.bundles.compose.test)

  // AndroidX
  androidTestImplementation(libs.bundles.androidx.test)

  // Datastore
  implementation(libs.bundles.datastore)

  // Graph Views
  implementation(libs.bundles.graph.view)

  // Apache commons
  implementation(libs.bundles.apache.commons)

  // Koin
  implementation(libs.bundles.koin)
  androidTestImplementation(libs.koin.test)

  // Kotest
  testImplementation(libs.bundles.kotest.all)
  androidTestImplementation(libs.bundles.kotest.extras)

  // Mockk
  testImplementation(libs.mockk)

  // Date manipulation
  implementation(libs.bundles.date.time)

  // Detekt
  detektPlugins(libs.detekt.formatting)

  coreLibraryDesugaring(libs.android.desugar.jdk) {
    because("We want to use features from Java 8+, and this is the way to do it in Android")
  }

  // Timber
  implementation(libs.timber) {
    because("Logging library, easy to use")
  }

  // KotlinCSV
  implementation(libs.kotlin.csv)

  // Snodge
  testImplementation(libs.snodge) {
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
