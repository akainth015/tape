package me.akainth.tape

import com.squareup.kotlinpoet.*
import java.io.Serializable

@Suppress("unused")
open class Dimension(val name: String, val base: String, val inline: Boolean = true) : Serializable {
    val units = mutableListOf<Unit>()

    fun yotta() {
        unit("yotta$base", 10e24)
    }

    fun zetta() {
        unit("zetta$base", 10e21)
    }

    fun exa() {
        unit("E$base", 10e18)
    }

    fun peta() {
        unit("peta$base", 10e15)
    }

    fun tera() {
        unit("T$base", 10e12)
    }

    fun giga() {
        unit("G$base", 10e9)
    }

    fun mega() {
        unit("mega$base", 10e6)
    }

    fun kilo() {
        unit("k$base", 10e3)
    }

    fun hecto() {
        unit("h$base", 10e3)
    }

    fun deca() {
        unit("da$base", 10)
    }

    fun deci() {
        unit("d$base", 10e-1)
    }

    fun centi() {
        unit("c$base", 10e-2)
    }

    fun milli() {
        unit("m$base", 10e-3)
    }

    fun micro() {
        unit("μ$base", 10e-6)
        unit("micro$base", 10e-6)
    }

    fun nano() {
        unit("n$base", 10e-9)
    }

    fun pico() {
        unit("p$base", 10e-12)
    }

    fun femto() {
        unit("f$base", 10e-15)
    }

    fun atto() {
        unit("a$base", 10e-18)
    }

    fun zepto() {
        unit("z$base", 10e-21)
    }

    fun yocto() {
        unit("y$base", 10e-24)
    }

    fun unit(name: String, ratioToBase: Number) {
        units += Unit(name, ratioToBase.toDouble(), this)
    }

    fun addMetricUnits() {
        giga()
        mega()
        kilo()
        deci()
        centi()
        milli()
        micro()
        nano()
    }

    fun addAllMetricUnits() {
        yotta()
        zetta()
        exa()
        peta()
        tera()
        giga()
        mega()
        kilo()
        hecto()
        deca()
        deci()
        centi()
        milli()
        micro()
        nano()
        pico()
        femto()
        atto()
        zepto()
        yocto()
    }

    open fun generateFile(): FileSpec {
        val primaryConstructor = FunSpec.constructorBuilder()
                .addParameter(base, DOUBLE)
                .build()
        val dimension = TypeSpec.classBuilder(name)
                .primaryConstructor(primaryConstructor)
                .addProperty(PropertySpec.builder(base, DOUBLE).initializer(base).build())

        if (inline) {
            dimension.addModifiers(KModifier.INLINE)
        }

        // Generate utility values to convert from the base unit to the desired unit
        dimension.addProperty(baseUnit.generateProperty())
        units.forEach { unit ->
            dimension.addProperty(unit.generateProperty())
        }
        generateProperties(dimension)

        val file = FileSpec.builder("me.akainth.tape.dimensions", name)
                .addType(dimension.build())

        // Generate the extension properties to convert from a number to the base unit
        file.addProperty(baseUnit.generateExtension())
        units.forEach { unit ->
            file.addProperty(unit.generateExtension())
        }
        generateExtensions(file)

        return file.build()
    }

    private val baseUnit = Unit(base, 1.0, this)

    open fun generateExtensions(file: FileSpec.Builder) {}

    open fun generateProperties(dimension: TypeSpec.Builder) {}

    operator fun div(other: Dimension): QuotientDimension {
        return QuotientDimension(this, other)
    }

    operator fun times(other: Dimension): ProductDimension {
        return ProductDimension(this, other)
    }

    override fun toString(): String {
        return "$name ($base) {${units.joinToString()}}"
    }
}

data class QuotientDimension(val top: Dimension, val bottom: Dimension)

data class ProductDimension(val first: Dimension, val second: Dimension)