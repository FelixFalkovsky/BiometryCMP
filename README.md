<p align="center">
  <img src="images/icon.png" width="96" alt="Biometry Auth KMP" />
</p>

# Biometry Auth KMP

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)](https://github.com/enumset/biometry-auth-kmp)
[![Kotlin](https://img.shields.io/badge/kotlin-2.2.20-purple.svg?logo=kotlin)](http://kotlinlang.org)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://opensource.org/licenses/Apache-2.0)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-lightgrey.svg)](https://kotlinlang.org/docs/multiplatform.html)

**Biometry authentication (Touch ID, Face ID, fingerprint) for Android and iOS from shared Kotlin Multiplatform code.**

---

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Samples](#samples)
- [Permissions](#permissions)
- [Building](#building)
- [License](#license)

---

## Features

- **Check availability** — `BiometryAuthenticator.isBiometryAvailable()` returns `BiometryAvailability` (supported type, error message).
- **Authenticate** — `BiometryAuthenticator.authenticate()` shows the system dialog and returns `BiometryResult` (Success / Cancelled / Error).
- **Shared models** — `BiometryResult`, `BiometryAvailability`, `BiometryType` in `commonMain`.
- **Android** — `BiometricManager` + `BiometricPrompt` (runs on main thread).
- **iOS** — `LocalAuthentication` framework (Touch ID / Face ID).

---

## Requirements

| | |
|---|---|
| **Gradle** | 8.0+ |
| **Kotlin** | 2.0+ |
| **Android** | minSdk 23+, compileSdk 34 |
| **iOS** | 11.0+ |

---

## Installation

**Repositories** (root `settings.gradle.kts` or `build.gradle.kts`):

```kotlin
repositories {
    mavenCentral()
    google()
}
```

**Dependency** (your KMP module):

```kotlin
// commonMain
commonMain.dependencies {
    implementation("com.enumSet:biometry-auth:1.0.0")
}
```

---

## Usage

### Common code

Use from a coroutine (recommended: `Dispatchers.Main` on Android for UI):

```kotlin
val authenticator = createBiometryAuthenticator()

// Check availability
val availability = authenticator.isBiometryAvailable()
if (!availability.isAvailable) {
    // availability.errorMessage, availability.biometryType
    return
}

// Authenticate
when (val result = authenticator.authenticate(
    title = "Sign in",
    subtitle = "Confirm your identity",
    negativeButtonText = "Cancel",
    allowDeviceCredentials = true
)) {
    is BiometryResult.Success -> { /* success */ }
    is BiometryResult.Cancelled -> { /* user cancelled */ }
    is BiometryResult.Error -> { /* result.message, result.code */ }
}
```

### Android

1. Before using biometry, set the `FragmentActivity` (e.g. in `onCreate`):

```kotlin
import com.enumSet.biometry.setFragmentActivityForBiometry

// In your FragmentActivity
setFragmentActivityForBiometry(this)
```

2. Call from a coroutine with `Dispatchers.Main` (dialog must run on main thread):

```kotlin
lifecycleScope.launch(Dispatchers.Main) {
    val authenticator = createBiometryAuthenticator()
    val result = authenticator.authenticate(
        title = "Sign in",
        subtitle = "Confirm your identity",
        negativeButtonText = "Cancel",
        allowDeviceCredentials = true
    )
}
```

### iOS

Use the same common API: `createBiometryAuthenticator()` and `authenticate()`. The `actual` implementation uses `LocalAuthentication`.

Add to your app **Info.plist** (required for Face ID):

```xml
<key>NSFaceIDUsageDescription</key>
<string>Authenticate with Face ID or Touch ID</string>
```

---

## Samples

| Module | Description |
|--------|-------------|
| `sample/androidApp` | Android app (availability check + auth). |
| `sample/composeApp` | Compose Multiplatform sample (Android + iOS). |
| `sample/iosApp` | Native iOS app + Xcode integration. |

**Run Android sample:**

```bash
./gradlew :sample:androidApp:assembleDebug
./gradlew :sample:androidApp:installDebug
```

**iOS:** build the framework with `./gradlew :biometry:linkDebugFrameworkIosSimulatorArm64` and follow [sample/iosApp/README.md](sample/iosApp/README.md) for Xcode.

---

## Permissions

The library does **not** declare permissions. Your app must add them.

**Android** — in your app manifest:

- `USE_BIOMETRIC` (API 28+)
- `USE_FINGERPRINT` (API 23–27, deprecated in 28)

See [Biometric authentication \| Android Developers](https://developer.android.com/identity/sign-in/biometric-auth).

---

## Building

```bash
./gradlew :biometry:build
```

---

## License

[Apache-2.0](LICENSE)
