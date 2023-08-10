import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.kotlin.dsl.named

fun Project.relocate(pattern: String) {
    tasks.named<ShadowJar>("shadowJar") {
        relocate(pattern, "org.geysermc.hurricane.relocations.$pattern")
    }
}

fun Project.exclude(group: String) {
    tasks.named<ShadowJar>("shadowJar") {
        exclude(group)
    }
}