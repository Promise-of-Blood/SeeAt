import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)
    id("com.google.dagger.hilt.android")
    id("com.google.android.gms.oss-licenses-plugin")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
}

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

android {
    namespace = "com.pob.seeat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pob.seeat"
        minSdk = 30
        targetSdk = 34
        versionCode = 4
        versionName = "1.1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String", "KAKAO_APP_KEY", "\"${properties["KAKAO_APP_KEY"]}\""
        )

        buildConfigField(
            "String", "NAVER_CLIENT_SECRET", "\"${properties["NAVER_CLIENT_SECRET"]}\""
        )

        buildConfigField(
            "String", "WEB_CLIENT_ID", "\"${properties["WEB_CLIENT_ID"]}\""
        )

    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            manifestPlaceholders["NAVER_CLIENT_ID"] = properties["NAVER_CLIENT_ID"] as String
        }
        release {
            isMinifyEnabled = false
            manifestPlaceholders["NAVER_CLIENT_ID"] = properties["NAVER_CLIENT_ID"] as String
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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

ksp {
    arg("room.schemaLocation", "${projectDir}/schemas")
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.storage.ktx)
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
    implementation(libs.geofire.android.common)

    // hilt
    implementation(libs.dagger.hilt.android)
    ksp(libs.hilt.compiler)

    // viewpager2
    implementation(libs.androidx.viewpager2)

    // glide
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // kakao
    implementation(libs.kakao.maps)
    implementation(libs.kakao.login)

    // naver
    implementation(libs.naver.login)
    implementation(libs.naver.map)
    implementation(libs.play.services.location)

    //google
    implementation(libs.play.services.auth)

    // oss
    implementation(libs.play.services.oss.licenses)

    // timber
    implementation(libs.timber)

    //image implement
    implementation(libs.android.image.cropper)

    // ViewPager2 Indicator
    implementation(libs.circleindicator)

    // PhotoView
    implementation(libs.photoview)

    implementation(libs.switchbutton.library)

    // room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.androidx.room.testing)
    implementation(libs.androidx.room.paging)

    // pull to refresh
    implementation(libs.androidx.swiperefreshlayout)
}
