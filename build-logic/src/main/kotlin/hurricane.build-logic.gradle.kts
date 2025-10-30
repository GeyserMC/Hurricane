plugins {
    `java-library`
    `maven-publish`
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks {
    processResources {
        filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
            expand(
                "id" to "hurricane",
                "name" to "Hurricane",
                "version" to project.version,
                "description" to "Hacky fixes to make Bedrock players that join via Geyser happy."
            )
        }
        filesMatching(listOf("plugin.yml")) {
            expand(
                "version" to properties["version"]
            )
        }
    }
}

repositories {
    // mavenLocal()
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.neoforged.net/releases")
    maven("https://repo.opencollab.dev/main/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io") {
        content {
            includeGroupByRegex("com.github.*")
        }
    }
}

group = properties["group"] as String
version = properties["version"] as String
java.sourceCompatibility = JavaVersion.VERSION_21

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}