plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.raven.clinic"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.raven.clinic"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // Core & UI
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Firebase BOM (единственная строка для управления версиями всех Firebase-библиотек)
    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))

    // Firebase Authentication KTX
    implementation("com.google.firebase:firebase-auth-ktx:21.1.0")
    // Firestore KTX
    implementation("com.google.firebase:firebase-firestore-ktx:24.7.0")
    // Realtime Database KTX
    implementation("com.google.firebase:firebase-database-ktx:20.2.2")
    // Firebase BOM – управляет версиями всех Firebase-библиотек
    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
    // Тесты
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
// Плагин Google Services уже подключён в блоке plugins выше, поэтому дополнительный apply не нужен
