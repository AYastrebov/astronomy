@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.kmp.library)
    `maven-publish`
}

group = "io.github.cosinekitty"
version = "2.1.19"

repositories {
    mavenCentral()
    google()
}

kotlin {
    android {
        namespace = "io.github.cosinekitty.astronomy"
        compileSdk = 36
        minSdk = 23
    }
    jvm()
    iosArm64()
    iosSimulatorArm64()
    macosArm64()
    linuxX64()
    mingwX64()
    wasmJs { nodejs() }
    wasmWasi { nodejs() }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.io.core)
        }
    }
}

tasks.withType<Test>().configureEach {
    workingDir = projectDir
}

publishing {
    publications {
        withType<MavenPublication> {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}
