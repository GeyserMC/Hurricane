plugins {
    id("hurricane.modded-conventions")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

val common: Configuration by configurations.creating
// Without this, the mixin config isn't read properly with the runServer neoforge task
val developmentNeoForge: Configuration = configurations.getByName("developmentNeoForge")

configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentNeoForge.extendsFrom(configurations["common"])
}

dependencies {
    neoForge(libs.neoforge)
    common(project(":mod-shared", configuration = "namedElements")) { isTransitive = false }
    shadow(project(path = ":mod-shared", configuration = "transformProductionNeoForge")) { isTransitive = false }

    include(libs.configurate.hocon)
    include(libs.configurate.core)
    include(libs.geantyref)
    include(libs.typesafe)
}

tasks {
    remapJar {
        archiveBaseName.set("hurricane-neoforge")
    }
//    remapModrinthJar {
//        archiveBaseName.set("hurricane-neoforge")
//    }
}

//modrinth {
//    loaders.add("neoforge")
//}