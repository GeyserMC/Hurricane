rootProject.name = "hurricane-parent"
include(":nms-provider")
include(":nms-1_17_R1")
include(":nms-1_18_R2")
include(":nms-1_18_R1")
include(":nms-1_19_R1")
include(":nms-1_19_R2")
include(":nms-1_20_R1")
include(":reflection")
include(":hurricane-spigot")
project(":hurricane-spigot").projectDir = file("spigot")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}
