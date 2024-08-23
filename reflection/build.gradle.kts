import org.gradle.plugins.ide.eclipse.model.AbstractClasspathEntry
import org.gradle.plugins.ide.eclipse.model.AccessRule

plugins {
    id("org.geysermc.hurricane.java-conventions")
    // suppress unsafe access errors for eclipse
    id("eclipse")
}

dependencies {
    compileOnly(libs.spigot.api)
}

// found how to do this here https://github.com/JFormDesigner/markdown-writer-fx/blob/main/build.gradle.kts
eclipse {
	classpath {
		file {
			whenMerged.add( object: Action<org.gradle.plugins.ide.eclipse.model.Classpath> {
				override fun execute( classpath: org.gradle.plugins.ide.eclipse.model.Classpath ) {
					val jre = classpath.entries.find {
						it is AbstractClasspathEntry &&
							it.path.contains("org.eclipse.jdt.launching.JRE_CONTAINER")
					} as AbstractClasspathEntry

					// make sun.misc & sun.reflect accessible in Eclipse project
					// (when refreshing Gradle project in buildship)
					jre.accessRules.add(AccessRule("accessible", "sun/misc/**"))
                    jre.accessRules.add(AccessRule("accessible", "sun/reflect/**"))

					// remove trailing slash from jre path
					if (jre.path.endsWith("/")) jre.path = jre.path.substring(0, jre.path.length - 1)
				}
			} )
		}
	}
}

description = "reflection"