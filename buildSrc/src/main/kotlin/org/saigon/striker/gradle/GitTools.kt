package org.saigon.striker.gradle

import org.eclipse.jgit.api.Git
import java.io.File
import java.time.Instant

data class GitCommit(
    val hash: String,
    val time: Instant?
)

fun getLatestGitCommit(rootDirectory: File): GitCommit {
    val git = Git.open(rootDirectory.resolve(".git"))
    val gitStatus = git.status().call()

    if (gitStatus.isClean) {
        val gitLog = git.log().setMaxCount(1).call()
        val commit = gitLog.first()

        return GitCommit(hash = commit.name, time = Instant.ofEpochSecond(commit.commitTime.toLong()))
    }
    return GitCommit(hash = "development", time = null)
}
