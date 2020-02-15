package edu.illinois.cs.cs125.gradleoverlay

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.tools.ant.DirectoryScanner
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class OverlayTask : DefaultTask() {

    @TaskAction
    fun run() {
        val configLoader = ObjectMapper(YAMLFactory()).also { it.registerModule(KotlinModule()) }
        val config = configLoader.readValue<OverlayConfig>(project.file("config/overlay.yaml"))
        val studentRoot = if (project.hasProperty("overlayfrom")) {
            project.rootProject.file(File(project.property("overlayfrom") as String))
        } else if (project.rootProject.childProjects.containsKey("student")) {
            project.rootProject.childProjects["student"]!!.projectDir // For testing
        } else {
            error("Specify overlay source with -Poverlayfrom")
        }
        val targetRoot = project.projectDir
        println("Overlaying from $studentRoot to $targetRoot")

        deleteFiles(config.delete + config.overwrite, targetRoot)
        copyFiles(config.overwrite + config.merge, studentRoot, targetRoot)
    }

    private fun copyFiles(paths: List<String>, studentRoot: File, testRoot: File) {
        val scanner = DirectoryScanner()
        scanner.setIncludes(paths.toTypedArray())
        scanner.basedir = studentRoot
        scanner.scan()

        scanner.includedFiles.forEach {
            val copyOrigin = File(studentRoot, it)
            val copyDestination = File(testRoot, it)

            copyOrigin.copyTo(copyDestination, true)
        }
    }

    private fun deleteFiles(paths: List<String>, testRoot: File) {
        val scanner = DirectoryScanner()
        scanner.setIncludes(paths.toTypedArray())
        scanner.basedir = testRoot
        scanner.scan()

        scanner.includedFiles.forEach {
            File(testRoot, it).delete()
        }
    }

}