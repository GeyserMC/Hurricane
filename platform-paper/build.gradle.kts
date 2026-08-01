import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService

plugins {
    id("hurricane.java-conventions")
    id("com.gradleup.shadow")
    id("com.modrinth.minotaur")
}

// The Paper API ships Java 21 bytecode (1.20.5) and Java 25 bytecode (26.2), so this module needs a
// JDK 25 toolchain to read it; indra keeps the release target at 17 so the jar still loads on Java 17.
indra {
    javaVersions {
        minimumToolchain(25)
        // The minimum Paper API (1.20.5) is published as Java 21 bytecode, so tests that link it
        // can't run on a JDK 17; test on the build toolchain rather than indra's default target (17).
        testWith().set(setOf(25))
    }
}

val projectVersion = version.toString()
val minimumPaperApi =
    "io.papermc.paper:paper-api:${libs.versions.paper.minimum.get()}@jar"
val minimumGeyserApi =
    "org.geysermc.geyser:api:${libs.versions.geyser.minimum.get()}@jar"
val currentPaperCompileClasspath = configurations.create("currentPaperCompileClasspath") {
    isCanBeConsumed = false
    isCanBeResolved = true
}
val javaToolchains = extensions.getByType<JavaToolchainService>()

dependencies {
    api(project(":core"))
    compileOnly(minimumPaperApi)
    compileOnly(minimumGeyserApi)
    compileOnly(libs.floodgate.api)
    // The minimum Paper and Geyser APIs are resolved as bare jars, so the API types they
    // expose in their own signatures have to be supplied here.
    compileOnly(libs.adventure.api)
    compileOnly(libs.guava)
    currentPaperCompileClasspath(project(":core"))
    currentPaperCompileClasspath(libs.paper.api)
    currentPaperCompileClasspath(libs.geyser.api)
    currentPaperCompileClasspath(libs.floodgate.api)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(minimumPaperApi)
    testImplementation(libs.adventure.api)
    testImplementation(libs.guava)
    testRuntimeOnly(libs.junit.platform.launcher)
}

val compileCurrentPaperJava = tasks.register<JavaCompile>("compileCurrentPaperJava") {
    source(sourceSets.main.get().java)
    classpath = currentPaperCompileClasspath
    destinationDirectory.set(layout.buildDirectory.dir("classes/java/currentPaper"))
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    options.release.set(indra.javaVersions().target())
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to projectVersion)
    }
}

tasks.jar {
    archiveClassifier.set("plain")
}

tasks.shadowJar {
    archiveFileName.set("hurricane-paper.jar")
    archiveClassifier.set("")
    from(rootProject.file("LICENSE"))
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("hurricane")
    versionNumber.set("$projectVersion-${System.getenv("GITHUB_RUN_NUMBER") ?: "local"}")
    versionType.set("release")
    uploadFile.set(tasks.shadowJar)
    loaders.addAll("paper", "folia")
    // Paper support reaches back to 1.20.5; expand this list before release.
    gameVersions.addAll("26.2")
    failSilently.set(false)
}

tasks.build {
    dependsOn(compileCurrentPaperJava)
    dependsOn(tasks.shadowJar)
}
