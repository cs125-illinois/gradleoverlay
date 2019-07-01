package edu.illinois.cs.cs125.gradleoverlay

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.tools.ant.DirectoryScanner
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Paths

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
        val testRoot = project.projectDir
        println("Overlaying from $studentRoot to $testRoot")

        copyFiles(config.copy, studentRoot, testRoot)
        deleteFiles(config.delete, testRoot)
    }


    fun copyFiles(paths: List<String>, studentRoot: File, testRoot: File) {
        val scanner = DirectoryScanner()
        scanner.setIncludes(paths.toTypedArray())
        scanner.setBasedir(studentRoot.absolutePath)
        scanner.scan()

        val copyList = scanner.getIncludedFiles()+ scanner.getIncludedDirectories()

        copyList.forEach {
            val copyOrigin = File(studentRoot.absolutePath + '/' + it)
            val copyDestination = File(testRoot.absolutePath + '/' + it)

            if (copyOrigin.isDirectory) {
                copyOrigin.copyRecursively(copyDestination, true)
            } else {
                copyOrigin.copyTo(copyDestination, true)
            }
        }
    }


    fun deleteFiles(paths: List<String>, testRoot: File) {
        val scanner = DirectoryScanner()
        scanner.setIncludes(paths.toTypedArray())
        scanner.setBasedir(testRoot.absolutePath)
        scanner.scan()

        val deleteList = scanner.getIncludedFiles()+ scanner.getIncludedDirectories()

        deleteList.forEach {
            val toDelete = File(testRoot.absolutePath + '/' + it)
            if (toDelete.isDirectory) {
                toDelete.deleteRecursively()
            } else {
                toDelete.delete()
            }
        }
    }
}