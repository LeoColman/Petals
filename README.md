Petals
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
