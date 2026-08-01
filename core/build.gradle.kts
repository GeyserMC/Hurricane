plugins {
    id("hurricane.java-conventions")
    // Lets the Fabric and NeoForge mod source sets pull this module in without applying Loom to it.
    id("dev.architectury.loom-companion")
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
