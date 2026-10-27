#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:Repository("https://central.sonatype.com/repository/maven-snapshots/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:4.0.0")

@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout:v7")
@file:DependsOn("actions:setup-java:v5")
@file:DependsOn("actions:cache:v4")
@file:DependsOn("actions:upload-artifact:v4")
@file:DependsOn("gradle:actions__setup-gradle:v4")

import io.github.typesafegithub.workflows.actions.actions.Cache
import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.actions.UploadArtifact
import io.github.typesafegithub.workflows.actions.gradle.ActionsSetupGradle
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow

// The device itself is declared in app/build.gradle.kts as a Gradle managed device, not here, so the
// same command reproduces this run on a laptop. Changing the image is a Gradle edit, not a CI edit.
private val DEVICE_TASK = "pixel6Api34FdroidDebugAndroidTest"

// ScreenshotTakerTest regenerates store artwork and uploads it to a server on the developer's
// machine, so it can only fail here. It carries @DevTooling and is filtered out by annotation.
private val SKIP_DEV_TOOLING =
  "-Pandroid.testInstrumentationRunnerArguments.notAnnotation=br.com.colman.petals.DevTooling"

workflow(
  name = "Instrumented Tests",
  on = listOf(Push(branches = listOf("main")), PullRequest()),
  sourceFile = __FILE__
) {
  // Its own workflow rather than folded into unit tests: it needs KVM, it is far slower, and a
  // failure here means something different from a JVM test failure.
  job(id = "instrumented-test", runsOn = RunnerType.UbuntuLatest, timeoutMinutes = 45) {
    uses(name = "Setup JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(name = "Checkout", action = Checkout())
    uses(name = "Setup Gradle", action = ActionsSetupGradle())

    // Without this the runner exposes /dev/kvm to root only, and the device falls back to software
    // emulation, which is too slow to finish inside any sane timeout.
    run(
      name = "Enable KVM group perms",
      command = """
        echo 'KERNEL=="kvm", GROUP="kvm", MODE="0666", OPTIONS+="static_node=kvm"' | sudo tee /etc/udev/rules.d/99-kvm4all.rules
        sudo udevadm control --reload-rules
        sudo udevadm trigger --name-match=kvm
      """.trimIndent()
    )

    // Gradle provisions the image on first use. Caching it keeps later runs from re-downloading a
    // system image that only changes when the device declaration does.
    uses(
      name = "Cache the managed device image",
      action = Cache(
        path = listOf(
          "~/.android/avd",
          "~/.android/adb*",
          "~/.android/gradle/avd"
        ),
        key = "gmd-${expr { runner.os }}-$DEVICE_TASK",
      )
    )

    run(
      name = "Run Instrumented Tests",
      command = "./gradlew :app:$DEVICE_TASK $SKIP_DEV_TOOLING"
    )

    // The XML says which test failed; the HTML report says how. Worth keeping when it goes red,
    // because nobody can reproduce an emulator failure by staring at the log.
    uses(
      name = "Upload instrumented test report",
      condition = expr { failure() },
      action = UploadArtifact(
        name = "instrumented-test-report",
        path = listOf(
          "app/build/reports/androidTests",
          "app/build/outputs/androidTest-results"
        )
      )
    )
  }
}
