import org.gradle.kotlin.dsl.invoke

plugins {
    id("hurricane.build-logic")
    id("io.papermc.paperweight.userdev")
}

tasks {
    assemble {
        // according to paperweight-test-plugin
        dependsOn(reobfJar)
    }
}