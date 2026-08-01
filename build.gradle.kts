plugins {
    base
}

allprojects {
    group = providers.gradleProperty("project_group").get()
    version = providers.gradleProperty("project_version").get()
}
