import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.jk1.license.render.InventoryMarkdownReportRenderer

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

buildscript {
  repositories {
    mavenCentral()
    google()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:7.2.2")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
  }
}

plugins {
  id("com.autonomousapps.dependency-analysis") version "1.13.1"
  id("com.github.ben-manes.versions") version "0.43.0"
  id("nl.littlerobots.version-catalog-update") version "0.7.0"
  id("com.github.jk1.dependency-license-report") version "2.1"
}

licenseReport {
  configurations = arrayOf("fdroidDebugCompileClasspath")
  allowedLicensesFile = file("config/allowed-licenses.json")
  filters = arrayOf(LicenseBundleNormalizer())
  renderers = arrayOf(InventoryMarkdownReportRenderer())
  excludeBoms = true
}

dependencyAnalysis {
  issues {
    all {
      onUnusedDependencies { severity("fail") }
      onUnusedAnnotationProcessors { severity("fail") }
    }
  }
}
