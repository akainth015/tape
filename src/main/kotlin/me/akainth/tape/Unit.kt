package me.akainth.tape

import com.squareup.kotlinpoet.*
import java.io.Serializable

class Unit(val name: String, val ratioToBase: Double, val dimension: Dimension): Serializable {
    fun generateExtension(): PropertySpec {
        val type = ClassName("me.akainth.tape.dimensions", dimension.name)
        val getter = FunSpec.getterBuilder()
                .addCode("return ${dimension.name}(toDouble() / ${ratioToBase})")
                .build()
        return PropertySpec.builder(name, type)
                .receiver(NUMBER)
                .getter(getter)
                .build()
    }

    fun generateProperty(): PropertySpec {
        val getter = FunSpec.getterBuilder()
                .addCode("return `${dimension.base}` * $ratioToBase")
                .build()
        return PropertySpec.builder(name, DOUBLE)
                .getter(getter)
                .build()
    }

    override fun toString(): String {
        return "$name ($ratioToBase)"
    }
}