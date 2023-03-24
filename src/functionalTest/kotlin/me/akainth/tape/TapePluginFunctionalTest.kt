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
            import me.akainth.tape.dimensions.feet
            import me.akainth.tape.dimensions.seconds
            import me.akainth.tape.dimensions.div
            import me.akainth.tape.dimensions.feetPerSecond
            import me.akainth.tape.dimensions.Distance
            
            fun main() {
                val x = 1.feet
                val t = 10.seconds
                val speed = x / t
                assert(speed == 0.1.feetPerSecond)
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
            import me.akainth.tape.dimensions.feet
            import me.akainth.tape.dimensions.Distance
            import me.akainth.tape.dimensions.Length
            import me.akainth.tape.dimensions.minus
            
            fun main() {
                val target: Distance = 10.feet
                val delta: Length = 10.feet
                assert(target - delta == 0.feet)
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
    fun `length conversions are correct`() {
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
            import me.akainth.tape.dimensions.*
            
            fun main() {
                assert(3.28084.meters == 1.feet)
                assert(12.inches == 1.feet)
                assert(100.centimeters == 1.meters)
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
    fun `time conversions are correct`() {
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
                    time
                }
                
                tasks["compileKotlin"].dependsOn(tasks["tape"])
                sourceSets["main"].java.srcDir(tape.targetDirectory.map {it.asFile}.get())
            """.trimIndent())

        val srcFolder = projectDirectory.newFolder("src", "main", "kotlin")
        srcFolder.resolve("main.kt").writeText("""
            import me.akainth.tape.dimensions.*
            
            fun main() {
                assert(1.minutes == 60.seconds)
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
    fun `mass conversions are correct`() {
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
                    mass
                }
                
                tasks["compileKotlin"].dependsOn(tasks["tape"])
                sourceSets["main"].java.srcDir(tape.targetDirectory.map {it.asFile}.get())
            """.trimIndent())

        val srcFolder = projectDirectory.newFolder("src", "main", "kotlin")
        srcFolder.resolve("main.kt").writeText("""
            import me.akainth.tape.dimensions.*
            
            fun main() {
                assert(1.kilograms == 2.204623.pounds)
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
    fun `square dimension calculations are correct`() {
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
                    area
                }
                
                tasks["compileKotlin"].dependsOn(tasks["tape"])
                sourceSets["main"].java.srcDir(tape.targetDirectory.map {it.asFile}.get())
            """.trimIndent())

        val srcFolder = projectDirectory.newFolder("src", "main", "kotlin")
        srcFolder.resolve("main.kt").writeText("""
            import me.akainth.tape.dimensions.*
            
            fun main() {
                assert(1.metersMeters == (3.28084 * 3.28084).feetFeet)
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
