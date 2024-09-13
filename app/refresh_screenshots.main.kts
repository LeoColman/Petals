#!/usr/bin/env kotlin

@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.9.0")
@file:DependsOn("io.ktor:ktor-server-core-jvm:2.3.12")
@file:DependsOn("io.ktor:ktor-server-netty-jvm:2.3.12")
@file:DependsOn("io.ktor:ktor-server-content-negotiation-jvm:2.3.12")
@file:DependsOn("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.12")
@file:DependsOn("io.ktor:ktor-server-host-common-jvm:2.3.12")

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

// Function to disable animations via ADB
fun disableAnimations() {
  listOf(
    "adb shell settings put global window_animation_scale 0",
    "adb shell settings put global transition_animation_scale 0",
    "adb shell settings put global animator_duration_scale 0",
  ).forEach { command ->
    try {
      val process = Runtime.getRuntime().exec(command)
      process.waitFor()
    } catch (e: Exception) {
      println("Error while trying to disable animations via ADB: ${e.localizedMessage}")
    }
  }
  println("Animations in the emulator has been disabled")
}

// Function to start the server
fun startServer(): NettyApplicationEngine {
  return embeddedServer(Netty, port = 8080) {
    install(ContentNegotiation) {
      json()
    }
    routing {
      post("/upload") {
        println("Received upload")
        println(call.parameters)
        val multipart = call.receiveMultipart()
        val lang = call.parameters["lang"] ?: "unknown"
        val country = call.parameters["country"] ?: "unknown"

        multipart.forEachPart { part ->
          if (part is PartData.FileItem) {
            val countryHyphen = if (country.isNotBlank()) "-$country" else ""
            val dir = File("../fastlane/metadata/android/${lang}${countryHyphen}/images/phoneScreenshots")
            if (!dir.exists()) {
              dir.mkdirs()
            }
            val file = File(dir, part.originalFileName ?: "screenshot.png")
            part.streamProvider().use { input -> file.outputStream().buffered().use { input.copyTo(it) } }
          }
          part.dispose()
        }
        call.respondText("File uploaded successfully", status = HttpStatusCode.OK)
      }
    }
  }.start(wait = false)
}

// Start the server
val server = startServer()

// Disable Animations
disableAnimations()

// Run the Android tests
println("Running Android tests...")
val process = ProcessBuilder(
  "../gradlew",
  "connectedFdroidDebugAndroidTest",
  "-Pandroid.testInstrumentationRunnerArguments.class=br.com.colman.petals.ScreenshotTakerTest"
).inheritIO().start()
val exitCode = process.waitFor()

if (exitCode == 0) {
  println("Tests ran successfully")
} else {
  println("Tests failed with exit code $exitCode")
}

// Stop the server
println("Stopping server...")
server.stop(1000, 10000)
println("Server stopped")
