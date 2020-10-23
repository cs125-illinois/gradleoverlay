package edu.illinois.cs.cs125.gradleoverlay

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("UNUSED")
class Plugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.create("overlay", Task::class.java)
    }
}
