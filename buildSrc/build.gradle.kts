plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.architectury.dev/")
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.indra)
    implementation(libs.architectury.plugin.library)
    implementation(libs.architectury.loom.plugin)
    implementation(libs.loom.companion.plugin)
    implementation(libs.shadow.plugin)
    implementation(libs.minotaur)
}
