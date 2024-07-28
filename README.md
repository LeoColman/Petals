# Petals

[![License](https://img.shields.io/github/license/LeoColman/Petals)](https://github.com/LeoColman/Petals/blob/main/LICENSE)
[![Unit Tests](https://github.com/LeoColman/Petals/actions/workflows/unit-tests.yaml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/unit-tests.yaml)
[![Lint](https://github.com/LeoColman/Petals/actions/workflows/lint.yaml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/lint.yaml)
[![Dependency Analysis](https://github.com/LeoColman/Petals/actions/workflows/dependency-license-analysis.yaml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/dependency-analysis.yaml)
<a href="https://hosted.weblate.org/engage/petals-app/">
<img src="https://hosted.weblate.org/widgets/petals-app/-/svg-badge.svg" alt="Translation status" />
</a>
[![GitHub Repo stars](https://img.shields.io/github/stars/LeoColman/Petals?style=plastic)](https://star-history.com/#LeoColman/Petals&Date)
[![Gitmoji](https://img.shields.io/badge/gitmoji-%20😜%20😍-FFDD67.svg?style=plastic)](https://gitmoji.dev/)
[![Git Secrets](https://img.shields.io/badge/git%20secrets-enabled-green)](https://github.com/sobolevn/git-secret)
[![Fdroid](https://img.shields.io/f-droid/v/br.com.colman.petals)](https://f-droid.org/packages/br.com.colman.petals/)
[![GitHub Release](https://img.shields.io/github/v/release/LeoColman/Petals?label=github)](https://github.com/LeoColman/Petals/releases)




<a href="https://www.producthunt.com/posts/petals?utm_source=badge-featured&utm_medium=badge&utm_souce=badge-petals" target="_blank"><img src="https://api.producthunt.com/widgets/embed-image/v1/featured.svg?post_id=356573&theme=light" alt="Petals - An&#0032;open&#0032;source&#0032;app&#0032;to&#0032;help&#0032;users&#0032;quit&#0032;or&#0032;control&#0032;weed&#0032;usage | Product Hunt" style="width: 250px; height: 54px;" width="250" height="54" /></a>
[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/K3K62Y2GB)


------

The Open Source app Petals aims to help its users to either quit weed, reduce usage or simply know how much they're
using.


[<img src="https://user-images.githubusercontent.com/1577251/236347752-5c312036-27d1-4515-ab86-dc2aa9a09e66.png"
alt="Get it on GitHub"
height="80">](https://github.com/LeoColman/Petals/releases)
[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
alt="Get it on F-Droid"
height="80">](https://f-droid.org/packages/br.com.colman.petals/)
[<img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png"
alt="Get it on Play Store"
height="80">](https://play.google.com/store/apps/details?id=br.com.colman.petals)

### ⚠️ The Google Play Store version contains advertisements

For an ad free experience download the app either from F-Droid or from the Releases Page.

## Translations
Translations are hosted at Weblate. You can contribute to your language [here](https://hosted.weblate.org/engage/petals-app)

<a href="https://hosted.weblate.org/engage/petals-app/">
<img src="https://hosted.weblate.org/widgets/petals-app/-/287x66-grey.png" alt="Translation status" />
</a>

<br>

<a href="https://hosted.weblate.org/engage/petals-app/">
<img src="https://hosted.weblate.org/widgets/petals-app/-/multi-red.svg" alt="Translation status" />
</a>

## Screenshots

<details>
<summary>Open to see screenshots</summary>

![](fastlane/metadata/android/en-US/images/phoneScreenshots/1.png)
![](fastlane/metadata/android/en-US/images/phoneScreenshots/2.png)
![](fastlane/metadata/android/en-US/images/phoneScreenshots/3.png)

[More Screenshots](fastlane/metadata/android/en-US/images/phoneScreenshots)

</details>

## Building

### Signed

The signed version (the one published to GitHub and PlayStore) can be built with:

```
./gradlew packageGithubReleaseUniversalApk
```

You must first decrypt secrets using `git secret reveal`

### Unsigned

If you're building an unsigned version, build the same one that goes to FDroid (as FDroid signs the app themselves).

```
./gradlew packageFdroidReleaseUniversalApk
```

This approach doesn't require secrets.

## Git Secrets

The **Keystore**, **Keystore Properties** and **Google Play deploy json** files are included in the repository using
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

Release to all our channels are made automatically after a tag is released. The workflow responsible for doing that
is [release.main.kts](.github/workflows/release.main.kts).

### Google Play Store

1. Decrypt all git secrets
2. Run `./gradlew packageGithubReleaseUniversalApk`
3. Publish to playstore
   running `bundle config path vendor/bundle && bundle install --jobs 4 --retry 3 && bundle exec fastlane playstore`

### F-Droid

F-Droid builds automatically from the repository whenever a new tag is published. The definitions for how to build the
app are
in [FDroid's data repository](https://gitlab.com/fdroid/fdroiddata/-/blob/master/metadata/br.com.colman.petals.yml). If
modifications are required, the place to change is there.

### Releases Page
1. Decrypt all git secrets
2. Run `./gradlew packageGithubReleaseUniversalApk`
