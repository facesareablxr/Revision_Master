plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "uk.ac.aber.dcs.cs31620.revisionmaster"
    compileSdk = 34

    defaultConfig {
        applicationId = "uk.ac.aber.dcs.cs31620.revisionmaster"
        minSdk = 27
        //noinspection OldTargetApi
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        allWarningsAsErrors = false
        freeCompilerArgs += "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Data binding for Compose UI, simplifying data flow.
    implementation("androidx.databinding:adapters:3.2.0-alpha11")

    // Core functionalities for building AppCompat applications.
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Material Design 3 components for modern UIs.
    implementation("androidx.compose.material3:material3:1.2.1")

    // For running unit tests.
    implementation("androidx.test:runner:1.5.2")

    // Additional Material Design components and themes.
    implementation("com.google.android.material:material:1.12.0")

    // Libraries for efficient pagination.
    implementation("androidx.paging:paging-common-android:3.3.0-rc01")

    // Firebase Authentication integration.
    implementation("androidx.paging:paging-common-android:3.3.0-rc01")

    // Animation graphics for Compose.
    implementation("androidx.compose.animation:animation-graphics-android:1.6.7")

    // Core of CameraX library.
    implementation("androidx.camera:camera-core:1.3.3")

    // Coil integration for Compose.
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Firebase dependencies
    implementation("com.google.android.gms:play-services-cast-framework:21.4.0")
    implementation("com.google.firebase:firebase-inappmessaging-display:21.0.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    implementation("com.google.firebase:firebase-ml-vision:24.1.0")
    implementation("com.google.firebase:firebase-storage-ktx:21.0.0")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")

    // Compose Platform and Core Dependencies
    // Bill of Materials (BOM) for Compose.
    platform("androidx.compose:compose-bom:2024.05.00")

    // Kotlin extensions for core Android.
    implementation("androidx.core:core-ktx:1.13.1")

    // Lifecycle components for managing data across UI lifecycles.
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Compose activities for app screens.
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.activity:activity-ktx:1.9.0")

    // Core library for building UI with Compose.
    implementation ("androidx.compose.ui:ui:1.6.7")

    // Foundational building blocks for Compose UI.
    implementation ("androidx.compose.foundation:foundation:1.6.7")

    // Material Design components for Compose UI.
    implementation ("androidx.compose.material:material:1.6.7")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation in Compose app.
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Testing Dependencies
    // JUnit for unit tests.
    testImplementation("junit:junit:4.13.2")

    // Mockito for UI tests.
    testImplementation("org.mockito:mockito-core:3.3.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    // Utilities for navigation testing.
    implementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.7")

    // Assertion library.
    androidTestImplementation("com.google.truth:truth:1.1.3")

    // Debugging tools.
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Integration between Compose and LiveData.
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // ViewModels with Compose.
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")

    // Coroutines for asynchronous programming.
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // LiveData for UI state management.
    implementation("androidx.lifecycle:lifecycle-livedata-core-ktx:2.7.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.7")

    // Firebase BOM.
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")

    // Image loading libraries.
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-alpha.5")
    implementation("com.squareup.picasso:picasso:2.5.2")
    implementation("io.coil-kt:coil-gif:2.1.0")

    // More Compose lifecycle components.
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // ML Kit dependencies.
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation("com.google.android.gms:play-services-vision:20.1.3")
    implementation("com.google.firebase:firebase-ml-vision-barcode-model:16.1.2")
}
