package org.saigon.striker.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.time.Instant

open class PrintBuildVersionTask : DefaultTask() {

    init {
        group = "Util"
        description = "Prints latest build version"
    }

    @TaskAction
    fun run() {
        val commit = getBuildVersion(project.rootDir)
        println("Build version: ${commit.hash} (${commit.time ?: Instant.now()})")
    }
}
