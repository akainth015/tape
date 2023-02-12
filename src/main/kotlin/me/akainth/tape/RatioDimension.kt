package me.akainth.tape

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OPERATOR
import com.squareup.kotlinpoet.TypeSpec
import java.util.*

class RatioDimension(name: String, val top: Dimension, val bottom: Dimension)
    : Dimension(name,
    "${top.base}Per${bottom.base.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}") {
    init {
        top.units.forEach { topUnit ->
            unit("${topUnit.name}Per${bottom.base.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}", topUnit.ratioToBase)
            bottom.units.forEach { bottomUnit ->
                unit("${topUnit.name}Per${bottomUnit.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}", topUnit.ratioToBase / bottomUnit.ratioToBase)
            }
        }
    }

    override fun generateProperties(dimension: TypeSpec.Builder) {
        val timesFun = FunSpec.builder("times")
                .addModifiers(OPERATOR)
                .addParameter("other", bottom.getClassName())
                .returns(top.getClassName())
                .addCode("return ${top.name}($base * other.${bottom.base})")
                .build()
        dimension.addFunction(timesFun)
    }

    override fun generateExtensions(file: FileSpec.Builder) {
        val divExtFun = FunSpec.builder("div")
                .addModifiers(OPERATOR)
                .receiver(top.getClassName())
                .addParameter("other", bottom.getClassName())
                .returns(getClassName())
                .addCode("return $name(${top.base} / other.${bottom.base})")
                .build()
        file.addFunction(divExtFun)
    }
}