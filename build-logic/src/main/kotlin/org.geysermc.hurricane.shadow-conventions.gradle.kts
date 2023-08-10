plugins {
    id("org.geysermc.hurricane.java-conventions")
    id("com.github.johnrengelman.shadow")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}