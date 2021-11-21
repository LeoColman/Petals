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

package br.com.colman.petals

object Libs {

   object Kotlin {
      val version = "1.5.31"
      val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
      val coroutineTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1"
   }

   object Detekt {
      val version = "1.19.0-RC1"
      val formatting = "io.gitlab.arturbosch.detekt:detekt-formatting:$version"
   }

   object Graph {
      val version = "4.2.2"
      val graphView = "com.jjoe64:graphview:4.2.2"
   }

   object Compose {
      val version = "1.0.5"
   }

   object JodaTime {
      val jodaTime = "joda-time:joda-time:2.10.13"
   }

   object DataStore {
      val version = "1.0.0"
      val dataStorePreferences = "androidx.datastore:datastore-preferences:$version"
   }

   object AndroidX {
      val activityCompose = "androidx.activity:activity-compose:1.3.1"
      val composeMaterial = "androidx.compose.material:material:${Compose.version}"
      val composeAnimation = "androidx.compose.animation:animation:${Compose.version}"
      val composeTooling = "androidx.compose.ui:ui-tooling:${Compose.version}"
      val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0"

      object Test {
         val version = "1.4.0"
         val core = "androidx.test:core:$version"
         val coreKtx = "androidx.test:core-ktx:$version"
      }
   }

   object ApacheCommons {
      val math = "org.apache.commons:commons-math3:3.6.1"
   }

   object Mockk {
      val version = "1.12.0"
      val mockk = "io.mockk:mockk:$version"
      val mockkAgent = "io.mockk:mockk-agent-jvm:$version"
   }

   object Koin {
      val version = "3.1.3"

      val android = "io.insert-koin:koin-android:$version"
      val compose = "io.insert-koin:koin-androidx-compose:$version"
   }

   object Robolectric {
      val version = "4.5.1"
      val robolectric = "org.robolectric:robolectric:$version"
   }

   object JUnit {
      val version = "4.13"
      val junit4 = "junit:junit:$version"
   }

   object Kotest {
      val version = "4.6.3"
      val junitRunner = "io.kotest:kotest-runner-junit5:$version"
      val robolectricExtension = "io.kotest.extensions:kotest-extensions-robolectric:0.4.0"
      val property = "io.kotest:kotest-property:$version"
   }
}
