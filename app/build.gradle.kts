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
    // Standard dependencies from project start
    // This dependency enables data binding in Compose UI, simplifying data flow between UI and underlying data sources.
    implementation("androidx.databinding:adapters:3.2.0-alpha11")
    // Provides core functionalities for building AppCompat applications, supporting older Android versions
    implementation("androidx.appcompat:appcompat:1.6.1")
    // This dependency offers the latest Material Design 3 components for building modern and consistent user interfaces.
    implementation("androidx.compose.material3:material3:1.2.1")
    // Required for running unit tests in your project.
    implementation("androidx.test:runner:1.5.2")
    //  Provides additional Material Design components and themes for UI, old version, but still useful.
    implementation("com.google.android.material:material:1.11.0")


    // Provides libraries for implementing efficient pagination in app, handling large datasets.
    implementation("androidx.paging:paging-common-android:3.3.0-alpha04")

    // Integrates Firebase Authentication features like user login and registration.
    implementation("androidx.paging:paging-common-android:3.3.0-alpha04")

    implementation("androidx.compose.animation:animation-graphics-android:1.6.6")
    implementation("androidx.camera:camera-core:1.3.3")


    //Firebase Implementation
    // Enables displaying in-app messaging from Firebase to users.
    implementation("com.google.android.gms:play-services-cast-framework:21.4.0")
    implementation("com.google.firebase:firebase-inappmessaging-display:20.4.2")
    // Allows access to Firebase Realtime Database, a NoSQL database solution.
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-database:20.3.1")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    // Provides access to Firestore, a document-oriented NoSQL database solution for Firebase in Kotlin.
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.1")
    implementation("com.google.firebase:firebase-ml-vision:24.1.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.firebase:firebase-messaging-ktx")


    //Compose Platform and Core Dependencies
    // This line defines the Bill of Materials (BOM) for Compose, ensuring all Compose libraries used are compatible versions.
    platform("androidx.compose:compose-bom:2024.04.00")
    // Provides essential Kotlin extensions for core Android functionalities.
    platform("androidx.compose:compose-bom:2024.04.00")

    implementation("androidx.core:core-ktx:1.12.0")
    // Offers lifecycle-aware components for managing data across different UI lifecycles in Kotlin.
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    // Enables building Compose activities, the foundation for the app screens.
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.activity:activity-ktx:1.8.2")
    // The core library for building user interfaces with Compose.
    implementation ("androidx.compose.ui:ui:1.6.6")
    // Provides foundational building blocks for the Compose UI like layouts, drawables, and interactions.
    implementation ("androidx.compose.foundation:foundation:1.6.6")
    //  Provides additional Material Design components and themes for UI, old version, but still useful, not completely necessary as I have the others but still here.
    implementation ("androidx.compose.material:material:1.6.5")
    implementation("androidx.compose.material:material-icons-extended")
    // Enables implementing navigation within the Compose app.
    implementation("androidx.navigation:navigation-compose:2.7.7")

    //Testing Dependencies:
    // The classic JUnit library for writing unit tests.
    testImplementation("junit:junit:4.13.2")
    // Enables writing UI tests for your Compose UI.
    testImplementation("org.mockito:mockito-core:3.3.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    // Provides testing utilities for navigation within Compose apps.
    implementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.7")
    // A library for asserting test conditions in a concise and readable way.
    androidTestImplementation("com.google.truth:truth:1.1.3")
    // Required for UI tooling features in debug builds.
    debugImplementation("androidx.compose.ui:ui-tooling")
    // Necessary for UI test manifest generation in debug builds.
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Provides integration between Compose and LiveData, a lifecycle-aware way to observe data changes.
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    implementation("androidx.compose.runtime:runtime-livedata")
    // Enables using ViewModels with Compose, managing UI state across lifecycle changes.
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")

    // Foundation for working with coroutines, enabling asynchronous programming.
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    // Integrates coroutines with Android's UI framework.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")


    // Enables using ViewModels for UI state management in Compose.
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    // Provides LiveData, a lifecycle-aware data holder.
    implementation ("androidx.lifecycle:lifecycle-livedata-core-ktx:2.7.0")
    // Bridges LiveData with Compose for seamless observation.
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.7")

    //Firebase Implementation
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")

    //Image implementation
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-alpha.5")
    implementation("com.squareup.picasso:picasso:2.5.2")
    implementation("io.coil-kt:coil:2.6.0")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    implementation("com.google.mlkit:text-recognition:16.0.0")

    implementation ("com.google.android.gms:play-services-vision:20.1.3")
    implementation ("com.google.firebase:firebase-ml-vision:24.1.0")
    implementation ("com.google.firebase:firebase-ml-vision-barcode-model:16.1.2")

}