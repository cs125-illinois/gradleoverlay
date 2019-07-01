package edu.illinois.cs.cs125.gradleoverlay

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("UNUSED")
class OverlayPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.create("overlay", OverlayTask::class.java)
    }

}