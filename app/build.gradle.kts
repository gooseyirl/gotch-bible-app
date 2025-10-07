plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.ajoberstar.reckon")
}

reckon {
    scopeFromProp()
    stageFromProp("beta", "rc", "final")
}

android {
    namespace = "com.example.carddeck"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.carddeck"
        minSdk = 24
        targetSdk = 34

        val reckonVersion = project.version.toString()
        versionName = reckonVersion

        // Generate versionCode from version string (e.g., 1.2.3 -> 10203)
        val versionParts = reckonVersion.split(".", "-").take(3)
        versionCode = versionParts.mapIndexed { index, part ->
            part.toIntOrNull()?.let { it * Math.pow(100.0, (2 - index).toDouble()).toInt() } ?: 0
        }.sum()
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
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}
