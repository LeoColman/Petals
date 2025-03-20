#!/usr/bin/env kotlin

@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.10.1")
@file:DependsOn("io.ktor:ktor-server-core-jvm:2.3.13")
@file:DependsOn("io.ktor:ktor-server-netty-jvm:2.3.13")
@file:DependsOn("io.ktor:ktor-server-content-negotiation-jvm:2.3.13")
@file:DependsOn("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.13")
@file:DependsOn("io.ktor:ktor-server-host-common-jvm:2.3.13")

import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import java.io.File

// Function to disable animations via ADB
fun disableAnimations() {
  listOf(
    "adb shell settings put global window_animation_scale 0",
    "adb shell settings put global transition_animation_scale 0",
    "adb shell settings put global animator_duration_scale 0"
  ).forEach { command ->
    val process = Runtime.getRuntime().exec(command)
    process.waitFor()
  }
  println("Animations in the emulator have been disabled")
}

fun startServer(): NettyApplicationEngine {
  return embeddedServer(Netty, port = 8081) {
    install(ContentNegotiation) {
      json()
    }
    routing {
      post("/upload") {
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

// Disable animations
disableAnimations()

// Run Android tests
ProcessBuilder(
  "../gradlew",
  "connectedFdroidDebugAndroidTest",
  "-Pandroid.testInstrumentationRunnerArguments.class=br.com.colman.petals.ScreenshotTakerTest"
).inheritIO().start().waitFor()

// Stop the server
server.stop(1000, 10000)
