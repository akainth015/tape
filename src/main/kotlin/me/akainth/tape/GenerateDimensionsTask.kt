package me.akainth.tape

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.Unit


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

    val length
        get(): Dimension = dimension("Length", "meters") {
            it.apply {
                unit("feet", 0.3048)
                unit("inches", 0.0254)
                addMetricUnits()
            }
        }

    val time by lazy {
        dimension("Time", "seconds") {
            it.apply {
                unit("minutes", 60)
                milli()
                nano()
            }
        }
    }

    val mass by lazy {
        dimension("Mass", "grams") {
            it.apply {
                unit("pounds", 453.5924)
                it.kilo()
            }
        }
    }

    val bytes by lazy {
        dimension("Bytes", "bytes") {
            it.apply {
                it.kilo()
                it.mega()
                it.giga()
                it.tera()
            }
        }
    }

    val area by lazy {
        dimension("Area", length * length)
    }

    val volume by lazy {
        dimension("Volume", area * length)
    }

    val speed by lazy {
        dimension("Speed", length / time)
    }

    val acceleration by lazy {
        dimension("Acceleration", speed / time)
    }

    val force by lazy {
        dimension("Force", mass * acceleration)
    }

    @TaskAction
    fun generateDimensions() {
        dimensions.map { dimension -> dimension.generateFile(useExperimentalInline) }
            .forEach { it.writeTo(targetDirectory) }
    }
}
