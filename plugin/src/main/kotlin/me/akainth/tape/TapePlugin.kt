package me.akainth.tape

import org.gradle.api.Project
import org.gradle.api.Plugin

/**
 * A Gradle plugin to register the "tape" task and extension
 */
class TapePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        // Register the extension
        val extension = project.extensions.create("tape", TapePluginExtension::class.java)

        // Register a task
        project.task("tape") { task ->
            task.doLast {
                extension.dimensions.forEach {
                    println("tape is generating the $it dimension")
                    it.build().writeTo(System.out)
                }
            }
        }
    }
}