plugins {
    id("hurricane.shadow-conventions")
    id("hurricane.downgrader-conventions")
}

relocate("org.spongepowered.configurate")
relocate("io.leangen.geantyref")
relocate("com.typesafe.config")

dependencies {
    api(projects.reflection)
    api(projects.core)
    compileOnly(libs.paper.api)
}

description = "hurricane-spigot"
