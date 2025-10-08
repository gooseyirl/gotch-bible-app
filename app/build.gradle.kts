plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.gooseco.gotchbible"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gooseco.gotchbible"
        minSdk = 24
        targetSdk = 35

        val reckonVersion = rootProject.version.toString()
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

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "gotch-bible-${versionName}-${buildType.name}.apk"
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
    implementation("nl.dionsegijn:konfetti-xml:2.0.4")
}
