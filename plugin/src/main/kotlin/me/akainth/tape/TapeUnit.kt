package me.akainth.tape

import com.squareup.kotlinpoet.DOUBLE
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.NUMBER
import com.squareup.kotlinpoet.PropertySpec
import org.gradle.api.Named
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

abstract class TapeUnit @Inject constructor() : Named {
    @get:Input
    abstract val ratioToBase: Property<Double>

    @get:Input
    abstract val singularName: Property<String>

    init {
        singularName.convention(name)
    }

    fun getProperty(dimension: Dimension): PropertySpec {
        return PropertySpec.builder(name, DOUBLE).apply {
            val getterFunction = FunSpec.getterBuilder().apply {
                addCode("return ${dimension.baseUnit} / ${ratioToBase.get()}")
            }.build()

            getter(getterFunction)
        }.build()
    }

    fun getExtension(dimension: Dimension): PropertySpec {
        return PropertySpec.builder(name, dimension.className).apply {
            receiver(NUMBER)
            val getterFunction = FunSpec.getterBuilder().apply {
                addCode("return ${dimension.name}(toDouble() * ${ratioToBase.get()})")
            }.build()

            getter(getterFunction)
        }.build()
    }

    override fun toString(): String {
        return name
    }
}