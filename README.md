# Petals

[![License](https://img.shields.io/github/license/LeoColman/Petals)](https://github.com/LeoColman/Petals/blob/main/LICENSE)
[![Unit Tests](https://github.com/LeoColman/Petals/actions/workflows/unit-tests.yaml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/unit-tests.yaml)
[![Lint](https://github.com/LeoColman/Petals/actions/workflows/lint.yaml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/lint.yaml)
[![Dependency Analysis](https://github.com/LeoColman/Petals/actions/workflows/dependency-analysis.yaml/badge.svg)](https://github.com/LeoColman/Petals/actions/workflows/dependency-analysis.yaml)

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/K3K62Y2GB)

------

The Open Source app Petals aims to help its users to either quit weed, reduce usage or simply know
how much they're using.

Download from the [Releases Page](https://github.com/LeoColman/Petals/releases) or from any of the
options below

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
alt="Get it on F-Droid"
height="80">](https://f-droid.org/packages/br.com.colman.petals/)
[<img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png"
alt="Get it on Play Store"
height="80">](https://play.google.com/store/apps/details?id=br.com.colman.petals)

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
The signed version (the one published to Github and PlayStore) can be built with:

```
./gradlew packageGithubReleaseUniversalApk
```

You must first decrypt secrets using `git secret reveal`

### Unsigned
If you're building an unsigned version, build the same one that goes to FDroid (as FDroid signs the
app themselves).

```
./gradlew packageFdroidReleaseUniversalApk
```

This approach doesn't require secrets.

## Git Secrets

The **Keystore**, **Keystore Properties** and **Google Play deploy json** files are included in the
repository using
[git secret](https://git-secret.io/). The current secret owners are:

- Leonardo Colman Lopes
    - Fingerprint `B3A5 9909 9ECC 4DB4 FD40 896F 7706 1922 C587 2792`
    - Original Author

- Github Actions
    - Fingerprint `1FF0 67E9 C75F A1BC 51D2 FC75 BA87 7D2B 9560 920A`
    - Auto-publishing signed app to github
    - Expires on 2024-07-29
    - Available on environment secret `GPG_KEY`