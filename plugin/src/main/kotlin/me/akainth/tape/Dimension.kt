package me.akainth.tape

import com.squareup.kotlinpoet.*
import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import javax.inject.Inject

/**
 * A dimension represents a quality of an object, such as its length, duration, area, etc.
 */
abstract class Dimension @Inject constructor() : Named {
    @get:Input
    abstract var baseUnit: Property<TapeUnit>

    @get:Input
    abstract var units: NamedDomainObjectContainer<TapeUnit>

    @get:Inject
    abstract val objectFactory: ObjectFactory

    fun baseUnit(baseUnitName: String, singular: String = baseUnitName): TapeUnit {
        return objectFactory.newInstance(TapeUnit::class.java, baseUnitName).apply {
            ratioToBase.set(1.0)
            singularName.set(singular)

            baseUnit.set(this)
        }
    }

    val className: ClassName
        @Internal get() = ClassName("me.akainth.tape.dimensions", name)

    /**
     * Build a KotlinPoet FileSpec out of this dimension that can be written to disk
     */
    fun build(): FileSpec {
        val baseUnit = this.baseUnit.get()
        return FileSpec.builder("me.akainth.tape.dimensions", name).apply {
            val dimensionClass = TypeSpec.classBuilder(className).apply {
                addModifiers(KModifier.VALUE)
                addAnnotation(JvmInline::class)

                val dimensionConstructor = FunSpec.constructorBuilder().apply {
                    addParameter(baseUnit.name, DOUBLE)
                }.build()
                primaryConstructor(dimensionConstructor)

                val baseUnitProperty = PropertySpec.builder(baseUnit.name, DOUBLE).apply {
                    initializer(baseUnit.name)
                }.build()
                addProperty(baseUnitProperty)

                val unitProperties = units.map { it.getProperty(this@Dimension) }
                addProperties(unitProperties)
            }.build()

            for (unit in units) {
                addProperty(unit.getExtension(this@Dimension))
            }
            addType(dimensionClass)
        }.build()
    }

    /**
     * Equality for dimensions is defined simply by the equality of the name. If more units are required, they should
     * be added to the first dimension instead.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dimension

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name
    }
}
