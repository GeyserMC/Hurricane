plugins {
    id("hurricane.modded-conventions")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

val common: Configuration by configurations.creating
val developmentFabric: Configuration = configurations.getByName("developmentFabric")

configurations {
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    developmentFabric.extendsFrom(configurations["common"])
}

tasks {
    remapJar {
        archiveBaseName.set("hurricane-fabric")
    }
//    remapModrinthJar {
//        archiveBaseName.set("hurricane-fabric")
//    }
}

dependencies {
    modImplementation(libs.fabric.loader)
    modApi(libs.fabric.api)
    common(project(":mod-shared", configuration = "namedElements"))
    shadow(project(path = ":mod-shared", configuration = "transformProductionFabric")) {
        isTransitive = false
    }

    include(libs.configurate.hocon)
    include(libs.configurate.core)
    include(libs.geantyref)
    include(libs.typesafe)
}

//modrinth {
//    loaders.add("fabric")
//}