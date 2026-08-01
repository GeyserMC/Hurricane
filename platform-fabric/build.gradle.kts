plugins {
    id("hurricane.loom-conventions")
    id("com.gradleup.shadow")
    id("com.modrinth.minotaur")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    mods {
        create("hurricane-fabric") {
            sourceSet(sourceSets.main.get())
            sourceSet("main", ":platform-modded-common")
            sourceSet("main", ":core")
        }
    }
}

val common = configurations.create("common")
val developmentFabric: Configuration = configurations.getByName("developmentFabric")
val projectVersion = version.toString()

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    developmentFabric.extendsFrom(common)
}

dependencies {
    implementation(libs.fabric.loader)
    api(libs.fabric.api)
    runtimeOnly(project(":core"))

    common(project(":platform-modded-common")) {
        isTransitive = false
    }
    shadow(project(path = ":platform-modded-common", configuration = "transformProductionFabric")) {
        isTransitive = false
    }
    shadow(project(":core"))
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand("version" to projectVersion)
    }
}

tasks.jar {
    archiveClassifier.set("plain")
}

tasks.shadowJar {
    configurations = listOf(project.configurations.getByName("shadow"))
    archiveFileName.set("hurricane-fabric.jar")
    archiveClassifier.set("")
    from(rootProject.file("LICENSE"))
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("hurricane")
    versionNumber.set("$projectVersion-${System.getenv("GITHUB_RUN_NUMBER") ?: "local"}")
    versionType.set("release")
    uploadFile.set(tasks.shadowJar)
    loaders.addAll("fabric")
    gameVersions.addAll("26.2")
    failSilently.set(false)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
