plugins {
    id("hurricane.build-logic")
    id("com.gradleup.shadow")
    id("xyz.wagyourtail.jvmdowngrader")
}

tasks {
    downgradeJar {
        mustRunAfter(shadowJar)
        inputFile.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    build {
        dependsOn(downgradeJar)
    }
}

jvmdg {
    downgradeTo = JavaVersion.VERSION_1_8
}