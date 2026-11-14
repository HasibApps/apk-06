import java.io.File
import java.util.Properties

plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }

// ─────────────────────────────────────────────────────────────────────────────
//  Release signing
//  Values are read from (in order of priority):
//    1. keystore.properties  (local builds, git-ignored)
//    2. environment variables (GitHub Actions secrets)
//  The SAME keystore is used for every CI build, so every APK has an identical
//  signature and can be installed over the previous one.
// ─────────────────────────────────────────────────────────────────────────────
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) keystorePropsFile.inputStream().use { load(it) }
}

fun signValue(prop: String, env: String): String? =
    (keystoreProps.getProperty(prop) ?: System.getenv(env))?.trim()?.takeIf { it.isNotEmpty() }

val releaseStoreFile: File? = signValue("storeFile", "KEYSTORE_FILE")?.let { raw ->
    val f = File(raw)
    if (f.isAbsolute) f else rootProject.file(raw)
}
val releaseStorePass = signValue("storePassword", "KEYSTORE_PASSWORD")
val releaseKeyAlias = signValue("keyAlias", "KEY_ALIAS")
val releaseKeyPass = signValue("keyPassword", "KEY_PASSWORD")
val hasReleaseSigning = releaseStoreFile != null && releaseStoreFile.exists() &&
        releaseStorePass != null && releaseKeyAlias != null && releaseKeyPass != null

android {
    namespace = "com.example.ytdl"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.ytdl"
        minSdk = 24; targetSdk = 34; versionCode = 4; versionName = "2.2"
        ndk { abiFilters += listOf("arm64-v8a") } // add "armeabi-v7a" only if your phone is 32-bit (doubles the size)
    }
    packaging {
        jniLibs { useLegacyPackaging = true }
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = releaseStoreFile!!
                storePassword = releaseStorePass
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPass
                storeType = signValue("storeType", "KEYSTORE_TYPE") ?: "JKS"
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (hasReleaseSigning) signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            // unsigned-by-release-key build, handy for local/Termux testing
            applicationIdSuffix = ".debug"
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.github.junkfood02.youtubedl-android:library:0.17.2")
    implementation("io.github.junkfood02.youtubedl-android:ffmpeg:0.17.2")
}
