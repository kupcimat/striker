package org.saigon.striker.gradle

import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class UpgradeDependenciesTask : DefaultTask() {

    init {
        group = "Util"
        description = "Upgrades dependencies in build files"
    }

    var buildFiles = listOf<String>()

    @TaskAction
    fun run() = runBlocking {
        validateBuildFiles()

        for (buildFile in buildFiles) {
            println("\nUpgrading dependencies for $buildFile")
            upgradeVersions(File(buildFile))
        }
    }

    private fun validateBuildFiles() {
        val invalidFiles = buildFiles.filterNot { File(it).isFile }
        if (invalidFiles.isNotEmpty()) {
            throw IllegalArgumentException("Some build files are missing or invalid: $invalidFiles")
        }
    }
}
