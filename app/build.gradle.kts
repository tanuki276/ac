pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    
    plugins {
        id("com.android.application") version "8.1.2"
        id("com.android.library") version "8.1.2"
        id("org.jetbrains.kotlin.android") version "1.9.10"
        id("dagger.hilt.android.plugin") version "2.48"
        id("com.google.gms.google-services") version "4.4.0"
        id("com.google.firebase.crashlytics") version "2.9.9"
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
            // AndroidX
            version("androidx-core", "1.12.0")
            version("androidx-lifecycle", "2.7.0")
            version("androidx-activity", "1.8.0")
            version("androidx-room", "2.6.0")
            version("androidx-navigation", "2.7.4")
            version("androidx-paging", "3.2.1")
            version("androidx-datastore", "1.0.0")
            
            // Compose
            version("compose-bom", "2023.10.01")
            version("compose", "1.5.3")
            
            // Network
            version("retrofit", "2.9.0")
            version("okhttp", "5.0.0-alpha.11")
            
            // DI
            version("hilt", "2.48")
            
            // Image
            version("coil", "2.5.0")
            
            // Animation
            version("lottie", "6.1.0")
            
            // Firebase
            version("firebase-bom", "32.5.0")
            
            // Testing
            version("junit", "4.13.2")
            version("androidx-test", "1.5.2")
            version("androidx-espresso", "3.5.1")
        }
    }
}

rootProject.name = "NyankoWars"
include(":app")