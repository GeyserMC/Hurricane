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
    paperweight.paperDevBundle("1.19.3-R0.1-SNAPSHOT")
}

description = "nms-1_19_R2"
