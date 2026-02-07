# Библиотека Biometry Auth для KMP (iOS + Android): план и инструкция

Подробный пошаговый план создания Kotlin Multiplatform библиотеки для биометрической аутентификации с последующей публикацией на GitHub.

---

## Оглавление

1. [Обзор и ссылки на документацию](#1-обзор-и-ссылки-на-документацию)
2. [Требования и окружение](#2-требования-и-окружение)
3. [Шаг 1: Создание структуры KMP-библиотеки](#шаг-1-создание-структуры-kmp-библиотеки)
4. [Шаг 2: Общий API (commonMain)](#шаг-2-общий-api-commonmain)
5. [Шаг 3: Реализация для Android](#шаг-3-реализация-для-android)
6. [Шаг 4: Реализация для iOS](#шаг-4-реализация-для-ios)
7. [Шаг 5: Тесты и sample-приложение](#шаг-5-тесты-и-sample-приложение)
8. [Шаг 6: Публикация на GitHub](#шаг-6-публикация-на-github)
9. [Опционально: публикация в Maven Central](#опционально-публикация-в-maven-central)

---

## 1. Обзор и ссылки на документацию

### Официальная документация

| Тема | Ссылка |
|------|--------|
| Kotlin Multiplatform — обзор | https://kotlinlang.org/docs/multiplatform.html |
| Создание KMP-библиотеки | https://kotlinlang.org/docs/multiplatform-lib.html |
| expect/actual | https://kotlinlang.org/docs/multiplatform-connect-to-apis.html |
| Публикация KMP-библиотеки | https://kotlinlang.org/docs/multiplatform-publish-lib.html |
| Публикация в Maven Central | https://kotlinlang.org/docs/multiplatform-publish-libraries.html |

### Платформенная биометрия

| Платформа | Документация |
|-----------|--------------|
| Android — BiometricPrompt | https://developer.android.com/identity/sign-in/biometric-auth |
| Android — androidx.biometric | https://developer.android.com/reference/androidx/biometric/package-summary |
| iOS — LocalAuthentication (Face ID / Touch ID) | https://developer.apple.com/documentation/localauthentication |

### Готовые примеры и референсы

| Ресурс | Описание |
|--------|----------|
| **moko-biometry** | Готовая KMP-библиотека: Touch ID, Face ID из общего кода. Можно использовать как референс по структуре и API. https://github.com/icerockdev/moko-biometry |
| **biometric-authentication** (prashant17d97) | KMP с корутинами, secure vault. https://github.com/prashant17d97/biometric-authentication- |
| **KMP library template** (JetBrains) | Официальный шаблон KMP-библиотеки с публикацией. https://github.com/Kotlin/multiplatform-library-template |

---

## 2. Требования и окружение

- **Gradle:** 8.0+
- **Kotlin:** 1.9+ (рекомендуется 2.0+)
- **Android:** minSdk 23+, targetSdk 34+
- **iOS:** 11.0+ (для Touch ID / Face ID через LocalAuthentication)
- **Mac** — для сборки и тестов iOS (и для симулятора).

Установите:
- JDK 17+
- Android Studio (или IntelliJ IDEA с Android plugin)
- Xcode (для iOS)
- CocoaPods (если будете использовать CocoaPods в примере)

---

## Шаг 1: Создание структуры KMP-библиотеки

### 1.1 Вариант A: из официального шаблона

```bash
# Клонировать шаблон и переименовать
git clone https://github.com/Kotlin/multiplatform-library-template.git biometry-auth-kmp
cd biometry-auth-kmp
# Удалить .git и инициализировать свой репозиторий
rm -rf .git && git init
```

Далее заменить имя модуля/артефакта на своё (например `biometry-auth`) в `settings.gradle.kts` и `build.gradle.kts`.

### 1.2 Вариант B: новый проект вручную

Создайте структуру каталогов:

```
biometry-auth-kmp/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradle/
│   └── libs.versions.toml
├── biometry/                    # модуль библиотеки
│   └── src/
│       ├── commonMain/
│       │   └── kotlin/
│       │       └── your/package/biometry/
│       ├── androidMain/
│       │   └── kotlin/
│       │       └── your/package/biometry/
│       └── iosMain/
│           └── kotlin/
│               └── your/package/biometry/
├── sample/                      # опционально: sample app
│   ├── androidApp/
│   └── iosApp/
├── .github/
│   └── workflows/               # CI (build, publish)
├── README.md
└── LICENSE
```

### 1.3 Корневой `settings.gradle.kts`

```kotlin
rootProject.name = "biometry-auth-kmp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":biometry")
// include(":sample:androidApp")
// include(":sample:iosApp")
```

### 1.4 Корневой `build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidLibrary) apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
```

### 1.5 Модуль `biometry/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

group = "com.yourname"
version = "1.0.0"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "BiometryAuth"
            isStatic = true
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
        }
        androidMain.dependencies {
            implementation("androidx.biometric:biometric:1.1.0")
        }
    }
}

android {
    namespace = "com.yourname.biometry"
    compileSdk = 34
    defaultConfig {
        minSdk = 23
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
```

В `gradle/libs.versions.toml` задайте версии плагинов (kotlinMultiplatform, androidLibrary) и зависимостей.

Итог шага 1: проект собирается (`./gradlew :biometry:build` без ошибок).

---

## Шаг 2: Общий API (commonMain)

В `commonMain` объявляем **expect**-декларации. Вся бизнес-логика авторизации остаётся в общем коде.

### 2.1 Модели (commonMain)

Создайте файлы в `biometry/src/commonMain/kotlin/.../biometry/`:

**BiometryType.kt** — тип биометрии (для информации):

```kotlin
enum class BiometryType {
    NONE,
    FINGERPRINT,
    FACE,
    IRIS
}
```

**BiometryResult.kt** — результат проверки:

```kotlin
sealed class BiometryResult {
    data object Success : BiometryResult()
    data object Cancelled : BiometryResult()
    data class Error(val message: String?, val code: Int) : BiometryResult()
}
```

**BiometryAvailability.kt** — доступность биометрии:

```kotlin
data class BiometryAvailability(
    val isAvailable: Boolean,
    val biometryType: BiometryType,
    val errorMessage: String? = null
)
```

### 2.2 Expect-интерфейс (commonMain)

**BiometryAuthenticator.kt**:

```kotlin
interface BiometryAuthenticator {
    suspend fun isBiometryAvailable(): BiometryAvailability
    suspend fun authenticate(
        title: String,
        subtitle: String? = null,
        negativeButtonText: String? = null,
        allowDeviceCredentials: Boolean = false
    ): BiometryResult
}

expect fun createBiometryAuthenticator(): BiometryAuthenticator
```

Или, если нужна фабрика с контекстом (как в moko-biometry), можно передавать контекст через expect-функцию с платформенными типами в `expect`/`actual`.

Итог шага 2: в commonMain только контракт и модели; платформенный код — в androidMain и iosMain.

---

## Шаг 3: Реализация для Android

### 3.1 Зависимости

В `biometry/build.gradle.kts` в `androidMain.dependencies` уже добавлено:

```kotlin
implementation("androidx.biometric:biometric:1.1.0")
```

### 3.2 actual-реализация

В `androidMain/kotlin/.../biometry/` создайте:

**AndroidBiometryAuthenticator.kt**:

- Реализует `BiometryAuthenticator`.
- Использует `BiometricManager` для проверки доступности.
- Использует `BiometricPrompt` + `FragmentActivity`/`FragmentManager` для показа диалога.
- Обёртка над callback’ами в `suspend` — через `suspendCancellableCoroutine` и вызовы из главного потока (например `runBlocking` на Main или передача `Context` и использование `MainScope()`/`LifecycleScope` в вызывающем коде).

Важно: показ `BiometricPrompt` должен происходить на main thread. В moko-biometry это решается привязкой к lifecycle и вызовом из UI. В вашей библиотеке можно:

- принимать `FragmentActivity` в фабрике и вызывать `runOnUiThread` внутри `authenticate`, или
- возвращать результат через `Continuation` из callback’а и использовать `Handler(Looper.getMainLooper())` для запуска показа диалога.

Пример скелета:

```kotlin
// actual fun createBiometryAuthenticator(context: Context): BiometryAuthenticator =
//     AndroidBiometryAuthenticator(context)
```

Проверка доступности:

- `BiometricManager.from(context).canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)` — см. документацию Android.

Документация: https://developer.android.com/identity/sign-in/biometric-auth

### 3.3 Манифест и разрешения

В манифесте приложения (или в демо-приложении), которое использует библиотеку, должно быть (если нужен fallback на device credentials):

- `USE_BIOMETRIC` / `USE_FINGERPRINT` (при необходимости для старых API).

Библиотека может не объявлять разрешения сама, а требовать их в README для приложения.

Итог шага 3: на Android вызов `authenticate` показывает системный диалог и возвращает `BiometryResult`.

---

## Шаг 4: Реализация для iOS

### 4.1 LocalAuthentication

На iOS используется фреймворк **LocalAuthentication** (Face ID / Touch ID). Вызов из Kotlin/Native через **cinterop** или через ожидаемые/фактические объявления без отдельного cinterop — вызывая Objective-C/Swift API из Kotlin через expect/actual и обёртки.

Проще всего: в `iosMain` писать Kotlin и вызывать платформенные API через **platform.Foundation** и **platform.UIKit** (если нужны контроллеры) или через обёртку на Swift/ObjC, с которой связываетесь из Kotlin. Стандартный способ для LocalAuthentication — сделать небольшой **cinterop** к `LocalAuthentication.framework` или реализовать логику в Swift и вызывать из Kotlin через `export`/Kotlin Native interop.

Упрощённый вариант без своего cinterop: использовать готовую обёртку или скопировать подход из **moko-biometry** (у них в iosMain вызывается нативный код). Структура:

- **iosMain**: `actual fun createBiometryAuthenticator(): BiometryAuthenticator = IosBiometryAuthenticator()`.
- Внутри `IosBiometryAuthenticator` — вызовы в главный поток (Main Dispatcher) и вызов нативного LAContext.

Пример логики (псевдокод на уровне идеи):

- `LAContext().canEvaluatePolicy(LAPolicy.deviceOwnerAuthenticationWithBiometrics)` — доступность.
- `evaluatePolicy(_:localizedReason:reply:)` — показать системный диалог и вернуть результат в callback; в Kotlin обернуть в `suspendCancellableCoroutine`.

Документация Apple: https://developer.apple.com/documentation/localauthentication

### 4.2 Info.plist (приложение)

В любом приложении, которое использует Face ID, в **Info.plist** нужно добавить:

```xml
<key>NSFaceIDUsageDescription</key>
<string>Использование Face ID для входа в приложение</string>
```

В README библиотеки это нужно явно указать.

Итог шага 4: на iOS вызов `authenticate` показывает системный Face ID / Touch ID и возвращает `BiometryResult`.

---

## Шаг 5: Тесты и sample-приложение

### 5.1 Unit-тесты

- **commonTest**: тесты для общих моделей (`BiometryResult`, `BiometryAvailability`, маппинг из платформенных кодов в общие типы).
- **androidTest**: можно добавить инструментальные тесты с эмулятором (опционально).
- **iOS**: тесты в Xcode или через Kotlin/Native test target.

### 5.2 Sample

Рекомендуется сделать два приложения (как в moko-biometry):

- **sample/androidApp** — один экран с кнопкой «Войти по биометрии», вызов `biometryAuthenticator.authenticate(...)` и отображение результата.
- **sample/iosApp** — то же самое на iOS (SwiftUI или UIKit).

Так вы проверите интеграцию и дадите пользователям пример использования.

---

## Шаг 6: Публикация на GitHub

### 6.1 Подготовка репозитория

1. Создайте репозиторий на GitHub (например `biometry-auth-kmp`).
2. Локально добавьте remote и запушьте:

```bash
git remote add origin https://github.com/YOUR_USERNAME/biometry-auth-kmp.git
git branch -M main
git push -u origin main
```

### 6.2 Что должно быть в репозитории

- **README.md**:
  - описание библиотеки и возможностей;
  - требования (Kotlin, minSdk, iOS version);
  - установка (Gradle snippet для добавления зависимости — если публикуете в Maven Central или JitPack);
  - пример использования (common + Android + iOS);
  - для iOS — напоминание про `NSFaceIDUsageDescription`;
  - лицензия.

- **LICENSE** (например Apache 2.0 или MIT).

- **.gitignore** (Kotlin/Android/Gradle/Xcode).

- Опционально: **CONTRIBUTING.md**, **CHANGELOG.md**.

### 6.3 Публикация артефактов

**Вариант 1 — только исходный код на GitHub**

Пользователи подключают библиотеку как подмодуль или копируют модуль:

```kotlin
// settings.gradle.kts
include(":biometry")
project(":biometry").projectDir = File("../biometry-auth-kmp/biometry")
```

**Вариант 2 — JitPack**

Добавьте в корень репозитория настройку публикации (maven-publish) так, чтобы JitPack мог собрать проект. В README укажите:

```kotlin
repositories { maven("https://jitpack.io") }
dependencies { implementation("com.github.YOUR_USERNAME:biometry-auth-kmp:VERSION") }
```

**Вариант 3 — Maven Central**

См. раздел «Опционально: публикация в Maven Central» и официальный гайд:  
https://kotlinlang.org/docs/multiplatform-publish-libraries.html

### 6.4 CI (GitHub Actions)

Пример workflow для сборки (без публикации в репозиторий):

```yaml
# .github/workflows/build.yml
name: Build
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - name: Build
        run: ./gradlew :biometry:build
```

Для iOS-сборки понадобится `macos-latest` и вызов `./gradlew :biometry:linkDebugFrameworkIos*`.

Итог шага 6: репозиторий на GitHub с понятным README, лицензией и (по желанию) CI и артефактами.

---

## Опционально: публикация в Maven Central

Если решите публиковать в Maven Central:

1. Аккаунт на https://central.sonatype.com/ и namespace (group id).
2. В модуле `biometry` подключите `maven-publish` и настройте `publishing { ... }` (group, version, repositories, публикации для kotlinMultiplatform и android).
3. Подпись артефактов (GPG или signing key в Gradle).
4. В README укажите зависимость вида:

```kotlin
implementation("com.yourname:biometry:1.0.0")
```

Подробно: https://kotlinlang.org/docs/multiplatform-publish-libraries.html

---

## Краткий чеклист по шагам

| # | Шаг | Готово |
|---|-----|--------|
| 1 | Создать KMP-проект (шаблон или вручную), настроить `biometry` модуль с android + iosX64/iosArm64/iosSimulatorArm64 | ☐ |
| 2 | commonMain: модели (BiometryType, BiometryResult, BiometryAvailability), expect-интерфейс BiometryAuthenticator и createBiometryAuthenticator() | ☐ |
| 3 | androidMain: actual, BiometricManager + BiometricPrompt, привязка к main thread и lifecycle при необходимости | ☐ |
| 4 | iosMain: actual, LocalAuthentication (LAContext), Main dispatcher, Info.plist в README | ☐ |
| 5 | Тесты commonTest, опционально sample android + iOS | ☐ |
| 6 | README, LICENSE, .gitignore, CI, репозиторий на GitHub | ☐ |
| 7 | (Опционально) maven-publish и Maven Central | ☐ |

---

## Полезные ссылки (сводка)

- KMP: https://kotlinlang.org/docs/multiplatform.html  
- Публикация KMP: https://kotlinlang.org/docs/multiplatform-publish-lib.html  
- Android BiometricPrompt: https://developer.android.com/identity/sign-in/biometric-auth  
- iOS LocalAuthentication: https://developer.apple.com/documentation/localauthentication  
- moko-biometry (референс): https://github.com/icerockdev/moko-biometry  
- KMP library template: https://github.com/Kotlin/multiplatform-library-template  

После реализации по этому плану у вас будет библиотека Biometry Auth для KMP с поддержкой авторизации на iOS и Android и готовая выкладка на GitHub. Если нужно, могу следующим шагом расписать конкретные файлы (имена и полный код) под вашу структуру пакетов и имен модулей.
