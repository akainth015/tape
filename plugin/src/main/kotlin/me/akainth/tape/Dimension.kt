package me.akainth.tape

import com.squareup.kotlinpoet.*
import nl.devhaan.kotlinpoetdsl.packaged
import java.io.Serializable

@Suppress("unused")
open class Dimension(val name: String, val base: String, val inline: Boolean = true) : Serializable {
    val units = mutableListOf<Unit>()

    fun yotta() {
        unit("yotta$base", 1e24)
    }

    fun zetta() {
        unit("zetta$base", 1e21)
    }

    fun exa() {
        unit("E$base", 1e18)
    }

    fun peta() {
        unit("peta$base", 1e15)
    }

    fun tera() {
        unit("T$base", 1e12)
    }

    fun giga() {
        unit("G$base", 1e9)
    }

    fun mega() {
        unit("mega$base", 1e6)
    }

    fun kilo() {
        unit("k$base", 1e3)
    }

    fun hecto() {
        unit("h$base", 1e3)
    }

    fun deca() {
        unit("da$base", 10)
    }

    fun deci() {
        unit("d$base", 1e-1)
    }

    fun centi() {
        unit("c$base", 1e-2)
    }

    fun milli() {
        unit("m$base", 1e-3)
    }

    fun micro() {
        unit("μ$base", 1e-6)
        unit("micro$base", 1e-6)
    }

    fun nano() {
        unit("n$base", 1e-9)
    }

    fun pico() {
        unit("p$base", 1e-12)
    }

    fun femto() {
        unit("f$base", 1e-15)
    }

    fun atto() {
        unit("a$base", 1e-18)
    }

    fun zepto() {
        unit("z$base", 1e-21)
    }

    fun yocto() {
        unit("y$base", 1e-24)
    }

    fun unit(name: String, ratioToBase: Number) {
        units += Unit(name, ratioToBase.toDouble(), this)
    }

    fun commonMetricVariants() {
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
        val ratioToBaseParameter = ParameterSpec.builder("ratioToBase", DOUBLE)
            .defaultValue("%L", 1.0)
            .build()

        val unitConstructor = FunSpec.constructorBuilder()
            .addParameter(ratioToBaseParameter)
            .build()

        val unitsEnum = TypeSpec.enumBuilder("${name}Units")
            .primaryConstructor(unitConstructor)
            .addEnumConstant(base)
            .apply {
                units.forEach { unit ->
                    addEnumConstant(
                        unit.name,
                        TypeSpec.anonymousClassBuilder()
                            .addSuperclassConstructorParameter("%L", unit.ratioToBase)
                            .build()
                    )
                }
            }
            .build()

        val unitsEnumType = unitsEnum.packaged("me.akainth.tape.dimensions")
        val parameter = ParameterSpec.builder("unit", unitsEnumType)
            .defaultValue("${name}Units.$base")
            .build()

        val property = PropertySpec.builder("unit", unitsEnumType)
            .initializer("unit")
            .build()

        val primaryConstructor = FunSpec.constructorBuilder()
            .addParameter(parameter)
            .build()

        val annotationClass = TypeSpec.annotationBuilder(name)
            .primaryConstructor(primaryConstructor)
            .addProperty(property)
            .build()

        val file = FileSpec.builder("me.akainth.tape.dimensions", name)
            .addType(annotationClass)
            .addType(unitsEnum)

        return file.build()
    }

    private val baseUnit = Unit(base, 1.0, this)

    open fun generateExtensions(file: FileSpec.Builder) {}

    open fun generateProperties(dimension: TypeSpec.Builder) {}

    override fun toString(): String {
        return "$name ($base) {${units.joinToString()}}"
    }
}