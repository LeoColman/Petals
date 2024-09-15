plugins {
  id("com.android.library")
  id("kotlin-android")
  alias(libs.plugins.compose.compiler)
  kotlin("kapt")
}

repositories {
  mavenCentral()
  google()
  maven("https://jitpack.io/")
  maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
}

android {
  buildFeatures {
    buildConfig = true
  }
    namespace = "br.com.colman.petals"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {  // Koin

  implementation(libs.bundles.compose)
  implementation(libs.androidx.navigation.runtime.ktx)
  implementation(libs.androidx.ui.android)
  compileOnly(libs.compose.material.tooling)
  debugRuntimeOnly(libs.compose.material.tooling)
  androidTestImplementation(libs.bundles.compose.test)
  implementation(libs.androidx.material3.android)
  implementation(libs.androidx.runtime.android)
}
