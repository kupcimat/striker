package org.saigon.striker.gradle

import kotlinx.coroutines.runBlocking
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
open class UpgradeDependenciesTask : DefaultTask() {

    init {
        group = "Util"
        description = "Upgrades dependencies in build files"
    }

    var createPullRequest = false
    var githubUsername = ""
    var githubToken = ""
    var buildFiles = listOf<String>()

    @TaskAction
    fun run() = runBlocking {
        validateBuildFiles()

        for (buildFile in buildFiles) {
            println("Upgrading dependencies for $buildFile")
            upgradeVersions(File(buildFile))
        }
        if (dependencyChanges() && createPullRequest) {
            println("Creating pull request")
            validateGithubCredentials()
            commit()
            push()
            pullRequest()
        }
    }

    private fun dependencyChanges(): Boolean {
        return gitStatus(project.rootDir).isClean.not()
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
            credentials = GitCredentials(
                httpsUri = "https://github.com/kupcimat/striker.git",
                username = githubUsername,
                password = githubToken
            )
        )
    }

    private suspend fun pullRequest() {
        createPullRequest(
            githubToken,
            pullRequest = PullRequestCreate(
                title = "Upgrade dependencies",
                head = "kupcimat:upgrade-dependencies",
                base = "master"
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
        if (githubUsername.isEmpty() || githubToken.isEmpty()) {
            throw IllegalArgumentException("GitHub username and token cannot be empty")
        }
    }
}
