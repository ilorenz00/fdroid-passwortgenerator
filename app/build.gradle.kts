plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.lorenzl5.hermes.passwort"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lorenzl5.hermes.passwort"
        minSdk = 26
        targetSdk = 34
        // Die CI (release-apk.yml) setzt versionCode = Lauf-Nummer; F-Droid entscheidet Updates nur danach.
        versionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = System.getenv("VERSION_NAME") ?: "0.1.0"
    }

    // Signieren nur in der CI (gemeinsamer Keystore aller lorenzl5-Apps, GitHub-Secrets).
    val ciKeystore = System.getenv("SIGNING_KEYSTORE")
    if (ciKeystore != null) {
        signingConfigs {
            create("release") {
                storeFile = file(ciKeystore)
                storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                keyAlias = System.getenv("SIGNING_KEY_ALIAS")
                keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (ciKeystore != null) {
                signingConfig = signingConfigs.getByName("release")
            }
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
        viewBinding = true
    }
    lint {
        // Lint bricht den Release-Build bei Fehlern ab: Fehler beheben, nicht abschalten.
        abortOnError = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
}
