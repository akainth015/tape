package me.akainth.tape

import com.squareup.kotlinpoet.*

class TimesDimension(name: String, val a: Dimension, val b: Dimension) : Dimension(name, "${a.base}${b.base.capitalize()}") {
    init {
        a.units.forEach { aUnit ->
            unit("${aUnit.name}${b.base}", aUnit.ratioToBase)
            b.units.forEach { bUnit ->
                unit("${aUnit.name}${bUnit.name.capitalize()}", aUnit.ratioToBase * bUnit.ratioToBase)
            }
        }
        for (unit in b.units) {
            unit("${a.base}${unit.name}", unit.ratioToBase)
        }
    }

    override fun generateExtensions(file: FileSpec.Builder) {
        val timesExtAFun = FunSpec.builder("times")
            .addModifiers(KModifier.OPERATOR)
            .receiver(a.getClassName())
            .addParameter("other", b.getClassName())
            .returns(getClassName())
            .addCode("return $name(${a.base} * other.${b.base})")
            .build()
        val timesExtBFun = FunSpec.builder("times")
            .addModifiers(KModifier.OPERATOR)
            .receiver(b.getClassName())
            .addParameter("other", a.getClassName())
            .returns(getClassName())
            .addCode("return $name(${b.base} * other.${a.base})")
            .build()
        file.addFunction(timesExtAFun)
        if (a.name != b.name) {
            file.addFunction(timesExtBFun)
        }
    }

    override fun generateProperties(dimension: TypeSpec.Builder) {
        val divByBFun = FunSpec.builder("div")
            .addModifiers(KModifier.OPERATOR)
            .addParameter("other", b.getClassName())
            .returns(a.getClassName())
            .addCode("return ${a.name}($base / other.${b.base})")
            .build()
        val divByAFun = FunSpec.builder("div")
            .addModifiers(KModifier.OPERATOR)
            .addParameter("other", a.getClassName())
            .returns(b.getClassName())
            .addCode("return ${b.name}($base / other.${a.base})")
            .build()
        dimension.addFunction(divByBFun)
        if (a.name != b.name) {
            dimension.addFunction(divByAFun)
        }
    }
}
