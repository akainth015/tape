package me.akainth.tape

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File


@Suppress("unused")
open class GenerateDimensionsTask : DefaultTask() {
    @Input
    val dimensions = arrayListOf<Dimension>()

    @Input
    var useExperimentalInline = true

    @OutputDirectory
    var targetDirectory: File = project.buildDir.resolve("generated/")

    fun dimension(name: String, base: String, setUpUnits: Action<in Dimension>): Dimension {
        val dimension = Dimension(name, base)
        setUpUnits.execute(dimension)
        dimensions += dimension
        return dimension
    }

    fun dimension(name: String, quotientDimension: QuotientDimension): RatioDimension {
        val dimension = RatioDimension(name, quotientDimension.top, quotientDimension.bottom)
        dimensions += dimension
        return dimension
    }

    fun dimension(name: String, productDimension: ProductDimension): TimesDimension {
        val dimension = TimesDimension(name, productDimension.first, productDimension.second)
        dimensions += dimension
        return dimension
    }

    @TaskAction
    fun generateDimensions() {
        dimensions.map { dimension -> dimension.generateFile(useExperimentalInline) }.forEach { it.writeTo(targetDirectory) }
    }
}
