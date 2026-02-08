#!/bin/sh
# Собирает BiometryAuthKMP и ComposeApp и копирует в sample/iosApp/Frameworks/.
# Запускать из корня репозитория: ./sample/iosApp/build-frameworks.sh [device|simulator] [clean]
# По умолчанию: simulator. Для устройства: device. Опция clean — полная пересборка без кэша Gradle.

set -e
REPO_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$REPO_ROOT"

VARIANT="${1:-simulator}"
if [ "$2" = "clean" ]; then
  echo "Cleaning Gradle build..."
  ./gradlew clean --no-daemon -q
fi

case "$VARIANT" in
  device|iphoneos)
    echo "Building frameworks for device (iosArm64)..."
    ./gradlew :biometry:linkDebugFrameworkIosArm64 :sample:composeApp:linkDebugFrameworkIosArm64 --no-daemon -q --no-build-cache
    BIN="biometry/build/bin/iosArm64/debugFramework"
    COMPOSE_BIN="sample/composeApp/build/bin/iosArm64/debugFramework"
    ;;
  *)
    echo "Building frameworks for simulator (iosSimulatorArm64)..."
    ./gradlew :biometry:linkDebugFrameworkIosSimulatorArm64 :sample:composeApp:linkDebugFrameworkIosSimulatorArm64 --no-daemon -q --no-build-cache
    BIN="biometry/build/bin/iosSimulatorArm64/debugFramework"
    COMPOSE_BIN="sample/composeApp/build/bin/iosSimulatorArm64/debugFramework"
    ;;
esac

FRAMEWORKS_DIR="$(cd "$(dirname "$0")" && pwd)/Frameworks"
mkdir -p "$FRAMEWORKS_DIR"
rm -rf "$FRAMEWORKS_DIR/BiometryAuthKMP.framework"
rm -rf "$FRAMEWORKS_DIR/ComposeApp.framework"
cp -R "$REPO_ROOT/$BIN/BiometryAuthKMP.framework" "$FRAMEWORKS_DIR/"
cp -R "$REPO_ROOT/$COMPOSE_BIN/ComposeApp.framework" "$FRAMEWORKS_DIR/"
echo ""
echo "Frameworks copied to $FRAMEWORKS_DIR"
echo "In Xcode: Product → Clean Build Folder (⇧⌘K), then Build & Run."
