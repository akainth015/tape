package me.akainth.tape

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

class TapePluginTest {
    @Test fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("me.akainth.tape")

        // Verify the result
        val tapeTask = project.tasks.findByName("tape")
        assertNotNull(tapeTask)
    }

    @Test fun `plugin registers extension`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("me.akainth.tape")

        // Verify the result
        val tapeExtension = project.extensions.findByName("tape")
        assertNotNull(tapeExtension)
    }
}
