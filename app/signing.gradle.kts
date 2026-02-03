import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
}

// ローカルプロパティから署名情報を取得
val localProperties = gradleLocalProperties(rootDir)

// デバッグ署名設定
android {
    signingConfigs {
        create("debug") {
            storeFile = file("${rootDir}/keystore/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        
        // GitHub Actions用署名設定
        create("ciRelease") {
            storeFile = file(System.getenv("RELEASE_STORE_FILE") ?: "debug.keystore")
            storePassword = System.getenv("RELEASE_STORE_PASSWORD") ?: "android"
            keyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: "androiddebugkey"
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD") ?: "android"
        }
        
        // 本番リリース署名設定
        create("release") {
            storeFile = file(localProperties.getProperty("RELEASE_STORE_FILE") ?: "${rootDir}/keystore/release.keystore")
            storePassword = localProperties.getProperty("RELEASE_STORE_PASSWORD") ?: ""
            keyAlias = localProperties.getProperty("RELEASE_KEY_ALIAS") ?: ""
            keyPassword = localProperties.getProperty("RELEASE_KEY_PASSWORD") ?: ""
        }
    }
    
    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        
        release {
            // GitHub ActionsではciRelease、ローカルではreleaseを使用
            val configName = if (System.getenv("GITHUB_ACTIONS") == "true") {
                "ciRelease"
            } else {
                "release"
            }
            signingConfig = signingConfigs.getByName(configName)
            
            // 難読化設定
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

// 署名検証タスク
tasks.register("validateSigning") {
    group = "verification"
    description = "Validate signing configuration"
    
    doLast {
        val releaseSigning = android.signingConfigs.getByName("release")
        
        if (!releaseSigning.storeFile.exists()) {
            throw GradleException("Release keystore file not found: ${releaseSigning.storeFile}")
        }
        
        if (releaseSigning.storePassword.isNullOrEmpty()) {
            throw GradleException("Release keystore password is empty")
        }
        
        if (releaseSigning.keyAlias.isNullOrEmpty()) {
            throw GradleException("Release key alias is empty")
        }
        
        if (releaseSigning.keyPassword.isNullOrEmpty()) {
            throw GradleException("Release key password is empty")
        }
        
        println("✓ Signing configuration is valid")
        println("  Keystore: ${releaseSigning.storeFile}")
        println("  Alias: ${releaseSigning.keyAlias}")
    }
}

// APK署名後の検証タスク
tasks.register("verifyReleaseApk") {
    group = "verification"
    description = "Verify signed APK"
    
    dependsOn("assembleRelease")
    
    doLast {
        val apkFile = file("${project.buildDir}/outputs/apk/release/app-release.apk")
        
        if (!apkFile.exists()) {
            throw GradleException("Release APK not found: $apkFile")
        }
        
        println("✓ Release APK generated: $apkFile")
        println("  Size: ${apkFile.length() / 1024} KB")
    }
}