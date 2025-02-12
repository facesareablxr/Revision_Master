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
    implementation("androidx.databinding:adapters:3.2.0-alpha11")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.paging:paging-common-android:3.3.0-alpha04")
    implementation("androidx.compose.animation:animation-graphics-android:1.6.7")
    platform("androidx.compose:compose-bom:2024.05.00")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation ("androidx.compose.ui:ui:1.6.7")
    implementation ("androidx.compose.foundation:foundation:1.6.7")
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("androidx.compose.material:material:1.6.7")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
    implementation ("androidx.lifecycle:lifecycle-livedata-core-ktx:2.7.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.7")
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation ("com.google.android.gms:play-services-vision:20.1.3")

    //Image implementation
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:compose:1.0.0-alpha.5")
    implementation("com.squareup.picasso:picasso:2.5.2")
    implementation("io.coil-kt:coil:2.6.0")
    implementation("io.coil-kt:coil-compose:2.6.0")

    //Firebase Implementation
    implementation("com.google.android.gms:play-services-cast-framework:21.4.0")
    implementation("com.google.firebase:firebase-inappmessaging-display:21.0.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    implementation("com.google.firebase:firebase-ml-vision:24.1.0")
    implementation("com.google.firebase:firebase-storage-ktx:21.0.0")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation ("com.google.firebase:firebase-ml-vision:24.1.0")
    implementation ("com.google.firebase:firebase-ml-vision-barcode-model:16.1.2")
}