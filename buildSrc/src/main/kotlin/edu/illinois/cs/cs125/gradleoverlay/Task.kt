package edu.illinois.cs.cs125.gradleoverlay

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileNotFoundException

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
        paths.forEach {
            try {
                val studentPath = File(studentRoot.path + '/' + it)
                val testPath = File(testRoot.path + '/' + it)

                if (it.contains("/**")) {
                    val copyDir = it.removeSuffix("/**")

                    val studentDir = File(studentRoot.path + '/' + copyDir)
                    val testDir = File(testRoot.path + '/' + copyDir)

                    studentDir.copyRecursively(testDir, true)
                } else {
                    studentPath.copyTo(testPath, true)
                }

            } catch (e: NoSuchFileException) {
                println(e)
            }
        }
    }


    fun deleteFiles(paths: List<String>, testRoot: File) {
        paths.forEach {
            try {
                val testPath = File(testRoot.path + '/' + it)

                if (it.contains("/**")) {
                    val deleteDir = it.removeSuffix("/**")
                    val testDir = File(testRoot.path + '/' + deleteDir)

                    testDir.deleteRecursively()
                } else {
                    testPath.delete()
                }
            } catch (e: NoSuchFileException) {
                println(e)
            }
        }
    }
}