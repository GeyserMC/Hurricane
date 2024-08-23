plugins {
    id("org.geysermc.hurricane.shadow-conventions")
}

relocate("org.spongepowered.configurate")
relocate("io.leangen.geantyref")
relocate("com.typesafe.config")

dependencies {
    api(libs.configurate.hocon) {
        exclude("org.checkerframework")
    }
    api(project(":reflection"))
    compileOnly(libs.bundles.geyser)
    compileOnly(libs.paper.api)
}

description = "hurricane-spigot"
