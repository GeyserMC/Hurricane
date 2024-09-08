plugins {
    `java-library`
    `maven-publish`
}

repositories {
    //mavenLocal()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://repo.maven.apache.org/maven2/")
}

group = properties["group"] as String
version = properties["version"] as String
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks {
    processResources {
        filesMatching(listOf("plugin.yml")) {
            expand(
                "version" to properties["version"]
            )
        }
    }
}