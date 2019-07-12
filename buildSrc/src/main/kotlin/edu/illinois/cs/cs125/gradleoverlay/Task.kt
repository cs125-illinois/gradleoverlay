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
            File(project.property("overlayfrom") as String)
        } else if (project.rootProject.childProjects.containsKey("student")) {
            project.rootProject.childProjects["student"]!!.projectDir // For testing
        } else {
            error("Specify overlay source with -Poverlayfrom")
        }
        val targetRoot = project.projectDir
        println("Overlaying from $studentRoot to $targetRoot")

        copyFiles(config.copy, studentRoot, targetRoot)
        deleteFiles(config.delete, targetRoot)
    }



    fun copyFiles(paths: List<String>, studentRoot: File, testRoot: File) {
        val scanner = DirectoryScanner()
        scanner.setIncludes(paths.toTypedArray())
        scanner.setBasedir(studentRoot.absolutePath)
        scanner.scan()

        val copyList = scanner.getIncludedFiles()

        copyList.forEach {
            val copyOrigin = File(studentRoot.absolutePath + '/' + it)
            val copyDestination = File(testRoot.absolutePath + '/' + it)

            copyOrigin.copyTo(copyDestination, true)
        }
    }

    

    fun deleteFiles(paths: List<String>, testRoot: File) {
        val scanner = DirectoryScanner()
        scanner.setIncludes(paths.toTypedArray())
        scanner.setBasedir(testRoot.absolutePath)
        scanner.scan()

        val deleteList = scanner.getIncludedFiles()

        deleteList.forEach {
            val toDelete = File(testRoot.absolutePath + '/' + it)
            toDelete.delete()
        }
    }
}