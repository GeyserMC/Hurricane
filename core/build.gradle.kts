plugins {
    id("hurricane.shadow-conventions")
}

dependencies {
    api(libs.configurate.hocon) {
        exclude("org.checkerframework")
    }
    compileOnly(libs.bundles.geyser)
}
