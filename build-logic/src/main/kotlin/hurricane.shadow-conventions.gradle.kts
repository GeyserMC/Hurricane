plugins {
    id("hurricane.java-conventions")
    id("com.gradleup.shadow")
    id("xyz.wagyourtail.jvmdowngrader")
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

    downgradeJar {
        mustRunAfter(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    build {
        dependsOn(shadowJar)
        dependsOn(downgradeJar)
    }
}

jvmdg {
    downgradeTo = JavaVersion.VERSION_1_8
}