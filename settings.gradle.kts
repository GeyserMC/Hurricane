@file:Suppress("UnstableApiUsage")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "hurricane-parent"
include(":reflection")
include(":mod-shared")
include(":fabric")
include(":neoforge")
include(":spigot")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.opencollab.dev/maven-snapshots/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.neoforged.net/releases")
    }
    includeBuild("build-logic")
}
