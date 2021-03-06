/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package me.akainth.tape

import org.gradle.api.Plugin
import org.gradle.api.Project

class TapePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register a task
        project.pluginManager.apply("java")
        project.tasks.register("tape", GenerateDimensionsTask::class.java)
    }
}
