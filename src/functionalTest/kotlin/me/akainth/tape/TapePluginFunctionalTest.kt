package me.akainth.tape

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import kotlin.test.Test

class TapePluginFunctionalTest {
    @get:Rule
    val projectDirectory = TemporaryFolder()

    @Test
    fun `generates default dimensions`() {
        // Set up the test build
        projectDirectory.newFile("settings.gradle.kts").createNewFile()
        projectDirectory.newFile("build.gradle.kts")
            .writeText("""
                plugins {
                    id("me.akainth.tape")
                    application
                    kotlin("jvm") version "1.6.10" 
                }
                
                repositories {
                    mavenCentral()
                }
                
                application {
                    mainClass.set("MainKt")
                }
                
                tape {
                    length
                    time
                    speed
                }
                
                tasks["compileKotlin"].dependsOn(tasks["tape"])
                sourceSets["main"].java.srcDir(tape.targetDirectory.map {it.asFile}.get())
            """.trimIndent())

        val srcFolder = projectDirectory.newFolder("src", "main", "kotlin")
        srcFolder.resolve("main.kt").writeText("""
            import me.akainth.tape.dimensions.ft
            import me.akainth.tape.dimensions.s
            import me.akainth.tape.dimensions.div
            import me.akainth.tape.dimensions.ftPerS
            import me.akainth.tape.dimensions.Distance
            
            fun main() {
                val x = 1.ft
                val t = 10.s
                val speed = x / t
                assert(speed == 0.1.ftPerS)
                println("SUCCESS")
            }
        """.trimIndent())


        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("run")
        runner.withProjectDir(projectDirectory.root)
        val result = runner.build()

        assert(result.output.contains("SUCCESS"))
    }

    @Test
    fun `generates dimension aliases`() {
        // Set up the test build
        projectDirectory.newFile("settings.gradle.kts").createNewFile()
        projectDirectory.newFile("build.gradle.kts")
            .writeText("""
                plugins {
                    id("me.akainth.tape")
                    application
                    kotlin("jvm") version "1.6.10" 
                }
                
                repositories {
                    mavenCentral()
                }
                
                application {
                    mainClass.set("MainKt")
                }
                
                tape {
                    length
                }
                
                tasks["compileKotlin"].dependsOn(tasks["tape"])
                sourceSets["main"].java.srcDir(tape.targetDirectory.map {it.asFile}.get())
            """.trimIndent())

        val srcFolder = projectDirectory.newFolder("src", "main", "kotlin")
        srcFolder.resolve("main.kt").writeText("""
            import me.akainth.tape.dimensions.ft
            import me.akainth.tape.dimensions.Distance
            import me.akainth.tape.dimensions.Length
            import me.akainth.tape.dimensions.minus
            
            fun main() {
                val target: Distance = 10.ft
                val delta: Length = 10.ft
                assert(target - delta == 0.ft)
                println("SUCCESS")
            }
        """.trimIndent())


        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("run")
        runner.withProjectDir(projectDirectory.root)
        val result = runner.build()

        assert(result.output.contains("SUCCESS"))
    }
}
