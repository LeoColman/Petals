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
import br.com.colman.petals.Version
import org.gradle.api.JavaVersion.VERSION_1_8

plugins {
   id("com.android.application")
   id("kotlin-android")
   kotlin("kapt")
   id("io.gitlab.arturbosch.detekt").version("1.19.0-RC1")
}

repositories {
   mavenCentral()
   google()
}

android {
   compileSdk = 31

   defaultConfig {
      applicationId = "br.com.colman.petals"
      minSdk = 21
      targetSdk = 30
      versionCode = Version.versionCode
      versionName = Version.versionName

      testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
   }

   buildTypes {
      release {
         isMinifyEnabled = true
      }
   }

   compileOptions {
      sourceCompatibility(VERSION_1_8)
      targetCompatibility(VERSION_1_8)
   }

   kotlinOptions {
      jvmTarget = "1.8"
   }

   buildFeatures {
      compose = true
   }

   composeOptions {
      kotlinCompilerExtensionVersion = Libs.Compose.version
   }

   testOptions {
      unitTests {
         isIncludeAndroidResources = true
         all { it.useJUnitPlatform() }
      }
   }

}

dependencies {
   // Kotlin
   implementation(Libs.Kotlin.reflect)
   testImplementation(Libs.Kotlin.coroutineTest)

   // AndroidX
   implementation(Libs.AndroidX.activityCompose)
   implementation(Libs.AndroidX.composeMaterial)
   implementation(Libs.AndroidX.composeAnimation)
   implementation(Libs.AndroidX.composeTooling)
   implementation(Libs.AndroidX.viewModelCompose)

   testImplementation(Libs.AndroidX.Test.core)
   testImplementation(Libs.AndroidX.Test.coreKtx)

   // Joda Time
   implementation(Libs.JodaTime.jodaTime)

   // Datastore
   implementation(Libs.DataStore.dataStorePreferences)

   // Koin
   implementation(Libs.Koin.android)
   implementation(Libs.Koin.compose)

   // Robolectric
   testImplementation(Libs.Robolectric.robolectric)

   // Kotest
   testImplementation(Libs.Kotest.junitRunner)
   testImplementation(Libs.Kotest.robolectricExtension)

   // JUnit
   testImplementation(Libs.JUnit.junit4)

   // Detekt
   detektPlugins(Libs.Detekt.formatting)
}

tasks.withType<Test>() {
   useJUnitPlatform()
}

detekt {
   buildUponDefaultConfig = true
   autoCorrect = true
}
