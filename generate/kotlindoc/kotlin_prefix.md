# Astronomy Engine (Kotlin Multiplatform)

[![JitPack build status](https://jitpack.io/v/cosinekitty/astronomy.svg)](https://jitpack.io/#cosinekitty/astronomy)

---

## Quick Start

Here are some [Kotlin examples](../../demo/kotlin/) and [Java examples](../../demo/java/) to get you started.

Feel free to [start a new discussion topic](https://github.com/cosinekitty/astronomy/discussions)
if you need some help in your astronomy-related project, either with this code or astronomy
computation concepts in general.

## Installation

Astronomy Engine is a Kotlin Multiplatform library. It supports these platforms:

- JVM (Java 17 or newer)
- Android (API level 23 or newer)
- iOS (`iosArm64`, `iosSimulatorArm64`) and macOS (`macosArm64`)
- Linux (`linuxX64`) and Windows (`mingwX64`)
- WebAssembly (`wasmJs`, `wasmWasi`)

It is published through JitPack. Add the JitPack repository to your `settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

In a Kotlin Multiplatform project, add the dependency to `commonMain`:
```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.cosinekitty:astronomy:[ASTRONOMY_ENGINE_VERSION]")
        }
    }
}
```

In a JVM or Android project:
```kotlin
dependencies {
    implementation("io.github.cosinekitty:astronomy:[ASTRONOMY_ENGINE_VERSION]")
}
```

Gradle reads the library's Gradle module metadata and picks the right artifact for
each platform. Build tools that don't read it, such as Maven, need the platform
artifact named explicitly, for example `io.github.cosinekitty:astronomy-jvm`.
For other build tools, see the [JitPack page](https://jitpack.io/#cosinekitty/astronomy).

JitPack builds on Linux, and Kotlin/Native can only compile the iOS and macOS targets
on a Mac, so JitPack does not provide those targets. To use Astronomy Engine on
iOS or macOS, build it from source on a Mac with `./gradlew publishToMavenLocal`
in the `source/kotlin` directory, and add `mavenLocal()` to your repositories.

---

## Contents

- [Installation](#installation)
- [Coordinate Transforms](#coords)
- [Gravity Simulator](#gravsim)
- [Types](#types)
- [Functions](#functions)
- [Properties](#properties)

---

<a name="coords"></a>
## Coordinate Transforms

The following orientation systems are supported.
Astronomy Engine can convert a vector from any of these orientations to any of the others.
It also allows converting from a vector to spherical (angular) coordinates and back,
within a given orientation. Note the 3-letter codes for each of the orientation systems;
these are used in function and type names.

- **EQJ = J2000 Mean Equator**: Uses the Earth's mean equator (corrected for precession but ignoring nutation) on January 1, 2000, at noon UTC. This moment in time is called J2000.
- **EQD = True Equator of Date**: Uses the Earth's equator on a given date and time, adjusted for precession and nutation.
- **ECL = J2000 Mean Ecliptic**: Uses the plane of the Earth's orbit around the Sun at J2000. The x-axis is referenced against the J2000 mean equinox.
- **ECT = True Ecliptic of Date**: Uses the true (corrected for precession and nutation) orbital plane of the Earth on the given date. The x-axis is referenced against the true equinox for that date.
- **HOR = Horizontal**: Uses the viewpoint of an observer at a specific location on the Earth at a given date and time.
- **GAL = Galactic**: Based on the IAU 1958 definition of galactic coordinates.

<a name="gravsim"></a>
## Gravity Simulator

Astronomy Engine provides a [GravitySimulator](doc/-gravity-simulator/index.md) class
that allows you to model the trajectories of one or more small bodies like asteroids,
comets, or coasting spacecraft. If you know an initial position vector
and velocity vector for a small body, the gravity simulator can incrementally
simulate the pull of gravity on it from the Sun and planets, to calculate its
movement through the Solar System.

---
