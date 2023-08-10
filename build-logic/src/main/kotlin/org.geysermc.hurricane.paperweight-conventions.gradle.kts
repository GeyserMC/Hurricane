
plugins {
    id("org.geysermc.hurricane.java-conventions")
    id("io.papermc.paperweight.userdev")
}

tasks {
    assemble {
        // according to paperweight-test-plugin
        dependsOn(reobfJar)
    }
}