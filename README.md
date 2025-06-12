# MedHub

**MedHub** — это прототип мобильного Android-приложения для записи к врачу, реализованный на Java/Kotlin с использованием Firebase Authentication и Firestore.  
Позволяет пользователю:

- Зарегистрироваться / войти через e-mail и пароль  
- Просмотреть список врачей с фото, специальностью и описанием  
- Записаться на приём, выбрав один из доступных слотов  
- Управлять своими записями (перенос, отмена)  
- Посмотреть все «Мои записи»  
- Изменить пароль в профиле  

---

## 📋 **Ключевые особенности**

- **Firebase Auth** (email/password)  
- **Cloud Firestore**  
  - `users/{uid}` → email, nickname  
  - `appointments/{uid}/user_appointments/{doctorName}` → doctorName, specialty, dateTime, doctorPhoto  
- **AppointmentManager** — локальный менеджер записей  
- **BottomNavigationView** на экранах «Домой» и «Профиль»  
- Тёмная тема с оранжевыми акцентами  

---

## 🚀 **Быстрый старт**

1. **Клонировать репозиторий**  
   ```bash
   git clone https://github.com/your-org/medclinic-android.git
   cd medclinic-android

---

2. Настроить Firebase

    Создайте проект в Firebase Console

    Добавьте Android-приложение с applicationId = "com.raven.clinic"

    Скачайте google-services.json и положите его в app/src/main/

    Включите в Authentication → Sign-in method метод Email/Password

    Во Firestore → Rules временно установите:
    
    service cloud.firestore {
        match /databases/{database}/documents {
            match /{document=**} {
               allow read, write: if true;
               }
          }
    }

3. Добавить SHA-1 ключ
   keytool -list -v -alias androiddebugkey \
  -keystore ~/.android/debug.keystore \
  -storepass android -keypass android

4. Sync & Rebuild
В Android Studio: File → Sync Project with Gradle Files, затем Rebuild Project.

5. Запустить
Выберите эмулятор или устройство и нажмите ▶ Run.

📂 **Структура проекта**

app/
├─ src/main/
│  ├─ java/com/raven/clinic/
│  │  ├─ activities/
│  │  │  ├ LoginActivity.java
│  │  │  ├ SignUpActivity.java
│  │  │  ├ HomeActivity.java
│  │  │  ├ DoctorsActivity.java
│  │  │  ├ AppointmentActivity.java
│  │  │  ├ ManageAppointmentActivity.java
│  │  │  ├ ConfirmationActivity.java
│  │  │  ├ MyAppointmentsActivity.java
│  │  │  ├ ProfileActivity.java
│  │  │  └ ChangePasswordActivity.java
│  │  ├─ adapters/
│  │  ├─ models/
│  │  ├─ utils/
│  │  └ AppointmentManager.java
│  └─ res/
│     ├─ layout/
│     ├─ drawable/
│     ├─ values/
│     ├─ menu/
│     ├─ navigation/
│     └─ xml/

🛠️ **Технологии и зависимости**

  - Язык: Java (JVM 1.8), поддержка Kotlin

  - Android SDK: API 24–34

  - UI: AndroidX AppCompat, Material Components, RecyclerView, CardView

  - Firebase BOM 32.1.1 → Auth KTX, Firestore KTX

  - Navigation Component (через nav_graph.xml)

🔧 Конфигурация Gradle

- Корневой build.gradle.kts:

plugins {
  id("com.android.application") version "8.2.0" apply false
  id("org.jetbrains.kotlin.android") version "1.9.20" apply false
}
buildscript {
  dependencies {
    classpath("com.google.gms:google-services:4.4.0")
  }
}

- app/build.gradle.kts:

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("com.google.gms.google-services")
}
android {
  namespace = "com.raven.clinic"
  compileSdk = 34
  defaultConfig { /* ... */ }
  buildTypes { /* ... */ }
}
dependencies {
  implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
  implementation("com.google.firebase:firebase-auth-ktx")
  implementation("com.google.firebase:firebase-firestore-ktx")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("com.google.android.material:material:1.11.0")
  implementation("androidx.recyclerview:recyclerview:1.3.2")
  implementation("androidx.cardview:cardview:1.0.0")
  // и др.
}

MIT License.
