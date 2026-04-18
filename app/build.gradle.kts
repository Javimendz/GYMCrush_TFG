plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.gymcrush"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gymcrush"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.cardview)
    
    // Splash Screen
    implementation(libs.core.splashscreen)
    
    // Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Google Maps
    implementation(libs.play.services.maps)
    
    // UI Libraries
    implementation(libs.glide)
    implementation(libs.glide.okhttp)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.lottie)
    
    implementation(libs.gridlayout)
    implementation(libs.fragment)

    // QR Code Generation
    implementation(libs.zxing.android)
    implementation(libs.zxing.core)

    // Video Player
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.1")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
