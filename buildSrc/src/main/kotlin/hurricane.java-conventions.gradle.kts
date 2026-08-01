plugins {
    `java-library`
    id("net.kyori.indra")
}

indra {
    javaVersions {
        // The Paper artifact still has to load on servers running Java 17.
        target(17)
    }
}

java {
    withSourcesJar()
}
