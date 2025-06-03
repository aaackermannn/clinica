// build.gradle.kts (уровень проекта)

plugins {
    // плагин android и kotlin не подключаем здесь — их подключаем уже в модулях
    // но плагин Google Services нужно объявить в buildscript-dependencies
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
}

buildscript {
    dependencies {
        // Плагин Google Services для Firebase
        classpath("com.google.gms:google-services:4.4.0")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
