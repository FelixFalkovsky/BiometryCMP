#!/bin/sh
# Собирает BiometryAuthKMP и ComposeApp и копирует в sample/iosApp/Frameworks/.
# Запускать из корня репозитория: ./sample/iosApp/build-frameworks.sh [device|simulator]
# По умолчанию: simulator (для симулятора). Для устройства: device.

set -e
REPO_ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$REPO_ROOT"

case "${1:-simulator}" in
  device|iphoneos)
    ./gradlew :biometry:linkDebugFrameworkIosArm64 :sample:composeApp:linkDebugFrameworkIosArm64 --no-daemon -q
    BIN="biometry/build/bin/iosArm64/debugFramework"
    COMPOSE_BIN="sample/composeApp/build/bin/iosArm64/debugFramework"
    ;;
  *)
    ./gradlew :biometry:linkDebugFrameworkIosSimulatorArm64 :sample:composeApp:linkDebugFrameworkIosSimulatorArm64 --no-daemon -q
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
echo "Frameworks copied to $FRAMEWORKS_DIR"
