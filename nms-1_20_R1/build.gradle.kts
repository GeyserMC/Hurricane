plugins {
    id("org.geysermc.hurricane.paperweight-conventions")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


dependencies {
    api(project(":nms-provider"))
    api(project(":reflection"))
    compileOnly(libs.paper.api)
    paperweight.paperDevBundle("1.20-R0.1-SNAPSHOT")
}

description = "nms-1_20_R1"