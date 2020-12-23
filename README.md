# tape

Tape is a Gradle plugin that generates units so that your code's safety can be evaluated by the compiler and Java /
Kotlin's type system.

To add the plugin to a project, add it to the `plugins` block

```kotlin
plugins {
    id("me.akainth.tape") version "1.2.0"
}
```

Tape will generate extensions that need to be added to your source sets

~~~kotlin
sourceSets["main"].java.srcDir(tasks.getByName("tape", GenerateDimensionsTask::class).targetDirectory)
~~~

Tape will not generate anything out of the box, you must first select which dimensions to generate.

```kotlin
tasks.getByName("tape", GenerateDimensionsTask::class) {
    length
    time
    speed
    acceleration
    mass
    force
    area
    volume
}
```

Tape also makes it easy to create your own dimensions, such as for current and voltage

```kotlin
val charge = dimension("ElectricCharge", "coulomb") {}
val current = dimension("Current", charge / time)
```

To configure the output from tape, you can configure the following members of `GenerateDimensionsTask`
~~~kotlin
tasks.getByName("tape", GenerateDimensionsTask::class) {
    targetDirectory = buildDir // buildDir.resolve("generated/") by default
    useExperimentalInline = false // true by default
}
~~~