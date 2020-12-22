package me.akainth.tape

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GenerateDimensionsTask : DefaultTask() {
    @Input
    val dimensions = mutableListOf<Dimension>()

    @OutputDirectory
    var targetDirectory: File = project.buildDir.resolve("generated/")

    fun dimension(name: String, base: String, setUpUnits: Action<in Dimension>): Dimension {
        val dimension = Dimension(name, base)
        setUpUnits.execute(dimension)
        dimensions += dimension
        return dimension
    }

    @TaskAction
    fun generateDimensions() {
        dimensions.map { dimension -> dimension.generateFile() }.forEach { it.writeTo(targetDirectory) }
    }
}