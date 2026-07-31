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

import java.util.Locale
import java.util.Properties
import javax.xml.parsers.DocumentBuilderFactory
import org.gradle.api.JavaVersion.VERSION_17

plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-parcelize")
  kotlin("kapt")
  alias(libs.plugins.detekt)
  alias(libs.plugins.sqldelight)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.kotlinx.kover)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.licensee)
}

repositories {
  mavenCentral()
  google()
  maven("https://jitpack.io/")
  maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
}

android {
  namespace = "br.com.colman.petals"
  compileSdk = 36

  defaultConfig {
    applicationId = "br.com.colman.petals"
    minSdk = 26
    targetSdk = 36
    versionCode = 3043001
    versionName = "3.43.1"

    testApplicationId = "$applicationId.test"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    testFunctionalTest = true
  }

  signingConfigs {
    val keystore = file("keystore")
    val keystoreSecret = file("keystore.secret")

    if(!keystore.exists() && keystoreSecret.exists()) {
      logger.warn("Impossible to create signing configuration with files encrypted")
      return@signingConfigs
    }
    val keystoreProperties = Properties().apply {
      load(file("keystore.properties").inputStream())
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
      minSdk = 23
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
      proguardFile("proguard-android-optimize.txt")
    }

    named("debug") {
      applicationIdSuffix = ".debug"
      isPseudoLocalesEnabled = true
      isDebuggable = true
    }
  }

  compileOptions {
    sourceCompatibility(VERSION_17)
    targetCompatibility(VERSION_17)
    isCoreLibraryDesugaringEnabled = true
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      all { it.useJUnitPlatform() }
    }
  }

  packaging {
    resources.excludes.add("META-INF/**")
    resources.excludes.add("win32-x86-64/attach_hotspot_windows.dll")
    resources.excludes.add("win32-x86/attach_hotspot_windows.dll")
  }

  lint {
    disable += "NullSafeMutableLiveData"
  }

}

/**
 * Classpath used to launch PIT. Kept apart from the app's own configurations so that the mutation
 * engine never leaks into the shipped artifact.
 */
val pitest = configurations.create("pitest") {
  isCanBeConsumed = false
  isCanBeResolved = true
}

dependencies {
  // PIT (mutation testing). Kotest ships its own PIT test plugin, so PIT drives the
  // Kotest engine directly instead of going through the JUnit Platform.
  pitest(libs.pitest.command.line)
  pitest(libs.kotest.pitest)

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
  testImplementation(libs.bundles.androidx.test)

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
  testImplementation(libs.bundles.kotest.jvm)
  testImplementation(libs.bundles.kotest.common)
  androidTestImplementation(libs.bundles.kotest.android)
  androidTestImplementation(libs.bundles.kotest.common)

  // Mockk
  testImplementation(libs.mockk)
  androidTestImplementation(libs.bundles.mockk.android)

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

  // SQLDelight
  implementation(libs.sqldelight.android.driver)
  implementation(libs.sqldelight.coroutines.extensions)
  implementation(libs.requery.sqlite)
  testImplementation(libs.sqldelight.sqlite.driver)

  // Google Ads
  "playstoreImplementation"(libs.play.services.ads)

  // Google Billing
  "playstoreImplementation"(libs.play.services.billing)

  // Google Reviews
  "playstoreImplementation"(libs.bundles.review)

  // Glance
  implementation(libs.bundles.glance)

  // WorkManager (auto-export) + SAF DocumentFile helper
  implementation(libs.androidx.work)
  implementation(libs.androidx.documentfile)

  // Fuel
  androidTestImplementation(libs.fuel) {
    because("Specifically to make Screenshots and upload them to a localhost server")
  }
  testImplementation(kotlin("reflect"))

}

