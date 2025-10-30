import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("hurricane.shadow-conventions")
    id("architectury-plugin")
    id("dev.architectury.loom")
}

architectury {
    minecraft = libs.versions.minecraft.version.get()
}

java {
    withSourcesJar()
}

loom {
    silentMojangMappingsLicense()
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
}

tasks {
    shadowJar {
        // Mirrors the example fabric project, otherwise tons of dependencies are shaded that shouldn't be
        configurations = listOf(project.configurations.shadow.get())

        // The remapped shadowJar is the final desired mod jar
        archiveVersion.set("")
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
        archiveVersion.set(project.version.toString())
    }

//    // TODO can we make this not stupid pls
//    register("remapModrinthJar", RemapJarTask::class) {
//        dependsOn(shadowJar)
//        inputFile.set(shadowJar.get().archiveFile)
//        archiveVersion.set(project.version.toString() + "+build." + System.getenv("GITHUB_RUN_NUMBER"))
//        archiveClassifier.set("")
//    }

    build {
        dependsOn(remapJar)
    }
}