# iOS Sample

Демо-приложение для проверки библиотеки **biometry-auth** на iOS (Face ID / Touch ID). UI и логика реализованы в KMP (модуль `:sample:composeApp`), в этом приложении — только хост и жизненный цикл.

## Почему без CocoaPods/SPM

Интеграция сделана через **ручную подстановку фреймворков** (Gradle собирает `.framework`, скрипт копирует в `Frameworks/`, Xcode линкует их). Это даёт:

- **Минимум зависимостей**: не нужны CocoaPods или SPM, только Xcode и Gradle.
- **Предсказуемость**: один и тот же процесс для любого окружения (CI, чужие машины).
- **Совместимость**: работает с любым Xcode-проектом без изменения структуры под pod’ы.

**CocoaPods** имеет смысл, если вы хотите подключать KMP как pod (плагин `kotlin("native.cocoapods")` + `pod install`) — тогда в проекте появится `Podfile` и папка `Pods/`. Плюсы: один шаг `pod install` для команды, привычно для iOS-разработчиков. Минусы: зависимость от Ruby/CocoaPods, нужно синхронизировать версию Kotlin и pod. Оба варианта (фреймворки вручную и CocoaPods) поддерживаются JetBrains и стабильны; выбор зависит от предпочтений команды и CI. **SPM** для KMP обычно не используется: Kotlin/Native не собирает нативные Swift Package, нужны обёртки и лишние шаги.

## Ошибка «No such module 'ComposeApp'»

Она появляется, пока фреймворки не собраны. Соберите их вручную **из корня репозитория**:

```bash
# Для симулятора (по умолчанию)
./sample/iosApp/build-frameworks.sh

# Для устройства
./sample/iosApp/build-frameworks.sh device
```

Либо вручную:
```bash
./gradlew :biometry:linkDebugFrameworkIosSimulatorArm64 :sample:composeApp:linkDebugFrameworkIosSimulatorArm64
# затем скопируйте biometry/build/bin/iosSimulatorArm64/debugFramework/*.framework и sample/composeApp/build/bin/iosSimulatorArm64/debugFramework/ComposeApp.framework в sample/iosApp/Frameworks/
```

После этого откройте проект в Xcode и соберите (⌘R). Дальше Run Script при сборке будет подставлять нужные фреймворки сам.

## Запуск

1. Откройте в Xcode: **`BiometrySample.xcodeproj`** (из папки `sample/iosApp/`).
2. Выберите **симулятор** (например, iPhone 15) или **подключённое устройство** в качестве цели.
3. Нажмите **Run** (⌘R).

При каждой сборке Run Script собирает фреймворки под выбранную цель (симулятор → `iosSimulatorArm64`, устройство → `iosArm64`) и копирует их в `Frameworks/`. Нужны JDK и доступ в сеть для Gradle.

**Если при сборке для устройства появляется ошибка** вроде *"building for 'iOS', but linking in object file built for 'iOS-simulator'"* — значит в `Frameworks/` лежат фреймворки для симулятора. Сделайте **Product → Clean Build Folder** (⇧⌘K), затем снова выберите устройство как цель и **Run** (⌘R). Run Script пересоберёт фреймворки под устройство.

## Структура

- **BiometrySample/** — исходники приложения:
  - `BiometrySampleApp.swift` — точка входа (@main).
  - `ContentView.swift` — хост Compose (`ComposeHostView` → `BiometrySampleViewController()` из KMP).
  - `Info.plist` — в т.ч. `NSFaceIDUsageDescription` для Face ID.
- **Frameworks/** — сюда копируются фреймворки при сборке (в .gitignore).
- **COMPOSE_INTEGRATION.md** — пошаговая интеграция Compose в iOS.

## Если класс из KMP не находится в Swift

В этом проекте точка входа — **`ComposeAppMainKt.BiometrySampleViewController()`** (модуль даёт префикс `ComposeApp`, файл `Main.kt` → `MainKt`). Если после обновления KMP Xcode перестанет находить тип, откройте `ComposeApp.framework/Headers/ComposeApp.h` и найдите объявление с `BiometrySampleViewController`.
