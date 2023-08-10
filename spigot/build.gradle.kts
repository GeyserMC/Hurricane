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
    api(project(":nms-provider"))
    api(project(":nms-1_17_R1"))
    api(project(":nms-1_18_R1"))
    api(project(":nms-1_19_R1"))
    api(project(":nms-1_18_R2"))
    api(project(":nms-1_19_R2"))
    api(project(":nms-1_20_R1"))
    api(project(":reflection"))
    compileOnly(libs.bundles.geyser)
    compileOnly(libs.paper.api)
}

description = "hurricane-spigot"
