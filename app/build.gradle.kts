plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    kotlin("kapt")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.engineerfred.kotlin.next"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.engineerfred.kotlin.next"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val hilt = "2.48.1"
    val composeNavigation = "2.7.7"
    val hiltComposeNavigation = "1.1.0"
    val coroutines = "1.7.3"
    val room = "2.6.0"
    val worker = "2.8.1"

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //dagger-hilt
    implementation("com.google.dagger:hilt-android:${hilt}")
    implementation("androidx.hilt:hilt-navigation-compose:${hiltComposeNavigation}")
    kapt("com.google.dagger:hilt-android-compiler:${hilt}")

    //view model
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    //navigation
    implementation(libs.composeNavigation)

    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutines}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutines}")

    //splashScreen
    implementation("androidx.core:core-splashscreen:1.1.0-alpha02")

    //dataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //more material icons
    implementation("androidx.compose.material:material-icons-extended")

    //coil
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("io.coil-kt:coil-video:2.4.0")

    //exoPlayer
    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")
    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.2.1")

    //room
    implementation("androidx.room:room-runtime:$room")
    implementation("androidx.room:room-ktx:$room")
    ksp("androidx.room:room-compiler:$room")

    // Kotlin + coroutines -workerManager
    implementation("androidx.work:work-runtime-ktx:$worker")
    implementation("androidx.hilt:hilt-common:1.1.0")

    //permissions
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")

    //firebase
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    implementation("com.google.ai.client.generativeai:generativeai:0.1.2")
}

kapt {
    correctErrorTypes = true
}