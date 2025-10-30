plugins {
    id("hurricane.build-logic")
    id("com.gradleup.shadow")
}

tasks {
    jar {
        archiveClassifier.set("unshaded")
        archiveVersion.set("")
        from(project.rootProject.file("LICENSE"))
    }

    shadowJar {
        archiveClassifier.set("shaded")
        archiveVersion.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}