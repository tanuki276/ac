pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.application") version "8.1.2" apply false
        id("com.android.library") version "8.1.2" apply false
        id("org.jetbrains.kotlin.android") version "1.9.10" apply false
        id("dagger.hilt.android.plugin") version "2.48" apply false
        id("com.google.gms.google-services") version "4.4.0" apply false
        id("com.google.firebase.crashlytics") version "2.9.9" apply false
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://maven.google.com")
    }
    versionCatalogs {
        create("libs") {
            version("androidx-core", "1.12.0")
            version("androidx-lifecycle", "2.7.0")
            version("androidx-activity", "1.8.0")
            version("androidx-room", "2.6.0")
            version("androidx-navigation", "2.7.4")
            version("androidx-paging", "3.2.1")
            version("androidx-datastore", "1.0.0")
            version("compose-bom", "2023.10.01")
            version("compose", "1.5.3")
            version("retrofit", "2.9.0")
            version("okhttp", "5.0.0-alpha.11")
            version("hilt", "2.48")
            version("coil", "2.5.0")
            version("lottie", "6.1.0")
            version("firebase-bom", "32.5.0")
            version("junit", "4.13.2")
            version("androidx-test", "1.5.2")
            version("androidx-espresso", "3.5.1")
        }
    }
}

rootProject.name = "NyankoWars"
include(":app")
