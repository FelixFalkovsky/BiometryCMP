pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "biometry-auth-kmp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":biometry")
include(":sample:androidApp")
include(":sample:composeApp")
include(":sample:iosApp")
