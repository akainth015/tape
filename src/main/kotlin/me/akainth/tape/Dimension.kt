@file:Suppress("MemberVisibilityCanBePrivate")

package me.akainth.tape

import com.squareup.kotlinpoet.*
import java.io.Serializable

@Suppress("unused")
open class Dimension(val name: String, val base: String) : Serializable {
    val aliases = mutableListOf<String>()
    val units = mutableListOf<Unit>()

    fun alias(name: String) {
        aliases += name
    }

    fun unit(name: String, ratioToBase: Number): Unit {
        val unit = Unit(name, ratioToBase.toDouble())
        units += unit
        return unit
    }

    fun yotta() {
        unit("yotta$base", 1e24).apply {
            singular = "yotta${baseUnit.singular}"
        }
    }

    fun zetta() {
        unit("zetta$base", 1e21).apply {
            singular = "zetta${baseUnit.singular}"
        }
    }

    fun exa() {
        unit("exa$base", 1e18).apply {
            singular = "exa${baseUnit.singular}"
        }
    }

    fun peta() {
        unit("peta$base", 1e15).apply {
            singular = "peta${baseUnit.singular}"
        }
    }

    fun tera() {
        unit("tera$base", 1e12).apply {
            singular = "tera${baseUnit.singular}"
        }
    }

    fun giga() {
        unit("giga$base", 1e9).apply {
            singular = "giga${baseUnit.singular}"
        }
    }

    fun mega() {
        unit("mega$base", 1e6).apply {
            singular = "mega${baseUnit.singular}"
        }
    }

    fun kilo() {
        unit("kilo$base", 1e3).apply {
            singular = "kilo${baseUnit.singular}"
        }
    }

    fun hecto() {
        unit("hecto$base", 1e3).apply {
            singular = "hecto${baseUnit.singular}"
        }
    }

    fun deca() {
        unit("deca$base", 10).apply {
            singular = "deca${baseUnit.singular}"
        }
    }

    fun deci() {
        unit("deci$base", 1e-1).apply {
            singular = "deci${baseUnit.singular}"
        }
    }

    fun centi() {
        unit("centi$base", 1e-2).apply {
            singular = "centi${baseUnit.singular}"
        }
    }

    fun milli() {
        unit("milli$base", 1e-3).apply {
            singular = "milli${baseUnit.singular}"
        }
    }

    fun micro() {
        unit("μ$base", 1e-6)
        unit("micro$base", 1e-6).apply {
            singular = "micro${baseUnit.singular}"
        }
    }

    fun nano() {
        unit("nano$base", 1e-9).apply {
            singular = "nano${baseUnit.singular}"
        }
    }

    fun pico() {
        unit("pico$base", 1e-12).apply {
            singular = "pico${baseUnit.singular}"
        }
    }

    fun femto() {
        unit("femto$base", 1e-15).apply {
            singular = "femto${baseUnit.singular}"
        }
    }

    fun atto() {
        unit("atto$base", 1e-18).apply {
            singular = "atto${baseUnit.singular}"
        }
    }

    fun zepto() {
        unit("zepto$base", 1e-21).apply {
            singular = "zepto${baseUnit.singular}"
        }
    }

    fun yocto() {
        unit("yocto$base", 1e-24).apply {
            singular = "yocto${baseUnit.singular}"
        }
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

    open fun generateFile(): FileSpec {
        val primaryConstructor = FunSpec.constructorBuilder()
            .addParameter(base, DOUBLE)
            .build()
        val dimension = TypeSpec.classBuilder(name)
            .primaryConstructor(primaryConstructor)
            .addProperty(PropertySpec.builder(base, DOUBLE).initializer(base).build())

        dimension.addModifiers(KModifier.VALUE)
        dimension.addAnnotation(ClassName("kotlin.jvm", "JvmInline"))

        // Generate utility values to convert from the base unit to the desired unit
        dimension.addProperty(baseUnit.generateProperty(this))
        units.forEach { unit ->
            dimension.addProperty(unit.generateProperty(this))
        }
        generateProperties(dimension)

        val file = FileSpec.builder("me.akainth.tape.dimensions", name)
            .addType(dimension.build())

        // Generate the extension properties to convert from a number to the base unit
        file.addProperty(baseUnit.generateExtension(this))
        units.forEach { unit ->
            file.addProperty(unit.generateExtension(this))
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

        aliases.map {
            TypeAliasSpec.builder(it, getClassName()).build()
        }.forEach(file::addTypeAlias)

        return file.build()
    }

    @Suppress("LeakingThis")
    val baseUnit = Unit(base, 1.0)

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
        return "$name {${aliases.joinToString()}} ($base) {${units.joinToString()}}"
    }
}

data class QuotientDimension(val top: Dimension, val bottom: Dimension)

data class ProductDimension(val first: Dimension, val second: Dimension)
