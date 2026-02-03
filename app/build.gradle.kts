cat <<EOF > app/build.gradle.kts
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // settingsで定義したバージョンが自動適用されます
}

android {
    namespace = "com.nyankowars"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nyankowars"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // settingsで定義したカタログ(libs)を使う
    implementation(libs.findLibrary("androidx-core").get())
    implementation(libs.findLibrary("androidx-lifecycle").get())
}
EOF
