plugins {
    id("com.gradle.plugin-publish") version "1.1.0"

    // Apply the Kotlin JVM plugin to add support for Kotlin.
    kotlin("jvm") version "1.8.10"

    idea
}

group = "me.akainth"
version = "2.1.1"

gradlePlugin {
    website.set("https://github.com/akainth015/tape")
    vcsUrl.set("https://github.com/akainth015/tape")

    // Define the plugin
    plugins {
        create("tape") {
            id = "me.akainth.tape"
            implementationClass = "me.akainth.tape.TapePlugin"
            displayName = "Tape"
            description = "A type-safe units generator"
            tags.set(listOf("tape", "units", "dimension", "kotlin"))
        }
    }
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    mavenCentral()
    maven("https://jitpack.io")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    // Used to generate the Kotlin classes
    implementation("com.squareup:kotlinpoet:1.12.0")

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
        testSources.from(allJava.srcDirs)
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
