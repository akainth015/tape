/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package me.akainth.tape

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test

/**
 * A simple unit test for the 'me.akainth.tape.greeting' plugin.
 */
class TapePluginTest {
    @Test fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("me.akainth.tape")

        // Verify the result

    }
}
