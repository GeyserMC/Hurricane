plugins {
    id("org.geysermc.hurricane.paperweight-conventions")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


dependencies {
    api(project(":nms-provider"))
    compileOnly(libs.paper.api)
    paperweight.paperDevBundle("1.18.1-R0.1-SNAPSHOT")
}

description = "nms-1_18_R1"
