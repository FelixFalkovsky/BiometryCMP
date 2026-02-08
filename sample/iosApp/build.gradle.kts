
// iOS sample — нативный Xcode-проект, не Gradle-модуль.
// Сборка фреймворков выполняется Run Script в Xcode или вручную:
//   ./gradlew :biometry:linkDebugFrameworkIosSimulatorArm64 :sample:composeApp:linkDebugFrameworkIosSimulatorArm64
// Откройте sample/iosApp/BiometrySample.xcodeproj в Xcode и запустите на симуляторе или устройстве.

tasks.register("iosSampleHelp") {
    group = "help"
    description = "Print instructions for building and running the iOS sample"
    doLast {
        println("iOS sample: open BiometrySample.xcodeproj in Xcode and build (⌘R).")
        println("Frameworks are built automatically by the Run Script phase in Xcode.")
    }
}
