# Petals

[![License](https://img.shields.io/github/license/LeoColman/Petals)](https://github.com/LeoColman/Petals/blob/main/LICENSE)
[![Unit Tests](https://github.com/LeoColman/Petals/actions/workflows/unit-tests.yaml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/unit-tests.yaml)
[![Detekt](https://github.com/LeoColman/Petals/actions/workflows/detekt.yaml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/detekt.yaml)
[![Dependency Analysis](https://github.com/LeoColman/Petals/actions/workflows/dependency-license-analysis.yaml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/dependency-analysis.yaml)
[![Weblate](https://hosted.weblate.org/widgets/petals-app/-/svg-badge.svg)](https://hosted.weblate.org/engage/petals-app/)
[![GitHub Repo stars](https://img.shields.io/github/stars/LeoColman/Petals?style=plastic)](https://star-history.com/#LeoColman/Petals&Date)
[![Gitmoji](https://img.shields.io/badge/gitmoji-%20😜%20😍-FFDD67.svg?style=plastic)](https://gitmoji.dev/)
[![Git Secrets](https://img.shields.io/badge/git%20secrets-enabled-green)](https://github.com/sobolevn/git-secret)
[![F-Droid](https://img.shields.io/f-droid/v/br.com.colman.petals)](https://f-droid.org/packages/br.com.colman.petals/)
[![GitHub Release](https://img.shields.io/github/v/release/LeoColman/Petals?label=github)](https://github.com/LeoColman/Petals/releases)
[![IzzyOnDroid Release](https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/br.com.colman.petals)](https://apt.izzysoft.de/fdroid/index/apk/br.com.colman.petals)
[![GitHub All Releases](https://img.shields.io/github/downloads/LeoColman/Petals/total?label=Downloads%20All%20Time%20(GitHub))](https://github.com/LeoColman/Petals/releases)
[![GitHub Release Downloads](https://img.shields.io/github/downloads/LeoColman/Petals/latest/total?label=Downloads%20Latest%20Release%20(GitHub))](https://github.com/LeoColman/Petals/releases/latest)
![Coverage](https://leocolman.github.io/Petals/coverage-badge.svg)
![Maintenance](https://img.shields.io/maintenance/yes/2025)
[![Alternatives](https://img.shields.io/badge/Alternative.to-6-blue)](https://alternativeto.net/software/petals-app/)



<p align="center" width="100%">
    <img  src="https://repobeats.axiom.co/api/embed/fc784a940119497476ba4d6694b88876e01aecbc.svg">
</p>

------
The Open Source app Petals aims to help its users to either quit weed, reduce usage or simply know how much they're
using.

[<img src="https://user-images.githubusercontent.com/1577251/236347752-5c312036-27d1-4515-ab86-dc2aa9a09e66.png" alt="Get it on GitHub" height="80">](https://github.com/LeoColman/Petals/releases)
[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/packages/br.com.colman.petals/)
[<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" alt="Get it on IzzyOnDroid" height="80">](https://apt.izzysoft.de/fdroid/index/apk/br.com.colman.petals)
[<img src="https://www.openapk.net/images/openapk-badge.png" alt="Get it on OpenAPK" height="80">](https://www.openapk.net/petals/br.com.colman.petals/)
[<img src="https://www.androidfreeware.net/images/androidfreeware-badge.png" alt="Get it on AndroidFreeware" height="80">](https://www.androidfreeware.net/download-petals-apk.html)
[<img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Play Store" height="80">](https://play.google.com/store/apps/details?id=br.com.colman.petals)

### ⚠️ The Google Play Store version contains advertisements

For an ad free experience download the app from other release channels.

## 🌐 Contributing Translations

[![Translation status](https://hosted.weblate.org/widgets/petals-app/-/287x66-grey.png)](https://hosted.weblate.org/engage/petals-app/)

Petals welcomes community contributions for translations! To contribute translations, please follow these guidelines:

- ✨ **New Languages**: If you are adding a new language, translations must be 100% complete before submission.
- ⚠️ **Minimum Completion**: Translations that fall below 60% completion will be removed to maintain quality.
- 🤝 **Contribution Methods**:
  - 🌍 Non-programmers are encouraged to collaborate via [Weblate](https://hosted.weblate.org/engage/petals-app/).
  - 🖥️ Programmers may contribute translations via pull requests directly on GitHub.

[![Translation Status](https://hosted.weblate.org/widgets/petals-app/-/multi-red.svg)](https://hosted.weblate.org/engage/petals-app/)

Translations are hosted at Weblate. You can contribute to your language [here](https://hosted.weblate.org/engage/petals-app/).


## Screenshots

<details>
<summary>Open to see screenshots</summary>

![](fastlane/metadata/android/en-US/images/phoneScreenshots/1.png)
![](fastlane/metadata/android/en-US/images/phoneScreenshots/2.png)
![](fastlane/metadata/android/en-US/images/phoneScreenshots/3.png)

[More Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots/)
</details>

## Building

You can assemble both debug and release versions of the app for different variants (F-Droid, Playstore, GitHub) using
the corresponding Gradle tasks. Here's how to do that:

### Debug Version

- F-Droid: `./gradlew assembleFdroidDebug`
- PlayStore: `./gradlew assemblePlaystoreDebug`
- GitHub: `./gradlew assembleGithubDebug`

### Release Version

For the release version, you must first decrypt secrets using `git secret reveal`. The release version can be assembled
as follows:

- F-Droid: `./gradlew assembleFdroidRelease`
- PlayStore: `./gradlew assemblePlaystoreRelease`
- GitHub: `./gradlew assembleGithubRelease`

## Git Secrets

The **Keystore**, **Keystore Properties**, and **Google Play deploy json** files are included in the repository using
[git secret](https://sobolevn.me/git-secret/). The current secret owners are:

- Leonardo Colman Lopes
    - Fingerprint `B3A5 9909 9ECC 4DB4 FD40 896F 7706 1922 C587 2792`
    - Original Author

- GitHub Actions
    - Fingerprint `882E 409C 71F4 565B 1698 B947 A992 5FF4 75B3 5E07`
    - Auto-publishing signed app to GitHub
    - Expires on 2027-04-05
    - Available on environment secret `GPG_KEY`

## Releasing

Release to all our channels is made automatically after a tag is released. The workflow responsible for doing this
is [release.main.kts](.github/workflows/release.main.kts).

### Google Play Store

1. Decrypt all git secrets
2. Run `./gradlew bundlePlaystoreRelease`
3. Publish to playstore by running
   `cd fastlane && bundle config path vendor/bundle && bundle install --jobs 4 --retry 3 && bundle exec fastlane playstore`

### F-Droid

F-Droid builds automatically from the repository whenever a new tag is published. The definitions for how to build the
app are
in [F-Droid's data repository](https://gitlab.com/fdroid/fdroiddata/-/blob/master/metadata/br.com.colman.petals.yml). If
modifications are required, the place to change is there.

### Releases Page

1. Decrypt all git secrets
2. Run `./gradlew assembleGithubRelease`
