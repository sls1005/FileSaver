plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.mikepenz.aboutlibraries.plugin")
}

android {
    namespace = "test.com.github.www.sls1005.filesaver"
    compileSdk = 35

    defaultConfig {
        applicationId = "test.com.github.www.sls1005.filesaver"
        minSdk = 21
        targetSdk = 35
        versionCode = 11
        versionName = "2.3.0"
        resourceConfigurations += arrayOf("en", "en-rGB", "en-rUS", "zh-rCN", "zh-rHK", "zh-rTW")
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    androidResources {
        generateLocaleConfig = true
    }
    signingConfigs {
        register("release") {
            enableV2Signing = true
            enableV3Signing = true
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
            signingConfig = signingConfigs.getByName("release")
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
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation(platform("androidx.compose:compose-bom:2025.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("com.mikepenz:aboutlibraries-core:11.4.0")
    implementation("com.mikepenz:aboutlibraries-compose-m3:11.4.0")
}

tasks.register<Copy>("Include license") {
    include("LICENSE")
    from("..")
    into("src/main/assets/")
}.also {
    val task = it.get()
    afterEvaluate {
        tasks.named("preReleaseBuild") {
            dependsOn(task)
        }
    }
}
