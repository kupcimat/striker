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

    var commitChanges = false
    var createPullRequest = false
    var githubHttpsUri = ""
    var githubUsername = ""
    var githubPassword = ""
    var buildFiles = listOf<String>()

    @TaskAction
    fun run() = runBlocking {
        validateBuildFiles()

        for (buildFile in buildFiles) {
            println("\nUpgrading dependencies for $buildFile")
            upgradeVersions(File(buildFile))
        }
        if (commitChanges) {
            println("\nCommitting changes to git")
            commit()
        }
        if (createPullRequest) {
            println("\nCreating GitHub pull request")
            validateGithubCredentials()
            push()
        }
    }

    private fun commit() {
        gitCheckout(project.rootDir, branch = "upgrade-dependencies")
        gitCommit(
            project.rootDir,
            message = "Upgrade dependencies",
            author = GitAuthor(
                name = "Dependencies Bot",
                email = "dependencies@striker.org"
            )
        )
    }

    private fun push() {
        gitPush(
            project.rootDir,
            branch = "upgrade-dependencies",
            credentials = GithubCredentials(
                httpsUri = githubHttpsUri,
                username = githubUsername,
                password = githubPassword
            )
        )
    }

    private fun validateBuildFiles() {
        val invalidFiles = buildFiles.filterNot { File(it).isFile }
        if (invalidFiles.isNotEmpty()) {
            throw IllegalArgumentException("Some build files are missing or invalid: $invalidFiles")
        }
    }

    private fun validateGithubCredentials() {
        if (githubHttpsUri.isEmpty() || githubUsername.isEmpty() || githubPassword.isEmpty()) {
            throw IllegalArgumentException("GitHub httpsUri, username and password cannot be empty")
        }
    }
}
