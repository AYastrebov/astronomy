@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

import org.jetbrains.dokka.gradle.formats.DokkaFormatPlugin
import org.jetbrains.dokka.gradle.internal.InternalDokkaGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.dokka)
    `maven-publish`
}

group = "io.github.cosinekitty"
version = "2.1.19"

repositories {
    mavenCentral()
    google()
}

// The tests read fixtures from (and write results to) the repo's generate/
// directory. A relative path only works on JVM, where Gradle controls the
// working directory: wasm tests run from build/wasm/packages/..., and iOS
// simulator tests run inside the simulator. Bake in an absolute path instead.
val dataRootDir = layout.projectDirectory.dir("../../generate").asFile.canonicalFile.invariantSeparatorsPath + "/"

val generateTestConfig by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/testConfig/kotlin")
    inputs.property("dataRootDir", dataRootDir)
    outputs.dir(outputDir)
    doLast {
        val file = outputDir.get().file("io/github/cosinekitty/astronomy/TestConfig.kt").asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            |package io.github.cosinekitty.astronomy
            |
            |internal const val dataRootDir = "$dataRootDir"
            |""".trimMargin()
        )
    }
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
    }

    android {
        namespace = "io.github.cosinekitty.astronomy"
        compileSdk = 36
        minSdk = 23
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
    jvm {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
    iosArm64()
    iosSimulatorArm64()
    macosArm64()
    linuxX64()
    mingwX64()
    wasmJs { nodejs() }
    wasmWasi { nodejs() }

    sourceSets {
        commonTest {
            kotlin.srcDir(generateTestConfig)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.io.core)
        }
    }
}

// WASI only lets a module open files under directories the host "preopens",
// and the launcher that the Kotlin plugin generates for wasmWasi tests preopens
// nothing (with no setting to change that). Grant it the fixture directory.
tasks.named("compileTestDevelopmentExecutableKotlinWasmWasi") {
    val launchers = outputs.files.asFileTree.matching { include("**/*-test.mjs") }
    doLast {
        launchers.forEach { launcher ->
            val original = launcher.readText()
            val patched = original.replace(
                "new WASI({ version: 'preview1', args: argv, env, })",
                "new WASI({ version: 'preview1', args: argv, env, preopens: { '$dataRootDir': '$dataRootDir' } })"
            )
            check(patched != original) { "Unexpected wasmWasi test launcher format in $launcher" }
            launcher.writeText(patched)
        }
    }
}

// Dokka 2 has no built-in Markdown task; the GFM output feeds generate/kotlindoc.
// This declaration is the one recommended by the Dokka GFM plugin README.
@OptIn(InternalDokkaGradlePluginApi::class)
abstract class DokkaMarkdownPlugin : DokkaFormatPlugin(formatName = "markdown") {
    override fun DokkaFormatPlugin.DokkaFormatPluginContext.configure() {
        project.dependencies {
            dokkaPlugin(dokka("gfm-plugin"))
            formatDependencies.dokkaPublicationPluginClasspathApiOnly.dependencies.addLater(
                dokka("gfm-template-processing-plugin")
            )
        }
    }
}
apply<DokkaMarkdownPlugin>()

dokka {
    dokkaSourceSets.configureEach {
        reportUndocumented.set(true)
    }
}

// NOTE: do not override artifactId here. The Kotlin Multiplatform plugin creates
// one publication per target and derives its coordinates automatically
// (astronomy, astronomy-jvm, astronomy-android, astronomy-linuxx64, ...).
// Forcing `artifactId = project.name` collapses every target onto the single
// coordinate io.github.cosinekitty:astronomy, so the targets overwrite each
// other and consumers resolve whichever variant happened to publish last.
// groupId and version already come from the project-level `group`/`version`.
