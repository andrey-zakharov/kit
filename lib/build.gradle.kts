plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    alias(libs.plugins.multiplatform)
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

val osName = System.getProperty("os.name")
val targetOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

val osArch = System.getProperty("os.arch")
var targetArch = when (osArch) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}
val group = "me.zakharov"
val version = "0.7.9" // or any more recent version
val target = "${targetOs}-${targetArch}"

kotlin {
    jvm()
    js {
        browser()
    }

    wasmJs {
        browser()
        nodejs()
        d8()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(kotlin("stdlib-common"))
                //implementation("org.jetbrains.skiko:skiko-awt-runtime-$target:$version")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test")) // This brings all the platform dependencies automatically
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}



testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("1.9.10")
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
