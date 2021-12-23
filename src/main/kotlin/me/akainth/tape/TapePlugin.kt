package me.akainth.tape

import org.gradle.api.Plugin
import org.gradle.api.Project

class TapePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("tape", TapeExtension::class.java, project)

        project.tasks.register("tape") {
            extension.dimensions.map {
                it
                    .map { dimension ->
                        dimension.generateFile()
                    }
                    .forEach { fileSpec ->
                        fileSpec.writeTo(extension.targetDirectory.get().asFile)
                    }
            }.get()
        }
    }
}
