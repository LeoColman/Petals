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
  id("kotlin-parcelize")
  kotlin("kapt")
  alias(libs.plugins.detekt)
  alias(libs.plugins.sqldelight)
  id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

repositories {
  mavenCentral()
  google()
  maven("https://jitpack.io/")
  maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
}

android {
  namespace = "br.com.colman.petals"
  compileSdk = 33

  defaultConfig {
    applicationId = "br.com.colman.petals"
    minSdk = 21
    targetSdk = 33
    versionCode = 3504
    versionName = "3.5.4"

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

    create("playstore") {
      dimension = "distribution"
      signingConfig = signingConfigs.findByName("self-sign")
    }

    create("github") {
      dimension = "distribution"
      signingConfig = signingConfigs.findByName("self-sign")
    }
  }

  sourceSets {
    named("playstore") {
      res.srcDirs("src/playstore/res")
      java.srcDirs("src/playstore/java")
      kotlin.srcDirs("src/playstore/kotlin")
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
    kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
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

  implementation(libs.sqldelight.android.driver)
  implementation(libs.sqldelight.coroutines.extensions)
  testImplementation(libs.sqldelight.sqlite.driver)
  implementation(libs.requery.sqlite)

  // Google Ads
  "playstoreImplementation"("com.google.android.gms:play-services-ads:21.4.0")

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

sqldelight {
  database("Database") {
    dialect = "sqlite:3.25"
    schemaOutputDirectory = file("src/main/sqldelight/databases")
    verifyMigrations = true
  }
}
