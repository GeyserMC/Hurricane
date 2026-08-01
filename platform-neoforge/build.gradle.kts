plugins {
    id("hurricane.loom-conventions")
    id("com.gradleup.shadow")
    id("com.modrinth.minotaur")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    mods {
        create("hurricane-neoforge") {
            sourceSet(sourceSets.main.get())
            sourceSet("main", ":platform-modded-common")
            sourceSet("main", ":core")
        }
    }
}

val common = configurations.create("common")
val developmentNeoForge: Configuration = configurations.getByName("developmentNeoForge")
val projectVersion = version.toString()

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    developmentNeoForge.extendsFrom(common)
}

dependencies {
    modules {
        module("com.google.guava:listenablefuture") {
            replacedBy("com.google.guava:guava", "listenablefuture is part of guava")
        }
    }

    neoForge(libs.neoforge)
    runtimeOnly(project(":core"))

    common(project(":platform-modded-common")) {
        isTransitive = false
    }
    shadow(project(path = ":platform-modded-common", configuration = "transformProductionNeoForge")) {
        isTransitive = false
    }
    shadow(project(":core"))
}

tasks.processResources {
    filesMatching("META-INF/neoforge.mods.toml") {
        expand("version" to projectVersion)
    }
}

tasks.jar {
    archiveClassifier.set("plain")
}

tasks.shadowJar {
    configurations = listOf(project.configurations.getByName("shadow"))
    archiveFileName.set("hurricane-neoforge.jar")
    archiveClassifier.set("")
    from(rootProject.file("LICENSE"))
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("hurricane")
    versionNumber.set("$projectVersion-${System.getenv("GITHUB_RUN_NUMBER") ?: "local"}")
    versionType.set("release")
    uploadFile.set(tasks.shadowJar)
    loaders.addAll("neoforge")
    gameVersions.addAll("26.2")
    failSilently.set(false)
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
