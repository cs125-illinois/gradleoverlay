package edu.illinois.cs.cs125.gradleoverlay

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.tools.ant.DirectoryScanner
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File

@Suppress("UNUSED")
class Plugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.create("overlay", Task::class.java)
    }
}

data class Overlay(
    val overwrite: List<String>,
    val merge: List<String> = listOf(),
    val delete: List<String> = listOf(),
    val checkpoints: Map<String, Checkpoint> = mapOf()
) {
    data class Checkpoint(
        val overwrite: List<String> = listOf(),
        val merge: List<String> = listOf(),
        val delete: List<String> = listOf()
    )
}

data class CheckpointConfig(val checkpoint: Int)

open class Task : DefaultTask() {

    @TaskAction
    fun run() {
        val mapper = ObjectMapper(YAMLFactory()).also { it.registerModule(KotlinModule()) }

        val config = mapper.readValue<Overlay>(project.file("config/overlay.yaml"))
        val overlayConfig = if (config.checkpoints.keys.isNotEmpty()) {
            mapper.readValue<CheckpointConfig>(project.file("grade.yaml")).checkpoint.let {
                config.checkpoints[it.toString()]
            }
        } else {
            null
        }

        require(project.hasProperty("overlayfrom")) {
            "Specify overlay source with -Poverlayfrom"
        }

        val studentRoot = project.rootProject.file(File(project.property("overlayfrom") as String))
        val targetRoot = project.projectDir
        println("Overlaying from $studentRoot to $targetRoot")

        (config.delete + config.overwrite).rm(targetRoot)
        if (overlayConfig != null) {
            (overlayConfig.delete + overlayConfig.overwrite).rm(targetRoot)
        }
        (config.overwrite + config.merge).cp(studentRoot, targetRoot)
        if (overlayConfig != null) {
            (overlayConfig.overwrite + overlayConfig.merge).cp(studentRoot, targetRoot)
        }
    }

    private fun Collection<String>.cp(studentRoot: File, testRoot: File) {
        DirectoryScanner().also { scanner ->
            scanner.setIncludes(toTypedArray())
            scanner.basedir = studentRoot
            scanner.scan()
            scanner.includedFiles.forEach { File(studentRoot, it).copyTo(File(testRoot, it), true) }
        }
    }

    private fun Collection<String>.rm(root: File) {
        DirectoryScanner().also { scanner ->
            scanner.setIncludes(toTypedArray())
            scanner.basedir = root
            scanner.scan()
            scanner.includedFiles.forEach { File(root, it).delete() }
        }
    }
}
