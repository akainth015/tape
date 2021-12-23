package me.akainth.tape

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

abstract class TapeExtension(project: Project) {
    @get:Input
    abstract val dimensions: SetProperty<Dimension>

    @get:Input
    abstract val targetDirectory: DirectoryProperty

    init {
        targetDirectory.convention(project.layout.buildDirectory.dir("generated"))
    }

    fun dimension(name: String, base: String, setUpUnits: Action<in Dimension>): Dimension {
        val dimension = Dimension(name, base)
        setUpUnits.execute(dimension)
        dimensions.add(dimension)
        return dimension
    }

    fun dimension(name: String, quotientDimension: QuotientDimension): RatioDimension {
        val dimension = RatioDimension(name, quotientDimension.top, quotientDimension.bottom)
        dimensions.add(dimension)
        return dimension
    }

    fun dimension(name: String, productDimension: ProductDimension): TimesDimension {
        val dimension = TimesDimension(name, productDimension.first, productDimension.second)
        dimensions.add(dimension)
        return dimension
    }

    @get:Internal
    val length by lazy {
        dimension("Length", "meters") {
            it.apply {
                baseUnit.singular = "meter"
                alias("Distance")
                unit("ft", 0.3048)
                unit("inches", 0.0254).apply {
                    singular = "inch"
                }
                centi()
                milli()
                kilo()
            }
        }
    }

    @get:Internal
    val time by lazy {
        dimension("Time", "s") {
            it.apply {
                unit("minutes", 60).apply {
                    singular = "minute"
                }
                milli()
                nano()
            }
        }
    }

    @get:Internal
    val mass by lazy {
        dimension("Mass", "g") {
            it.apply {
                unit("pounds", 453.5924)
                it.kilo()
            }
        }
    }

    @get:Internal
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

    @get:Internal
    val area by lazy {
        dimension("Area", length * length)
    }

    @get:Internal
    val volume by lazy {
        dimension("Volume", area * length)
    }

    @get:Internal
    val speed by lazy {
        dimension("Speed", length / time)
    }

    @get:Internal
    val acceleration by lazy {
        dimension("Acceleration", speed / time)
    }

    @get:Internal
    val force by lazy {
        dimension("Force", mass * acceleration).apply {
            unit("N", 1)
        }
    }
}
