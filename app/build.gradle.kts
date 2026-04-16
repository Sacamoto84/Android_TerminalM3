plugins {
    id("com.android.application")
    id ("com.google.devtools.ksp")
    id ("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "com.example.terminalm3"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.terminalm3"
        minSdk = 23
        targetSdk = 37
        versionCode = 1
        versionName = "3.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation(platform("androidx.compose:compose-bom:2026.03.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3:1.4.0")

    implementation ("androidx.navigation:navigation-compose:2.9.7")

    implementation ("com.google.accompanist:accompanist-webview:0.36.0")
    implementation ("com.google.accompanist:accompanist-pager:0.36.0")
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.36.0")
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.36.0")

    implementation ("com.jakewharton.timber:timber:5.0.1")

    implementation ("com.holix.android:bottomsheetdialog-compose:1.6.0")

    implementation ("com.siddroid:holi:1.0.1")
    implementation("androidx.compose.ui:ui-tooling-preview-android:1.10.6")

    //https://github.com/ajitsing/Sherlock
    debugImplementation("com.github.ajitsing:sherlock:1.0.4@aar")
    debugImplementation("androidx.compose.ui:ui-tooling:1.10.6") //{
    //transitive = true
    //}
    releaseImplementation("com.github.ajitsing:sherlock-no-op:1.0.4@aar")

    ///////////////////
    //Hilt
    implementation ("com.google.dagger:hilt-android:2.59.2")
    implementation ("androidx.hilt:hilt-navigation-compose:1.3.0")
    ksp ("com.google.dagger:hilt-compiler:2.59.2")
    //implementation 'androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03'
    ksp ("androidx.hilt:hilt-compiler:1.3.0")
    ///////////////////

}