kover {
  reports {
    filters {
      excludes {
        annotatedBy("androidx.compose.runtime.Composable")
        classes("*ComposableSingletons*")
        classes("*DatabaseImpl*")
        classes("*Queries*")
        classes("*BuildConfig*")
        classes("*\$inlined\$*")
      }
    }
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
  systemProperty("kotest.framework.config.fqn", "br.com.colman.petals.KotestConfig")
}

tasks.detekt {
  dependsOn("detektMain", "detektTest")
}
detekt {
  buildUponDefaultConfig = true
  autoCorrect = true
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

sqldelight {
  databases {
    create("Database") {
      packageName.set("br.com.colman.petals")
      dialect(libs.sqldelight.sqlite.dialect)
      schemaOutputDirectory = file("src/main/sqldelight/databases")
      verifyMigrations = true
    }
  }
}

// Workaround: SQLDelight 2.3.x uses variant.sources.java?.addGeneratedSourceDirectory for AGP < 9.0,
// but KGP 2.x does not include java-registered generated sources in Kotlin compilation.
// Manually wire the generate task output dir into KotlinCompile sources.
afterEvaluate {
  android.applicationVariants.all {
    val variantName = name
    val capitalizedName = variantName.replaceFirstChar { it.uppercase() }
    val sqldelightDir = layout.buildDirectory.dir("generated/sqldelight/code/Database/$variantName")
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>()
      .matching { it.name == "compile${capitalizedName}Kotlin" }
      .configureEach {
        dependsOn("generate${capitalizedName}DatabaseInterface")
        source(sqldelightDir)
      }
  }
}

licensee {
  allow("Apache-2.0")
  allow("BSD-3-Clause")
  allow("MIT")

  allowUrl("https://github.com/jsoizo/kotlin-csv/blob/master/LICENSE") {
    because("Apache-2.0 but self-hosted")
  }

  allowUrl("https://github.com/jjoe64/GraphView/blob/master/license.txtl") {
    because("Typo of `license.txt`, which is Apache-2.0")
    because("https://github.com/jjoe64/GraphView/blob/master/license.txt")
  }

  allowUrl("https://github.com/devsrsouza/compose-icons/blob/master/LICENSE") {
    because("MIT License")
  }

  allowUrl("https://developer.android.com/studio/terms.html") {
    because("Android Software Development Kit License Agreement.")
  }

  allowUrl("https://developer.android.com/guide/playcore/license") {
    because("Google Play License Agreement.")
  }
}

/**
 * Parse kover coverage report for badge creation
 */
tasks.register("printLineCoverage") {
  group = "verification" // Put into the same group as the `kover` tasks
  dependsOn("koverXmlReport")
  doLast {
    val report = file("${layout.buildDirectory.get()}/reports/kover/report.xml")

    val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(report)
    val rootNode = doc.firstChild
    var childNode = rootNode.firstChild

    var coveragePercent = 0.0

    while (childNode != null) {
      if (childNode.nodeName == "counter") {
        val typeAttr = childNode.attributes.getNamedItem("type")
        if (typeAttr.textContent == "LINE") {
          val missedAttr = childNode.attributes.getNamedItem("missed")
          val coveredAttr = childNode.attributes.getNamedItem("covered")

          val missed = missedAttr.textContent.toLong()
          val covered = coveredAttr.textContent.toLong()

          coveragePercent = (covered * 100.0) / (missed + covered)

          break
        }
      }
      childNode = childNode.nextSibling
    }

    // Locale.US because the workflow feeds this straight into `printf "%.0f"`, which rejects the
    // comma separator a machine set to, say, pt_BR would otherwise produce.
    println("%.1f".format(Locale.US, coveragePercent))
  }
}

/**
 * Mutation testing.
 *
 * PIT has no maintained Android Gradle plugin (the community one is pinned to AGP 7), so PIT is
 * invoked straight from its command line entrypoint. Everything it needs is derived from the
 * `fdroidDebug` unit test task, which is the flavour with no proprietary dependencies.
 */
val mutationVariant = "fdroidDebug"
val mutationVariantCapitalized = mutationVariant.replaceFirstChar { it.uppercase() }

/**
 * Same exclusions as the `kover` block above, so both numbers describe the same code. Composables
 * are dropped through PIT's `fann` feature, which filters anything carrying the given annotation.
 */
val mutationExcludedClasses = listOf(
  "*ComposableSingletons*",
  "*DatabaseImpl*",
  "*Queries*",
  "*BuildConfig*",
  // Kotlin inlines library code into our classes, and PIT happily mutates it. Verified against the
  // report: of the 513 mutants in these synthetic classes, none came from a file under src/main —
  // they were all Emitters.kt, SafeCollector.common.kt, Koin or the stdlib. That is a measurement,
  // not a guarantee: an `inline fun` of our own taking a lambda would land here too.
  "*\$inlined\$*",
)

/**
 * PIT's `--targetTests` glob matches `*Test`, so a spec class named anything else is never handed
 * to the engine. Its mutants are then reported as NO_COVERAGE, which drags the mutation score down
 * but leaves test strength — the number on the badge — completely unmoved. The badge cannot show
 * this mistake, so fail loudly instead.
 *
 * This reads source rather than bytecode, so it only recognises specs that name one of Kotest's
 * own styles as their supertype. A spec extending a project-specific base class would still slip
 * through; there is no such base class today.
 */
val verifySpecNaming = tasks.register("verifySpecNaming") {
  group = "verification"
  description = "Fails if a Kotest spec is named so that mutation testing would silently skip it"

  val testSources = fileTree("src/test/kotlin") { include("**/*.kt") }
  inputs.files(testSources).withPropertyName("testSources")

  doLast {
    // `(?:\w+\s+)*` so that `internal class`, `private class` and friends are caught too.
    val specDeclaration = Regex(
      """^(?:\w+\s+)*class\s+(\w+)\s*:\s*(FunSpec|StringSpec|ShouldSpec|DescribeSpec|BehaviorSpec""" +
        """|FreeSpec|WordSpec|ExpectSpec|FeatureSpec|AnnotationSpec)\b""",
      RegexOption.MULTILINE
    )

    val misnamed = testSources.files.flatMap { source ->
      specDeclaration.findAll(source.readText())
        .map { it.groupValues[1] }
        .filterNot { it.endsWith("Test") }
    }

    if (misnamed.isNotEmpty()) {
      throw GradleException(
        "Kotest specs must be named *Test or PIT silently skips them: ${misnamed.joinToString()}"
      )
    }
  }
}

tasks.register<JavaExec>("pitest") {
  group = "verification"
  description = "Runs PIT mutation testing against the $mutationVariant variant"

  val unitTest = tasks.named<Test>("test${mutationVariantCapitalized}UnitTest")
  dependsOn(unitTest, verifySpecNaming)

  val mutableCodePaths = files(
    layout.buildDirectory.dir("tmp/kotlin-classes/$mutationVariant"),
    layout.buildDirectory.dir("intermediates/javac/$mutationVariant/classes"),
  )
  val reportDir = layout.buildDirectory.dir("reports/pitest")
  val classpathFile = layout.buildDirectory.file("tmp/pitest/classpath.txt")
  val sourceDirs = files("src/main/kotlin", "src/main/java")

  // Everything PIT needs on the classpath of the code under test, in one lazy collection so that
  // nothing has to reach back into the test task while the build is running.
  val codeUnderTest = files(
    mutableCodePaths,
    unitTest.map { it.testClassesDirs },
    unitTest.map { it.classpath },
  )

  // Declaring inputs and outputs is what lets Gradle skip this task on the follow-up
  // `printTestStrength` and `printMutationScore` invocations. Without them PIT is never
  // up-to-date, so it reruns on every invocation: three full runs per CI job, each producing a
  // different report, and PIT's console output leaking into `$(./gradlew -q ...)`.
  inputs.files(codeUnderTest).withPropertyName("codeUnderTest")
  inputs.files(sourceDirs).withPropertyName("sourceDirs")
  outputs.dir(reportDir).withPropertyName("report")

  mainClass.set("org.pitest.mutationtest.commandline.MutationCoverageReport")

  // PIT hands its own launch classpath down to the minions, and the Kotest PIT plugin drags in
  // older Byte Buddy / coroutines than the app tests are compiled against. Putting the unit test
  // runtime first means the minions load exactly the jars `testFdroidDebugUnitTest` loads.
  classpath(unitTest.map { it.classpath })
  classpath(pitest)

  args(
    "--classPathFile=${classpathFile.get().asFile.absolutePath}",
    "--mutableCodePaths=${mutableCodePaths.joinToString(",") { it.absolutePath }}",
    "--sourceDirs=${sourceDirs.joinToString(",") { it.absolutePath }}",
    "--reportDir=${reportDir.get().asFile.absolutePath}",
    "--targetClasses=br.com.colman.petals.*",
    // Specs only. A wider glob makes PIT offer every Android class to the Kotest engine as a
    // candidate test, and instantiating those outside a device hangs the run. `verifySpecNaming`
    // guards the convention this relies on.
    "--targetTests=br.com.colman.petals.*Test",
    "--excludedClasses=${mutationExcludedClasses.joinToString(",")}",
    "--features=+fann(annotation[Composable])",
    "--testPlugin=Kotest",
    // Property based specs are slow enough that PIT's 4s default kills healthy minions.
    "--timeoutConst=10000",
    "--jvmArgs=-Dkotest.framework.config.fqn=br.com.colman.petals.KotestConfig",
    "--outputFormats=HTML,XML",
    "--timestampedReports=false",
    "--failWhenNoMutations=false",
  )

  doFirst {
    // PIT reads the classpath from a file because the flattened Android test classpath is far
    // longer than a command line can hold.
    classpathFile.get().asFile.apply {
      parentFile.mkdirs()
      writeText(codeUnderTest.filter { it.exists() }.joinToString("\n") { it.absolutePath })
    }

    // Added here rather than above so the core count of whichever machine runs the build stays out
    // of the task's input fingerprint.
    args("--threads=${Runtime.getRuntime().availableProcessors()}")
  }
}

/**
 * Number of mutants PIT detected, how many a test actually executed, and how many exist at all.
 */
fun readMutationTotals(): Triple<Int, Int, Int> {
  val report = file("${layout.buildDirectory.get()}/reports/pitest/mutations.xml")
  // Missing or empty means PIT produced nothing, not that every mutant survived. Publishing 0%
  // for that would be indistinguishable from a genuinely terrible suite, so refuse to guess.
  if (!report.exists()) {
    throw GradleException("PIT wrote no report at $report. Run `./gradlew pitest` first.")
  }

  val mutations = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(report)
    .getElementsByTagName("mutation")
  if (mutations.length == 0) throw GradleException("PIT generated no mutants; $report is empty.")

  var detected = 0
  var withoutCoverage = 0
  for (index in 0 until mutations.length) {
    val attributes = mutations.item(index).attributes
    if (attributes.getNamedItem("detected").textContent == "true") detected++
    if (attributes.getNamedItem("status").textContent == "NO_COVERAGE") withoutCoverage++
  }

  return Triple(detected, mutations.length - withoutCoverage, mutations.length)
}

/**
 * Share of mutants a test both executed and noticed. Unlike the raw mutation score this ignores
 * code no test reaches, which Kover already reports, so it answers the one question coverage
 * cannot: when a test does run this line, would it complain if the line were wrong?
 *
 * This is the same number PIT prints as "Test strength" in its console summary.
 */
tasks.register("printTestStrength") {
  group = "verification"
  dependsOn("pitest")
  doLast {
    val (detected, covered, _) = readMutationTotals()
    println("%.1f".format(Locale.US, if (covered == 0) 0.0 else (detected * 100.0) / covered))
  }
}

/**
 * Share of all mutants that were detected. Reported alongside the badge for context: a low score
 * next to a high test strength means untested code, not weak assertions.
 */
tasks.register("printMutationScore") {
  group = "verification"
  dependsOn("pitest")
  doLast {
    val (detected, _, total) = readMutationTotals()
    println("%.1f".format(Locale.US, if (total == 0) 0.0 else (detected * 100.0) / total))
  }
}
