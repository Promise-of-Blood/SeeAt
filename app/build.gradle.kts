import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.pob.seeat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pob.seeat"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "KAKAO_APP_KEY",
            gradleLocalProperties(rootDir, providers).getProperty("KAKAO_APP_KEY")
        )
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // coroutine
    implementation(libs.kotlinx.coroutines.android)

    // retrofit
    implementation(libs.bundles.retrofit)

    // fragment
    implementation(libs.androidx.fragment.ktx)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // hilt
    implementation(libs.dagger.hilt.android)
    ksp(libs.hilt.compiler)

    // viewpager2
    implementation(libs.androidx.viewpager2)

    // glide
    implementation(libs.glide)

    // kakao
    implementation(libs.kakao.maps)
    implementation(libs.kakao.login)

    // naver
    implementation(libs.naver.login)
}