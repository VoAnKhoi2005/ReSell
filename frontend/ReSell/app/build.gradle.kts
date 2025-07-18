import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.hilt)
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.resell"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.resell"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val baseUrl: String = gradleLocalProperties(rootDir, providers).getProperty("BASE_URL") ?: error("BASE_URL missing in local.properties")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.foundation.layout)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.security.crypto.ktx)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.hilt)
    kapt(libs.hiltCompiler)
    implementation(libs.androidxHiltNavigationCompose)
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.coroutines)
    implementation(libs.coil)
    implementation(libs.converter.gson)

    // Arrow
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)
    implementation(libs.okhttp)

    //Moshi
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.converter.moshi)

    //Firebase
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    //implementation(libs.androidx.credentials.play.services.auth)

    implementation(libs.accompanist.permissions)
    implementation(libs.play.services.location)

    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
    implementation (libs.play.services.auth)
    implementation ("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation ("com.google.android.gms:play-services-auth:21.0.0")
    implementation (libs.recaptcha)

    implementation ("androidx.credentials:credentials:1.2.0-alpha02")
    implementation ("androidx.credentials:credentials-play-services-auth:1.2.0-alpha02")

    implementation(libs.logging.interceptor)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-process:2.9.1")
}
