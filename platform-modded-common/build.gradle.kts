plugins {
    id("hurricane.loom-conventions")
}

architectury {
    common("fabric", "neoforge")
}

dependencies {
    api(project(":core"))
    compileOnly(libs.geyser.api)
    compileOnly(libs.floodgate.api)
    compileOnly(libs.mixin)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
