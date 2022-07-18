# Petals

[![License](https://img.shields.io/github/license/LeoColman/Petals)](https://github.com/LeoColman/Petals/blob/main/LICENSE)
[![Unit Tests](https://github.com/LeoColman/Petals/actions/workflows/unit-tests.yaml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/unit-tests.yaml)
[![Lint](https://github.com/LeoColman/Petals/actions/workflows/lint.yaml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/lint.yaml)
[![Dependency Analysis](https://github.com/LeoColman/Petals/actions/workflows/dependency-analysis.yaml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/dependency-analysis.yaml)

------

 The Open Source app Petals aims to help its users to either quit weed, reduce usage or simply know how much they're using. 
 
 Download from the [Releases Page](https://github.com/LeoColman/Petals/releases) or from any of the options below
 
 [<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/br.com.colman.petals/)
 [<img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png"
     alt="Get it on Play Store"
     height="80">](https://play.google.com/store/apps/details?id=br.com.colman.petals)    
    
     

## Building

The signed version (the one published to Github) can be built with:
```
./gradlew packageGithubReleaseUniversalApk
```

If you're building an unsigned version, comment all the signing configs and run
```
./gradlew packageFdroidReleaseUniversalApk
```
