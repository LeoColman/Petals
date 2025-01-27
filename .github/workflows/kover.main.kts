#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:3.2.0")

@file:Repository("https://bindings.krzeminski.it")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("gradle:gradle-build-action:v3")
@file:DependsOn("peaceiris:actions-gh-pages:v3")


import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.gradle.GradleBuildAction
import io.github.typesafegithub.workflows.actions.peaceiris.ActionsGhPages
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.expr
import io.github.typesafegithub.workflows.dsl.workflow


workflow(
  name = "Kover Coverage",
  on = listOf(Push(branches = listOf("main"))),
  sourceFile = __FILE__
) {
  job(id = "analyse", runsOn = RunnerType.UbuntuLatest) {
    uses(name = "Set up JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(action = Checkout())
    uses(action = GradleBuildAction(arguments = "koverHtmlReport"))
    run(command = "cat app/build/reports/kover/html/index.html >> \$GITHUB_STEP_SUMMARY")

    uses(action = GradleBuildAction(arguments = "koverXmlReport"))
    run(
      name = "Generate coverage output",
      command = """
                COVERAGE=$(${expr { github.workspace }}/gradlew -q printLineCoverage)
                echo "Raw Coverage: ${'$'}COVERAGE"
                COVERAGE=${'$'}(printf "%.0f" "${'$'}COVERAGE")  # Round to integer
                echo "Rounded Coverage: ${'$'}COVERAGE"
                echo "COVERAGE=${'$'}COVERAGE" >> ${'$'}GITHUB_ENV
            """.trimIndent()
    )

    run(
      name = "Generate Coverage Badge",
      command = """
                mkdir -p badge
                COLOR=$(
                    if [ "${'$'}COVERAGE" -ge 90 ]; then
                        echo "brightgreen"
                    elif [ "${'$'}COVERAGE" -ge 70 ]; then
                        echo "yellow"
                    else
                        echo "red"
                    fi
                )
                URL="https://img.shields.io/badge/Coverage-${'$'}COVERAGE%25-${'$'}COLOR"
                curl -sS "${'$'}URL" -o badge/coverage-badge.svg
            """.trimIndent()
    )

    uses(
      name = "Deploy Badge to GitHub Pages",
      action = ActionsGhPages(
        githubToken = expr { secrets.GITHUB_TOKEN },
        publishDir = "badge",
        publishBranch = "gh-pages",
        allowEmptyCommit = false,
        forceOrphan = true
      )
    )
  }
}
