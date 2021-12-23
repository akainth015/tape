plugins {
    // Apply the Java Gradle plugin development plugin to add support for developing Gradle plugins
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.12.0"

    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.6.10"

    idea
}

gradlePlugin {
    // Define the plugin
    plugins {
        create("tape") {
            id = "me.akainth.tape"
            version = "2.0.0"
            implementationClass = "me.akainth.tape.TapePlugin"
        }
    }
}

pluginBundle {
    website = "https://akainth.me"
    vcsUrl = "https://github.com/akainth015/tape"

    description = "A type-safe units generator"
    (plugins) {
        "tape" {
            displayName = "Tape"
            tags = listOf("tape", "units", "dimension", "kotlin")
        }
    }
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    // Used to generate the Kotlin classes
    @Suppress("SpellCheckingInspection")
    implementation("com.squareup:kotlinpoet:1.10.2")

    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

// Add a source set for the functional test suite
val functionalTest: SourceSet by sourceSets.creating {
    idea.module {
        testSourceDirs.plusAssign(allJava.srcDirs)
    }
}
gradlePlugin.testSourceSets(functionalTest)

val testConfiguration = configurations.testImplementation.get()
configurations[functionalTest.implementationConfigurationName]
    .extendsFrom(testConfiguration)

// Add a task to run the functional tests
val functionalTestTask by tasks.creating(Test::class) {
    testClassesDirs = functionalTest.output.classesDirs
    classpath = configurations[functionalTest.runtimeClasspathConfigurationName] + functionalTest.output
}

val check by tasks.getting(Task::class) {
    // Run the functional tests as part of `check`
    dependsOn(functionalTestTask)
}
