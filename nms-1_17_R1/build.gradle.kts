plugins {
    id("org.geysermc.hurricane.paperweight-conventions")
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

dependencies {
    api(project(":nms-provider"))
    compileOnly(libs.paper.api)
    paperweight.paperDevBundle("1.17.1-R0.1-SNAPSHOT")
}

description = "nms-1_17_R1"
