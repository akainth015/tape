# tape

Tape is a Gradle plugin that generates units so that your code's safety can be evaluated by the compiler and Java /
Kotlin's type system. It aims to provide a simple, and minimal interface for this task. If more extensive support and generation is required, it is recommended that you take a look at [kunalsheth/units-of-measure](https://github.com/kunalsheth/units-of-measure) instead.

To add the plugin to a project, add it to the `plugins` block

```kotlin
plugins {
    id("me.akainth.tape") version "2.1.0"
}
```

Tape will generate extensions that need to be added to your source sets. This should go _after_ the `tape` block in your buildscript.

~~~kotlin
tasks["compileKotlin"].dependsOn(tasks["tape"])
sourceSets["main"].java.srcDir(tape.targetDirectory.map {it.asFile}.get())
~~~

Tape will not generate anything out of the box, you must first select which dimensions to generate.

```kotlin
tape {    
    length
    time
    mass
    bytes
    area
    volume
    speed
    acceleration
    force
}
```

Tape also makes it easy to create your own dimensions, such as for current and voltage

```kotlin
val charge = dimension("ElectricCharge", "coulomb") {}
val current = dimension("Current", charge / time)
```

To configure the output from tape, you can configure the following members of `GenerateDimensionsTask`
~~~kotlin
tape {
    targetDirectory.set(project.layout.buildDirectory.dir("generated"))
}
~~~
