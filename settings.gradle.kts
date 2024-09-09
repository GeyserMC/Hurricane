rootProject.name = "hurricane-parent"
include(":reflection")
include(":hurricane-spigot")
project(":hurricane-spigot").projectDir = file("spigot")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}
