@file:Suppress("MemberVisibilityCanBePrivate")

package me.akainth.tape

import com.squareup.kotlinpoet.*
import java.io.Serializable

@Suppress("unused")
open class Dimension(val name: String, val base: String) : Serializable {
    val units = mutableListOf<Unit>()

    fun yotta() {
        unit("yotta$base", 1e24)
    }

    fun zetta() {
        unit("zetta$base", 1e21)
    }

    fun exa() {
        unit("exa$base", 1e18)
    }

    fun peta() {
        unit("peta$base", 1e15)
    }

    fun tera() {
        unit("tera$base", 1e12)
    }

    fun giga() {
        unit("giga$base", 1e9)
    }

    fun mega() {
        unit("mega$base", 1e6)
    }

    fun kilo() {
        unit("kilo$base", 1e3)
    }

    fun hecto() {
        unit("hecto$base", 1e3)
    }

    fun deca() {
        unit("deca$base", 10)
    }

    fun deci() {
        unit("deci$base", 1e-1)
    }

    fun centi() {
        unit("centi$base", 1e-2)
    }

    fun milli() {
        unit("milli$base", 1e-3)
    }

    fun micro() {
        unit("μ$base", 1e-6)
        unit("micro$base", 1e-6)
    }

    fun nano() {
        unit("nano$base", 1e-9)
    }

    fun pico() {
        unit("pico$base", 1e-12)
    }

    fun femto() {
        unit("femto$base", 1e-15)
    }

    fun atto() {
        unit("atto$base", 1e-18)
    }

    fun zepto() {
        unit("zepto$base", 1e-21)
    }

    fun yocto() {
        unit("yocto$base", 1e-24)
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

    fun getClassName() = ClassName("me.akainth.tape.dimensions", name)

    open fun generateFile(useExperimentalInline: Boolean): FileSpec {
        val primaryConstructor = FunSpec.constructorBuilder()
                .addParameter(base, DOUBLE)
                .build()
        val dimension = TypeSpec.classBuilder(name)
                .primaryConstructor(primaryConstructor)
                .addProperty(PropertySpec.builder(base, DOUBLE).initializer(base).build())

        if (useExperimentalInline) {
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

        val plusFunc = FunSpec.builder("plus")
            .addModifiers(KModifier.OPERATOR)
            .receiver(getClassName())
            .addParameter("other", getClassName())
            .returns(getClassName())
            .addCode("return $name($base + other.$base)")
            .build()
        val minusFunc = FunSpec.builder("minus")
            .addModifiers(KModifier.OPERATOR)
            .receiver(getClassName())
            .addParameter("other", getClassName())
            .returns(getClassName())
            .addCode("return $name($base - other.$base)")
            .build()
        file.addFunction(plusFunc)
        file.addFunction(minusFunc)

        return file.build()
    }

    @Suppress("LeakingThis")
    private val baseUnit = Unit(base, 1.0, this)

    open fun generateExtensions(file: FileSpec.Builder) {}

    open fun generateProperties(dimension: TypeSpec.Builder) {}

    operator fun div(other: Dimension): QuotientDimension {
        return QuotientDimension(this, other)
    }

    fun multiply(other: Dimension): ProductDimension {
        return ProductDimension(this, other)
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
