# Petals

[![License](https://img.shields.io/github/license/LeoColman/Petals)](https://github.com/LeoColman/Petals/blob/main/LICENSE)
[![Unit Tests](https://github.com/LeoColman/Petals/actions/workflows/unit-tests.yml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/unit-tests.yml)
[![Lint](https://github.com/LeoColman/Petals/actions/workflows/lint.yml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/lint.yml)

------

 The Open Source app Petals aims to help its users to either quit weed, reduce usage or simply know how much they're using. 
 
 [<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/br.com.colman.petals/)

## Building

The signed version (the one published to Github) can be built with:
```
./gradlew packageGithubReleaseUniversalApk
```

If you're building an unsigned version, comment all the signing configs and run
```
./gradlew packageFdroidReleaseUniversalApk
```
