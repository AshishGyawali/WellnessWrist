plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.wellnesswrist"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.wellnesswrist"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

    }

    buildFeatures {
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.play.services.wearable)
    implementation(libs.wear)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.lifecycle.viewmodel)
//    implementation(libs.library)
}