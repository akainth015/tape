package me.akainth.tape

import org.gradle.testkit.runner.GradleRunner
import java.io.File
import kotlin.test.Test

class TapePluginFunctionalTest {
    @Test
    fun `can run task`() {
        // Setup the test build
        val projectDir = File("build/functionalTest")
        projectDir.mkdirs()
        projectDir.resolve("settings.gradle").writeText("")
        projectDir.resolve("build.gradle")
            .writeText(
                """
        plugins {
            id("me.akainth.tape")
            id "application"

            id "org.jetbrains.kotlin.jvm" version "1.4.21"
        }

        repositories {
            jcenter()
        }

        dependencies {
            implementation(group: 'org.jetbrains.kotlin', name: 'kotlin-stdlib-jdk8', version: '1.4.21')
        }

        sourceSets.main.kotlin.srcDirs += tape.targetDirectory

        tape {
            def length = dimension("Length", "meters") {
                unit("feet", 0.3048)
                unit("inches", 0.0254)
                addMetricUnits()
            }
            def time = dimension("Time", "seconds") {
                milli()
                nano()
            }
            def mass = dimension("Mass", "grams") {
                addMetricUnits()
            }
            def speed = dimension("Speed", length / time)
            def acceleration = dimension("Acceleration", speed / time)
            dimension("Force", acceleration * mass)
            dimension("Area", length * length)
        }

        application {
            mainClassName = "MainKt"
        }

        compileKotlin.dependsOn(tape)
        """.trimIndent()
            )
        projectDir.resolve("src/main/kotlin/main.kt")
            .writeText(
                """
            import me.akainth.tape.dimensions.feet
            import me.akainth.tape.dimensions.seconds
            import me.akainth.tape.dimensions.div
            import me.akainth.tape.dimensions.feetPerSeconds
            
            fun main() {
                val x = 1.feet
                val t = 10.seconds
                val speed = x / t
                println("speed = ${'$'}speed")
                println("fps = ${'$'}{speed.feetPerSeconds}")
            }
        """.trimIndent()
            )

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("run")
        runner.withProjectDir(projectDir)
        runner.build()
    }
}
