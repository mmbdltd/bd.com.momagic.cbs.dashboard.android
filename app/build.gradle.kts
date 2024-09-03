plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "bd.com.momagic.cbs.dashboard.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "bd.com.momagic.cbs.dashboard.android"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "PROFILE", "\"development\"")
        }

        release {
            isMinifyEnabled = false
            buildConfigField("String", "PROFILE", "\"production\"")
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

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.jackson.dataformat.xml)
    implementation(libs.jackson.dataformat.csv)
    implementation(libs.slf4j.android)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.swipe.refresh.layout)
}
