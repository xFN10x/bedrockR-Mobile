
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "fn10.bedrockrmobile"
    compileSdk = 36

    packaging {
        resources {
            excludes += "fn10.bedrockr/Launcher.class"
        }
    }

    defaultConfig {
        applicationId = "fn10.bedrockrmobile"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "Ma1.0 (a2.0)"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("x86", "armeabi-v7a", "arm64-v8a", "x86_64")
        }
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    kotlin {
        jvmToolchain(25)
    }

    testOptions {
        unitTests.all {
            it.failOnNoDiscoveredTests = false
        }
    }
}


dependencies {
    coreLibraryDesugaring(libs.desugarjdklibs)

    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation(libs.guava)
    implementation(libs.commons.io)
    implementation(libs.commons.lang3)
    implementation(libs.bedrockr)
    // https://mvnrepository.com/artifact/net.lingala.zip4j/zip4j
    implementation(libs.zip4j)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.core.ktx)
    implementation(libs.coordinatorlayout)
    implementation(libs.constraintlayout)
    implementation(libs.gson)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}
