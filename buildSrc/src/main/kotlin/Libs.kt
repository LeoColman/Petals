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

   object Android {
      val version = "1.1.5"
      val desugarJdk = "com.android.tools:desugar_jdk_libs:$version"
   }

   object AndroidX {
      object Compose {
         val version = "1.1.0"

         val material = "androidx.compose.material:material:$version"
         val materialIcons = "androidx.compose.material:material-icons-extended:$version"
         val tooling = "androidx.compose.ui:ui-tooling:$version"

         object Test {
            val uiTest = "androidx.compose.ui:ui-test:$version"
            val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4:$version"
         }
      }

      val activityCompose = "androidx.activity:activity-compose:1.3.1"
      val navigationCompose = "androidx.navigation:navigation-compose:2.4.0-beta02"

      object DataStore {
         val version = "1.0.0"

         val android = "androidx.datastore:datastore-preferences:$version"
         val core = "androidx.datastore:datastore-core:$version"
      }

      object Test {
         val version = "1.4.0"
         val rules = "androidx.test:rules:$version"
         val runner = "androidx.test:runner:$version"
      }
   }

   object TablerIcons {
      private val version = "1.0.0"
      val tablerIcons = "br.com.devsrsouza.compose.icons.android:tabler-icons:$version"
   }

   object KotlinCsv {
      private val version = "1.2.0"
      val jvm = "com.github.doyaaaaaken:kotlin-csv-jvm:$version"
   }

   object Kotlin {
      val version = "1.6.10"
      val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
   }

   object KotlinX {
      object Test {
         val version = "1.5.1"
         val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
      }
   }

   object Detekt {
      val version = "1.21.0-RC1"
      val formatting = "io.gitlab.arturbosch.detekt:detekt-formatting:$version"
   }

   object GraphView {
      val version = "4.2.2"
      val graphView = "com.jjoe64:graphview:$version"
   }

   object MPAndroidChart {
      val version = "v3.1.0"
      val mpAndroidChart = "com.github.PhilJay:MPAndroidChart:$version"
   }

   object KotlinDateRange {
      val version = "1.0.0"
      val kotlinDateRange = "me.moallemi.tools:kotlin-date-range:$version"
   }

   object JodaTime {
      val version = "2.10.13"
      val jodaTime = "joda-time:joda-time:$version"
   }

   object Apache {
      val commons = "org.apache.commons:commons-lang3:3.12.0"
      val math = "org.apache.commons:commons-math3:3.6.1"
   }

   object Mockk {
      val version = "1.12.0"
      val mockk = "io.mockk:mockk:$version"
   }

   object Koin {
      val version = "3.2.0-beta-1"

      val android = "io.insert-koin:koin-android:$version"
      val test = "io.insert-koin:koin-test:$version"
      val compose = "io.insert-koin:koin-androidx-compose:$version"
   }

   object JUnit {
      val version = "4.13"
      val junit4 = "junit:junit:$version"
   }

   object Kotest {
      val version = "5.1.0"
      val junitRunner = "io.kotest:kotest-runner-junit5:$version"
      val property = "io.kotest:kotest-property:$version"
      val assertions = "io.kotest:kotest-assertions-core:$version"
   }

   object ComposeMaterialDialogs {
      val version = "0.6.3"

      val core = "io.github.vanpra.compose-material-dialogs:core:$version"
      val dateTime = "io.github.vanpra.compose-material-dialogs:datetime:$version"
   }

   object Timber {
      val version = "5.0.1"

      val timber = "com.jakewharton.timber:timber:$version"
   }

   object Snodge {
      val version = "3.7.0.0"
      val snodge = "com.natpryce:snodge:$version"
   }
}
