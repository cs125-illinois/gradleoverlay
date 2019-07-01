package edu.illinois.cs.cs125.gradleoverlay

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class OverlayTask : DefaultTask() {

    @TaskAction
    fun run() {
        val configLoader = ObjectMapper(YAMLFactory()).also { it.registerModule(KotlinModule()) }
        val config = configLoader.readValue<OverlayConfig>(project.file("config/overlay.yaml"))
        val studentRoot = if (project.hasProperty("overlayfrom")) {
            File(project.property("overlayfrom") as String)
        } else {
            project.rootProject.childProjects["student"]!!.projectDir
        }
        println("Overlaying from $studentRoot")
        // TODO
    }

}