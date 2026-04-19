plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.gymcrush"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gymcrush"
        minSdk = 26
        targetSdk = 35
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

    // Google Play Services
    implementation(libs.gms.maps)
    implementation(libs.gms.base)
    implementation(libs.gms.location)
    
    // UI Libraries
    implementation(libs.glide)
    implementation(libs.glide.okhttp)
    annotationProcessor(libs.glide.compiler)
    implementation(libs.lottie)
    
    implementation(libs.gridlayout)
    implementation(libs.fragment)
    implementation(libs.recyclerview)

    // QR Code Generation
    implementation(libs.zxing.android)
    implementation(libs.zxing.core)

    // RxJava
    implementation(libs.rxjava2)
    implementation(libs.rxandroid2)
    implementation("io.reactivex.rxjava3:rxjava:3.1.12")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")

    // Video Player
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.1")
    
    // STOMP WebSocket
    implementation(libs.stomp)

    // PhotoView para Tour Virtual / Zoom en fotos
    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    testImplementation(libs.junit)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
