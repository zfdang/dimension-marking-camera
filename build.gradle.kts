buildscript {
    val agp_version by extra("8.10.1")
    repositories {
        google()
        mavenCentral()
    }
}

// Top-level build file
plugins {
    id("com.android.application") version "8.10.1" apply false
    // Update Kotlin to 2.1.0 to match recent library metadata
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    // Compose compiler Gradle plugin (required for Kotlin >= 2.0 with compose enabled)
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
}
