pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.architectury.dev/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven {
                    name = "paperMinimumApi"
                    url = uri("https://repo.papermc.io/repository/maven-public/")
                    metadataSources {
                        artifact()
                    }
                }
            }
            filter {
                includeVersion(
                    "io.papermc.paper",
                    "paper-api",
                    "1.20.5-R0.1-20240429.035133-20"
                )
            }
        }
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.opencollab.dev/main/")
        maven("https://repo.spongepowered.org/repository/maven-public/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
    }
}

rootProject.name = "Hurricane"

include(
    ":core",
    ":platform-paper",
    ":platform-modded-common",
    ":platform-fabric",
    ":platform-neoforge",
)
