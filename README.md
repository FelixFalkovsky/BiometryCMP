# Biometry Auth KMP

<!-- TODO: добавить бейджи после публикации: license, Maven Central version, Kotlin version -->

Библиотека [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) для биометрической аутентификации (Touch ID, Face ID, отпечаток) на Android и iOS из общего кода.

## Содержание

- [Возможности](#возможности)
- [Требования](#требования)
- [Установка](#установка)
- [Использование](#использование)
- [Sample](#sample)
- [Что библиотека не делает](#что-библиотека-не-делает)
- [Сборка и разработка](#сборка-и-разработка)
- [Публикация](#публикация)
- [Contributing](#contributing)
- [Лицензия](#лицензия)

---

## Возможности

- Проверка доступности биометрии: [BiometryAuthenticator.isBiometryAvailable()](biometry/src/commonMain/kotlin/com/enumSet/biometry/BiometryAuthenticator.kt)
- Запуск системного диалога аутентификации: [BiometryAuthenticator.authenticate()](biometry/src/commonMain/kotlin/com/enumSet/biometry/BiometryAuthenticator.kt)
- Общие модели: [BiometryResult](biometry/src/commonMain/kotlin/com/enumSet/biometry/BiometryResult.kt), [BiometryAvailability](biometry/src/commonMain/kotlin/com/enumSet/biometry/BiometryAvailability.kt), [BiometryType](biometry/src/commonMain/kotlin/com/enumSet/biometry/BiometryType.kt)
- **Android:** BiometricManager + BiometricPrompt (main thread)
- **iOS:** LocalAuthentication (TODO: реализация в шаге 4 плана)

---

## Требования

- **Gradle:** 8.0+
- **Kotlin:** 1.9+ (рекомендуется 2.0+)
- **Android:** minSdk 23+, targetSdk 34+
- **iOS:** 11.0+ (Touch ID / Face ID через LocalAuthentication)
- **macOS** — для сборки и тестов iOS (симулятор)

Установите:

- JDK 17+
- Android Studio (или IntelliJ IDEA с Android plugin)
- Xcode (для iOS)
- CocoaPods (опционально, если используется в sample-приложении)

---

## Установка

<!-- TODO: после публикации в Maven Central заменить на актуальные group/artifact/version -->

**Root `settings.gradle.kts` / `build.gradle.kts`** — репозитории:

```kotlin
// allprojects или dependencyResolutionManagement
repositories {
    mavenCentral()
    google()
}
```

**Модуль приложения** (KMP):

```kotlin
// kotlin { sourceSets { commonMain.dependencies { ... } } }
commonMain.dependencies {
    implementation("com.enumSet:biometry-auth:1.0.0") // TODO: подставить версию после публикации
}
```

Для только Android (без KMP) используйте соответствующий артефакт (например, `biometry-auth-android` — уточнить по итогам публикации).

---

## Использование

### Общий код (commonMain)

Вызов из корутины (рекомендуется Main dispatcher для показа UI на Android):

```kotlin
val authenticator = createBiometryAuthenticator()

// Проверка доступности
val availability = authenticator.isBiometryAvailable()
if (!availability.isAvailable) {
    // availability.errorMessage, availability.biometryType
    return
}

// Запуск аутентификации
when (val result = authenticator.authenticate(
    title = "Вход",
    subtitle = "Подтвердите личность",
    negativeButtonText = "Отмена",
    allowDeviceCredentials = true
)) {
    is BiometryResult.Success -> { /* успех */ }
    is BiometryResult.Cancelled -> { /* отмена пользователем */ }
    is BiometryResult.Error -> { /* result.message, result.code */ }
}
```

### Android

1. Перед использованием установите [FragmentActivity](https://developer.android.com/reference/androidx/fragment/app/FragmentActivity) (например, в `onCreate`):

```kotlin
import com.enumSet.biometry.setFragmentActivityForBiometry

// в Activity (FragmentActivity)
setFragmentActivityForBiometry(this)
```

2. Вызывайте биометрию из корутины с Main dispatcher (показ диалога — на main thread):

```kotlin
lifecycleScope.launch(Dispatchers.Main) {
    val authenticator = createBiometryAuthenticator()
    val result = authenticator.authenticate(
        title = "Вход",
        subtitle = "Подтвердите личность",
        negativeButtonText = "Отмена",
        allowDeviceCredentials = true
    )
}
```

### iOS

<!-- TODO: дополнить после реализации шага 4 (LocalAuthentication); указать NSFaceIDUsageDescription в Info.plist -->

```kotlin
// Общий код тот же: createBiometryAuthenticator() + authenticate()
// На iOS actual реализация использует LocalAuthentication.
```

В **Info.plist** приложения добавьте ключ для Face ID:

```xml
<key>NSFaceIDUsageDescription</key>
<string>Аутентификация с помощью Face ID или Touch ID</string>
```

---

## Sample

Примеры для проверки реализации библиотеки (собственный код, отличный от других библиотек):

| Модуль              | Описание                              | Запуск |
|---------------------|----------------------------------------|--------|
| `sample/androidApp` | Демо-приложение Android (проверка + вход) | см. ниже |
| `sample/iosApp`     | Инструкция по сборке фреймворка и Xcode | см. [sample/iosApp/README.md](sample/iosApp/README.md) |

**Структура:**

```
sample/
├── androidApp/     # Android-приложение, зависимость project(":biometry")
│   └── src/main/   # MainActivity, layout, манифест с USE_BIOMETRIC
└── iosApp/         # README + сборка фреймворка, интеграция в Xcode вручную
```

**Запуск Android sample:**

```bash
# Сборка debug-APK
./gradlew :sample:androidApp:assembleDebug

# Установка на подключённое устройство или эмулятор
./gradlew :sample:androidApp:installDebug
```

В приложении: две кнопки — «Проверить доступность» и «Войти по биометрии»; результат выводится в текстовое поле и в Toast.

**iOS:** фреймворк собирается командой `./gradlew :biometry:linkDebugFrameworkIosSimulatorArm64`; пошаговая интеграция в Xcode — в [sample/iosApp/README.md](sample/iosApp/README.md).

---

## Что библиотека не делает

- **Не объявляет разрешения в манифесте.** Их должно добавлять приложение.
- **Android:** при необходимости укажите в манифесте приложения:
  - `USE_BIOMETRIC` (API 28+)
  - `USE_FINGERPRINT` (API 23–27, устарело в API 28)
- Документация: [Biometric authentication | Android Developers](https://developer.android.com/identity/sign-in/biometric-auth)

---

## Сборка и разработка

```bash
./gradlew :biometry:build
```

Перед коммитом/релизом убедитесь, что сборка проходит на всех таргетах (Android, iOS).

---

## Публикация

<!-- TODO: заполнить по мере настройки: Maven Central, GPG, секреты в CI -->

- [Publishing to Maven Central](https://www.jetbrains.com/help/kotlin/multiplatform-publish-libraries.html)
- [Central Portal](https://central.sonatype.org/publish-ea/publish-ea-guide/)
- [Gradle Maven Publish Plugin](https://vanniktech.github.io/gradle-maven-publish-plugin/central/)

---

## Contributing

<!-- TODO: добавить CONTRIBUTING.md и ссылку сюда; правила контрибуции, ветки, PR -->

Приветствуются багфиксы и улучшения документации. Для крупных изменений лучше сначала обсудить в issue.

---

## Лицензия

<!-- TODO: указать фактическую лицензию (например, Apache-2.0) и год/автора -->

См. файл [LICENSE](LICENSE).
