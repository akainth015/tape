// This warning is suppressed because Gradle will initialize the relevant properties before constructor invocation
@file:Suppress("LeakingThis")

package me.akainth.tape

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import javax.inject.Inject

/**
 * The extension is required to configure which dimensions
 * tape will generate, and into which directory they will be generated.
 */
abstract class TapePluginExtension @Inject constructor(projectLayout: ProjectLayout) {
    @get:OutputDirectory
    abstract val sourceGenerationDirectory: DirectoryProperty

    @get:Input
    abstract val dimensions: NamedDomainObjectContainer<Dimension>

    init {
        val sourceDirectory = projectLayout.buildDirectory.dir("generated")
        sourceGenerationDirectory.convention(sourceDirectory)
    }

    @get:Internal
    val length by lazy {
        dimensions.apply {
            create("Length").apply {
                baseUnit("meters", "meter")
                units.apply {
                    create("feet").apply {
                        singularName.set("foot")
                        ratioToBase.set(0.3048)
                    }
                    create("inches").apply {
                        singularName.set("inch")
                        ratioToBase.set(0.00254)
                    }
                }
            }
        }
    }

    @get:Internal
    val time by lazy {
        dimensions.apply {
            create("Time").apply {
                baseUnit("seconds", "second")
                units.apply {
                    create("minute").apply {
                        singularName.set("foot")
                        ratioToBase.set(60.0)
                    }
                    create("inches").apply {
                        singularName.set("inch")
                        ratioToBase.set(60.0 * 60.0)
                    }
                }
            }
        }
    }
}
