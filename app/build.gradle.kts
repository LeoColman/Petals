import br.com.colman.petals.Libs
import br.com.colman.petals.Version
import org.gradle.api.JavaVersion.VERSION_1_8

plugins {
   id("com.android.application")
   id("kotlin-android")
   kotlin("kapt")
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
}

tasks.withType<Test>() {
   useJUnitPlatform()
}
