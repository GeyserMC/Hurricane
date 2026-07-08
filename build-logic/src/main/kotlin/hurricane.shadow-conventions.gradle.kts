plugins {
    id("hurricane.java-conventions")
    id("com.gradleup.shadow")
}

tasks {
    jar {
        archiveClassifier.set("unshaded")
        archiveVersion.set("")
    }

    shadowJar {
        archiveClassifier.set("shaded")
        archiveVersion.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}