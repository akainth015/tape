package me.akainth.tape

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OPERATOR
import com.squareup.kotlinpoet.TypeSpec

class RatioDimension(name: String, val top: Dimension, val bottom: Dimension, inline: Boolean = true)
    : Dimension(name, "${top.base}Per${bottom.base.capitalize()}", inline) {
    init {
        top.units.forEach { topUnit ->
            bottom.units.forEach { bottomUnit ->
                unit("${topUnit.name}Per${bottomUnit.name.capitalize()}", topUnit.ratioToBase / bottomUnit.ratioToBase)
            }
        }
    }

    override fun generateProperties(dimension: TypeSpec.Builder) {
        val timesFun = FunSpec.builder("times")
                .addModifiers(OPERATOR)
                .addParameter("other", ClassName("me.akainth.tape.dimensions", bottom.name))
                .returns(ClassName("me.akainth.tape.dimensions", top.name))
                .addCode("return ${top.name}($base * other.${bottom.base})")
                .build()
        dimension.addFunction(timesFun)
    }

    override fun generateExtensions(file: FileSpec.Builder) {
        val divExtFun = FunSpec.builder("div")
                .addModifiers(OPERATOR)
                .receiver(ClassName("me.akainth.tape.dimensions", top.name))
                .addParameter("other", ClassName("me.akainth.tape.dimensions", bottom.name))
                .returns(ClassName("me.akainth.tape.dimensions", name))
                .addCode("return $name(${top.base} / other.${bottom.base})")
                .build()
        file.addFunction(divExtFun)
    }
}