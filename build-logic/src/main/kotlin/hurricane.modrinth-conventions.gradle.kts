plugins {
    id("com.modrinth.minotaur")
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN")) // Even though this is the default value, apparently this prevents GitHub Actions caching the token?
    projectId.set("hurricane")
    //versionNumber.set(project.version as String + "-" + System.getenv("GITHUB_RUN_NUMBER"))
    versionType.set("release")

    syncBodyFrom.set(rootProject.file("README.md").readText())
    //changelog.set(rootProject.file("CHANGELOG.md").readText())

    //uploadFile.set(tasks.getByPath("remapModrinthJar"))
    //gameVersions.addAll("1.21.3")
    failSilently.set(false)
}