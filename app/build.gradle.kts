import com.droidstarter.support.SigningConfigs

plugins {
    alias(libs.plugins.droidstarter.android.application.compose)
    alias(libs.plugins.droidstarter.android.hilt)
}

android {
    namespace = "io.github.sridhar.sp.virtual.display"

    defaultConfig {
        applicationId = "io.github.sridhar.sp.virtual.display"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    SigningConfigs.newInstance(project).applyDefaultKeyStoreSigningConfig()

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName(SigningConfigs.DEFAULT_KEYSTORE_SIGNING_CONFIG)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            signingConfig = signingConfigs.getByName(SigningConfigs.DEFAULT_KEYSTORE_SIGNING_CONFIG)
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtimeKtx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(projects.core.designsystem)

    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit4)
}