#!/usr/bin/env kotlin

import java.io.File
import kotlin.system.exitProcess

val buildGradleFile = File("app/build.gradle.kts")
val changelogs = "fastlane/metadata/android/en-US/changelogs"
val buildGradleContent = buildGradleFile.readText()

val versionCodeRegex = Regex("""versionCode\s*=\s*(\d+)""")
val versionNameRegex = Regex("""versionName\s*=\s*"([\d.]+)"""")

val currentVersionCode = versionCodeRegex.find(buildGradleContent)!!.groupValues[1].toInt()
val currentVersionName = versionNameRegex.find(buildGradleContent)!!.groupValues[1]

println("Current version: $currentVersionName ($currentVersionCode)")
println("Detecting bump type...")
var bumpType = args.getOrNull(0)
if(bumpType == null) {
  println("BUMP_TYPE not set, input one of: major, minor, patch")
  exitProcess(1)
}

check(bumpType in listOf("major", "minor", "patch")) { "BUMP_TYPE must be one of: major, minor, patch" }


val versionParts = currentVersionName.split(".").map { it.toIntOrNull() ?: 0 }
val (newMajor, newMinor, newPatch) = when (bumpType) {
  "major" -> Triple(versionParts[0] + 1, 0, 0)
  "minor" -> Triple(versionParts[0], versionParts[1] + 1, 0)
  "patch" -> Triple(versionParts[0], versionParts[1], versionParts[2] + 1)
  else -> throw IllegalStateException("Unknown bump type: $bumpType")
}
val newVersionName = "$newMajor.$newMinor.$newPatch"
println("New version: $newVersionName")




val newVersionCode = "${newMajor * 1000000 + newMinor * 1000 + newPatch}"
println("New version code: $newVersionCode")


val updatedContent = buildGradleContent
  .replace(versionCodeRegex, "versionCode = $newVersionCode")
  .replace(versionNameRegex, "versionName = \"$newVersionName\"")

buildGradleFile.writeText(updatedContent)


val changelogFile = File(changelogs, "$newVersionCode.txt")
if (!changelogFile.exists()) {
  println("Creating changelog file... $changelogFile")
  changelogFile.createNewFile()
}

val changelog = args.getOrNull(1)
if(changelog != null) {
  println("Writing changelog...")
  changelogFile.writeText(changelog)
}

ProcessBuilder("git", "add", buildGradleFile.absolutePath, changelogFile.absolutePath).inheritIO().start().waitFor()
ProcessBuilder("git", "commit", "-m", "🔖 Prepare Release $newVersionName ($newVersionCode)").inheritIO().start().waitFor()
ProcessBuilder("git", "tag", "-a", newVersionName, "-m", "Release version $newVersionName").inheritIO().start().waitFor()
ProcessBuilder("git", "push", "origin", "main", "--tags").inheritIO().start().waitFor()
